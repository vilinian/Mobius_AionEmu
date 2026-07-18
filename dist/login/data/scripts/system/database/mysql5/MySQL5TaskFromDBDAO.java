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

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.loginserver.dao.TaskFromDBDAO;
import com.aionemu.loginserver.taskmanager.handler.TaskFromDBHandler;
import com.aionemu.loginserver.taskmanager.handler.TaskFromDBHandlerHolder;
import com.aionemu.loginserver.taskmanager.trigger.TaskFromDBTrigger;
import com.aionemu.loginserver.taskmanager.trigger.TaskFromDBTriggerHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class provides the database access object for handling tasks stored in a {@code mysql5} database.<br>
 * It extends {@link TaskFromDBDAO} to implement specific queries compatible with the MySQL 5.x engine.
 * @author Divinity, nrg
 */
public class MySQL5TaskFromDBDAO extends TaskFromDBDAO {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(MySQL5TaskFromDBDAO.class);
	private static final String SELECT_ALL_QUERY = "SELECT * FROM tasks ORDER BY id";

	/**
	 * Retrieves all task triggers from the database.<br>
	 * This method executes a query to fetch every record in the tasks table.<br>
	 * It creates new instances of {@link TaskFromDBTrigger} and their handlers dynamically.
	 * @return An {@code ArrayList} containing all loaded {@link TaskFromDBTrigger} objects.
	 */
	@Override
	public ArrayList<TaskFromDBTrigger> getAllTasks() {
		final ArrayList<TaskFromDBTrigger> result = new ArrayList<TaskFromDBTrigger>();

		Connection con = null;

		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_ALL_QUERY);

			ResultSet rset = stmt.executeQuery();

			while (rset.next()) {
				try {
					TaskFromDBTrigger trigger = TaskFromDBTriggerHolder.valueOf(rset.getString("trigger_type")).getTriggerClass().newInstance();
					TaskFromDBHandler handler = TaskFromDBHandlerHolder.valueOf(rset.getString("task_type")).getTaskClass().newInstance();

					handler.setTaskId(rset.getInt("id"));

					String execParamsResult = rset.getString("exec_param");
					if (execParamsResult != null) {
						handler.setParams(rset.getString("exec_param").split(" "));
					}

					trigger.setHandlerToTrigger(handler);

					String triggerParamsResult = rset.getString("trigger_param");
					if (triggerParamsResult != null) {
						trigger.setParams(rset.getString("trigger_param").split(" "));
					}

					result.add(trigger);

				} catch (InstantiationException ex) {
					log.error(ex.getMessage(), ex);
				} catch (IllegalAccessException ex) {
					log.error(ex.getMessage(), ex);
				}
			}

			rset.close();
			stmt.close();
		} catch (SQLException e) {
			log.error("Loading tasks failed: ", e);
		} finally {
			DatabaseFactory.close(stmt, con);
		}

		return result;
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
