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
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerEventsWindowDAO;
import com.aionemu.gameserver.model.event_window.PlayerEventWindowEntry;
import com.aionemu.gameserver.model.event_window.PlayerEventWindowList;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides the database access layer for handling player event windows using {@code MySQL5}.<br>
 * It extends {@link PlayerEventsWindowDAO} to implement specific queries and data persistence logic.
 * @author Ranastic
 */
public class MySQL5PlayerEventsWindowDAO extends PlayerEventsWindowDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerEventsWindowDAO.class);
	
	/**
	 * Loads the event window data for a specific player from the database.<br>
	 * This method retrieves all entries associated with the {@code Player} account ID.<br>
	 * It returns a new {@link PlayerEventWindowList} containing the loaded entries.
	 * @param player The {@code Player} object whose data needs to be loaded.
	 * @return A {@code PlayerEventWindowList} containing the retrieved event window entries.
	 */
	@Override
	public PlayerEventWindowList load(Player player)
	{
		final List<PlayerEventWindowEntry> eventWindow = new ArrayList<>();
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("SELECT * FROM `player_events_window` WHERE `account_id`=?");
			stmt.setInt(1, player.getPlayerAccount().getId());
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final int id = rset.getInt("event_id");
				final Timestamp lastStamp = rset.getTimestamp("last_stamp");
				final int elapsed = rset.getInt("elapsed");
				eventWindow.add(new PlayerEventWindowEntry(id, lastStamp, elapsed, PersistentState.UPDATED));
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore Event Window account: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return new PlayerEventWindowList(eventWindow);
	}
	
	/**
	 * Saves the event window data for a specific account to the database.<br>
	 * It inserts a new record or updates an existing one if it already exists.
	 * @param accountId The unique identifier for the player account.
	 * @param eventId The unique identifier for the specific event.
	 * @param last_stamp The timestamp of the last recorded activity.
	 * @param elapsed The amount of time that has passed.
	 * @return {@code true} if the operation succeeded, or {@code false} if an error occurred.
	 */
	@Override
	public boolean store(int accountId, int eventId, Timestamp last_stamp, int elapsed)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("INSERT INTO `player_events_window` (`account_id`, `event_id`, `last_stamp`, `elapsed`) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE `event_id` = VALUES(`event_id`), `last_stamp` = VALUES(`last_stamp`)");
			stmt.setInt(1, accountId);
			stmt.setInt(2, eventId);
			stmt.setTimestamp(3, last_stamp);
			stmt.setInt(4, elapsed);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not store event window for account " + accountId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Adds a new entry to the player events window table.<br>
	 * This method saves the initial record for an account and event.
	 * @param accountId The unique identifier for the user account.
	 * @param eventId The specific ID of the event being tracked.
	 * @param last_stamp The starting timestamp for the event.
	 */
	@Override
	public void insert(int accountId, int eventId, Timestamp last_stamp)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("INSERT INTO `player_events_window` (`account_id`, `event_id`, `last_stamp`) VALUES (?,?,?)");
			stmt.setInt(1, accountId);
			stmt.setInt(2, eventId);
			stmt.setTimestamp(3, last_stamp);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Can't insert into events window: " + e.getMessage());
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Removes a specific record from the database.<br>
	 * This method deletes an entry based on both the {@code accountId} and {@code eventId}.<br>
	 * It uses the {@code java.util.function.Consumer)} helper to execute the SQL command.
	 * @param accountId The unique identifier for the player account.
	 * @param eventId The unique identifier for the specific event window.
	 */
	@Override
	public void delete(int accountId, int eventId)
	{
		DB.insertUpdate("DELETE FROM player_events_window WHERE account_id = ? AND event_id = ?", preparedStatement ->
		{
			preparedStatement.setInt(1, accountId);
			preparedStatement.setInt(2, eventId);
		});
	}
	
	/**
	 * Retrieves all event IDs for a specific account.<br>
	 * This method queries the database for records matching the provided {@code accountId}.
	 * @param accountId The unique identifier of the account to query.
	 * @return A {@code List<Integer>} containing all retrieved event IDs.
	 */
	@Override
	public List<Integer> getEventsWindow(int accountId)
	{
		final List<Integer> ids = new ArrayList<>();
		DB.select("SELECT event_id FROM player_events_window WHERE account_id = ?", new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setInt(1, accountId);
			}
			
			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while (resultSet.next())
				{
					ids.add(resultSet.getInt("event_id"));
				}
			}
		});
		
		return ids;
	}
	
	/**
	 * Retrieves the most recent timestamp for a specific event.<br>
	 * This method looks up the {@code last_stamp} in the database.<br>
	 * If no record is found or an error occurs, it returns the current system time.
	 * @param accountId The unique identifier of the player account.
	 * @param eventId The unique identifier of the specific event.
	 * @return A {@code Timestamp} representing the last recorded time.
	 */
	@Override
	public Timestamp getLastStamp(int accountId, int eventId)
	{
		final PreparedStatement s = DB.prepareStatement("SELECT last_stamp FROM player_events_window WHERE account_id = ? AND event_id = ?");
		try
		{
			s.setInt(1, accountId);
			s.setInt(2, eventId);
			final ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getTimestamp("last_stamp");
		}
		catch (SQLException e)
		{
			log.error("Can't get last received Stamp!" + e);
			return new Timestamp(System.currentTimeMillis());
		}
		finally
		{
			DB.close(s);
		}
	}
	
	/**
	 * This method retrieves the elapsed time for a specific event.<br>
	 * It looks up the value based on the provided account and event identifiers.<br>
	 * If an error occurs, it returns {@code 0}.
	 * @param accountId The unique identifier for the player account.
	 * @param eventId The unique identifier for the game event.
	 * @return The elapsed time as an {@code int}.
	 */
	@Override
	public int getElapsed(int accountId, int eventId)
	{
		final PreparedStatement s = DB.prepareStatement("SELECT elapsed FROM player_events_window WHERE account_id = ? AND event_id = ?");
		try
		{
			s.setInt(1, accountId);
			s.setInt(2, eventId);
			final ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("elapsed");
		}
		catch (SQLException e)
		{
			return 0;
		}
		finally
		{
			DB.close(s);
		}
	}
	
	/**
	 * Updates the {@code elapsed} time for a specific event.<br>
	 * This method modifies the record in the database for a given account and event.
	 * @param accountId The unique identifier of the player account.
	 * @param eventId The unique identifier of the event window.
	 * @param elapsed The new value to set for the elapsed time.
	 */
	@Override
	public void updateElapsed(int accountId, int eventId, int elapsed)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE player_events_window SET elapsed = ? WHERE account_id = ? AND event_id = ?");
			stmt.setDouble(1, elapsed);
			stmt.setInt(2, accountId);
			stmt.setInt(3, eventId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error updating elapsed ", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Retrieves the total number of rewards received for a specific event.<br>
	 * This method queries the database using the provided {@code accountId} and {@code eventId}.<br>
	 * It returns 0 if an error occurs during the database query.
	 * @param accountId The unique identifier for the player account.
	 * @param eventId The unique identifier for the specific game event.
	 * @return The total count of rewards received as an {@code int}.
	 */
	@Override
	public int getRewardRecivedCount(int accountId, int eventId)
	{
		final PreparedStatement s = DB.prepareStatement("SELECT reward_recived_count FROM player_events_window WHERE account_id = ? AND event_id = ?");
		try
		{
			s.setInt(1, accountId);
			s.setInt(2, eventId);
			final ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("reward_recived_count");
		}
		catch (SQLException e)
		{
			return 0;
		}
		finally
		{
			DB.close(s);
		}
	}
	
	/**
	 * Updates the number of rewards received for a specific event.<br>
	 * This method also resets the elapsed time and updates the timestamp.
	 * @param accountId The unique identifier for the player account.
	 * @param eventId The unique identifier for the game event.
	 * @param rewardRecivedCount The new count of rewards to save in the database.
	 */
	@Override
	public void setRewardRecivedCount(int accountId, int eventId, int rewardRecivedCount)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE player_events_window SET reward_recived_count = ?, elapsed = 0, last_stamp = now() WHERE account_id = ? AND event_id = ?");
			stmt.setInt(1, rewardRecivedCount);
			stmt.setInt(2, accountId);
			stmt.setInt(3, eventId);
			stmt.execute();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("Can't get reward recived count\n" + e);
		}
		finally
		{
			DatabaseFactory.close(con);
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
