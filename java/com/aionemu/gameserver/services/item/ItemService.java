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
package com.aionemu.gameserver.services.item;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemSkillEnhance;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.RndArray;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;

/**
 * This service handles all core logic related to {@link Item} management within the game world.<br>
 * It manages item creation, movement, and interactions between {@link Player} objects and {@link Storage}.<br>
 * Use this class to perform high-level operations such as adding items to inventories or processing equipment.
 * @author KID
 * @rework Blackfire
 */
public class ItemService
{
	private static final Logger log = LoggerFactory.getLogger("ITEM_LOG");
	public static final ItemUpdatePredicate DEFAULT_UPDATE_PREDICATE = new ItemUpdatePredicate(ItemAddType.ITEM_COLLECT, ItemUpdateType.INC_ITEM_COLLECT);
	
	/**
	 * Loads the stone data for a specific list of items.<br>
	 * This method uses {@link ItemStoneListDAO} to fetch information from the database.<br>
	 * It only executes if the provided {@code itemList} is not {@code null} and contains elements.
	 * @param itemList The collection of {@link Item} objects to load stones for.
	 */
	public static void loadItemStones(Collection<Item> itemList)
	{
		if ((itemList != null) && (itemList.size() > 0))
		{
			DAOManager.getDAO(ItemStoneListDAO.class).load(itemList);
		}
	}
	
	/**
	 * Adds a specific amount of an item to a {@link Player}.<br>
	 * This method handles the creation and placement of the item in the inventory.
	 * @param player The {@link Player} who will receive the item.
	 * @param itemId The unique identifier for the item type.
	 * @param count The number of items to add.
	 * @return The unique ID of the newly created item.
	 */
	public static long addItem(Player player, int itemId, long count)
	{
		return addItem(player, itemId, count, DEFAULT_UPDATE_PREDICATE);
	}
	
	/**
	 * Adds a specific quantity of an item to a {@link Player}.<br>
	 * This method uses a {@code ItemUpdatePredicate} to determine how the item is handled.<br>
	 * It returns the unique ID of the newly created item.
	 * @param player The {@link Player} receiving the item.
	 * @param itemId The unique identifier for the item type.
	 * @param count The amount of the item to add.
	 * @param predicate A condition used to update or modify the item during addition.
	 * @return The new item ID, or a value indicating failure if the operation fails.
	 */
	public static long addItem(Player player, int itemId, long count, ItemUpdatePredicate predicate)
	{
		return addItem(player, itemId, count, null, predicate);
	}
	
	/**
	 * Adds an {@link Item} to a {@link Player} based on the provided source item.<br>
	 * This method copies the properties from the {@code sourceItem}.
	 * @param player The {@link Player} who will receive the item.
	 * @param sourceItem The {@link Item} used as the template for the new item.
	 * @return The unique ID of the newly created item.
	 */
	public static long addItem(Player player, Item sourceItem)
	{
		return addItem(player, sourceItem.getItemId(), sourceItem.getItemCount(), sourceItem, DEFAULT_UPDATE_PREDICATE);
	}
	
	/**
	 * Adds an item to a {@link Player} based on the properties of a provided {@code Item}.<br>
	 * This method uses the count and ID from the {@code sourceItem} to create the new entry.<br>
	 * It also applies any specific logic defined in the {@code predicate}.
	 * @param player The {@link Player} who will receive the item.
	 * @param sourceItem The {@code Item} used as a template for the addition.
	 * @param predicate A {@code ItemUpdatePredicate} to determine how the item is updated.
	 * @return The unique ID of the newly created item.
	 */
	public static long addItem(Player player, Item sourceItem, ItemUpdatePredicate predicate)
	{
		return addItem(player, sourceItem.getItemId(), sourceItem.getItemCount(), sourceItem, predicate);
	}
	
	/**
	 * Adds a specific amount of an item to a {@link Player}.<br>
	 * This method uses the provided {@code sourceItem} as the origin for the new item.<br>
	 * It handles stackable and non-stackable items automatically.
	 * @param player The {@link Player} who will receive the item.
	 * @param itemId The unique identifier of the item to add.
	 * @param count The quantity of the item to give.
	 * @param sourceItem The {@link Item} object used as the source for the new item.
	 * @return The unique ID of the newly created item.
	 */
	public static long addItem(Player player, int itemId, long count, Item sourceItem)
	{
		return addItem(player, itemId, count, sourceItem, DEFAULT_UPDATE_PREDICATE);
	}
	
