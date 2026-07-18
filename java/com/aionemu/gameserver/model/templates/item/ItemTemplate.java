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
package com.aionemu.gameserver.model.templates.item;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.LunaSystemConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.items.ItemMask;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.TitleTemplate;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Represents the base configuration and properties for an item in the game.<br>
 * This class serves as a template used to define how items behave, appear, and interact with players.<br>
 * It extends {@link VisibleObjectTemplate} to provide shared visual and spatial data.
 * @author Luno modified by ATracer
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(namespace = "", name = "ItemTemplate")
public class ItemTemplate extends VisibleObjectTemplate
{
	@XmlAttribute(name = "id", required = true)
	@XmlID
	private String id;
	
	@XmlElement(name = "modifiers", required = false)
	protected ModifiersTemplate modifiers;
	
	@XmlAttribute(name = "name_desc")
	private String namedesc;
	
	@XmlElement(name = "actions", required = false)
	protected ItemActions actions;
	
	@XmlAttribute(name = "mask")
	private int mask;
	
	@XmlAttribute(name = "category")
	private ItemCategory category = ItemCategory.NONE;
	
	@XmlAttribute(name = "slot")
	private int itemSlot;
	
	@XmlAttribute(name = "equipment_type")
	private EquipType equipmentType = EquipType.NONE;
	
	@XmlAttribute(name = "weapon_boost")
	private int weaponBoost;
	
	@XmlAttribute(name = "price")
	private int price;
	
	@XmlAttribute(name = "luna_price")
	private int lunaPrice;
	
	@XmlAttribute(name = "max_stack_count")
	private int maxStackCount = 1;
	
	@XmlAttribute(name = "level")
	private int level;
	
	@XmlAttribute(name = "quality")
	private ItemQuality itemQuality;
	
	@XmlAttribute(name = "item_type")
	private ItemType itemType;
	
	@XmlAttribute(name = "weapon_type")
	private WeaponType weaponType;
	
	@XmlAttribute(name = "armor_type")
	private ArmorType armorType;
	
	@XmlAttribute(name = "attack_type")
	private ItemAttackType attackType;
	
	@XmlAttribute(name = "attack_gap")
	private float attackGap;
	
	@XmlAttribute(name = "desc")
	private String description;
	
	@XmlAttribute(name = "option_slot_bonus")
	private int optionSlotBonus;
	
	@XmlAttribute(name = "rnd_bonus")
	private int rnd_bonus = 0;
	
	@XmlAttribute(name = "rnd_real_bonus")
	private int real_rnd_bonus = 0;
	
	@XmlAttribute(name = "rnd_count")
	private int rnd_count = 0;
	
	@XmlAttribute(name = "bonus_apply")
	private String bonusApply; // enum
	
	@XmlAttribute(name = "race")
	private Race race = Race.PC_ALL;
	
	@XmlAttribute(name = "id")
	private int itemId;
	
	@XmlAttribute(name = "return_world")
	private int returnWorldId;
	
	@XmlAttribute(name = "return_alias")
	private String returnAlias;
	
	@XmlElement(name = "godstone")
	private GodstoneInfo godstoneInfo;
	
	@XmlElement(name = "stigma")
	private Stigma stigma;
	
	@XmlAttribute(name = "name")
	private String name;
	
	@XmlAttribute(name = "restrict")
	private String restrict;
	
	@XmlAttribute(name = "restrict_max")
	private String restrictMax;
	
	@XmlTransient
	private int[] restricts;
	
	@XmlTransient
	private byte[] restrictsMax;
	
	@XmlAttribute(name = "m_slots")
	private int manastoneSlots;
	
	@XmlAttribute(name = "s_slots")
	private int specialSlots;
	
	@XmlAttribute(name = "max_enchant")
	private int maxEnchant;
	
	@XmlAttribute(name = "max_enchant_bonus")
	private int max_enchant_bonus;
	
	@XmlAttribute(name = "pack_count")
	protected int packCount;
	
	@XmlAttribute(name = "temp_exchange_time")
	protected int temExchangeTime;
	
	@XmlAttribute(name = "expire_time")
	protected int expireTime;
	
	@XmlElement(name = "weapon_stats")
	protected WeaponStats weaponStats;
	
	@XmlAttribute(name = "activate_count")
	private int activationCount;
	
	@XmlElement(name = "tradein_list")
	protected TradeinList tradeinList;
	
	@XmlElement(name = "acquisition")
	private Acquisition acquisition;
	
	@XmlElement(name = "disposition")
	private Disposition disposition;
	
	@XmlElement(name = "improve")
	private Improvement improvement;
	
	@XmlElement(name = "uselimits")
	private ItemUseLimits useLimits = new ItemUseLimits();
	
	@XmlElement(name = "inventory")
	private ExtraInventory extraInventory;
	
	@XmlElement(name = "idian")
	private Idian idianAction;
	
	@XmlAttribute(name = "robot_id")
	private int robot_id;
	
	@XmlAttribute(name = "max_authorize")
	private int max_authorize;
	
	@XmlAttribute(name = "authorize_condition")
	private int authorize_condition;
	
	@XmlAttribute(name = "authorize_name")
	private int authorize_name;
	
	@XmlAttribute(name = "oversea_only")
	private int oversea_only;
	
	@XmlAttribute(name = "activate_combat")
	private boolean activateCombat = false;
	
	@XmlAttribute(name = "exceed_enchant")
	private boolean exceedEnchant = false;
	
	@XmlAttribute(name = "enchant_skill")
	private String enchantSkillSet;
	
	@XmlAttribute(name = "minion_ticket")
	private boolean minion_ticket;
	
