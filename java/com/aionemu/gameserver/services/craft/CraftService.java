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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.recipe.Component;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CRAFT_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CRAFT_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemService.ItemUpdatePredicate;
import com.aionemu.gameserver.skillengine.task.CraftingTask;
import com.aionemu.gameserver.skillengine.task.MorphingTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * Handles the core logic for item crafting within the game.<br>
 * This service manages recipe validation, material consumption, and reward distribution.<br>
 * It interacts with {@link ItemTemplate} and {@link RecipeTemplate} to process player requests.
 * @author MrPoke, sphinx, synchro2
 */
public class CraftService
{
	private static final Logger log = LoggerFactory.getLogger("CRAFT_LOG");
	
	/**
	 * Completes the crafting process for a player.<br>
	 * This method calculates rewards, grants items, and updates skill experience.<br>
	 * It also handles critical success logic and cooldowns.
	 * @param player The {@link Player} who is performing the craft.
	 * @param recipetemplate The {@link RecipeTemplate} used for the crafting process.
	 * @param critCount The number of critical successes achieved.
	 * @param bonus The percentage bonus applied to the experience reward.
	 */
	public static void finishCrafting(Player player, RecipeTemplate recipetemplate, int critCount, int bonus)
	{
		if (recipetemplate.getMaxProductionCount() != null)
		{
			player.getRecipeList().deleteRecipe(player, recipetemplate.getId());
			if (critCount == 0)
			{
				QuestEngine.getInstance().onFailCraft(new QuestEnv(null, player, 0, 0), recipetemplate.getComboProduct(1) == null ? 0 : recipetemplate.getComboProduct(1));
			}
		}
		
		int xpReward = (int) (((0.008 * (recipetemplate.getSkillpoint() + 100) * (recipetemplate.getSkillpoint() + 100)) + 60));
		xpReward = xpReward + ((xpReward * bonus) / 100); // bonus
		final int productItemId = critCount > 0 ? recipetemplate.getComboProduct(critCount) : recipetemplate.getProductid();
		
		ItemService.addItem(player, productItemId, recipetemplate.getQuantity(), new ItemUpdatePredicate()
		{
			@Override
			public boolean changeItem(Item item)
			{
				if (item.getItemTemplate().isWeapon() || item.getItemTemplate().isArmor())
				{
					item.setItemCreator(player.getName());
				}
				
				return true;
			}
		});
		
		final ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(productItemId);
		if (LoggingConfig.LOG_CRAFT)
		{
			log.info((critCount > 0 ? "[CRAFT][Critical] ID/Count" : "[CRAFT][Normal] ID/Count") + (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "/Item Name - " + productItemId + "/" + recipetemplate.getQuantity() + "/" + itemTemplate.getName() : " - " + productItemId + "/" + recipetemplate.getQuantity()) + " to player " + player.getName());
		}
		
		final int gainedCraftExp = (int) RewardType.CRAFTING.calcReward(player, xpReward);
		
		// Check Expert and Master Crafting
		final int skillId = recipetemplate.getSkillid();
		if ((skillId == 40001) || (skillId == 40002) || (skillId == 40003) || (skillId == 40004) || (skillId == 40007) || (skillId == 40008) || (skillId == 40010))
		{
			if ((player.getSkillList().getSkillLevel(skillId) >= 500) && (recipetemplate.getSkillpoint() < 500))
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_COMBINE_EXP_GRAND_MASTER);
			}
			else if ((player.getSkillList().getSkillLevel(skillId) >= 400) && (recipetemplate.getSkillpoint() < 400))
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_COMBINE_EXP);
			}
			else
			{
				if (player.getSkillList().addSkillXp(player, recipetemplate.getSkillid(), gainedCraftExp, recipetemplate.getSkillpoint()))
				{
					player.getCommonData().addExp(xpReward, RewardType.CRAFTING);
				}
				else
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_PRODUCTION_EXP(new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(recipetemplate.getSkillid()).getNameId())));
				}
			}
		}
		
		if (recipetemplate.getCraftDelayId() != null)
		{
			player.getCraftCooldownList().addCraftCooldown(recipetemplate.getCraftDelayId(), recipetemplate.getCraftDelayTime());
		}
	}
	
	/**
	 * Starts the crafting process for a specific player.<br>
	 * This method validates the requirements and initiates the {@code CraftingTask}.<br>
	 * It handles both standard crafting and morphing tasks based on the {@code recipeId}.
	 * @param player The {@link Player} who is performing the action.
	 * @param recipeId The unique identifier for the {@code RecipeTemplate}.
	 * @param targetObjId The ID of the {@link VisibleObject} being used as a target.
	 * @param craftType The type of crafting to perform.
	 */
	public static void startCrafting(Player player, int recipeId, int targetObjId, int craftType)
	{
		final RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		final int skillId = recipeTemplate.getSkillid();
		final VisibleObject target = player.getKnownList().getObject(targetObjId);
		final ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());
		
		if (!checkCraft(player, recipeTemplate, skillId, target, itemTemplate, craftType))
		{
			sendCancelCraft(player, skillId, targetObjId, itemTemplate);
			return;
		}
		
		if (recipeTemplate.getDp() != null)
		{
			player.getCommonData().addDp(-recipeTemplate.getDp());
		}
		
		if (skillId == 40009)
		{
			player.setCraftingTask(new MorphingTask(player, (StaticObject) target, recipeTemplate));
		}
		else
		{
			final int skillLvlDiff = player.getSkillList().getSkillLevel(skillId) - recipeTemplate.getSkillpoint();
			player.setCraftingTask(new CraftingTask(player, (StaticObject) target, recipeTemplate, skillLvlDiff, craftType == 1 ? 15 : 0));
		}
		
		player.getCraftingTask().start();
	}
	
	/**
	 * Validates if a player can perform a specific crafting action.<br>
	 * This method checks requirements such as inventory space, skills, and materials.<br>
	 * It also verifies the target object and cooldown status.
	 * @param player The {@link Player} attempting to craft the item.
	 * @param recipeTemplate The {@link RecipeTemplate} containing crafting details.
	 * @param skillId The unique identifier for the required skill.
	 * @param target The {@link VisibleObject} being used as a crafting station.
	 * @param itemTemplate The {@link ItemTemplate} of the product to be created.
	 * @param craftType The specific type of crafting action being performed.
	 * @return {@code true} if all requirements are met, otherwise {@code false}.
	 */
	private static boolean checkCraft(Player player, RecipeTemplate recipeTemplate, int skillId, VisibleObject target, ItemTemplate itemTemplate, int craftType)
	{
		if ((recipeTemplate == null) || (itemTemplate == null) || ((player.getCraftingTask() != null) && player.getCraftingTask().isInProgress()))
		{
			return false;
		}
		
		// morphing dont need static object/npc to use
		if ((skillId != 40009) && ((target == null) || !(target instanceof StaticObject)))
		{
			AuditLogger.info(player, " tried to craft incorrect target.");
			return false;
		}
		
		if ((recipeTemplate.getDp() != null) && (player.getCommonData().getDp() < recipeTemplate.getDp()))
		{
			AuditLogger.info(player, " try craft without required DP count.");
			return false;
		}
		
		if (player.getInventory().isFull())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMBINE_INVENTORY_IS_FULL);
			return false;
		}
		
		if (!player.getRecipeList().isRecipePresent(recipeTemplate.getId()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMBINE_CAN_NOT_FIND_RECIPE);
			return false;
		}
		
		if (recipeTemplate.getCraftDelayId() != null)
		{
			if (!player.getCraftCooldownList().isCanCraft(recipeTemplate.getCraftDelayId()))
			{
				AuditLogger.info(player, " try craft item before cooldown expire.");
				return false;
			}
		}
		
		for (Component component : recipeTemplate.getComponent())
		{
			if (!player.getInventory().decreaseByItemId(component.getItemid(), component.getQuantity()))
			{
				AuditLogger.info(player, " tried craft without required items.");
				return false;
			}
		}
		
		if ((craftType == 1) && !player.getInventory().decreaseByItemId(getBonusReqItem(skillId), 1))
		{
			AuditLogger.info(player, " tried craft without " + getBonusReqItem(skillId) + " . ");
			return false;
		}
		
		if (!player.getSkillList().isSkillPresent(skillId) || (player.getSkillList().getSkillLevel(skillId) < recipeTemplate.getSkillpoint()))
		{
			AuditLogger.info(player, " tried craft without required skill.");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Sends a packet to the player to cancel an ongoing crafting action.<br>
	 * It updates the craft status and plays a cancellation animation.
	 * @param player The {@code Player} who is performing the craft.
	 * @param skillId The unique identifier for the crafting skill.
	 * @param targetObjId The object ID of the target where crafting occurs.
	 * @param itemTemplate The {@code ItemTemplate} of the item being crafted.
	 */
	private static void sendCancelCraft(Player player, int skillId, int targetObjId, ItemTemplate itemTemplate)
	{
		PacketSendUtility.sendPacket(player, new SM_CRAFT_UPDATE(skillId, itemTemplate, 0, 0, 4));
		PacketSendUtility.broadcastPacket(player, new SM_CRAFT_ANIMATION(player.getObjectId(), targetObjId, 0, 2), true);
	}
	
	/**
	 * Retrieves the required item ID for a specific crafting skill.<br>
	 * This method maps {@code skillId} values to their corresponding bonus items.<br>
	 * It returns {@code 0} if no specific item is assigned.
	 * @param skillId The unique identifier of the crafting skill.
	 * @return The integer ID of the required bonus item or {@code 0}.
	 */
	private static int getBonusReqItem(int skillId)
	{
		switch (skillId)
		{
			case 40001: // Cooking
				return 169401081;
			case 40002: // Weaponsmithing
				return 169401076;
			case 40003: // Armorsmithing
				return 169401077;
			case 40004: // Tailoring
				return 169401078;
			case 40007: // Alchemy
				return 169401080;
			case 40008: // Handicrafting
				return 169401079;
			case 40010: // Menusier
				return 169401082;
			case 40011:
				return 0;
		}
		
		return 0;
	}
}
