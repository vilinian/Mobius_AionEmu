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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.loginserver.dao.PlayerTransferDAO;
import com.aionemu.loginserver.service.ptransfer.PlayerTransferTask;

/**
 * This class provides the database access layer for handling player transfers in a {@code mysql5} environment.<br>
 * It extends {@link PlayerTransferDAO} to implement specific SQL queries required for moving characters between servers.
 * @author KID
 */
public class MySQL5PlayerTransferDAO extends PlayerTransferDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerTransferDAO.class);

	/**
	 * Retrieves all pending player transfer tasks from the database.<br>
	 * It looks for records where the {@code status} is equal to {@code 0}.
	 * @return A {@link List} containing all new {@link PlayerTransferTask} objects.
	 */
	@Override
	public List<PlayerTransferTask> getNew() {
		List<PlayerTransferTask> list = new ArrayList<>();
		PreparedStatement st = DB.prepareStatement("SELECT * FROM player_transfers WHERE `status` = ?");
		try {
			st.setInt(1, 0);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				PlayerTransferTask task = new PlayerTransferTask();
				task.id = rs.getInt("id");
				task.sourceServerId = (byte) rs.getShort("source_server");
				task.targetServerId = (byte) rs.getShort("target_server");
				task.sourceAccountId = rs.getInt("source_account_id");
				task.targetAccountId = rs.getInt("target_account_id");
				task.playerId = rs.getInt("player_id");
				list.add(task);
			}
		}
		catch (Exception e) {
			log.error("Can't select getNew: ", e);
		}
		finally {
			DB.close(st);
		}

		return list;
	}

	/**
	 * Updates the status and comment of a player transfer in the database.<br>
	 * It automatically sets the completion time based on the new status.
	 * @param task The {@link PlayerTransferTask} object containing the update details.
	 * @return {@code true} if the update was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean update(PlayerTransferTask task) {
		String table = "";
		switch (task.status) {
			case PlayerTransferTask.STATUS_ACTIVE:
				table = ", time_performed=NOW()";
				break;
			case PlayerTransferTask.STATUS_DONE:
			case PlayerTransferTask.STATUS_ERROR:
				table = ", time_done=NOW()";
				break;
		}
		
		return DB.insertUpdate("UPDATE player_transfers SET status=?, comment=?" + table + " WHERE id=?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setByte(1, task.status);
				preparedStatement.setString(2, task.comment);
				preparedStatement.setInt(3, task.id);
				preparedStatement.execute();
			}
		});
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
