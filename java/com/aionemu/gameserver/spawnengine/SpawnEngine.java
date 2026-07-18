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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.administration.DeveloperConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.basespawns.BaseSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.riftspawns.RiftSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.vortexspawns.VortexSpawnTemplate;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.services.HousingService;
import com.aionemu.gameserver.services.rift.RiftManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * This class manages the spawning of {@link Npc} entities within the game world.<br>
 * The current implementation is temporary and will be replaced in a future update.
 * @author Luno modified by ATracer, Source, Wakizashi, xTz, nrg
 * @modif Dr2co
 */
public class SpawnEngine
{
	private static Logger log = LoggerFactory.getLogger(SpawnEngine.class);
	
	/**
	 * Creates and registers a new {@link VisibleObject} based on a template.<br>
	 * This method retrieves the object using the provided index.<br>
	 * It also handles registration for event-based spawns.
	 * @param spawn The {@code SpawnTemplate} containing the data for the new object.
	 * @param instanceIndex The unique index used to identify this specific instance.
	 * @return The newly created {@link VisibleObject}.
	 */
	public static VisibleObject spawnObject(SpawnTemplate spawn, int instanceIndex)
	{
		final VisibleObject visObj = getSpawnedObject(spawn, instanceIndex);
		if (spawn.isEventSpawn())
		{
			spawn.getEventTemplate().addSpawnedObject(visObj);
		}
		
		spawn.addVisibleObject(visObj);
		return visObj;
	}
	
	/**
	 * Retrieves a {@link VisibleObject} based on the provided template and index.<br>
	 * This method determines the correct spawning logic by checking the type of {@code spawn}.<br>
	 * It handles different types like gatherables, base NPCs, rift NPCs, siege NPCs, and vortex NPCs.
	 * @param spawn The {@link SpawnTemplate} containing the data for the object to be spawned.
	 * @param instanceIndex The unique index for the specific instance of the object.
	 * @return The created {@link VisibleObject} instance.
	 */
	private static VisibleObject getSpawnedObject(SpawnTemplate spawn, int instanceIndex)
	{
		final int objectId = spawn.getNpcId();
		
		if ((objectId > 400000) && (objectId < 499999))
		{
			return VisibleObjectSpawner.spawnGatherable(spawn, instanceIndex);
		}
		else if (spawn instanceof BaseSpawnTemplate)
		{
			return VisibleObjectSpawner.spawnBaseNpc((BaseSpawnTemplate) spawn, instanceIndex);
		}
		else if (spawn instanceof RiftSpawnTemplate)
		{
			return VisibleObjectSpawner.spawnRiftNpc((RiftSpawnTemplate) spawn, instanceIndex);
		}
		else if (spawn instanceof SiegeSpawnTemplate)
		{
			return VisibleObjectSpawner.spawnSiegeNpc((SiegeSpawnTemplate) spawn, instanceIndex);
		}
		else if (spawn instanceof VortexSpawnTemplate)
		{
			return VisibleObjectSpawner.spawnInvasionNpc((VortexSpawnTemplate) spawn, instanceIndex);
		}
		else
		{
			return VisibleObjectSpawner.spawnNpc(spawn, instanceIndex);
		}
	}
	
	/**
	 * Creates a new {@link SpawnTemplate} for an NPC.<br>
	 * This method initializes the spawn with basic coordinates and orientation.
	 * @param worldId The unique identifier for the world map.
	 * @param npcId The unique identifier for the NPC type.
	 * @param x The X coordinate of the spawn location.
	 * @param y The Y coordinate of the spawn location.
	 * @param z The Z coordinate of the spawn location.
	 * @param heading The direction the NPC faces at the spawn point.
	 * @return A new {@code SpawnTemplate} object.
	 */
	static SpawnTemplate createSpawnTemplate(int worldId, int npcId, float x, float y, float z, byte heading)
	{
		return new SpawnTemplate(new SpawnGroup2(worldId, npcId), x, y, z, heading, 0, null, 0, 0);
	}
	
