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
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * This class handles the activation logic for {@link NpcAI2} entities.<br>
 * It manages how NPCs transition into active states within the game world.
 * @author ATracer
 */
public class ActivateEventHandler
{
	/**
	 * Activates the logic for a specific {@link NpcAI2} instance.<br>
	 * This method checks if the NPC is in the {@code AIState.IDLE} state.<br>
	 * If it is idle, it updates the owner's known list and triggers the {@code think()} method.
	 * @param npcAI The {@code NpcAI2} object to activate.
	 */
	public static void onActivate(NpcAI2 npcAI)
	{
		if (npcAI.isInState(AIState.IDLE))
		{
			npcAI.getOwner().updateKnownlist();
			npcAI.think();
		}
	}
	
	/**
	 * This method handles the deactivation logic for an {@link NpcAI2} instance.<br>
	 * It stops any active walking behavior and clears current effects.<br>
	 * The NPC's known list and aggro list are also reset during this process.
	 * @param npcAI The {@code NpcAI2} object to deactivate.
	 */
	public static void onDeactivate(NpcAI2 npcAI)
	{
		if (npcAI.isInState(AIState.WALKING))
		{
			WalkManager.stopWalking(npcAI);
		}
		
		npcAI.think();
		final Npc npc = npcAI.getOwner();
		npc.updateKnownlist();
		npc.getAggroList().clear();
		npc.getEffectController().removeAllEffects();
	}
}
