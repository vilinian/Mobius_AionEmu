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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.PlayerStorage;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.services.item.ItemService;

/**
 * This class provides the {@code MySQL5} database implementation for inventory operations.<br>
 * It handles saving and loading player items, equipment, and storage data to a {@code MySQL5} database.<br>
 * It extends {@link InventoryDAO} to provide specific SQL queries for these actions.
 * @author ATracer
 */
public class MySQL5InventoryDAO extends InventoryDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5InventoryDAO.class);
	public static final String SELECT_QUERY = "SELECT `item_unique_id`, `item_id`, `item_count`, `item_color`, `color_expires`, `item_creator`, `expire_time`, `activation_count`, `is_equiped`, `is_soul_bound`, `slot`, `enchant`, `item_skin`, `fusioned_item`, `optional_socket`, `optional_fusion_socket`, `charge`, `rnd_bonus`, `rnd_count`, `pack_count`, `authorize`, `is_packed`, `is_amplified`, `buff_skill`, `reduction_level`, `luna_reskin`, `isEnhance`, `enhanceSkillId`, `enhanceSkillEnchant`, `is_seal`, `skin_skill`, `grind_socket`, `grind_color`, `grind_stone`, `grind_slot`, `contaminated` FROM `inventory` WHERE `item_owner`=? AND `item_location`=? AND `is_equiped`=?";
	public static final String INSERT_QUERY = "INSERT INTO `inventory` (`item_unique_id`, `item_id`, `item_count`, `item_color`, `color_expires`, `item_creator`, `expire_time`, `activation_count`, `item_owner`, `is_equiped`, `is_soul_bound`, `slot`, `item_location`, `enchant`, `item_skin`, `fusioned_item`, `optional_socket`, `optional_fusion_socket`, `charge`, `rnd_bonus`, `rnd_count`, `pack_count`, `authorize`, `is_packed`, `is_amplified`, `buff_skill`, `reduction_level`, `luna_reskin`, `isEnhance`, `enhanceSkillId`, `enhanceSkillEnchant`, `is_seal`, `skin_skill`, `grind_socket`, `grind_color`, `grind_stone`, `grind_slot`, `contaminated`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE inventory SET  item_count=?, item_color=?, color_expires=?, item_creator=?, expire_time=?, activation_count=?,item_owner=?, is_equiped=?, is_soul_bound=?, slot=?, item_location=?, enchant=?, item_skin=?, fusioned_item=?, optional_socket=?, optional_fusion_socket=?, charge=?, rnd_bonus=?, rnd_count=?, pack_count=?, authorize=?, is_packed=?, is_amplified=?, buff_skill=?, reduction_level=?, luna_reskin=?, isEnhance=?, enhanceSkillId=?, enhanceSkillEnchant=?, is_seal=?, skin_skill=?, grind_socket=?, grind_color=?, grind_color=?, grind_slot=?, contaminated=? WHERE item_unique_id=?";
	public static final String DELETE_QUERY = "DELETE FROM inventory WHERE item_unique_id=?";
	public static final String DELETE_CLEAN_QUERY = "DELETE FROM inventory WHERE item_owner=? AND item_location != 2"; // legion warehouse needs not to be excluded, since players and legions are IDAwareDAOs
	public static final String SELECT_ACCOUNT_QUERY = "SELECT `account_id` FROM `players` WHERE `id`=?";
	public static final String SELECT_LEGION_QUERY = "SELECT `legion_id` FROM `legion_members` WHERE `player_id`=?";
	public static final String DELETE_ACCOUNT_WH = "DELETE FROM inventory WHERE item_owner=? AND item_location=2";
	public static final String SELECT_QUERY2 = "SELECT * FROM `inventory` WHERE `item_owner`=? AND `item_location`=?";
	private static final Predicate<Item> itemsToInsertPredicate = (Item input) -> (input != null) && (PersistentState.NEW == input.getPersistentState());
	private static final Predicate<Item> itemsToUpdatePredicate = (Item input) -> (input != null) && (PersistentState.UPDATE_REQUIRED == input.getPersistentState());
	private static final Predicate<Item> itemsToDeletePredicate = (Item input) -> (input != null) && (PersistentState.DELETED == input.getPersistentState());
	
	/**
	 * Loads the storage data for a specific player from the database.<br>
	 * This method retrieves all items associated with the given {@code StorageType}.<br>
	 * It handles account warehouse lookups by converting the player ID to an account ID.
	 * @param playerId The unique identifier of the player.
	 * @param storageType The type of storage to load.
	 * @return A {@link Storage} object containing the loaded items.
	 */
	@Override
	public Storage loadStorage(int playerId, StorageType storageType)
	{
		final Storage inventory = new PlayerStorage(storageType);
		final int storage = storageType.getId();
		final int equipped = 0;
		
		if (storageType == StorageType.ACCOUNT_WAREHOUSE)
		{
			playerId = loadPlayerAccountId(playerId);
		}
		
		final int owner = playerId;
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, owner);
			stmt.setInt(2, storage);
			stmt.setInt(3, equipped);
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final Item item = constructItem(storage, rset);
				item.setPersistentState(PersistentState.UPDATED);
				if (item.getItemTemplate() == null)
				{
					log.error(playerId + "loaded error item, itemUniqueId is: " + item.getObjectId());
				}
				else
				{
					inventory.onLoadHandler(item);
				}
			}
			
			rset.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore storage data for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(stmt, con);
		}
		
		return inventory;
	}
	
	/**
	 * Retrieves a list of items directly from the database for a specific player and storage type.<br>
	 * This method handles account warehouse logic by converting the {@code playerId} to an account ID if necessary.<br>
	 * It returns an empty list if an error occurs during the database operation.
	 * @param playerId The unique identifier of the player.
	 * @param storageType The type of storage to load from.
	 * @return A {@code List} of {@link Item} objects found in the specified storage.
	 */
	@Override
	public List<Item> loadStorageDirect(int playerId, StorageType storageType)
	{
		final List<Item> list = new ArrayList<>();
		final int storage = storageType.getId();
		
		if (storageType == StorageType.ACCOUNT_WAREHOUSE)
		{
			playerId = loadPlayerAccountId(playerId);
		}
		
		final int owner = playerId;
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_QUERY2);
			stmt.setInt(1, owner);
			stmt.setInt(2, storageType.getId());
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				list.add(constructItem(storage, rset));
			}
			
			rset.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore loadStorageDirect data for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(stmt, con);
		}
		
		return list;
	}
	
	/**
	 * Loads the equipped items for a specific {@link Player}.<br>
	 * This method retrieves data from the database and populates an {@code Equipment} object.<br>
	 * If an error occurs during loading, it logs the exception and returns the empty equipment.
	 * @param player The {@link Player} whose equipment needs to be loaded.
	 * @return The populated {@code Equipment} object for the player.
	 */
	@Override
	public Equipment loadEquipment(Player player)
	{
		final Equipment equipment = new Equipment(player);
		
		final int playerId = player.getObjectId();
		final int storage = 0;
		final int equipped = 1;
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, storage);
			stmt.setInt(3, equipped);
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final Item item = constructItem(storage, rset);
				item.setPersistentState(PersistentState.UPDATED);
				equipment.onLoadHandler(item);
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore Equipment data for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return equipment;
	}
	
	/**
	 * Retrieves the list of equipped items for a specific player.<br>
	 * This method fetches data from the database using the {@code playerId}.<br>
	 * It returns an empty list if no equipment is found or an error occurs.
	 * @param playerId The unique identifier of the player.
	 * @return A {@code List} of {@link Item} objects currently equipped by the player.
	 */
	@Override
	public List<Item> loadEquipment(int playerId)
	{
		final List<Item> items = new ArrayList<>();
		final int storage = 0;
		final int equipped = 1;
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, storage);
			stmt.setInt(3, equipped);
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final Item item = constructItem(storage, rset);
				items.add(item);
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore Equipment data for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return items;
	}
	
	/**
	 * Creates a new {@link Item} object from a database result set.<br>
	 * This method maps the columns in the {@code rset} to the {@code Item} constructor.<br>
	 * It uses the provided {@code storage} value for the item's location.
	 * @param storage The storage ID where the item is located.
	 * @param rset The {@link ResultSet} containing the database row data.
	 * @return A new {@link Item} instance populated with data from the result set.
	 * @throws SQLException If a database access error occurs.
	 */
	private Item constructItem(int storage, ResultSet rset) throws SQLException
	{
		final int itemUniqueId = rset.getInt("item_unique_id");
		final int itemId = rset.getInt("item_id");
		final long itemCount = rset.getLong("item_count");
		final int itemColor = rset.getInt("item_color");
		final int colorExpireTime = rset.getInt("color_expires");
		final String itemCreator = rset.getString("item_creator");
		final int expireTime = rset.getInt("expire_time");
		final int activationCount = rset.getInt("activation_count");
		final int isEquiped = rset.getInt("is_equiped");
		final int isSoulBound = rset.getInt("is_soul_bound");
		final long slot = rset.getLong("slot");
		final int enchant = rset.getInt("enchant");
		final int itemSkin = rset.getInt("item_skin");
		final int fusionedItem = rset.getInt("fusioned_item");
		final int optionalSocket = rset.getInt("optional_socket");
		final int optionalFusionSocket = rset.getInt("optional_fusion_socket");
		final int charge = rset.getInt("charge");
		final int randomBonus = rset.getInt("rnd_bonus");
		final int rndCount = rset.getInt("rnd_count");
		final int packCount = rset.getInt("pack_count");
		final int isPacked = rset.getInt("is_packed");
		final int max_authorize = rset.getInt("authorize");
		final int isAmplified = rset.getInt("is_amplified");
		final int amplificationSkill = rset.getInt("buff_skill");
		final int reductionLevel = rset.getInt("reduction_level");
		final int isLunaReskin = rset.getInt("luna_reskin");
		final boolean isEnhance = rset.getBoolean("isEnhance");
		final int enhanceSkillId = rset.getInt("enhanceSkillId");
		final int enhanceSkillEnchant = rset.getInt("enhanceSkillEnchant");
		final int unSeal = rset.getInt("is_seal");
		final int skinSkill = rset.getInt("skin_skill");
		final int grindSocket = rset.getInt("grind_socket");
		final int grindColor = rset.getInt("grind_color");
		final long grindStone = rset.getInt("grind_stone");
		final int grindSlot = rset.getInt("grind_slot");
		final boolean contaminated = rset.getBoolean("contaminated");
		final Item item = new Item(itemUniqueId, itemId, itemCount, itemColor, colorExpireTime, itemCreator, expireTime, activationCount, isEquiped == 1, isSoulBound == 1, slot, storage, enchant, itemSkin, fusionedItem, optionalSocket, optionalFusionSocket, charge, randomBonus, rndCount, packCount, max_authorize, isPacked == 1, isAmplified == 1, amplificationSkill, reductionLevel, isLunaReskin == 1, isEnhance, enhanceSkillId, enhanceSkillEnchant, unSeal, skinSkill, grindSocket, grindColor, grindStone, grindSlot, contaminated);
		return item;
	}
	
	/**
	 * Retrieves the {@code account_id} for a specific player from the database.<br>
	 * This method uses the provided {@code playerId} to execute a query.<br>
	 * It returns {@code 0} if no record is found or an error occurs.
	 * @param playerId The unique identifier of the player to look up.
	 * @return The integer account ID associated with the player.
	 */
	private int loadPlayerAccountId(int playerId)
	{
		Connection con = null;
		int accountId = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_ACCOUNT_QUERY);
			stmt.setInt(1, playerId);
			final ResultSet rset = stmt.executeQuery();
			if (rset.next())
			{
				accountId = rset.getInt("account_id");
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore accountId data for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return accountId;
	}
	
	/**
	 * Retrieves the {@code legion_id} from the database for a specific player.<br>
	 * This method uses the provided {@code playerId} to execute a query.<br>
	 * It returns 0 if no record is found or an error occurs.
	 * @param playerId The unique identifier of the player.
	 * @return The integer ID of the legion associated with the player.
	 */
	public int loadLegionId(int playerId)
	{
		Connection con = null;
		int legionId = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_LEGION_QUERY);
			stmt.setInt(1, playerId);
			final ResultSet rset = stmt.executeQuery();
			if (rset.next())
			{
				legionId = rset.getInt("legion_id");
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Failed to load legion id for player id: " + playerId, e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return legionId;
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
		final int playerId = player.getObjectId();
		final Integer accountId = player.getPlayerAccount() != null ? player.getPlayerAccount().getId() : null;
		final Integer legionId = player.getLegion() != null ? player.getLegion().getLegionId() : null;
		
		final List<Item> allPlayerItems = player.getDirtyItemsToUpdate();
		return store(allPlayerItems, playerId, accountId, legionId);
	}
	
	/**
	 * Saves a specific {@code Item} to the database for a given {@link Player}.<br>
	 * This method handles the necessary IDs like account and legion automatically.
	 * @param item The {@code Item} object to be saved.
	 * @param player The {@link Player} who owns the item.
	 * @return {@code true} if the save was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean store(Item item, Player player)
	{
		final int playerId = player.getObjectId();
		final int accountId = player.getPlayerAccount().getId();
		final Integer legionId = player.getLegion() != null ? player.getLegion().getLegionId() : null;
		
		return store(item, playerId, accountId, legionId);
	}
	
	/**
	 * Saves a list of {@code Item} objects to the database for a specific player.<br>
	 * This method automatically determines the correct account and legion IDs if they are missing.<br>
	 * It calls the overloaded {@code int, Integer, Integer)} method to complete the operation.
	 * @param items The list of {@code Item} objects to save.
	 * @param playerId The unique ID of the player owning the items.
	 * @return {@code true} if the storage was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean store(List<Item> items, int playerId)
	{
		Integer accountId = null;
		Integer legionId = null;
		
		for (Item item : items)
		{
			if ((accountId == null) && (item.getItemLocation() == StorageType.ACCOUNT_WAREHOUSE.getId()))
			{
				accountId = loadPlayerAccountId(playerId);
			}
			
			if ((legionId == null) && (item.getItemLocation() == StorageType.LEGION_WAREHOUSE.getId()))
			{
				final int localLegionId = loadLegionId(playerId);
				if (localLegionId > 0)
				{
					legionId = localLegionId;
				}
			}
		}
		
		return store(items, playerId, accountId, legionId);
	}
	
	/**
	 * Saves a list of {@code Item} objects to the database.<br>
	 * This method handles inserting, updating, and deleting items in one transaction.<br>
	 * It updates the persistent state of all provided items to {@code PersistentState.UPDATED}.
	 * @param items The list of {@code Item} objects to be processed.
	 * @param playerId The unique identifier for the player.
	 * @param accountId The unique identifier for the account.
	 * @param legionId The unique identifier for the legion.
	 * @return {@code true} if all database operations succeeded, otherwise {@code false}.
	 */
	@Override
	public boolean store(List<Item> items, Integer playerId, Integer accountId, Integer legionId)
	{
		final Collection<Item> itemsToUpdate = items.stream().filter(itemsToUpdatePredicate).collect(Collectors.toList());
		final Collection<Item> itemsToInsert = items.stream().filter(itemsToInsertPredicate).collect(Collectors.toList());
		final Collection<Item> itemsToDelete = items.stream().filter(itemsToDeletePredicate).collect(Collectors.toList());
		
		boolean deleteResult = false;
		boolean insertResult = false;
		boolean updateResult = false;
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			deleteResult = deleteItems(con, itemsToDelete);
			insertResult = insertItems(con, itemsToInsert, playerId, accountId, legionId);
			updateResult = updateItems(con, itemsToUpdate, playerId, accountId, legionId);
		}
		catch (SQLException e)
		{
			log.error("Can't open connection to save player inventory: " + playerId);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		for (Item item : items)
		{
			item.setPersistentState(PersistentState.UPDATED);
		}
		
		if (!itemsToDelete.isEmpty() && deleteResult)
		{
			ItemService.releaseItemIds(itemsToDelete);
		}
		
		return deleteResult && insertResult && updateResult;
	}
	
	/**
	 * Determines the owner identifier for a specific {@code Item}.<br>
	 * It checks the item location to decide whether to return the account, legion, or player ID.
	 * @param item The {@code Item} object to check.
	 * @param playerId The unique identifier of the player.
	 * @param accountId The unique identifier of the account.
	 * @param legionId The unique identifier of the legion.
	 * @return The integer ID of the owner based on the item's storage location.
	 */
	private int getItemOwnerId(Item item, Integer playerId, Integer accountId, Integer legionId)
	{
		if (item.getItemLocation() == StorageType.ACCOUNT_WAREHOUSE.getId())
		{
			return accountId;
		}
		
		if (item.getItemLocation() == StorageType.LEGION_WAREHOUSE.getId())
		{
			return legionId != null ? legionId : playerId;
		}
		
		return playerId;
	}
	
	/**
	 * Inserts a collection of {@code Item} objects into the database.<br>
	 * This method uses a batch statement to improve performance.<br>
	 * It returns {@code true} if the operation succeeds or if the input list is empty.<br>
	 * If an error occurs during execution, it logs the exception and returns {@code false}.
	 * @param con The active database {@link Connection}.
	 * @param items The collection of {@code Item} objects to insert.
	 * @param playerId The unique ID of the player.
	 * @param accountId The unique ID of the account.
	 * @param legionId The unique ID of the legion.
	 * @return {@code true} if successful, {@code false} otherwise.
	 */
	private boolean insertItems(Connection con, Collection<Item> items, Integer playerId, Integer accountId, Integer legionId)
	{
		if (GenericValidator.isBlankOrNull(items))
		{
			return true;
		}
		
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(INSERT_QUERY);
			
			for (Item item : items)
			{
				stmt.setInt(1, item.getObjectId());
				stmt.setInt(2, item.getItemTemplate().getTemplateId());
				stmt.setLong(3, item.getItemCount());
				stmt.setInt(4, item.getItemColor());
				stmt.setInt(5, item.getColorExpireTime());
				stmt.setString(6, item.getItemCreator());
				stmt.setInt(7, item.getExpireTime());
				stmt.setInt(8, item.getActivationCount());
				stmt.setInt(9, getItemOwnerId(item, playerId, accountId, legionId));
				stmt.setBoolean(10, item.isEquipped());
				stmt.setInt(11, item.isSoulBound() ? 1 : 0);
				stmt.setLong(12, item.getEquipmentSlot());
				stmt.setInt(13, item.getItemLocation());
				stmt.setInt(14, item.getItemTemplate().getMaxAuthorize() > 0 ? 0 : item.getEnchantOrAuthorizeLevel());
				stmt.setInt(15, item.getItemSkinTemplate().getTemplateId());
				stmt.setInt(16, item.getFusionedItemId());
				stmt.setInt(17, item.getOptionalSocket());
				stmt.setInt(18, item.getOptionalFusionSocket());
				stmt.setInt(19, item.getChargePoints());
				stmt.setInt(20, item.getBonusNumber());
				stmt.setInt(21, item.getRandomCount());
				stmt.setInt(22, item.getPackCount());
				stmt.setInt(23, item.getItemTemplate().getMaxAuthorize() > 0 ? item.getEnchantOrAuthorizeLevel() : 0);
				stmt.setBoolean(24, item.isPacked());
				stmt.setBoolean(25, item.isAmplified());
				stmt.setInt(26, item.getAmplificationSkill());
				stmt.setInt(27, item.getReductionLevel());
				stmt.setBoolean(28, item.isLunaReskin());
				stmt.setBoolean(29, item.isEnhance());
				stmt.setInt(30, item.getEnhanceSkillId());
				stmt.setInt(31, item.getEnhanceEnchantLevel());
				stmt.setInt(32, item.getUnSeal());
				stmt.setInt(33, item.getItemSkinSkill());
				stmt.setInt(34, item.getGrindSocket());
				stmt.setInt(35, item.getGrindColor());
				stmt.setLong(36, item.getGrindStone());
				stmt.setInt(37, item.getGrindSlot());
				stmt.setBoolean(38, item.isContaminated());
				stmt.addBatch();
			}
			
			stmt.executeBatch();
			con.commit();
		}
		catch (Exception e)
		{
			log.error("Failed to execute insert batch", e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(stmt);
		}
		
		return true;
	}
	
	/**
	 * Updates the database records for a collection of items.<br>
	 * This method uses a batch update to save item properties for a specific player.<br>
	 * It returns {@code true} if the operation succeeds or if the input list is empty.
	 * @param con The active {@link Connection} used to execute the SQL query.
	 * @param items The collection of {@link Item} objects to be updated.
	 * @param playerId The unique identifier for the player.
	 * @param accountId The unique identifier for the account.
	 * @param legionId The unique identifier for the legion.
	 * @return {@code true} if the update was successful, otherwise {@code false}.
	 */
	private boolean updateItems(Connection con, Collection<Item> items, Integer playerId, Integer accountId, Integer legionId)
	{
		if (GenericValidator.isBlankOrNull(items))
		{
			return true;
		}
		
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(UPDATE_QUERY);
			
			for (Item item : items)
			{
				stmt.setLong(1, item.getItemCount());
				stmt.setInt(2, item.getItemColor());
				stmt.setInt(3, item.getColorExpireTime());
				stmt.setString(4, item.getItemCreator());
				stmt.setInt(5, item.getExpireTime());
				stmt.setInt(6, item.getActivationCount());
				stmt.setInt(7, getItemOwnerId(item, playerId, accountId, legionId));
				stmt.setBoolean(8, item.isEquipped());
				stmt.setInt(9, item.isSoulBound() ? 1 : 0);
				stmt.setLong(10, item.getEquipmentSlot());
				stmt.setInt(11, item.getItemLocation());
				stmt.setInt(12, item.getItemTemplate().getMaxAuthorize() > 0 ? 0 : item.getEnchantOrAuthorizeLevel());
				stmt.setInt(13, item.getItemSkinTemplate().getTemplateId());
				stmt.setInt(14, item.getFusionedItemId());
				stmt.setInt(15, item.getOptionalSocket());
				stmt.setInt(16, item.getOptionalFusionSocket());
				stmt.setInt(17, item.getChargePoints());
				stmt.setInt(18, item.getBonusNumber());
				stmt.setInt(19, item.getRandomCount());
				stmt.setInt(20, item.getPackCount());
				stmt.setInt(21, item.getItemTemplate().getMaxAuthorize() > 0 ? item.getEnchantOrAuthorizeLevel() : 0);
				stmt.setBoolean(22, item.isPacked());
				stmt.setBoolean(23, item.isAmplified());
				stmt.setInt(24, item.getAmplificationSkill());
				stmt.setInt(25, item.getReductionLevel());
				stmt.setBoolean(26, item.isLunaReskin());
				stmt.setBoolean(27, item.isEnhance());
				stmt.setInt(28, item.getEnhanceSkillId());
				stmt.setInt(29, item.getEnhanceEnchantLevel());
				stmt.setInt(30, item.getUnSeal());
				stmt.setInt(31, item.getItemSkinSkill());
				stmt.setInt(32, item.getGrindSocket());
				stmt.setInt(33, item.getGrindColor());
				stmt.setLong(34, item.getGrindStone());
				stmt.setLong(35, item.getGrindSlot());
				stmt.setBoolean(36, item.isContaminated());
				stmt.setInt(37, item.getObjectId());
				stmt.addBatch();
			}
			
			stmt.executeBatch();
			con.commit();
		}
		catch (Exception e)
		{
			log.error("Failed to execute update batch", e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(stmt);
		}
		
		return true;
	}
	
	/**
	 * Deletes a collection of {@link Item} objects from the database.<br>
	 * This method uses a batch process to remove items by their unique object IDs.<br>
	 * It returns {@code true} if the operation succeeds or if the input is empty.
	 * @param con The active {@link Connection} used to execute the SQL query.
	 * @param items The collection of {@link Item} objects to be removed from the database.
	 * @return {@code true} if the deletion was successful, otherwise {@code false}.
	 */
	private boolean deleteItems(Connection con, Collection<Item> items)
	{
		if (GenericValidator.isBlankOrNull(items))
		{
			return true;
		}
		
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(DELETE_QUERY);
			for (Item item : items)
			{
				stmt.setInt(1, item.getObjectId());
				stmt.addBatch();
			}
			
			stmt.executeBatch();
			con.commit();
		}
		catch (Exception e)
		{
			log.error("Failed to execute delete batch", e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(stmt);
		}
		
		return true;
	}
	
	/**
	 * Deletes all items belonging to a specific player from the database.<br>
	 * This method uses {@code DELETE_CLEAN_QUERY} to clear the inventory.
	 * @param playerId The unique identifier of the player whose items will be removed.
	 * @return {@code true} if the deletion was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean deletePlayerItems(int playerId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_CLEAN_QUERY);
			stmt.setInt(1, playerId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error Player all items. PlayerObjId: " + playerId, e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Deletes all warehouse items associated with a specific account.<br>
	 * This method uses the {@code DELETE_ACCOUNT_WH} query to clear data.<br>
	 * It handles database connection management and logs any errors that occur.
	 * @param accountId The unique identifier of the account to clear.
	 */
	@Override
	public void deleteAccountWH(int accountId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_ACCOUNT_WH);
			stmt.setInt(1, accountId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error deleting all items from account WH. AccountId: " + accountId, e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Retrieves all unique identifiers from the {@code inventory} table.<br>
	 * This method queries the database to collect every {@code item_unique_id}.<br>
	 * If an error occurs, it returns an empty array.
	 * @return An array of integers containing the item unique IDs.
	 */
	@Override
	public int[] getUsedIDs()
	{
		final PreparedStatement statement = DB.prepareStatement("SELECT item_unique_id FROM inventory", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
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
				ids[i] = rs.getInt("item_unique_id");
			}
			
			return ids;
		}
		catch (SQLException e)
		{
			log.error("Can't get list of id's from inventory table", e);
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