	/**
	 * Creates a new {@link SpawnTemplate} with specific creator information.<br>
	 * This method initializes the spawn coordinates and then sets the owner details.
	 * @param worldId The unique identifier for the world.
	 * @param npcId The unique identifier for the NPC type.
	 * @param x The X coordinate of the spawn point.
	 * @param y The Y coordinate of the spawn point.
	 * @param z The Z coordinate of the spawn point.
	 * @param heading The direction the NPC faces.
	 * @param creatorId The ID of the player who created this spawn.
	 * @param masterName The name associated with the master of the spawn.
	 * @return A new {@link SpawnTemplate} object.
	 */
	static SpawnTemplate createSpawnTemplate(int worldId, int npcId, float x, float y, float z, byte heading, int creatorId, String masterName)
	{
		final SpawnTemplate template = createSpawnTemplate(worldId, npcId, x, y, z, heading);
		template.setCreatorId(creatorId);
		template.setMasterName(masterName);
		return template;
	}
	
	/**
	 * Creates a new {@link SpawnTemplate} for an NPC.<br>
	 * This method initializes the spawn with specific coordinates and walking data.
	 * @param worldId The unique identifier for the world.
	 * @param npcId The unique identifier for the NPC type.
	 * @param x The X coordinate of the spawn location.
	 * @param y The Y coordinate of the spawn location.
	 * @param z The Z coordinate of the spawn location.
	 * @param heading The direction the NPC faces.
	 * @param walkerId The unique identifier for the walking path.
	 * @param walkerIdx The index of the specific point on the walking path.
	 * @return A new {@link SpawnTemplate} object.
	 */
	static SpawnTemplate createSpawnTemplate(int worldId, int npcId, float x, float y, float z, byte heading, String walkerId, int walkerIdx)
	{
		return new SpawnTemplate(new SpawnGroup2(worldId, npcId), x, y, z, heading, 0, walkerId, walkerIdx, 0, 0);
	}
	
	/**
	 * Creates a new {@link SiegeSpawnTemplate} for the game world.<br>
	 * This method initializes a siege-specific spawn with the provided coordinates and attributes.
	 * @param worldId The unique identifier for the world.
	 * @param npcId The unique identifier for the NPC to be spawned.
	 * @param siegeId The unique identifier for the siege event.
	 * @param race The {@link SiegeRace} of the spawn.
	 * @param mod The {@link SiegeModType} applied to the spawn.
	 * @param x The X coordinate in the world.
	 * @param y The Y coordinate in the world.
	 * @param z The Z coordinate in the world.
	 * @param heading The rotation direction of the spawn.
	 * @return A new {@link SiegeSpawnTemplate} object.
	 */
	public static SiegeSpawnTemplate addNewSiegeSpawn(int worldId, int npcId, int siegeId, SiegeRace race, SiegeModType mod, float x, float y, float z, byte heading)
	{
		final SiegeSpawnTemplate spawnTemplate = new SiegeSpawnTemplate(new SpawnGroup2(worldId, npcId), x, y, z, heading, 0, null, 0, 0);
		spawnTemplate.setSiegeId(siegeId);
		spawnTemplate.setSiegeRace(race);
		spawnTemplate.setSiegeModType(mod);
		return spawnTemplate;
	}
	
	/**
	 * Creates a new {@link SpawnTemplate} with a specific respawn time.<br>
	 * This method initializes the template using coordinates and heading data.
	 * @param worldId The unique identifier for the world.
	 * @param npcId The unique identifier for the NPC type.
	 * @param x The X coordinate of the spawn point.
	 * @param y The Y coordinate of the spawn point.
	 * @param z The Z coordinate of the spawn point.
	 * @param heading The direction the NPC faces.
	 * @param respawnTime The time in seconds before the NPC reappears.
	 * @return A new {@link SpawnTemplate} object.
	 */
	public static SpawnTemplate addNewSpawn(int worldId, int npcId, float x, float y, float z, byte heading, int respawnTime)
	{
		final SpawnTemplate spawnTemplate = createSpawnTemplate(worldId, npcId, x, y, z, heading);
		spawnTemplate.setRespawnTime(respawnTime);
		return spawnTemplate;
	}
	
