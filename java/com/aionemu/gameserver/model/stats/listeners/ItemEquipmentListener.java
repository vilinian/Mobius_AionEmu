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
package com.aionemu.gameserver.model.stats.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.IdianStone;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.RandomStats;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.stats.container.CreatureGameStats;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.WeaponStats;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.model.templates.itemset.FullBonus;
import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;
import com.aionemu.gameserver.model.templates.itemset.PartBonus;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.StigmaService;
import com.aionemu.gameserver.services.enchant.EnchantService;

/**
 * This class listens for changes in a {@link Player}'s equipment to update their statistics.<br>
 * It handles the calculation of bonuses from items, sets, and enchantments.<br>
 * It ensures that character stats are correctly recalculated whenever an item is equipped or removed.
 * @author xavier modified by Wakizashi rework Phantom_KNA
 */
public class ItemEquipmentListener
{
	/**
	 * Handles the logic for when a player equips an {@code Item}.<br>
	 * This method updates stats and applies effects from stones and skills.<br>
	 * It also triggers item set recalculations and enchantment checks.
	 * @param item The {@code Item} being equipped by the player.
	 * @param owner The {@link Player} who is equipping the item.
	 */
	public static void onItemEquipment(Item item, Player owner)
	{
		owner.getController().cancelUseItem();
		final ItemTemplate itemTemplate = item.getItemTemplate();
		
		onItemEquipment(item, owner.getGameStats(), owner);
		
		// Check if belongs to ItemSet
		if (itemTemplate.isItemSet())
		{
			recalculateItemSet(itemTemplate.getItemSet(), owner, item.getItemTemplate().isWeapon());
		}
		
		if (item.hasManaStones())
		{
			addStonesStats(item, item.getItemStones(), owner.getGameStats());
		}
		
		if (item.hasFusionStones())
		{
			addStonesStats(item, item.getFusionStones(), owner.getGameStats());
		}
		
		final IdianStone idianStone = item.getIdianStone();
		if (idianStone != null)
		{
			idianStone.onEquip(owner);
		}
		
		addGodstoneEffect(owner, item);
		final RandomStats randomStats = item.getRandomStats();
		if (randomStats != null)
		{
			randomStats.onEquip(owner);
		}
		
		if (item.getConditioningInfo() != null)
		{
			owner.getObserveController().addObserver(item.getConditioningInfo());
			item.getConditioningInfo().setPlayer(owner);
		}
		
		if (item.getAmplificationSkill() > 0)
		{
			owner.getSkillList().addSkill(owner, item.getAmplificationSkill(), 1);
		}
		
		if (item.getItemTemplate().isStigma())
		{
			StigmaService.recheckHiddenStigma(owner);
		}
		
		if (item.getItemSkinSkill() > 0)
		{
			owner.getSkillList().addSkill(owner, item.getItemSkinSkill(), 1);
		}
		
		EnchantService.onItemEquip(owner, item);
		EnchantService.getGloryShield(owner);
	}
	
	/**
	 * Removes an item from the player's equipment.<br>
	 * This method updates the player stats by removing all bonuses associated with the {@code item}.<br>
	 * It handles item sets, stones, and special effects like godstones or skills.
	 * @param item The {@code Item} being removed from the character.
	 * @param owner The {@link Player} who is currently unequipping the item.
	 */
	public static void onItemUnequipment(Item item, Player owner)
	{
		owner.getController().cancelUseItem();
		
		final ItemTemplate itemTemplate = item.getItemTemplate();
		
		// Check if belongs to ItemSet
		if (itemTemplate.isItemSet())
		{
			recalculateItemSet(itemTemplate.getItemSet(), owner, item.getItemTemplate().isWeapon());
		}
		
		owner.getGameStats().endEffect(item);
		
		if (item.hasManaStones())
		{
			removeStoneStats(item.getItemStones(), owner.getGameStats());
		}
		
		if (item.hasFusionStones())
		{
			removeStoneStats(item.getFusionStones(), owner.getGameStats());
		}
		
		if (item.getConditioningInfo() != null)
		{
			owner.getObserveController().removeObserver(item.getConditioningInfo());
			item.getConditioningInfo().setPlayer(null);
		}
		
		final IdianStone idianStone = item.getIdianStone();
		if (idianStone != null)
		{
			idianStone.onUnEquip(owner);
		}
		
		removeGodstoneEffect(owner, item);
		final RandomStats randomStats = item.getRandomStats();
		if (randomStats != null)
		{
			randomStats.onUnEquip(owner);
		}
		
		if (item.isAmplified() && (item.getEnchantOrAuthorizeLevel() >= 20))
		{
			SkillLearnService.removeSkill(owner, item.getAmplificationSkill());
		}
		
		if (item.getItemTemplate().isStigma())
		{
			StigmaService.recheckHiddenStigma(owner);
		}
		
		if (item.getItemSkinSkill() > 0)
		{
			if (owner.getSkillList().isSkillPresent(item.getItemSkinSkill()))
			{
				SkillLearnService.removeSkill(owner, item.getItemSkinSkill());
			}
		}
		
		EnchantService.getGloryShield(owner);
	}
	
