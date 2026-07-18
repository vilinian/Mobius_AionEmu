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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.configs.administration.DeveloperConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Represents a specific geographical area or section within the game world.<br>
 * This class handles spatial data and object management for a portion of the {@code ZoneInstance}.
 * @author -Nemesiss-
 */
public class MapRegion
{
	private static final Logger log = LoggerFactory.getLogger(MapRegion.class);
	/**
	 * Region id of this map region [NOT WORLD ID!]
	 */
	private final int regionId;
	/**
	 * WorldMapInstance witch is parent of this map region.
	 */
	private final WorldMapInstance parent;
	/**
	 * Surrounding regions + self.
	 */
	private volatile MapRegion[] neighbours = new MapRegion[0];
	/**
	 * Objects on this map region.
	 */
	private final Map<Integer, VisibleObject> objects = new ConcurrentHashMap<>();
	private final AtomicInteger playerCount = new AtomicInteger(0);
	private final AtomicBoolean regionActive = new AtomicBoolean(false);
	private final int zoneCount;
	/**
	 * Zones in this region
	 */
	private Map<Integer, TreeSet<ZoneInstance>> zoneMap;
	
	/**
	 * Creates a new instance of a {@code MapRegion}.<br>
	 * This constructor initializes the region with its unique ID and parent map.<br>
	 * It also sets up the associated zones and adds the region to its own neighbors.
	 * @param id The unique identifier for this specific region.
	 * @param parent The {@link WorldMapInstance} that contains this region.
	 * @param zones An array of {@link ZoneInstance} objects located within this region.
	 */
	MapRegion(int id, WorldMapInstance parent, ZoneInstance[] zones)
	{
		regionId = id;
		this.parent = parent;
		zoneCount = zones.length;
		createZoneMap(zones);
		addNeighbourRegion(this);
	}
	
	/**
	 * Retrieves the unique identifier for the map.<br>
	 * This value corresponds to the {@code mapId} field.
	 * @return The {@code Integer} ID of the map.
	 */
	public Integer getMapId()
	{
		return getParent().getMapId();
	}
	
	/**
	 * Retrieves the {@link World} associated with this map region.<br>
	 * This method gets the world by accessing the parent {@link WorldMapInstance}.
	 * @return The {@code World} object.
	 */
	public World getWorld()
	{
		return getParent().getWorld();
	}
	
	/**
	 * Retrieves the unique identifier for this map region.<br>
	 * This value is distinct from the world ID.
	 * @return the {@code int} representation of the region ID.
	 */
	public int getRegionId()
	{
		return regionId;
	}
	
	/**
	 * Retrieves the {@link WorldMapInstance} that contains this region.<br>
	 * This method returns the parent map associated with the current {@code MapRegion}.
	 * @return The parent {@link WorldMapInstance} object.
	 */
	public WorldMapInstance getParent()
	{
		return parent;
	}
	
	/**
	 * Retrieves all visible objects located within this map region.<br>
	 * The objects are stored in a {@code Map}.
	 * @return A {@code Map} containing the unique IDs and corresponding {@link VisibleObject} instances.
	 */
	public Map<Integer, VisibleObject> getObjects()
	{
		return objects;
	}
	
	/**
	 * Retrieves all {@link StaticDoor} objects located in this map region.<br>
	 * The results are stored in a {@code Map} where the key is the door's static ID.
	 * @return A {@code Map} containing the doors and their corresponding IDs.
	 */
	public Map<Integer, StaticDoor> getDoors()
	{
		final Map<Integer, StaticDoor> doors = new HashMap<>();
		for (VisibleObject obj : objects.values())
		{
			if (obj instanceof StaticDoor)
			{
				final StaticDoor door = (StaticDoor) obj;
				doors.put(door.getSpawn().getStaticId(), door);
			}
		}
		
		return doors;
	}
	
	/**
	 * Retrieves the regions adjacent to this one.<br>
	 * This includes the current {@code MapRegion} instance itself.
	 * @return an array of {@link MapRegion} objects.
	 */
	public MapRegion[] getNeighbours()
	{
		return neighbours;
	}
	
