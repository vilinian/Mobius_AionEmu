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
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerAchievementActionDAO;
import com.aionemu.gameserver.dao.PlayerAchievementDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementState;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.model.gameobjects.player.achievement.PlayerAchievement;

/**
 * This class provides the {@code MySQL5} database implementation for handling player achievements.<br>
 * It manages data persistence and retrieval for {@link PlayerAchievement} objects using SQL queries.
 */
public class MySQL5PlayerAchievementDAO extends PlayerAchievementDAO
{
	private final Logger log = LoggerFactory.getLogger(MySQL5PlayerAchievementDAO.class);
	
	public static final String INSERT_ACHIEVEMENT = "INSERT INTO player_achievements (object_id, id, player_id, state, step, type, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String LOAD_QUERY = "SELECT * FROM `player_achievements` WHERE `player_id`=?";
	public static final String UPDATE_QUERY = "UPDATE player_achievements set state=?, step=? WHERE `player_id`=? AND `object_id`=?";
	
	/**
	 * Loads all achievements for a specific player from the database.<br>
	 * This method populates the {@code Player} object with achievement data.<br>
	 * It also triggers action loading for daily and weekly types.
	 * @param player The {@link Player} whose achievements need to be loaded.
	 */
	@Override
	public void loadAchievements(Player player)
	{
		DB.select(LOAD_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					final PlayerAchievement daily = new PlayerAchievement(rset.getInt("object_id"));
					daily.setStep(rset.getInt("step"));
					daily.setId(rset.getInt("id"));
					daily.setState(AchievementState.getPlayerClassById(rset.getInt("state")));
					daily.setType(AchievementType.getPlayerClassById(rset.getInt("type")));
					daily.setStartDate(rset.getTimestamp("start_date"));
					daily.setEndateDate(rset.getTimestamp("end_date"));
					if ((daily.getType() == AchievementType.DAILY) || (daily.getType() == AchievementType.WEEKLY))
					{
						player.getPlayerAchievements().put(rset.getInt("object_id"), daily);
						DAOManager.getDAO(PlayerAchievementActionDAO.class).loadActions(player, daily);
					}
					else
					{
						player.getPlayerEventAchievements().put(rset.getInt("object_id"), daily);
					}
					
				}
			}
		});
	}
	
	/**
	 * Saves a new achievement to the database for a specific player.<br>
	 * This method uses the {@code INSERT_ACHIEVEMENT} query to persist the data.
	 * @param player The {@link Player} who is earning the achievement.
	 * @param achievement The {@link PlayerAchievement} object containing the details to save.
	 * @return {@code true} if the database operation succeeded, or {@code false} if an error occurred.
	 */
	@Override
	public boolean storeAchievement(Player player, PlayerAchievement achievement)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_ACHIEVEMENT);
			stmt.setInt(1, achievement.getObjectId());
			stmt.setInt(2, achievement.getId());
			stmt.setInt(3, player.getObjectId());
			stmt.setInt(4, achievement.getState().getValue());
			stmt.setInt(5, achievement.getStep());
			stmt.setInt(6, achievement.getType().getValue());
			stmt.setTimestamp(7, achievement.getStartDate());
			stmt.setTimestamp(8, achievement.getEndateDate());
			stmt.execute();
			stmt.close();
			return true;
		}
		catch (SQLException e)
		{
			log.error("addRank", e);
			
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the progress of a specific achievement for a player in the database.<br>
	 * This method modifies the {@code state} and {@code step} values.
	 * @param player The {@link Player} whose data is being updated.
	 * @param achievement The {@link PlayerAchievement} object containing the new values.
	 * @return {@code true} if the update was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean update(Player player, PlayerAchievement achievement)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, achievement.getState().getValue());
			stmt.setInt(2, achievement.getStep());
			stmt.setInt(3, player.getObjectId());
			stmt.setInt(4, achievement.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not update PlayerAchievement data for Player " + player.getName() + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Removes all records from the database for a specific achievement type.<br>
	 * This method uses the {@code AchievementType} value to filter which rows to delete.<br>
	 * It interacts with the {@link DB} class to execute the SQL command.
	 * @param type The {@code AchievementType} that should be removed from the database.
	 */
	@Override
	public void deleteAchievements(AchievementType type)
	{
		final PreparedStatement statement = DB.prepareStatement("DELETE FROM player_achievements WHERE type = ?");
		try
		{
			statement.setInt(1, type.getValue());
		}
		catch (SQLException e)
		{
			log.error("Some crap, can't set int parameter to PreparedStatement", e);
		}
		
		DB.executeUpdateAndClose(statement);
	}
	
	/**
	 * Retrieves all unique object identifiers from the {@code player_achievements} table.<br>
	 * This method queries the database to collect every {@code object_id}.<br>
	 * If an error occurs, it returns an empty array.
	 * @return An array of integers containing the achievement object IDs.
	 */
	@Override
	public int[] getUsedIDs()
	{
		final PreparedStatement statement = DB.prepareStatement("SELECT object_id FROM player_achievements", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		try
		{
			final ResultSet rs = statement.executeQuery();
			rs.last();
			final int count = rs.getRow();
			rs.beforeFirst();
			final int[] ids = new int[count];
			for (int i = 0; i < count; i++)
			{
				rs.next();
				ids[i] = rs.getInt("object_id");
			}
			
			return ids;
		}
		catch (SQLException e)
		{
			log.error("Can't get list of id's from achievements table", e);
		}
		finally
		{
			DB.close(statement);
		}
		
		return new int[0];
	}
	
	/**
	 * Checks if the current database system supports a specific feature.<br>
	 * This method delegates the check to {@code int, int)}.
	 * @param s The name of the feature to check.
	 * @param i The first integer parameter for the feature.
	 * @param i1 The second integer parameter for the feature.
	 * @return {@code true} if the feature is supported, {@code false} otherwise.
	 */
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
