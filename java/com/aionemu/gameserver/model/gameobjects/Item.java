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
package com.aionemu.gameserver.model.gameobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dao.RealItemRndBonusDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ChargeInfo;
import com.aionemu.gameserver.model.items.GodStone;
import com.aionemu.gameserver.model.items.IdianStone;
import com.aionemu.gameserver.model.items.ItemMask;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.OdianStone;
import com.aionemu.gameserver.model.items.RandomBonusResult;
import com.aionemu.gameserver.model.items.RandomStats;
import com.aionemu.gameserver.model.items.RealRandomBonus;
import com.aionemu.gameserver.model.items.RuneStone;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.ItemStorage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.item.EquipType;
import com.aionemu.gameserver.model.templates.item.Improvement;
import com.aionemu.gameserver.model.templates.item.ItemCustomSetTemplate;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.actions.DyeAction;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.model.templates.item.bonuses.StatBonusType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Represents a general game item within the world.<br>
 * This class handles core properties such as stats, expiration, and storage for all {@link ItemTemplate} instances.
 * @author ATracer, Wakizashi, xTz
 * @author GiGatR00n
 * @reworked Blackfire
 */
public class Item extends AionObject implements IExpirable, StatOwner
{
	private final Logger log = LoggerFactory.getLogger(Item.class);
	private long itemCount = 1;
	private int itemColor = 0;
	private int colorExpireTime = 0;
	private String itemCreator;
	private final ItemTemplate itemTemplate;
	private ItemTemplate itemSkinTemplate;
	private ItemTemplate fusionedItemTemplate;
	private boolean isEquipped = false;
	private long equipmentSlot = ItemStorage.FIRST_AVAILABLE_SLOT;
	private PersistentState persistentState;
	private Set<ManaStone> manaStones;
	private Set<ManaStone> fusionStones;
	private int optionalSocket;
	private int optionalFusionSocket;
	private GodStone godStone;
	private IdianStone idianStone;
	private boolean isSoulBound = false;
	private int itemLocation;
	private int enchantLevel;
	private int expireTime = 0;
	private int temporaryExchangeTime = 0;
	private long repurchasePrice;
	private int activationCount = 0;
	private ChargeInfo conditioningInfo;
	private int bonusNumber = 0;
	private List<StatFunction> currentModifiers;
	private RandomStats randomStats;
	private RealRandomBonus realRndBonus;
	private int rndCount;
	public static int MAX_BASIC_STONES = 6;
	private int packCount;
	private int authorize;
	private boolean isPacked = false;
	private boolean isAmplified = false;
	private int amplificationSkill = 0;
	private int ReductionLevel = 0;
	private boolean luna_reskin = false;
	private boolean canEnhance = false;
	private int enhanceSkillId;
	private int enhanceEnchantLevel;
	private int unSeal = 0;
	private int SkinSkill = 0;
	private OdianStone odianStone;
	private RuneStone runeStone;
	private int grindSocket;
	private int grindColor;
	private long grindStone;
	private int grindSlot;
	private Item grind;
	private boolean contaminated = false;
	
	/**
	 * Creates a new {@link Item} instance using a template.<br>
	 * This constructor initializes the item properties based on the provided {@code itemTemplate}.<br>
	 * It also handles default values for sockets, skills, and custom sets.
	 * @param objId The unique identifier for this specific object.
	 * @param itemTemplate The {@link ItemTemplate} used to define the item's base properties.
	 */
	public Item(int objId, ItemTemplate itemTemplate)
	{
		super(objId);
		this.itemTemplate = itemTemplate;
		activationCount = itemTemplate.getActivationCount();
		if (itemTemplate.getExpireTime() != 0)
		{
			expireTime = ((int) (System.currentTimeMillis() / 1000) + (itemTemplate.getExpireTime() * 60)) - 1;
		}
		
		final int optionSlotBonus = itemTemplate.getOptionSlotBonus();
		if (optionSlotBonus != 0)
		{
			optionalSocket = -1;
		}
		
		if (this.itemTemplate.getSkinSkill() != 0)
		{
			SkinSkill = itemTemplate.getSkinSkill();
		}
		
		optionalSocket = 0;
		grindSocket = 0;
		grindColor = 0;
		
		// add custom item set
		final ItemCustomSetTemplate itemCustomSetTemplate = DataManager.ITEM_CUSTOM_SET_DATA.getItemCustomSetTemplate(itemTemplate.getTemplateId());
		if (itemCustomSetTemplate != null)
		{
			enchantLevel = itemCustomSetTemplate.getEnchant_level();
			if ((itemCustomSetTemplate.getMana_stone() != null) && (itemCustomSetTemplate.getMana_stone().size() > 0))
			{
				for (int i = 0; i < itemCustomSetTemplate.getMana_stone().size(); i++)
				{
					ItemSocketService.addManaStone(this, itemCustomSetTemplate.getMana_stone().get(i), i);
				}
			}
		}
		
		persistentState = PersistentState.NEW;
		updateChargeInfo(0);
	}
	
	/**
	 * Creates a new {@link Item} instance with specific quantity and equipment details.<br>
	 * This constructor initializes the item using an existing template.
	 * @param objId The unique identifier for the object.
	 * @param itemTemplate The {@link ItemTemplate} defining the base properties of the item.
	 * @param itemCount The total number of items in this stack.
	 * @param isEquipped Set to {@code true} if the item is currently worn by a character, otherwise {@code false}.
	 * @param equipmentSlot The specific slot index where the item is equipped.
	 */
	public Item(int objId, ItemTemplate itemTemplate, long itemCount, boolean isEquipped, long equipmentSlot)
	{
		this(objId, itemTemplate);
		this.itemCount = itemCount;
		this.isEquipped = isEquipped;
		this.equipmentSlot = equipmentSlot;
	}
	
	/**
	 * Constructs a new {@link Item} instance with all specific properties.<br>
	 * This constructor initializes the item data from the template and sets various attributes like enchantments, sockets, and bonuses.<br>
	 * It also handles special logic for fused items and random stats.
	 * @param objId The unique object identifier.
	 * @param itemId The unique item identifier used to fetch the template.
	 * @param itemCount The quantity of the item.
	 * @param itemColor The color value of the item.
	 * @param colorExpires The expiration time for the item color.
	 * @param itemCreator The name of the creator who made the item.
	 * @param expireTime The expiration time of the item.
	 * @param activationCount The number of times the item has been activated.
	 * @param isEquipped Whether the item is currently equipped.
	 * @param isSoulBound Whether the item is bound to a specific character.
	 * @param equipmentSlot The slot where the item is equipped.
	 * @param itemLocation The location of the item in the world or inventory.
	 * @param enchant
	 * @param itemSkin
	 * @param fusionedItem
	 * @param optionalSocket
	 * @param optionalFusionSocket
	 * @param charge
	 * @param randomBonus
	 * @param rndCount
	 * @param packCount
	 * @param authorize
	 * @param isPacked
	 * @param isAmplified
	 * @param amplificationSkill
	 * @param reductionLevel
	 * @param lunaReskin
	 * @param isEnhance
	 * @param enhanceSkillId
	 * @param enhanceEnchantLevel
	 * @param unSeal
	 * @param SkinSkill
	 * @param grindSocket
	 * @param grindColor
	 * @param grindStone
	 * @param grindSlot
	 * @param contaminated
	 */
	public Item(int objId, int itemId, long itemCount, int itemColor, int colorExpires, String itemCreator, int expireTime, int activationCount, boolean isEquipped, boolean isSoulBound, long equipmentSlot, int itemLocation, int enchant, int itemSkin, int fusionedItem, int optionalSocket, int optionalFusionSocket, int charge, int randomBonus, int rndCount, int packCount, int authorize, boolean isPacked, boolean isAmplified, int amplificationSkill, int reductionLevel, boolean lunaReskin, boolean isEnhance, int enhanceSkillId, int enhanceEnchantLevel, int unSeal, int SkinSkill, int grindSocket, int grindColor, long grindStone, int grindSlot, boolean contaminated)
	{
		super(objId);
		
		itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		this.itemCount = itemCount;
		this.itemColor = itemColor;
		colorExpireTime = colorExpires;
		this.itemCreator = itemCreator;
		this.expireTime = expireTime;
		this.activationCount = activationCount;
		this.isEquipped = isEquipped;
		this.isSoulBound = isSoulBound;
		this.equipmentSlot = equipmentSlot;
		this.itemLocation = itemLocation;
		enchantLevel = enchant;
		fusionedItemTemplate = DataManager.ITEM_DATA.getItemTemplate(fusionedItem);
		itemSkinTemplate = DataManager.ITEM_DATA.getItemTemplate(itemSkin);
		this.optionalSocket = optionalSocket;
		this.optionalFusionSocket = optionalFusionSocket;
		bonusNumber = randomBonus;
		this.rndCount = rndCount;
		this.packCount = packCount;
		this.authorize = authorize;
		this.isPacked = isPacked;
		this.isAmplified = isAmplified;
		this.amplificationSkill = amplificationSkill;
		if ((itemTemplate.getRandomBonusId() != 0) && (bonusNumber > 0))
		{
			randomStats = new RandomStats(itemTemplate.getRandomBonusId(), bonusNumber);
		}
		
		if (fusionedItemTemplate != null)
		{
			if (!itemTemplate.isCanFuse() || !itemTemplate.isTwoHandWeapon() || !fusionedItemTemplate.isCanFuse() || !fusionedItemTemplate.isTwoHandWeapon())
			{
				fusionedItemTemplate = null;
				this.optionalFusionSocket = 0;
			}
		}
		
		ReductionLevel = reductionLevel;
		luna_reskin = lunaReskin;
		canEnhance = isEnhance;
		this.enhanceSkillId = enhanceSkillId;
		this.enhanceEnchantLevel = enhanceEnchantLevel;
		this.SkinSkill = SkinSkill;
		this.grindSocket = grindSocket;
		this.grindColor = grindColor;
		this.grindStone = grindStone;
		this.grindSlot = grindSlot;
		this.contaminated = contaminated;
		DAOManager.getDAO(RealItemRndBonusDAO.class).loadRandomBonuses(this);
		updateChargeInfo(charge);
	}
	
