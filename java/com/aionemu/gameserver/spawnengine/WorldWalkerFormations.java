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
package com.aionemu.gameserver.spawnengine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class manages the formation patterns for {@code WorldWalker} entities.<br>
 * It defines how groups of monsters are positioned and arranged within the game world.
 * @author Rolandas
 */
public class WorldWalkerFormations
{
	private final Map<Integer, InstanceWalkerFormations> formations;
	
	/**
	 * Initializes a new instance of the {@code WorldWalkerFormations} class.<br>
	 * This constructor sets up the internal storage for walker formations.
	 */
	public WorldWalkerFormations()
	{
		formations = new ConcurrentHashMap<>();
	}
	
	/**
	 * Retrieves the formation data for a specific instance.<br>
	 * If no formation exists for the given {@code instanceId}, it creates a new one.
	 * @param instanceId The unique identifier for the instance.
	 * @return The {@link InstanceWalkerFormations} associated with the ID.
	 */
	protected InstanceWalkerFormations getInstanceFormations(int instanceId)
	{
		InstanceWalkerFormations instanceFormation = formations.get(instanceId);
		if (instanceFormation == null)
		{
			instanceFormation = new InstanceWalkerFormations();
			formations.put(instanceId, instanceFormation);
		}
		
		return instanceFormation;
	}
}