	/**
	 * Adds a specific quantity of an item to a player's inventory.<br>
	 * This method handles both stackable and non-stackable items based on the template.<br>
	 * It also updates achievements and checks for inventory space.
	 * @param player The {@link Player} receiving the item.
	 * @param itemId The unique identifier of the item to add.
	 * @param count The amount of the item to give to the player.
	 * @param sourceItem The {@link Item} being consumed or used as a source for this addition.
	 * @param predicate A {@code ItemUpdatePredicate} to determine how the item should be updated.
	 * @return The final count of items successfully added to the inventory.
	 */
	public static long addItem(Player player, int itemId, long count, Item sourceItem, ItemUpdatePredicate predicate)
	{
		final ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if ((count <= 0) || (itemTemplate == null))
		{
			return 0;
		}
		
		Objects.requireNonNull(itemTemplate, "No item with id " + itemId);
		Objects.requireNonNull(predicate, "Predicate is not supplied");
		
		if (LoggingConfig.LOG_ITEM)
		{
			log.info("[ITEM] ID/Count" + (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "/Item Name - " + itemTemplate.getTemplateId() + "/" + count + "/" + itemTemplate.getName() : " - " + itemTemplate.getTemplateId() + "/" + count) + " to player " + player.getName());
		}
		
		AchievementService.getInstance().onUpdateAchievementAction(player, itemId, (int) count, AchievementActionType.COLLECT_ITEM);
		final Storage inventory = player.getInventory();
		if (itemTemplate.isKinah())
		{
			// quests do not add here
			inventory.increaseKinah(count);
			return 0;
		}
		
		if (itemTemplate.isStackable())
		{
			count = addStackableItem(player, itemTemplate, count, predicate);
		}
		else
		{
			count = addNonStackableItem(player, itemTemplate, count, sourceItem, predicate);
		}
		
		if (inventory.isFull(itemTemplate.getExtraInventoryId()) && (count > 0))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_INVEN_ERROR);
		}
		
		return count;
	}
	
	/**
	 * Adds non-stackable items to a {@link Player} inventory.<br>
	 * This method handles item creation and property copying from a source.<br>
	 * It continues adding until the inventory is full or the count reaches 0.
	 * @param player The {@link Player} receiving the items.
	 * @param itemTemplate The {@link ItemTemplate} defining the new item properties.
	 * @param count The total number of items to add.
	 * @param sourceItem The {@link Item} used as a template for copying information, or {@code null}.
	 * @param predicate The {@code ItemUpdatePredicate} used to modify and determine the addition type.
	 * @return The remaining count of items that could not be added.
	 */
	private static long addNonStackableItem(Player player, ItemTemplate itemTemplate, long count, Item sourceItem, ItemUpdatePredicate predicate)
	{
		final Storage inventory = player.getInventory();
		final ItemSkillEnhance skillEnhance = DataManager.ITEM_SKILL_ENHANCE_DATA.getSkillEnhance(itemTemplate.getSkillEnhance());
		while (!inventory.isFull(itemTemplate.getExtraInventoryId()) && (count > 0))
		{
			final Item newItem = ItemFactory.newItem(itemTemplate.getTemplateId());
			
			if (newItem.getExpireTime() != 0)
			{
				ExpireTimerTask.getInstance().addTask(newItem, player);
			}
			
			if (sourceItem != null)
			{
				copyItemInfo(sourceItem, newItem);
			}
			
			if (itemTemplate.getSkillEnhance() != 0)
			{
				newItem.setEnhanceSkillId(RndArray.get(skillEnhance.getSkillId()));
				newItem.setEnhanceEnchantLevel(1);
				newItem.setIsEnhance(true);
			}
			
			predicate.changeItem(newItem);
			inventory.add(newItem, predicate.getAddType());
			count--;
		}
		
		return count;
	}
	
	/**
	 * Copies specific properties from a source item to a new item.<br>
	 * This includes sockets, stones, enchant levels, and visual attributes.<br>
	 * It ensures the {@code newItem} matches the state of the {@code sourceItem}.
	 * @param sourceItem The original {@link Item} to copy data from.
	 * @param newItem The target {@link Item} that will receive the copied data.
	 */
	private static void copyItemInfo(Item sourceItem, Item newItem)
	{
		newItem.setOptionalSocket(sourceItem.getOptionalSocket());
		newItem.setItemCreator(sourceItem.getItemCreator());
		if (sourceItem.hasManaStones())
		{
			for (ManaStone manaStone : sourceItem.getItemStones())
			{
				ItemSocketService.addManaStone(newItem, manaStone.getItemId());
			}
		}
		
		if (sourceItem.getGodStone() != null)
		{
			newItem.addGodStone(sourceItem.getGodStone().getItemId());
		}
		
		if (sourceItem.getEnchantOrAuthorizeLevel() > 0)
		{
			newItem.setEnchantOrAuthorizeLevel(sourceItem.getEnchantOrAuthorizeLevel());
		}
		
		if (sourceItem.isSoulBound())
		{
			newItem.setSoulBound(true);
		}
		
		newItem.setBonusNumber(sourceItem.getBonusNumber());
		newItem.setRandomStats(sourceItem.getRandomStats());
		newItem.setRandomCount(sourceItem.getRandomCount());
		newItem.setIdianStone(sourceItem.getIdianStone());
		newItem.setItemColor(sourceItem.getItemColor());
		newItem.setItemSkinTemplate(sourceItem.getItemSkinTemplate());
		newItem.setIsEnhance(sourceItem.isEnhance());
	}
	
	/**
	 * Adds a stackable item to the player's inventory or equipment.<br>
	 * This method first attempts to increase the count of existing items in the inventory.<br>
	 * If the item is a shard, it also checks for equipped shards.<br>
	 * Any remaining count is added as new items if space is available.
	 * @param player The {@link Player} receiving the item.
	 * @param itemTemplate The {@link ItemTemplate} defining the item properties.
	 * @param count The amount of the item to add.
	 * @param predicate The {@code ItemUpdatePredicate} used to determine update types.
	 * @return The remaining count of items that could not be added.
	 */
	private static long addStackableItem(Player player, ItemTemplate itemTemplate, long count, ItemUpdatePredicate predicate)
	{
		final Storage inventory = player.getInventory();
		Collection<Item> items = inventory.getItemsByItemId(itemTemplate.getTemplateId());
		for (Item item : items)
		{
			if (count == 0)
			{
				break;
			}
			
			count = inventory.increaseItemCount(item, count, predicate.getUpdateType(item, true));
		}
		
		// If Power Shard's are Equiped and there are no Power Shard's in Inventory / or max Stack is reached (in Inventory) they get added to Equiped Power Shard's
		if (itemTemplate.getCategory() == ItemCategory.SHARD)
		{
			final Equipment equipment = player.getEquipment();
			items = equipment.getEquippedItemsByItemId(itemTemplate.getTemplateId());
			for (Item item : items)
			{
				if (count == 0)
				{
					break;
				}
				
				count = equipment.increaseEquippedItemCount(item, count);
			}
		}
		
		while (!inventory.isFull(itemTemplate.getExtraInventoryId()) && (count > 0))
		{
			final Item newItem = ItemFactory.newItem(itemTemplate.getTemplateId(), count);
			count -= newItem.getItemCount();
			inventory.add(newItem, predicate.getAddType());
		}
		
		return count;
	}
	
	/**
	 * Adds a list of quest items to the specified {@link Player}.<br>
	 * This method handles the logic for granting multiple {@code QuestItems} at once.
	 * @param player The {@link Player} who will receive the items.
	 * @param questItems The list of {@code QuestItems} to be added.
	 * @return {@code true} if all items were successfully added, otherwise {@code false}.
	 */
	public static boolean addQuestItems(Player player, List<QuestItems> questItems)
	{
		return addQuestItems(player, questItems, DEFAULT_UPDATE_PREDICATE);
	}
	
	/**
	 * Adds a list of quest items to the specified {@link Player}.<br>
	 * This method checks if there is enough space in the inventory before adding any items.<br>
	 * It returns {@code false} and sends a system message if the inventory is full.
	 * @param player The {@link Player} who will receive the items.
	 * @param questItems The list of {@link QuestItems} to be added.
	 * @param predicate A condition used to determine how the items should be updated.
	 * @return {@code true} if all items were successfully added, or {@code false} otherwise.
	 */
	public static boolean addQuestItems(Player player, List<QuestItems> questItems, ItemUpdatePredicate predicate)
	{
		int slotReq = 0, specialSlot = 0;
		
		for (QuestItems qi : questItems)
		{
			if ((qi.getItemId() != ItemId.KINAH.value()) && (qi.getCount() != 0))
			{
				final ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(qi.getItemId());
				final long stackCount = template.getMaxStackCount();
				long count = qi.getCount() / stackCount;
				if ((qi.getCount() % stackCount) != 0)
				{
					count++;
				}
				
				if (template.getExtraInventoryId() > 0)
				{
					specialSlot += count;
				}
				else
				{
					slotReq += count;
				}
			}
		}
		
		final Storage inventory = player.getInventory();
		if (((slotReq > 0) && (inventory.getFreeSlots() < slotReq)) || ((specialSlot > 0) && (inventory.getSpecialCubeFreeSlots() < specialSlot)))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DECOMPRESS_INVENTORY_IS_FULL);
			return false;
		}
		
		for (QuestItems qi : questItems)
		{
			addItem(player, qi.getItemId(), qi.getCount(), predicate);
		}
		
		return true;
	}
	
	/**
	 * Releases the unique object ID of an item back to the system.<br>
	 * This method calls {@code releaseId} using the ID from the provided {@code Item}.
	 * @param item The {@code Item} object whose ID needs to be released.
	 */
	public static void releaseItemId(Item item)
	{
		IDFactory.getInstance().releaseId(item.getObjectId());
	}
	
	/**
	 * Releases the unique IDs for a collection of {@link Item} objects.<br>
	 * This method converts each item to its ID and calls {@code releaseIds}.
	 * @param items The collection of items to release.
	 */
	public static void releaseItemIds(Collection<Item> items)
	{
		final Collection<Integer> idIterator = items.stream().map(AionObject.OBJECT_TO_ID_TRANSFORMER).collect(Collectors.toList());
		IDFactory.getInstance().releaseIds(idIterator);
	}
	
	public static class ItemUpdatePredicate
	{
		private final ItemUpdateType itemUpdateType;
		private final ItemAddType itemAddType;
		
		public ItemUpdatePredicate(ItemAddType itemAddType, ItemUpdateType itemUpdateType)
		{
			this.itemUpdateType = itemUpdateType;
			this.itemAddType = itemAddType;
		}
		
		public ItemUpdatePredicate()
		{
			this(ItemAddType.ITEM_COLLECT, ItemUpdateType.INC_ITEM_COLLECT);
		}
		
		public ItemUpdateType getUpdateType(Item item, boolean isIncrease)
		{
			if (item.getItemTemplate().isKinah())
			{
				return ItemUpdateType.getKinahUpdateTypeFromAddType(itemAddType, isIncrease);
			}
			
			return itemUpdateType;
		}
		
		public ItemAddType getAddType()
		{
			return itemAddType;
		}
		
		/**
		 * @param item
		 * @return
		 */
		public boolean changeItem(Item item)
		{
			return true;
		}
	}
	
	/**
	 * Moves an item from the world into a player's inventory.<br>
	 * This method finds the player by their unique ID and attempts to add the specified item.
	 * @param playerObjectId The unique identifier of the player.
	 * @param itemId The unique identifier of the item to be added.
	 * @return {@code true} if the item was successfully added, or {@code false} otherwise.
	 */
	public static boolean dropItemToInventory(int playerObjectId, int itemId)
	{
		return dropItemToInventory(World.getInstance().findPlayer(playerObjectId), itemId);
	}
	
	/**
	 * Adds a single item to the inventory of a specific {@link Player}.<br>
	 * This method checks if the player is online and has enough space.<br>
	 * It also verifies if there is room in an existing stack for the item.
	 * @param player The {@link Player} who will receive the item.
	 * @param itemId The unique identifier of the item to add.
	 * @return {@code true} if the item was successfully added, otherwise {@code false}.
	 */
	public static boolean dropItemToInventory(Player player, int itemId)
	{
		if ((player == null) || !player.isOnline())
		{
			return false;
		}
		
		final Storage storage = player.getInventory();
		if (storage.getFreeSlots() < 1)
		{
			final List<Item> items = storage.getItemsByItemId(itemId);
			boolean hasFreeStack = false;
			for (Item item : items)
			{
				if ((item.getPersistentState() == PersistentState.DELETED) || (item.getItemCount() < item.getItemTemplate().getMaxStackCount()))
				{
					hasFreeStack = true;
					break;
				}
			}
			
			if (!hasFreeStack)
			{
				return false;
			}
		}
		
		// TODO: check the exact type in retail
		return addItem(player, itemId, 1, new ItemUpdatePredicate(ItemAddType.ITEM_COLLECT, ItemUpdateType.INC_CASH_ITEM)) == 0;
	}
	
	/**
	 * Checks if a specific item exists in the data manager.<br>
	 * It retrieves the {@code ItemTemplate} using the provided ID.<br>
	 * Returns {@code true} if the template is found and {@code false} otherwise.
	 * @param randomItemId The unique identifier of the item to check.
	 * @return {@code true} if the item exists, {@code false} if it does not.
	 */
	public static boolean checkRandomTemplate(int randomItemId)
	{
		final ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(randomItemId);
		return template != null;
	}
	
	/**
	 * Creates a new {@link Item} instance based on the provided template ID.<br>
	 * This method validates that the item exists and has a positive count.
	 * @param resultItemId The unique identifier for the item template.
	 * @param count The quantity of the item to create.
	 * @param object A generic object parameter used for additional context.
	 * @param unk An unknown integer parameter.
	 * @param unk1 Another unknown integer parameter.
	 * @param unk2 A third unknown integer parameter.
	 * @return The newly created {@link Item} object, or {@code null} if the item is invalid.
	 */
	public static Item newItem(int resultItemId, int count, Object object, int unk, int unk1, int unk2)
	{
		final ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(resultItemId);
		if ((count <= 0) || (itemTemplate == null))
		{
			return null;
		}
		
		Objects.requireNonNull(itemTemplate, "No item with id " + resultItemId);
		
		final Item newItem = ItemFactory.newItem(itemTemplate.getTemplateId());
		
		return newItem;
	}
	
	/**
	 * Upgrades an item and copies its properties to a new instance.<br>
	 * This method transfers sockets, mana stones, god stones, and soulbound status.<br>
	 * It also handles enchantment levels and amplification skills based on the source item.<br>
	 * Finally, it adds the resulting {@code newItem} to the player's inventory.
	 * @param player The {@link Player} who owns the items.
	 * @param sourceItem The original {@link Item} being used for the upgrade.
	 * @param newItem The new {@link Item} instance created from the upgrade.
	 */
	public static void makeUpgradeItem(Player player, Item sourceItem, Item newItem)
	{
		final Storage inventory = player.getInventory();
		newItem.setOptionalSocket(sourceItem.getOptionalSocket());
		final int enchantLevel = sourceItem.getEnchantOrAuthorizeLevel();
		
		if (sourceItem.getFusionedItemId() != 0)
		{
			newItem.setFusionedItem(sourceItem.getFusionedItemTemplate());
		}
		
		if (sourceItem.hasManaStones())
		{
			ItemSocketService.copyManaStones(sourceItem, newItem);
		}
		
		if (sourceItem.hasGodStone())
		{
			newItem.addGodStone(sourceItem.getGodStone().getItemId());
		}
		
		if (sourceItem.getItemTemplate().isPlume())
		{
			newItem.setEnchantOrAuthorizeLevel(1);
			newItem.setEnchantOrAuthorizeLevel(0);
		}
		else
		{
			if (enchantLevel >= 20)
			{
				newItem.setEnchantOrAuthorizeLevel(enchantLevel - 5);
				newItem.setAmplificationSkill(sourceItem.getAmplificationSkill());
				newItem.setAmplified(true);
			}
			else
			{
				newItem.setEnchantOrAuthorizeLevel(enchantLevel);
			}
		}
		
		if (sourceItem.isSoulBound())
		{
			newItem.setSoulBound(true);
		}
		
		if (sourceItem.getBonusNumber() > 0)
		{
			newItem.setBonusNumber(sourceItem.getBonusNumber());
			newItem.setRandomStats(sourceItem.getRandomStats());
			newItem.setRandomCount(sourceItem.getRandomCount());
		}
		
		final ItemUpdatePredicate predicate = DEFAULT_UPDATE_PREDICATE;
		predicate.changeItem(newItem);
		inventory.add(newItem, predicate.getAddType());
	}
}