	/**
	 * Processes the statistics for an item when it is equipped.<br>
	 * This method calculates modifiers based on the item slot and fusion status.<br>
	 * It updates the {@code Item} object and applies effects to the {@code CreatureGameStats}.
	 * @param item The {@link Item} being equipped.
	 * @param cgs The {@link CreatureGameStats} container for the character.
	 * @param player The {@link Player} who owns the item.
	 */
	private static void onItemEquipment(Item item, CreatureGameStats<?> cgs, Player player)
	{
		final ItemTemplate itemTemplate = item.getItemTemplate();
		final long slot = item.getEquipmentSlot();
		final List<StatFunction> modifiers = itemTemplate.getModifiers();
		if (modifiers == null)
		{
			return;
		}
		
		List<StatFunction> allModifiers = null;
		// List<StatFunction> decreaseAllModifiers = null;
		
		if ((slot & ItemSlot.MAIN_OR_SUB.getSlotIdMask()) != 0)
		{
			allModifiers = wrapModifiers(item, modifiers);
			if (item.hasFusionedItem())
			{
				// add all bonus modifiers according to rules
				final ItemTemplate fusionedItemTemplate = item.getFusionedItemTemplate();
				final WeaponType weaponType = fusionedItemTemplate.getWeaponType();
				final List<StatFunction> fusionedItemModifiers = fusionedItemTemplate.getModifiers();
				if (fusionedItemModifiers != null)
				{
					allModifiers.addAll(wrapModifiers(item, fusionedItemModifiers));
				}
				
				// add 10% of Magic Boost and Attack
				final WeaponStats weaponStats = fusionedItemTemplate.getWeaponStats();
				if (weaponStats != null)
				{
					final int boostMagicalSkill = Math.round(0.1f * weaponStats.getBoostMagicalSkill());
					final int attack = Math.round(0.1f * weaponStats.getMeanDamage());
					if ((weaponType == WeaponType.ORB_2H) || (weaponType == WeaponType.BOOK_2H) || (weaponType == WeaponType.GUN_1H) || (weaponType == WeaponType.CANNON_2H) || (weaponType == WeaponType.HARP_2H) || (weaponType == WeaponType.KEYBLADE_2H) || (weaponType == WeaponType.SPRAY_2H))
					{
						allModifiers.add(new StatAddFunction(StatEnum.MAGICAL_ATTACK, attack, false));
						allModifiers.add(new StatAddFunction(StatEnum.BOOST_MAGICAL_SKILL, boostMagicalSkill, false));
					}
					else
					{
						allModifiers.add(new StatAddFunction(StatEnum.MAIN_HAND_POWER, attack, false));
					}
				}
			}
		}
		else
		{
			allModifiers = modifiers;
		}
		
		item.setCurrentModifiers(allModifiers);
		cgs.addEffect(item, allModifiers);
	}
	
