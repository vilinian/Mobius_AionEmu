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
import com.aionemu.gameserver.dao.OldNamesDAO;

/**
 * This class provides the database access logic for handling old character names in a {@code mysql5} environment.<br>
 * It extends {@link OldNamesDAO} to implement specific queries compatible with older MySQL schemas.
 * @author synchro2
 */
public class MySQL5OldNamesDAO extends OldNamesDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5OldNamesDAO.class);
	private static final String INSERT_QUERY = "INSERT INTO `old_names` (`player_id`, `old_name`, `new_name`) VALUES (?,?,?)";
	
	/**
	 * Checks if a specific name exists in the old names database.<br>
	 * This method queries the {@code old_names} table to see if the name is taken.<br>
	 * It returns {@code true} if the name is found or if an error occurs.
	 * @param name The name string to check.
	 * @return {@code true} if the name is already used, {@code false} otherwise.
	 */
	@Override
	public boolean isOldName(String name)
	{
		final PreparedStatement s = DB.prepareStatement("SELECT count(player_id) as cnt FROM old_names WHERE ? = old_names.old_name");
		try
		{
			s.setString(1, name);
			final ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("cnt") > 0;
		}
		catch (SQLException e)
		{
			log.error("Can't check if name " + name + ", is used, returning possitive result", e);
			return true;
		}
		finally
		{
			DB.close(s);
		}
	}
	
	/**
	 * This method adds a new record to the database.<br>
	 * It maps an old name to a new name for a specific player.
	 * @param id The unique identifier of the player.
	 * @param oldname The previous name used by the player.
	 * @param newname The current name of the player.
	 */
	@Override
	public void insertNames(int id, String oldname, String newname)
	{
		DB.insertUpdate(INSERT_QUERY, stmt ->
		{
			stmt.setInt(1, id);
			stmt.setString(2, oldname);
			stmt.setString(3, newname);
			stmt.execute();
		});
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
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
