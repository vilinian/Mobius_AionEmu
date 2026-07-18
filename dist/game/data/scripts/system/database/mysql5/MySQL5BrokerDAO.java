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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.BrokerDAO;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.broker.BrokerRace;
import com.aionemu.gameserver.model.gameobjects.BrokerItem;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * This class provides the {@code MySQL5} specific implementation for the {@link BrokerDAO} class.<br>
 * It handles database operations related to brokers using {@code MySQL5DAOUtils}.
 */
public class MySQL5BrokerDAO extends BrokerDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5BrokerDAO.class);
	
	/**
	 * Loads all items from the database into a list.<br>
	 * This method retrieves data from the {@code broker} table.<br>
	 * It maps the results to {@link BrokerItem} objects.
	 * @return A {@code List} of {@link BrokerItem} objects.
	 */
	@Override
	public List<BrokerItem> loadBroker()
	{
		final List<BrokerItem> brokerItems = new ArrayList<>();
		
		final List<Item> items = getBrokerItems();
		
		DB.select("SELECT * FROM broker", rset ->
		{
			while (rset.next())
			{
				final int itemPointer = rset.getInt("item_pointer");
				final int itemId = rset.getInt("item_id");
				final long itemCount = rset.getLong("item_count");
				final String itemCreator = rset.getString("item_creator");
				final String seller = rset.getString("seller");
				final int sellerId = rset.getInt("seller_id");
				final long price = rset.getLong("price");
				final BrokerRace itemBrokerRace = BrokerRace.valueOf(rset.getString("broker_race"));
				final Timestamp expireTime = rset.getTimestamp("expire_time");
				final Timestamp settleTime = rset.getTimestamp("settle_time");
				final int sold = rset.getInt("is_sold");
				final int settled = rset.getInt("is_settled");
				final int partSale = rset.getInt("is_partsale");
				
				final boolean isSold = sold == 1;
				final boolean isSettled = settled == 1;
				final boolean isPartSale = partSale == 1;
				
				Item item = null;
				if (!isSold)
				{
					for (Item brItem : items)
					{
						if (itemPointer == brItem.getObjectId())
						{
							item = brItem;
							break;
						}
					}
				}
				
				brokerItems.add(new BrokerItem(item, itemId, itemPointer, itemCount, itemCreator, price, seller, sellerId, itemBrokerRace, isSold, isSettled, expireTime, settleTime, isPartSale));
			}
		});
		
		return brokerItems;
	}
	
	/**
	 * Retrieves all items currently located in the broker.<br>
	 * This method queries the database for records where {@code item_location} is {@code 126}.<br>
	 * It converts each database row into an {@link Item} object.
	 * @return A {@code List} of {@link Item} objects found in the broker.
	 */
	private List<Item> getBrokerItems()
	{
		final List<Item> brokerItems = new ArrayList<>();
		
		DB.select("SELECT * FROM inventory WHERE `item_location` = 126", rset ->
		{
			while (rset.next())
			{
				final int itemUniqueId = rset.getInt("item_unique_id");
				final int itemId = rset.getInt("item_id");
				final long itemCount = rset.getLong("item_count");
				final int itemColor = rset.getInt("item_color");
				final int colorExpireTime = rset.getInt("color_expires");
				final String itemCreator = rset.getString("item_creator");
				final int expireTime = rset.getInt("expire_time");
				final int activationCount = rset.getInt("activation_count");
				final long slot = rset.getLong("slot");
				final int location = rset.getInt("item_location");
				final int enchant = rset.getInt("enchant");
				final int itemSkin = rset.getInt("item_skin");
				final int fusionedItem = rset.getInt("fusioned_item");
				final int optionalSocket = rset.getInt("optional_socket");
				final int optionalFusionSocket = rset.getInt("optional_fusion_socket");
				final int charge = rset.getInt("charge");
				final int randomBonus = rset.getInt("rnd_bonus");
				final int rndCount = rset.getInt("rnd_count");
				final int packCount = rset.getInt("pack_count");
				final int max_authorize = rset.getInt("authorize");
				final int isAmplified = rset.getInt("is_amplified");
				final int buffSkill = rset.getInt("buff_skill");
				final int reductionLevel = rset.getInt("reduction_level");
				final boolean isEnhance = rset.getBoolean("isEnhance");
				final int enhanceSkillId = rset.getInt("enhanceSkillId");
				final int enhanceSkillEnchant = rset.getInt("enhanceSkillEnchant");
				final int unSeal = rset.getInt("is_seal");
				final int skinSkill = rset.getInt("skin_skill");
				final int grindSocket = rset.getInt("grind_socket");
				final int grindColor = rset.getInt("grind_color");
				final boolean contaminated = rset.getBoolean("contaminated");
				
				brokerItems.add(new Item(itemUniqueId, itemId, itemCount, itemColor, colorExpireTime, itemCreator, expireTime, activationCount, false, false, slot, location, enchant, itemSkin, fusionedItem, optionalSocket, optionalFusionSocket, charge, randomBonus, rndCount, packCount, max_authorize, false, isAmplified == 1, buffSkill, reductionLevel, false, isEnhance, enhanceSkillId, enhanceSkillEnchant, unSeal, skinSkill, grindSocket, grindColor, 0, 0, contaminated));
			}
		});
		
		return brokerItems;
	}
	
	/**
	 * Saves a {@link BrokerItem} to the database.<br>
	 * This method handles inserting, updating, or deleting items based on their {@code PersistentState}.<br>
	 * It also updates the player inventory if a new item is added.
	 * @param item The {@code BrokerItem} to be saved.
	 * @return {@code true} if the operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean store(BrokerItem item)
	{
		boolean result = false;
		
		if (item == null)
		{
			log.warn("Null broker item on save");
			return result;
		}
		
		switch (item.getPersistentState())
		{
			case NEW:
				result = insertBrokerItem(item);
				if (item.getItem() != null)
				{
					DAOManager.getDAO(InventoryDAO.class).store(item.getItem(), item.getSellerId());
				}
				break;
			
			case DELETED:
				result = deleteBrokerItem(item);
				break;
			
			case UPDATE_ITEM_BROKER:
				result = updateItem(item);
				
			case UPDATE_REQUIRED:
				result = updateBrokerItem(item);
				break;
			default:
				break;
		}
		
		if (result)
		{
			item.setPersistentState(PersistentState.UPDATED);
		}
		
		return result;
	}
	
	/**
	 * Saves a new {@code BrokerItem} to the database.<br>
	 * This method executes an {@code INSERT} query to store item details.
	 * @param item The {@code BrokerItem} object to be saved.
	 * @return {@code true} if the insertion was successful, otherwise {@code false}.
	 */
	private boolean insertBrokerItem(BrokerItem item)
	{
		final boolean result = DB.insertUpdate("INSERT INTO `broker` (`item_pointer`, `item_id`, `item_count`, `item_creator`, `seller`, `price`, `broker_race`, `expire_time`, `settle_time`, `seller_id`, `is_sold`, `is_settled`, `is_partsale`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)", stmt ->
		{
			stmt.setInt(1, item.getItemUniqueId());
			stmt.setInt(2, item.getItemId());
			stmt.setLong(3, item.getItemCount());
			stmt.setString(4, item.getItemCreator());
			stmt.setString(5, item.getSeller());
			stmt.setLong(6, item.getPrice());
			stmt.setString(7, String.valueOf(item.getItemBrokerRace()));
			stmt.setTimestamp(8, item.getExpireTime());
			stmt.setTimestamp(9, item.getSettleTime());
			stmt.setInt(10, item.getSellerId());
			stmt.setBoolean(11, item.isSold());
			stmt.setBoolean(12, item.isSettled());
			stmt.setBoolean(13, item.isPartSale());
			
			stmt.execute();
		});
		
		return result;
	}
	
	/**
	 * Removes a specific {@link BrokerItem} from the database.<br>
	 * This method uses the unique ID, seller ID, and expiration time to identify the record.
	 * @param item The {@code BrokerItem} object to be deleted.
	 * @return {@code true} if the deletion was successful, otherwise {@code false}.
	 */
	private boolean deleteBrokerItem(BrokerItem item)
	{
		final boolean result = DB.insertUpdate("DELETE FROM `broker` WHERE `item_pointer` = ? AND `seller_id` = ? AND `expire_time` = ?", stmt ->
		{
			stmt.setInt(1, item.getItemUniqueId());
			stmt.setInt(2, item.getSellerId());
			stmt.setTimestamp(3, item.getExpireTime());
			
			stmt.execute();
		});
		
		return result;
	}
	
	/**
	 * Checks if a specific item is available for purchase from the broker.<br>
	 * This method verifies that the item exists and has not been sold yet.
	 * @param itemForCheck The unique identifier of the item to check.
	 * @return {@code true} if the item is available, otherwise {@code false}.
	 */
	@Override
	public boolean preBuyCheck(int itemForCheck)
	{
		final PreparedStatement st = DB.prepareStatement("SELECT * FROM broker WHERE `item_pointer` = ? and `is_sold` = 0");
		log.info("Checking broker item: " + itemForCheck);
		try
		{
			st.setInt(1, itemForCheck);
			
			final ResultSet rs = st.executeQuery();
			
			if (rs.next())
			{
				return true;
			}
		}
		catch (SQLException e)
		{
			log.error("Can't to prebuy broker check: ", e);
		}
		finally
		{
			DB.close(st);
		}
		
		return false;
	}
	
	/**
	 * Updates the status of a specific broker item in the database.<br>
	 * This method marks an item as settled and updates its sale status.
	 * @param item The {@code BrokerItem} object containing the updated data.
	 * @return {@code true} if the update was successful, otherwise {@code false}.
	 */
	private boolean updateBrokerItem(BrokerItem item)
	{
		final boolean result = DB.insertUpdate("UPDATE broker SET `is_sold` = ?, `is_settled` = 1, `settle_time` = ? WHERE `item_pointer` = ? AND `expire_time` = ? AND `seller_id` = ? AND `is_settled` = 0", stmt ->
		{
			stmt.setBoolean(1, item.isSold());
			stmt.setTimestamp(2, item.getSettleTime());
			stmt.setInt(3, item.getItemUniqueId());
			stmt.setTimestamp(4, item.getExpireTime());
			stmt.setInt(5, item.getSellerId());
			
			stmt.execute();
		});
		
		return result;
	}
	
	/**
	 * Updates the details of a specific broker item in the database.<br>
	 * This method modifies fields like price and count for an unsettled item.
	 * @param item The {@code BrokerItem} object containing the new data.
	 * @return {@code true} if the update was successful, otherwise {@code false}.
	 */
	private boolean updateItem(BrokerItem item)
	{
		final boolean result = DB.insertUpdate("UPDATE broker SET `item_count` = ?, `price` = ?, `is_sold` = ?, `is_settled` = ?, `settle_time` = ?, `is_partsale` = ? WHERE `item_pointer` = ? AND `expire_time` = ? AND `seller_id` = ? AND `is_settled` = 0", stmt ->
		{
			stmt.setLong(1, item.getItemCount());
			stmt.setLong(2, item.getPrice());
			stmt.setBoolean(3, item.isSold());
			stmt.setBoolean(4, item.isSettled());
			stmt.setTimestamp(5, item.getSettleTime());
			stmt.setBoolean(6, item.isPartSale());
			stmt.setInt(7, item.getItemUniqueId());
			stmt.setTimestamp(8, item.getExpireTime());
			stmt.setInt(9, item.getSellerId());
			stmt.execute();
		});
		
		return result;
	}
	
	/**
	 * Retrieves all unique identifiers from the {@code players} table.<br>
	 * This method queries the database to collect every {@code id}.<br>
	 * If an error occurs, it returns an empty array.
	 * @return An array of integers containing the player IDs.
	 */
	@Override
	public int[] getUsedIDs()
	{
		final PreparedStatement statement = DB.prepareStatement("SELECT id FROM players", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		try
		{
			final ResultSet rs = statement.executeQuery();
			rs.last();
			final int count = rs.getRow();
			rs.beforeFirst();
			final int[] ids = new int[count];
			for (int i = 0; i < count; i++)
			{
				rs.next();
				ids[i] = rs.getInt("id");
			}
			
			return ids;
		}
		catch (SQLException e)
		{
			log.error("Can't get list of id's from players table", e);
		}
		finally
		{
			DB.close(statement);
		}
		
		return new int[0];
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
