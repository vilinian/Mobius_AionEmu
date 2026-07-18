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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ReadStH;
import com.aionemu.loginserver.GameServerInfo;
import com.aionemu.loginserver.dao.GameServersDAO;

/**
 * This class provides the {@code MySQL5} database implementation for the {@link GameServersDAO}.<br>
 * It handles data access operations specifically for game server information using a {@code MySQL5} backend.
 * @author -Nemesiss-
 */
public class MySQL5GameServersDAO extends GameServersDAO {

	/**
	 * Retrieves all game servers from the database.<br>
	 * It maps each server ID to its corresponding {@link GameServerInfo} object.
	 * @return A {@code Map} containing all game servers.
	 */
	@Override
	public Map<Byte, GameServerInfo> getAllGameServers() {

		final Map<Byte, GameServerInfo> result = new HashMap<Byte, GameServerInfo>();
		DB.select("SELECT * FROM gameservers", new ReadStH() {

			@Override
			public void handleRead(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					byte id = resultSet.getByte("id");
					String ipMask = resultSet.getString("mask");
					String password = resultSet.getString("password");
					GameServerInfo gsi = new GameServerInfo(id, ipMask, password);
					result.put(id, gsi);
				}
			}
		});
		
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
