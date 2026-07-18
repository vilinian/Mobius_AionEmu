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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.loginserver.dao.AccountTimeDAO;
import com.aionemu.loginserver.model.AccountTime;

/**
 * This class provides the {@code MySQL5} database implementation for the {@link AccountTimeDAO} interface.<br>
 * It handles data access operations related to account time records in a {@code MySQL5} environment.
 * @author EvilSpirit
 */
public class MySQL5AccountTimeDAO extends AccountTimeDAO {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(MySQL5AccountTimeDAO.class);

	/**
	 * Updates the time records for a specific account.<br>
	 * This method saves the {@code AccountTime} data into the database.<br>
	 * It uses an insert or replace operation to ensure the record is current.
	 *
	 * @param accountId The unique identifier of the account to update.
	 * @param accountTime The {@link AccountTime} object containing the new data.
	 * @return {@code true} if the database operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean updateAccountTime(int accountId, AccountTime accountTime) {
		return DB.insertUpdate("REPLACE INTO account_time (account_id, last_active, expiration_time, "
			+ "session_duration, accumulated_online, accumulated_rest, penalty_end) values " + "(?,?,?,?,?,?,?)",
			new IUStH() {

				@Override
				public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1, accountId);
					preparedStatement.setTimestamp(2, accountTime.getLastLoginTime());
					preparedStatement.setTimestamp(3, accountTime.getExpirationTime());
					preparedStatement.setLong(4, accountTime.getSessionDuration());
					preparedStatement.setLong(5, accountTime.getAccumulatedOnlineTime());
					preparedStatement.setLong(6, accountTime.getAccumulatedRestTime());
					preparedStatement.setTimestamp(7, accountTime.getPenaltyEnd());
					preparedStatement.execute();
				}
			});
	}

	/**
	 * Retrieves the {@link AccountTime} data for a specific account.<br>
	 * It searches the database using the provided {@code accountId}.<br>
	 * Returns {@code null} if no record is found or an error occurs.
	 * @param accountId The unique identifier of the account to look up.
	 * @return The {@link AccountTime} object containing the account's time data.
	 */
	@Override
	public AccountTime getAccountTime(int accountId) {
		AccountTime accountTime = null;
		PreparedStatement st = DB.prepareStatement("SELECT * FROM account_time WHERE account_id = ?");

		try {
			st.setLong(1, accountId);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				accountTime = new AccountTime();

				accountTime.setLastLoginTime(rs.getTimestamp("last_active"));
				accountTime.setSessionDuration(rs.getLong("session_duration"));
				accountTime.setAccumulatedOnlineTime(rs.getLong("accumulated_online"));
				accountTime.setAccumulatedRestTime(rs.getLong("accumulated_rest"));
				accountTime.setPenaltyEnd(rs.getTimestamp("penalty_end"));
				accountTime.setExpirationTime(rs.getTimestamp("expiration_time"));
			}
		}
		catch (Exception e) {
			log.error("Can't get account time for account with id: " + accountId, e);
		}
		finally {
			DB.close(st);
		}

		return accountTime;

	}

	/**
	 * Checks if the current system supports a specific database version.<br>
	 * It uses {@code int, int)} to verify compatibility.
	 * @param database The name of the database to check.
	 * @param majorVersion The major version number of the database.
	 * @param minorVersion The minor version number of the database.
	 * @return {@code true} if the database is supported, otherwise {@code false}.
	 */
	@Override
	public boolean supports(String database, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(database, majorVersion, minorVersion);
	}
}