	/**
	 * Assigns a random bonus to the item based on its template.<br>
	 * This method checks if the item has a valid random bonus ID.<br>
	 * It then retrieves and applies the corresponding stats from {@link DataManager}.
	 * @return {@code true} if the bonus was successfully applied, {@code false} otherwise.
	 */
	public boolean setRndBonus()
	{
		final int setId = itemTemplate.getRandomBonusId();
		if (setId > 0)
		{
			final RandomBonusResult bonus = DataManager.ITEM_RANDOM_BONUSES.getRandomModifiers(StatBonusType.INVENTORY, setId);
			if (bonus != null)
			{
				bonusNumber = bonus.getTemplateNumber();
				randomStats = new RandomStats(itemTemplate.getRandomBonusId(), bonusNumber);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Updates the {@code conditioningInfo} based on the provided charge level.<br>
	 * It creates a new {@link ChargeInfo} if it is currently {@code null}.<br>
	 * It sets {@code conditioningInfo} to {@code null} if the max charge level is 0.
	 * @param charge The charge value to be used for the item.
	 */
	private void updateChargeInfo(int charge)
	{
		final int chargeLevel = getChargeLevelMax();
		if ((conditioningInfo == null) && (chargeLevel > 0))
		{
			conditioningInfo = new ChargeInfo(charge, this);
		}
		
		// when break fusioned item and second item has conditioned info - set to null
		if ((conditioningInfo != null) && (chargeLevel == 0))
		{
			conditioningInfo = null;
		}
	}
	
	/**
	 * Checks if the item is a tuneable object.<br>
	 * This method returns {@code true} if the optional socket value is {@code -1}.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if the item has a tune, {@code false} otherwise.
	 */
	public boolean hasTune()
	{
		if (getOptionalSocket() == -1)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the name of the item.<br>
	 * This method returns the {@code String} name from the associated {@code ItemTemplate}.
	 * @return The name of the item as a {@code String}.
	 */
	@Override
	public String getName()
	{
		// TODO: item description should return probably string and not id
		return String.valueOf(itemTemplate.getName());
	}
	
	/**
	 * Retrieves the name of the person who created the item.<br>
	 * If no creator is defined, it returns an empty {@code String}.
	 * @return The name of the item creator or an empty string.
	 */
	public String getItemCreator()
	{
		if (itemCreator == null)
		{
			return "";
		}
		
		return itemCreator;
	}
	
	/**
	 * Sets the name of the person who created the item.<br>
	 * This updates the {@code itemCreator} field in this {@link BrokerItem} instance.
	 * @param itemCreator The name of the creator as a {@code String}.
	 */
	public void setItemCreator(String itemCreator)
	{
		this.itemCreator = itemCreator;
	}
	
	/**
	 * Retrieves the display name of the item.<br>
	 * This method gets the name from the associated {@code ItemTemplate}.
	 * @return The name of the item as a {@code String}.
	 */
	public String getItemName()
	{
		return itemTemplate.getName();
	}
	
	/**
	 * Retrieves the value of the optional socket.<br>
	 * This method returns the {@code int} value stored in the {@code optionalSocket} field.
	 * @return The current value of the optional socket.
	 */
	public int getOptionalSocket()
	{
		return optionalSocket;
	}
	
	/**
	 * Sets the {@code optionalSocket} value for this item.<br>
	 * This updates the internal state of the object.
	 * @param optionalSocket The new integer value to set for the optional socket.
	 */
	public void setOptionalSocket(int optionalSocket)
	{
		this.optionalSocket = optionalSocket;
	}
	
	/**
	 * Checks if the item has an optional socket.<br>
	 * It returns {@code true} if the {@code optionalSocket} value is not {@code 0}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if an optional socket exists, {@code false} otherwise.
	 */
	public boolean hasOptionalSocket()
	{
		return optionalSocket != 0;
	}
	
	/**
	 * Retrieves the value of the optional fusion socket.<br>
	 * This value is stored within the {@code Item} object.
	 * @return The integer value of the optional fusion socket.
	 */
	public int getOptionalFusionSocket()
	{
		return optionalFusionSocket;
	}
	
	/**
	 * Checks if the item has an optional fusion socket.<br>
	 * This method returns {@code true} if the {@code optionalFusionSocket} value is not {@code 0}.
	 * @return {@code true} if a socket exists, {@code false} otherwise.
	 */
	public boolean hasOptionalFusionSocket()
	{
		return optionalFusionSocket != 0;
	}
	
	/**
	 * Sets the {@code optionalFusionSocket} value for this item.<br>
	 * This updates the internal state of the object with the provided integer.
	 * @param optionalFusionSocket The new socket value to assign.
	 */
	public void setOptionalFusionSocket(int optionalFusionSocket)
	{
		this.optionalFusionSocket = optionalFusionSocket;
	}
	
	/**
	 * Retrieves the {@link ItemTemplate} for this drop.<br>
	 * It returns the cached {@code template} if it exists.<br>
	 * If the {@code template} is {@code null}, it fetches the data from {@link DataManager}.
	 * @return The {@link ItemTemplate} associated with this drop.
	 */
	public ItemTemplate getItemTemplate()
	{
		return itemTemplate;
	}
	
	/**
	 * Retrieves the skin template for this item.<br>
	 * If no specific skin is applied, it returns the base {@code ItemTemplate}.
	 * @return The {@code ItemTemplate} used for the item's appearance.
	 */
	public ItemTemplate getItemSkinTemplate()
	{
		if (itemSkinTemplate == null)
		{
			return itemTemplate;
		}
		
		return itemSkinTemplate;
	}
	
	/**
	 * Updates the skin template for this item.<br>
	 * This method sets the {@code itemSkinTemplate} to a new value.<br>
	 * It also marks the persistent state as {@code UPDATE_REQUIRED}.
	 * @param newTemplate The new {@link ItemTemplate} to apply.
	 */
	public void setItemSkinTemplate(ItemTemplate newTemplate)
	{
		itemSkinTemplate = newTemplate;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Checks if the current item is a skinned version of another item.<br>
	 * It compares the current {@code itemTemplate} with its skin template.
	 * @return {@code true} if the item is skinned, otherwise {@code false}.
	 */
	public boolean isSkinnedItem()
	{
		return getItemSkinTemplate() != itemTemplate;
	}
	
	/**
	 * Retrieves the current color of the item.<br>
	 * This method checks if a {@code DyeAction} is applied to the item.<br>
	 * If a dye exists, it returns that color.<br>
	 * Otherwise, it returns the default {@code itemColor}.
	 * @return The integer value representing the current item color.
	 */
	public int getItemColor()
	{
		final DyeAction dyeAction = getDyeAction();
		return dyeAction != null ? dyeAction.getColor() : itemColor;
	}
	
	/**
	 * Retrieves the {@code DyeAction} associated with the current item color.<br>
	 * This method checks if the {@code itemColor} is valid and exists in the data manager.<br>
	 * It returns {@code null} if the color is invalid or no actions are defined.
	 * @return The {@code DyeAction} from the dye template, or {@code null} if not found.
	 */
	private DyeAction getDyeAction()
	{
		if (itemColor < 0)
		{
			return null;
		}
		
		final ItemTemplate dyeTemplate = DataManager.ITEM_DATA.getItemTemplate(itemColor);
		if (dyeTemplate == null)
		{
			return null;
		}
		
		final ItemActions actions = dyeTemplate.getActions();
		if (actions == null)
		{
			return null;
		}
		
		return actions.getDyeAction();
	}
	
	/**
	 * Updates the color of the item.<br>
	 * This method sets the {@code itemColor} field to a new value.<br>
	 * It also marks the object as requiring a persistent state update.
	 * @param coloringItemId The new ID for the item color.
	 */
	public void setItemColor(int coloringItemId)
	{
		itemColor = coloringItemId;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Calculates the remaining time for the item color effect.<br>
	 * It returns the difference between the expiration time and the current system time.<br>
	 * If no expiration is set, it returns {@code 0}.
	 * @return The number of seconds remaining before the color expires.
	 */
	public int getColorTimeLeft()
	{
		if (colorExpireTime == 0)
		{
			return 0;
		}
		
		return (int) (colorExpireTime - (System.currentTimeMillis() / 1000));
	}
	
	/**
	 * Retrieves the expiration time for the item's color.<br>
	 * This value is used to determine when a specific visual effect expires.
	 * @return The {@code int} value representing the color expiration time.
	 */
	public int getColorExpireTime()
	{
		return colorExpireTime;
	}
	
	/**
	 * Sets the remaining time for a color effect.<br>
	 * This method updates the {@code colorExpireTime} field.<br>
	 * It also marks the object state as requiring an update.
	 * @param dyeRemainsUntil The amount of time left until the color expires.
	 */
	public void setColorExpireTime(int dyeRemainsUntil)
	{
		colorExpireTime = dyeRemainsUntil;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the total number of items for this broker entry.<br>
	 * This value represents the quantity of the {@link Item}.
	 * @return The current count of items as a {@code long}.
	 */
	public long getItemCount()
	{
		return itemCount;
	}
	
	/**
	 * Calculates the remaining capacity for this item stack.<br>
	 * It subtracts the current {@code itemCount} from the maximum allowed stack size.
	 * @return The number of items that can still be added to the stack.
	 */
	public long getFreeCount()
	{
		return itemTemplate.getMaxStackCount() - itemCount;
	}
	
	/**
	 * Updates the total number of items for this {@link BrokerItem}.<br>
	 * This method sets the internal {@code itemCount} field.
	 * @param itemCount The new quantity of items to set.
	 */
	public void setItemCount(long itemCount)
	{
		this.itemCount = itemCount;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Increases the current item count by a specified amount.<br>
	 * This method ensures the total does not exceed the maximum stack size defined in {@code itemTemplate}.<br>
	 * It updates the persistent state if any items are successfully added.
	 * @param count The amount to add to the current item count.
	 * @return The number of items that could not be added because the stack was full.
	 */
	public long increaseItemCount(long count)
	{
		if (count <= 0)
		{
			return 0;
		}
		
		final long cap = itemTemplate.getMaxStackCount();
		final long addCount = (itemCount + count) > cap ? cap - itemCount : count;
		if (addCount != 0)
		{
			itemCount += addCount;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
		
		return count - addCount;
	}
	
	/**
	 * Reduces the current quantity of this item by a specified amount.<br>
	 * If the requested {@code count} exceeds the available amount, it removes all items.<br>
	 * Updates the persistent state based on whether any items remain.
	 * @param count The number of items to remove from the stack.
	 * @return The actual number of items removed from the stack.
	 */
	public long decreaseItemCount(long count)
	{
		if (count <= 0)
		{
			return 0;
		}
		
		final long removeCount = count >= itemCount ? itemCount : count;
		itemCount -= removeCount;
		if ((itemCount == 0) && !itemTemplate.isKinah())
		{
			setPersistentState(PersistentState.DELETED);
		}
		else
		{
			setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
		
		return count - removeCount;
	}
	
	/**
	 * Checks if the item is currently equipped.
	 * @return {@code true} if the item is equipped, {@code false} otherwise.
	 */
	public boolean isEquipped()
	{
		return isEquipped;
	}
	
	/**
	 * Updates the equipment status of this item.<br>
	 * Sets the {@code isEquipped} field to the provided value.<br>
	 * This method triggers a persistent state update to save the change.
	 * @param isEquipped The new equipment status to set. Use {@code true} if equipped and {@code false} otherwise.
	 */
	public void setEquipped(boolean isEquipped)
	{
		this.isEquipped = isEquipped;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the specific slot index for this piece of equipment.<br>
	 * This value identifies where the item is placed in the character's inventory.
	 * @return The {@code long} value representing the equipment slot.
	 */
	public long getEquipmentSlot()
	{
		return equipmentSlot;
	}
	
	/**
	 * Sets the specific slot for this piece of equipment.<br>
	 * This method updates the {@code equipmentSlot} field.<br>
	 * It also marks the object state as requiring an update to the database.
	 * @param equipmentSlot The unique identifier for the equipment slot.
	 */
	public void setEquipmentSlot(long equipmentSlot)
	{
		this.equipmentSlot = equipmentSlot;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the collection of {@link ManaStone} objects associated with this item.<br>
	 * If the internal collection is null, it initializes it using {@code itemStonesCollection}.
	 * @return A {@code Set} containing all {@link ManaStone} items.
	 */
	public Set<ManaStone> getItemStones()
	{
		if (manaStones == null)
		{
			manaStones = itemStonesCollection();
		}
		
		return manaStones;
	}
	
	/**
	 * Retrieves the collection of {@link ManaStone} objects used for fusion.<br>
	 * If the internal collection is null, it initializes it using {@code itemStonesCollection}.
	 * @return A {@code Set} containing all {@link ManaStone} items.
	 */
	public Set<ManaStone> getFusionStones()
	{
		if (fusionStones == null)
		{
			fusionStones = itemStonesCollection();
		}
		
		return fusionStones;
	}
	
	/**
	 * Gets the total number of fusion stones.<br>
	 * Returns {@code 0} if no fusion stones exist.
	 * @return The size of the fusion stones collection.
	 */
	public int getFusionStonesSize()
	{
		if (fusionStones == null)
		{
			return 0;
		}
		
		return fusionStones.size();
	}
	
	/**
	 * Gets the total number of stones currently held by the item.<br>
	 * This method checks if the {@code manaStones} list is null before counting.
	 * @return The size of the stone collection or 0 if no stones exist.
	 */
	public int getItemStonesSize()
	{
		if (manaStones == null)
		{
			return 0;
		}
		
		return manaStones.size();
	}
	
	/**
	 * Retrieves the collection of {@link ManaStone} items.<br>
	 * The stones are sorted by their slot position.
	 * @return A {@code Set<ManaStone>} containing all mana stones.
	 */
	private Set<ManaStone> itemStonesCollection()
	{
		return new TreeSet<>((o1, o2) ->
		{
			if (o1.getSlot() == o2.getSlot())
			{
				return 0;
			}
			
			return o1.getSlot() > o2.getSlot() ? 1 : -1;
		});
	}
	
	/**
	 * Checks if the item contains any {@link ManaStone} objects.<br>
	 * Returns {@code true} if the list is not empty.<br>
	 * Returns {@code false} if the list is null or empty.
	 * @return {@code true} if mana stones exist, otherwise {@code false}.
	 */
	public boolean hasManaStones()
	{
		return (manaStones != null) && (manaStones.size() > 0);
	}
	
	/**
	 * Checks if the item contains any fusion stones.<br>
	 * It returns {@code true} if the {@code fusionStones} list is not {@code null} and is not empty.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if there are fusion stones present, {@code false} otherwise.
	 */
	public boolean hasFusionStones()
	{
		return (fusionStones != null) && (fusionStones.size() > 0);
	}
	
	/**
	 * Checks if the item is an {@link IdianStone}.<br>
	 * This method verifies that the {@code idianStone} field is not {@code null}.
	 * @return {@code true} if the item is an {@link IdianStone}, otherwise {@code false}.
	 */
	public boolean hasIdianStone()
	{
		return idianStone != null;
	}
	
	/**
	 * Checks if the item contains a {@link GodStone}.
	 * @return {@code true} if the {@code godStone} is not {@code null}, otherwise {@code false}.
	 */
	public boolean hasGodStone()
	{
		return godStone != null;
	}
	
	/**
	 * Retrieves the {@code GodStone} associated with this item.
	 * @return the {@code GodStone} object.
	 */
	public GodStone getGodStone()
	{
		return godStone;
	}
	
	/**
	 * Creates and adds a new {@link GodStone} to the object.<br>
	 * This method initializes the stone using the provided {@code itemId}.<br>
	 * It determines if the state is new or requires an update based on existing data.
	 * @param itemId The unique identifier for the item template.
	 * @return The newly created {@link GodStone} instance.
	 */
	public GodStone addGodStone(int itemId)
	{
		final PersistentState state = godStone != null ? PersistentState.UPDATE_REQUIRED : PersistentState.NEW;
		godStone = new GodStone(getObjectId(), itemId, state);
		return godStone;
	}
	
	/**
	 * Sets the {@code GodStone} for this item.<br>
	 * This method updates the internal {@code godStone} field.
	 * @param godStone The {@link GodStone} object to assign.
	 */
	public void setGodStone(GodStone godStone)
	{
		this.godStone = godStone;
	}
	
	/**
	 * Retrieves the current level of enchantment or authorization for this item.<br>
	 * It checks if the item template allows for authorization first.<br>
	 * If authorization is possible, it returns the {@code authorize} value.<br>
	 * Otherwise, it returns the {@code enchantLevel}.
	 * @return The integer level of either authorization or enchantment.
	 */
	public int getEnchantOrAuthorizeLevel()
	{
		if (getItemTemplate().getMaxAuthorize() > 0)
		{
			return authorize;
		}
		
		return enchantLevel;
	}
	
	/**
	 * Sets the enchantment or authorization level for this item.<br>
	 * This method checks if the item template allows authorization.<br>
	 * If it does, it sets the {@code authorize} value.<br>
	 * Otherwise, it sets the {@code enchantLevel} value.<br>
	 * The persistent state is updated to require a database save.
	 * @param level The new level to apply to the item.
	 */
	public void setEnchantOrAuthorizeLevel(int level)
	{
		if (getItemTemplate().getMaxAuthorize() > 0)
		{
			authorize = level;
		}
		else
		{
			enchantLevel = level;
		}
		
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the {@code persistentState} of this object.<br>
	 * This method prevents changing from {@code PersistentState.NEW} to {@code PersistentState.UPDATE_REQUIRED}.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		switch (persistentState)
		{
			case DELETED:
				if (this.persistentState == PersistentState.NEW)
				{
					this.persistentState = PersistentState.NOACTION;
				}
				else
				{
					this.persistentState = PersistentState.DELETED;
				}
				break;
			case UPDATE_REQUIRED:
				if (this.persistentState == PersistentState.NEW)
				{
					break;
				}
			default:
				this.persistentState = persistentState;
		}
		
	}
	
	/**
	 * Updates the location of the item to a specific storage type.<br>
	 * This method marks the item state as requiring an update for persistence.
	 * @param storageType The {@code int} value representing the new storage location.
	 */
	public void setItemLocation(int storageType)
	{
		itemLocation = storageType;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the current location of the item.<br>
	 * This value represents where the item is stored in the game world.
	 * @return the {@code int} representing the item location.
	 */
	public int getItemLocation()
	{
		return itemLocation;
	}
	
	/**
	 * Retrieves the mask associated with this item.<br>
	 * This value is obtained from the {@link ItemTemplate}.
	 * @return the integer mask of the item.
	 */
	public int getItemMask()
	{
		return itemTemplate.getMask();
	}
	
	/**
	 * Checks if the item is bound to a specific soul.<br>
	 * Returns {@code true} if the item cannot be traded or shared.<br>
	 * Returns {@code false} if the item is freely tradable.
	 * @return the soul bound status of the item.
	 */
	public boolean isSoulBound()
	{
		return isSoulBound;
	}
	
	/**
	 * Checks if the player's item is soul bound.<br>
	 * This method returns {@code false} if the player has the permission to disable soul binding.<br>
	 * Otherwise, it returns the current soul bound status of the item.
	 * @param player The {@link Player} object to check permissions for.
	 * @return {@code true} if the item is soul bound and the permission is not active; {@code false} otherwise.
	 */
	private boolean isSoulBound(Player player)
	{
		if (player.havePermission(MembershipConfig.DISABLE_SOULBIND))
		{
			return false;
		}
		
		return isSoulBound;
	}
	
	/**
	 * Sets whether the item is bound to a specific character.<br>
	 * This method updates the {@code isSoulBound} status and marks the state for persistence.
	 * @param isSoulBound The new soul bound status to apply.
	 */
	public void setSoulBound(boolean isSoulBound)
	{
		this.isSoulBound = isSoulBound;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the equipment type for this item.<br>
	 * This method checks specific properties of the {@code ItemTemplate}.<br>
	 * It handles special cases like Stigmas and Estimas.
	 * @return the {@link EquipType} associated with the item.
	 */
	public EquipType getEquipmentType()
	{
		if (itemTemplate.isStigma())
		{
			return EquipType.STIGMA;
		}
		
		if (itemTemplate.isAccessory())
		{
			return EquipType.ARMOR; // fix for possible client <=> server data errors related to type "NONE"
		}
		
		if (itemTemplate.isEstima())
		{
			return EquipType.ESTIMA;
		}
		
		return itemTemplate.getEquipmentType();
	}
	
	/**
	 * Returns a string representation of the {@code Item}.<br>
	 * This method includes details like the template ID, equipment slot, and item count.
	 * @return A formatted string containing the properties of this item.
	 */
	@Override
	public String toString()
	{
		return "Item [itemId=" + itemTemplate.getTemplateId() + " equipmentSlot=" + equipmentSlot + ", godStone=" + godStone + ", isEquipped=" + isEquipped + ", itemColor=" + itemColor + ", itemCount=" + itemCount + ", itemLocation=" + itemLocation + ", itemTemplate=" + itemTemplate + ", manaStones=" + manaStones + ", persistentState=" + persistentState + "]";
	}
	
	/**
	 * Retrieves the unique identifier for this wardrobe item.<br>
	 * This value corresponds to the {@code itemId} assigned during object creation.
	 * @return The unique {@code int} ID of the item.
	 */
	public int getItemId()
	{
		return itemTemplate.getTemplateId();
	}
	
	/**
	 * Retrieves the unique identifier for the name.<br>
	 * This value corresponds to the {@code name_id} attribute from the template.
	 * @return The integer ID of the name.
	 */
	public int getNameId()
	{
		return itemTemplate.getNameId();
	}
	
	/**
	 * Checks if the item is a result of a fusion process.<br>
	 * It returns {@code true} if the {@code fusionedItemTemplate} is not {@code null}.
	 * @return {@code true} if the item was fused, otherwise {@code false}.
	 */
	public boolean hasFusionedItem()
	{
		return fusionedItemTemplate != null;
	}
	
	/**
	 * Retrieves the template for the item that was fused into this object.<br>
	 * This is useful when identifying the base item in a fusion process.
	 * @return the {@code ItemTemplate} of the fused item.
	 */
	public ItemTemplate getFusionedItemTemplate()
	{
		return fusionedItemTemplate;
	}
	
	/**
	 * Retrieves the unique identifier of the item used in a fusion.<br>
	 * It returns the {@code templateId} from the {@code fusionedItemTemplate}.<br>
	 * If no fusion template exists, it returns {@code 0}.
	 * @return The ID of the fused item or {@code 0} if none.
	 */
	public int getFusionedItemId()
	{
		return fusionedItemTemplate != null ? fusionedItemTemplate.getTemplateId() : 0;
	}
	
	/**
	 * Sets the template for the fused item.<br>
	 * This method updates the {@code fusionedItemTemplate} field.<br>
	 * It also resets the charge information to {@code 0}.
	 * @param itemTemplate The {@link ItemTemplate} of the fused item.
	 */
	public void setFusionedItem(ItemTemplate itemTemplate)
	{
		fusionedItemTemplate = itemTemplate;
		updateChargeInfo(0);
	}
	
	/**
	 * Calculates the total number of sockets available for an item.<br>
	 * This method checks if the item is a weapon or armor before counting.<br>
	 * It handles both standard items and fusioned items separately.
	 * @param isFusionItem A boolean indicating if the item is a fusioned object.
	 * @return The total count of sockets as an {@code int}.
	 */
	public int getSockets(boolean isFusionItem)
	{
		int numSockets;
		if (itemTemplate.isWeapon() || itemTemplate.isArmor())
		{
			if (isFusionItem)
			{
				final ItemTemplate fusedTemp = getFusionedItemTemplate();
				if (fusedTemp == null)
				{
					log.error("Item {} with itemId {} has empty fusioned item ", getObjectId(), getItemId());
					return 0;
				}
				
				numSockets = fusedTemp.getManastoneSlots();
				numSockets += hasOptionalFusionSocket() ? getOptionalFusionSocket() : 0;
			}
			else
			{
				numSockets = getItemTemplate().getManastoneSlots();
				numSockets += hasOptionalSocket() ? getOptionalSocket() : 0;
			}
			
			return numSockets;
		}
		
		return 0;
	}
	
	/**
	 * Retrieves the specific mask for an item based on a {@link Player}.<br>
	 * This method checks the configuration to determine the correct value.
	 * @param player The {@code Player} object used to check permissions or settings.
	 * @return The resulting integer mask value.
	 */
	public int getItemMask(Player player)
	{
		final int finalMask = checkConfig(player, itemTemplate.getMask());
		return finalMask;
	}
	
	/**
	 * Checks and updates the item mask based on player permissions.<br>
	 * This method adds specific flags like {@code TRADEABLE} or storage rights.<br>
	 * It evaluates various membership and trade permissions for the given {@link Player}.
	 * @param player The {@link Player} whose permissions are being checked.
	 * @param mask The initial bitmask to be updated.
	 * @return The updated integer mask including new permission flags.
	 */
	private int checkConfig(Player player, int mask)
	{
		int newMask = mask;
		if (player.havePermission(MembershipConfig.STORE_WH_ALL))
		{
			newMask = newMask | ItemMask.STORABLE_IN_WH;
		}
		
		if (player.havePermission(MembershipConfig.STORE_AWH_ALL))
		{
			newMask = newMask | ItemMask.STORABLE_IN_AWH;
		}
		
		if (player.havePermission(MembershipConfig.STORE_LWH_ALL))
		{
			newMask = newMask | ItemMask.STORABLE_IN_LWH;
		}
		
		if (player.havePermission(MembershipConfig.TRADE_ALL))
		{
			newMask = newMask | ItemMask.TRADEABLE;
		}
		
		if (player.havePermission(MembershipConfig.REMODEL_ALL))
		{
			newMask = newMask | ItemMask.REMODELABLE;
		}
		
		return newMask;
	}
	
	/**
	 * Checks if the current item is the same as another {@code Item}.<br>
	 * This method compares both the object ID and the item ID.
	 * @param i The other {@code Item} to compare against.
	 * @return {@code true} if both IDs match, otherwise {@code false}.
	 */
	public boolean isSameItem(Item i)
	{
		return getObjectId().equals(i.getObjectId()) && (getItemId() == i.getItemId());
	}
	
	/**
	 * Checks if the {@link Player} is allowed to store items in a warehouse.<br>
	 * This method verifies the player's item mask and ensures the character is not soul bound.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player can use the warehouse, otherwise {@code false}.
	 */
	public boolean isStorableinWarehouse(Player player)
	{
		return ((getItemMask(player) & ItemMask.STORABLE_IN_WH) == ItemMask.STORABLE_IN_WH) && !isSoulBound(player);
	}
	
	/**
	 * Checks if the player can store items in an ACC warehouse.<br>
	 * This method verifies the item mask and ensures the item is not soul bound.
	 * @param player The {@link Player} object to check.
	 * @return {@code true} if the item is storable, {@code false} otherwise.
	 */
	public boolean isStorableinAccWarehouse(Player player)
	{
		return ((getItemMask(player) & ItemMask.STORABLE_IN_AWH) == ItemMask.STORABLE_IN_AWH) && !isSoulBound(player);
	}
	
	/**
	 * Checks if the player can store items in a Leg Warehouse.<br>
	 * This method verifies the {@code ItemMask} and ensures the item is not soul bound.
	 * @param player The {@link Player} object to check.
	 * @return {@code true} if the item is storable, {@code false} otherwise.
	 */
	public boolean isStorableinLegWarehouse(Player player)
	{
		return ((getItemMask(player) & ItemMask.STORABLE_IN_LWH) == ItemMask.STORABLE_IN_LWH) && !isSoulBound(player);
	}
	
	/**
	 * Checks if the player is allowed to trade an item.<br>
	 * This method verifies the {@code ItemMask} and ensures the item is not soul bound.
	 * @param player The {@link Player} object to check.
	 * @return {@code true} if the item can be traded, {@code false} otherwise.
	 */
	public boolean isTradeable(Player player)
	{
		return ((getItemMask(player) & ItemMask.TRADEABLE) == ItemMask.TRADEABLE) && !isSoulBound(player);
	}
	
	/**
	 * Checks if the item can be remodeled by a specific player.<br>
	 * This method compares the {@code ItemMask} of the item against the {@code REMODELABLE} flag.
	 * @param player The {@link Player} who is attempting to remodel the item.
	 * @return {@code true} if the item can be remodeled, {@code false} otherwise.
	 */
	public boolean isRemodelable(Player player)
	{
		return (getItemMask(player) & ItemMask.REMODELABLE) == ItemMask.REMODELABLE;
	}
	
	/**
	 * Checks if the item can be sold.<br>
	 * This method evaluates the {@code ItemMask} of the object.<br>
	 * It returns {@code true} if the sellable flag is set.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the item is sellable, {@code false} otherwise.
	 */
	public boolean isSellable()
	{
		return (getItemMask() & ItemMask.SELLABLE) == ItemMask.SELLABLE;
	}
	
	/**
	 * Checks if the item allows for AP extraction.<br>
	 * This method verifies the {@code ItemMask} of the current object.<br>
	 * It returns {@code true} if the {@code CAN_AP_EXTRACT} flag is set.
	 * @return {@code true} if extraction is allowed, {@code false} otherwise.
	 */
	public boolean canApExtract()
	{
		return (getItemMask() & ItemMask.CAN_AP_EXTRACT) == ItemMask.CAN_AP_EXTRACT;
	}
	
	/**
	 * Checks if the item is allowed to have a socket godstone.<br>
	 * This method verifies the {@code ItemMask} for the {@code CAN_PROC_ENCHANT} flag.
	 * @return {@code true} if the item can be socketed, otherwise {@code false}.
	 */
	public boolean canSocketGodstone()
	{
		return (getItemMask() & ItemMask.CAN_PROC_ENCHANT) == ItemMask.CAN_PROC_ENCHANT;
	}
	
	/**
	 * Checks if the item is eligible for amplification.<br>
	 * This method verifies the {@code ItemMask} of the current object.<br>
	 * It returns {@code true} if the {@code CAN_AMPLIFICATION} flag is set.
	 * @return {@code true} if the item can be amplified, {@code false} otherwise.
	 */
	public boolean canAmplify()
	{
		return (getItemMask() & ItemMask.CAN_AMPLIFICATION) == ItemMask.CAN_AMPLIFICATION;
	}
	
	/**
	 * Retrieves the expiration time of this object.<br>
	 * This value represents when the object will expire.
	 * @return The expiration time as an {@code int}.
	 */
	@Override
	public int getExpireTime()
	{
		return expireTime;
	}
	
	/**
	 * Sets the expiration time for this item.<br>
	 * This updates the {@code expireTime} field of the object.
	 * @param expireTime The new expiration time value to set.
	 */
	public void setExpireTime(int expireTime)
	{
		this.expireTime = expireTime;
	}
	
	/**
	 * Calculates the remaining time until this item expires.<br>
	 * It subtracts the current system time from the stored expiration timestamp.<br>
	 * If no expiration is set, it returns {@code 0}.
	 * @return The number of seconds remaining before the item expires.
	 */
	public int getExpireTimeRemaining()
	{
		if (expireTime == 0)
		{
			return 0;
		}
		
		return expireTime - (int) (System.currentTimeMillis() / 1000);
	}
	
	/**
	 * Retrieves the time remaining for a temporary exchange.<br>
	 * This value is used to track how long an item can be traded or moved.
	 * @return The current {@code int} value of the temporary exchange time.
	 */
	public int getTemporaryExchangeTime()
	{
		return temporaryExchangeTime;
	}
	
	/**
	 * Calculates the remaining time for a temporary exchange.<br>
	 * It returns the difference between the expiration timestamp and the current time in seconds.<br>
	 * If no exchange time is set, it returns {@code 0}.
	 * @return The number of seconds remaining until the exchange expires.
	 */
	public int getTemporaryExchangeTimeRemaining()
	{
		if (temporaryExchangeTime == 0)
		{
			return 0;
		}
		
		return temporaryExchangeTime - (int) (System.currentTimeMillis() / 1000);
	}
	
	/**
	 * Sets the time for a temporary exchange.<br>
	 * This value is stored in the {@code temporaryExchangeTime} field.
	 * @param temporaryExchangeTime The new time to set for the exchange.
	 */
	public void setTemporaryExchangeTime(int temporaryExchangeTime)
	{
		this.temporaryExchangeTime = temporaryExchangeTime;
	}
	
	/**
	 * Expires the item for a specific player.<br>
	 * This method unequips the item if it is currently worn.<br>
	 * It also removes the item from all storage types except {@code LEGION_WAREHOUSE}.<br>
	 * System messages are sent to the {@link Player} based on the storage type.
	 * @param player The {@link Player} who triggered the expiration.
	 */
	@Override
	public void expireEnd(Player player)
	{
		if (player == null)
		{
			return;
		}
		
		if (isEquipped())
		{
			player.getEquipment().unEquipItem(getObjectId(), getEquipmentSlot());
		}
		
		for (StorageType i : StorageType.values())
		{
			if (i == StorageType.LEGION_WAREHOUSE)
			{
				continue;
			}
			
			final IStorage storage = player.getStorage(i.getId());
			
			if ((storage != null) && (storage.getItemByObjId(getObjectId()) != null))
			{
				storage.delete(this);
				switch (i)
				{
					case CUBE:
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400034, new DescriptionId(getNameId())));
						break;
					case ACCOUNT_WAREHOUSE:
					case REGULAR_WAREHOUSE:
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400406, new DescriptionId(getNameId())));
						break;
					default:
						break;
				}
			}
		}
	}
	
	/**
	 * Sends an expiration message to a specific player.<br>
	 * This method notifies the {@code Player} that an object is expiring.<br>
	 * It uses the provided {@code time} value to format the remaining duration.
	 * @param player The {@code Player} who will receive the notification.
	 * @param time The amount of time left before expiration.
	 */
	@Override
	public void expireMessage(Player player, int time)
	{
		if (player != null)
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400481, new DescriptionId(getNameId()), time));
		}
	}
	
	/**
	 * Sets the repurchase price for this item.<br>
	 * This updates the {@code repurchasePrice} field with a new value.
	 * @param price The new price to set for the item.
	 */
	public void setRepurchasePrice(long price)
	{
		repurchasePrice = price;
	}
	
	/**
	 * Retrieves the current repurchase price of the item.<br>
	 * This value represents how much currency is returned when selling back.
	 * @return The {@code long} value of the repurchase price.
	 */
	public long getRepurchasePrice()
	{
		return repurchasePrice;
	}
	
	/**
	 * Retrieves the current number of times this item has been activated.<br>
	 * This value is stored in the {@code activationCount} field.
	 * @return The total count of activations as an {@code int}.
	 */
	public int getActivationCount()
	{
		return activationCount;
	}
	
	/**
	 * Sets the number of times this item has been activated.<br>
	 * This updates the {@code activationCount} field for the current object.
	 * @param activationCount The new count to assign to the item.
	 */
	public void setActivationCount(int activationCount)
	{
		this.activationCount = activationCount;
	}
	
	/**
	 * Retrieves the current conditioning information for this item.<br>
	 * This method returns a {@link ChargeInfo} object containing specific details.
	 * @return the {@code ChargeInfo} associated with the item.
	 */
	public ChargeInfo getConditioningInfo()
	{
		return conditioningInfo;
	}
	
	/**
	 * Sets the {@code ChargeInfo} for this item.<br>
	 * This method updates the internal {@code conditioningInfo} field.
	 * @param conditioning The {@link ChargeInfo} to apply to the item.
	 */
	public void setConditioningInfo(ChargeInfo conditioning)
	{
		conditioningInfo = conditioning;
	}
	
	/**
	 * Retrieves the current number of charge points.<br>
	 * This method checks if {@code conditioningInfo} is not {@code null}.<br>
	 * If it exists, it returns the value from {@code conditioningInfo}.<br>
	 * Otherwise, it returns {@code 0}.
	 * @return The total number of charge points as an {@code int}.
	 */
	public int getChargePoints()
	{
		return conditioningInfo != null ? conditioningInfo.getChargePoints() : 0;
	}
	
	/**
	 * Retrieves the current charge level of the item.<br>
	 * This method checks the total points from {@code getChargePoints}.<br>
	 * It returns a value based on whether the points exceed {@code ChargeInfo.LEVEL1}.
	 * @return The calculated charge level as an {@code int}.
	 */
	public int getChargeLevel()
	{
		if (getChargePoints() == 0)
		{
			return 0;
		}
		
		return getChargePoints() > ChargeInfo.LEVEL1 ? 2 : 1;
	}
	
	/**
	 * Retrieves the maximum charge level of this item.<br>
	 * It compares the current item's improvement level with its fused item's level.<br>
	 * The method returns the higher value between the two levels.
	 * @return the highest charge level as an {@code int}.
	 */
	public int getChargeLevelMax()
	{
		int thisChargeLevel = 0;
		if (getImprovement() != null)
		{
			thisChargeLevel = getImprovement().getLevel();
		}
		
		int fusionedChargeLevel = 0;
		if (hasFusionedItem() && (getFusionedItemTemplate().getImprovement() != null))
		{
			fusionedChargeLevel = getFusionedItemTemplate().getImprovement().getLevel();
		}
		
		return Math.max(thisChargeLevel, fusionedChargeLevel);
	}
	
	/**
	 * Checks if the chair object is allowed to expire at this moment.<br>
	 * This method currently always returns {@code true}.
	 * @return {@code true} if the object can expire now, otherwise {@code false}.
	 */
	@Override
	public boolean canExpireNow()
	{
		return true;
	}
	
	/**
	 * Retrieves the {@link Improvement} associated with this item.<br>
	 * It first checks the base item template for an improvement.<br>
	 * If none exists, it checks the fusioned item template instead.<br>
	 * Returns {@code null} if no improvement is found in either source.
	 * @return The {@link Improvement} object or {@code null}.
	 */
	public Improvement getImprovement()
	{
		if (getItemTemplate().getImprovement() != null)
		{
			return getItemTemplate().getImprovement();
		}
		else if (hasFusionedItem() && (getFusionedItemTemplate().getImprovement() != null))
		{
			return getFusionedItemTemplate().getImprovement();
		}
		
		return null;
	}
	
	/**
	 * Retrieves the current random bonus number.<br>
	 * This value is used to determine specific item properties.
	 * @return the {@code int} value of the bonus number.
	 */
	public int getBonusNumber()
	{
		return bonusNumber;
	}
	
	/**
	 * Retrieves the list of active modifiers for this item.<br>
	 * If no modifiers exist, it initializes a new {@code ArrayList}.
	 * @return A {@code List} of {@link StatFunction} objects.
	 */
	public List<StatFunction> getCurrentModifiers()
	{
		if (currentModifiers == null)
		{
			currentModifiers = new ArrayList<>();
		}
		
		return currentModifiers;
	}
	
	/**
	 * Updates the list of active modifiers for this item.<br>
	 * This method clears the existing modifiers and replaces them with the new list.
	 * @param currentModifiers The new list of {@link StatFunction} objects to apply.
	 */
	public void setCurrentModifiers(List<StatFunction> currentModifiers)
	{
		getCurrentModifiers().clear();
		getCurrentModifiers().addAll(currentModifiers);
	}
	
	/**
	 * Retrieves the {@link IdianStone} associated with this item.<br>
	 * This method returns the specific stone data if it exists.
	 * @return the {@code IdianStone} object.
	 */
	public IdianStone getIdianStone()
	{
		return idianStone;
	}
	
	/**
	 * Sets the {@code IdianStone} for this item.<br>
	 * This method updates the internal stone reference.
	 * @param idianStone The {@link IdianStone} object to assign.
	 */
	public void setIdianStone(IdianStone idianStone)
	{
		this.idianStone = idianStone;
	}
	
	/**
	 * Retrieves the current random statistics for this item.<br>
	 * This method returns a {@link RandomStats} object containing all calculated bonuses.
	 * @return The {@code RandomStats} associated with this item.
	 */
	public RandomStats getRandomStats()
	{
		return randomStats;
	}
	
	/**
	 * Sets the {@code RandomStats} for this item.<br>
	 * This method updates the internal stats object with the provided values.
	 * @param randomStats The {@link RandomStats} object to assign.
	 */
	public void setRandomStats(RandomStats randomStats)
	{
		this.randomStats = randomStats;
	}
	
	/**
	 * Sets the unique identifier for the random bonus.<br>
	 * This value is used to track specific bonus properties on an {@code Item}.
	 * @param bonusNumber The integer value representing the bonus number.
	 */
	public void setBonusNumber(int bonusNumber)
	{
		this.bonusNumber = bonusNumber;
	}
	
	/**
	 * Sets the random count for this item.<br>
	 * This value determines how many random bonuses are applied.
	 * @param rndCount The number of random bonuses to set.
	 */
	public void setRandomCount(int rndCount)
	{
		this.rndCount = rndCount;
	}
	
	/**
	 * Retrieves the random count value for this item.<br>
	 * This value is used to determine the number of random bonuses.
	 * @return the current {@code rndCount} as an {@code int}.
	 */
	public int getRandomCount()
	{
		return rndCount;
	}
	
	/**
	 * Sets the number of packs for this item.<br>
	 * This updates the {@code packCount} field in the current object.
	 * @param packCount The new count to assign to the item.
	 */
	public void setPackCount(int packCount)
	{
		this.packCount = packCount;
	}
	
	/**
	 * Retrieves the number of items in a pack.<br>
	 * This value represents how many individual units are contained within the item.
	 * @return The total count of items in the pack as an {@code int}.
	 */
	public int getPackCount()
	{
		return packCount;
	}
	
	/**
	 * Checks if the item is currently packed.<br>
	 * This method returns {@code true} if the item is in a packed state.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if the item is packed, {@code false} otherwise.
	 */
	public boolean isPacked()
	{
		return isPacked;
	}
	
	/**
	 * Sets whether the item is currently packed.<br>
	 * This method updates the {@code isPacked} status.<br>
	 * It also marks the object state as requiring an update for persistence.
	 * @param isPacked The new packing status to set.
	 */
	public void setPacked(boolean isPacked)
	{
		this.isPacked = isPacked;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Checks if the item is currently amplified.<br>
	 * This method returns {@code true} if the amplification status is active.
	 * @return {@code true} if the item is amplified, {@code false} otherwise.
	 */
	public boolean isAmplified()
	{
		return isAmplified;
	}
	
	/**
	 * Sets whether the item is amplified.<br>
	 * This updates the {@code isAmplified} status of the object.
	 * @param isAmplified The amplification status to set. Use {@code true} for amplified or {@code false} otherwise.
	 */
	public void setAmplified(boolean isAmplified)
	{
		this.isAmplified = isAmplified;
	}
	
	/**
	 * Retrieves the current amplification skill level of the item.<br>
	 * This value is only returned if {@code EnchantsConfig.ENCHANT_SKILL_ENABLE} is set to {@code true}.<br>
	 * If skills are disabled, it returns {@code 0}.
	 * @return The integer value of the amplification skill or {@code 0}.
	 */
	public int getAmplificationSkill()
	{
		if (EnchantsConfig.ENCHANT_SKILL_ENABLE)
		{
			return amplificationSkill;
		}
		
		return 0;
	}
	
	/**
	 * Sets the amplification skill level for this item.<br>
	 * This updates the {@code amplificationSkill} field.
	 * @param skill The new skill value to assign.
	 */
	public void setAmplificationSkill(int skill)
	{
		amplificationSkill = skill;
	}
	
	/**
	 * Sets the reduction level for this item.<br>
	 * This method updates the {@code ReductionLevel} field.<br>
	 * It also marks the state as requiring an update to persist changes.
	 * @param paramInt The new reduction level value to set.
	 */
	public void setReductionLevel(int paramInt)
	{
		ReductionLevel = paramInt;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Retrieves the current reduction level of the item.<br>
	 * This value represents how many levels the item has been reduced.
	 * @return The {@code int} value of the reduction level.
	 */
	public int getReductionLevel()
	{
		return ReductionLevel;
	}
	
	/**
	 * Updates the Luna reskin status of the item.<br>
	 * This method sets whether the item uses a special skin.
	 * @param luna_reskin The new value to set for the {@code luna_reskin} property.
	 */
	public void setLunaReskin(boolean luna_reskin)
	{
		this.luna_reskin = luna_reskin;
	}
	
	/**
	 * Checks if the item uses the Luna reskin.
	 * @return {@code true} if the item is a Luna reskin, {@code false} otherwise.
	 */
	public boolean isLunaReskin()
	{
		return luna_reskin;
	}
	
	/**
	 * Checks if the item is eligible for enhancement.<br>
	 * This method returns {@code true} if the item can be enhanced.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if the item can be enhanced, {@code false} otherwise.
	 */
	public boolean isEnhance()
	{
		return canEnhance;
	}
	
	/**
	 * Sets whether the item is eligible for enhancement.<br>
	 * This updates the {@code canEnhance} field of the object.
	 * @param canEnhance The boolean value to set for enhancement eligibility.
	 */
	public void setIsEnhance(boolean canEnhance)
	{
		this.canEnhance = canEnhance;
	}
	
	/**
	 * Retrieves the unique identifier for the enhancement skill.<br>
	 * This value is used to determine which specific skill was applied to the item.
	 * @return the {@code int} ID of the enhancement skill.
	 */
	public int getEnhanceSkillId()
	{
		return enhanceSkillId;
	}
	
	/**
	 * Sets the unique identifier for the enhancement skill.<br>
	 * This value is used to determine which specific skill is applied to the item.
	 * @param enhanceSkillId The {@code int} ID of the enhancement skill.
	 */
	public void setEnhanceSkillId(int enhanceSkillId)
	{
		this.enhanceSkillId = enhanceSkillId;
	}
	
	/**
	 * Retrieves the current enhancement level of the item.<br>
	 * This value represents how many times the item has been enhanced.
	 * @return The {@code int} value of the enhancement level.
	 */
	public int getEnhanceEnchantLevel()
	{
		return enhanceEnchantLevel;
	}
	
	/**
	 * Sets the enhancement enchantment level for this item.<br>
	 * This updates the {@code enhanceEnchantLevel} field.
	 * @param enhanceEnchantLevel The new enchantment level to apply.
	 */
	public void setEnhanceEnchantLevel(int enhanceEnchantLevel)
	{
		this.enhanceEnchantLevel = enhanceEnchantLevel;
	}
	
	/**
	 * Checks if the item is currently in a sealed state.<br>
	 * This method evaluates the {@code unSeal} property of the item.<br>
	 * It returns {@code true} if the seal is active and {@code false} otherwise.
	 * @return {@code true} if the item is sealed, {@code false} otherwise.
	 */
	public boolean isSeal()
	{
		if (unSeal == 1)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the current unseal level of the item.<br>
	 * This value is used to determine the specific properties of the object.
	 * @return The {@code int} value representing the unseal level.
	 */
	public int getUnSeal()
	{
		return unSeal;
	}
	
	/**
	 * Sets the {@code unSeal} value for this item.<br>
	 * This method marks the object state as requiring an update to the database.
	 * @param unSeal The new {@code int} value for the unSeal property.
	 */
	public void setUnSeal(int unSeal)
	{
		this.unSeal = unSeal;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Sets the skin skill for this item.<br>
	 * This updates the {@code SkinSkill} field with the provided value.
	 * @param skill The unique identifier for the skin skill.
	 */
	public void setItemSkinSkill(int skill)
	{
		SkinSkill = skill;
	}
	
	/**
	 * Retrieves the skill ID associated with the item's skin.<br>
	 * This value is stored in the {@code SkinSkill} field of the {@link Item} object.
	 * @return The integer ID of the skin skill.
	 */
	public int getItemSkinSkill()
	{
		return SkinSkill;
	}
	
	/**
	 * Retrieves the random bonus associated with this item.<br>
	 * This method returns the {@code RealRandomBonus} object.
	 * @return the current {@code RealRandomBonus} for the item.
	 */
	public RealRandomBonus getRealRndBonus()
	{
		return realRndBonus;
	}
	
	/**
	 * Sets the {@code RealRandomBonus} for this item.<br>
	 * This method updates the internal bonus data used for random stats.
	 * @param realRndBonus The {@link RealRandomBonus} object to assign.
	 */
	public void setRealRndBonus(RealRandomBonus realRndBonus)
	{
		this.realRndBonus = realRndBonus;
	}
	
	/**
	 * Retrieves the current grind socket value.<br>
	 * This value is used to identify which specific slot is being utilized for grinding.
	 * @return The integer value of the {@code grindSocket}.
	 */
	public int getGrindSocket()
	{
		return grindSocket;
	}
	
	/**
	 * Sets the {@code grindSocket} value for this item.<br>
	 * This updates the internal state of the object with the provided integer.
	 * @param grindSocket The new socket value to assign.
	 */
	public void setGrindSocket(int grindSocket)
	{
		this.grindSocket = grindSocket;
	}
	
	/**
	 * Retrieves the {@code OdianStone} object associated with this item.
	 * @return The {@link OdianStone} instance.
	 */
	public OdianStone getOdianStone()
	{
		return odianStone;
	}
	
	/**
	 * Creates and adds a new {@link OdianStone} to the object.<br>
	 * This method initializes the stone using the provided item identifier.
	 * @param itemId The unique ID of the item to create.
	 * @return The newly created {@code OdianStone} instance.
	 */
	public OdianStone addOdianStone(int itemId)
	{
		final PersistentState state = odianStone != null ? PersistentState.UPDATE_REQUIRED : PersistentState.NEW;
		odianStone = new OdianStone(getObjectId(), itemId, state);
		return odianStone;
	}
	
	/**
	 * Sets the {@code OdianStone} for this item.<br>
	 * This method updates the internal {@code odianStone} field.
	 * @param odianStone The {@link OdianStone} object to assign.
	 */
	public void setOdianStone(OdianStone odianStone)
	{
		this.odianStone = odianStone;
	}
	
	/**
	 * Checks if the item contains an {@link OdianStone}.<br>
	 * This method returns {@code true} if the stone is present.<br>
	 * It returns {@code false} if the stone is {@code null}.
	 * @return {@code true} if the item has an {@link OdianStone}, otherwise {@code false}.
	 */
	public boolean hasOdianStone()
	{
		return odianStone != null;
	}
	
	/**
	 * Retrieves the {@code RuneStone} associated with this object.
	 * @return the {@code RuneStone} instance.
	 */
	public RuneStone getRuneStone()
	{
		return runeStone;
	}
	
	/**
	 * Creates and adds a new {@link RuneStone} to the object.<br>
	 * This method initializes the stone using the provided item identifier.
	 * @param itemId The unique ID of the item to create.
	 * @return The newly created {@code RuneStone} instance.
	 */
	public RuneStone addRuneStone(int itemId)
	{
		final PersistentState state = runeStone != null ? PersistentState.UPDATE_REQUIRED : PersistentState.NEW;
		runeStone = new RuneStone(getObjectId(), itemId, state);
		return runeStone;
	}
	
	/**
	 * Sets the {@code RuneStone} for this item.<br>
	 * This method updates the internal {@code runeStone} field.
	 * @param runeStone The {@link RuneStone} object to assign.
	 */
	public void setRuneStone(RuneStone runeStone)
	{
		this.runeStone = runeStone;
	}
	
	/**
	 * Checks if the item contains a {@link RuneStone}.
	 * @return {@code true} if the {@code runeStone} is not {@code null}, otherwise {@code false}.
	 */
	public boolean hasRuneStone()
	{
		return runeStone != null;
	}
	
	/**
	 * Retrieves the color value associated with the grind effect.<br>
	 * This value is used to determine the visual appearance of the item's grinding property.
	 * @return The {@code int} color code for the grind effect.
	 */
	public int getGrindColor()
	{
		return grindColor;
	}
	
	/**
	 * Sets the color of the grind effect.<br>
	 * This updates the {@code grindColor} field for this item.
	 * @param grindColor The integer value representing the new color.
	 */
	public void setGrindColor(int grindColor)
	{
		this.grindColor = grindColor;
	}
	
	/**
	 * Retrieves the current grind value of this item.
	 * @return the {@code Item} object representing the grind data.
	 */
	public Item getGrind()
	{
		return grind;
	}
	
	/**
	 * Sets the {@code grind} property of this item.<br>
	 * This method updates the internal reference to a {@link Item} object.
	 * @param grind The {@code Item} to be assigned as the grind.
	 */
	public void setGrind(Item grind)
	{
		this.grind = grind;
	}
	
	/**
	 * Checks if the item has a grinding stone applied.<br>
	 * This method returns {@code true} if the {@code grind} field is not {@code null}.
	 * @return {@code true} if it has a grinding stone, {@code false} otherwise.
	 */
	public boolean hasGring()
	{
		return grind != null;
	}
	
	/**
	 * Checks if the item is currently in a contaminated state.<br>
	 * This status is retrieved from the internal {@code contaminated} field.
	 * @return {@code true} if the item is contaminated, {@code false} otherwise.
	 */
	public boolean isContaminated()
	{
		return contaminated;
	}
	
	/**
	 * Updates the contamination status of this item.<br>
	 * Sets the {@code contaminated} field to the provided value.
	 * @param contaminated The new contamination state to apply.
	 */
	public void setContaminated(boolean contaminated)
	{
		this.contaminated = contaminated;
	}
	
	/**
	 * Retrieves the current value of the {@code grindStone}.<br>
	 * This value represents the specific stone used for grinding.
	 * @return The {@code long} value of the {@code grindStone}.
	 */
	public long getGrindStone()
	{
		return grindStone;
	}
	
	/**
	 * Sets the {@code grindStone} value for this item.<br>
	 * This updates the internal state of the object with the provided {@code long} value.
	 * @param grindStone The new {@code grindStone} value to assign.
	 */
	public void setGrindStone(long grindStone)
	{
		this.grindStone = grindStone;
	}
	
	/**
	 * Retrieves the current grind slot of the item.<br>
	 * This value identifies which specific slot is used for grinding.
	 * @return The integer value representing the {@code grindSlot}.
	 */
	public int getGrindSlot()
	{
		return grindSlot;
	}
	
	/**
	 * Sets the specific slot for grinding.<br>
	 * This updates the {@code grindSlot} value of the item.
	 * @param grindSlot The index of the grind slot to set.
	 */
	public void setGrindSlot(int grindSlot)
	{
		this.grindSlot = grindSlot;
	}
}
