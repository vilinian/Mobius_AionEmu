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
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * This class handles events related to the follow behavior of {@link NpcAI2} entities.<br>
 * It manages how creatures react when they are commanded or instructed to follow a target.
 * @author ATracer
 */
public class FollowEventHandler
{
	/**
	 * Makes an {@link NpcAI2} follow a specific {@link Creature}.<br>
	 * This method updates the NPC state to {@code FOLLOWING}.<br>
	 * It sets the target and triggers the following emote.
	 * @param npcAI The AI instance that will perform the action.
	 * @param creature The entity that the NPC should follow.
	 */
	public static void follow(NpcAI2 npcAI, Creature creature)
	{
		if (npcAI.setStateIfNot(AIState.FOLLOWING))
		{
			npcAI.getOwner().setTarget(creature);
			EmoteManager.emoteStartFollowing(npcAI.getOwner());
		}
	}
	
	/**
	 * Handles the logic when a {@link Creature} moves.<br>
	 * This method checks if the {@code npcAI} is currently in the {@code FOLLOWING} state.<br>
	 * It verifies if the owner is targeting the creature and if the creature is alive.<br>
	 * If these conditions are met, it calls {@code Creature)}.
	 * @param npcAI The AI instance of the NPC.
	 * @param creature The creature that has moved.
	 */
	public static void creatureMoved(NpcAI2 npcAI, Creature creature)
	{
		if (npcAI.isInState(AIState.FOLLOWING))
		{
			if (npcAI.getOwner().isTargeting(creature.getObjectId()) && !creature.getLifeStats().isAlreadyDead())
			{
				checkFollowTarget(npcAI, creature);
			}
		}
	}
	
	/**
	 * Checks if a {@link Creature} is within the valid range for an {@link NpcAI2}.<br>
	 * It triggers a {@code TARGET_TOOFAR} event if the target is too far away.
	 * @param npcAI The AI instance to check.
	 * @param creature The creature being followed.
	 */
	public static void checkFollowTarget(NpcAI2 npcAI, Creature creature)
	{
		if (!isInRange(npcAI, creature))
		{
			npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
		}
	}
	
	/**
	 * Checks if a {@link VisibleObject} is within a specific distance of an {@link AbstractAI}.<br>
	 * The range depends on whether the object is in an instance and the health of the AI owner.
	 * @param ai The {@code AbstractAI} to check from.
	 * @param object The {@code VisibleObject} to check against.
	 * @return {@code true} if the object is within range, otherwise {@code false}.
	 */
	public static boolean isInRange(AbstractAI ai, VisibleObject object)
	{
		if (object == null)
		{
			return false;
		}
		
		if (object.isInInstance())
		{
			return MathUtil.isIn3dRange(ai.getOwner(), object, 9999);
		}
		else if (ai.getOwner().getLifeStats().getHpPercentage() < 100)
		{
			return MathUtil.isIn3dRange(ai.getOwner(), object, 30);
		}
		else
		{
			return MathUtil.isIn3dRange(ai.getOwner(), object, 15);
		}
	}
	
	/**
	 * Stops the {@link NpcAI2} from following a specific {@link Creature}.<br>
	 * This method resets the NPC state to {@code AIState.IDLE}.<br>
	 * It also clears the target and cancels any active movement.
	 * @param npcAI The AI instance to update.
	 * @param creature The creature that was being followed.
	 */
	public static void stopFollow(NpcAI2 npcAI, Creature creature)
	{
		if (npcAI.setStateIfNot(AIState.IDLE))
		{
			npcAI.getOwner().setTarget(null);
			npcAI.getOwner().getMoveController().abortMove();
			npcAI.getOwner().getController().scheduleRespawn();
			npcAI.getOwner().getController().onDelete();
		}
	}
}
