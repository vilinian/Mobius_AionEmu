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
package com.aionemu.gameserver.ai2;

import java.util.Arrays;
import java.util.EnumSet;

import com.aionemu.gameserver.ai2.event.AIEventType;

/**
 * This enum defines the various state transitions and events for the AI system.<br>
 * It is used by {@link AIEventType} to manage how entities react to different game conditions.
 * @author ATracer
 */
public enum StateEvents
{
	CREATED_EVENTS(AIEventType.SPAWNED),
	DESPAWN_EVENTS(AIEventType.RESPAWNED, AIEventType.SPAWNED, AIEventType.DIED),
	DEAD_EVENTS(AIEventType.DESPAWNED, AIEventType.DROP_REGISTERED);
	
	private final EnumSet<AIEventType> events;
	
	/**
	 * Creates a new {@link StateEvents} instance.<br>
	 * This constructor initializes the internal set of events.
	 * @param aiEventTypes A variable number of {@code AIEventType} values to include.
	 */
	private StateEvents(AIEventType... aiEventTypes)
	{
		events = EnumSet.copyOf(Arrays.asList(aiEventTypes));
	}
	
	/**
	 * Checks if a specific event is associated with this state.<br>
	 * This method returns {@code true} if the event exists in the internal set.
	 * @param event The {@link AIEventType} to check.
	 * @return {@code true} if the event is present, otherwise {@code false}.
	 */
	public boolean hasEvent(AIEventType event)
	{
		return events.contains(event);
	}
}