	@XmlAttribute(name = "is_cash_contract")
	private boolean is_cash_contract;
	
	@XmlAttribute(name = "minion_list")
	private int minionList;
	
	@XmlAttribute(name = "skill_enchant")
	private int skill_enchant;
	
	@XmlAttribute(name = "enchant_type")
	private EnchantType enchantType;
	
	@XmlAttribute(name = "skin_skill")
	private int skin_skill;
	
	@XmlAttribute(name = "option_slot_max")
	private int option_slot_max;
	
	@XmlAttribute(name = "option_slot_add_count")
	private int option_slot_add_count;
	
	@XmlAttribute(name = "grind_color")
	private int grindColor;
	
	@XmlAttribute(name = "grind_tier")
	private int grindTier;
	
	@XmlAttribute(name = "grind_quality")
	private int grindQuality;
	
	@XmlAttribute(name = "grind_slot")
	private GrindSlot grindSlot;
	
	@XmlAttribute(name = "grind_slot_opening_cost")
	private int grind_slot_opening_cost;
	
	@XmlAttribute(name = "odian_skill_id")
	private int odianSkillId;
	
	@XmlAttribute(name = "odian_skill_level")
	private int odianSkillLevel;
	
	@XmlAttribute(name = "rune_transform_table_id")
	private int runTransformTableId;
	
	@XmlAttribute(name = "transform_list")
	private int transformList;
	
	private static final WeaponStats emptyWeaponStats = new WeaponStats();
	@XmlTransient
	private boolean isQuestUpdateItem;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It parses the {@code id}, {@code restrict}, and {@code restrictMax} attributes into their respective numeric formats.<br>
	 * It also initializes the {@code weaponStats} field if it is null.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (id != null)
		{
			setItemId(Integer.parseInt(id));
		}
		
		final String[] parts = restrict.split(",");
		restricts = new int[18]; // 18 (7.x Painter)
		for (int i = 0; i < parts.length; i++)
		{
			restricts[i] = Integer.parseInt(parts[i]);
		}
		
		if (restrictMax != null)
		{
			final String[] partsMax = restrictMax.split(",");
			restrictsMax = new byte[18]; // 18 (7.x Painter)
			for (int i = 0; i < partsMax.length; i++)
			{
				restrictsMax[i] = Byte.parseByte(partsMax[i]);
			}
		}
		
