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
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * This class handles the logic for freezing {@link Npc} entities.<br>
 * It manages state transitions when an AI is placed in a frozen status.
 * @author Rolandas
 */
public class FreezeEventHandler
{
	/**
	 * This method handles the logic when an {@link AbstractAI} is no longer frozen.<br>
	 * It removes the {@code FREEZE} sub-state from the AI.<br>
	 * If the AI belongs to a {@link NpcAI2}, it restores its previous walking behavior.<br>
	 * The AI then performs its next thought cycle.
	 * @param ai The {@link AbstractAI} instance that is being unfrozen.
	 */
	public static void onUnfreeze(AbstractAI ai)
	{
		if (ai.isInSubState(AISubState.FREEZE))
		{
			ai.setSubStateIfNot(AISubState.NONE);
			if (ai instanceof NpcAI2)
			{
				final Npc npc = ((NpcAI2) ai).getOwner();
				if (npc.getWalkerGroup() != null)
				{
					ai.setStateIfNot(AIState.WALKING);
					ai.setSubStateIfNot(AISubState.WALK_WAIT_GROUP);
				}
				else if (npc.getSpawn().getRandomWalk() > 0)
				{
					ai.setStateIfNot(AIState.WALKING);
					ai.setSubStateIfNot(AISubState.WALK_RANDOM);
				}
				
				npc.updateKnownlist();
			}
			
			ai.think();
		}
	}
	
	/**
	 * This method handles the logic when an {@link AbstractAI} is frozen.<br>
	 * It stops any active walking and sets the state to {@code IDLE}.<br>
	 * If the AI is an {@link NpcAI2}, it clears its aggro list and effects.
	 * @param ai The {@link AbstractAI} instance to freeze.
	 */
	public static void onFreeze(AbstractAI ai)
	{
		if (ai.isInState(AIState.WALKING))
		{
			WalkManager.stopWalking((NpcAI2) ai);
		}
		
		ai.setStateIfNot(AIState.IDLE);
		ai.setSubStateIfNot(AISubState.FREEZE);
		ai.think();
		if (ai instanceof NpcAI2)
		{
			final Npc npc = ((NpcAI2) ai).getOwner();
			npc.updateKnownlist();
			npc.getAggroList().clear();
			npc.getEffectController().removeAllEffects();
		}
	}
}
