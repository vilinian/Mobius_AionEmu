/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package system.database.mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerSkillListDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.skill.PlayerSkillList;

/**
 * This class provides the database access layer for managing player skill lists using {@code MySQL5}.<br>
 * It handles the persistence of {@link PlayerSkillList} data to and from the database.<br>
 * It extends {@link PlayerSkillListDAO} to implement specific queries for the {@code mysql5} driver.
 * @author SoulKeeper
 * @author IceReaper, orfeo087, Avol, AEJTester
 */
public class MySQL5PlayerSkillListDAO extends PlayerSkillListDAO
{
	public static final String INSERT_QUERY = "INSERT INTO `player_skills` (`player_id`, `skill_id`, `skill_level`) VALUES (?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE `player_skills` set skill_level=? where player_id=? AND skill_id=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_skills` WHERE `player_id`=? AND skill_id=?";
	public static final String SELECT_QUERY = "SELECT `skill_id`, `skill_level` FROM `player_skills` WHERE `player_id`=?";
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerSkillListDAO.class);
	private static final Predicate<PlayerSkillEntry> skillsToInsertPredicate = (PlayerSkillEntry input) -> (input != null) && (PersistentState.NEW == input.getPersistentState());
	private static final Predicate<PlayerSkillEntry> skillsToUpdatePredicate = (PlayerSkillEntry input) -> (input != null) && (PersistentState.UPDATE_REQUIRED == input.getPersistentState());
	private static final Predicate<PlayerSkillEntry> skillsToDeletePredicate = (PlayerSkillEntry input) -> (input != null) && (PersistentState.DELETED == input.getPersistentState());
	
	/**
	 * Retrieves the list of skills for a specific player from the database.<br>
	 * This method uses {@code SELECT_QUERY} to fetch all skill entries.<br>
	 * It returns a new {@link PlayerSkillList} containing the loaded data.
	 * @param playerId The unique identifier of the player to load skills for.
	 * @return A {@link PlayerSkillList} object containing the player's skills.
	 */
	@Override
	public PlayerSkillList loadSkillList(int playerId)
	{
		final List<PlayerSkillEntry> skills = new ArrayList<>();
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final int id = rset.getInt("skill_id");
				final int lv = rset.getInt("skill_level");
				
				skills.add(new PlayerSkillEntry(id, false, false, lv, PersistentState.UPDATED));
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore SkillList data for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return new PlayerSkillList(skills);
	}
	
	/**
	 * Saves the current skill data for a specific player to the database.<br>
	 * This method processes both active and deleted skills from the {@link Player} object.<br>
	 * It calls the internal {@code store} method to perform the actual database operations.
	 * @param player The {@code Player} object containing the skills to be saved.
	 * @return Always returns {@code true} after the save operation is attempted.
	 */
	@Override
	public boolean storeSkills(Player player)
	{
		final List<PlayerSkillEntry> skillsActive = new ArrayList<>(Arrays.asList(player.getSkillList().getAllSkills()));
		final List<PlayerSkillEntry> skillsDeleted = new ArrayList<>(Arrays.asList(player.getSkillList().getDeletedSkills()));
		store(player, skillsActive);
		store(player, skillsDeleted);
		return true;
	}
	
	/**
	 * Saves the skill list for a specific {@link Player} to the database.<br>
	 * This method handles deleting old records and adding new ones.<br>
	 * It also updates existing skills and sets their persistent state.
	 * @param player The {@code Player} object whose skills are being saved.
	 * @param skills The list of {@code PlayerSkillEntry} objects to persist.
	 */
	private void store(Player player, List<PlayerSkillEntry> skills)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			
			deleteSkills(con, player, skills);
			addSkills(con, player, skills);
			updateSkills(con, player, skills);
			
		}
		catch (SQLException e)
		{
			log.error("Failed to open connection to database while saving SkillList for player " + player.getObjectId());
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		for (PlayerSkillEntry skill : skills)
		{
			skill.setPersistentState(PersistentState.UPDATED);
		}
	}
	
	/**
	 * Adds a list of skills to the database for a specific player.<br>
	 * This method filters the provided {@code List<PlayerSkillEntry>} and uses batch processing to insert them into the table.<br>
	 * It handles the SQL connection and commits the transaction automatically.
	 * @param con The active {@link Connection} used to execute the database queries.
	 * @param player The {@link Player} object whose skills are being saved.
	 * @param skills The list of {@link PlayerSkillEntry} objects to be added.
	 */
	private void addSkills(Connection con, Player player, List<PlayerSkillEntry> skills)
	{
		final Collection<PlayerSkillEntry> skillsToInsert = skills.stream().filter(skillsToInsertPredicate).collect(Collectors.toList());
		if (GenericValidator.isBlankOrNull(skillsToInsert))
		{
			return;
		}
		
		PreparedStatement ps = null;
		try
		{
			ps = con.prepareStatement(INSERT_QUERY);
			
			for (PlayerSkillEntry skill : skillsToInsert)
			{
				// log.info("MYSQL INSERT SKILL: " + skill.getSkillId());
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, skill.getSkillId());
				ps.setInt(3, skill.getSkillLevel());
				ps.addBatch();
			}
			
			ps.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			// log.error("Can't add skills for player: " + player.getObjectId() + " Execption is : " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(ps);
		}
	}
	
	/**
	 * Updates the skill levels in the database for a specific player.<br>
	 * This method filters the provided list and executes a batch update.<br>
	 * It handles the {@code SQLException} by logging an error message.
	 * @param con The active {@link Connection} to the database.
	 * @param player The {@link Player} whose skills are being updated.
	 * @param skills A {@link List} of {@link PlayerSkillEntry} objects containing the new skill data.
	 */
	private void updateSkills(Connection con, Player player, List<PlayerSkillEntry> skills)
	{
		final Collection<PlayerSkillEntry> skillsToUpdate = skills.stream().filter(skillsToUpdatePredicate).collect(Collectors.toList());
		if (GenericValidator.isBlankOrNull(skillsToUpdate))
		{
			return;
		}
		
		PreparedStatement ps = null;
		try
		{
			ps = con.prepareStatement(UPDATE_QUERY);
			
			for (PlayerSkillEntry skill : skillsToUpdate)
			{
				ps.setInt(1, skill.getSkillLevel());
				ps.setInt(2, player.getObjectId());
				ps.setInt(3, skill.getSkillId());
				ps.addBatch();
			}
			
			ps.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			log.error("Can't update skills for player: " + player.getObjectId());
		}
		finally
		{
			DatabaseFactory.close(ps);
		}
	}
	
	/**
	 * Removes specific skills from the database for a given {@link Player}.<br>
	 * This method filters the provided list and executes a batch delete query.
	 * @param con The active {@code Connection} to the database.
	 * @param player The {@code Player} object whose skills are being modified.
	 * @param skills A {@code List} of {@code PlayerSkillEntry} objects to be removed.
	 */
	private void deleteSkills(Connection con, Player player, List<PlayerSkillEntry> skills)
	{
		final Collection<PlayerSkillEntry> skillsToDelete = skills.stream().filter(skillsToDeletePredicate).collect(Collectors.toList());
		if (GenericValidator.isBlankOrNull(skillsToDelete))
		{
			return;
		}
		
		PreparedStatement ps = null;
		try
		{
			ps = con.prepareStatement(DELETE_QUERY);
			
			for (PlayerSkillEntry skill : skillsToDelete)
			{
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, skill.getSkillId());
				ps.addBatch();
			}
			
			ps.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			log.error("Can't delete skills for player: " + player.getObjectId());
		}
		finally
		{
			DatabaseFactory.close(ps);
		}
	}
	
	/**
	 * Checks if the current database is compatible with this DAO.<br>
	 * It uses {@code int, int)} to verify the version.
	 * @param databaseName The name of the database to check.
	 * @param majorVersion The major version number of the database.
	 * @param minorVersion The minor version number of the database.
	 * @return {@code true} if the database is supported, {@code false} otherwise.
	 */
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
