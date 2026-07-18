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
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.geometry.Point3D;

/**
 * This class handles events related to NPCs returning to their original positions.<br>
 * It manages the logic for {@link Npc} behavior when they finish a task or move back to a designated point.
 * @author ATracer
 */
public class ReturningEventHandler
{
	/**
	 * This method is called when an NPC is no longer at its home location.<br>
	 * It updates the {@code AIState} to {@code RETURNING}.<br>
	 * It triggers a return emote and starts the walking logic for the NPC.
	 * @param npcAI The {@code NpcAI2} object representing the NPC being updated.
	 */
	public static void onNotAtHome(NpcAI2 npcAI)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "onNotAtHome");
		}
		
		if (npcAI.setStateIfNot(AIState.RETURNING))
		{
			if (npcAI.isLogging())
			{
				AI2Logger.info(npcAI, "returning and restoring");
			}
			
			EmoteManager.emoteStartReturning(npcAI.getOwner());
		}
		
		if (npcAI.isInState(AIState.RETURNING))
		{
			final Npc npc = npcAI.getOwner();
			if (npc.hasWalkRoutes())
			{
				WalkManager.startWalking(npcAI);
			}
			else
			{
				final Point3D prevStep = npcAI.getOwner().getMoveController().recallPreviousStep();
				npcAI.getOwner().getMoveController().moveToPoint(prevStep.getX(), prevStep.getY(), prevStep.getZ());
			}
		}
	}
	
	/**
	 * Handles the logic when an NPC returns to its home location.<br>
	 * It clears previous movement steps and sets the state to {@code AIState.IDLE}.<br>
	 * This method also triggers the idle emote and starts the idle thinking process.
	 * @param npcAI The {@link NpcAI2} instance of the NPC returning home.
	 */
	public static void onBackHome(NpcAI2 npcAI)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "onBackHome");
		}
		
		npcAI.getOwner().getMoveController().clearBackSteps();
		if (npcAI.setStateIfNot(AIState.IDLE))
		{
			EmoteManager.emoteStartIdling(npcAI.getOwner());
			ThinkEventHandler.thinkIdle(npcAI);
		}
		
		final Npc npc = npcAI.getOwner();
		npc.getController().onReturnHome();
	}
}
