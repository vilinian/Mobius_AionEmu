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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.ServerVariablesDAO;

/**
 * This class provides the Data Access Object (DAO) implementation for managing server variables in a {@code mysql5} database.<br>
 * It extends {@link ServerVariablesDAO} to handle specific queries and operations required by the MySQL 5 system.
 * @author Ben
 */
public class MySQL5ServerVariablesDAO extends ServerVariablesDAO
{
	private static Logger log = LoggerFactory.getLogger(ServerVariablesDAO.class);
	
	/**
	 * Loads a variable value from the database.<br>
	 * It searches for a key matching the provided {@code var}.<br>
	 * If the key is not found or an error occurs, it returns {@code 0}.
	 * @param var The name of the server variable to load.
	 * @return The integer value of the variable.
	 */
	@Override
	public int load(String var)
	{
		final PreparedStatement ps = DB.prepareStatement("SELECT `value` FROM `server_variables` WHERE `key`=?");
		try
		{
			ps.setString(1, var);
			final ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				return Integer.parseInt(rs.getString("value"));
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
		
		return 0;
	}
	
	/**
	 * Saves a variable and its associated time to the database.<br>
	 * This method uses a {@code REPLACE INTO} statement to update existing values.
	 * @param var The name of the variable to store.
	 * @param time The integer value representing the time.
	 * @return {@code true} if the operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean store(String var, int time)
	{
		boolean success = false;
		final PreparedStatement ps = DB.prepareStatement("REPLACE INTO `server_variables` (`key`,`value`) VALUES (?,?)");
		try
		{
			ps.setString(1, var);
			ps.setString(2, String.valueOf(time));
			success = ps.executeUpdate() > 0;
		}
		catch (SQLException e)
		{
			log.error("Error storing server time", e);
		}
		finally
		{
			DB.close(ps);
		}
		
		return success;
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
