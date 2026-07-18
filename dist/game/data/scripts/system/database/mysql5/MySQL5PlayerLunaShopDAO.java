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
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerLunaShopDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerLunaShop;

/**
 * This class provides the {@code MySQL5} database implementation for handling player Luna Shop data.<br>
 * It extends {@link PlayerLunaShopDAO} to perform specific SQL queries for shop persistence.
 */
public class MySQL5PlayerLunaShopDAO extends PlayerLunaShopDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerLunaShopDAO.class);
	
	public static final String ADD_QUERY = "INSERT INTO `player_luna_shop` (`player_id`, `free_under`, `free_munition`, `free_chest`) VALUES (?,?,?,?)";
	public static final String SELECT_QUERY = "SELECT * FROM `player_luna_shop` WHERE `player_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_luna_shop`";
	public static final String UPDATE_QUERY = "UPDATE player_luna_shop set `free_under`=?, `free_munition`=?, `free_chest`=? WHERE `player_id`=?";
	
	/**
	 * Loads the {@link PlayerLunaShop} data for a specific player from the database.<br>
	 * This method retrieves shop settings and attaches them to the provided {@code Player}.<br>
	 * If no record is found, the player will not have a shop assigned.
	 * @param player The {@code Player} object whose shop data needs to be loaded.
	 */
	@Override
	public void load(Player player)
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
				final boolean under = rset.getBoolean("free_under");
				final boolean factory = rset.getBoolean("free_munition");
				final boolean chest = rset.getBoolean("free_chest");
				final PlayerLunaShop pls = new PlayerLunaShop(under, factory, chest);
				pls.setPersistentState(PersistentState.UPDATED);
				player.setPlayerLunaShop(pls);
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore PlayerLunaShop data for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Adds a new entry to the player luna shop table.<br>
	 * This method saves the shop status for a specific player.
	 * @param playerId The unique identifier of the player.
	 * @param freeUnderpath Set to {@code true} if the underpath is free.
	 * @param freeFactory Set to {@code true} if the factory is free.
	 * @param freeChest Set to {@code true} if the chest is free.
	 * @return {@code true} if the insertion was successful, otherwise {@code false}.
	 */
	@Override
	public boolean add(int playerId, boolean freeUnderpath, boolean freeFactory, boolean freeChest)
	{
		return DB.insertUpdate(ADD_QUERY, ps ->
		{
			ps.setInt(1, playerId);
			ps.setBoolean(2, freeUnderpath);
			ps.setBoolean(3, freeFactory);
			ps.setBoolean(4, freeChest);
			ps.execute();
			ps.close();
		});
	}
	
	/**
	 * Removes all records from the player luna shop table.<br>
	 * This method executes the {@code DELETE_QUERY}.
	 * @return {@code true} if the deletion was successful, {@code false} otherwise.
	 */
	@Override
	public boolean delete()
	{
		return DB.insertUpdate(DELETE_QUERY, ps ->
		{
			ps.execute();
			ps.close();
		});
	}
	
	/**
	 * Saves the modified items of a {@link Player} to the database.<br>
	 * This method identifies all dirty items and persists them using the player's unique IDs.
	 * @param player The {@code Player} object containing the items to be saved.
	 * @return {@code true} if the save operation was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean store(Player player)
	{
		Connection con = null;
		boolean insert = false;
		try
		{
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			final PlayerLunaShop bind = player.getPlayerLunaShop();
			switch (bind.getPersistentState())
			{
				case UPDATE_REQUIRED:
				case NEW:
					insert = updateLunaShop(con, player);
					log.info("LunaShop DB updated.");
					break;
				default:
					break;
			}
			
			bind.setPersistentState(PersistentState.UPDATED);
		}
		catch (SQLException e)
		{
			log.error("Can't open connection to save player updateLunaShop: " + player.getObjectId());
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return insert;
	}
	
	/**
	 * Updates the Luna Shop data for a specific player in the database.<br>
	 * This method uses the {@code UPDATE_QUERY} to save current shop settings.<br>
	 * It commits the changes to the database if successful.
	 * @param con The active {@code Connection} used to execute the query.
	 * @param player The {@link Player} object containing the Luna Shop data to be saved.
	 * @return {@code true} if the update was successful, or {@code false} if an error occurred.
	 */
	public boolean updateLunaShop(Connection con, Player player)
	{
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(UPDATE_QUERY);
			final PlayerLunaShop lr = player.getPlayerLunaShop();
			stmt.setBoolean(1, lr.isFreeUnderpath());
			stmt.setBoolean(2, lr.isFreeFactory());
			stmt.setBoolean(3, lr.isFreeChest());
			stmt.setInt(4, player.getObjectId());
			stmt.addBatch();
			stmt.executeBatch();
			con.commit();
		}
		catch (Exception e)
		{
			log.error("Could not update PlayerLunaShop data for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(stmt);
		}
		
		return true;
	}
	
	/**
	 * Updates the Luna Shop settings for a specific object ID.<br>
	 * This method modifies the free underpath, factory, and chest statuses in the database.
	 * @param obj The unique identifier of the object to update.
	 * @param freeUnderpath Set to {@code true} if the underpath is free.
	 * @param freeFactory Set to {@code true} if the factory is free.
	 * @param freeChest Set to {@code true} if the chest is free.
	 * @return {@code true} if the update was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean setLunaShopByObjId(int obj, boolean freeUnderpath, boolean freeFactory, boolean freeChest)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setBoolean(1, freeUnderpath);
			stmt.setBoolean(2, freeFactory);
			stmt.setBoolean(3, freeChest);
			stmt.setInt(4, obj);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
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
