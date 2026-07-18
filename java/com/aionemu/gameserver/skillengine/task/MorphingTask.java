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

import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CRAFT_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CRAFT_UPDATE;
import com.aionemu.gameserver.services.craft.CraftService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the asynchronous processing of morphing tasks within the crafting system.<br>
 * This task manages the logic for transforming items or entities as defined by {@link RecipeTemplate}.<br>
 * It ensures that morphing actions are executed correctly without blocking the main game thread.
 * @author Antraxx
 * @author Kamikaze
 */
public class MorphingTask extends CraftingTask
{
	/**
	 * Creates a new {@link MorphingTask} for a player. <br>
	 * This task handles the logic for morphing objects. <br>
	 * It initializes the success and failure thresholds.
	 * @param requestor The {@link Player} who started the action.
	 * @param responder The {@link StaticObject} being interacted with.
	 * @param recipeTemplates The {@link RecipeTemplate} used for this task.
	 */
	public MorphingTask(Player requestor, StaticObject responder, RecipeTemplate recipeTemplates)
	{
		super(requestor, responder, recipeTemplates, 0, 0);
		maxSuccessValue = 50;
		maxFailureValue = 75;
	}
	
	/**
	 * Starts the morphing task execution.<br>
	 * It triggers the initial interaction start sequence.<br>
	 * This method schedules a recurring task using {@link ThreadPoolManager}.<br>
	 * The task validates participants and handles the interaction logic.
	 */
	@Override
	public void start()
	{
		onInteractionStart();
		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				if (!validateParticipants())
				{
					stop(true);
				}
				
				final boolean stopTask = onInteraction();
				if (stopTask)
				{
					stop(false);
				}
			}
		}, CraftConfig.CRAFT_TIMERMORPH_DELAY, CraftConfig.CRAFT_TIMERMORPH_PERIOD);
	}
	
	/**
	 * Performs the initial analysis of the crafting interaction.<br>
	 * This method prepares the necessary data for the task execution.<br>
	 * It ensures all requirements are met before starting the process.
	 */
	@Override
	protected void analyzeInteraction()
	{
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
		PacketSendUtility.broadcastPacket(requestor, new SM_CRAFT_ANIMATION(requestor.getObjectId(), 0, 0, 3), true);
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
		PacketSendUtility.broadcastPacket(requestor, new SM_CRAFT_ANIMATION(requestor.getObjectId(), 0, 0, 2), true);
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, 0, 0, 5));
		CraftService.finishCrafting(requestor, recipeTemplate, critCount, 0);
		return true;
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
	 * Handles the logic for a single step in a crafting interaction.<br>
	 * It checks if the task has reached a success or failure state.<br>
	 * If neither is met, it calls {@code analyzeInteraction} and updates the UI.
	 * @return {@code true} if the task failed, {@code false} if it is still in progress or succeeded.
	 */
	@Override
	protected boolean onInteraction()
	{
		currentSuccessValue = maxSuccessValue;
		return onSuccessFinish();
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
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, maxSuccessValue, maxFailureValue, 0));
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, 0, 0, 1));
		PacketSendUtility.broadcastPacket(requestor, new SM_CRAFT_ANIMATION(requestor.getObjectId(), 0, recipeTemplate.getSkillid(), 0), true);
		PacketSendUtility.broadcastPacket(requestor, new SM_CRAFT_ANIMATION(requestor.getObjectId(), 0, recipeTemplate.getSkillid(), 1), true);
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
		PacketSendUtility.broadcastPacket(requestor, new SM_CRAFT_ANIMATION(requestor.getObjectId(), 0, 0, 2), true);
		requestor.setCraftingTask(null);
	}
}
