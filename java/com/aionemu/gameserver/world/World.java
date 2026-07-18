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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.BindPointPosition;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.spawns.basespawns.BaseSpawnTemplate;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.world.container.PlayerContainer;
import com.aionemu.gameserver.world.exceptions.AlreadySpawnedException;
import com.aionemu.gameserver.world.exceptions.DuplicateAionObjectException;
import com.aionemu.gameserver.world.exceptions.WorldMapNotExistException;
import com.aionemu.gameserver.world.knownlist.Visitor;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class manages the global game world environment.<br>
 * It handles the spawning, despawning, and storage of {@link Player} objects and other in-game entities.<br>
 * Additionally, it manages {@link WorldMapTemplate} data and various game instances.
 * @author -Nemesiss-, Source, Wakizashi
 */
public class World
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(World.class);
	/**
	 * Container with all players that entered world.
	 */
	private final PlayerContainer allPlayers;
	/**
	 * Container with all AionObjects in the world [ie Players, Npcs etc]
	 */
	private final Map<Integer, VisibleObject> allObjects;
	/**
	 * Container with all SiegeNpcs in the world [SiegeNpcs,SiegeProtectors etc]
	 */
	private final TIntObjectHashMap<Collection<SiegeNpc>> localSiegeNpcs = new TIntObjectHashMap<>();
	/**
	 * Container with all Npcs related to base spawns
	 */
	private final Map<Integer, List<Npc>> baseNpc;
	/**
	 * Container with all Npcs in the world
	 */
	private final Map<Integer, Npc> allNpcs;
	/**
	 * World maps supported by server.
	 */
	private final TIntObjectHashMap<WorldMap> worldMaps;
	
	/**
	 * Private constructor for the {@link World} class.<br>
	 * This constructor initializes all internal data structures.<br>
	 * It also loads and creates all {@link WorldMap} instances from the data manager.
	 */
	private World()
	{
		allPlayers = new PlayerContainer();
		allObjects = new ConcurrentHashMap<>();
		baseNpc = new ConcurrentHashMap<>();
		allNpcs = new ConcurrentHashMap<>();
		worldMaps = new TIntObjectHashMap<>();
		
		for (WorldMapTemplate template : DataManager.WORLD_MAPS_DATA)
		{
			worldMaps.put(template.getMapId(), new WorldMap(template, this));
		}
		
		log.info("World: " + worldMaps.size() + " worlds map created.");
	}
	
	/**
	 * Retrieves the singleton instance of the {@link World} class.<br>
	 * This provides a global access point to the world manager.
	 * @return The single {@code World} instance.
	 */
	public static World getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Adds a {@code VisibleObject} to the world registry.<br>
	 * This method validates that the object has a valid position before storing it.<br>
	 * It also categorizes the object into specific collections based on its type.
	 * @param object The {@code VisibleObject} to be stored in the world.
	 */
	public void storeObject(VisibleObject object)
	{
		if (object.getPosition() == null)
		{
			log.warn("Not putting object with null position!!! " + object.getObjectTemplate().getTemplateId());
			return;
		}
		
		if (allObjects.put(object.getObjectId(), object) != null)
		{
			throw new DuplicateAionObjectException();
		}
		
		if (object instanceof Player)
		{
			allPlayers.add((Player) object);
		}
		
		if (object instanceof SiegeNpc)
		{
			final SiegeNpc siegeNpc = (SiegeNpc) object;
			Collection<SiegeNpc> npcs = localSiegeNpcs.get(siegeNpc.getSiegeId());
			if (npcs == null)
			{
				synchronized (localSiegeNpcs)
				{
					if (localSiegeNpcs.containsKey(siegeNpc.getSiegeId()))
					{
						npcs = localSiegeNpcs.get(siegeNpc.getSiegeId());
					}
					else
					{
						// We now have multi-threaded siege timers that are thread-safe.
						npcs = new CopyOnWriteArrayList<>();
						localSiegeNpcs.put(siegeNpc.getSiegeId(), npcs);
					}
				}
			}
			
			npcs.add(siegeNpc);
		}
		
		if (object.getSpawn() instanceof BaseSpawnTemplate)
		{
			final BaseSpawnTemplate bst = (BaseSpawnTemplate) object.getSpawn();
			final int baseId = bst.getId();
			if (!baseNpc.containsKey(baseId))
			{
				baseNpc.put(baseId, new ArrayList<>());
			}
			
			baseNpc.get(baseId).add((Npc) object);
		}
		
		if (object instanceof Npc)
		{
			allNpcs.put(object.getObjectId(), (Npc) object);
		}
	}
	
	/**
	 * Removes a {@link VisibleObject} from the world tracking systems.<br>
	 * This method cleans up references in various internal collections based on the object type.<br>
	 * It handles specific logic for {@code SiegeNpc}, {@code Npc}, and {@code Player} types.
	 * @param object The {@code VisibleObject} to be removed from the world.
	 */
	public void removeObject(VisibleObject object)
	{
		allObjects.remove(object.getObjectId());
		
		if (object instanceof SiegeNpc)
		{
			final SiegeNpc siegeNpc = (SiegeNpc) object;
			final Collection<SiegeNpc> locSpawn = localSiegeNpcs.get(siegeNpc.getSiegeId());
			if (!GenericValidator.isBlankOrNull(locSpawn))
			{
				locSpawn.remove(siegeNpc);
			}
		}
		
		if (object.getSpawn() instanceof BaseSpawnTemplate)
		{
			final BaseSpawnTemplate bst = (BaseSpawnTemplate) object.getSpawn();
			final int baseId = bst.getId();
			baseNpc.get(baseId).remove(object);
		}
		
		if (object instanceof Npc)
		{
			allNpcs.remove(object.getObjectId());
		}
		
		if (object instanceof Player)
		{
			allPlayers.remove((Player) object);
		}
	}
	
	/**
	 * Provides an iterator to loop through all players in the world.<br>
	 * This is useful for performing actions on every {@link Player} currently online.
	 * @return An {@code Iterator<Player>} containing all active players.
	 */
	public Iterator<Player> getPlayersIterator()
	{
		return allPlayers.iterator();
	}
	
	/**
	 * Retrieves all {@link SiegeNpc} objects for a specific location.<br>
	 * This method returns an empty set if no NPCs are found.
	 * @param locationId The unique identifier for the map or area.
	 * @return A {@code Collection} of {@code SiegeNpc} objects located at the given ID.
	 */
	public Collection<SiegeNpc> getLocalSiegeNpcs(int locationId)
	{
		final Collection<SiegeNpc> result = localSiegeNpcs.get(locationId);
		return result != null ? result : Collections.<SiegeNpc> emptySet();
	}
	
	/**
	 * Retrieves the list of {@link Npc} objects for a specific base.<br>
	 * This method looks up the spawns associated with the provided {@code baseId}.
	 * @param baseId The unique identifier for the base.
	 * @return A {@link List} containing the NPCs for that base.
	 */
	public List<Npc> getBaseSpawns(int baseId)
	{
		return baseNpc.get(baseId);
	}
	
	/**
	 * Retrieves all {@link Npc} objects currently in the world.
	 * @return A {@code Collection} of all active {@link Npc} instances.
	 */
	public Collection<Npc> getNpcs()
	{
		return allNpcs.values();
	}
	
	/**
	 * Searches for a {@link Player} by their name.<br>
	 * This method retrieves the player from the internal collection.
	 * @param name The unique name of the player to find.
	 * @return The {@code Player} object if found, or {@code null} otherwise.
	 */
	public Player findPlayer(String name)
	{
		return allPlayers.get(name);
	}
	
	/**
	 * Finds a {@link Player} based on their unique ID.<br>
	 * This method retrieves the player from the internal collection.
	 * @param objectId The unique identifier of the player to find.
	 * @return The {@code Player} object if found, or {@code null} if no player exists with that ID.
	 */
	public Player findPlayer(int objectId)
	{
		return allPlayers.get(objectId);
	}
	
	/**
	 * Retrieves a {@link VisibleObject} from the world using its unique ID.<br>
	 * This method looks up the object in the internal collection.
	 * @param objectId The unique identifier of the object to find.
	 * @return The {@code VisibleObject} associated with the provided ID, or {@code null} if not found.
	 */
	public VisibleObject findVisibleObject(int objectId)
	{
		return allObjects.get(objectId);
	}
	
	/**
	 * Checks if a specific object is currently registered in the world.<br>
	 * It verifies if the {@code VisibleObject} exists within the internal collection.
	 * @param object The {@code VisibleObject} to check.
	 * @return {@code true} if the object is in the world, {@code false} otherwise.
	 */
	public boolean isInWorld(VisibleObject object)
	{
		return allObjects.containsKey(object.getObjectId());
	}
	
	/**
	 * Retrieves a {@link WorldMap} based on the provided unique identifier.<br>
	 * This method will throw a {@code WorldMapNotExistException} if the ID is invalid.
	 * @param id The unique identifier of the map to retrieve.
	 * @return The {@code WorldMap} object associated with the given ID.
	 */
	public WorldMap getWorldMap(int id)
	{
		final WorldMap map = worldMaps.get(id);
		/**
		 * Check if world map exist
		 */
		if (map == null)
		{
			throw new WorldMapNotExistException("Map: " + id + " not exist!");
		}
		
		return map;
	}
	
	/**
	 * Updates the spatial coordinates and rotation of a {@link VisibleObject}.<br>
	 * This method modifies the position data for the specified object.
	 * @param object The {@code VisibleObject} to move.
	 * @param newX The new X coordinate.
	 * @param newY The new Y coordinate.
	 * @param newZ The new Z coordinate.
	 * @param newHeading The new heading value as a {@code byte}.
	 */
	public void updatePosition(VisibleObject object, float newX, float newY, float newZ, byte newHeading)
	{
		this.updatePosition(object, newX, newY, newZ, newHeading, true);
	}
	
	/**
	 * Updates the spatial coordinates and heading of a {@link VisibleObject}.<br>
	 * This method handles region transitions and zone revalidation.<br>
	 * It prevents updates if the object is not currently spawned.
	 * @param object The {@link VisibleObject} to move.
	 * @param newX The new X coordinate.
	 * @param newY The new Y coordinate.
	 * @param newZ The new Z coordinate.
	 * @param newHeading The new heading value.
	 * @param updateKnownList Set to {@code true} to trigger a known list update for the object.
	 */
	public void updatePosition(VisibleObject object, float newX, float newY, float newZ, byte newHeading, boolean updateKnownList)
	{
		// prevent updating object position in despawned state
		Npc npc = null;
		
		if (!object.isSpawned())
		{
			return;
		}
		
		if (object instanceof Npc)
		{
			npc = (Npc) object;
		}
		
		final MapRegion oldRegion = object.getActiveRegion();
		if (oldRegion == null)
		{
			log.warn(String.format("CHECKPOINT: %sId:%d oldRegion is null, map - %d, object coordinates - %f %f %f", object.getClass().getSimpleName().replace(".class", ""), npc != null ? npc.getNpcId() : 0, object.getWorldId(), object.getX(), object.getY(), object.getZ()));
			return;
		}
		
		final MapRegion newRegion = oldRegion.getParent().getRegion(newX, newY, newZ);
		if (newRegion == null)
		{
			log.warn(String.format("CHECKPOINT: %sId:%d newRegion is null, map - %d, object coordinates - %f %f %f", object.getClass().getSimpleName().replace(".class", ""), npc != null ? npc.getNpcId() : 0, object.getWorldId(), newX, newY, newZ), new Throwable());
			if (object instanceof Creature)
			{
				((Creature) object).getMoveController().abortMove();
			}
			
			if (object instanceof Player)
			{
				final Player player = (Player) object;
				float x, y, z;
				int worldId;
				byte h = 0;
				
				if (player.getBindPoint() != null)
				{
					final BindPointPosition bplist = player.getBindPoint();
					worldId = bplist.getMapId();
					x = bplist.getX();
					y = bplist.getY();
					z = bplist.getZ();
					h = bplist.getHeading();
				}
				else
				{
					final LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getCommonData().getRace());
					worldId = locationData.getMapId();
					x = locationData.getX();
					y = locationData.getY();
					z = locationData.getZ();
				}
				
				setPosition(object, worldId, x, y, z, h);
			}
			return;
		}
		
		object.getPosition().setXYZH(newX, newY, newZ, newHeading);
		
		if (newRegion != oldRegion)
		{
			if (object instanceof Creature)
			{
				oldRegion.revalidateZones((Creature) object);
				newRegion.revalidateZones((Creature) object);
			}
			
			oldRegion.remove(object);
			newRegion.add(object);
			object.getPosition().setMapRegion(newRegion);
		}
		
		if (updateKnownList)
		{
			object.updateKnownlist();
		}
	}
	
	/**
	 * Sets the spatial coordinates and orientation for a specific object.<br>
	 * This method updates the {@code x}, {@code y}, and {@code z} values.<br>
	 * It also sets the {@code heading} and ensures the object is on the correct map.
	 * @param object The {@link VisibleObject} to move.
	 * @param mapId The unique identifier for the world map.
	 * @param x The new X coordinate.
	 * @param y The new Y coordinate.
	 * @param z The new Z coordinate.
	 * @param heading The direction the object is facing.
	 */
	public void setPosition(VisibleObject object, int mapId, float x, float y, float z, byte heading)
	{
		int instanceId = 1;
		if (object.getWorldId() == mapId)
		{
			instanceId = object.getInstanceId();
		}
		
		this.setPosition(object, mapId, instanceId, x, y, z, heading);
	}
	
	/**
	 * Sets the position of a {@link VisibleObject} in the world.<br>
	 * This method handles despawning the object if it is already active.<br>
	 * It updates the coordinates, heading, and map region for the target instance.
	 * @param object The {@link VisibleObject} to move.
	 * @param mapId The unique identifier of the map.
	 * @param instance The specific instance ID of the map.
	 * @param x The new X coordinate.
	 * @param y The new Y coordinate.
	 * @param z The new Z coordinate.
	 * @param heading The new rotation heading.
	 */
	public void setPosition(VisibleObject object, int mapId, int instance, float x, float y, float z, byte heading)
	{
		if (object.isSpawned())
		{
			despawn(object);
		}
		
		final WorldMapInstance instanceMap = getWorldMap(mapId).getWorldMapInstanceById(instance);
		if (instanceMap == null)
		{
			return;
		}
		
		final WorldPosition newPosition = World.getInstance().createPosition(mapId, x, y, z, heading, instance);
		object.setPosition(newPosition);
		
		final MapRegion region = instanceMap.getRegion(object);
		object.getPosition().setMapRegion(region);
	}
	
	/**
	 * Creates a new {@code WorldPosition} object based on the provided coordinates.<br>
	 * This method calculates the correct map region for the given location.
	 * @param mapId The unique identifier of the world map.
	 * @param x The X coordinate in the world.
	 * @param y The Y coordinate in the world.
	 * @param z The Z coordinate in the world.
	 * @param heading The direction the object is facing.
	 * @param instanceId The specific instance ID of the map.
	 * @return A new {@code WorldPosition} object with the set coordinates and region.
	 */
	public WorldPosition createPosition(int mapId, float x, float y, float z, byte heading, int instanceId)
	{
		final WorldPosition position = new WorldPosition(mapId);
		position.setXYZH(x, y, z, heading);
		position.setMapRegion(getWorldMap(mapId).getWorldMapInstanceById(instanceId).getRegion(x, y, z));
		return position;
	}
	
	/**
	 * Prepares a {@link VisibleObject} for spawning into the world.<br>
	 * This method sets the object state to active and updates its position status.<br>
	 * It also registers the object with its active region and triggers the spawn controller.
	 * @param object The {@code VisibleObject} to be spawned.
	 */
	public void preSpawn(VisibleObject object)
	{
		((Player) object).setState(CreatureState.ACTIVE);
		object.getPosition().setIsSpawned(true);
		object.getActiveRegion().getParent().addObject(object);
		object.getActiveRegion().add(object);
		object.getController().onAfterSpawn();
	}
	
	/**
	 * Spawns a {@link VisibleObject} into the game world.<br>
	 * This method registers the object with its active region and updates its spawn status.<br>
	 * It triggers lifecycle events via the object controller.
	 * @param object The {@code VisibleObject} to be spawned.
	 */
	public void spawn(VisibleObject object)
	{
		if (object.getPosition().isSpawned())
		{
			throw new AlreadySpawnedException();
		}
		
		object.getController().onBeforeSpawn();
		object.getPosition().setIsSpawned(true);
		
		object.getActiveRegion().getParent().addObject(object);
		object.getActiveRegion().add(object);
		object.getController().onAfterSpawn();
		
		object.updateKnownlist();
	}
	
	/**
	 * Removes an object from the active world.<br>
	 * This method handles the cleanup of a {@link VisibleObject}.<br>
	 * It ensures the object is no longer processed by the game engine.
	 * @param object The {@code VisibleObject} to be removed from the world.
	 */
	public void despawn(VisibleObject object)
	{
		despawn(object, true);
	}
	
	/**
	 * Removes an object from the world and updates its spawn status.<br>
	 * This method handles region cleanup and optional list clearing.
	 * @param object The {@link VisibleObject} to be removed from the world.
	 * @param clearKnownlist Set to {@code true} to call {@code clearKnownlist} on the object.
	 */
	public void despawn(VisibleObject object, boolean clearKnownlist)
	{
		final MapRegion oldMapRegion = object.getActiveRegion();
		if (object.getActiveRegion() != null)
		{
			// can be null if an instance gets deleted?
			if (object.getActiveRegion().getParent() != null)
			{
				object.getActiveRegion().getParent().removeObject(object);
			}
			
			object.getActiveRegion().remove(object);
		}
		
		object.getPosition().setIsSpawned(false);
		if ((oldMapRegion != null) && (object instanceof Creature))
		{
			oldMapRegion.revalidateZones((Creature) object);
		}
		
		if (clearKnownlist)
		{
			object.clearKnownlist();
		}
	}
	
	/**
	 * Retrieves a list of all players currently in the world.<br>
	 * This method calls {@code allPlayers}.getAllPlayers() to fetch the data.
	 * @return A {@code Collection} containing all {@link Player} objects.
	 */
	public Collection<Player> getAllPlayers()
	{
		return allPlayers.getAllPlayers();
	}
	
	/**
	 * Iterates through all {@link Player} objects in the current location.<br>
	 * Applies the provided {@code Visitor} to each non-null player found.<br>
	 * Logs an error if any exception occurs during the process.
	 * @param visitor The {@code Visitor} to apply to every player.
	 */
	public void doOnAllPlayers(Visitor<Player> visitor)
	{
		allPlayers.doOnAllPlayers(visitor);
	}
	
	/**
	 * Iterates through every {@link VisibleObject} currently in the world.<br>
	 * Applies the provided {@code visitor} logic to each non-null object found.<br>
	 * Logs an error if any exception occurs during the process.
	 * @param visitor The {@code Visitor<VisibleObject>} to execute on each object.
	 */
	public void doOnAllObjects(Visitor<VisibleObject> visitor)
	{
		try
		{
			for (VisibleObject object : allObjects.values())
			{
				if (object != null)
				{
					visitor.visit(object);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("Exception when running visitor on all objects", ex);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final World instance = new World();
	}
}