	/**
	 * Creates a new {@link SpawnTemplate} with specific coordinates and walking data.<br>
	 * This method initializes the template using the provided world, NPC, and position details.<br>
	 * It also sets the required respawn time for the entity.
	 * @param worldId The unique identifier for the world map.
	 * @param npcId The unique identifier for the NPC type.
	 * @param x The X coordinate of the spawn point.
	 * @param y The Y coordinate of the spawn point.
	 * @param z The Z coordinate of the spawn point.
	 * @param heading The direction the NPC faces at the spawn point.
	 * @param respawnTime The time in seconds before the NPC can reappear.
	 * @param walkerId The unique identifier for the walking path.
	 * @param walkerIdx The index of the specific point on the walking path.
	 * @return A new {@link SpawnTemplate} object containing all the specified data.
	 */
	public static SpawnTemplate addNewSpawn(int worldId, int npcId, float x, float y, float z, byte heading, int respawnTime, String walkerId, int walkerIdx)
	{
		final SpawnTemplate spawnTemplate = createSpawnTemplate(worldId, npcId, x, y, z, heading, walkerId, walkerIdx);
		spawnTemplate.setRespawnTime(respawnTime);
		return spawnTemplate;
	}
	
	/**
	 * Creates a new spawn template for an NPC that only appears once.<br>
	 * This method uses a respawn time of {@code 0}.
	 * @param worldId The unique identifier for the world map.
	 * @param npcId The unique identifier for the NPC type.
	 * @param x The X coordinate in the world.
	 * @param y The Y coordinate in the world.
	 * @param z The Z coordinate in the world.
	 * @param heading The direction the NPC faces.
	 * @return A new {@link SpawnTemplate} object.
	 */
	public static SpawnTemplate addNewSingleTimeSpawn(int worldId, int npcId, float x, float y, float z, byte heading)
	{
		return addNewSpawn(worldId, npcId, x, y, z, heading, 0);
	}
	
	/**
	 * Creates a new single-time spawn template for an NPC.<br>
	 * This method sets the creator and master name on the new template.
	 * @param worldId The unique identifier of the world.
	 * @param npcId The unique identifier of the NPC to spawn.
	 * @param x The X coordinate of the spawn location.
	 * @param y The Y coordinate of the spawn location.
	 * @param z The Z coordinate of the spawn location.
	 * @param heading The direction the NPC faces.
	 * @param creatorId The ID of the user who created this spawn.
	 * @param masterName The name associated with the master of this spawn.
	 * @return A new {@link SpawnTemplate} object.
	 */
	public static SpawnTemplate addNewSingleTimeSpawn(int worldId, int npcId, float x, float y, float z, byte heading, int creatorId, String masterName)
	{
		final SpawnTemplate template = addNewSpawn(worldId, npcId, x, y, z, heading, 0);
		template.setCreatorId(creatorId);
		template.setMasterName(masterName);
		return template;
	}
	
	/**
	 * Creates a new single-time spawn template for an NPC.<br>
	 * This method sets the respawn time to {@code 0}.<br>
	 * It uses the provided coordinates and rotation to position the spawn.
	 * @param worldId The unique identifier of the world.
	 * @param npcId The unique identifier of the NPC type.
	 * @param x The X coordinate in the world.
	 * @param y The Y coordinate in the world.
	 * @param z The Z coordinate in the world.
	 * @param heading The rotation angle of the spawn.
	 * @param walkerId The unique identifier for the walker entity.
	 * @param walkerIdx The index of the walker.
	 * @return A new {@link SpawnTemplate} object.
	 */
	public static SpawnTemplate addNewSingleTimeSpawn(int worldId, int npcId, float x, float y, float z, byte heading, String walkerId, int walkerIdx)
	{
		return addNewSpawn(worldId, npcId, x, y, z, heading, 0, walkerId, walkerIdx);
	}
	
	/**
	 * Places a {@code VisibleObject} into the game world.<br>
	 * This method uses the coordinates and rotation from a {@link SpawnTemplate}.<br>
	 * It handles the positioning for a specific instance index.
	 * @param visibleObject The object to be added to the world.
	 * @param spawn The template containing the location data.
	 * @param instanceIndex The unique index for this specific instance.
	 */
	static void bringIntoWorld(VisibleObject visibleObject, SpawnTemplate spawn, int instanceIndex)
	{
		bringIntoWorld(visibleObject, spawn.getWorldId(), instanceIndex, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getHeading());
	}
	
