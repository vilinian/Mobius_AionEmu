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
package com.aionemu.gameserver.world;

import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.instance.handlers.InstanceHandler;

/**
 * This factory class is responsible for creating and initializing {@link InstanceEngine} objects.<br>
 * It handles the generation of world map instances based on configuration data.
 * @author ATracer
 */
public class WorldMapInstanceFactory
{
	/**
	 * Creates a new {@link WorldMapInstance} based on a parent map.<br>
	 * This method uses a default owner ID of {@code 0}.
	 * @param parent The {@link WorldMap} that this instance belongs to.
	 * @param instanceId The unique identifier for the new instance.
	 * @return A new {@code WorldMapInstance} object.
	 */
	public static WorldMapInstance createWorldMapInstance(WorldMap parent, int instanceId)
	{
		return createWorldMapInstance(parent, instanceId, 0);
	}
	
	/**
	 * Creates a new {@link WorldMapInstance} based on the provided map and IDs.<br>
	 * This method automatically selects the correct instance type depending on the map ID.<br>
	 * It also assigns the appropriate {@link InstanceHandler} to the new instance.
	 * @param parent The {@code WorldMap} that serves as the base for this instance.
	 * @param instanceId The unique identifier for the specific instance.
	 * @param ownerId The identifier of the player or entity who owns the instance.
	 * @return A newly created and initialized {@code WorldMapInstance}.
	 */
	public static WorldMapInstance createWorldMapInstance(WorldMap parent, int instanceId, int ownerId)
	{
		WorldMapInstance worldMapInstance = null;
		if (parent.getMapId() == WorldMapType.RESHANTA.getId())
		{
			worldMapInstance = new WorldMap3DInstance(parent, instanceId);
		}
		else
		{
			worldMapInstance = new WorldMap2DInstance(parent, instanceId, ownerId);
		}
		
		final InstanceHandler instanceHandler = InstanceEngine.getInstance().getNewInstanceHandler(parent.getMapId());
		worldMapInstance.setInstanceHandler(instanceHandler);
		return worldMapInstance;
	}
}
