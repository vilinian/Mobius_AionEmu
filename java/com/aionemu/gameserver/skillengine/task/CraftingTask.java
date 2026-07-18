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
package com.aionemu.gameserver.skillengine.task;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CRAFT_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CRAFT_UPDATE;
import com.aionemu.gameserver.services.craft.CraftService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the asynchronous processing of item crafting requests.<br>
 * This class manages the logic for creating items based on {@link RecipeTemplate} data.<br>
 * It ensures that crafting actions are executed correctly within the game world.
 * @author Mr. Poke
 * @author synchro2
 * @author Antraxx
 * @author Kamikaze
 * @Reworked Kill3r
 */
public class CraftingTask extends AbstractCraftTask
{
	protected RecipeTemplate recipeTemplate;
	protected ItemTemplate itemTemplate;
	protected ItemTemplate itemTemplateReal;
	protected int critCount;
	protected boolean crit = false;
	protected boolean purpleCrit = false;
	protected int maxCritCount;
	private final int bonus;
	
	/**
	 * Creates a new {@code CraftingTask} for a player.<br>
	 * This constructor initializes the task with required crafting data.<br>
	 * It sets up the recipe and calculates the maximum possible critical hits.
	 * @param requestor The {@link Player} who is performing the craft.
	 * @param responder The {@link StaticObject} where the craft occurs.
	 * @param recipeTemplate The {@link RecipeTemplate} used for this task.
	 * @param skillLvlDiff The difference in skill levels between the player and the object.
	 * @param bonus A numeric value representing a crafting bonus.
	 */
	public CraftingTask(Player requestor, StaticObject responder, RecipeTemplate recipeTemplate, int skillLvlDiff, int bonus)
	{
		super(requestor, responder, skillLvlDiff);
		this.recipeTemplate = recipeTemplate;
		maxCritCount = recipeTemplate.getComboProductSize();
		this.bonus = bonus;
	}
	
	/**
	 * Initializes the crafting variables for a specific task.<br>
	 * This method sets the {@code itemQuality} and calculates the maximum success and failure values.<br>
	 * It resets the current progress counters to {@code 0}.
	 */
	private void craftSetup()
	{
		itemQuality = itemTemplateReal.getItemQuality();
		
		// if (this.itemTemplateReal.getCategory() == ItemCategory.QUEST) {
		// this.itemQuality = ItemQuality.COMMON;
		// }
		
		currentSuccessValue = 0;
		currentFailureValue = 0;
		maxSuccessValue = (int) Math.round((itemQuality.getQualityId() + 3) * 3.5) * 5;
		maxFailureValue = (int) Math.round((itemQuality.getQualityId() + 3) * 5.25) * 5;
	}
	
	/**
	 * Calculates the success or failure progress for a crafting task.<br>
	 * This method uses {@code skillLvlDiff} to determine the difficulty.<br>
	 * It updates either {@code currentSuccessValue} or {@code currentFailureValue}.<br>
	 * The values are capped at {@code maxFailureValue}.
	 */
	@Override
	protected void analyzeInteraction()
	{
		final int critVal = Rnd.get(55000) / (skillLvlDiff + 1);
		if (critVal < CraftConfig.CRAFT_CHANCE_BLUECRIT)
		{
			critType = CraftCritType.BLUE;
		}
		else if ((critVal < CraftConfig.CRAFT_CHANCE_INSTANT) && (itemQuality.getQualityId() < ItemQuality.EPIC.getQualityId()))
		{
			critType = CraftCritType.INSTANT;
			currentSuccessValue = maxSuccessValue;
			return;
		}
		
		if (CraftConfig.CRAFT_CHECKTASK)
		{
			if (task == null)
			{
				return;
			}
		}
		
		double mod = (Math.sqrt((double) skillLvlDiff / 450f) * 100f) + (Rnd.nextGaussian() * 10f);
		mod -= (double) itemQuality.getQualityId() / 2;
		if (mod < 0)
		{
			currentFailureValue -= (int) mod;
		}
		else
		{
			currentSuccessValue += (int) mod;
		}
		
		if (currentSuccessValue >= maxSuccessValue)
		{
			currentSuccessValue = maxSuccessValue;
		}
		else if (currentFailureValue >= maxFailureValue)
		{
			currentFailureValue = maxFailureValue;
		}
	}
	