	/**
	 * Adds a {@code VisibleObject} to the game world.<br>
	 * This method stores the object and sets its position coordinates.<br>
	 * It then triggers the spawn process for the object.
	 * @param visibleObject The object to be added to the world.
	 * @param worldId The unique identifier of the world.
	 * @param instanceIndex The index of the specific instance.
	 * @param x The X coordinate in the world.
	 * @param y The Y coordinate in the world.
	 * @param z The Z coordinate in the world.
	 * @param h The heading value for the object.
	 */
	public static void bringIntoWorld(VisibleObject visibleObject, int worldId, int instanceIndex, float x, float y, float z, byte h)
	{
		final World world = World.getInstance();
		world.storeObject(visibleObject);
		world.setPosition(visibleObject, worldId, instanceIndex, x, y, z, h);
		world.spawn(visibleObject);
	}
	
	/**
	 * Adds a {@code VisibleObject} to the game world.<br>
	 * This method stores and spawns the object in the current {@link World}.<br>
	 * It throws an exception if the object position is {@code null}.
	 * @param visibleObject The object to be added to the world.
	 */
	public static void bringIntoWorld(VisibleObject visibleObject)
	{
		if (visibleObject.getPosition() == null)
		{
			throw new IllegalArgumentException("Position is null");
		}
		
		final World world = World.getInstance();
		world.storeObject(visibleObject);
		world.spawn(visibleObject);
	}
	
	/**
	 * This method triggers the spawning of all available conquest NPCs.<br>
	 * It iterates through every position in {@code spawnsByPosition}.<br>
	 * For each location, it selects one random {@link SpawnTemplate} to spawn.<br>
	 * The total number of spawned objects is logged via {@code log}.
	 */
	public static void spawnAll()
	{
		if (!DeveloperConfig.SPAWN_ENABLE)
		{
			log.info("[SpawnService] Spawns are disabled ??!!??");
			return;
		}
		
		for (WorldMapTemplate worldMapTemplate : DataManager.WORLD_MAPS_DATA)
		{
			if (worldMapTemplate.isInstance())
			{
				continue;
			}
			
			spawnBasedOnTemplate(worldMapTemplate);
		}
		
		DataManager.SPAWNS_DATA2.clearTemplates();
		printWorldSpawnStats();
		ConquestSpawnManager.spawnAll();
	}
	
	/**
	 * Spawns all objects for a specific world map.<br>
	 * This method retrieves the {@code WorldMapTemplate} using the provided {@code worldId}.<br>
	 * It triggers the spawning process if the template is not an instance.
	 * @param worldId The unique identifier of the world map to load.
	 */
	public static void spawnWorldMap(int worldId)
	{
		final WorldMapTemplate template = DataManager.WORLD_MAPS_DATA.getTemplate(worldId);
		if ((template != null) && !template.isInstance())
		{
			spawnBasedOnTemplate(template);
		}
	}
	
	/**
	 * Spawns instances based on the provided {@code WorldMapTemplate}.<br>
	 * It calculates the total number of twin spawns to create.<br>
	 * This method calls {@code int, byte)} for each instance.
	 * @param worldMapTemplate The template containing map and spawn data.
	 */
	private static void spawnBasedOnTemplate(WorldMapTemplate worldMapTemplate)
	{
		int twinSpawns = worldMapTemplate.getTwinCount();
		if (twinSpawns == 0)
		{
			twinSpawns = 1;
		}
		
		twinSpawns += worldMapTemplate.getBeginnerTwinCount();
		final int mapId = worldMapTemplate.getMapId();
		
		for (int instanceId = 1; instanceId <= twinSpawns; instanceId++)
		{
			spawnInstance(mapId, instanceId, (byte) 0);
		}
	}
	
	/**
	 * Spawns a new instance of an object into the game world.<br>
	 * This method uses the provided IDs to locate and create the correct entity.
	 * @param worldId The unique identifier for the world map.
	 * @param instanceId The specific index of the instance to spawn in.
	 * @param difficultId The difficulty level associated with the spawn.
	 */
	public static void spawnInstance(int worldId, int instanceId, byte difficultId)
	{
		spawnInstance(worldId, instanceId, difficultId, 0);
	}
	
