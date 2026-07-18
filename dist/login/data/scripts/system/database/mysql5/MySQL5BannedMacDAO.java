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


package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.loginserver.dao.BannedMacDAO;
import com.aionemu.loginserver.model.base.BannedMacEntry;

/**
 * This class provides the Data Access Object (DAO) implementation for managing banned MAC addresses in a {@code mysql5} database.<br>
 * It extends {@link BannedMacDAO} to handle specific database operations for the login server.
 * @author KID
 */
public class MySQL5BannedMacDAO extends BannedMacDAO {

	private static Logger log = LoggerFactory.getLogger(MySQL5BannedMacDAO.class);

	/**
	 * Loads all banned MAC addresses from the database.<br>
	 * It retrieves records from the {@code banned_mac} table.<br>
	 * Each record is converted into a {@link BannedMacEntry} object.
	 * @return A {@code Map} where keys are MAC addresses and values are {@link BannedMacEntry} objects.
	 */
	@Override
	public Map<String, BannedMacEntry> load() {
		Map<String, BannedMacEntry> map = new HashMap<>();
		PreparedStatement ps = DB.prepareStatement("SELECT `address`,`time`,`details` FROM `banned_mac`");
		try {
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				String address = rs.getString("address");
				map.put(address, new BannedMacEntry(address, rs.getTimestamp("time"), rs.getString("details")));
			}
		}
		catch (SQLException e) {
			log.error("Error loading last saved server time", e);
		}
		finally {
			DB.close(ps);
		}
		
		return map;
	}

	/**
	 * Updates an existing record in the database.<br>
	 * It uses a {@code REPLACE INTO} statement to save the data.<br>
	 * This method handles the mapping of a {@link BannedMacEntry} object.
	 * @param entry The {@code BannedMacEntry} object containing the data to update.
	 * @return {@code true} if the database was updated successfully, otherwise {@code false}.
	 */
	@Override
	public boolean update(BannedMacEntry entry) {
		boolean success = false;
		PreparedStatement ps = DB.prepareStatement("REPLACE INTO `banned_mac` (`address`,`time`,`details`) VALUES (?,?,?)");
		try {
			ps.setString(1, entry.getMac());
			ps.setTimestamp(2, entry.getTime());
			ps.setString(3, entry.getDetails());
			success = ps.executeUpdate() > 0;
		}
		catch (SQLException e) {
			log.error("Error storing BannedMacEntry "+entry.getMac(), e);
		}
		finally {
			DB.close(ps);
		}

		return success;
	}

	/**
	 * Removes a banned MAC address from the database.<br>
	 * This method deletes the entry matching the provided {@code address}.
	 * @param address The unique identifier of the MAC address to remove.
	 * @return {@code true} if the deletion was successful, {@code false} otherwise.
	 */
	@Override
	public boolean remove(String address) {
		boolean success = false;
		PreparedStatement ps = DB.prepareStatement("DELETE FROM `banned_mac` WHERE address=?");
		try {
			ps.setString(1, address);
			success = ps.executeUpdate() > 0;
		}
		catch (SQLException e) {
			log.error("Error removing BannedMacEntry "+address, e);
		}
		finally {
			DB.close(ps);
		}

		return success;
	}

	/**
	 * Removes all expired entries from the {@code banned_mac} table.<br>
	 * This method deletes records where the timestamp is older than the current date.<br>
	 * It helps keep the database clean by removing outdated ban information.
	 */
	@Override
	public void cleanExpiredBans() {
		DB.insertUpdate("DELETE FROM `banned_mac` WHERE time < current_date");
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
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
