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
package com.aionemu.gameserver.services.craft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.dao.PlayerRecipesDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RecipeList;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.model.templates.CraftLearnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEARN_RECIPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles the logic for updating and managing craft skills.<br>
 * It processes recipe learning and updates {@link PlayerSkillList} data.<br>
 * It ensures that crafting progress is correctly synchronized with the game state.
 * @author MrPoke, sphinx
 * @modified Imaginary
 */
public class CraftSkillUpdateService
{
	private static final Logger log = LoggerFactory.getLogger(CraftSkillUpdateService.class);
	protected static final Map<Integer, CraftLearnTemplate> npcBySkill = new HashMap<>();
	private static final Map<Integer, Integer> cost = new HashMap<>();
	private static final List<Integer> craftingSkillIds = new ArrayList<>();
	
	/**
	 * Provides the global instance of the {@link CraftSkillUpdateService}.<br>
	 * This method follows the singleton pattern.
	 * @return The single shared instance of {@code CraftSkillUpdateService}.
	 */
	public static CraftSkillUpdateService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor to prevent direct instantiation.<br>
	 * This class uses the singleton pattern via {@code getInstance}.
	 */
	private CraftSkillUpdateService()
	{
		// Asmodian
		npcBySkill.put(204096, new CraftLearnTemplate(30002, false, "Extract Vitality"));
		npcBySkill.put(830158, new CraftLearnTemplate(30002, false, "Extract Vitality"));
		npcBySkill.put(204257, new CraftLearnTemplate(30003, false, "Extract Aether"));
		npcBySkill.put(830148, new CraftLearnTemplate(30003, false, "Extract Aether"));
		
		npcBySkill.put(204100, new CraftLearnTemplate(40001, true, "Cooking"));
		npcBySkill.put(830142, new CraftLearnTemplate(40001, true, "Cooking"));
		npcBySkill.put(204104, new CraftLearnTemplate(40002, true, "Weaponsmithing"));
		npcBySkill.put(830146, new CraftLearnTemplate(40002, true, "Weaponsmithing"));
		npcBySkill.put(204106, new CraftLearnTemplate(40003, true, "Armorsmithing"));
		npcBySkill.put(830144, new CraftLearnTemplate(40003, true, "Armorsmithing"));
		npcBySkill.put(204110, new CraftLearnTemplate(40004, true, "Tailoring"));
		npcBySkill.put(830136, new CraftLearnTemplate(40004, true, "Tailoring"));
		npcBySkill.put(204102, new CraftLearnTemplate(40007, true, "Alchemy"));
		npcBySkill.put(830138, new CraftLearnTemplate(40007, true, "Alchemy"));
		npcBySkill.put(204108, new CraftLearnTemplate(40008, true, "Handicrafting"));
		npcBySkill.put(830140, new CraftLearnTemplate(40008, true, "Handicrafting"));
		npcBySkill.put(798452, new CraftLearnTemplate(40010, true, "Menusier"));
		npcBySkill.put(798456, new CraftLearnTemplate(40010, true, "Menusier"));
		
		// Elyos
		npcBySkill.put(203780, new CraftLearnTemplate(30002, false, "Extract Vitality"));
		npcBySkill.put(830066, new CraftLearnTemplate(30002, false, "Extract Vitality"));
		npcBySkill.put(203782, new CraftLearnTemplate(30003, false, "Extract Aether"));
		npcBySkill.put(830064, new CraftLearnTemplate(30003, false, "Extract Aether"));
		
		npcBySkill.put(203784, new CraftLearnTemplate(40001, true, "Cooking"));
		npcBySkill.put(830058, new CraftLearnTemplate(40001, true, "Cooking"));
		npcBySkill.put(203788, new CraftLearnTemplate(40002, true, "Weaponsmithing"));
		npcBySkill.put(830062, new CraftLearnTemplate(40002, true, "Weaponsmithing"));
		npcBySkill.put(203790, new CraftLearnTemplate(40003, true, "Armorsmithing"));
		npcBySkill.put(830060, new CraftLearnTemplate(40003, true, "Armorsmithing"));
		npcBySkill.put(203793, new CraftLearnTemplate(40004, true, "Tailoring"));
		npcBySkill.put(830052, new CraftLearnTemplate(40004, true, "Tailoring"));
		npcBySkill.put(203786, new CraftLearnTemplate(40007, true, "Alchemy"));
		npcBySkill.put(830054, new CraftLearnTemplate(40007, true, "Alchemy"));
		npcBySkill.put(203792, new CraftLearnTemplate(40008, true, "Handicrafting"));
		npcBySkill.put(830056, new CraftLearnTemplate(40008, true, "Handicrafting"));
		npcBySkill.put(798450, new CraftLearnTemplate(40010, true, "Menusier"));
		npcBySkill.put(798454, new CraftLearnTemplate(40010, true, "Menusier"));
		
		cost.put(0, 3500);
		cost.put(99, 17000);
		cost.put(199, 115000);
		cost.put(299, 460000);
		cost.put(399, 0);
		cost.put(449, 6004900);
		cost.put(499, 12000000);
		
		craftingSkillIds.add(40001);
		craftingSkillIds.add(40002);
		craftingSkillIds.add(40003);
		craftingSkillIds.add(40004);
		craftingSkillIds.add(40007);
		craftingSkillIds.add(40008);
		craftingSkillIds.add(40010);
		
		log.info("CraftSkillUpdateService: Initialized.");
	}
	
