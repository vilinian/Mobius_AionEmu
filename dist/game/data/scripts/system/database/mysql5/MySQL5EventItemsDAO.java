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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.EventItemsDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.event.MaxCountOfDay;

/**
 * This class provides the {@code MySQL5} database implementation for handling event items.<br>
 * It extends {@link EventItemsDAO} to perform specific data access operations using {@code MySQL5DAOUtils}.
 * @author Alex
 */
public class MySQL5EventItemsDAO extends EventItemsDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5EventItemsDAO.class);
	
	public static final String INSERT_QUERY = "INSERT INTO `event_items` (`player_id`, `item_id`, `counts`) VALUES (?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `event_items` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `item_id`, `counts` FROM `event_items` WHERE `player_id`=?";
	
	private static final Predicate<MaxCountOfDay> maxCountOfDay = (MaxCountOfDay input) -> input != null;
	
	/**
	 * Loads event items from the database for a specific player.<br>
	 * This method populates the {@link Player} object with item data.<br>
	 * It uses the {@code SELECT_QUERY} to fetch records.
	 * @param player The {@code Player} whose items need to be loaded.
	 */
	@Override
	public void loadItems(Player player)
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
					final int itemId = rset.getInt("item_id");
					final int counts = rset.getInt("counts");
					player.addItemMaxCountOfDay(itemId, counts);
				}
			}
		});
	}
	
	/**
	 * Saves the current event items for a specific player to the database.<br>
	 * This method clears existing records and inserts new counts from the {@code Player} object.<br>
	 * It uses a batch process to improve performance during the save operation.
	 * @param player The {@link Player} whose items need to be stored.
	 */
	@Override
	public void storeItems(Player player)
	{
		// player.clearItemMaxThisCount();
		deleteItems(player);
		final Map<Integer, MaxCountOfDay> itemsm = player.getItemMaxThisCounts();
		
		if (itemsm == null)
		{
			return;
		}
		
		final Map<Integer, MaxCountOfDay> map = new HashMap<>();
		for (Map.Entry<Integer, MaxCountOfDay> entry : itemsm.entrySet())
		{
			if (maxCountOfDay.test(entry.getValue()))
			{
				map.put(entry.getKey(), entry.getValue());
			}
		}
		
		final Iterator<Map.Entry<Integer, MaxCountOfDay>> iterator = map.entrySet().iterator();
		if (!iterator.hasNext())
		{
			return;
		}
		
		Connection con = null;
		PreparedStatement st = null;
		try
		{
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			st = con.prepareStatement(INSERT_QUERY);
			while (iterator.hasNext())
			{
				final Map.Entry<Integer, MaxCountOfDay> entry = iterator.next();
				st.setInt(1, player.getObjectId());
				st.setInt(2, entry.getKey());
				st.setInt(3, entry.getValue().getThisCount());
				st.addBatch();
			}
			
			st.executeBatch();
			con.commit();
			player.clearItemMaxThisCount();
		}
		catch (SQLException e)
		{
			log.error("Error while storing event_items for player " + player.getObjectId(), e);
		}
		finally
		{
			DatabaseFactory.close(st, con);
		}
	}
	
	/**
	 * Removes an item from the database.<br>
	 * This method uses the {@code itemId} to identify which record to delete.<br>
	 * It executes a SQL delete command on the {@code event_items} table.
	 * @param itemId The unique identifier of the item to be removed.
	 */
	@Override
	public void deleteItems(int itemId)
	{
		DB.insertUpdate("DELETE FROM `event_items` WHERE `item_id`= ?", stmt ->
		{
			stmt.setInt(1, itemId);
			stmt.execute();
		});
	}
	
	/**
	 * Removes all event items for a specific player.<br>
	 * This method uses the {@code DELETE_QUERY} to clear data from the database.<br>
	 * It identifies the target using the {@code Player} object ID.
	 * @param player The {@link Player} whose items should be deleted.
	 */
	private void deleteItems(Player player)
	{
		DB.insertUpdate(DELETE_QUERY, stmt ->
		{
			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		});
	}
	
	/**
	 * Checks if the current database configuration supports specific requirements.<br>
	 * This method delegates the check to {@code int, int)}.
	 * @param arg0 The first requirement string.
	 * @param arg1 The first integer value.
	 * @param arg2 The second integer value.
	 * @return {@code true} if the requirements are met, otherwise {@code false}.
	 */
	@Override
	public boolean supports(String arg0, int arg1, int arg2)
	{
		return MySQL5DAOUtils.supports(arg0, arg1, arg2);
	}
}
