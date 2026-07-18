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

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * This class handles the logic triggered when an {@link Npc} dies.<br>
 * It manages state transitions and cleanup for the AI system.
 * @author ATracer
 */
public class DiedEventHandler
{
	/**
	 * This method is called when an {@link NpcAI2} entity dies.<br>
	 * It logs the death event if logging is enabled.<br>
	 * It also clears the target of the owner object.
	 * @param npcAI The {@code NpcAI2} instance that has died.
	 */
	public static void onDie(NpcAI2 npcAI)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "onDie");
		}
		
		onSimpleDie(npcAI);
		
		final Npc owner = npcAI.getOwner();
		owner.setTarget(null);
	}
	
	/**
	 * Handles the logic for when an NPC dies simply.<br>
	 * It logs the event, checks for shout triggers, and updates the AI state.<br>
	 * The aggro list of the owner is cleared during this process.
	 * @param npcAI The {@link NpcAI2} object representing the NPC that died.
	 */
	public static void onSimpleDie(NpcAI2 npcAI)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "onSimpleDie");
		}
		
		if (npcAI.poll(AIQuestion.CAN_SHOUT))
		{
			ShoutEventHandler.onDied(npcAI);
		}
		
		npcAI.setStateIfNot(AIState.DIED);
		npcAI.setSubStateIfNot(AISubState.NONE);
		npcAI.getOwner().getAggroList().clear();
	}
}
