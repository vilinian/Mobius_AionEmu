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
package com.aionemu.gameserver.ai2.scenario;

import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This class serves as a blueprint for defining scenario behaviors.<br>
 * It provides the base configuration and data needed to initialize an {@link AbstractAI} instance.<br>
 * Developers use this template to create various scripted events within the game world.
 * @author ATracer
 */
public class ScenarioTemplate implements AI2Scenario
{
	/**
	 * Handles specific actions when a {@link Creature} triggers an event.<br>
	 * This method is called by the scenario system to process creature interactions.
	 * @param ai The {@code AbstractAI} instance currently executing the logic.
	 * @param event The type of {@link AIEventType} that occurred.
	 * @param creature The {@link Creature} object involved in the event.
	 */
	@Override
	public void onCreatureEvent(AbstractAI ai, AIEventType event, Creature creature)
	{
		// TODO Auto-generated method stub
	}
	
	/**
	 * Handles general events for a specific {@link AbstractAI}.<br>
	 * This method is triggered when an {@code AIEventType} occurs.
	 * @param ai The {@link AbstractAI} instance that triggered the event.
	 * @param event The type of {@link AIEventType} that occurred.
	 */
	@Override
	public void onGeneralEvent(AbstractAI ai, AIEventType event)
	{
		// TODO Auto-generated method stub
	}
}
