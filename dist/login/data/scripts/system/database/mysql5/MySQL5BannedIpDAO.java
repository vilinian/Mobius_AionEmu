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
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.ReadStH;
import com.aionemu.loginserver.dao.BannedIpDAO;
import com.aionemu.loginserver.model.BannedIP;

/**
 * This class provides the {@code MySQL5} database implementation for the {@link BannedIpDAO} interface.<br>
 * It handles all data access operations related to banned IP addresses in a {@code MySQL5} environment.
 * @author SoulKeeper
 */
public class MySQL5BannedIpDAO extends BannedIpDAO {

	/**
	 * Adds a new IP address to the ban list.<br>
	 * This method uses the provided {@code mask} to create the entry.<br>
	 * It sets the expiration time to {@code null}.
	 * @param mask The IP address or subnet mask to block.
	 * @return The newly created {@link BannedIP} object.
	 */
	@Override
	public BannedIP insert(String mask) {
		return insert(mask, null);
	}

	/**
	 * Adds a new banned IP entry to the database.<br>
	 * This method creates a {@link BannedIP} object using the provided details.<br>
	 * It returns the created object if successful.<br>
	 * If the insertion fails, it returns {@code null}.
	 * @param mask The IP address or subnet mask to block.
	 * @param expireTime The timestamp when the ban should end.
	 * @return The saved {@link BannedIP} object or {@code null} if the operation failed.
	 */
	@Override
	public BannedIP insert(String mask, Timestamp expireTime) {
		BannedIP result = new BannedIP();
		result.setMask(mask);
		result.setTimeEnd(expireTime);

		if (insert(result)){
			return result;
		}
		
		return null;
	}

	/**
	 * Adds a new {@link BannedIP} record to the database.<br>
	 * This method saves the mask and expiration time.<br>
	 * It returns {@code true} if the operation succeeds.<br>
	 * It returns {@code false} if the insertion fails.
	 * @param bannedIP The {@code BannedIP} object containing the data to save.
	 * @return A boolean indicating whether the record was successfully inserted.
	 */
	@Override
	public boolean insert(BannedIP bannedIP) {
		boolean insert = DB.insertUpdate("INSERT INTO banned_ip(mask, time_end) VALUES (?, ?)", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, bannedIP.getMask());
				if (bannedIP.getTimeEnd() == null)
					preparedStatement.setNull(2, Types.TIMESTAMP);
				else
					preparedStatement.setTimestamp(2, bannedIP.getTimeEnd());
				preparedStatement.execute();
			}
		});

		if (!insert)
			return false;

		final BannedIP result = new BannedIP();
		DB.select("SELECT * FROM banned_ip WHERE mask = ?", new ParamReadStH() {

			@Override
			public void setParams(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, bannedIP.getMask());
			}

			@Override
			public void handleRead(ResultSet resultSet) throws SQLException {
				resultSet.next(); // mask is unique, only one result allowed
				result.setId(resultSet.getInt("id"));
				result.setMask(resultSet.getString("mask"));
				result.setTimeEnd(resultSet.getTimestamp("time_end"));
			}
		});
		
		return true;
	}

	/**
	 * Updates an existing record in the database.<br>
	 * This method modifies the mask and expiration time for a specific {@link BannedIP}.
	 * @param bannedIP The {@code BannedIP} object containing the updated data.
	 * @return {@code true} if the update was successful, otherwise {@code false}.
	 */
	@Override
	public boolean update(BannedIP bannedIP) {
		return DB.insertUpdate("UPDATE banned_ip SET mask = ?, time_end = ? WHERE id = ?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, bannedIP.getMask());
				if (bannedIP.getTimeEnd() == null)
					preparedStatement.setNull(2, Types.TIMESTAMP);
				else
					preparedStatement.setTimestamp(2, bannedIP.getTimeEnd());
				preparedStatement.setInt(3, bannedIP.getId());
				preparedStatement.execute();
			}
		});
	}

	/**
	 * Removes a banned IP from the database based on its network mask.<br>
	 * This method deletes the record where the {@code mask} matches the provided value.
	 * @param mask The network mask string to be removed.
	 * @return {@code true} if the deletion was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean remove(String mask) {
		return DB.insertUpdate("DELETE FROM banned_ip WHERE mask = ?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, mask);
				preparedStatement.execute();
			}
		});
	}

	/**
	 * Removes a specific IP ban from the database.<br>
	 * This method uses the mask provided in the {@code BannedIP} object to identify the record.
	 * @param bannedIP The {@link BannedIP} object containing the data to remove.
	 * @return {@code true} if the operation was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean remove(BannedIP bannedIP) {
		return DB.insertUpdate("DELETE FROM banned_ip WHERE mask = ?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
				// Changed from id to mask because we don't get id of last inserted ban
				preparedStatement.setString(1, bannedIP.getMask());
				preparedStatement.execute();
			}
		});
	}

	/**
	 * Retrieves all banned IP addresses from the database.<br>
	 * This method returns a {@code Set} of {@link BannedIP} objects.<br>
	 * It fetches every record currently stored in the {@code banned_ip} table.
	 * @return A {@code Set} containing all {@code BannedIP} records.
	 */
	@Override
	public Set<BannedIP> getAllBans() {

		final Set<BannedIP> result = new HashSet<BannedIP>();
		DB.select("SELECT * FROM banned_ip", new ReadStH() {

			@Override
			public void handleRead(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					BannedIP ip = new BannedIP();
					ip.setId(resultSet.getInt("id"));
					ip.setMask(resultSet.getString("mask"));
					ip.setTimeEnd(resultSet.getTimestamp("time_end"));
					result.add(ip);
				}
			}
		});
		
		return result;
	}

	/**
	 * Removes all expired entries from the {@code banned_ip} table.<br>
	 * This method deletes records where the timestamp is older than the current date.<br>
	 * It helps keep the database clean by removing outdated ban information.
	 */
	@Override
	public void cleanExpiredBans() {
		DB.insertUpdate("DELETE FROM banned_ip WHERE time_end < current_timestamp AND time_end IS NOT NULL");
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
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
