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
import com.aionemu.gameserver.ai2.handler.SimpleAbyssGuardHandler;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This class handles the basic artificial intelligence for {@link Creature} entities of the abyss guard type.<br>
 * It extends {@link AggressiveNpcAI2} to provide standard aggressive behaviors for these NPCs.
 * @author Rolandas
 */
@AIName("simple_abyssguard")
public class AbyssGuardSimpleAI2 extends AggressiveNpcAI2
{
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
		
		switch (eventType)
		{
			case DIALOG_START:
			case DIALOG_FINISH:
				return isNonFightingState();
			case CREATURE_MOVED:
				return getState() != AIState.FIGHT;
			default:
				break;
		}
		
		return true;
	}
	
	/**
	 * Processes the logic when a {@code Creature} is spotted.<br>
	 * This method delegates the behavior to the {@link SimpleAbyssGuardHandler}.
	 * @param creature The {@code Creature} that was seen.
	 */
	@Override
	protected void handleCreatureSee(Creature creature)
	{
		SimpleAbyssGuardHandler.onCreatureSee(this, creature);
	}
	
	/**
	 * This method handles the logic when a {@code Creature} moves.<br>
	 * It triggers the movement behavior for the AI.
	 * @param creature The {@code Creature} that has moved.
	 */
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		SimpleAbyssGuardHandler.onCreatureMoved(this, creature);
	}
	
	/**
	 * This method checks if the AI should defend against a specific attacker.<br>
	 * It evaluates the {@code attacker} to determine defensive actions.<br>
	 * Currently, this logic is not implemented.
	 * @param attacker The {@code Creature} that is attacking the NPC.
	 * @return {@code false} because no defense logic is currently active.
	 */
	@Override
	protected boolean handleGuardAgainstAttacker(Creature attacker)
	{
		return false;
	}
}