	/**
	 * Adds a new adjacent region to the current {@code MapRegion}.<br>
	 * This updates the internal list of surrounding regions.
	 * @param neighbour The {@code MapRegion} object to be added.
	 */
	void addNeighbourRegion(MapRegion neighbour)
	{
		neighbours = Arrays.copyOf(neighbours, neighbours.length + 1);
		neighbours[neighbours.length - 1] = neighbour;
	}
	
	/**
	 * Adds a {@link VisibleObject} to this map region.<br>
	 * It updates the internal object collection and checks for activity changes if the object is a {@link Player}.<br>
	 * If {@code DeveloperConfig.SPAWN_CHECK} is enabled, it validates that the object is within a valid zone.
	 * @param object The {@link VisibleObject} to be added to the region.
	 */
	void add(VisibleObject object)
	{
		if (objects.put(object.getObjectId(), object) == null)
		{
			if (object instanceof Player)
			{
				checkActiveness(playerCount.incrementAndGet() > 0);
			}
			else if (DeveloperConfig.SPAWN_CHECK)
			{
				final Iterator<TreeSet<ZoneInstance>> zoneIter = zoneMap.values().iterator();
				while (zoneIter.hasNext())
				{
					final TreeSet<ZoneInstance> zones = zoneIter.next();
					for (ZoneInstance zone : zones)
					{
						if (!zone.isInsideCordinate(object.getX(), object.getY(), object.getZ()))
						{
							continue;
						}
						
						if (zone.getZoneTemplate().getZoneType() != ZoneClassName.DUMMY)
						{
							return;
						}
					}
				}
				
				log.warn("Outside any zones: id=" + object + " > X:" + object.getX() + ",Y:" + object.getY() + ",Z:" + object.getZ());
			}
		}
	}
	
	/**
	 * Removes a specific {@code VisibleObject} from this map region.<br>
	 * This method updates the internal object list and checks if any players remain.<br>
	 * If a {@link Player} is removed, it may trigger a change in the region's active state.
	 * @param object The {@code VisibleObject} to be removed from the region.
	 */
	void remove(VisibleObject object)
	{
		if (objects.remove(object.getObjectId()) != null)
		{
			if (object instanceof Player)
			{
				checkActiveness(playerCount.decrementAndGet() > 0);
			}
		}
	}
	
	/**
	 * Updates the active state of this map region.<br>
	 * If {@code true} is passed, it triggers the activation process.<br>
	 * If {@code false} is passed, it triggers the deactivation process.
	 * @param active The desired activity status for the region.
	 */
	void checkActiveness(boolean active)
	{
		if (active && regionActive.compareAndSet(false, true))
		{
			startActivation();
		}
		else if (!active)
		{
			startDeactivation();
		}
	}
	
