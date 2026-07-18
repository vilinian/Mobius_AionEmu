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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerBindPointDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.BindPointPosition;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides the database access layer for managing player bind points using {@code MySQL5}.<br>
 * It extends {@link PlayerBindPointDAO} to handle specific queries related to {@link Player} coordinates.
 * @author evilset
 */
public class MySQL5PlayerBindPointDAO extends PlayerBindPointDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerBindPointDAO.class);
	public static final String INSERT_QUERY = "REPLACE INTO `player_bind_point` (`player_id`, `map_id`, `x`, `y`, `z`, `heading`) VALUES (?,?,?,?,?,?)";
	public static final String SELECT_QUERY = "SELECT `map_id`, `x`, `y`, `z`, `heading` FROM `player_bind_point` WHERE `player_id`=?";
	public static final String UPDATE_QUERY = "UPDATE player_bind_point set `map_id`=?, `x`=?, `y`=? , `z`=?, `heading`=? WHERE `player_id`=?";
	
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
	
	/**
	 * Loads the bind point data for a specific {@link Player} from the database.<br>
	 * This method retrieves coordinates and map information to update the player's state.<br>
	 * If no record is found, the player will not have a loaded bind point.
	 * @param player The {@code Player} object whose bind point needs to be loaded.
	 */
	@Override
	public void loadBindPoint(Player player)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, player.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			if (rset.next())
			{
				final int mapId = rset.getInt("map_id");
				final float x = rset.getFloat("x");
				final float y = rset.getFloat("y");
				final float z = rset.getFloat("z");
				final byte heading = rset.getByte("heading");
				final BindPointPosition bind = new BindPointPosition(mapId, x, y, z, heading);
				bind.setPersistentState(PersistentState.UPDATED);
				player.setBindPoint(bind);
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore BindPointPosition data for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Saves the bind point information for a specific player to the database.<br>
	 * This method uses {@code INSERT_QUERY} to store the coordinates and heading.<br>
	 * It returns {@code true} if the operation succeeds.<br>
	 * It returns {@code false} if an error occurs during the database execution.
	 * @param player The {@link Player} object containing the bind point data to save.
	 * @return {@code true} if the data was saved successfully, otherwise {@code false}.
	 */
	@Override
	public boolean insertBindPoint(Player player)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			final BindPointPosition bpp = player.getBindPoint();
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, bpp.getMapId());
			stmt.setFloat(3, bpp.getX());
			stmt.setFloat(4, bpp.getY());
			stmt.setFloat(5, bpp.getZ());
			stmt.setByte(6, bpp.getHeading());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not store BindPointPosition data for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Updates the bind point coordinates in the database for a specific player.<br>
	 * This method saves the current {@link BindPointPosition} of the {@code player}.
	 * @param player The {@code Player} object whose data needs to be updated.
	 * @return {@code true} if the update was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean updateBindPoint(Player player)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			final BindPointPosition bpp = player.getBindPoint();
			stmt.setInt(1, bpp.getMapId());
			stmt.setFloat(2, bpp.getX());
			stmt.setFloat(3, bpp.getY());
			stmt.setFloat(4, bpp.getZ());
			stmt.setByte(5, bpp.getHeading());
			stmt.setFloat(6, player.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not update BindPointPosition data for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Saves the bind point information for a {@link Player} to the database.<br>
	 * This method checks the persistent state of the bind point and performs an insert or update accordingly.
	 * @param player The {@code Player} object whose bind point needs to be saved.
	 * @return {@code true} if the save operation was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean store(Player player)
	{
		boolean insert = false;
		final BindPointPosition bind = player.getBindPoint();
		
		switch (bind.getPersistentState())
		{
			case NEW:
				insert = insertBindPoint(player);
				break;
			case UPDATE_REQUIRED:
				insert = updateBindPoint(player);
				break;
			default:
				break;
		}
		
		bind.setPersistentState(PersistentState.UPDATED);
		return insert;
	}
}
