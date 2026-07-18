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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.TemporarySpawn;

import java.util.List;

/**
 * This class manages the lifecycle of {@link TemporarySpawn} objects.<br>
 * It handles the dynamic spawning and removal of NPCs based on temporary rules.<br>
 * Use this engine to process short-term spawn events that are not permanent.
 * @author xTz
 */
public class TemporarySpawnEngine
{
	private static final Map<SpawnGroup2, HashSet<Integer>> temporarySpawns = new HashMap<>();
	
	/**
	 * Triggers the spawning of all available conquest NPCs.<br>
	 * It iterates through every position in {@code spawnsByPosition}.<br>
	 * For each location, it selects one random {@link SpawnTemplate} to spawn.<br>
	 * The total number of spawned objects is logged via {@code log}.
	 */
	public static void spawnAll()
	{
		spawn(true);
	}
	
	/**
	 * This method is called every time the game hour changes.<br>
	 * It clears all current temporary spawns.<br>
	 * It then triggers a new spawn cycle using {@code spawn}.
	 */
	public static void onHourChange()
	{
		despawn();
		spawn(false);
	}
	
	/**
	 * Removes temporary spawns from the game world.<br>
	 * This method checks all {@link SpawnGroup2} templates.<br>
	 * It cancels respawn tasks and deletes objects that are no longer active.
	 */
	private static void despawn()
	{
		for (SpawnGroup2 spawn : temporarySpawns.keySet())
		{
			for (SpawnTemplate template : spawn.getSpawnTemplates())
			{
				if (!template.getTemporarySpawn().isInSpawnTime())
				{
					final List<VisibleObject> objects = template.getVisibleObjects();
					if (objects == null)
					{
						continue;
					}
					
					for (VisibleObject object : objects)
					{
						if (object instanceof Npc)
						{
							final Npc npc = (Npc) object;
							npc.getController().cancelTask(TaskId.RESPAWN);
						}
						
						if (object.isSpawned())
						{
							object.getController().onDelete();
						}
						
						spawn.setTemplateUse(object.getInstanceId(), template, false);
					}
					
					objects.clear();
				}
			}
		}
	}
	
	/**
	 * This method handles the logic for spawning objects into the game world.<br>
	 * It iterates through all registered spawn groups and checks if they should be active.<br>
	 * It uses {@code int)} to create the actual entities.
	 * @param startCheck Determines whether to include extra conditions like respawn times during the initial check.
	 */
	private static void spawn(boolean startCheck)
	{
		for (SpawnGroup2 spawn : temporarySpawns.keySet())
		{
			final HashSet<Integer> instances = temporarySpawns.get(spawn);
			if (spawn.hasPool())
			{
				final TemporarySpawn temporarySpawn = spawn.geTemporarySpawn();
				if ((temporarySpawn.canSpawn() || (startCheck && (spawn.getRespawnTime() != 0) && temporarySpawn.isInSpawnTime())))
				{
					for (Integer instanceId : instances)
					{
						spawn.resetTemplates(instanceId);
						for (int pool = 0; pool < spawn.getPool(); pool++)
						{
							final SpawnTemplate template = spawn.getRndTemplate(instanceId);
							SpawnEngine.spawnObject(template, instanceId);
						}
					}
				}
			}
			else
			{
				for (SpawnTemplate template : spawn.getSpawnTemplates())
				{
					final TemporarySpawn temporarySpawn = template.getTemporarySpawn();
					if ((temporarySpawn.isInSpawnTime() || (startCheck && !template.isNoRespawn() && temporarySpawn.isInSpawnTime())))
					{
						for (Integer instanceId : instances)
						{
							if (!spawn.isTemplateUsed(instanceId, template))
							{
								SpawnEngine.spawnObject(template, instanceId);
								spawn.setTemplateUse(instanceId, template, true);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Adds a specific {@code SpawnGroup2} to the temporary spawn list for a given instance.<br>
	 * This method ensures that the group is tracked by its unique {@code instanceId}.<br>
	 * It updates the internal {@code temporarySpawns} map.
	 * @param spawn The {@code SpawnGroup2} object to be added.
	 * @param instanceId The unique ID of the instance where the spawn occurs.
	 */
	public static void addSpawnGroup(SpawnGroup2 spawn, int instanceId)
	{
		HashSet<Integer> instances = temporarySpawns.get(spawn);
		if (instances == null)
		{
			instances = new HashSet<>();
			temporarySpawns.put(spawn, instances);
		}
		
		instances.add(instanceId);
	}
}
