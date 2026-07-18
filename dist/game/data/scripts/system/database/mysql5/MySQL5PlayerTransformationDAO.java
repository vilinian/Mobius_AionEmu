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
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerTransformationDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides the {@code MySQL5} database implementation for player transformations.<br>
 * It handles all data access operations related to character appearances and visual changes.<br>
 * It extends {@link PlayerTransformationDAO} to interact with the underlying database.
 */
public class MySQL5PlayerTransformationDAO extends PlayerTransformationDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerTransformationDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_transformation` (`player_id`, `panel_id`, `item_id`) VALUES (?,?,?)";
	public static final String SELECT_QUERY = "SELECT * FROM `player_transformation` WHERE `player_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_transformation` WHERE `player_id`=?";
	
	/**
	 * Loads the transformation data for a specific player from the database.<br>
	 * This method updates the {@code TransformModel} of the provided {@link Player}.
	 * @param player The {@code Player} object whose transformation data needs to be loaded.
	 */
	@Override
	public void loadPlTransfo(Player player)
	{
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					final int panelId = rset.getInt("panel_id");
					final int itemId = rset.getInt("item_id");
					player.getTransformModel().setPanelId(panelId);
					player.getTransformModel().setItemId(itemId);
				}
			}
		});
	}
	
	/**
	 * Saves a player transformation to the database.<br>
	 * This method uses {@code INSERT_QUERY} to store the data.<br>
	 * It returns {@code true} if the operation succeeds.<br>
	 * It returns {@code false} if an error occurs during the database operation.
	 * @param playerId The unique identifier of the player.
	 * @param panelId The ID of the transformation panel.
	 * @param itemId The ID of the specific item.
	 * @return A boolean indicating whether the save was successful.
	 */
	@Override
	public boolean storePlTransfo(int playerId, int panelId, int itemId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, panelId);
			stmt.setInt(3, itemId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not store Player Tranformation." + playerId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Removes all transformation records for a specific player from the database.<br>
	 * This method uses the {@code DELETE_QUERY} to clear data associated with the provided ID.
	 * @param playerId The unique identifier of the player whose data should be deleted.
	 * @return {@code true} if the deletion was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean deletePlTransfo(int playerId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, playerId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not delete f2p for player " + playerId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
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
