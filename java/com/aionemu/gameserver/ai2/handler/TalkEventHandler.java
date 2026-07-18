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
package com.aionemu.gameserver.ai2.handler;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.TownService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class handles events related to NPC dialogue interactions.<br>
 * It manages the logic for opening and closing {@code SM_DIALOG_WINDOW} packets.<br>
 * It coordinates between {@link NpcAI2}, {@link QuestEngine}, and other game services during conversations.
 * @author ATracer
 */
public class TalkEventHandler
{
	/**
	 * Handles the logic when a creature interacts with an NPC.<br>
	 * It triggers {@code Creature)} first.<br>
	 * If the creature is a {@link Player}, it checks for quest dialogs and town restrictions.
	 * @param npcAI The AI instance of the NPC being interacted with.
	 * @param creature The creature that initiated the talk action.
	 */
	public static void onTalk(NpcAI2 npcAI, Creature creature)
	{
		onSimpleTalk(npcAI, creature);
		
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			if (QuestEngine.getInstance().onDialog(new QuestEnv(npcAI.getOwner(), player, 0, -1)))
			{
				return;
			}
			
			// only player villagers can use villager npcs in oriel/pernon
			switch (npcAI.getOwner().getObjectTemplate().getTitleId())
			{
				case 462877: // Village Trade Broker
				case 462878: // Village Guestbloom.
				case 462881: // Village Quest Board
					final int playerTownId = TownService.getInstance().getTownResidence(player);
					final int currentTownId = TownService.getInstance().getTownIdByPosition(player);
					if (playerTownId != currentTownId)
					{
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 44));
						return;
					}
					
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 10));
					return;
				default:
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 10));
					break;
			}
		}
		
	}
	
	/**
	 * Handles simple interaction between an NPC and a creature.<br>
	 * This method checks if the {@link NpcAI2} owner is a dialog NPC.<br>
	 * If true, it sets the AI state to {@code TALK} and targets the {@code creature}.
	 * @param npcAI The AI instance of the NPC.
	 * @param creature The creature interacting with the NPC.
	 */
	public static void onSimpleTalk(NpcAI2 npcAI, Creature creature)
	{
		if (npcAI.getOwner().getObjectTemplate().isDialogNpc())
		{
			npcAI.setSubStateIfNot(AISubState.TALK);
			npcAI.getOwner().setTarget(creature);
		}
	}
	
	/**
	 * Handles the logic when a conversation with an NPC ends.<br>
	 * It checks if the {@link Npc} is currently targeting the {@code creature}.<br>
	 * If the AI state is not {@code FOLLOWING}, it clears the target.<br>
	 * Finally, it triggers the {@code think()} method on the {@code npcAI}.
	 * @param npcAI The AI instance of the NPC.
	 * @param creature The creature that finished talking to the NPC.
	 */
	public static void onFinishTalk(NpcAI2 npcAI, Creature creature)
	{
		final Npc owner = npcAI.getOwner();
		if (owner.isTargeting(creature.getObjectId()))
		{
			if (npcAI.getState() != AIState.FOLLOWING)
			{
				owner.setTarget(null);
			}
			
			npcAI.think();
		}
	}
	
	/**
	 * Handles the logic when a simple conversation finishes.<br>
	 * It checks if the {@link Npc} is targeting the {@code creature}.<br>
	 * If it is, the target is cleared and the sub-state is reset.
	 * @param npcAI The AI controller for the NPC.
	 * @param creature The creature that finished talking.
	 */
	public static void onSimpleFinishTalk(NpcAI2 npcAI, Creature creature)
	{
		final Npc owner = npcAI.getOwner();
		if (owner.isTargeting(creature.getObjectId()) && npcAI.setSubStateIfNot(AISubState.NONE))
		{
			owner.setTarget(null);
		}
	}
}
