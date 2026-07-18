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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerSkillSkinListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skinskill.SkillSkin;
import com.aionemu.gameserver.model.skinskill.SkillSkinList;

/**
 * This class provides the database access layer for managing player skill skin lists using {@code MySQL5}.<br>
 * It extends {@link PlayerSkillSkinListDAO} to handle specific queries related to {@code SkillSkin} data.
 * @author Ghostfur (Aion-Unique)
 */
public class MySQL5PlayerSkillSkinListDAO extends PlayerSkillSkinListDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerTitleListDAO.class);
	private static final String LOAD_QUERY = "SELECT `skin_id`, `remaining`, `active` FROM `player_skill_skins` WHERE `player_id`=?";
	private static final String INSERT_QUERY = "INSERT INTO `player_skill_skins`(`player_id`,`skin_id`, `remaining`, `active`) VALUES (?,?,?,?)";
	private static final String DELETE_QUERY = "DELETE FROM `player_skill_skins` WHERE `player_id`=? AND `skin_id` =?;";
	
	/**
	 * Retrieves the list of skill skins for a specific player.<br>
	 * This method queries the database using the provided {@code playerId}.<br>
	 * It populates a new {@link SkillSkinList} object with all matching records.
	 * @param playerId The unique identifier of the player to load data for.
	 * @return A {@link SkillSkinList} containing the player's skill skins.
	 */
	@Override
	public SkillSkinList loadSkillSkinList(int playerId)
	{
		final SkillSkinList tl = new SkillSkinList();
		
		DB.select(LOAD_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					final int id = rset.getInt("skin_id");
					final int remaining = rset.getInt("remaining");
					final int active = rset.getInt("active");
					tl.addEntry(id, remaining, active);
				}
			}
		});
		
		return tl;
	}
	
	/**
	 * Saves a new {@link SkillSkin} to the database for a specific player.<br>
	 * This method uses an {@code INSERT} query to store the skin data.<br>
	 * It returns {@code true} if the operation succeeds.<br>
	 * It returns {@code false} if any error occurs during the database process.
	 * @param player The {@link Player} object who owns the skill skin.
	 * @param entry The {@link SkillSkin} data to be saved.
	 * @return {@code true} if successful, otherwise {@code false}.
	 */
	@Override
	public boolean storeSkillSkins(Player player, SkillSkin entry)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, entry.getId());
			stmt.setInt(3, entry.getExpireTime());
			stmt.setInt(4, entry.getIsActive());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not store skill skin for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Updates the active status of a specific skill skin for a player.<br>
	 * This method sets the {@code active} flag to {@code 1} in the database.
	 * @param playerObjId The unique identifier for the player.
	 * @param skinId The unique identifier for the skill skin.
	 * @return {@code true} if the update was successful, otherwise {@code false}.
	 */
	@Override
	public boolean setActive(int playerObjId, int skinId)
	{
		return DB.insertUpdate("UPDATE player_skill_skins SET active =1 WHERE `player_id`=? AND `skin_id` = ?", preparedStatement ->
		{
			preparedStatement.setInt(1, playerObjId);
			preparedStatement.setInt(2, skinId);
			preparedStatement.execute();
		});
	}
	
	/**
	 * Deactivates a specific skill skin for a player.<br>
	 * This method sets the {@code active} status to {@code false}.
	 * @param playerObjId The unique identifier of the player.
	 * @param skinId The unique identifier of the skill skin.
	 * @return {@code true} if the update was successful, otherwise {@code false}.
	 */
	@Override
	public boolean setDeactive(int playerObjId, int skinId)
	{
		return DB.insertUpdate("UPDATE player_skill_skins SET active=0 WHERE `player_id`=? AND `skin_id`=?", preparedStatement ->
		{
			preparedStatement.setInt(1, playerObjId);
			preparedStatement.setInt(2, skinId);
			preparedStatement.execute();
		});
	}
	
	/**
	 * Removes a specific skill skin from the database for a player.<br>
	 * This method deletes the entry matching both {@code playerId} and {@code skinId}.
	 * @param playerId The unique identifier of the player.
	 * @param skinId The unique identifier of the skill skin to remove.
	 * @return {@code true} if the deletion was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean removeSkillSkin(int playerId, int skinId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, skinId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not delete skill skin for player " + playerId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
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
