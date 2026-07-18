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
 * This class provides a caching mechanism for walker formations.<br>
 * It stores and retrieves formation data to improve performance during spawning.<br>
 * It uses a {@code ConcurrentHashMap} to ensure thread-safe access to the cached values.
 * @author Rolandas
 */
class WalkerFormationsCache
{
	private static Map<Integer, WorldWalkerFormations> formations = new ConcurrentHashMap<>();
	
	/**
	 * Private constructor for the {@link WalkerFormationsCache} class.<br>
	 * This prevents other classes from creating new instances of this cache.
	 */
	private WalkerFormationsCache()
	{
	}
	
	/**
	 * Retrieves the walker formations for a specific instance.<br>
	 * This method looks up the data based on the provided {@code worldId} and {@code instanceId}.<br>
	 * It ensures that the required formation data is loaded into the cache.
	 * @param worldId The unique identifier for the world.
	 * @param instanceId The unique identifier for the instance.
	 * @return The {@link InstanceWalkerFormations} object for the given IDs.
	 */
	protected static InstanceWalkerFormations getInstanceFormations(int worldId, int instanceId)
	{
		WorldWalkerFormations wwf = formations.get(worldId);
		if (wwf == null)
		{
			wwf = new WorldWalkerFormations();
			formations.put(worldId, wwf);
		}
		
		return wwf.getInstanceFormations(instanceId);
	}
	
	/**
	 * This method is called when a game instance is destroyed.<br>
	 * It cleans up the resources for the specific world and instance.<br>
	 * It calls {@code int)} to handle the cleanup.
	 * @param worldId The unique identifier of the world.
	 * @param instanceId The unique identifier of the instance.
	 */
	protected static void onInstanceDestroy(int worldId, int instanceId)
	{
		getInstanceFormations(worldId, instanceId).onInstanceDestroy();
	}
}
