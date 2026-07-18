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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;

/**
 * This class manages the formation of walker groups during initial spawns.<br>
 * It ensures that {@link Npc} entities return to their correct positions if they die. This utility is intended for use with patches only.
 * @author vlog
 * @based on Imaginary's imagination
 * @modified Rolandas
 */
public class WalkerFormator
{
	private static final Logger log = LoggerFactory.getLogger(WalkerFormator.class);
	
	/**
	 * Processes NPCs that belong to a cluster group.<br>
	 * This method assigns the {@code npc} to a {@link WalkerGroup} if it exists.<br>
	 * It also handles caching new candidates for clustered formations.
	 * @param npc The {@code Npc} object to process.
	 * @param worldId The ID of the current world.
	 * @param instanceId The ID of the current instance.
	 * @return {@code false} if the NPC was handled by a group or failed processing, and {@code true} if it was cached as a new candidate.
	 */
	public static boolean processClusteredNpc(Npc npc, int worldId, int instanceId)
	{
		final SpawnTemplate spawn = npc.getSpawn();
		if (spawn.getWalkerId() != null)
		{
			final InstanceWalkerFormations formations = WalkerFormationsCache.getInstanceFormations(worldId, instanceId);
			final WalkerGroup wg = formations.getSpawnWalkerGroup(spawn.getWalkerId());
			
			if (wg != null)
			{
				npc.setWalkerGroup(wg);
				wg.respawn(npc);
				return false;
			}
			
			final WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(spawn.getWalkerId());
			if (template == null)
			{
				log.warn("Missing walker ID: " + spawn.getWalkerId());
				return false;
			}
			
			if (template.getPool() < 2)
			{
				return false;
			}
			
			return formations.cacheWalkerCandidate(new ClusteredNpc(npc, instanceId, template));
		}
		
		return false;
	}
	
	/**
	 * Organizes and spawns walker groups for a specific world and instance.<br>
	 * This method retrieves the formations from {@link WalkerFormationsCache}.<br>
	 * It then calls the {@code organizeAndSpawn()} method on those formations.
	 * @param worldId The unique identifier for the world.
	 * @param instanceId The unique identifier for the instance.
	 */
	public static void organizeAndSpawn(int worldId, int instanceId)
	{
		final InstanceWalkerFormations formations = WalkerFormationsCache.getInstanceFormations(worldId, instanceId);
		formations.organizeAndSpawn();
	}
	
	/**
	 * Updates the current cluster for a specific walker group.<br>
	 * This method modifies the formation data for a given world and instance.
	 * @param worldId The unique identifier of the world.
	 * @param instanceId The unique identifier of the instance.
	 * @param walkerGroup The {@code WalkerGroup} to apply to the cluster.
	 */
	public static void changeWalkerGroup(int worldId, int instanceId, WalkerGroup walkerGroup)
	{
		final InstanceWalkerFormations formations = WalkerFormationsCache.getInstanceFormations(worldId, instanceId);
		formations.changeCluster(walkerGroup);
	}
	
	/**
	 * This method handles the cleanup when a game instance is destroyed.<br>
	 * It notifies {@link WalkerFormationsCache} to clear its data for the specific instance.
	 * @param worldId The unique identifier of the world.
	 * @param instanceId The unique identifier of the instance.
	 */
	public static void onInstanceDestroy(int worldId, int instanceId)
	{
		WalkerFormationsCache.onInstanceDestroy(worldId, instanceId);
	}
}
