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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerWardrobeDAO;
import com.aionemu.gameserver.model.dorinerk_wardrobe.PlayerWardrobeEntry;
import com.aionemu.gameserver.model.dorinerk_wardrobe.PlayerWardrobeList;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides the {@code MySQL5} database implementation for managing player wardrobes.<br>
 * It handles data persistence operations for {@link PlayerWardrobeEntry} objects using {@code MySQL5DAOUtils}.<br>
 * It extends the base {@link PlayerWardrobeDAO} to provide specific queries for the wardrobe system.
 */
public class MySQL5PlayerWardrobeDAO extends PlayerWardrobeDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerWardrobeDAO.class);
	
	public static final String INSERT_OR_UPDATE = "INSERT INTO `player_wardrobe` (`player_id`, `item_id`, `slot`, `reskin_count`) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE `item_id` = VALUES(`item_id`), `slot` = VALUES(`slot`)";
	public static final String SELECT_QUERY = "SELECT `item_id`,`slot`,`reskin_count` FROM `player_wardrobe` WHERE `player_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_wardrobe` WHERE `player_id`=? AND `item_id`=?";
	
	/**
	 * Retrieves the wardrobe data for a specific player from the database.<br>
	 * This method fetches all entries associated with the {@code Player} object ID.<br>
	 * It returns a new {@link PlayerWardrobeList} containing the loaded items.
	 * @param player The {@code Player} whose wardrobe needs to be loaded.
	 * @return A {@link PlayerWardrobeList} containing the player's wardrobe entries.
	 */
	@Override
	public PlayerWardrobeList load(Player player)
	{
		final List<PlayerWardrobeEntry> w = new ArrayList<>();
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, player.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final int itemId = rset.getInt("item_id");
				final int slot = rset.getInt("slot");
				final int reskin = rset.getInt("slot");
				w.add(new PlayerWardrobeEntry(itemId, slot, reskin, PersistentState.UPDATED));
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore Wardrobe time for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return new PlayerWardrobeList(w);
	}
	
	/**
	 * Saves a wardrobe entry into the database.<br>
	 * This method updates existing records or inserts new ones based on the provided data.
	 * @param objectId The unique identifier for the player.
	 * @param itemId The unique identifier for the item.
	 * @param slot The specific slot index where the item is stored.
	 * @param reskin The number of reskins applied to the item.
	 * @return {@code true} if the operation succeeded, or {@code false} if an error occurred.
	 */
	@Override
	public boolean store(int objectId, int itemId, int slot, int reskin)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_OR_UPDATE);
			stmt.setInt(1, objectId);
			stmt.setInt(2, itemId);
			stmt.setInt(3, slot);
			stmt.setInt(4, reskin);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not store Wardrobe for player " + objectId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Removes a specific item from the player's wardrobe in the database.<br>
	 * This method uses the {@code DELETE_QUERY} to find the record matching both IDs.
	 * @param objectId The unique identifier of the player.
	 * @param itemId The unique identifier of the item to remove.
	 * @return {@code true} if the deletion was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean delete(int objectId, int itemId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, objectId);
			stmt.setInt(2, itemId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not delete Wardrobe for player " + objectId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Calculates the total number of items in a player's wardrobe.<br>
	 * This method queries the database for the count associated with a specific ID.<br>
	 * It returns {@code 0} if an error occurs during the database operation.
	 * @param playerObjId The unique identifier of the player to check.
	 * @return The total number of items in the wardrobe.
	 */
	@Override
	public int getItemSize(int playerObjId)
	{
		Connection con = null;
		int size = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) AS `size` FROM `player_wardrobe` WHERE `player_id`=?");
			stmt.setInt(1, playerObjId);
			final ResultSet rs = stmt.executeQuery();
			rs.next();
			size = rs.getInt("size");
			rs.close();
			stmt.close();
		}
		catch (Exception e)
		{
			return 0;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return size;
	}
	
	/**
	 * Retrieves the item ID from a specific slot for a given player.<br>
	 * This method queries the database using the {@code obj} and {@code slot} parameters.<br>
	 * It returns 0 if no item is found or an error occurs.
	 * @param obj The unique identifier of the player.
	 * @param slot The specific wardrobe slot to check.
	 * @return The integer ID of the wardrobe item, or 0 if not found.
	 */
	@Override
	public int getWardrobeItemBySlot(int obj, int slot)
	{
		Connection con = null;
		int wardrobeItemId = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement s = con.prepareStatement("SELECT `item_id` FROM `player_wardrobe` WHERE `player_id`=? AND `slot`=?");
			s.setInt(1, obj);
			s.setInt(2, slot);
			final ResultSet rs = s.executeQuery();
			rs.next();
			wardrobeItemId = rs.getInt("item_id");
			rs.close();
			s.close();
		}
		catch (Exception e)
		{
			return 0;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return wardrobeItemId;
	}
	
	/**
	 * Retrieves the current reskin count for a specific wardrobe slot.<br>
	 * This method queries the database using the provided player ID and slot index.<br>
	 * It returns 0 if no record is found or an error occurs.
	 * @param obj The unique identifier of the player object.
	 * @param slot The specific wardrobe slot to check.
	 * @return The integer value of the reskin count from the database.
	 */
	@Override
	public int getReskinCountBySlot(int obj, int slot)
	{
		Connection con = null;
		int reskinCount = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement s = con.prepareStatement("SELECT `reskin_count` FROM `player_wardrobe` WHERE `player_id`=? AND `slot`=?");
			s.setInt(1, obj);
			s.setInt(2, slot);
			final ResultSet rs = s.executeQuery();
			rs.next();
			reskinCount = rs.getInt("reskin_count");
			rs.close();
			s.close();
		}
		catch (Exception e)
		{
			return 0;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return reskinCount;
	}
	
	/**
	 * Updates the reskin count for a specific slot of a player.<br>
	 * This method modifies the database record associated with the provided {@code obj} and {@code slot}.
	 * @param obj The unique identifier for the player.
	 * @param slot The specific wardrobe slot to update.
	 * @param reskin_count The new count value to assign to the slot.
	 * @return {@code true} if the database update was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean setReskinCountBySlot(int obj, int slot, int reskin_count)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE player_wardrobe set `reskin_count`=? WHERE `player_id`=? AND `slot`=?");
			stmt.setInt(1, reskin_count);
			stmt.setInt(2, obj);
			stmt.setInt(3, slot);
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