	/**
	 * Filters out specific unwanted modifiers from a list.<br>
	 * It removes certain stats like {@code ATTACK_SPEED} and {@code BOOST_CASTING_TIME}.<br>
	 * The method returns a new list containing only the valid {@link StatFunction} objects.
	 * @param item The {@code Item} being processed.
	 * @param modifiers A list of {@link StatFunction} to be filtered.
	 * @return A new {@code List} of filtered {@link StatFunction} objects.
	 */
	private static List<StatFunction> wrapModifiers(Item item, List<StatFunction> modifiers)
	{
		final List<StatFunction> allModifiers = new ArrayList<>();
		for (StatFunction modifier : modifiers)
		{
			switch (modifier.getName())
			{
				// why they are removed look at DuplicateStatFunction
				case ATTACK_SPEED:
				case PVP_ATTACK_RATIO:
				case PVP_DEFEND_RATIO:
				case BOOST_CASTING_TIME:
					continue;
				default:
					allModifiers.add(modifier);
			}
		}
		
		return allModifiers;
	}
	
	/**
	 * Updates the item set bonuses for a specific player.<br>
	 * This method checks how many parts of an {@code ItemSetTemplate} are equipped.<br>
	 * It adds part bonuses and the full bonus to the player's stats if requirements are met.
	 * @param itemSetTemplate The template containing the item set data.
	 * @param player The player whose stats need updating.
	 * @param isWeapon A boolean indicating if the current calculation involves a weapon.
	 */
	private static void recalculateItemSet(ItemSetTemplate itemSetTemplate, Player player, boolean isWeapon)
	{
		if (itemSetTemplate == null)
		{
			return;
		}
		
		// 1.- Check equipment for items already equip with this itemSetTemplate id
		final int itemSetPartsEquipped = player.getEquipment().itemSetPartsEquipped(itemSetTemplate.getId());
		
		if (itemSetTemplate.getFullbonus() != null)
		{
			if (itemSetPartsEquipped > itemSetTemplate.getFullbonus().getCount())
			{
				// DO NOT REMOVE
				return;
			}
		}
		
		player.getGameStats().endEffect(itemSetTemplate);
		
		// If main hand and off hand is same , no bonus
		int mainHandItemId = 0;
		int offHandItemId = 0;
		if (player.getEquipment().getMainHandWeapon() != null)
		{
			mainHandItemId = player.getEquipment().getMainHandWeapon().getItemId();
		}
		
		if (player.getEquipment().getOffHandWeapon() != null)
		{
			offHandItemId = player.getEquipment().getOffHandWeapon().getItemId();
		}
		
		final boolean mainAndOffNotSame = mainHandItemId != offHandItemId;
		
		// 2.- Check Item Set Parts and add effects one by one if not done already
		for (PartBonus itempartbonus : itemSetTemplate.getPartbonus())
		{
			if (mainAndOffNotSame && isWeapon)
			{
				// If the partbonus was not applied before, do it now
				if (itempartbonus.getCount() <= itemSetPartsEquipped)
				{
					player.getGameStats().addEffect(itemSetTemplate, itempartbonus.getModifiers());
				}
			}
			else if (!isWeapon)
			{
				// If the partbonus was not applied before, do it now
				if (itempartbonus.getCount() <= itemSetPartsEquipped)
				{
					if (itempartbonus.getModifiers() != null)
					{
						player.getGameStats().addEffect(itemSetTemplate, itempartbonus.getModifiers());
					}
				}
			}
		}
		
		// 3.- Finally check if all items are applied and set the full bonus if not already applied
		final FullBonus fullbonus = itemSetTemplate.getFullbonus();
		if ((fullbonus != null) && (itemSetPartsEquipped == fullbonus.getCount()))
		{
			// Add the full bonus with index equal to total parts plus one to avoid confusion with the part bonus equal to the number of objects.
			player.getGameStats().addEffect(itemSetTemplate, fullbonus.getModifiers());
		}
	}
	