	/**
	 * Spawns all relevant objects and NPCs for a specific world instance.<br>
	 * This method processes spawn groups based on the provided difficulty ID.<br>
	 * It also handles special spawns like rifts, static objects, and houses.
	 * @param worldId The unique identifier of the world map.
	 * @param instanceId The specific instance index for the current session.
	 * @param difficultId The difficulty level used to filter which spawns to load.
	 * @param ownerId The ID of the player who owns the housing in this instance.
	 */
	public static void spawnInstance(int worldId, int instanceId, byte difficultId, int ownerId)
	{
		final List<SpawnGroup2> worldSpawns = DataManager.SPAWNS_DATA2.getSpawnsByWorldId(worldId);
		final WorldMapTemplate worldTemplate = DataManager.WORLD_MAPS_DATA.getTemplate(worldId);
		StaticDoorSpawnManager.spawnTemplate(worldId, instanceId);
		
		if (worldSpawns != null)
		{
			for (SpawnGroup2 spawn : worldSpawns)
			{
				final int difficult = spawn.getDifficultId();
				if ((difficult != 0) && (difficult != difficultId))
				{
					continue;
				}
				
				// Disable temporary spawns in instances because TemporarySpawnEngine does not support removing them.
				if (spawn.isTemporarySpawn() && !worldTemplate.isInstance())
				{
					TemporarySpawnEngine.addSpawnGroup(spawn, instanceId);
					continue;
				}
				
				if (spawn.getHandlerType() != null)
				{
					switch (spawn.getHandlerType())
					{
						case RIFT:
							RiftManager.addRiftSpawnTemplate(spawn);
							break;
						case STATIC:
							StaticObjectSpawnManager.spawnTemplate(spawn, instanceId);
							break;
						case CONQUEST:
							ConquestSpawnManager.addConquestSpawnTemplate(spawn);
							break;
						default:
							break;
					}
				}
				else if (spawn.hasPool() && checkPool(spawn))
				{
					spawn.resetTemplates(instanceId);
					for (int i = 0; i < spawn.getPool(); i++)
					{
						final SpawnTemplate template = spawn.getRndTemplate(instanceId);
						if (template == null)
						{
							break;
						}
						
						spawnObject(template, instanceId);
					}
				}
				else
				{
					for (SpawnTemplate template : spawn.getSpawnTemplates())
					{
						spawnObject(template, instanceId);
					}
				}
			}
			
			WalkerFormator.organizeAndSpawn(worldId, instanceId);
		}
		
		HousingService.getInstance().spawnHouses(worldId, instanceId, ownerId);
	}
	
	/**
	 * Verifies if the current number of templates is within the allowed pool size.<br>
	 * It logs a warning if there are more templates than the defined limit.
	 * @param spawn The {@code SpawnGroup2} object to check.
	 * @return {@code true} if the pool size is valid, or {@code false} otherwise.
	 */
	private static boolean checkPool(SpawnGroup2 spawn)
	{
		if (spawn.getSpawnTemplates().size() < spawn.getPool())
		{
			log.warn("[SpawnService] Pool size more then spots, npcId: " + spawn.getNpcId() + ", worldId: " + spawn.getWorldId());
			return false;
		}
		
		return true;
	}
	
	/**
	 * Prints the total count of spawned objects to the server log.<br>
	 * This method calculates the number of {@code Npc} and {@code Gatherable} objects across all maps.<br>
	 * It also triggers a check for missing spawns via the {@link QuestEngine}.
	 */
	public static void printWorldSpawnStats()
	{
		final StatsCollector visitor = new StatsCollector();
		World.getInstance().doOnAllObjects(visitor);
		GameServer.log.info("[SpawnEngine] Loaded totally " + visitor.getNpcCount() + " NpcSpawns on " + visitor.getNpcMapCount() + " Maps");
		GameServer.log.info("[SpawnEngine] Loaded totally " + visitor.getGatherableCount() + " GatherableSpawns on " + visitor.getGatherMapCount() + " Maps");
		QuestEngine.getInstance().printMissingSpawns();
	}
	