	/**
	 * Assigns specific morph recipes to a {@link Player} based on their race.<br>
	 * This method checks if the player is level 10 and adds missing recipes to the database.<br>
	 * It then sends an {@code SM_LEARN_RECIPE} packet to the player.
	 * @param player The {@link Player} who will receive the new recipes.
	 */
	public void setMorphRecipe(Player player)
	{
		final int object = player.getObjectId();
		final Race race = player.getRace();
		if (player.getLevel() == 10)
		{
			RecipeList recipelist = null;
			recipelist = DAOManager.getDAO(PlayerRecipesDAO.class).load(object);
			if (race == Race.ELYOS)
			{
				if (!recipelist.isRecipePresent(155000005))
				{
					DAOManager.getDAO(PlayerRecipesDAO.class).addRecipe(object, 155000005);
					PacketSendUtility.sendPacket(player, new SM_LEARN_RECIPE(155000005));
				}
				
				if (!recipelist.isRecipePresent(155000002))
				{
					DAOManager.getDAO(PlayerRecipesDAO.class).addRecipe(object, 155000002);
					PacketSendUtility.sendPacket(player, new SM_LEARN_RECIPE(155000002));
				}
				
				if (!recipelist.isRecipePresent(155000001))
				{
					DAOManager.getDAO(PlayerRecipesDAO.class).addRecipe(object, 155000001);
					PacketSendUtility.sendPacket(player, new SM_LEARN_RECIPE(155000001));
				}
			}
			else if (race == Race.ASMODIANS)
			{
				if (!recipelist.isRecipePresent(155005005))
				{
					DAOManager.getDAO(PlayerRecipesDAO.class).addRecipe(object, 155005005);
					PacketSendUtility.sendPacket(player, new SM_LEARN_RECIPE(155005005));
				}
				
				if (!recipelist.isRecipePresent(155005002))
				{
					DAOManager.getDAO(PlayerRecipesDAO.class).addRecipe(object, 155005002);
					PacketSendUtility.sendPacket(player, new SM_LEARN_RECIPE(155005002));
				}
				
				if (!recipelist.isRecipePresent(155005001))
				{
					DAOManager.getDAO(PlayerRecipesDAO.class).addRecipe(object, 155005001);
					PacketSendUtility.sendPacket(player, new SM_LEARN_RECIPE(155005001));
				}
			}
			
		}
	}
	
