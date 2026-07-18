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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MAGIC_CRAFT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MAGIC_CRAFT_ANIMATION;
import com.aionemu.gameserver.services.craft.CraftService;
import com.aionemu.gameserver.services.craft.MagicCraftService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the asynchronous processing of magic crafting requests.<br>
 * This task manages the logic for creating items using {@link MagicCraftService}.<br>
 * It ensures that crafting operations do not block the main game thread.
 * @author FrozenKiller
 */
public class MagicCraftTask extends CraftingTask
{
	/**
	 * Creates a new task for performing a magic craft action.<br>
	 * This constructor initializes the {@link CraftingTask} with specific limits.
	 * @param requestor The {@link Player} who is initiating the craft.
	 * @param responder The {@link StaticObject} being interacted with.
	 * @param recipeTemplates The {@link RecipeTemplate} used for this craft.
	 */
	public MagicCraftTask(Player requestor, StaticObject responder, RecipeTemplate recipeTemplates)
	{
		super(requestor, responder, recipeTemplates, 0, 0);
		maxSuccessValue = 2000000;
		maxFailureValue = 2000000;
	}
	
	/**
	 * Starts the execution of the magic craft task.<br>
	 * It triggers the initial interaction sequence.<br>
	 * This method schedules a background thread to validate participants and process the result.
	 */
	@Override
	public void start()
	{
		onInteractionStart();
		task = ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				if (!validateParticipants())
				{
					stop(true);
				}
				
				final boolean stopTask = onSuccessFinish();
				if (stopTask)
				{
					stop(false);
				}
			}
		}, 4000);
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
	 * Handles the logic for completing a crafting task successfully.<br>
	 * It checks for critical hits and updates the craft status.<br>
	 * This method triggers the final crafting results via {@link CraftService}.
	 * @return {@code true} if the craft is finished, or {@code false} if it continues to the next step.
	 */
	@Override
	protected boolean onSuccessFinish()
	{
		PacketSendUtility.sendPacket(requestor, new SM_MAGIC_CRAFT_ANIMATION(requestor.getObjectId(), 2));
		PacketSendUtility.sendPacket(requestor, new SM_MAGIC_CRAFT(2, recipeTemplate));
		MagicCraftService.finishMagicCrafting(requestor, recipeTemplate, 0, 0);
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
	 * Sends a progress update packet to the player.<br>
	 * This method updates the current success and failure values.<br>
	 * It also resets the {@code critType} if it was set to {@code PURPLE}.
	 */
	@Override
	protected void sendInteractionUpdate()
	{
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
		PacketSendUtility.sendPacket(requestor, new SM_MAGIC_CRAFT(0, recipeTemplate));
		PacketSendUtility.sendPacket(requestor, new SM_MAGIC_CRAFT_ANIMATION(requestor.getObjectId(), 0));
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
		PacketSendUtility.sendPacket(requestor, new SM_MAGIC_CRAFT_ANIMATION(requestor.getObjectId(), 1));
		PacketSendUtility.sendPacket(requestor, new SM_MAGIC_CRAFT(1, 0));
		requestor.setCraftingTask(null);
	}
}
