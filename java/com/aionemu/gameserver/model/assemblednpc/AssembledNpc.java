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
package com.aionemu.gameserver.model.assemblednpc;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an NPC composed of multiple parts or components.<br>
 * This class manages the assembly and data for complex non-player characters.
 * @author xTz
 */
public class AssembledNpc
{
	private List<AssembledNpcPart> assembledPatrs = new ArrayList<>();
	private final long spawnTime = System.currentTimeMillis();
	private final int routeId;
	private final int mapId;
	
	/**
	 * Creates a new instance of an {@link AssembledNpc}.<br>
	 * This constructor initializes the NPC with its required location and parts.
	 * @param routeId The unique identifier for the movement route.
	 * @param mapId The unique identifier for the map where the NPC spawns.
	 * @param liveTime The duration in milliseconds that the NPC remains active.
	 * @param assembledPatrs A {@code List} containing all parts of the assembled NPC.
	 */
	public AssembledNpc(int routeId, int mapId, int liveTime, List<AssembledNpcPart> assembledPatrs)
	{
		this.assembledPatrs = assembledPatrs;
		this.routeId = routeId;
		this.mapId = mapId;
	}
	
	/**
	 * Retrieves the list of parts that make up this {@link AssembledNpc}.<br>
	 * This method returns all components currently attached to the NPC.
	 * @return A {@code List} containing all {@code AssembledNpcPart} objects.
	 */
	public List<AssembledNpcPart> getAssembledParts()
	{
		return assembledPatrs;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC's current route.<br>
	 * This value is assigned during the creation of the {@link AssembledNpc}.
	 * @return The {@code int} representing the route ID.
	 */
	public int getRouteId()
	{
		return routeId;
	}
	
	/**
	 * Retrieves the unique identifier for the map.<br>
	 * This value is assigned during the creation of the {@link AssembledNpc}.
	 * @return The {@code int} representing the map ID.
	 */
	public int getMapId()
	{
		return mapId;
	}
	
	/**
	 * Calculates how long the NPC has been on the map.<br>
	 * It returns the difference between the current time and the {@code spawnTime}.
	 * @return The total time in milliseconds since the NPC was spawned.
	 */
	public long getTimeOnMap()
	{
		return System.currentTimeMillis() - spawnTime;
	}
}
