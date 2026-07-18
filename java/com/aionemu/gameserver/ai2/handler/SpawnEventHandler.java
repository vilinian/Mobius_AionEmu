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
import com.aionemu.gameserver.ai2.NpcAI2;

/**
 * This class handles events related to the spawning of {@link NpcAI2} entities.<br>
 * It manages the initialization and lifecycle of AI states when a new NPC enters the world.
 * @author ATracer
 */
public class SpawnEventHandler
{
	/**
	 * This method is called when an NPC first spawns.<br>
	 * It initializes the {@link NpcAI2} state to {@code AIState.IDLE}.<br>
	 * If the map region is active, it triggers the initial think process.
	 * @param npcAI The {@code NpcAI2} object that was just spawned.
	 */
	public static void onSpawn(NpcAI2 npcAI)
	{
		if (npcAI.setStateIfNot(AIState.IDLE))
		{
			if (npcAI.getOwner().getPosition().isMapRegionActive())
			{
				npcAI.think();
			}
		}
	}
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * This method updates the state of the {@link NpcAI2} object.<br>
	 * It sets the current state to {@code AIState.DESPAWNED}.
	 * @param npcAI The {@code NpcAI2} instance that was despawned.
	 */
	public static void onDespawn(NpcAI2 npcAI)
	{
		npcAI.setStateIfNot(AIState.DESPAWNED);
	}
	
	/**
	 * This method is called when an NPC respawns.<br>
	 * It resets the movement state of the NPC's owner.
	 * @param npcAI The {@link NpcAI2} object representing the NPC.
	 */
	public static void onRespawn(NpcAI2 npcAI)
	{
		npcAI.getOwner().getMoveController().resetMove();
	}
}