	/**
	 * Allows a {@link Player} to learn a crafting skill from an {@link Npc}.<br>
	 * This method checks level requirements, skill limits, and quest completion.<br>
	 * It also verifies if the player has enough kinah to pay for the upgrade.<br>
	 * If all conditions are met, it opens a confirmation window for the player.
	 * @param player The {@link Player} attempting to learn the skill.
	 * @param npc The {@link Npc} providing the skill training.
	 */
	public void learnSkill(Player player, Npc npc)
	{
		if (player.getLevel() < 10)
		{
			return;
		}
		
		final CraftLearnTemplate template = npcBySkill.get(npc.getNpcId());
		if (template == null)
		{
			return;
		}
		
		final int skillId = template.getSkillId();
		if (skillId == 0)
		{
			return;
		}
		
		int skillLvl = 0;
		final PlayerSkillList skillList = player.getSkillList();
		if (skillList.isSkillPresent(skillId))
		{
			skillLvl = skillList.getSkillLevel(skillId);
		}
		
		if (!cost.containsKey(skillLvl))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1390233));
			return;
		}
		
		// Retail : Max 2 expert crafting skill
		if (isCraftingSkill(skillId) && (!canLearnMoreExpertCraftingSkill(player) && (skillLvl == 399)))
		{
			PacketSendUtility.sendMessage(player, "You can only have " + CraftConfig.MAX_EXPERT_CRAFTING_SKILLS + " Expert crafting skills.");
			return;
		}
		
		// Retail : Max 1 master crafting skill
		if (isCraftingSkill(skillId) && (!canLearnMoreMasterCraftingSkill(player) && (skillLvl == 499)))
		{
			PacketSendUtility.sendMessage(player, "You can only have " + CraftConfig.MAX_MASTER_CRAFTING_SKILLS + " Master crafting skill.");
			return;
		}
		
		// Prevents player from buying expert craft upgrade (399 to 400)
		if (skillLvl == 399)
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300834));
			return;
		}
		
		// There is no upgrade payment for Essence and Aether tapping at 449, skip.
		if ((skillLvl == 449) && ((skillId == 30002) || (skillId == 30003)))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1390233));
			return;
		}
		
		// You must do quest before being able to buy master update (499 to 500)
		if ((skillLvl == 499) && (((skillId == 40001) && (!player.isCompleteQuest(29039) || !player.isCompleteQuest(19039))) || ((skillId == 40002) && (!player.isCompleteQuest(29009) || !player.isCompleteQuest(19009))) || ((skillId == 40003) && (!player.isCompleteQuest(29015) || !player.isCompleteQuest(19015))) || ((skillId == 40004) && (!player.isCompleteQuest(29021) || !player.isCompleteQuest(19021))) || ((skillId == 40007) && (!player.isCompleteQuest(29033) || !player.isCompleteQuest(19033))) || ((skillId == 40008) && (!player.isCompleteQuest(29027) || !player.isCompleteQuest(19027))) || ((skillId == 40010) && (!player.isCompleteQuest(29058) || !player.isCompleteQuest(19058)))))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400286));
			return;
		}
		
		// There is no Master upgrade for Aether and Essence tapping yet.
		if ((skillLvl == 499) && ((skillId == 30002) || (skillId == 30003)))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400286));
			return;
		}
		
		final int price = cost.get(skillLvl);
		final long kinah = player.getInventory().getKinah();
		final int skillLevel = skillLvl;
		final RequestResponseHandler responseHandler = new RequestResponseHandler(npc)
		{
			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				if ((price < kinah) && responder.getInventory().tryDecreaseKinah(price))
				{
					final PlayerSkillList skillList = responder.getSkillList();
					skillList.addSkill(responder, skillId, skillLevel + 1);
					responder.getRecipeList().autoLearnRecipe(responder, skillId, skillLevel + 1);
					PacketSendUtility.sendPacket(responder, new SM_SKILL_LIST(skillList.getSkillEntry(skillId), 1330004, false));
				}
				else
				{
					PacketSendUtility.sendPacket(responder, new SM_SYSTEM_MESSAGE(1300388));
				}
			}
			
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				// do nothing
			}
		};
		
		final boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_CRAFT_ADDSKILL_CONFIRM, responseHandler);
		if (result)
		{
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_CRAFT_ADDSKILL_CONFIRM, 0, 0, new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(skillId).getNameId()), String.valueOf(price)));
		}
	}
	
	/**
	 * Checks if a specific ID belongs to the crafting skills list.<br>
	 * This method iterates through all registered crafting IDs.
	 * @param skillId The unique identifier of the skill to check.
	 * @return {@code true} if the ID is a crafting skill, otherwise {@code false}.
	 */
	public static boolean isCraftingSkill(int skillId)
	{
		final Iterator<Integer> it = craftingSkillIds.iterator();
		while (it.hasNext())
		{
			if (it.next() == skillId)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Calculates the total number of expert crafting skills a player has. <br>
	 * It checks all known crafting skill IDs for the {@code Player}.<br>
	 * A skill is counted if its level is between 400 and 499 inclusive.
	 * @param player The {@link Player} object to check.
	 * @return The total count of expert crafting skills.
	 */
	static int getTotalExpertCraftingSkills(Player player)
	{
		int mastered = 0;
		
		final Iterator<Integer> it = craftingSkillIds.iterator();
		while (it.hasNext())
		{
			final int skillId = it.next();
			int skillLvl = 0;
			if (player.getSkillList().isSkillPresent(skillId))
			{
				skillLvl = player.getSkillList().getSkillLevel(skillId);
				if ((skillLvl > 399) && (skillLvl <= 499))
				{
					mastered++;
				}
			}
		}
		
		return mastered;
	}
	
	/**
	 * Calculates the total number of master crafting skills a player has. <br>
	 * A skill is considered mastered if its level is greater than {@code 499}. <br>
	 * This method checks all IDs in the {@code craftingSkillIds} list.
	 * @param player The {@link Player} object to check.
	 * @return The total count of mastered crafting skills.
	 */
	static int getTotalMasterCraftingSkills(Player player)
	{
		int mastered = 0;
		
		final Iterator<Integer> it = craftingSkillIds.iterator();
		while (it.hasNext())
		{
			final int skillId = it.next();
			int skillLvl = 0;
			if (player.getSkillList().isSkillPresent(skillId))
			{
				skillLvl = player.getSkillList().getSkillLevel(skillId);
				if (skillLvl > 499)
				{
					mastered++;
				}
			}
		}
		
		return mastered;
	}
	
	/**
	 * Checks if a {@link Player} can learn additional expert crafting skills.<br>
	 * This method compares the current total of expert and master skills against the limit in {@code CraftConfig}.
	 * @param player The {@link Player} to check.
	 * @return {@code true} if the player can still learn more skills, {@code false} otherwise.
	 */
	public static boolean canLearnMoreExpertCraftingSkill(Player player)
	{
		return (getTotalExpertCraftingSkills(player) + getTotalMasterCraftingSkills(player)) < CraftConfig.MAX_EXPERT_CRAFTING_SKILLS;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to learn another master crafting skill.<br>
	 * This method compares the current count against the limit defined in {@code CraftConfig}.
	 * @param player The {@link Player} object to check.
	 * @return {@code true} if the player can learn more skills, {@code false} otherwise.
	 */
	public static boolean canLearnMoreMasterCraftingSkill(Player player)
	{
		return getTotalMasterCraftingSkills(player) < CraftConfig.MAX_MASTER_CRAFTING_SKILLS;
	}
	
	private static class SingletonHolder
	{
		protected static final CraftSkillUpdateService instance = new CraftSkillUpdateService();
	}
}
