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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerMinionsDAO;
import com.aionemu.gameserver.model.gameobjects.player.MinionCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides the {@code MySQL5} database implementation for managing player minions.<br>
 * It handles all data access operations related to {@link Player} minions within a {@code mysql5} environment.
 */
public class MySQL5PlayerMinionsDAO extends PlayerMinionsDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerMinionsDAO.class);
	
	/**
	 * Saves a new minion to the database.<br>
	 * This method uses {@code MinionCommonData} to populate the player_minions table.<br>
	 * It handles the SQL execution and closes the connection automatically.
	 * @param minionCommonData The data object containing all details for the new minion.
	 */
	@Override
	public void insertPlayerMinion(MinionCommonData minionCommonData)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("INSERT INTO player_minions(player_id, object_id, minion_id, name, grade, level, birthday, growth_points, is_lock, despawn_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			stmt.setInt(1, minionCommonData.getMasterObjectId());
			stmt.setInt(2, minionCommonData.getObjectId());
			stmt.setInt(3, minionCommonData.getMinionId());
			stmt.setString(4, minionCommonData.getName());
			stmt.setString(5, minionCommonData.getMinionGrade());
			stmt.setInt(6, minionCommonData.getMinionLevel());
			stmt.setTimestamp(7, minionCommonData.getBirthdayTimestamp());
			stmt.setInt(8, minionCommonData.getGrowthPoints());
			stmt.setBoolean(9, minionCommonData.isLocked());
			stmt.setTimestamp(10, minionCommonData.getDespawnTime());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error inserting new minion #" + minionCommonData.getMinionId() + "[" + minionCommonData.getName() + "]", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Removes a specific minion from a player's database record.<br>
	 * This method deletes the entry matching both the {@code Player} ID and the {@code minionObjectId}.<br>
	 * It handles the database connection and executes the delete query.
	 * @param player The {@link Player} object who owns the minion.
	 * @param minionObjectId The unique identifier of the minion to be removed.
	 */
	@Override
	public void removePlayerMinion(Player player, int minionObjectId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("DELETE FROM player_minions WHERE player_id = ? AND object_id = ?");
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, minionObjectId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error removing minion #" + minionObjectId, e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Retrieves all minions belonging to a specific {@link Player}.<br>
	 * This method fetches data from the database using the player's object ID.
	 * @param player The {@code Player} whose minions need to be retrieved.
	 * @return A {@code List} of {@code MinionCommonData} objects for the player.
	 */
	@Override
	public List<MinionCommonData> getPlayerMinions(Player player)
	{
		final List<MinionCommonData> minions = new ArrayList<>();
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("SELECT * FROM player_minions WHERE player_id = ?");
			stmt.setInt(1, player.getObjectId());
			final ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				final MinionCommonData minionCommonData = new MinionCommonData(rs.getInt("minion_id"), player.getObjectId(), rs.getString("name"), rs.getString("grade"), rs.getInt("level"), rs.getInt("growth_points"), rs.getBoolean("is_lock"));
				minionCommonData.setObjectId(rs.getInt("object_id"));
				minionCommonData.setBirthday(rs.getTimestamp("birthday"));
				Timestamp ts = null;
				try
				{
					ts = rs.getTimestamp("despawn_time");
				}
				catch (Exception e)
				{
				}
				
				if (ts == null)
				{
					ts = new Timestamp(System.currentTimeMillis());
				}
				
				minionCommonData.setDespawnTime(ts);
				minions.add(minionCommonData);
			}
			
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error getting minions for " + player.getObjectId(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return minions;
	}
	
	/**
	 * Updates the name of a specific minion in the database.<br>
	 * This method uses the data provided in the {@code MinionCommonData} object.<br>
	 * It identifies the correct record using the master object ID and the unique object ID.
	 * @param minionCommonData The data object containing the new name and identification details.
	 */
	@Override
	public void updateName(MinionCommonData minionCommonData)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE player_minions SET name = ? WHERE player_id = ? AND object_id = ?");
			stmt.setString(1, minionCommonData.getName());
			stmt.setInt(2, minionCommonData.getMasterObjectId());
			stmt.setInt(3, minionCommonData.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error update minion #" + minionCommonData.getMinionId(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the growth points for a specific minion in the database.<br>
	 * This method uses the data provided in {@code minionCommonData}.<br>
	 * It updates the record where the player ID and object ID match.
	 * @param minionCommonData The data object containing the new growth points and IDs.
	 */
	@Override
	public void updateMinionGrowth(MinionCommonData minionCommonData)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE player_minions SET growth_points = ? WHERE player_id = ? AND object_id = ?");
			stmt.setInt(1, minionCommonData.getGrowthPoints());
			stmt.setInt(2, minionCommonData.getMasterObjectId());
			stmt.setInt(3, minionCommonData.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error update minion #" + minionCommonData.getMinionId(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the lock status of a specific minion in the database.<br>
	 * This method sets the {@code is_lock} value based on the provided data.<br>
	 * It uses the master object ID and the unique object ID to identify the record.
	 * @param minionCommonData The {@link MinionCommonData} object containing the updated lock state.
	 */
	@Override
	public void updateMinionLock(MinionCommonData minionCommonData)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE player_minions SET is_lock = ? WHERE player_id = ? AND object_id = ?");
			stmt.setBoolean(1, minionCommonData.isLocked());
			stmt.setInt(2, minionCommonData.getMasterObjectId());
			stmt.setInt(3, minionCommonData.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error update minion #" + minionCommonData.getMinionId(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Checks if a specific minion name is already taken by a player.<br>
	 * This method queries the database for any existing records matching the provided criteria.
	 * @param playerId The unique identifier of the player to check.
	 * @param name The name of the minion to verify.
	 * @return {@code true} if the name is already in use or if a database error occurs, {@code false} otherwise.
	 */
	@Override
	public boolean isNameUsed(int playerId, String name)
	{
		final PreparedStatement s = DB.prepareStatement("SELECT count(player_id) as cnt FROM player_minions WHERE  player_id = ? AND ? = player_minions.name");
		try
		{
			s.setString(1, name);
			s.setInt(2, playerId);
			final ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("cnt") > 0;
		}
		catch (SQLException e)
		{
			log.error("Can't check if name " + name + ", is used, returning possitive result", e);
			return true;
		}
		finally
		{
			DB.close(s);
		}
	}
	
	/**
	 * Updates the specific time for a player's minion.<br>
	 * This method saves the new {@code time} value to the database.
	 * @param player The {@link Player} object who owns the minion.
	 * @param minionId The unique identifier of the minion.
	 * @param time The new time value to be set.
	 */
	@Override
	public void setTime(Player player, int minionId, long time)
	{
		// TODO Auto-generated method stub
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