		if (weaponStats == null)
		{
			weaponStats = emptyWeaponStats;
		}
	}
	
	/**
	 * Calculates the maximum level restriction for a specific player.<br>
	 * This method checks if the {@code Player} exceeds the allowed level for their class.<br>
	 * It returns the limit if exceeded, otherwise it returns 0.
	 * @param player The {@link Player} object to check.
	 * @return The maximum level restriction as a {@code byte}.
	 */
	public byte getMaxLevelRestrict(Player player)
	{
		if (restrictMax != null)
		{
			final byte restrictId = player.getPlayerClass().getClassId();
			final byte restrictLevel = restrictsMax[restrictId];
			return player.getLevel() <= restrictLevel ? 0 : restrictLevel;
		}
		
		return 0;
	}
	
	/**
	 * Retrieves the bitmask associated with this item.<br>
	 * This value is used for bitwise AND operations.<br>
	 * It helps identify specific properties or restrictions when multiple options are allowed.
	 * @return The integer mask for this item.
	 */
	public int getMask()
	{
		return mask;
	}
	
	/**
	 * Retrieves the category of this item.<br>
	 * This method returns the {@code ItemCategory} associated with the template.
	 * @return The {@code ItemCategory} of the item.
	 */
	public ItemCategory getCategory()
	{
		return category;
	}
	
	/**
	 * Retrieves the equipment slot index for this item.<br>
	 * This method includes logic to handle specific items that may have missing slot data.<br>
	 * It returns a unique integer representing the correct slot position.
	 * @return The {@code int} value of the item slot.
	 */
	public int getItemSlot()
	{
		if (itemSlot < 1)
		{
			// TEMP FIX UNTIL PARSER IS FIXED (Missing Slot for some Items!!!!!
			if ((getCategory() == ItemCategory.JACKET) && (getArmorType() == ArmorType.ROBE))
			{
				return 8;
			}
		}
		
		if (isTwoHandWeapon())
		{
			// Temp fix for 2Hand Weapon's Display TODO find a better way ^^
			return 3;
		}
		
		return itemSlot;
	}
	
	/**
	 * Checks if the item is restricted to a specific class.<br>
	 * It determines if the {@code playerClass} or its starting class can use it.
	 * @param playerClass The {@link PlayerClass} to check against.
	 * @return {@code true} if the class is allowed to use the item, {@code false} otherwise.
	 */
	public boolean isClassSpecific(PlayerClass playerClass)
	{
		boolean related = restricts[playerClass.ordinal()] > 0;
		if (!related && !playerClass.isStartingClass())
		{
			related = restricts[PlayerClass.getStartingClassFor(playerClass).ordinal()] > 0;
		}
		
		return related;
	}
	
	/**
	 * Retrieves the minimum level required for a specific class to use this item.<br>
	 * It checks the {@code restricts} array based on the provided {@link PlayerClass}.<br>
	 * If the level is between 66 and 83, it returns 66 as a special case.<br>
	 * Returns -1 if no level restriction exists.
	 * @param playerClass The class of the player attempting to use the item.
	 * @return The required level integer or -1 if there is no restriction.
	 */
	public int getRequiredLevel(PlayerClass playerClass)
	{
		final int requiredLevel = restricts[playerClass.ordinal()];
		
		// A player can equip item between 66-83 but have not full stats apply
		if ((requiredLevel >= 66) && (requiredLevel <= 83))
		{
			return 66;
		}
		
		if (requiredLevel == 0)
		{
			return -1;
		}
		
		return requiredLevel;
	}
	
	/**
	 * Retrieves the list of stat modifiers for this {@link TitleTemplate}.<br>
	 * This method returns all active effects applied to the title.
	 * @return a {@code List} of {@link StatFunction} objects or {@code null} if no modifiers exist.
	 */
	public List<StatFunction> getModifiers()
	{
		if (modifiers != null)
		{
			return modifiers.getModifiers();
		}
		
		return null;
	}
	
	/**
	 * Retrieves the list of actions associated with this item.<br>
	 * These actions define what happens when a player interacts with the item.
	 * @return the {@code ItemActions} object for this template.
	 */
	public ItemActions getActions()
	{
		return actions;
	}
	
	/**
	 * Retrieves the equipment type for this item.<br>
	 * This method checks specific properties of the {@code ItemTemplate}.<br>
	 * It handles special cases like Stigmas and Estimas.
	 * @return the {@link EquipType} associated with the item.
	 */
	public EquipType getEquipmentType()
	{
		return equipmentType;
	}
	
	/**
	 * Retrieves the cost of this bind point.<br>
	 * This value is stored as an {@code int}.
	 * @return The current price of the template.
	 */
	public int getPrice()
	{
		return price;
	}
	
	/**
	 * Retrieves the price of the item in Luna currency.<br>
	 * This value is used for trading and shop transactions.
	 * @return The current {@code int} price in Luna.
	 */
	public int getLunaPrice()
	{
		return lunaPrice;
	}
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the quality level of this item.<br>
	 * This value determines the rarity or grade of the object.
	 * @return The {@code ItemQuality} of the current template.
	 */
	public ItemQuality getItemQuality()
	{
		return itemQuality;
	}
	
	/**
	 * Retrieves the type of the item.<br>
	 * This method returns the {@code ItemType} associated with this template.
	 * @return The {@code ItemType} of the item.
	 */
	public ItemType getItemType()
	{
		return itemType;
	}
	
	/**
	 * Retrieves the specific type of weapon for this item.<br>
	 * This helps identify if the item is a sword, bow, or other weapon category.
	 * @return the {@code WeaponType} of the item.
	 */
	public WeaponType getWeaponType()
	{
		return weaponType;
	}
	
	/**
	 * Retrieves the specific type of armor for this item.<br>
	 * This method checks if the item is a plume, bracelet, or glyph first.<br>
	 * If it matches none of those, it returns the default {@code armorType}.
	 * @return the {@link ArmorType} associated with this item.
	 */
	public ArmorType getArmorType()
	{
		if (isPlume())
		{
			return ArmorType.PLUME;
		}
		
		if (isBracelet())
		{
			return ArmorType.ACCESSORY;
		}
		
		if (isGlyph())
		{
			return ArmorType.GLYPH;
		}
		
		return armorType;
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	@Override
	public int getNameId()
	{
		try
		{
			final int val = Integer.parseInt(description);
			return val;
		}
		catch (NumberFormatException nfe)
		{
			return 0;
		}
	}
	
	/**
	 * Retrieves the maximum number of items that can be stacked.<br>
	 * This method checks for specific currency caps like Kinah or Luna.<br>
	 * It returns {@code Long.MAX_VALUE} if no specific cap is defined.
	 * @return The maximum stack count as a {@code long}.
	 */
	public long getMaxStackCount()
	{
		if (isKinah())
		{
			if (CustomConfig.ENABLE_KINAH_CAP)
			{
				return CustomConfig.KINAH_CAP_VALUE;
			}
			
			return Long.MAX_VALUE;
		}
		
		if (isLuna())
		{
			if (LunaSystemConfig.ENABLE_LUNA_CAP)
			{
				return LunaSystemConfig.LUNA_CAP_VALUE;
			}
			
			return Long.MAX_VALUE;
		}
		
		return maxStackCount;
	}
	
	/**
	 * Retrieves the attack type associated with this item.<br>
	 * This value determines how the item's damage is categorized.
	 * @return the {@link ItemAttackType} of the attack.
	 */
	public ItemAttackType getAttackType()
	{
		return attackType;
	}
	
	/**
	 * Retrieves the attack gap value for this item.<br>
	 * This value determines the spacing between consecutive attacks.
	 * @return The {@code float} value of the attack gap.
	 */
	public float getAttackGap()
	{
		return attackGap;
	}
	
	/**
	 * Retrieves the bonus value for an option slot.<br>
	 * This value determines how much extra benefit a player receives from specific slots.
	 * @return The integer value of the option slot bonus.
	 */
	public int getOptionSlotBonus()
	{
		return optionSlotBonus;
	}
	
	/**
	 * Retrieves the bonus application status for this item.<br>
	 * This value determines how bonuses are applied to the player.
	 * @return The {@code String} representing the bonus application type.
	 */
	public String getBonusApply()
	{
		return bonusApply;
	}
	
	/**
	 * Checks if the item template prevents enchanting.<br>
	 * This method evaluates the {@code ItemMask} to see if the {@code NO_ENCHANT} flag is set.
	 * @return {@code true} if enchanting is disabled for this item, {@code false} otherwise.
	 */
	public boolean isNoEnchant()
	{
		return (getMask() & ItemMask.NO_ENCHANT) == ItemMask.NO_ENCHANT;
	}
	
	/**
	 * Checks if the item can be dyed.<br>
	 * This method verifies if the {@code ItemMask.DYEABLE} flag is set.
	 * @return {@code true} if dyeing is allowed, {@code false} otherwise.
	 */
	public boolean isItemDyePermitted()
	{
		return (getMask() & ItemMask.DYEABLE) == ItemMask.DYEABLE;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the weapon boost value for this item.<br>
	 * This value determines how much a weapon enhances its power.
	 * @return The integer value of the {@code weaponBoost}.
	 */
	public int getWeaponBoost()
	{
		return weaponBoost;
	}
	
	/**
	 * Checks if the item is classified as a weapon.<br>
	 * This method compares the {@code equipmentType} against {@code WEAPON}.
	 * @return {@code true} if the item is a weapon, {@code false} otherwise.
	 */
	public boolean isWeapon()
	{
		return equipmentType == EquipType.WEAPON;
	}
	
	/**
	 * Checks if the item belongs to the armor category.<br>
	 * This method compares the {@code equipmentType} against {@code ARMOR}.
	 * @return {@code true} if the item is armor, {@code false} otherwise.
	 */
	public boolean isArmor()
	{
		return equipmentType == EquipType.ARMOR;
	}
	
	/**
	 * Checks if the item belongs to a mana slot.<br>
	 * This method returns {@code true} if the category is {@code MANA_SLOT_OPEN}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the mana slot is open, {@code false} otherwise.
	 */
	public boolean isManaSlotOpen()
	{
		return category == ItemCategory.MANA_SLOT_OPEN;
	}
	
	/**
	 * Checks if the item belongs to an open grind slot.<br>
	 * This method compares the current {@link ItemCategory} against the {@code GRIND_SLOT_OPEN} value.
	 * @return {@code true} if the category is an open grind slot, {@code false} otherwise.
	 */
	public boolean isGrindSlotOpen()
	{
		return category == ItemCategory.GRIND_SLOT_OPEN;
	}
	
	/**
	 * Checks if the item belongs to the {@code ODIAN} category.<br>
	 * This method returns {@code true} if the category matches {@code ODIAN}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the item is an Odian; {@code false} otherwise.
	 */
	public boolean isOdian()
	{
		return category == ItemCategory.ODIAN;
	}
	
	/**
	 * Checks if the item belongs to the {@code RUNE} category.
	 * @return {@code true} if the item is a rune, otherwise {@code false}.
	 */
	public boolean isRune()
	{
		return category == ItemCategory.RUNE;
	}
	
	/**
	 * Checks if the item is a Kinah.<br>
	 * This method compares the current {@code itemId} against the value of {@code KINAH}.
	 * @return {@code true} if the item is a Kinah, otherwise {@code false}.
	 */
	public boolean isKinah()
	{
		return itemId == ItemId.KINAH.value();
	}
	
	/**
	 * Checks if the current item is a Luna.<br>
	 * This method compares the {@code itemId} against the value of {@code LUNA}.
	 * @return {@code true} if the item is a Luna, otherwise {@code false}.
	 */
	public boolean isLuna()
	{
		return itemId == ItemId.LUNA.value();
	}
	
	/**
	 * Checks if the item belongs to the stigma category.<br>
	 * This method returns the status of the {@code isStigma} flag.
	 * @return {@code true} if the item is a stigma, {@code false} otherwise.
	 */
	public boolean isStigma()
	{
		return category == ItemCategory.STIGMA; // return itemId > 140001101 && itemId < 140001930;
	}
	
	/**
	 * Checks if the item belongs to the grind enchant category.<br>
	 * This method compares the current {@code category} against {@code GRIND_ENCHANT}.
	 * @return {@code true} if it is a grind enchant, {@code false} otherwise.
	 */
	public boolean isGrindEnchant()
	{
		return category == ItemCategory.GRIND_ENCHANT;
	}
	
	/**
	 * Checks if the item name indicates it is in a damaged state.<br>
	 * This method returns {@code true} if the name ends with {@code (damaged)}.
	 * @return {@code true} if the item is damaged, otherwise {@code false}.
	 */
	public boolean isInertStigma()
	{
		return name.endsWith("(damaged)");
	}
	
	/**
	 * Checks if the item template is invisible.<br>
	 * This method returns {@code true} only for a specific internal ID.
	 * @return {@code true} if the item is invisible, otherwise {@code false}.
	 */
	public boolean isTransformInvisible()
	{
		return itemId == 190099001;
	}
	
	/**
	 * Checks if the item is a transformation object.<br>
	 * This method returns {@code true} if the item ID matches the specific transform ID.
	 * @return {@code true} if this is a transformation item, {@code false} otherwise.
	 */
	public boolean isTransform()
	{
		return itemId == 190099000;
	}
	
	/**
	 * Checks if the item belongs to the {@code PLUME} category.<br>
	 * This method returns {@code true} if the item is a plume.
	 * @return {@code true} if it is a plume, {@code false} otherwise.
	 */
	public boolean isPlume()
	{
		return category == ItemCategory.PLUME; // return itemId >= 187100015 && itemId <= 187100018;
	}
	
	/**
	 * Checks if the item belongs to the bracelet category.<br>
	 * This method compares the current {@code category} against {@code ItemCategory.BRACELET}.
	 * @return {@code true} if the item is a bracelet, otherwise {@code false}.
	 */
	public boolean isBracelet()
	{
		return category == ItemCategory.BRACELET;
	}
	
	/**
	 * Checks if the item belongs to the {@code ESTIMA} category.<br>
	 * This method returns {@code true} if the category matches {@code ESTIMA}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if this is an Estima item, {@code false} otherwise.
	 */
	public boolean isEstima()
	{
		return category == ItemCategory.ESTIMA;
	}
	
	/**
	 * Checks if the item belongs to the glyph category.<br>
	 * This method compares the current category against {@code ItemCategory.GLYPH}.
	 * @return {@code true} if the item is a glyph, otherwise {@code false}.
	 */
	public boolean isGlyph()
	{
		return category == ItemCategory.GLYPH;
	}
	
	/**
	 * Checks if the item belongs to the glyph enchantment category.<br>
	 * This method compares the current category against {@code ItemCategory.GLYPH_ENCHANT}.
	 * @return {@code true} if the item is a glyph enchant, {@code false} otherwise.
	 */
	public boolean isGlyphEnchant()
	{
		return category == ItemCategory.GLYPH_ENCHANT;
	}
	
	/**
	 * Sets the unique identifier for the item.<br>
	 * This method updates the {@code itemId} field.
	 * @param itemId The new ID to assign to the item.
	 */
	public void setItemId(int itemId)
	{
		this.itemId = itemId;
	}
	
	/**
	 * Checks if the item is a minion ticket.
	 * @return {@code true} if it is a minion ticket, {@code false} otherwise.
	 */
	public boolean getMinionTicket()
	{
		return minion_ticket;
	}
	
	/**
	 * Checks if the item is a cash contract.<br>
	 * This method returns {@code true} if the item is marked as a cash contract.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if it is a cash contract, {@code false} otherwise.
	 */
	public boolean isMinionCashContract()
	{
		return is_cash_contract;
	}
	
	/**
	 * Retrieves the list of minions associated with this item.
	 * @return The {@code int} value representing the minion list.
	 */
	public int getMinionList()
	{
		return minionList;
	}
	
	/**
	 * Retrieves the {@link ItemSetTemplate} associated with this item.<br>
	 * It uses the unique {@code itemId} to look up data in the {@code DataManager}.
	 * @return The {@link ItemSetTemplate} for this item, or {@code null} if not found.
	 */
	public ItemSetTemplate getItemSet()
	{
		return DataManager.ITEM_SET_DATA.getItemSetTemplateByItemId(itemId);
	}
	
	/**
	 * Checks if the current item belongs to an {@link ItemSetTemplate}.<br>
	 * This method determines if the item provides specific set bonuses.
	 * @return {@code true} if the item is part of a set, otherwise {@code false}.
	 */
	public boolean isItemSet()
	{
		return getItemSet() != null;
	}
	
	/**
	 * Retrieves the information associated with a Godstone.<br>
	 * This method returns the {@code godstoneInfo} object for this item template.
	 * @return The {@link GodstoneInfo} data.
	 */
	public GodstoneInfo getGodstoneInfo()
	{
		return godstoneInfo;
	}
	
	/**
	 * Retrieves the display name of the item.<br>
	 * This method returns the {@code name} field associated with this object.<br>
	 * If the name is {@code null}, it returns an empty {@code String}.
	 * @return The name of the item as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return name != null ? name : "";
	}
	
	/**
	 * Retrieves the unique identifier for this item template.<br>
	 * This value corresponds to the {@code itemId} field.
	 * @return The unique template ID as an {@code int}.
	 */
	@Override
	public int getTemplateId()
	{
		return itemId;
	}
	
	/**
	 * Retrieves the unique identifier for the world object.<br>
	 * This ID is used to identify the item in the game world.
	 * @return The {@code int} value of the world ID.
	 */
	public int getReturnWorldId()
	{
		return returnWorldId;
	}
	
	/**
	 * Retrieves the alias for the return value.<br>
	 * This is used to identify the specific return type of an action.
	 * @return The {@code String} representing the return alias.
	 */
	public String getReturnAlias()
	{
		return returnAlias;
	}
	
	/**
	 * Retrieves the {@code Stigma} associated with this item.
	 * @return the {@code Stigma} object.
	 */
	public Stigma getStigma()
	{
		return stigma;
	}
	
	/**
	 * Retrieves the number of available slots for Manastones.<br>
	 * This value represents how many stones can be equipped on this item.
	 * @return The total count of {@code manastoneSlots}.
	 */
	public int getManastoneSlots()
	{
		return manastoneSlots;
	}
	
	/**
	 * Retrieves the number of special slots for this item.<br>
	 * This value represents specific slot configurations defined in the template.
	 * @return The total count of {@code specialSlots}.
	 */
	public int getSpecialSlots()
	{
		return specialSlots;
	}
	
	/**
	 * Retrieves the maximum enchantment level for this item.<br>
	 * This value defines the upper limit of how many times an item can be enchanted.
	 * @return The maximum enchantment level as an {@code int}.
	 */
	public int getMaxEnchantLevel()
	{
		return maxEnchant;
	}
	
	/**
	 * Retrieves the maximum enchantment bonus for this item.<br>
	 * This value represents the highest possible bonus a player can achieve.
	 * @return The maximum enchantment bonus as an {@code int}.
	 */
	public int getMaxEnchantBonus()
	{
		return max_enchant_bonus;
	}
	
	/**
	 * Checks if the item is restricted to a single instance.<br>
	 * This method evaluates the {@code getMask} against the {@code ItemMask.LIMIT_ONE} flag.
	 * @return {@code true} if the item has a limit of one, {@code false} otherwise.
	 */
	public boolean hasLimitOne()
	{
		return (getMask() & ItemMask.LIMIT_ONE) == ItemMask.LIMIT_ONE;
	}
	
	/**
	 * Checks if the item can be traded by players.<br>
	 * This method evaluates the {@code getMask} against the {@code ItemMask.TRADEABLE} flag.
	 * @return {@code true} if the item is tradeable, {@code false} otherwise.
	 */
	public boolean isTradeable()
	{
		return (getMask() & ItemMask.TRADEABLE) == ItemMask.TRADEABLE;
	}
	
	/**
	 * Checks if the item can be used to create a composite weapon.<br>
	 * This method evaluates the {@code getMask} against the {@code ItemMask.CAN_COMPOSITE_WEAPON} flag.
	 * @return {@code true} if the item is allowed to be fused into a composite weapon, {@code false} otherwise.
	 */
	public boolean isCanFuse()
	{
		return (getMask() & ItemMask.CAN_COMPOSITE_WEAPON) == ItemMask.CAN_COMPOSITE_WEAPON;
	}
	
	/**
	 * Checks if the item can be split into multiple parts.<br>
	 * This method evaluates the {@code ItemMask} to see if the {@code CAN_SPLIT} flag is set.
	 * @return {@code true} if the item is splittable, {@code false} otherwise.
	 */
	public boolean canExtract()
	{
		return (getMask() & ItemMask.CAN_SPLIT) == ItemMask.CAN_SPLIT;
	}
	
	/**
	 * Checks if the item is bound to a specific soul.<br>
	 * Returns {@code true} if the item cannot be traded or shared.<br>
	 * Returns {@code false} if the item is freely tradable.
	 * @return the soul bound status of the item.
	 */
	public boolean isSoulBound()
	{
		return (getMask() & ItemMask.SOUL_BOUND) == ItemMask.SOUL_BOUND;
	}
	
	/**
	 * Checks if the item can be broken.<br>
	 * This method evaluates the {@code getMask} against the {@code ItemMask.BREAKABLE} flag.
	 * @return {@code true} if the item is breakable, otherwise {@code false}.
	 */
	public boolean isBreakable()
	{
		return (getMask() & ItemMask.BREAKABLE) == ItemMask.BREAKABLE;
	}
	
	/**
	 * Checks if the item can be deleted.<br>
	 * This method verifies the {@code ItemMask} for the {@code DELETABLE} flag.
	 * @return {@code true} if the item is deletable, {@code false} otherwise.
	 */
	public boolean isDeletable()
	{
		return (getMask() & ItemMask.DELETABLE) == ItemMask.DELETABLE;
	}
	
	/**
	 * Checks if the item can be polished.<br>
	 * This method verifies the {@code ItemMask} for the {@code CAN_POLISH} flag.
	 * @return {@code true} if polishing is allowed, {@code false} otherwise.
	 */
	public boolean isCanPolish()
	{
		return (getMask() & ItemMask.CAN_POLISH) == ItemMask.CAN_POLISH;
	}
	
	/**
	 * Checks if the item is a two-handed weapon.<br>
	 * This method first verifies if the item is a weapon using {@code isWeapon}.<br>
	 * It then checks if the required slots for the weapon type equal {@code 2}.
	 * @return {@code true} if it is a two-handed weapon, {@code false} otherwise.
	 */
	public boolean isTwoHandWeapon()
	{
		if (!isWeapon())
		{
			return false;
		}
		
		return weaponType.getRequiredSlots() == 2 ? true : false;
	}
	
	/**
	 * Retrieves the temporary exchange time for this item.<br>
	 * This value represents how long an exchange remains valid.
	 * @return The exchange time as an {@code int}.
	 */
	public int getTempExchangeTime()
	{
		return temExchangeTime;
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
	 * Retrieves the expiration time of this object.<br>
	 * This value represents when the object will expire.
	 * @return The expiration time as an {@code int}.
	 */
	public int getExpireTime()
	{
		return expireTime;
	}
	
	/**
	 * Retrieves the statistics for this weapon.<br>
	 * This method returns a {@code WeaponStats} object containing specific data.
	 * @return the {@code WeaponStats} associated with this item.
	 */
	public WeaponStats getWeaponStats()
	{
		return weaponStats;
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
	 * Retrieves the unique identifier for the extra inventory.<br>
	 * Returns {@code -1} if no extra inventory is assigned.
	 * @return The {@code int} ID of the extra inventory or {@code -1}.
	 */
	public int getExtraInventoryId()
	{
		if (extraInventory == null)
		{
			return -1;
		}
		
		return extraInventory.getId();
	}
	
	/**
	 * Updates the item mask based on the provided filter.<br>
	 * This method modifies the internal state of the {@code ItemTemplate}.<br>
	 * It adds the filter if {@code apply} is {@code true}.<br>
	 * It removes the filter if {@code apply} is {@code false}.
	 * @param apply A boolean indicating whether to add or remove the mask.
	 * @param filter The bitmask value to be applied to the current mask.
	 */
	public void modifyMask(boolean apply, int filter)
	{
		if (apply)
		{
			mask |= filter;
		}
		else
		{
			mask &= ~filter;
		}
	}
	
	/**
	 * Checks if this item can be stacked in the inventory.<br>
	 * It returns {@code true} if the maximum stack count is greater than {@code 1}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the item is stackable, {@code false} otherwise.
	 */
	public boolean isStackable()
	{
		return maxStackCount > 1;
	}
	
	/**
	 * Checks if the item has a specific area restriction.<br>
	 * This method returns {@code true} if an area limit is defined.<br>
	 * It returns {@code false} if there are no restrictions.
	 * @return {@code true} if restricted, {@code false} otherwise.
	 */
	public boolean hasAreaRestriction()
	{
		return useLimits.getUseArea() != null;
	}
	
	/**
	 * Retrieves the area where this item can be used.<br>
	 * This method returns a {@link ZoneName} object.
	 * @return the {@code ZoneName} of the allowed use area.
	 */
	public ZoneName getUseArea()
	{
		return useLimits.getUseArea();
	}
	
	/**
	 * Retrieves the list of items associated with this template.<br>
	 * This method returns the {@code tradeinList} field.
	 * @return a {@link TradeinList} containing the trade-in data.
	 */
	public TradeinList getTradeinList()
	{
		return tradeinList;
	}
	
	/**
	 * Retrieves the {@code Acquisition} data for this item.<br>
	 * This method returns the information regarding how the item is obtained.
	 * @return the {@code Acquisition} object associated with this template.
	 */
	public Acquisition getAcquisition()
	{
		return acquisition;
	}
	
	/**
	 * Retrieves a random bonus identifier from the template.<br>
	 * This value is used to determine which specific bonus is applied.
	 * @return The {@code int} value of the random bonus.
	 */
	public int getRandomBonusId()
	{
		return rnd_bonus;
	}
	
	/**
	 * Retrieves the actual random bonus value for this item.<br>
	 * This value is used to calculate randomized effects.
	 * @return The {@code int} value of the real random bonus.
	 */
	public int getRealRndBonus()
	{
		return real_rnd_bonus;
	}
	
	/**
	 * Retrieves a random count for the bonus. <br>
	 * This value is used to determine how many bonuses are applied.
	 * @return The randomly generated {@code int} count.
	 */
	public int getRandomBonusCount()
	{
		return rnd_count;
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
		return improvement;
	}
	
	/**
	 * Retrieves the usage limits for this item.<br>
	 * This method returns the {@code ItemUseLimits} object associated with the template.
	 * @return the {@code ItemUseLimits} of the current item.
	 */
	public ItemUseLimits getUseLimits()
	{
		return useLimits;
	}
	
	/**
	 * Retrieves the current disposition of the item.<br>
	 * This value indicates how the item behaves or its status in the game world.
	 * @return The {@code Disposition} of this item.
	 */
	public Disposition getDisposition()
	{
		return disposition;
	}
	
	/**
	 * Retrieves the ownership world limit for this item.<br>
	 * This value determines which world owns the item limits.
	 * @return The integer representing the ownership world.
	 */
	public int getOwnershipWorld()
	{
		return useLimits.getOwnershipWorld();
	}
	
	/**
	 * Checks if the item is classified as cloth armor.<br>
	 * This method verifies that the {@code armorType} is not {@code ARROW}.<br>
	 * It also ensures the {@code equipmentType} is set to {@code ARMOR}.
	 * @return {@code true} if the item is cloth, otherwise {@code false}.
	 */
	public boolean isCloth()
	{
		return (armorType != null) && (armorType != ArmorType.ARROW) && (equipmentType == EquipType.ARMOR);
	}
	
	/**
	 * Checks if the item is used for quest updates.<br>
	 * This method returns {@code true} if the item triggers a quest progression.
	 * @return {@code true} if it is a quest update item, {@code false} otherwise.
	 */
	public boolean isQuestUpdateItem()
	{
		return isQuestUpdateItem;
	}
	
	/**
	 * Sets whether this item is used to update a quest.<br>
	 * This updates the {@code isQuestUpdateItem} field.
	 * @param value The boolean value to set for quest update status.
	 */
	public void setQuestUpdateItem(boolean value)
	{
		isQuestUpdateItem = value;
	}
	
	/**
	 * Retrieves the action associated with an Idian.<br>
	 * This method returns the {@code IdianAction} for this item template.
	 * @return the {@code IdianAction} object.
	 */
	public Idian getIdianAction()
	{
		return idianAction;
	}
	
	/**
	 * Checks if the item belongs to the combination category.<br>
	 * This method returns {@code true} if the item is a combination type.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the item is a combination item, {@code false} otherwise.
	 */
	public boolean isCombinationItem()
	{
		return category == ItemCategory.COMBINATION;
	}
	
	/**
	 * Checks if the item belongs to the enchantment category.<br>
	 * This method compares the current category against {@code ItemCategory.ENCHANTMENT}.
	 * @return {@code true} if the item is an enchantment stone, otherwise {@code false}.
	 */
	public boolean isEnchantmentStone()
	{
		return category == ItemCategory.ENCHANTMENT;
	}
	
	/**
	 * Checks if the item belongs to an accessory category.<br>
	 * This includes items like rings, necklaces, and belts.
	 * @return {@code true} if the item is an accessory, {@code false} otherwise.
	 */
	public boolean isAccessory()
	{
		return (category == ItemCategory.EARRINGS) || (category == ItemCategory.RINGS) || (category == ItemCategory.NECKLACE) || (category == ItemCategory.PLUME) || (category == ItemCategory.BRACELET) || (category == ItemCategory.BELT) || (category == ItemCategory.HELMET);
	}
	
	/**
	 * Checks if the item belongs to an accessory category.<br>
	 * This includes rings, necklaces, earrings, and belts.
	 * @return {@code true} if the item is an accessory, {@code false} otherwise.
	 */
	public boolean isOdianAccessory()
	{
		return (category == ItemCategory.EARRINGS) || (category == ItemCategory.RINGS) || (category == ItemCategory.NECKLACE) || (category == ItemCategory.BELT);
	}
	
	/**
	 * Checks if the item belongs to a rune accessory category.<br>
	 * This includes bracelets, plumes, and wing armor types.
	 * @return {@code true} if the item is a rune accessory, {@code false} otherwise.
	 */
	public boolean isRuneAccessory()
	{
		return (category == ItemCategory.BRACELET) || (category == ItemCategory.PLUME) || (armorType == ArmorType.WING);
	}
	
	/**
	 * Retrieves the maximum authorization value for this item.<br>
	 * This value is used to check permission limits.
	 * @return The maximum authorization as an {@code int}.
	 */
	public int getMaxAuthorize()
	{
		return max_authorize;
	}
	
	/**
	 * Retrieves the authorization name for this item.<br>
	 * This value identifies the specific permission required to use or interact with the object.
	 * @return The {@code int} value representing the authorization name.
	 */
	public int getAuthorizeName()
	{
		return authorize_name;
	}
	
	/**
	 * Retrieves the authorization condition for this item.<br>
	 * This value determines if a player meets the requirements to use or own the item.
	 * @return The integer value of the {@code authorize_condition}.
	 */
	public int getAuthorizeCondition()
	{
		return authorize_condition;
	}
	
	/**
	 * Checks if the item is restricted to oversea servers.<br>
	 * This value determines regional availability for the item.
	 * @return the {@code int} value representing the oversea only status.
	 */
	public int getOverseaOnly()
	{
		return oversea_only;
	}
	
	/**
	 * Checks if the item can exceed the normal enchantment limit.<br>
	 * This returns {@code true} if the limit is bypassed.
	 * @return {@code true} if exceeding enchantments is allowed, {@code false} otherwise.
	 */
	public boolean getExceedEnchant()
	{
		return exceedEnchant;
	}
	
	/**
	 * Retrieves the set of skills associated with enchanting.<br>
	 * This method returns the {@code enchantSkillSet} string.
	 * @return The string representing the enchantment skill set.
	 */
	public String getEnchantSkillSet()
	{
		return enchantSkillSet;
	}
	
	/**
	 * Retrieves the unique identifier for the robot.<br>
	 * This value is used to identify specific automated entities.
	 * @return The {@code int} ID of the robot.
	 */
	public int getRobotId()
	{
		return robot_id;
	}
	
	/**
	 * Retrieves the description of the item.<br>
	 * This value is stored in the {@code name_desc} attribute.
	 * @return The description string for this item template.
	 */
	public String getNamedesc()
	{
		return namedesc;
	}
	
	/**
	 * Retrieves the skill enchantment value for this item.<br>
	 * This value determines the specific skill enhancement applied to the object.
	 * @return The integer value of the {@code skill_enchant}.
	 */
	public int getSkillEnchant()
	{
		return skill_enchant;
	}
	
	/**
	 * Retrieves the skill enchantment value for this item.<br>
	 * This value determines how much a skill is enhanced by the item.
	 * @return The integer value of the skill enchantment.
	 */
	public int getSkillEnhance()
	{
		return skill_enchant;
	}
	
	/**
	 * Retrieves the enchantment type of this item.<br>
	 * This method returns the {@code EnchantType} associated with the template.
	 * @return The {@code EnchantType} of the item.
	 */
	public EnchantType getEnchantType()
	{
		return enchantType;
	}
	
	/**
	 * Retrieves the skill value associated with the item skin.<br>
	 * This value determines which specific skill is linked to the visual effect.
	 * @return The integer value of the skin skill.
	 */
	public int getSkinSkill()
	{
		return skin_skill;
	}
	
	/**
	 * Retrieves the maximum number of slots available for options.<br>
	 * This value is used to determine how many extra modifiers can be added.
	 * @return The maximum slot count as an {@code int}.
	 */
	public int getMaxSlot()
	{
		return option_slot_max;
	}
	
	/**
	 * Retrieves the total count of additional option slots for this item.<br>
	 * This value determines how many extra options can be added to the template.
	 * @return The number of additional option slots as an {@code int}.
	 */
	public int getOptionSlotAddCount()
	{
		return option_slot_add_count;
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
	 * Retrieves the cost to open a grind slot.<br>
	 * This value is used for processing currency transactions.
	 * @return The integer cost of opening a grind slot.
	 */
	public int getGrindSlotOpeningCost()
	{
		return grind_slot_opening_cost;
	}
	
	/**
	 * Retrieves the current grind slot for this item.<br>
	 * This value determines which specific slot the item occupies during grinding.
	 * @return The {@code GrindSlot} associated with this template.
	 */
	public GrindSlot getGrindSlot()
	{
		return grindSlot;
	}
	
	/**
	 * Retrieves the quality level of the grinding item.<br>
	 * This value determines how effective the item is for gathering resources.
	 * @return The {@code int} value representing the grind quality.
	 */
	public int getGrindQuality()
	{
		return grindQuality;
	}
	
	/**
	 * Retrieves the current grind tier for this item.<br>
	 * This value determines the difficulty level of the item's content.
	 * @return the {@code int</sup> representing the grind tier.
	 */
	@SuppressWarnings("javadoc")
	public int getGrindTier()
	{
		return grindTier;
	}
	
	/**
	 * Retrieves the unique identifier for the Odian skill.<br>
	 * This value is used to link the item to a specific skill in the system.
	 * @return the {@code int} ID of the Odian skill.
	 */
	public int getOdianSkillId()
	{
		return odianSkillId;
	}
	
	/**
	 * Retrieves the current level of the Odian skill.<br>
	 * This value represents the specific skill progression for this item.
	 * @return The integer value of the {@code odianSkillLevel}.
	 */
	public int getOdianSkillLevel()
	{
		return odianSkillLevel;
	}
	
	/**
	 * Retrieves the unique identifier for the run transform table.<br>
	 * This ID is used to link the item to its specific transformation data.
	 * @return The {@code int} value of the run transform table ID.
	 */
	public int getRunTransformTableId()
	{
		return runTransformTableId;
	}
	
	/**
	 * Retrieves the list of transformations associated with this item.<br>
	 * This method returns the internal {@code transformList} field.
	 * @return The list of transformations as an {@code int}.
	 */
	public int getTransformList()
	{
		return transformList;
	}
}
