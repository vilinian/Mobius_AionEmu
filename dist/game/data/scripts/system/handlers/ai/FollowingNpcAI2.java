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
package system.handlers.ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.StateEvents;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.FollowEventHandler;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * Handles the behavior for NPCs that follow a specific target.<br>
 * This class manages state transitions and actions for the {@code following} AI type.
 * @author ATracer
 */
@AIName("following")
public class FollowingNpcAI2 extends GeneralNpcAI2
{
	/**
	 * Processes the request for a {@code Creature} to follow this NPC.<br>
	 * It triggers the logic defined in {@code follow}.
	 * @param creature The {@code Creature} that wants to follow.
	 */
	@Override
	protected void handleFollowMe(Creature creature)
	{
		FollowEventHandler.follow(this, creature);
	}
	
	/**
	 * Checks if this AI handler can process a specific event.<br>
	 * It evaluates the current {@link AIState} and the provided {@code eventType}.
	 * @param eventType The type of event to check.
	 * @return {@code true} if the event can be handled, {@code false} otherwise.
	 */
	@Override
	protected boolean canHandleEvent(AIEventType eventType)
	{
		switch (getState())
		{
			case DESPAWNED:
				return StateEvents.DESPAWN_EVENTS.hasEvent(eventType);
			case DIED:
				return StateEvents.DEAD_EVENTS.hasEvent(eventType);
			case CREATED:
				return StateEvents.CREATED_EVENTS.hasEvent(eventType);
			default:
				break;
		}
		
		if (eventType == AIEventType.CREATURE_MOVED)
		{
			return getState() == AIState.FOLLOWING;
		}
		
		return true;
	}
	
	/**
	 * This method handles the logic when a {@code Creature} moves.<br>
	 * It triggers the movement behavior for the AI.
	 * @param creature The {@code Creature} that has moved.
	 */
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		if (creature == getOwner().getTarget())
		{
			FollowEventHandler.creatureMoved(this, creature);
		}
		else if (getOwner().getTarget() == null)
		{
			FollowEventHandler.stopFollow(this, creature);
		}
	}
	
	/**
	 * This method stops a {@link Creature} from following the NPC.<br>
	 * It calls the {@code stopFollow} method in {@link FollowEventHandler}.
	 * @param creature The {@code Creature} that is currently following.
	 */
	@Override
	protected void handleStopFollowMe(Creature creature)
	{
		FollowEventHandler.stopFollow(this, creature);
	}
}
