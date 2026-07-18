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
package system.handlers.ai.quests;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AI2Actions.SelectDialogResult;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.handler.CreatureEventHandler;
import com.aionemu.gameserver.ai2.handler.SimpleAbyssGuardHandler;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestActionType;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.drop.DropService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import system.handlers.ai.ActionItemNpcAI2;

/**
 * Handles the AI behavior for NPCs that interact with quest items.<br>
 * This class manages specific actions when a {@link Player} uses an item to progress a quest.<br>
 * It extends {@link ActionItemNpcAI2} to provide specialized logic for quest-related interactions.
 * @author xTz
 */
@AIName("quest_use_item")
public class QuestItemNpcAI2 extends ActionItemNpcAI2
{
	private List<Player> registeredPlayers = new ArrayList<>();
	
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It triggers the start of the item usage logic for the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		if (!(QuestEngine.getInstance().onCanAct(new QuestEnv(getOwner(), player, 0, 0), getObjectTemplate().getTemplateId(), QuestActionType.ACTION_ITEM_USE)))
		{
			return;
		}
		
		super.handleDialogStart(player);
	}
	
	/**
	 * Handles the completion of an item usage action.<br>
	 * This method is called when a {@link Player} finishes using an item.<br>
	 * It checks if the owner is in an instance before processing.
	 * @param player The {@code Player} who finished using the item.
	 */
	@Override
	protected void handleUseItemFinish(Player player)
	{
		final SelectDialogResult dialogResult = AI2Actions.selectDialog(this, player, 0, -1);
		if (getNpcId() == 730229)
		{
			if (dialogResult.isSuccess())
			{
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 3057, 10034)); // Dialog, QuestId
			}
		}
		
		if (!dialogResult.isSuccess())
		{
			if (isDialogNpc())
			{
				// show default dialog
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), DialogAction.SELECT_ACTION_1011.id()));
			}
			return;
		}
		
		final QuestEnv questEnv = dialogResult.getEnv();
		if (QuestService.getQuestDrop(getNpcId()).isEmpty())
		{
			return;
		}
		
		if (registeredPlayers.isEmpty())
		{
			AI2Actions.scheduleRespawn(this);
			if (player.isInGroup2())
			{
				registeredPlayers = QuestService.getEachDropMembersGroup(player.getPlayerGroup2(), getNpcId(), questEnv.getQuestId());
				if (registeredPlayers.isEmpty())
				{
					registeredPlayers.add(player);
				}
			}
			else if (player.isInAlliance2())
			{
				registeredPlayers = QuestService.getEachDropMembersAlliance(player.getPlayerAlliance2(), getNpcId(), questEnv.getQuestId());
				if (registeredPlayers.isEmpty())
				{
					registeredPlayers.add(player);
				}
			}
			else
			{
				registeredPlayers.add(player);
			}
			
			AI2Actions.registerDrop(this, player, registeredPlayers);
			DropService.getInstance().requestDropList(player, getObjectId());
		}
		else if (registeredPlayers.contains(player))
		{
			DropService.getInstance().requestDropList(player, getObjectId());
		}
	}
	
	/**
	 * Checks if the current NPC is configured as a dialog NPC.<br>
	 * This method retrieves the status from the {@code ObjectTemplate}.
	 * @return {@code true} if the NPC can initiate dialogs, {@code false} otherwise.
	 */
	private boolean isDialogNpc()
	{
		return getObjectTemplate().isDialogNpc();
	}
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It clears the list of {@link Player} objects from the registered players list.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		super.handleDespawned();
		registeredPlayers.clear();
	}
	
	/**
	 * Processes the logic when a {@code Creature} is spotted.<br>
	 * This method delegates the behavior to the {@link SimpleAbyssGuardHandler}.
	 * @param creature The {@code Creature} that was seen.
	 */
	@Override
	protected void handleCreatureSee(Creature creature)
	{
		CreatureEventHandler.onCreatureSee(this, creature);
	}
	
	/**
	 * This method handles the logic when a {@code Creature} moves.<br>
	 * It triggers the movement behavior for the AI.
	 * @param creature The {@code Creature} that has moved.
	 */
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		CreatureEventHandler.onCreatureMoved(this, creature);
	}
}
