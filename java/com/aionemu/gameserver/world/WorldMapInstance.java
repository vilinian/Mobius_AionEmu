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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.instance.handlers.InstanceHandler;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.Trap;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.league.League;
import com.aionemu.gameserver.model.templates.quest.QuestNpc;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.world.exceptions.DuplicateAionObjectException;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.RegionZone;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.ZoneService;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Represents a specific instance of a world map within the game environment.<br>
 * This class manages the spatial data and objects associated with a {@link WorldMapTemplate}.
 * @author -Nemesiss-
 */
public abstract class WorldMapInstance
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(WorldMapInstance.class);
	/**
	 * Size of region
	 */
	public static final int regionSize = WorldConfig.WORLD_REGION_SIZE;
	/**
	 * WorldMap witch is parent of this instance.
	 */
	private final WorldMap parent;
	/**
	 * Map of active regions.
	 */
	protected final TIntObjectHashMap<MapRegion> regions = new TIntObjectHashMap<>();
	/**
	 * All objects spawned in this world map instance
	 */
	private final Map<Integer, VisibleObject> worldMapObjects = new ConcurrentHashMap<>();
	/**
	 * All players spawned in this world map instance
	 */
	private final Map<Integer, Player> worldMapPlayers = new ConcurrentHashMap<>();
	private final Set<Integer> registeredObjects = ConcurrentHashMap.newKeySet();
	private PlayerGroup registeredGroup = null;
	private Future<?> emptyInstanceTask = null;
	/**
	 * Id of this instance (channel)
	 */
	private final int instanceId;
	private final List<Integer> questIds = new ArrayList<>();
	private InstanceHandler instanceHandler;
	private Map<ZoneName, ZoneInstance> zones = new HashMap<>();
	
	// TODO: Merge this with owner
	private Integer soloPlayer;
	private PlayerAlliance registredAlliance;
	private League registredLeague;
	
	/**
	 * Creates a new {@link WorldMapInstance} for a specific map.<br>
	 * This constructor initializes the zones and regions based on the parent map.
	 * @param parent The {@link WorldMap} that this instance belongs to.
	 * @param instanceId The unique identifier for this specific instance.
	 */
	public WorldMapInstance(WorldMap parent, int instanceId)
	{
		this.parent = parent;
		this.instanceId = instanceId;
		zones = ZoneService.getInstance().getZoneInstancesByWorldId(parent.getMapId());
		initMapRegions();
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
	 * Retrieves the {@link WorldMap} that contains this instance.<br>
	 * This method returns the parent map associated with the current map instance.
	 * @return The parent {@code WorldMap} object.
	 */
	public WorldMap getParent()
	{
		return parent;
	}
	
	/**
	 * Retrieves the template for this map.<br>
	 * This provides access to the base configuration of the {@link WorldMap}.
	 * @return The {@code WorldMapTemplate} associated with this map.
	 */
	public WorldMapTemplate getTemplate()
	{
		return parent.getTemplate();
	}
	
	/**
	 * Finds the {@link MapRegion} for a specific visible object.<br>
	 * This method uses the coordinates of the provided {@code object}.
	 * @param object The {@code VisibleObject} to check.
	 * @return The {@link MapRegion} where the object is located.
	 */
	MapRegion getRegion(VisibleObject object)
	{
		return getRegion(object.getX(), object.getY(), object.getZ());
	}
	
	/**
	 * Returns MapRegion that contains given x,y coordinates. If the region doesn't exist, it's created.
	 * @param x
	 * @param y
	 * @param z
	 * @return a MapRegion
	 */
	public abstract MapRegion getRegion(float x, float y, float z);
	
	/**
	 * Create new MapRegion and add link to neighbours.
	 * @param regionId
	 * @return newly created map region
	 */
	protected abstract MapRegion createMapRegion(int regionId);
	
	protected abstract void initMapRegions();
	
	public abstract boolean isPersonal();
	
	public abstract int getOwnerId();
	
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
	 * Adds a {@link VisibleObject} to the current world map instance.<br>
	 * This method registers the object and handles specific logic for {@link Npc} and {@link Player} types.<br>
	 * It will throw a {@code DuplicateAionObjectException} if an object with the same ID already exists.
	 * @param object The {@link VisibleObject} to be added to the instance.
	 */
	public void addObject(VisibleObject object)
	{
		if (worldMapObjects.put(object.getObjectId(), object) != null)
		{
			throw new DuplicateAionObjectException("Object with templateId " + String.valueOf(object.getObjectTemplate().getTemplateId()) + " already spawned in the instance " + String.valueOf(getMapId()) + " " + String.valueOf(getInstanceId()));
		}
		
		if (object instanceof Npc)
		{
			final QuestNpc data = QuestEngine.getInstance().getQuestNpc(((Npc) object).getNpcId());
			if (data != null)
			{
				for (int id : data.getOnQuestStart())
				{
					if (!questIds.contains(id))
					{
						questIds.add(id);
					}
				}
			}
		}
		
		if (object instanceof Player)
		{
			if (getParent().isPossibleFly())
			{
				((Player) object).setInsideZoneType(ZoneType.FLY);
			}
			
			worldMapPlayers.put(object.getObjectId(), (Player) object);
		}
	}
	
	/**
	 * Removes a specific {@link AionObject} from the world map instance.<br>
	 * This method handles special cleanup for {@link Player} objects.<br>
	 * It updates internal collections and clears zone status if applicable.
	 * @param object The {@code AionObject} to be removed from the map.
	 */
	public void removeObject(AionObject object)
	{
		worldMapObjects.remove(object.getObjectId());
		if (object instanceof Player)
		{
			if (getParent().isPossibleFly())
			{
				((Player) object).unsetInsideZoneType(ZoneType.FLY);
			}
			
			worldMapPlayers.remove(object.getObjectId());
		}
	}
	
	/**
	 * Finds a specific {@link Npc} by its unique identifier.<br>
	 * This method searches through all visible objects in the current instance.<br>
	 * It returns the matching {@code Npc} object if found.
	 * @param npcId The unique ID of the NPC to search for.
	 * @return The {@code Npc} object, or {@code null} if no match is found.
	 */
	public Npc getNpc(int npcId)
	{
		for (Iterator<VisibleObject> iter = objectIterator(); iter.hasNext();)
		{
			final VisibleObject obj = iter.next();
			if (obj instanceof Npc)
			{
				final Npc npc = (Npc) obj;
				if (npc.getNpcId() == npcId)
				{
					return npc;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves all {@link Player} objects currently located within this instance.<br>
	 * This method iterates through the internal player list to collect every active user.
	 * @return A {@code List} containing all {@code Player} objects inside the map.
	 */
	public List<Player> getPlayersInside()
	{
		final List<Player> playersInside = new ArrayList<>();
		final Iterator<Player> players = playerIterator();
		while (players.hasNext())
		{
			playersInside.add(players.next());
		}
		
		return playersInside;
	}
	
	/**
	 * Retrieves a list of {@link Npc} objects that match the specified ID.<br>
	 * This method searches through all visible objects in the current instance.<br>
	 * It returns an empty list if no matches are found.
	 * @param npcId The unique identifier used to filter the NPCs.
	 * @return A {@code List} of {@link Npc} objects matching the given ID.
	 */
	public List<Npc> getNpcs(int npcId)
	{
		final List<Npc> npcs = new ArrayList<>();
		for (Iterator<VisibleObject> iter = objectIterator(); iter.hasNext();)
		{
			final VisibleObject obj = iter.next();
			if (obj instanceof Npc)
			{
				final Npc npc = (Npc) obj;
				if (npc.getNpcId() == npcId)
				{
					npcs.add(npc);
				}
			}
		}
		
		return npcs;
	}
	
	/**
	 * Retrieves all {@link Npc} objects currently in this instance.<br>
	 * This method filters the visible objects to find every instance of an {@code Npc}.
	 * @return A {@code List} containing all {@link Npc} objects.
	 */
	public List<Npc> getNpcs()
	{
		final List<Npc> npcs = new ArrayList<>();
		for (Iterator<VisibleObject> iter = objectIterator(); iter.hasNext();)
		{
			final VisibleObject obj = iter.next();
			if (obj instanceof Npc)
			{
				npcs.add((Npc) obj);
			}
		}
		
		return npcs;
	}
	
	/**
	 * Retrieves all {@link StaticDoor} objects located in this map region.<br>
	 * The results are stored in a {@code Map} where the key is the door's static ID.
	 * @return A {@code Map} containing the doors and their corresponding IDs.
	 */
	public Map<Integer, StaticDoor> getDoors()
	{
		final Map<Integer, StaticDoor> doors = new HashMap<>();
		for (Iterator<VisibleObject> iter = objectIterator(); iter.hasNext();)
		{
			final VisibleObject obj = iter.next();
			if (obj instanceof StaticDoor)
			{
				final StaticDoor door = (StaticDoor) obj;
				doors.put(door.getSpawn().getStaticId(), door);
			}
		}
		
		return doors;
	}
	
	/**
	 * Retrieves all {@link Trap} objects owned by a specific creature.<br>
	 * This method filters the visible objects in the current instance.<br>
	 * It checks if the creator ID of each trap matches the object ID of the provided creature.
	 * @param p The {@code Creature} whose traps should be retrieved.
	 * @return A {@code List} containing all traps belonging to the specified creature.
	 */
	public List<Trap> getTraps(Creature p)
	{
		final List<Trap> traps = new ArrayList<>();
		for (Iterator<VisibleObject> iter = objectIterator(); iter.hasNext();)
		{
			final VisibleObject obj = iter.next();
			if (obj instanceof Trap)
			{
				final Trap t = (Trap) obj;
				if (t.getCreatorId() == p.getObjectId())
				{
					traps.add(t);
				}
			}
		}
		
		return traps;
	}
	
	/**
	 * Retrieves the unique identifier for this instance.<br>
	 * This ID was provided during the construction of {@link CollisionResults}.
	 * @return The {@code int} value representing the instance ID.
	 */
	public int getInstanceId()
	{
		return instanceId;
	}
	
	/**
	 * Checks if this instance is designated for beginner players.<br>
	 * It compares the current {@code getInstanceId()} against the template's twin count.<br>
	 * Returns {@code true} if the ID exceeds the allowed count, otherwise returns {@code false}.
	 * @return {@code true} if it is a beginner instance, {@code false} otherwise.
	 */
	public boolean isBeginnerInstance()
	{
		if ((parent == null) || parent.getTemplate().isInstance())
		{
			// TODO: check Karamatis and Ataxiar for exception in FastTrack ?
			// return parent.getTemplate().getBeginnerTwinCount() > 0;
			return false;
		}
		
		int twinCount = parent.getTemplate().getTwinCount();
		if (twinCount == 0)
		{
			twinCount = 1;
		}
		
		return getInstanceId() > twinCount;
	}
	
	/**
	 * Checks if a specific object exists within this instance.<br>
	 * It looks for the {@code objId} in the internal player map.
	 * @param objId The unique identifier of the object to check.
	 * @return {@code true} if the object is found, {@code false} otherwise.
	 */
	public boolean isInInstance(int objId)
	{
		return worldMapPlayers.containsKey(objId);
	}
	
	/**
	 * Provides an iterator for all visible objects in this instance.<br>
	 * Use this to loop through every {@link VisibleObject} currently loaded.
	 * @return An {@code Iterator} containing all {@code VisibleObject} instances.
	 */
	public Iterator<VisibleObject> objectIterator()
	{
		return worldMapObjects.values().iterator();
	}
	
	/**
	 * Provides an iterator for all players in this instance.<br>
	 * Use this to loop through every {@link Player} currently present.
	 * @return An {@code Iterator} containing the list of {@link Player} objects.
	 */
	public Iterator<Player> playerIterator()
	{
		return worldMapPlayers.values().iterator();
	}
	
	/**
	 * Registers a {@link PlayerGroup} to this instance.<br>
	 * This method updates the internal registered group and calls {@code register} using the team ID.
	 * @param group The {@code PlayerGroup} object to register.
	 */
	public void registerGroup(PlayerGroup group)
	{
		registeredGroup = group;
		register(group.getTeamId());
	}
	
	/**
	 * Registers a {@link PlayerAlliance} with the current instance.<br>
	 * This method sets the registered alliance and calls {@code register} using its object ID.
	 * @param group The {@code PlayerAlliance} to register.
	 */
	public void registerGroup(PlayerAlliance group)
	{
		registredAlliance = group;
		register(group.getObjectId());
	}
	
	/**
	 * Registers a {@link League} with the current instance.<br>
	 * This method sets the registered league and calls {@code register} to track it.
	 * @param group The {@code League} object to register.
	 */
	public void registerGroup(League group)
	{
		registredLeague = group;
		register(group.getObjectId());
	}
	
	/**
	 * Retrieves the alliance currently registered in this instance.<br>
	 * This method returns the {@link PlayerAlliance} object associated with the registration.
	 * @return The registered {@code PlayerAlliance} or {@code null} if none exists.
	 */
	public PlayerAlliance getRegistredAlliance()
	{
		return registredAlliance;
	}
	
	/**
	 * Retrieves the league currently registered in this instance.<br>
	 * This method returns the {@link League} object associated with the registration.
	 * @return the registered {@code League} object.
	 */
	public League getRegistredLeague()
	{
		return registredLeague;
	}
	
	/**
	 * Adds an object to the internal registry of this instance.<br>
	 * This method tracks which objects are currently active in the map.
	 * @param objectId The unique identifier of the object to register.
	 */
	public void register(int objectId)
	{
		registeredObjects.add(objectId);
	}
	
	/**
	 * Checks if a specific object is currently registered in this instance.<br>
	 * This method looks up the {@code objectId} within the internal registry.
	 * @param objectId The unique identifier of the object to check.
	 * @return {@code true} if the object is registered, otherwise {@code false}.
	 */
	public boolean isRegistered(int objectId)
	{
		return registeredObjects.contains(objectId);
	}
	
	/**
	 * Retrieves the pre-defined task for an empty instance.<br>
	 * This method returns a {@code Future} representing the background task.
	 * @return A {@code Future<?>} object containing the empty instance task.
	 */
	public Future<?> getEmptyInstanceTask()
	{
		return emptyInstanceTask;
	}
	
	/**
	 * Sets the task for handling an empty instance.<br>
	 * This method assigns a {@code Future<?>} to the internal {@code emptyInstanceTask} field.
	 * @param emptyInstanceTask The {@code Future<?>} task to be used when an instance is empty.
	 */
	public void setEmptyInstanceTask(Future<?> emptyInstanceTask)
	{
		this.emptyInstanceTask = emptyInstanceTask;
	}
	
	/**
	 * Retrieves the {@link PlayerGroup} currently registered in this instance.<br>
	 * This method returns the group associated with the current world map.
	 * @return the registered {@code PlayerGroup} object or {@code null} if none exists.
	 */
	public PlayerGroup getRegisteredGroup()
	{
		return registeredGroup;
	}
	
	/**
	 * Gets the total number of players currently in this instance.<br>
	 * This method returns the size of the {@code worldMapPlayers} collection.
	 * @return The count of active {@link Player} objects.
	 */
	public int playersCount()
	{
		return worldMapPlayers.size();
	}
	
	/**
	 * Retrieves the list of quest identifiers for this instance.<br>
	 * This method returns all unique IDs associated with quests in the current area.
	 * @return A {@code List<Integer>} containing the quest IDs.
	 */
	public List<Integer> getQuestIds()
	{
		return questIds;
	}
	
	/**
	 * Retrieves the handler for this specific world instance.<br>
	 * This provides access to logic related to {@link InstanceHandler}.
	 * @return The {@code InstanceHandler} associated with this instance.
	 */
	public InstanceHandler getInstanceHandler()
	{
		return instanceHandler;
	}
	
	/**
	 * Sets the handler for this specific world map instance.<br>
	 * This allows the system to manage logic related to the {@link InstanceHandler}.
	 * @param instanceHandler The {@code InstanceHandler} to be assigned.
	 */
	public void setInstanceHandler(InstanceHandler instanceHandler)
	{
		this.instanceHandler = instanceHandler;
	}
	
	/**
	 * Retrieves a {@link Player} based on their unique object ID.<br>
	 * This method searches through all players in the current map instance.<br>
	 * It returns {@code null} if no player matches the provided ID.
	 * @param object The unique identifier of the player to find.
	 * @return The matching {@link Player} object or {@code null}.
	 */
	public Player getPlayer(Integer object)
	{
		for (Player player : worldMapPlayers.values())
		{
			if (object == player.getObjectId())
			{
				return player;
			}
		}
		
		return null;
	}
	
	/**
	 * Iterates through all {@link Player} objects in the current location.<br>
	 * Applies the provided {@code Visitor} to each non-null player found.<br>
	 * Logs an error if any exception occurs during the process.
	 * @param visitor The {@code Visitor} to apply to every player.
	 */
	public void doOnAllPlayers(Visitor<Player> visitor)
	{
		try
		{
			for (Player player : worldMapPlayers.values())
			{
				if (player != null)
				{
					visitor.visit(player);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("Exception when running visitor on all players" + ex);
		}
	}
	
	/**
	 * Filters {@link ZoneInstance} objects based on a specific geographic area.<br>
	 * This method checks which zones intersect with the provided rectangular coordinates.<br>
	 * It returns an array of all matching zones found in the map.
	 * @param mapId The unique identifier for the world map.
	 * @param regionId The unique identifier for the region being queried.
	 * @param startX The minimum X coordinate of the search area.
	 * @param startY The minimum Y coordinate of the search area.
	 * @param minZ The minimum Z coordinate of the search area.
	 * @param maxZ The maximum Z coordinate of the search area.
	 * @return An array of {@link ZoneInstance} objects that overlap with the specified region.
	 */
	protected ZoneInstance[] filterZones(int mapId, int regionId, float startX, float startY, float minZ, float maxZ)
	{
		final List<ZoneInstance> regionZones = new ArrayList<>();
		final RegionZone regionZone = new RegionZone(startX, startY, minZ, maxZ);
		
		for (ZoneInstance zoneInstance : zones.values())
		{
			if (zoneInstance.getAreaTemplate().intersectsRectangle(regionZone))
			{
				regionZones.add(zoneInstance);
			}
			else if (zoneInstance.getZoneTemplate().getZoneType() == ZoneClassName.DUMMY)
			{
				log.error("Region " + regionId + " should intersect with whole map zone!!! (map=" + mapId + ")");
			}
		}
		
		return regionZones.toArray(new ZoneInstance[regionZones.size()]);
	}
	
	/**
	 * Checks if a specific object is located within a given zone.<br>
	 * This method returns {@code false} if the zone name does not exist.
	 * @param object The {@link VisibleObject} to check.
	 * @param zoneName The {@link ZoneName} of the area to verify.
	 * @return {@code true} if the object is inside the zone, otherwise {@code false}.
	 */
	public boolean isInsideZone(VisibleObject object, ZoneName zoneName)
	{
		final ZoneInstance zoneTemplate = zones.get(zoneName);
		if (zoneTemplate == null)
		{
			return false;
		}
		
		return isInsideZone(object.getPosition(), zoneName);
	}
	
	/**
	 * Checks if a specific position is located within a given zone.<br>
	 * This method retrieves the {@code MapRegion} for the coordinates and verifies the zone membership.
	 * @param pos The {@code WorldPosition} to check.
	 * @param zoneName The {@code ZoneName} of the area to verify.
	 * @return {@code true} if the position is inside the specified zone, otherwise {@code false}.
	 */
	public boolean isInsideZone(WorldPosition pos, ZoneName zoneName)
	{
		final MapRegion mapRegion = this.getRegion(pos.getX(), pos.getY(), pos.getZ());
		return mapRegion.isInsideZone(zoneName, pos.getX(), pos.getY(), pos.getZ());
	}
	
	/**
	 * Sets the current solo player object.<br>
	 * This method updates the {@code soloPlayer} field with the provided value.
	 * @param obj The unique identifier of the solo player object.
	 */
	public void setSoloPlayerObj(Integer obj)
	{
		soloPlayer = obj;
	}
	
	/**
	 * Retrieves the unique identifier for the solo player.<br>
	 * This value represents the {@code Integer} ID of the current solo player.
	 * @return The {@code Integer} ID of the solo player, or {@code null} if no solo player exists.
	 */
	public Integer getSoloPlayerObj()
	{
		return soloPlayer;
	}
}
