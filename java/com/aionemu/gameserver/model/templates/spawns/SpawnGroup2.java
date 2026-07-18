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
package com.aionemu.gameserver.model.templates.spawns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.taskmanager.AbstractLockManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.dynamicportal.DynamicPortalStateType;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.base.BaseTemplate;
import com.aionemu.gameserver.model.templates.spawns.basespawns.BaseSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.dynamicportalspawns.DynamicPortalSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.riftspawns.RiftSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.vortexspawns.VortexSpawnTemplate;
import com.aionemu.gameserver.model.vortex.VortexStateType;
import com.aionemu.gameserver.spawnengine.SpawnHandlerType;

/**
 * This class manages a specific group of spawn templates for the game world.<br>
 * It handles the logic for spawning entities within {@link BaseSpawnTemplate} categories.<br>
 * Use this class to define and organize secondary spawn behaviors.
 * @author xTz
 * @modified Rolandas
 */
public class SpawnGroup2 extends AbstractLockManager
{
	private static final Logger log = LoggerFactory.getLogger(SpawnGroup2.class);
	private final int worldId;
	private int npcId;
	private int pool;
	private byte difficultId;
	private TemporarySpawn temporarySpawn;
	private int respawnTime;
	private SpawnHandlerType handlerType;
	private final List<SpawnTemplate> spots = new ArrayList<>();
	private HashMap<Integer, HashMap<SpawnTemplate, Boolean>> poolUsedTemplates;
	