	static class StatsCollector implements Visitor<VisibleObject>
	{
		int npcCount;
		int gatherableCount;
		List<Integer> npc_maps = new ArrayList<>();
		List<Integer> gather_maps = new ArrayList<>();
		
		@Override
		public void visit(VisibleObject object)
		{
			if (object instanceof Npc)
			{
				if (!npc_maps.contains(object.getWorldId()))
				{
					npc_maps.add(object.getWorldId());
				}
				
				npcCount++;
			}
			else if (object instanceof Gatherable)
			{
				if (!gather_maps.contains(object.getWorldId()))
				{
					gather_maps.add(object.getWorldId());
				}
				
				gatherableCount++;
			}
		}
		
		public int getNpcCount()
		{
			return npcCount;
		}
		
		public int getGatherableCount()
		{
			return gatherableCount;
		}
		
		public int getNpcMapCount()
		{
			return npc_maps.size();
		}
		
		public int getGatherMapCount()
		{
			return gather_maps.size();
		}
	}
	
	// Service Spawn for Cron Service
	/**
	 * Creates a new {@link SpawnTemplate} for an NPC using specific coordinates.<br>
	 * This method sets up the spawn point with a defined rotation and movement behavior.
	 * @param worldId The unique identifier of the world.
	 * @param npcId The unique identifier of the NPC to be spawned.
	 * @param x The X coordinate for the spawn location.
	 * @param y The Y coordinate for the spawn location.
	 * @param z The Z coordinate for the spawn location.
	 * @param heading The rotation angle of the NPC at the spawn point.
	 * @param randomWalk The value determining the NPC's random movement behavior.
	 * @return A new {@link SpawnTemplate} object.
	 */
	static SpawnTemplate createSpawnTemplateCron(int worldId, int npcId, float x, float y, float z, byte heading, int randomWalk)
	{
		return new SpawnTemplate(new SpawnGroup2(worldId, npcId), x, y, z, heading, randomWalk, null, 0, 0);
	}
	
	/**
	 * Creates a new cron-based spawn template for an NPC.<br>
	 * This method initializes the spawn with specific coordinates and movement settings.<br>
	 * It then applies the designated respawn time to the resulting {@code SpawnTemplate}.
	 * @param worldId The unique identifier of the world.
	 * @param npcId The unique identifier of the NPC to be spawned.
	 * @param x The X coordinate for the spawn location.
	 * @param y The Y coordinate for the spawn location.
	 * @param z The Z coordinate for the spawn location.
	 * @param heading The direction the NPC faces when it spawns.
	 * @param respawnTime The time in seconds before the NPC can reappear.
	 * @param randomWalk The value determining the NPC's random movement behavior.
	 * @return A new {@code SpawnTemplate} object containing all provided data.
	 */
	public static SpawnTemplate addNewSpawn2Cron(int worldId, int npcId, float x, float y, float z, byte heading, int respawnTime, int randomWalk)
	{
		final SpawnTemplate spawnTemplateCron = createSpawnTemplateCron(worldId, npcId, x, y, z, heading, randomWalk);
		spawnTemplateCron.setRespawnTime(respawnTime);
		return spawnTemplateCron;
	}
	
	/**
	 * Creates a new single-time spawn template for an NPC.<br>
	 * This method uses the {@code randomWalk} value to determine movement behavior.<br>
	 * It internally calls {@code int, float, float, float, byte, int, int)}.
	 * @param worldId The unique identifier for the world map.
	 * @param npcId The unique identifier for the NPC type to spawn.
	 * @param x The X coordinate of the spawn location.
	 * @param y The Y coordinate of the spawn location.
	 * @param z The Z coordinate of the spawn location.
	 * @param heading The initial rotation direction of the NPC.
	 * @param randomWalk The value used to define the NPC's random walking behavior.
	 * @return A new {@link SpawnTemplate} object containing the specified data.
	 */
	public static SpawnTemplate addNewSingleTimeSpawnCron(int worldId, int npcId, float x, float y, float z, byte heading, int randomWalk)
	{
		return addNewSpawn2Cron(worldId, npcId, x, y, z, heading, 0, randomWalk);
	}
}
