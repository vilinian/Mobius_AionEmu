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
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.HouseScriptsDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.player.PlayerScripts;

/**
 * This class provides the database access layer for handling house scripts using {@code MySQL5}.<br>
 * It extends {@link HouseScriptsDAO} to implement specific queries and data operations.
 * @author Rolandas
 */
public class MySQL5HouseScriptsDAO extends HouseScriptsDAO
{
	private static Logger log = LoggerFactory.getLogger(MySQL5HouseScriptsDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `house_scripts` (`house_id`,`index`,`script`) VALUES (?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE `house_scripts` SET `script`=? WHERE `house_id`=? AND `index`=?";
	public static final String DELETE_QUERY = "DELETE FROM `house_scripts` WHERE `house_id`=? AND `index`=?";
	private static final String SELECT_QUERY = "SELECT `index`,`script` FROM `house_scripts` WHERE `house_id`=?";
	
	/**
	 * Adds a new script to a specific house in the database.<br>
	 * This method saves the {@code scriptXML} data at the given {@code position}.<br>
	 * It uses the {@code INSERT_QUERY} to perform the operation.
	 * @param houseId The unique identifier for the house.
	 * @param position The index position where the script should be placed.
	 * @param scriptXML The XML content of the script to save. If {@code null}, it saves a null value.
	 */
	@Override
	public void addScript(int houseId, int position, String scriptXML)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, houseId);
			stmt.setInt(2, position);
			if (scriptXML == null)
			{
				stmt.setNull(3, Types.LONGNVARCHAR);
			}
			else
			{
				stmt.setString(3, scriptXML);
			}
			
			stmt.executeUpdate();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not save script data for houseId: " + houseId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Retrieves all scripts associated with a specific house.<br>
	 * This method fetches data from the database using the provided {@code houseId}.<br>
	 * It returns a {@link PlayerScripts} object containing the loaded scripts.
	 * @param houseId The unique identifier of the house to query.
	 * @return A {@code PlayerScripts} object populated with the house's script data.
	 */
	@Override
	public PlayerScripts getPlayerScripts(int houseId)
	{
		Connection con = null;
		final PlayerScripts scripts = new PlayerScripts(houseId);
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, houseId);
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final int position = rset.getInt("index");
				final String scriptXML = rset.getString("script");
				scripts.addScript(position, scriptXML);
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore script data for houseId: " + houseId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return scripts;
	}
	
	/**
	 * Updates an existing script in the database.<br>
	 * This method modifies the {@code scriptXML} for a specific house and position.<br>
	 * It uses the {@code UPDATE_QUERY} to perform the update.
	 * @param houseId The unique identifier of the house.
	 * @param position The index position of the script.
	 * @param scriptXML The new XML content for the script. If this is {@code null}, the database field will be set to null.
	 */
	@Override
	public void updateScript(int houseId, int position, String scriptXML)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			if (scriptXML == null)
			{
				stmt.setNull(1, Types.LONGNVARCHAR);
			}
			else
			{
				stmt.setString(1, scriptXML);
			}
			
			stmt.setInt(2, houseId);
			stmt.setInt(3, position);
			stmt.executeUpdate();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not save script data for houseId: " + houseId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Removes a specific script from the database.<br>
	 * This method uses the {@code houseId} and {@code position} to identify the record.<br>
	 * It executes a delete query on the house scripts table.
	 * @param houseId The unique identifier of the house.
	 * @param position The index position of the script to remove.
	 */
	@Override
	public void deleteScript(int houseId, int position)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, houseId);
			stmt.setInt(2, position);
			stmt.executeUpdate();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not delete script for houseId: " + houseId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
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
