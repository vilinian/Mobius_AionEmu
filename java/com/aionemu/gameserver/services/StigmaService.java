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
package com.aionemu.gameserver.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.templates.HiddenStigmasTemplate;
import com.aionemu.gameserver.model.templates.item.RequireSkill;
import com.aionemu.gameserver.model.templates.item.Stigma;
import com.aionemu.gameserver.model.templates.item.Stigma.StigmaSkill;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * This service manages the logic for {@link Stigma} items and their associated skills.<br>
 * It handles skill acquisition, requirements checking, and updates for player equipment.<br>
 * Use this class to interact with any stigma-related mechanics in the game server.
 * @author ATracer
 * @modified cura
 * @updated ever & Kill3r 4.8
 */
public class StigmaService
{
	private static final Logger log = LoggerFactory.getLogger(StigmaService.class);
	
	/**
	 * Validates and processes the action of equipping a stigma item.<br>
	 * This method checks for valid slot counts, class restrictions, and required kinah costs.<br>
	 * If successful, it adds the corresponding skills to the {@link Player}.
	 * @param player The {@link Player} attempting to equip the item.
	 * @param resultItem The {@link Item} being equipped.
	 * @param slot The equipment slot index where the item is placed.
	 * @return {@code true} if the action was successful, {@code false} otherwise.
	 */
	public static boolean notifyEquipAction(Player player, Item resultItem, long slot)
	{
		if (resultItem.getItemTemplate().isStigma())
		{
			if (ItemSlot.isRegularStigma(slot))
			{
				// check the number of Regular stigma wearing
				if (getPossibleRegulerStigmaCount(player) <= player.getEquipment().getEquippedItemsRegularStigma().size())
				{
					AuditLogger.info(player, "Possible client hack Regular stigma count big :O");
					return false;
				}
			}
			else if (ItemSlot.isAdvancedStigma(slot))
			{
				// check the number of Advanced stigma wearing
				if (getPossibleAdvancedStigmaCount(player) <= player.getEquipment().getEquippedItemsAdvancedStigma().size())
				{
					AuditLogger.info(player, "Possible client hack Advanced stigma count big :O");
					return false;
				}
			}
			else if (ItemSlot.isMajorStigma(slot))
			{
				// check the number of Major stigma wearing
				if (getPossibleAdvancedStigmaCount(player) <= player.getEquipment().getEquippedItemsMajorStigma().size())
				{
					AuditLogger.info(player, "Possible client hack Major stigma count big :O");
					return false;
				}
			}
			else if (ItemSlot.isSpecialStigma(slot))
			{
				// check the number of Special stigma wearing
				if (getPossibleSpecialStigmaCount(player) <= player.getEquipment().getEquippedItemsSpecialStigma().size())
				{
					AuditLogger.info(player, "Possible client hack Major stigma count big :O");
					return false;
				}
			}
			
			if (!resultItem.getItemTemplate().isClassSpecific(player.getCommonData().getPlayerClass()))
			{
				AuditLogger.info(player, "Possible client hack not valid for class.");
				return false;
			}
			
			// You cannot equip 2 stigma skills at 1 slot , was possible before.. o.o
			if (!StigmaService.isPossibleEquippedStigma(player, resultItem))
			{
				AuditLogger.info(player, "Player tried to get Multiple Stigma's from One Stigma Stone!");
				return false;
			}
			
			final Stigma stigmaInfo = resultItem.getItemTemplate().getStigma();
			
			if (stigmaInfo == null)
			{
				log.warn("Stigma info missing for item: " + resultItem.getItemTemplate().getTemplateId());
				return false;
			}
			
			// TEMP fix until ItemTemplate is Updated
			final int kinahCount = getkinahCount(resultItem); // stigmaInfo.getKinah();
			if (player.getInventory().getKinah() < kinahCount)
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300413));
				return false;
			}
			
			player.getInventory().tryDecreaseKinah(kinahCount);
			
			// default main stigma skill
			player.getSkillList().addStigmaSkill(player, stigmaInfo.getSkills(), true);
			
			// other stigma lvls of the same skill
			final List<Integer> stigmaData = stigmaInfo.getSkillIdOnly();
			List<StigmaSkill> stigmaUpperLvls;
			
			if (stigmaData.size() != 1)
			{
				// Dual Skill i.e Exhausting Wave
				for (Integer id : stigmaData)
				{
					stigmaUpperLvls = getStigmaInfoUpToLevel(player.getCommonData().getLevel(), id); // only 2 skill here '1:534 1:434
					if (stigmaUpperLvls != null)
					{
						for (StigmaSkill stigma : stigmaUpperLvls)
						{
							player.getSkillList().addStigmaSkill(player, stigma.getSkillId(), resultItem.getEnchantOrAuthorizeLevel() + 1, false, true);
						}
					}
				}
			}
			else
			{
				// Single Skill
				stigmaUpperLvls = getStigmaInfoUpToLevel(player.getCommonData().getLevel(), stigmaData.get(0)); // only 1 skill here '1:534'
				if (stigmaUpperLvls != null)
				{
					for (StigmaSkill stigma : stigmaUpperLvls)
					{
						player.getSkillList().addStigmaSkill(player, stigma.getSkillId(), resultItem.getEnchantOrAuthorizeLevel() + 1, false, true);
					}
				}
			}
			
			final List<Integer> sStigma = player.getEquipment().getEquippedItemsAllStigmaIds();
			sStigma.add(resultItem.getItemId()); // The last item ur about to add is not in getEquippedItemsAllStigma , so adding manual
		}
		
		return true;
	}
	
	/**
	 * Retrieves a list of {@link StigmaSkill} objects based on the player's level.<br>
	 * It starts from a specific skill ID and finds all reachable skills in that tree.<br>
	 * The method filters out skills that exceed the current {@code playerLvl}.
	 * @param playerLvl The current level of the player to check against requirements.
	 * @param startingStigmaSkillid The initial skill ID used as the root for the search.
	 * @return A list of available {@link StigmaSkill} objects or {@code null} if none are found.
	 */
	public static List<StigmaSkill> getStigmaInfoUpToLevel(int playerLvl, int startingStigmaSkillid)
	{
		final List<StigmaSkill> stigmaList = new ArrayList<>();
		final SkillLearnTemplate[] temps = DataManager.SKILL_TREE_DATA.getTemplatesForSkill(startingStigmaSkillid);
		String skillName;
		
		for (SkillLearnTemplate skillTemp : temps)
		{
			skillName = skillTemp.getName();
			for (int id = startingStigmaSkillid; id <= 5000; id++)
			{
				if ((startingStigmaSkillid == 3330) && (id == 3331))
				{
					id = 4591; // exception for Shadowfall, its lvl1 id is 3330 and lvl2 is starting from 4591
				}
				else if ((startingStigmaSkillid == 1210) && (id == 1216))
				{
					id = 4603;
				}
				
				final SkillLearnTemplate[] NextTemps = DataManager.SKILL_TREE_DATA.getTemplatesForSkill(id);
				for (SkillLearnTemplate skillTemp2 : NextTemps)
				{
					if (skillTemp2.getName().equalsIgnoreCase(skillName))
					{
						if (playerLvl >= skillTemp2.getMinLevel())
						{
							// Log the player's eligibility for the skill, including current and previous levels.
							final int lv = skillTemp2.getSkillLevel(), i = skillTemp2.getSkillId();
							final StigmaSkill sti = new StigmaSkill(lv, i);
							
							stigmaList.add(sti);
						}
					}
					else
					{
						return stigmaList;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Handles the logic for unequipping a {@link Item}.<br>
	 * It checks if the item is a stigma and validates skill requirements.<br>
	 * If successful, it removes associated skills and effects from the {@link Player}.
	 * @param player The {@link Player} attempting to unequip the item.
	 * @param resultItem The {@link Item} being removed from equipment.
	 * @return {@code true} if the unequip action was successful, or {@code false} otherwise.
	 */
	public static boolean notifyUnequipAction(Player player, Item resultItem)
	{
		if (resultItem.getItemTemplate().isStigma())
		{
			final Stigma stigmaInfo = resultItem.getItemTemplate().getStigma();
			final int itemId = resultItem.getItemId();
			final Equipment equipment = player.getEquipment();
			if ((itemId == 140000007) || (itemId == 140000005))
			{
				if (equipment.hasDualWeaponEquipped(ItemSlot.LEFT_HAND))
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_STIGMA_CANNT_UNEQUIP_STONE_FIRST_UNEQUIP_CURRENT_EQUIPPED_ITEM);
					return false;
				}
			}
			
			for (Item item : player.getEquipment().getEquippedItemsAllStigma())
			{
				final Stigma si = item.getItemTemplate().getStigma();
				if ((resultItem == item) || (si == null))
				{
					continue;
				}
				
				for (StigmaSkill sSkill : stigmaInfo.getSkills())
				{
					for (RequireSkill rs : si.getRequireSkill())
					{
						if (rs.getSkillIds().contains(sSkill.getSkillId()))
						{
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300410, new DescriptionId(resultItem.getItemTemplate().getNameId()), new DescriptionId(item.getItemTemplate().getNameId())));
							return false;
						}
					}
				}
			}
			
			for (StigmaSkill sSkill : stigmaInfo.getSkills())
			{
				final int nameId = DataManager.SKILL_DATA.getSkillTemplate(sSkill.getSkillId()).getNameId();
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300403, new DescriptionId(nameId)));
				
				// remove skill
				SkillLearnService.removeSkill(player, sSkill.getSkillId());
				
				// remove effect
				player.getEffectController().removeEffect(sSkill.getSkillId());
				
				// remove hidden stigma
				player.getSkillList().deleteHiddenStigma(player);
			}
			
			// for remaining lvls for the same skill ..
			final List<Integer> stigmaData = stigmaInfo.getSkillIdOnly();
			List<StigmaSkill> stigmaUpperLvls;
			
			if (stigmaData.size() != 1)
			{
				for (Integer id : stigmaData)
				{
					stigmaUpperLvls = getStigmaInfoUpToLevel(player.getCommonData().getLevel(), id); // only 2 skill here '1:534 1:434
					if (stigmaUpperLvls != null)
					{
						for (StigmaSkill stigma : stigmaUpperLvls)
						{
							player.getSkillList().removeSkill(stigma.getSkillId());
							
							// remove effect
							player.getEffectController().removeEffect(stigma.getSkillId());
						}
					}
				}
			}
			else
			{
				stigmaUpperLvls = getStigmaInfoUpToLevel(player.getCommonData().getLevel(), stigmaData.get(0)); // only 1 skill here '1:534'
				if (stigmaUpperLvls != null)
				{
					for (StigmaSkill stigma : stigmaUpperLvls)
					{
						player.getSkillList().removeSkill(stigma.getSkillId());
						
						// remove effect
						player.getEffectController().removeEffect(stigma.getSkillId());
					}
				}
			}
			
		}
		
		return true;
	}
	
	/**
	 * Verifies if a player should have a hidden stigma skill.<br>
	 * This method checks the player's level and equipped items.<br>
	 * It adds the required skill to the {@code Player} if it is missing.
	 * @param player The {@code Player} object to check.
	 */
	public static void recheckHiddenStigma(Player player)
	{
		if ((player.getLevel() >= 55) && (player.getEquipment().getEquippedItemsAllStigma().size() >= 6))
		{
			for (Item stigma : player.getEquipment().getEquippedItemsAllStigma())
			{
				if (stigma.getItemTemplate().isNoEnchant())
				{
					return;
				}
			}
			
			final int hiddenStigmaSkillId = DataManager.HIDDEN_STIGMA_DATA.getHiddenStigmaSkill(player);
			
			if (hiddenStigmaSkillId != 0)
			{
				if (!player.getSkillList().isHaveHiddenStigma(player) || (player.getSkillList().isHaveHiddenStigma(player) && (player.getSkillList().getStigmaSkillEntry(hiddenStigmaSkillId) == null)))
				{
					player.getSkillList().addHiddenStigmaSkill(player, hiddenStigmaSkillId, 1);
				}
			}
		}
	}
	
	/**
	 * Processes stigma data when a {@link Player} logs into the server.<br>
	 * This method updates the player's skill list based on equipped stigmas.<br>
	 * It also validates that the number of equipped stigmas is legitimate.
	 * @param player The {@code Player} object that is currently logging in.
	 */
	public static void onPlayerLogin(Player player)
	{
		final List<Item> equippedItems = player.getEquipment().getEquippedItemsAllStigma();
		for (Item item : equippedItems)
		{
			// All Equipped Items are Stigmas
			if (item.getItemTemplate().isStigma())
			{
				final Stigma stigmaInfo = item.getItemTemplate().getStigma();
				
				if (stigmaInfo == null)
				{
					log.warn("Stigma info missing for item: " + item.getItemTemplate().getTemplateId());
					return;
				}
				
				if (item.getItemTemplate().getTemplateId() == 140001132)
				{
					System.out.println("!!! USED !!!");
				}
				
				player.getSkillList().addStigmaSkill(player, stigmaInfo.getSkills(), false);
				player.getSkillList().deleteHiddenStigmaSilent(player);
				recheckHiddenStigma(player);
				
				final List<Integer> stigmaData = stigmaInfo.getSkillIdOnly();
				List<StigmaSkill> stigmaUpperLvls;
				
				if (stigmaData.size() != 1)
				{
					for (Integer id : stigmaData)
					{
						stigmaUpperLvls = getStigmaInfoUpToLevel(player.getCommonData().getLevel(), id); // only 2 skill here '1:534 1:434'
						if (stigmaUpperLvls != null)
						{
							for (StigmaSkill stigma : stigmaUpperLvls)
							{
								player.getSkillList().addStigmaSkill(player, stigma.getSkillId(), item.getEnchantOrAuthorizeLevel() + 1, false, false);
							}
						}
					}
				}
				else
				{
					stigmaUpperLvls = getStigmaInfoUpToLevel(player.getCommonData().getLevel(), stigmaData.get(0)); // only 1 skill here '1:534'
					if (stigmaUpperLvls != null)
					{
						for (StigmaSkill stigma : stigmaUpperLvls)
						{
							player.getSkillList().addStigmaSkill(player, stigma.getSkillId(), item.getEnchantOrAuthorizeLevel() + 1, false, false);
						}
					}
				}
			}
		}
		
		for (Item item : equippedItems)
		{
			if (item.getItemTemplate().isStigma())
			{
				if (!isPossibleEquippedStigma(player, item))
				{
					AuditLogger.info(player, "Possible client hack stigma count big :O");
					player.getEquipment().unEquipItem(item.getObjectId(), 0);
					continue;
				}
				
				final Stigma stigmaInfo = item.getItemTemplate().getStigma();
				
				if (stigmaInfo == null)
				{
					log.warn("Stigma info missing for item: " + item.getItemTemplate().getTemplateId());
					player.getEquipment().unEquipItem(item.getObjectId(), 0);
					continue;
				}
				
				if (!item.getItemTemplate().isClassSpecific(player.getCommonData().getPlayerClass()))
				{
					AuditLogger.info(player, "Possible client hack not valid for class.");
					player.getEquipment().unEquipItem(item.getObjectId(), 0);
				}
			}
		}
	}
	
	/**
	 * Calculates the number of regular stigma slots available for a player.<br>
	 * This method checks the {@code Player} level, race, and quest completion status.<br>
	 * It also verifies if the player has specific membership permissions.
	 * @param player The {@link Player} object to check.
	 * @return The total count of available regular stigma slots as an {@code int}.
	 */
	private static int getPossibleRegulerStigmaCount(Player player)
	{
		if ((player == null) || (player.getLevel() < 20))
		{
			return 0;
		}
		
		if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST))
		{
			return 3;
		}
		
		/*
		 * Stigma Quest Elyos: 1929, Asmodians: 2900
		 */
		switch (player.getRace())
		{
			case ELYOS:
			{
				if (player.isCompleteQuest(1929))
				{
					if (player.getLevel() < 20)
					{
						return 1;
					}
					else if (player.getLevel() < 30)
					{
						return 2;
					}
					else if (player.getLevel() < 40)
					{
						return 3;
					}
					else
					{
						return 3;
					}
				}
				break;
			}
			case ASMODIANS:
			{
				if (player.isCompleteQuest(2900))
				{
					if (player.getLevel() < 20)
					{
						return 1;
					}
					else if (player.getLevel() < 30)
					{
						return 2;
					}
					else if (player.getLevel() < 40)
					{
						return 3;
					}
					else
					{
						return 3;
					}
				}
				break;
			}
			default:
				break;
		}
		
		return 0;
	}
	
	/**
	 * Calculates the number of advanced stigma slots available for a player.<br>
	 * It checks the {@code Player} level and specific quest completions.<br>
	 * It also considers the player's race and membership permissions.
	 * @param player The {@link Player} object to check.
	 * @return The total count of possible advanced stigma slots as an {@code int}.
	 */
	private static int getPossibleAdvancedStigmaCount(Player player)
	{
		if ((player == null) || (player.getLevel() < 45))
		{
			return 0;
		}
		
		if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST))
		{
			return 2;
		}
		
		switch (player.getRace())
		{
			case ELYOS:
			{
				if (player.isCompleteQuest(1929))
				{
					if (player.getLevel() < 45)
					{
						return 1;
					}
					else if (player.getLevel() < 50)
					{
						return 2;
					}
					else
					{
						return 2;
					}
				}
				break;
			}
			case ASMODIANS:
			{
				if (player.isCompleteQuest(2900))
				{
					if (player.getLevel() < 45)
					{
						return 1;
					}
					else if (player.getLevel() < 50)
					{
						return 2;
					}
					else
					{
						return 2;
					}
				}
				break;
			}
			default:
				break;
		}
		
		return 0;
	}
	
	/**
	 * Calculates the number of available major stigma slots for a player.<br>
	 * It checks if the {@code Player} meets level and quest requirements.<br>
	 * The result depends on membership permissions or specific race quests.
	 * @param player The {@code Player} object to check.
	 * @return The total count of possible major stigma slots as an {@code int}.
	 */
	private static int getPossibleMajorStigmaCount(Player player)
	{
		if ((player == null) || (player.getLevel() < 55))
		{
			return 0;
		}
		
		if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST))
		{
			return 1;
		}
		
		switch (player.getRace())
		{
			case ELYOS:
			{
				if (player.isCompleteQuest(1929))
				{
					if (player.getLevel() > 54)
					{
						return 1;
					}
				}
				break;
			}
			case ASMODIANS:
			{
				if (player.isCompleteQuest(2900))
				{
					if (player.getLevel() > 54)
					{
						return 1;
					}
				}
				break;
			}
			default:
				break;
		}
		
		return 0;
	}
	
	/**
	 * Calculates the number of special stigmas available for a player.<br>
	 * This method returns a fixed value of {@code 3}.<br>
	 * It returns {@code 0} if the {@code player} is {@code null}.
	 * @param player The {@link Player} object to check.
	 * @return The count of possible special stigmas.
	 */
	private static int getPossibleSpecialStigmaCount(Player player)
	{
		// No check needed (checked by Client)
		if (player == null)
		{
			return 0;
		}
		
		return 3;
	}
	
	/**
	 * Checks if a {@link Player} can equip a specific {@link Item} based on stigma limits.<br>
	 * This method validates the item type and the available slot count for the player.<br>
	 * It returns {@code true} if the equipment is valid and {@code false} otherwise.
	 * @param player The {@link Player} attempting to equip the item.
	 * @param item The {@link Item} that needs to be checked.
	 * @return {@code true} if the item can be equipped, {@code false} otherwise.
	 */
	private static boolean isPossibleEquippedStigma(Player player, Item item)
	{
		if ((player == null) || ((item == null) || !item.getItemTemplate().isStigma()))
		{
			return false;
		}
		
		final long itemSlotToEquip = item.getEquipmentSlot();
		
		// Stigma
		if (ItemSlot.isRegularStigma(itemSlotToEquip))
		{
			final int stigmaCount = getPossibleRegulerStigmaCount(player);
			if (stigmaCount > 0)
			{
				switch (stigmaCount)
				{
					case 1:
					{
						if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask())
						{
							return true;
						}
						break;
					}
					case 2:
					{
						if ((itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask()) || (itemSlotToEquip == ItemSlot.STIGMA2.getSlotIdMask()))
						{
							return true;
						}
						break;
					}
					case 3:
					{
						if ((itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask()) || (itemSlotToEquip == ItemSlot.STIGMA2.getSlotIdMask()) || (itemSlotToEquip == ItemSlot.STIGMA3.getSlotIdMask()))
						{
							return true;
						}
						break;
					}
					default:
						break;
				}
			}
		}
		// Advanced Stigma
		else if (ItemSlot.isAdvancedStigma(itemSlotToEquip))
		{
			final int advStigmaCount = getPossibleAdvancedStigmaCount(player);
			if (advStigmaCount > 0)
			{
				switch (advStigmaCount)
				{
					case 1:
					{
						if (itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask())
						{
							return true;
						}
						break;
					}
					case 2:
					{
						if ((itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask()) || (itemSlotToEquip == ItemSlot.ADV_STIGMA2.getSlotIdMask()))
						{
							return true;
						}
						break;
					}
					default:
						break;
				}
			}
		}
		// Major Stigma
		else if (ItemSlot.isMajorStigma(itemSlotToEquip))
		{
			final int majStigmaCount = getPossibleMajorStigmaCount(player);
			if (majStigmaCount == 1)
			{
				if (itemSlotToEquip == ItemSlot.MAJ_STIGMA.getSlotIdMask())
				{
					return true;
				}
			}
		}
		// Special Stigma
		else if (ItemSlot.isSpecialStigma(itemSlotToEquip))
		{
			final int specStigmaCount = getPossibleSpecialStigmaCount(player);
			if (specStigmaCount > 0)
			{
				switch (specStigmaCount)
				{
					case 1:
					{
						if (itemSlotToEquip == ItemSlot.SPECIAL_STIGMA1.getSlotIdMask())
						{
							return true;
						}
						break;
					}
					case 2:
					{
						if ((itemSlotToEquip == ItemSlot.SPECIAL_STIGMA1.getSlotIdMask()) || (itemSlotToEquip == ItemSlot.SPECIAL_STIGMA2.getSlotIdMask()))
						{
							return true;
						}
						break;
					}
					case 3:
					{
						if ((itemSlotToEquip == ItemSlot.SPECIAL_STIGMA1.getSlotIdMask()) || (itemSlotToEquip == ItemSlot.SPECIAL_STIGMA2.getSlotIdMask()) || (itemSlotToEquip == ItemSlot.SPECIAL_STIGMA3.getSlotIdMask()))
						{
							return true;
						}
						break;
					}
					default:
						break;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * This method refreshes the data for all hidden stigmas.<br>
	 * It iterates through every class and triggers {@code dataProcessing()} on each template.<br>
	 * Use this to update stigma information when configuration files change.
	 */
	public static void reparseHiddenStigmas()
	{
		for (HiddenStigmasTemplate classStigmas : DataManager.HIDDEN_STIGMA_DATA.getHiddenStigmasByClass())
		{
			for (HiddenStigmasTemplate.HiddenStigmaTemplate hst : classStigmas.getHiddenStigmas())
			{
				hst.dataProcessing();
			}
		}
	}
	
	// TEMP fix until ItemTemplate is Updated
	/**
	 * Calculates the amount of {@code Kinah} required for an item.<br>
	 * The value depends on the {@code ItemQuality} of the {@link Item}.
	 * @param resultItem The {@code Item} to check for quality requirements.
	 * @return The number of {@code Kinah} required as an {@code int}.
	 */
	private static int getkinahCount(Item resultItem)
	{
		switch (resultItem.getItemTemplate().getItemQuality())
		{
			case RARE:
			{
				return 8000;
			}
			case LEGEND:
			{
				return 32000;
			}
			case UNIQUE:
			{
				return 128000;
			}
			default:
				return 0;
		}
	}
}
