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
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;
import com.aionemu.gameserver.model.templates.gather.Material;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHER_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHER_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for gathering resources from {@link Gatherable} objects.<br>
 * It manages the progression of a gathering action and rewards the player with {@link Material} items.
 * @author ATracer
 * @author Antraxx
 * @author Kamikaze
 */
public class GatheringTask extends AbstractCraftTask
{
	private final GatherableTemplate template;
	private final Material material;
	
	/**
	 * Creates a new {@link GatheringTask} for a player.<br>
	 * This constructor initializes the task with specific gathering data.<br>
	 * It sets up the success and failure values based on the material quality.
	 * @param requestor The {@link Player} who started the gathering action.
	 * @param gatherable The {@link Gatherable} object being interacted with.
	 * @param material The {@link Material} that will be produced.
	 * @param skillLvlDiff The difference in skill level for the calculation.
	 */
	public GatheringTask(Player requestor, Gatherable gatherable, Material material, int skillLvlDiff)
	{
		super(requestor, gatherable, skillLvlDiff);
		template = gatherable.getObjectTemplate();
		this.material = material;
		itemQuality = DataManager.ITEM_DATA.getItemTemplate(this.material.getItemid()).getItemQuality();
		currentSuccessValue = 0;
		currentFailureValue = 0;
		maxSuccessValue = (itemQuality.getQualityId() + 1) * 20;
		maxFailureValue = (itemQuality.getQualityId() + 1) * 30;
	}
	
	/**
	 * This method is called when a gathering interaction is cancelled.<br>
	 * It sends an update packet to the {@code requestor}.<br>
	 * It also broadcasts a status packet to the {@code requestor}.
	 */
	@Override
	protected void onInteractionAbort()
	{
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, 0, 0, 5));
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(), 2));
	}
	
	/**
	 * Handles the completion of a gathering interaction.<br>
	 * This method tells the {@link Gatherable} controller to finish the process.
	 */
	@Override
	protected void onInteractionFinish()
	{
		((Gatherable) responder).getController().completeInteraction();
	}
	
	/**
	 * Starts the gathering interaction process.<br>
	 * This method sends initial update packets to the players.<br>
	 * It then triggers the main {@code onInteraction} logic.<br>
	 * Finally, it broadcasts the status of the interaction.
	 */
	@Override
	protected void onInteractionStart()
	{
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, maxSuccessValue, maxFailureValue, 0));
		onInteraction();
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(), 0), true);
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(), 1), true);
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
		if (critVal < CraftConfig.CRAFT_CHANCE_PURPLECRIT)
		{
			critType = CraftCritType.PURPLE;
			currentSuccessValue = maxSuccessValue;
			return;
		}
		else if (critVal < CraftConfig.CRAFT_CHANCE_BLUECRIT)
		{
			critType = CraftCritType.BLUE;
		}
		else if (critVal < CraftConfig.CRAFT_CHANCE_INSTANT)
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
		mod -= itemQuality.getQualityId();
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
	 * Sends a progress update packet to the player.<br>
	 * This method updates the current success and failure values.<br>
	 * It also resets the {@code critType} if it was set to {@code PURPLE}.
	 */
	@Override
	protected void sendInteractionUpdate()
	{
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue, currentFailureValue, critType.getPacketId()));
		if (critType == CraftCritType.BLUE)
		{
			critType = CraftCritType.NONE;
		}
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
	 * Handles the logic when a gathering attempt fails.<br>
	 * It sends {@code SM_GATHER_UPDATE} packets to the requestor.<br>
	 * It also broadcasts an {@code SM_GATHER_STATUS} packet.
	 */
	@Override
	protected void onFailureFinish()
	{
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue, currentFailureValue, 1));
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue, currentFailureValue, 7));
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(), 3), true);
	}
	
	/**
	 * Handles the logic for completing a gathering task successfully.<br>
	 * It sends success packets and updates the player inventory.<br>
	 * This method rewards the player and handles instance-specific gather events.
	 * @return {@code true} if the task is finished, or {@code false} if it continues to the next step.
	 */
	@Override
	protected boolean onSuccessFinish()
	{
		PacketSendUtility.sendPacket(requestor, SM_SYSTEM_MESSAGE.STR_EXTRACT_GATHER_SUCCESS_1_BASIC(new DescriptionId(material.getNameid())));
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(), 2), true);
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue, currentFailureValue, 6));
		if (template.getEraseValue() > 0)
		{
			requestor.getInventory().decreaseByItemId(template.getRequiredItemId(), template.getEraseValue());
		}
		
		ItemService.addItem(requestor, material.getItemid(), requestor.getRates().getGatheringCountRate());
		if (requestor.isInInstance())
		{
			requestor.getPosition().getWorldMapInstance().getInstanceHandler().onGather(requestor, (Gatherable) responder);
		}
		else
		{
			requestor.getPosition().getWorld().getWorldMap(requestor.getWorldId()).getWorldHandler().onGather(requestor, (Gatherable) responder);
		}
		
		((Gatherable) responder).getController().rewardPlayer(requestor);
		return true;
	}
}