	/**
	 * Adds the statistics from a set of {@link ManaStone} objects to an item.<br>
	 * This method iterates through all stones and applies their effects to the provided {@code CreatureGameStats}.<br>
	 * It returns early if the stone set is {@code null} or empty.
	 * @param item The {@link Item} that contains the stones.
	 * @param itemStones A set of {@link ManaStone} objects to process.
	 * @param cgs The {@link CreatureGameStats} container where stats will be added.
	 */
	private static void addStonesStats(Item item, Set<? extends ManaStone> itemStones, CreatureGameStats<?> cgs)
	{
		if ((itemStones == null) || (itemStones.size() == 0))
		{
			return;
		}
		
		for (ManaStone stone : itemStones)
		{
			addStoneStats(item, stone, cgs);
		}
	}
	
	/**
	 * Adds the statistics of a {@link ManaStone} to a creature's stats.<br>
	 * This method retrieves the modifiers from the stone and applies them to the provided {@code CreatureGameStats}.<br>
	 * If the stone has no modifiers, the method returns without making changes.
	 * @param item The {@link Item} that contains the stone.
	 * @param stone The {@link ManaStone} whose stats need to be added.
	 * @param cgs The {@link CreatureGameStats} container where effects are applied.
	 */
	public static void addStoneStats(Item item, ManaStone stone, CreatureGameStats<?> cgs)
	{
		final List<StatFunction> modifiers = stone.getModifiers();
		if (modifiers == null)
		{
			return;
		}
		
		cgs.addEffect(stone, modifiers);
	}
	
	/**
	 * Removes the statistics provided by a set of {@link ManaStone} objects.<br>
	 * This method updates the {@code CreatureGameStats<?>} container by ending all active effects from the stones.<br>
	 * It returns early if the provided set is {@code null} or empty.
	 * @param itemStones The set of stones to remove stats from.
	 * @param cgs The game statistics container to update.
	 */
	public static void removeStoneStats(Set<? extends ManaStone> itemStones, CreatureGameStats<?> cgs)
	{
		if ((itemStones == null) || (itemStones.size() == 0))
		{
			return;
		}
		
		for (ManaStone stone : itemStones)
		{
			final List<StatFunction> modifiers = stone.getModifiers();
			if (modifiers != null)
			{
				cgs.endEffect(stone);
			}
		}
	}
	
	/**
	 * Applies the effects of a {@code GodStone} to a player.<br>
	 * This method checks if the provided {@code Item} has an attached stone.<br>
	 * If it exists, it triggers the {@code onEquip} logic for that stone.
	 * @param player The {@link Player} who is equipping the item.
	 * @param item The {@link Item} being equipped by the player.
	 */
	private static void addGodstoneEffect(Player player, Item item)
	{
		if (item.getGodStone() != null)
		{
			item.getGodStone().onEquip(player);
		}
	}
	
	/**
	 * Removes the effect of a godstone from a player.<br>
	 * This method checks if the {@code item} has an attached godstone.<br>
	 * If it exists, it triggers the {@code onUnEquip} logic for that stone.
	 * @param player The {@link Player} who is currently using the item.
	 * @param item The {@link Item} being processed to remove its effect.
	 */
	private static void removeGodstoneEffect(Player player, Item item)
	{
		if (item.getGodStone() != null)
		{
			item.getGodStone().onUnEquip(player);
		}
	}
	
	/**
	 * Adds the bonus statistics from an {@link Item} to a creature's stats.<br>
	 * This method applies the provided list of {@code StatFunction} modifiers.
	 * @param item The {@link Item} containing the bonus statistics.
	 * @param modifiers A list of {@link StatFunction} objects to be applied.
	 * @param cgs The {@link CreatureGameStats} container where stats are added.
	 */
	public static void addIdianBonusStats(Item item, List<StatFunction> modifiers, CreatureGameStats<?> cgs)
	{
		cgs.addEffect(item, modifiers);
	}
	
	/**
	 * Removes the bonus statistics provided by an {@link Item}.<br>
	 * This method calls {@code endEffect} on the {@code CreatureGameStats<?>} object.
	 * @param item The {@link Item} whose stats need to be removed.
	 * @param cgs The {@code CreatureGameStats<?>} container for the creature.
	 */
	public static void removeIdianBonusStats(Item item, CreatureGameStats<?> cgs)
	{
		cgs.endEffect(item);
	}
}