	/**
	 * Starts the activation process for this map region.<br>
	 * This method schedules a task to run after {@code 1000} milliseconds.<br>
	 * It calls {@code activateObjects} and activates all neighboring regions.
	 */
	void startActivation()
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			log.debug("Activating in map {} region {}", getMapId(), regionId);
			MapRegion.this.activateObjects();
			for (MapRegion neighbor : getNeighbours())
			{
				neighbor.activate();
			}
		}, 1000);
	}
	
	/**
	 * Schedules the deactivation of this map region and its neighbors.<br>
	 * The process starts after a delay of {@code 60000} milliseconds.<br>
	 * It checks if each neighbor is active before calling {@code deactivate}.
	 */
	void startDeactivation()
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			log.debug("Deactivating in map {} region {}", getMapId(), regionId);
			for (MapRegion neighbor : getNeighbours())
			{
				if (!neighbor.isNeighboursActive())
				{
					neighbor.deactivate();
				}
			}
		}, 60000);
	}
	
	/**
	 * Activates this map region.<br>
	 * This method sets the active state to {@code true}.<br>
	 * It also triggers the internal {@code activateObjects} method.
	 */
	public void activate()
	{
		if (regionActive.compareAndSet(false, true))
		{
			activateObjects();
		}
	}
	
	/**
	 * Iterates through all visible objects in this region.<br>
	 * Sends an {@code ACTIVATE} event to any object that is a {@link Creature}.
	 */
	private void activateObjects()
	{
		for (VisibleObject visObject : objects.values())
		{
			if (visObject instanceof Creature)
			{
				final Creature creature = (Creature) visObject;
				creature.getAi2().onGeneralEvent(AIEventType.ACTIVATE);
			}
		}
	}
	
	/**
	 * Deactivates the current map region.<br>
	 * This method sets the active state to {@code false}.<br>
	 * It also calls {@code deactivateObjects} if the region was previously active.
	 */
	public void deactivate()
	{
		if (regionActive.compareAndSet(true, false))
		{
			deactivateObjects();
		}
	}
	
	/**
	 * Iterates through all objects in the current region.<br>
	 * Sends a deactivation event to specific creatures.<br>
	 * This excludes raid monsters and certain siege NPCs.
	 */
	private void deactivateObjects()
	{
		for (VisibleObject visObject : objects.values())
		{
			if ((visObject instanceof Creature) && !(SiegeConfig.BALAUR_AUTO_ASSAULT && (visObject instanceof SiegeNpc)) && !((Creature) visObject).isFlag() && !((Creature) visObject).isRaidMonster())
			{
				final Creature creature = (Creature) visObject;
				creature.getAi2().onGeneralEvent(AIEventType.DEACTIVATE);
			}
		}
	}
	
	/**
	 * Checks if the current map region is active.<br>
	 * This method considers the {@code WorldConfig.WORLD_ACTIVE_TRACE} setting.<br>
	 * It returns {@code true} if the trace is disabled or if the region is active.
	 * @return {@code true} if the region is active, {@code false} otherwise.
	 */
	public boolean isMapRegionActive()
	{
		return !WorldConfig.WORLD_ACTIVE_TRACE || regionActive.get();
	}
	
	/**
	 * Checks if any adjacent regions are currently active.<br>
	 * A region is considered active if it is enabled and has at least one player.
	 * @return {@code true} if at least one neighbour is active, {@code false} otherwise.
	 */
	boolean isNeighboursActive()
	{
		for (int i = 0; i < neighbours.length; i++)
		{
			final MapRegion r = neighbours[i];
			if ((r != null) && r.regionActive.get() && (r.playerCount.get() > 0))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Updates the zone status for a specific creature.<br>
	 * This method checks if the {@code creature} is still inside various zones.<br>
	 * It triggers {@code onLeave} or {@code onEnter} based on the result of {@code revalidate}.
	 * @param creature The {@code Creature} to check for zone transitions.
	 */
	public void revalidateZones(Creature creature)
	{
		for (Map.Entry<Integer, TreeSet<ZoneInstance>> e : zoneMap.entrySet())
		{
			boolean foundZone = false;
			final int category = e.getKey();
			final TreeSet<ZoneInstance> zones = e.getValue();
			for (ZoneInstance zone : zones)
			{
				if (!creature.isSpawned() || ((category != -1) && foundZone))
				{
					zone.onLeave(creature);
					continue;
				}
				
				final boolean result = zone.revalidate(creature);
				if (!result)
				{
					zone.onLeave(creature);
					continue;
				}
				
				if (category != -1)
				{
					foundZone = true;
				}
				
				zone.onEnter(creature);
			}
		}
	}
	
	/**
	 * Retrieves all {@link ZoneInstance} objects that contain the specified creature.<br>
	 * This method checks every zone associated with this region.<br>
	 * It returns a list of zones where the {@code creature} is currently located.
	 * @param creature The {@code Creature} to check for zone membership.
	 * @return A {@code List} of {@link ZoneInstance} objects containing the creature.
	 */
	public List<ZoneInstance> getZones(Creature creature)
	{
		final List<ZoneInstance> z = new ArrayList<>();
		for (Map.Entry<Integer, TreeSet<ZoneInstance>> e : zoneMap.entrySet())
		{
			final TreeSet<ZoneInstance> zones = e.getValue();
			for (ZoneInstance zone : zones)
			{
				if (zone.isInsideCreature(creature))
				{
					z.add(zone);
				}
			}
		}
		
		return z;
	}
	
	/**
	 * This method checks if any zone within the region triggers an event when a creature dies.<br>
	 * It iterates through all zones to see if the {@code target} is inside one.<br>
	 * If a zone's {@code Creature)} method returns {@code true}, this method also returns {@code true}.
	 * @param attacker The creature that performed the attack.
	 * @param target The creature that died.
	 * @return {@code true} if any zone triggered an event, otherwise {@code false}.
	 */
	public boolean onDie(Creature attacker, Creature target)
	{
		for (Map.Entry<Integer, TreeSet<ZoneInstance>> e : zoneMap.entrySet())
		{
			final TreeSet<ZoneInstance> zones = e.getValue();
			for (ZoneInstance zone : zones)
			{
				if (zone.isInsideCreature(target))
				{
					if (zone.onDie(attacker, target))
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific coordinate is located within a given zone.<br>
	 * This method searches for the {@code ZoneName} and validates the position.
	 * @param zoneName The name of the zone to check.
	 * @param x The X coordinate of the position.
	 * @param y The Y coordinate of the position.
	 * @param z The Z coordinate of the position.
	 * @return {@code true} if the coordinates are inside the zone, otherwise {@code false}.
	 */
	public boolean isInsideZone(ZoneName zoneName, float x, float y, float z)
	{
		for (Map.Entry<Integer, TreeSet<ZoneInstance>> e : zoneMap.entrySet())
		{
			final TreeSet<ZoneInstance> zones = e.getValue();
			for (ZoneInstance zone : zones)
			{
				if (zone.getZoneTemplate().getName() != zoneName)
				{
					continue;
				}
				
				return zone.isInsideCordinate(x, y, z);
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific creature is located within a given zone.<br>
	 * This method searches through all zones associated with the map region.<br>
	 * It returns {@code true} if the creature is inside any instance of the specified {@link ZoneName}.<br>
	 * If no such zone exists or the creature is not inside it, it returns {@code false}.
	 * @param zoneName The name of the zone to check.
	 * @param creature The {@link Creature} to verify against the zone.
	 * @return {@code true} if the creature is inside the zone, otherwise {@code false}.
	 */
	public boolean isInsideZone(ZoneName zoneName, Creature creature)
	{
		for (Map.Entry<Integer, TreeSet<ZoneInstance>> e : zoneMap.entrySet())
		{
			final TreeSet<ZoneInstance> zones = e.getValue();
			for (ZoneInstance zone : zones)
			{
				if (zone.getZoneTemplate().getName() != zoneName)
				{
					continue;
				}
				
				return zone.isInsideCreature(creature);
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a {@link Creature} is located within a specific zone.<br>
	 * This method searches through all zones associated with the map region.<br>
	 * It verifies if the creature's position matches the zone boundaries.
	 * @param zoneName The name of the zone to check.
	 * @param creature The {@link Creature} to verify.
	 * @return {@code true} if the creature is inside the specified zone, otherwise {@code false}.
	 */
	public boolean isInsideItemUseZone(ZoneName zoneName, Creature creature)
	{
		for (Map.Entry<Integer, TreeSet<ZoneInstance>> e : zoneMap.entrySet())
		{
			final TreeSet<ZoneInstance> zones = e.getValue();
			for (ZoneInstance zone : zones)
			{
				if (!zone.getZoneTemplate().getXmlName().startsWith(zoneName.toString()) || !zone.isInsideCreature(creature))
				{
					continue;
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Initializes the {@code zoneMap} by categorizing all provided zones.<br>
	 * It groups each {@link ZoneInstance} based on its template priority and type.<br>
	 * This helps in organizing zones for faster retrieval during gameplay.
	 * @param zones An array of {@link ZoneInstance} objects to be mapped.
	 */
	private void createZoneMap(ZoneInstance[] zones)
	{
		zoneMap = new HashMap<>();
		for (int i = 0; i < zones.length; i++)
		{
			final ZoneInstance zone = zones[i];
			int category = -1;
			if (zone.getZoneTemplate().getPriority() != 0)
			{
				category = zone.getZoneTemplate().getZoneType().ordinal();
			}
			
			TreeSet<ZoneInstance> zoneCategory = zoneMap.get(category);
			if (zoneCategory == null)
			{
				zoneCategory = new TreeSet<>();
				zoneMap.put(category, zoneCategory);
			}
			
			zoneCategory.add(zone);
		}
	}
	
	/**
	 * Retrieves the total number of zones.<br>
	 * This value is stored in the {@code zoneCount} field.
	 * @return The total count of zones as an {@code int}.
	 */
	public int getZoneCount()
	{
		return zoneCount;
	}
}
