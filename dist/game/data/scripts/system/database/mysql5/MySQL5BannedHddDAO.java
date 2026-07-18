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
import com.aionemu.gameserver.dao.BannedHddDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.network.BannedHDDEntry;

/**
 * This class provides the {@code MySQL5} database implementation for handling banned HDD entries.<br>
 * It extends {@link BannedHddDAO} to perform specific queries against a {@code mysql5} database schema.
 * @author Alex
 */
public class MySQL5BannedHddDAO extends BannedHddDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5BannedHddDAO.class);
	
	/**
	 * Loads all banned HDD entries from the database.<br>
	 * This method retrieves data from the {@code banned_hdd} table.<br>
	 * It returns a map where the key is the HDD serial.
	 * @return A {@link Map} containing all loaded {@link BannedHDDEntry} objects.
	 */
	@Override
	public Map<String, BannedHDDEntry> load()
	{
		final Map<String, BannedHDDEntry> map = new HashMap<>();
		final PreparedStatement ps = DB.prepareStatement("SELECT `hdd_serial`,`time`,`details` FROM `banned_hdd`");
		try
		{
			final ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				final String address = rs.getString("hdd_serial");
				map.put(address, new BannedHDDEntry(address, rs.getTimestamp("time"), rs.getString("details")));
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
	 * Updates an existing record in the database.<br>
	 * It uses a {@code REPLACE INTO} statement to save the data.<br>
	 * This method handles {@link BannedHDDEntry} objects.
	 * @param entry The {@code BannedHDDEntry} object containing the new data to save.
	 * @return {@code true} if the database update was successful, otherwise {@code false}.
	 */
	@Override
	public boolean update(BannedHDDEntry entry)
	{
		boolean success = false;
		final PreparedStatement ps = DB.prepareStatement("REPLACE INTO `banned_hdd` (`hdd_serial`,`time`,`details`) VALUES (?,?,?)");
		try
		{
			ps.setString(1, entry.getHDDSerial());
			ps.setTimestamp(2, entry.getTime());
			ps.setString(3, entry.getDetails());
			success = ps.executeUpdate() > 0;
		}
		catch (SQLException e)
		{
			log.error("Error storing BannedHDDEntry " + entry.getHDDSerial(), e);
		}
		finally
		{
			DB.close(ps);
		}
		
		return success;
	}
	
	/**
	 * Removes a specific hard drive from the banned list.<br>
	 * This method deletes the entry matching the provided serial number.
	 * @param hdd_serial The unique serial number of the hard drive to remove.
	 * @return {@code true} if the deletion was successful, otherwise {@code false}.
	 */
	@Override
	public boolean remove(String hdd_serial)
	{
		boolean success = false;
		final PreparedStatement ps = DB.prepareStatement("DELETE FROM `banned_hdd` WHERE hdd_serial=?");
		try
		{
			ps.setString(1, hdd_serial);
			success = ps.executeUpdate() > 0;
		}
		catch (SQLException e)
		{
			log.error("Error removing BannedHDDEntry " + hdd_serial, e);
		}
		finally
		{
			DB.close(ps);
		}
		
		return success;
	}
	
	/**
	 * Removes all expired entries from the {@code banned_hdd} table.<br>
	 * This method deletes records where the timestamp is older than the current date.<br>
	 * It helps keep the database clean by removing outdated ban information.
	 */
	@Override
	public void cleanExpiredBans()
	{
		DB.insertUpdate("DELETE FROM `banned_hdd` WHERE time < current_date");
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
