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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerVarsDAO;

/**
 * This class provides the database access logic for managing player variables using {@code MySQL5}.<br>
 * It extends {@link PlayerVarsDAO} to handle specific queries and data mapping for the game's variable system.
 * @author KID
 */
public class MySQL5PlayerVarsDAO extends PlayerVarsDAO
{
	/**
	 * Retrieves all variables for a specific player from the database.<br>
	 * This method fetches data using the {@code playerId}.<br>
	 * It returns a {@code Map} containing the keys and values.
	 * @param playerId The unique identifier of the player to load.
	 * @return A {@code Map} where keys are parameter names and values are their corresponding data.
	 */
	@Override
	public Map<String, Object> load(int playerId)
	{
		final Map<String, Object> map = new HashMap<>();
		DB.select("SELECT param,value FROM player_vars WHERE player_id=?", new ParamReadStH()
		{
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					final String key = rset.getString("param");
					final String value = rset.getString("value");
					map.put(key, value);
				}
			}
			
			@Override
			public void setParams(PreparedStatement st) throws SQLException
			{
				st.setInt(1, playerId);
			}
		});
		
		return map;
	}
	
	/**
	 * Saves a new variable for a specific player.<br>
	 * This method updates the database with the provided {@code key} and {@code value}.
	 * @param playerId The unique ID of the player.
	 * @param key The name of the variable to store.
	 * @param value The data to be saved as a string.
	 * @return {@code true} if the operation succeeded, or {@code false} otherwise.
	 */
	@Override
	public boolean set(int playerId, String key, Object value)
	{
		final boolean result = DB.insertUpdate("INSERT INTO player_vars (`player_id`, `param`, `value`, `time`) VALUES (?,?,?,NOW())", stmt ->
		{
			stmt.setInt(1, playerId);
			stmt.setString(2, key);
			stmt.setString(3, value.toString());
			stmt.execute();
		});
		
		return result;
	}
	
	/**
	 * Removes a specific variable for a player from the database.<br>
	 * This method deletes the entry where the {@code playerId} and {@code key} match.
	 * @param playerId The unique ID of the player.
	 * @param key The name of the variable to remove.
	 * @return {@code true} if the operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean remove(int playerId, String key)
	{
		final boolean result = DB.insertUpdate("DELETE FROM player_vars WHERE player_id=? AND param=?", stmt ->
		{
			stmt.setInt(1, playerId);
			stmt.setString(2, key);
			stmt.execute();
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
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
