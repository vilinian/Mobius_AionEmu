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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.NetworkBannedDAO;
import com.aionemu.gameserver.network.NetworkBanEntry;

/**
 * This class provides the database access layer for managing network bans in a {@code mysql5} environment.<br>
 * It extends {@link NetworkBannedDAO} to handle specific SQL queries for banning and unbanning IP addresses.
 * @author Alex
 */
public class MySQL5NetworkBannedDAO extends NetworkBannedDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5NetworkBannedDAO.class);
	
	/**
	 * Loads all network ban records from the database.<br>
	 * It retrieves every entry from the {@code network_ban} table.<br>
	 * Each record is converted into a {@link NetworkBanEntry} object.
	 * @return A {@code Map} where the keys are IP addresses and the values are {@link NetworkBanEntry} objects.
	 */
	@Override
	public Map<String, NetworkBanEntry> load()
	{
		final Map<String, NetworkBanEntry> map = new HashMap<>();
		final PreparedStatement ps = DB.prepareStatement("SELECT `ip`,`time`,`details` FROM `network_ban`");
		try
		{
			final ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				final String address = rs.getString("ip");
				map.put(address, new NetworkBanEntry(address, rs.getTimestamp("time"), rs.getString("details")));
			}
		}
		catch (SQLException e)
		{
			log.error("Error loading last saved server time", e);
		}
		finally
		{
			DB.close(ps);
		}
		
		return map;
	}
	
	/**
	 * Updates an existing ban or inserts a new one into the database.<br>
	 * This method uses the {@code REPLACE INTO} SQL command.<br>
	 * It saves the IP, time, and details from the provided entry.
	 * @param entry The {@link NetworkBanEntry} object containing the data to save.
	 * @return {@code true} if the database update was successful, otherwise {@code false}.
	 */
	@Override
	public boolean update(NetworkBanEntry entry)
	{
		boolean success = false;
		final PreparedStatement ps = DB.prepareStatement("REPLACE INTO `network_ban` (`ip`,`time`,`details`) VALUES (?,?,?)");
		try
		{
			ps.setString(1, entry.getNetworkIP());
			ps.setTimestamp(2, entry.getTime());
			ps.setString(3, entry.getDetails());
			success = ps.executeUpdate() > 0;
		}
		catch (SQLException e)
		{
			log.error("Error storing NetworkBanEntry " + entry.getNetworkIP(), e);
		}
		finally
		{
			DB.close(ps);
		}
		
		return success;
	}
	
	/**
	 * Removes a network ban from the database based on an IP address.<br>
	 * This method deletes the entry associated with the provided {@code ip}.
	 * @param ip The IP address to remove from the ban list.
	 * @return {@code true} if the deletion was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean remove(String ip)
	{
		boolean success = false;
		final PreparedStatement ps = DB.prepareStatement("DELETE FROM `network_ban` WHERE ip=?");
		try
		{
			ps.setString(1, ip);
			success = ps.executeUpdate() > 0;
		}
		catch (SQLException e)
		{
			log.error("Error removing BannedHDDEntry " + ip, e);
		}
		finally
		{
			DB.close(ps);
		}
		
		return success;
	}
	
	/**
	 * Removes all expired entries from the {@code network_ban} table.<br>
	 * This method deletes records where the timestamp is older than the current date.<br>
	 * It helps keep the database clean by removing outdated ban information.
	 */
	@Override
	public void cleanExpiredBans()
	{
		DB.insertUpdate("DELETE FROM `network_ban` WHERE time < current_date");
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
