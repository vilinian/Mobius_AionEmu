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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.Free2PlayDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.f2p.F2p;
import com.aionemu.gameserver.model.gameobjects.player.f2p.F2pAccount;

/**
 * This class provides the {@code MySQL5} database implementation for {@link Free2PlayDAO}.<br>
 * It handles data access operations specifically for free-to-play accounts and players.<br>
 * It extends the base functionality of {@link Free2PlayDAO} to support {@code mysql5} queries.
 * @author teenwolf
 */
public class MySQL5Free2PlayDAO extends Free2PlayDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5Free2PlayDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `f2paccount` (`account_id`, `time`) VALUES (?,?) ON DUPLICATE KEY UPDATE `account_id` = VALUES (`account_id`), `time` = VALUES(`time`)";
	public static final String SELECT_QUERY = "SELECT `time` FROM `f2paccount` WHERE `account_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `f2paccount` WHERE `account_id`=?";
	public static final String UPDATE_QUERY = "UPDATE `f2paccount` set `time`=? where `account_id`=?";
	
	/**
	 * Loads the free-to-play time for a specific account from the database.<br>
	 * This method updates the {@link Player} object with the retrieved data.
	 * @param player The {@code Player} object to receive the updated information.
	 * @param accountId The unique identifier of the account to query.
	 */
	@Override
	public void loadF2pInfo(Player player, int accountId)
	{
		Connection con = null;
		final F2p f2p = new F2p(player);
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, accountId);
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final int time = rset.getInt("time");
				f2p.add(new F2pAccount(time), false);
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore f2p time for accountId: " + accountId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		player.setF2p(f2p);
	}
	
	/**
	 * Saves the free-to-play information for a specific account.<br>
	 * This method updates the database record if it already exists.
	 * @param accountId The unique identifier for the player account.
	 * @param time The timestamp associated with the free-to-play status.
	 * @return {@code true} if the operation succeeded, or {@code false} if an error occurred.
	 */
	@Override
	public boolean storeF2p(int accountId, int time)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, accountId);
			stmt.setInt(2, time);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not store f2p for accountId " + accountId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Updates the free-to-play time for a specific account.<br>
	 * This method modifies the {@code time} value in the database for the given {@code accountId}.
	 * @param accountId The unique identifier of the account to update.
	 * @param time The new timestamp or value to set for the account.
	 * @return {@code true} if the update was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean updateF2p(int accountId, int time)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, time);
			stmt.setInt(2, accountId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not update f2p for accountId " + accountId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Removes the free-to-play record from the database.<br>
	 * This method uses the {@code accountId} to identify which record to delete.<br>
	 * It returns {@code true} if the operation succeeds.<br>
	 * It returns {@code false} if an error occurs during the database operation.
	 * @param accountId The unique identifier of the account to remove.
	 * @return {@code true} if successful, or {@code false} otherwise.
	 */
	@Override
	public boolean deleteF2p(int accountId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, accountId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not delete f2p for accountId " + accountId + " from DB: " + e.getMessage(), e);
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
