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
package com.aionemu.gameserver.services;

import java.util.Set;
import java.util.concurrent.Future;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

import java.util.List;

/**
 * Manages the respawning logic for {@link Npc} and {@link VisibleObject} entities.<br>
 * It handles periodic spawning based on {@link SpawnTemplate} configurations within the world.
 * @author ATracer, Source, xTz
 */
public class RespawnService
{
	private static final int IMMEDIATE_DECAY = 5 * 1000;
	private static final int WITHOUT_DROP_DECAY = (int) (1.5 * 60 * 1000);
	private static final int WITH_DROP_DECAY = 5 * 60 * 1000;
	
	/**
	 * Schedules a task to remove an {@link Npc} from the world.<br>
	 * The delay depends on whether the NPC has loot items.<br>
	 * It automatically determines the correct interval based on current drops.
	 * @param npc The {@code Npc} object to be decayed.
	 * @return A {@code Future<?>} representing the pending decay task.
	 */
	public static Future<?> scheduleDecayTask(Npc npc)
	{
		int decayInterval;
		final Set<DropItem> drop = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		
		if (drop == null)
		{
			decayInterval = IMMEDIATE_DECAY;
		}
		else if (drop.isEmpty())
		{
			decayInterval = WITHOUT_DROP_DECAY;
		}
		else
		{
			decayInterval = WITH_DROP_DECAY;
		}
		
		return scheduleDecayTask(npc, decayInterval);
	}
	
	/**
	 * Schedules a task to handle the decay of an {@link Npc}.<br>
	 * This method uses the {@code ThreadPoolManager} to run the task.<br>
	 * The task will execute after the specified time interval.
	 * @param npc The {@link Npc} object that needs to be decayed.
	 * @param decayInterval The delay in milliseconds before the decay occurs.
	 * @return A {@code Future<?>} representing the pending decay task.
	 */
	public static Future<?> scheduleDecayTask(Npc npc, long decayInterval)
	{
		return ThreadPoolManager.getInstance().schedule(new DecayTask(npc.getObjectId()), decayInterval);
	}
	
	/**
	 * Schedules a task to respawn a specific object.<br>
	 * This method uses the {@link ThreadPoolManager} to handle the timing.<br>
	 * It calculates the delay based on the {@code RespawnTime} of the spawn template.
	 * @param visibleObject The {@code VisibleObject} that needs to be respawned.
	 * @return A {@code Future<?>} representing the pending respawn task.
	 */
	public static Future<?> scheduleRespawnTask(VisibleObject visibleObject)
	{
		final int interval = visibleObject.getSpawn().getRespawnTime();
		final SpawnTemplate spawnTemplate = visibleObject.getSpawn();
		final int instanceId = visibleObject.getInstanceId();
		return ThreadPoolManager.getInstance().schedule(new RespawnTask(spawnTemplate, instanceId), interval * 1000);
	}
	
	/**
	 * Creates a new object based on a template in a specific instance.<br>
	 * This method checks if the spawn is valid and active before spawning.<br>
	 * It returns {@code null} if the conditions are not met.
	 * @param spawnTemplate The {@link SpawnTemplate} containing the data for the object.
	 * @param instanceId The unique identifier for the world instance.
	 * @return The newly created {@link VisibleObject} or {@code null}.
	 */
	private static VisibleObject respawn(SpawnTemplate spawnTemplate, int instanceId)
	{
		if (spawnTemplate.isTemporarySpawn() && !spawnTemplate.getTemporarySpawn().isInSpawnTime())
		{
			return null;
		}
		
		final int worldId = spawnTemplate.getWorldId();
		final boolean instanceExists = InstanceService.isInstanceExist(worldId, instanceId);
		if (spawnTemplate.isNoRespawn() || !instanceExists)
		{
			return null;
		}
		
		if (spawnTemplate.hasPool())
		{
			spawnTemplate = spawnTemplate.changeTemplate(instanceId);
		}
		
		return SpawnEngine.spawnObject(spawnTemplate, instanceId);
	}
	
	private static class DecayTask implements Runnable
	{
		private final int npcId;
		
		DecayTask(int npcId)
		{
			this.npcId = npcId;
		}
		
		@Override
		public void run()
		{
			final VisibleObject visibleObject = World.getInstance().findVisibleObject(npcId);
			if (visibleObject != null)
			{
				visibleObject.getController().onDelete();
			}
		}
	}
	
	private static class RespawnTask implements Runnable
	{
		private final SpawnTemplate spawn;
		private final int instanceId;
		
		RespawnTask(SpawnTemplate spawn, int instanceId)
		{
			this.spawn = spawn;
			this.instanceId = instanceId;
		}
		
		@Override
		public void run()
		{
			final List<VisibleObject> visibleObjects = spawn.getVisibleObjects();
			if (visibleObjects != null)
			{
				for (VisibleObject visibleObject : visibleObjects)
				{
					if ((visibleObject != null) && (visibleObject instanceof Npc) && (visibleObject.getInstanceId() == instanceId))
					{
						((Npc) visibleObject).getController().cancelTask(TaskId.RESPAWN);
					}
				}
			}
			
			respawn(spawn, instanceId);
		}
	}
}
