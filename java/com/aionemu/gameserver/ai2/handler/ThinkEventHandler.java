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
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * This class handles various events related to the AI thinking process.<br>
 * It processes {@link AIEventType} signals for {@link NpcAI2} entities.<br>
 * It ensures that NPC behaviors are updated correctly based on game state changes.
 * @author ATracer
 */
public class ThinkEventHandler
{
	/**
	 * Processes the logic for an NPC's current state.<br>
	 * It checks if the NPC is alive and can acquire a think lock.<br>
	 * The method then calls specific handlers based on the {@code getState} value.
	 * @param npcAI The {@code NpcAI2} instance to process.
	 */
	public static void onThink(NpcAI2 npcAI)
	{
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "think");
		}
		
		if (npcAI.isAlreadyDead())
		{
			AI2Logger.info(npcAI, "can't think in dead state");
			return;
		}
		
		if (!npcAI.tryLockThink())
		{
			AI2Logger.info(npcAI, "can't acquire lock");
			return;
		}
		
		try
		{
			if (!npcAI.getOwner().getPosition().isMapRegionActive() || (npcAI.getSubState() == AISubState.FREEZE))
			{
				thinkInInactiveRegion(npcAI);
				return;
			}
			
			if (npcAI.isLogging())
			{
				AI2Logger.info(npcAI, "think state " + npcAI.getState());
			}
			
			switch (npcAI.getState())
			{
				case FIGHT:
					thinkAttack(npcAI);
					break;
				case WALKING:
					thinkWalking(npcAI);
					break;
				case IDLE:
					thinkIdle(npcAI);
					break;
				default:
					break;
			}
		}
		finally
		{
			npcAI.unlockThink();
		}
	}
	
	/**
	 * Handles the logic for an {@link NpcAI2} located in an inactive region.<br>
	 * It checks if the NPC is allowed to think before processing.<br>
	 * If the state is not {@code FIGHT}, it triggers a {@code NOT_AT_HOME} event.
	 * @param npcAI The {@link NpcAI2} instance to process.
	 */
	private static void thinkInInactiveRegion(NpcAI2 npcAI)
	{
		if (!npcAI.canThink())
		{
			return;
		}
		
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "think in inactive region: " + npcAI.getState());
		}
		
		switch (npcAI.getState())
		{
			case FIGHT:
				thinkAttack(npcAI);
				break;
			default:
				if (!npcAI.getOwner().isAtSpawnLocation())
				{
					npcAI.onGeneralEvent(AIEventType.NOT_AT_HOME);
				}
		}
		
	}
	
	/**
	 * Handles the attack logic for an {@link NpcAI2} instance.<br>
	 * It checks if there is a valid target in the aggro list.<br>
	 * If no target exists, it triggers a return to home or previous step.
	 * @param npcAI The {@code NpcAI2} object to process.
	 */
	public static void thinkAttack(NpcAI2 npcAI)
	{
		final Npc npc = npcAI.getOwner();
		final Creature mostHated = npc.getAggroList().getMostHated();
		if ((mostHated != null) && !mostHated.getLifeStats().isAlreadyDead())
		{
			npcAI.onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
		}
		else
		{
			npc.getMoveController().recallPreviousStep();
			npcAI.onGeneralEvent(AIEventType.ATTACK_FINISH);
			npcAI.onGeneralEvent(npc.isAtSpawnLocation() ? AIEventType.BACK_HOME : AIEventType.NOT_AT_HOME);
		}
	}
	
	/**
	 * This method handles the walking logic for an NPC.<br>
	 * It tells the {@link WalkManager} to start moving the character.
	 * @param npcAI The {@code NpcAI2} object representing the NPC.
	 */
	public static void thinkWalking(NpcAI2 npcAI)
	{
		WalkManager.startWalking(npcAI);
	}
	
	/**
	 * Handles the idle logic for an NPC.<br>
	 * This method checks if the {@link NpcAI2} is currently walking.<br>
	 * It attempts to start the walking process.<br>
	 * If walking fails to start, it sets the state to {@code AIState.IDLE}.
	 * @param npcAI The {@link NpcAI2} instance to update.
	 */
	public static void thinkIdle(NpcAI2 npcAI)
	{
		if (WalkManager.isWalking(npcAI))
		{
			final boolean startedWalking = WalkManager.startWalking(npcAI);
			if (!startedWalking)
			{
				npcAI.setStateIfNot(AIState.IDLE);
			}
		}
	}
}
