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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerMacrossesDAO;
import com.aionemu.gameserver.model.gameobjects.player.MacroList;

/**
 * This class provides the database access layer for managing player macros using a {@code MySQL5} connection.<br>
 * It extends {@link PlayerMacrossesDAO} to handle specific queries related to macro storage.
 * @author Aquanox
 */
public class MySQL5PlayerMacrossesDAO extends PlayerMacrossesDAO
{
	private static Logger log = LoggerFactory.getLogger(MySQL5PlayerMacrossesDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_macrosses` (`player_id`, `order`, `macro`) VALUES (?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE `player_macrosses` SET `macro`=? WHERE `player_id`=? AND `order`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_macrosses` WHERE `player_id`=? AND `order`=?";
	public static final String SELECT_QUERY = "SELECT `order`, `macro` FROM `player_macrosses` WHERE `player_id`=?";
	
	/**
	 * Adds a new macro to the database for a specific player.<br>
	 * This method saves the macro content at the given position.
	 * @param playerId The unique identifier of the player.
	 * @param macroPosition The numerical order or index of the macro.
	 * @param macro The text content of the macro to be saved.
	 */
	@Override
	public void addMacro(int playerId, int macroPosition, String macro)
	{
		DB.insertUpdate(INSERT_QUERY, stmt ->
		{
			log.debug("[DAO: MySQL5PlayerMacrossesDAO] storing macro " + playerId + " " + macroPosition);
			stmt.setInt(1, playerId);
			stmt.setInt(2, macroPosition);
			stmt.setString(3, macro);
			stmt.execute();
		});
	}
	
	/**
	 * Updates an existing macro for a specific player.<br>
	 * This method modifies the content of a macro at a given position.
	 * @param playerId The unique identifier of the player.
	 * @param macroPosition The numerical order or index of the macro.
	 * @param macro The new text content for the macro.
	 */
	@Override
	public void updateMacro(int playerId, int macroPosition, String macro)
	{
		DB.insertUpdate(UPDATE_QUERY, stmt ->
		{
			log.debug("[DAO: MySQL5PlayerMacrossesDAO] updating macro " + playerId + " " + macroPosition);
			stmt.setString(1, macro);
			stmt.setInt(2, playerId);
			stmt.setInt(3, macroPosition);
			stmt.execute();
		});
	}
	
	/**
	 * Removes a specific macro from the database.<br>
	 * This method deletes the entry based on the player and its position.
	 * @param playerId The unique identifier of the player.
	 * @param macroPosition The index or order of the macro to remove.
	 */
	@Override
	public void deleteMacro(int playerId, int macroPosition)
	{
		DB.insertUpdate(DELETE_QUERY, stmt ->
		{
			log.debug("[DAO: MySQL5PlayerMacrossesDAO] removing macro " + playerId + " " + macroPosition);
			stmt.setInt(1, playerId);
			stmt.setInt(2, macroPosition);
			stmt.execute();
		});
	}
	
	/**
	 * Retrieves all macros for a specific player from the database.<br>
	 * This method populates a {@link MacroList} using the data found in the table.
	 * @param playerId The unique identifier of the player.
	 * @return A new {@code MacroList} containing the player's macro data.
	 */
	@Override
	public MacroList restoreMacrosses(int playerId)
	{
		final Map<Integer, String> macrosses = new HashMap<>();
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			final ResultSet rset = stmt.executeQuery();
			log.debug("[DAO: MySQL5PlayerMacrossesDAO] loading macroses for playerId: " + playerId);
			while (rset.next())
			{
				final int order = rset.getInt("order");
				final String text = rset.getString("macro");
				macrosses.put(order, text);
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore MacroList data for player " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return new MacroList(macrosses);
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