	/**
	 * Creates a new {@code SpawnGroup2} instance for a specific world.<br>
	 * This constructor initializes the group using the provided {@link Spawn} data.<br>
	 * It populates the internal list of spawn templates from the source spawn object.
	 * @param worldId The unique identifier for the game world.
	 * @param spawn The {@link Spawn} template used to initialize this group.
	 */
	public SpawnGroup2(int worldId, Spawn spawn)
	{
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates())
		{
			final SpawnTemplate spawnTemplate = new SpawnTemplate(this, template);
			if (spawn.isEventSpawn())
			{
				spawnTemplate.setEventTemplate(spawn.getEventTemplate());
			}
			
			spots.add(spawnTemplate);
		}
	}
	
	/**
	 * Creates a new {@code SpawnGroup2} instance with specific world and race data.<br>
	 * This constructor initializes the spawn group and populates its templates.
	 * @param worldId The unique identifier for the game world.
	 * @param spawn The {@link Spawn} object containing spot template information.
	 * @param id The unique identifier to assign to each spawned template.
	 * @param race The {@link Race} type assigned to the spawned templates.
	 */
	public SpawnGroup2(int worldId, Spawn spawn, int id, Race race)
	{
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates())
		{
			final BaseSpawnTemplate spawnTemplate = new BaseSpawnTemplate(this, template);
			spawnTemplate.setId(id);
			spawnTemplate.setBaseRace(race);
			spots.add(spawnTemplate);
		}
	}
	
	/**
	 * Creates a new {@code SpawnGroup2} instance using a specific world ID and spawn data.<br>
	 * This constructor initializes the group and populates its internal list of templates.
	 * @param worldId The unique identifier for the game world.
	 * @param spawn The {@link Spawn} object containing the base spawn information.
	 * @param id The unique identifier to assign to each spawned template.
	 */
	public SpawnGroup2(int worldId, Spawn spawn, int id)
	{
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates())
		{
			final RiftSpawnTemplate spawnTemplate = new RiftSpawnTemplate(this, template);
			spawnTemplate.setId(id);
			spots.add(spawnTemplate);
		}
	}
	
	/**
	 * Creates a new {@code SpawnGroup2} for a specific world and spawn.<br>
	 * This constructor initializes the group using a {@link Spawn} template.<br>
	 * It populates the internal spots list with {@code VortexSpawnTemplate} objects.
	 * @param worldId The unique identifier for the game world.
	 * @param spawn The base {@link Spawn} data used for initialization.
	 * @param id The unique identifier to assign to each spawned template.
	 * @param type The specific {@code VortexStateType} to apply to the templates.
	 */
	public SpawnGroup2(int worldId, Spawn spawn, int id, VortexStateType type)
	{
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates())
		{
			final VortexSpawnTemplate spawnTemplate = new VortexSpawnTemplate(this, template);
			spawnTemplate.setId(id);
			spawnTemplate.setStateType(type);
			spots.add(spawnTemplate);
		}
	}
	
	/**
	 * Creates a new {@code SpawnGroup2} for a specific siege event.<br>
	 * This constructor initializes the group with siege-specific data.<br>
	 * It populates the spawn spots using the provided {@link Spawn} template.
	 * @param worldId The unique identifier for the game world.
	 * @param spawn The base spawn configuration to use.
	 * @param siegeId The unique identifier for the current siege.
	 * @param race The specific race associated with this siege group.
	 * @param mod The type of modification applied to the siege.
	 */
	public SpawnGroup2(int worldId, Spawn spawn, int siegeId, SiegeRace race, SiegeModType mod)
	{
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates())
		{
			final SiegeSpawnTemplate spawnTemplate = new SiegeSpawnTemplate(this, template);
			spawnTemplate.setSiegeId(siegeId);
			spawnTemplate.setSiegeRace(race);
			spawnTemplate.setSiegeModType(mod);
			spots.add(spawnTemplate);
		}
	}
	
	/**
	 * Creates a new {@code SpawnGroup2} for dynamic portals.<br>
	 * This constructor initializes the group using a specific world and spawn data.<br>
	 * It populates the internal spots list with {@link DynamicPortalSpawnTemplate} objects.
	 * @param worldId The unique identifier for the game world.
	 * @param spawn The base {@link Spawn} configuration to use.
	 * @param id The unique identifier for this specific spawn group.
	 * @param type The state type of the dynamic portal.
	 */
	public SpawnGroup2(int worldId, Spawn spawn, int id, DynamicPortalStateType type)
	{
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates())
		{
			final DynamicPortalSpawnTemplate spawnTemplate = new DynamicPortalSpawnTemplate(this, template);
			spawnTemplate.setId(id);
			spawnTemplate.setDStateType(type);
			spots.add(spawnTemplate);
		}
	}
	
	/**
	 * Sets up the initial values for this group from a {@code Spawn} object.<br>
	 * It copies data like {@code respawnTime}, {@code pool}, and {@code npcId}.<br>
	 * This method also initializes the {@code poolUsedTemplates} map.
	 * @param spawn The {@code Spawn} template used to populate the fields.
	 */
	private void initializing(Spawn spawn)
	{
		temporarySpawn = spawn.getTemporarySpawn();
		respawnTime = spawn.getRespawnTime();
		pool = spawn.getPool();
		npcId = spawn.getNpcId();
		handlerType = spawn.getSpawnHandlerType();
		difficultId = spawn.getDifficultId();
		poolUsedTemplates = new HashMap<>();
	}
	
	/**
	 * Creates a new {@code SpawnGroup2} instance for a specific NPC.<br>
	 * This constructor initializes the group with the provided world and NPC identifiers.
	 * @param worldId The unique identifier for the game world.
	 * @param npcId The unique identifier for the NPC type.
	 */
	public SpawnGroup2(int worldId, int npcId)
	{
		this.worldId = worldId;
		this.npcId = npcId;
	}
	
	/**
	 * Retrieves the list of all spawn templates for this group.<br>
	 * This method returns the internal {@code spots} collection.
	 * @return A {@code List} containing all {@link SpawnTemplate} objects.
	 */
	public List<SpawnTemplate> getSpawnTemplates()
	{
		return spots;
	}
	
	/**
	 * Adds a new {@code SpawnTemplate} to the list of available spots.<br>
	 * This method uses a write lock to ensure thread safety during the addition.
	 * @param spawnTemplate The {@code SpawnTemplate} object to be added.
	 */
	public void addSpawnTemplate(SpawnTemplate spawnTemplate)
	{
		super.writeLock();
		try
		{
			spots.add(spawnTemplate);
		}
		finally
		{
			super.writeUnlock();
		}
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return worldId;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
	}
	
	/**
	 * Retrieves the {@code TemporarySpawn} associated with this group.<br>
	 * This method returns the current temporary spawn object.
	 * @return The {@code TemporarySpawn} instance or {@code null} if none exists.
	 */
	public TemporarySpawn geTemporarySpawn()
	{
		return temporarySpawn;
	}
	
	/**
	 * Retrieves the current value of the spawn pool.<br>
	 * This value is used to determine the size or quantity of the spawn group.
	 * @return The integer value of the {@code pool}.
	 */
	public int getPool()
	{
		return pool;
	}
	
	/**
	 * Checks if this spawn group has an active pool.<br>
	 * It returns {@code true} if the {@code pool} value is greater than {@code 0}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if a pool exists, {@code false} otherwise.
	 */
	public boolean hasPool()
	{
		return pool > 0;
	}
	
	/**
	 * Retrieves the time it takes for an NPC to reappear.<br>
	 * This value is used by the spawn engine to manage respawns.
	 * @return The respawn time as an {@code int}.
	 */
	public int getRespawnTime()
	{
		return respawnTime;
	}
	
	/**
	 * Sets the time required for an NPC to reappear.<br>
	 * This updates the {@code respawnTime} field in this object.
	 * @param respawnTime The new time value to set.
	 */
	public void setRespawnTime(int respawnTime)
	{
		this.respawnTime = respawnTime;
	}
	
	/**
	 * Checks if this spawn group is a temporary spawn.<br>
	 * It returns {@code true} if the {@code temporarySpawn} field is not {@code null}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if it is a temporary spawn, {@code false} otherwise.
	 */
	public boolean isTemporarySpawn()
	{
		return temporarySpawn != null;
	}
	
	/**
	 * Retrieves the type of spawn handler used by this group.<br>
	 * This helps identify how the spawning logic is processed.
	 * @return the {@code SpawnHandlerType} associated with this instance.
	 */
	public SpawnHandlerType getHandlerType()
	{
		return handlerType;
	}
	
	/**
	 * Retrieves a random {@link SpawnTemplate} from the available pool.<br>
	 * It filters out templates that are already in use for the given {@code instanceId}.<br>
	 * If no templates are available, it returns {@code null}.
	 * @param instanceId The unique identifier for the current spawn instance.
	 * @return A random {@link SpawnTemplate} or {@code null} if none are available.
	 */
	public SpawnTemplate getRndTemplate(int instanceId)
	{
		final List<SpawnTemplate> allTemplates = spots;
		final List<SpawnTemplate> templates = new ArrayList<>();
		super.readLock();
		try
		{
			for (SpawnTemplate template : allTemplates)
			{
				if (!isTemplateUsed(instanceId, template))
				{
					templates.add(template);
				}
			}
			
			if (templates.size() == 0)
			{
				log.warn("Pool size more then spots, npcId: " + npcId + ", worldId: " + worldId);
				return null;
			}
		}
		finally
		{
			super.readUnlock();
		}
		
		final SpawnTemplate spawnTemplate = templates.get(Rnd.get(0, templates.size() - 1));
		setTemplateUse(instanceId, spawnTemplate, true);
		return spawnTemplate;
	}
	
	/**
	 * Updates the usage status of a specific spawn template for a given instance.<br>
	 * This method tracks whether a {@link SpawnTemplate} has been used within an instance.
	 * @param instanceId The unique identifier for the current instance.
	 * @param template The {@link SpawnTemplate} to update.
	 * @param isUsed The new usage status, set to {@code true} if used or {@code false} if not.
	 */
	public void setTemplateUse(int instanceId, SpawnTemplate template, boolean isUsed)
	{
		super.writeLock();
		try
		{
			HashMap<SpawnTemplate, Boolean> states = poolUsedTemplates.get(instanceId);
			if (states == null)
			{
				states = new HashMap<>();
				poolUsedTemplates.put(instanceId, states);
			}
			
			states.put(template, isUsed);
		}
		finally
		{
			super.writeUnlock();
		}
	}
	
	/**
	 * Checks if a specific template has been used for a given instance.<br>
	 * This method retrieves the usage status from the {@code poolUsedTemplates} map.<br>
	 * It returns {@code false} if the instance or template is not found.
	 * @param instanceId The unique identifier for the spawn instance.
	 * @param template The {@link SpawnTemplate} to check.
	 * @return {@code true} if the template was used, otherwise {@code false}.
	 */
	public boolean isTemplateUsed(int instanceId, SpawnTemplate template)
	{
		super.readLock();
		try
		{
			final HashMap<SpawnTemplate, Boolean> states = poolUsedTemplates.get(instanceId);
			if (states == null)
			{
				return false;
			}
			
			final Boolean state = states.get(template);
			if (state == null)
			{
				return false;
			}
			
			return state;
		}
		finally
		{
			super.readUnlock();
		}
	}
	
	/**
	 * Resets the usage status of all templates for a specific instance.<br>
	 * This method sets the value to {@code false} for every template in the map.<br>
	 * It only performs an action if the {@code instanceId} exists in the pool.
	 * @param instanceId The unique identifier for the spawn instance.
	 */
	public void resetTemplates(int instanceId)
	{
		final HashMap<SpawnTemplate, Boolean> states = poolUsedTemplates.get(instanceId);
		if (states == null)
		{
			return;
		}
		
		super.writeLock();
		try
		{
			for (SpawnTemplate template : states.keySet())
			{
				states.put(template, false);
			}
		}
		finally
		{
			super.writeUnlock();
		}
	}
	
	/**
	 * Retrieves the unique identifier for the difficulty level.<br>
	 * This value is used to distinguish between different difficulty settings.
	 * @return the {@code byte} value representing the difficulty ID.
	 */
	public byte getDifficultId()
	{
		return difficultId;
	}
}