	/**
	 * Handles the logic when a crafting attempt fails.<br>
	 * It sends an {@code SM_CRAFT_UPDATE} packet to the requestor.<br>
	 * It also broadcasts an {@code SM_CRAFT_ANIMATION} for the animation.
	 */
	@Override
	protected void onFailureFinish()
	{
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, currentSuccessValue, currentFailureValue, 6));
		PacketSendUtility.broadcastPacket(requestor, new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), 0, 3), true);
	}
	
	/**
	 * Handles the logic for completing a crafting task successfully.<br>
	 * It checks for critical hits and updates the craft status.<br>
	 * This method triggers the final crafting results via {@link CraftService}.
	 * @return {@code true} if the craft is finished, or {@code false} if it continues to the next step.
	 */
	@Override
	protected boolean onSuccessFinish()
	{
		if (checkCrit() && (recipeTemplate.getComboProduct(critCount) != null))
		{
			if (purpleCrit)
			{
				critCount++;
			}
			
			craftSetup();
			PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplateReal, maxSuccessValue, maxFailureValue, 3));
			return false;
		}
		
		if ((critCount > 0) && checkCrit())
		{
			PacketSendUtility.broadcastPacket(requestor, new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), 0, 2), true);
			PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplateReal, currentSuccessValue, currentFailureValue, 5));
			CraftService.finishCrafting(requestor, recipeTemplate, critCount, bonus);
			return true;
		}
		
		PacketSendUtility.broadcastPacket(requestor, new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), 0, 2), true);
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplateReal, currentSuccessValue, currentFailureValue, 5));
		CraftService.finishCrafting(requestor, recipeTemplate, critCount, bonus);
		return true;
	}
	
	/**
	 * Sends a progress update packet to the player.<br>
	 * This method updates the current success and failure values.<br>
	 * It also resets the {@code critType} if it was set to {@code PURPLE}.
	 */
	@Override
	protected void sendInteractionUpdate()
	{
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, currentSuccessValue, currentFailureValue, critType.getPacketId()));
		if (critType == CraftCritType.PURPLE)
		{
			critType = CraftCritType.NONE;
		}
	}
	
	/**
	 * This method is called when a crafting interaction is cancelled.<br>
	 * It sends an update packet to the {@code requestor}.<br>
	 * It also broadcasts a craft animation to the {@code requestor}.<br>
	 * Finally, it clears the crafting task from the {@code requestor}.
	 */
	@Override
	protected void onInteractionAbort()
	{
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, 0, 0, 4));
		PacketSendUtility.broadcastPacket(requestor, new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), 0, 2), true);
		requestor.setCraftingTask(null);
	}
	
	/**
	 * Handles the final cleanup after a crafting interaction ends.<br>
	 * This method clears the {@code CraftingTask} from the {@link Player}.
	 */
	@Override
	protected void onInteractionFinish()
	{
		requestor.setCraftingTask(null);
	}
	
	/**
	 * Initializes the crafting process and prepares the necessary data.<br>
	 * This method sets up the item templates and handles critical success logic.<br>
	 * It also sends the initial craft update and animation packets to the players.
	 */
	@Override
	protected void onInteractionStart()
	{
		itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());
		itemTemplateReal = itemTemplate;
		craftSetup();
		
		if ((recipeTemplate.getMaxProductionCount() != null) && (itemTemplateReal.getCategory() == ItemCategory.QUEST))
		{
			requestor.getRecipeList().deleteRecipe(requestor, recipeTemplate.getId());
		}
		
		int chance = requestor.getRates().getCraftCritRate();
		if (maxCritCount > 0)
		{
			if ((critCount > 0) && (maxCritCount > 1))
			{
				chance = requestor.getRates().getComboCritRate();
				final House house = requestor.getActiveHouse();
				if (house != null)
				{
					switch (house.getHouseType())
					{
						case ESTATE:
						case MANSION:
						case HOUSE:
						case STUDIO:
						case PALACE:
							chance += 5;
							break;
						default:
							break;
					}
				}
			}
			
			if ((critCount < maxCritCount) && (Rnd.get(100) < chance))
			{
				critCount++;
				crit = true;
			}
			
			// Only Double Proc happens , if maxCritCount is 2.
			if (((critCount > 0) && (critCount <= maxCritCount) && (maxCritCount != 1)) && (Rnd.get(100) < chance))
			{
				purpleCrit = true;
			}
		}
		
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, maxSuccessValue, maxFailureValue, 0));
		onInteraction();
		PacketSendUtility.broadcastPacket(requestor, new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), recipeTemplate.getSkillid(), 0), true);
		PacketSendUtility.broadcastPacket(requestor, new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), recipeTemplate.getSkillid(), 1), true);
	}
	
	/**
	 * Handles the logic for a single step in a crafting interaction.<br>
	 * It checks if the task has reached a success or failure state.<br>
	 * If neither is met, it calls {@code analyzeInteraction} and updates the UI.
	 * @return {@code true} if the task failed, {@code false} if it is still in progress or succeeded.
	 */
	@Override
	protected boolean onInteraction()
	{
		if (currentSuccessValue == maxSuccessValue)
		{
			return onSuccessFinish();
		}
		
		if (currentFailureValue == maxFailureValue)
		{
			onFailureFinish();
			return true;
		}
		
		analyzeInteraction();
		sendInteractionUpdate();
		return false;
	}
	
	/**
	 * Checks if a critical hit or purple critical hit occurred.<br>
	 * Resets the status flags and updates the {@code itemTemplateReal}.
	 * @return {@code true} if a crit was processed, otherwise {@code false}.
	 */
	private boolean checkCrit()
	{
		if (crit)
		{
			crit = false;
			itemTemplateReal = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getComboProduct(critCount));
			return true;
		}
		
		if (purpleCrit)
		{
			purpleCrit = false;
			itemTemplateReal = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getComboProduct(critCount));
			return true;
		}
		
		return false;
	}
}
