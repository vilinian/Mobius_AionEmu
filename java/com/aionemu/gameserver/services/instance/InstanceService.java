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
package com.aionemu.gameserver.services.instance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.league.League;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.HousingService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.StaticDoorSpawnManager;
import com.aionemu.gameserver.spawnengine.WalkerFormator;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMap2DInstance;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapInstanceFactory;
import com.aionemu.gameserver.world.WorldMapType;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * Manages the lifecycle and operations of game world instances.<br>
 * This service handles the creation, retrieval, and management of {@link WorldMapInstance} objects.<br>
 * It serves as a central hub for coordinating instance-related logic across the server.
 * @author ATracer
 */
public class InstanceService
{
	private static final Logger log = LoggerFactory.getLogger("INSTANCE_LOG");
	private static final List<Integer> instanceAggro = new ArrayList<>();
	private static final List<Integer> instanceCoolDownFilter = new ArrayList<>();
	private static final int SOLO_INSTANCES_DESTROY_DELAY = 10 * 60 * 1000; // 10 minutes
	
	/**
	 * Initializes the instance configuration data.<br>
	 * This method parses comma-separated values from {@link CustomConfig}.<br>
	 * It populates the aggro and cooldown filter lists for instances.
	 */
	public static void load()
	{
		for (String s : CustomConfig.INSTANCES_MOB_AGGRO.split(","))
		{
			instanceAggro.add(Integer.parseInt(s));
		}
		
		for (String s : CustomConfig.INSTANCES_COOL_DOWN_FILTER.split(","))
		{
			instanceCoolDownFilter.add(Integer.parseInt(s));
		}
	}
	
	/**
	 * Finds and creates the next available instance for a specific map.<br>
	 * This method handles the creation, registration, and initialization of the new {@code WorldMapInstance}.<br>
	 * It ensures that the instance is properly added to the world and started via the {@link SpawnEngine}.
	 * @param worldId The unique identifier of the world map.
	 * @param ownerId The identifier of the player who owns the instance.
	 * @return The newly created {@code WorldMapInstance} object.
	 */
	public synchronized static WorldMapInstance getNextAvailableInstance(int worldId, int ownerId)
	{
		final WorldMap map = World.getInstance().getWorldMap(worldId);
		
		if (!map.isInstanceType())
		{
			throw new UnsupportedOperationException("Invalid call for next available instance  of " + worldId);
		}
		
		final int nextInstanceId = map.getNextInstanceId();
		log.info("Creating new instance:" + worldId + " id:" + nextInstanceId + " owner:" + ownerId);
		final WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId, ownerId);
		
		map.addInstance(nextInstanceId, worldMapInstance);
		SpawnEngine.spawnInstance(worldId, worldMapInstance.getInstanceId(), (byte) 0, ownerId);
		InstanceEngine.getInstance().onInstanceCreate(worldMapInstance);
		
		// finally start the checker
		if (map.isInstanceType())
		{
			startInstanceChecker(worldMapInstance);
		}
		
		return worldMapInstance;
	}
	
	/**
	 * Finds the next available {@link WorldMapInstance} for a specific world.<br>
	 * This method searches for an instance that is not currently occupied by any owner.<br>
	 * It acts as a shortcut for finding instances with a default owner ID of {@code 0}.
	 * @param worldId The unique identifier of the world map.
	 * @return The next available {@link WorldMapInstance} or {@code null} if none are found.
	 */
	public synchronized static WorldMapInstance getNextAvailableInstance(int worldId)
	{
		return getNextAvailableInstance(worldId, 0);
	}
	
	/**
	 * Removes a specific world map instance from the game.<br>
	 * This method cleans up all objects and handles player notifications.<br>
	 * It also triggers destruction logic for specialized instances like 2D maps.
	 * @param instance The {@code WorldMapInstance} to be destroyed.
	 */
	public static void destroyInstance(WorldMapInstance instance)
	{
		if (instance.getEmptyInstanceTask() != null)
		{
			instance.getEmptyInstanceTask().cancel(false);
		}
		
		final int worldId = instance.getMapId();
		final WorldMap map = World.getInstance().getWorldMap(worldId);
		if (!map.isInstanceType())
		{
			return;
		}
		
		final int instanceId = instance.getInstanceId();
		
		map.removeWorldMapInstance(instanceId);
		
		log.info("Destroying instance:" + worldId + " " + instanceId);
		
		final Iterator<VisibleObject> it = instance.objectIterator();
		while (it.hasNext())
		{
			final VisibleObject obj = it.next();
			if (obj instanceof Player)
			{
				final Player player = (Player) obj;
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.LEAVE_INSTANCE_NOT_PARTY));
				moveToExitPoint((Player) obj);
			}
			else
			{
				obj.getController().onDelete();
			}
		}
		
		instance.getInstanceHandler().onInstanceDestroy();
		if (instance instanceof WorldMap2DInstance)
		{
			final WorldMap2DInstance w2d = (WorldMap2DInstance) instance;
			if (w2d.isPersonal())
			{
				HousingService.getInstance().onInstanceDestroy(w2d.getOwnerId());
			}
		}
		
		WalkerFormator.onInstanceDestroy(worldId, instanceId);
	}
	
	/**
	 * Registers a {@link Player} to a specific {@link WorldMapInstance}.<br>
	 * This method links the player's object ID to the instance.<br>
	 * It also sets the player as the solo player for that instance.
	 * @param instance The {@link WorldMapInstance} where the player will be registered.
	 * @param player The {@link Player} who needs to be registered.
	 */
	public static void registerPlayerWithInstance(WorldMapInstance instance, Player player)
	{
		final Integer obj = player.getObjectId();
		instance.register(obj);
		instance.setSoloPlayerObj(obj);
	}
	
	/**
	 * Registers a {@link PlayerGroup} with a specific {@link WorldMapInstance}.<br>
	 * This method links the group to the instance for tracking purposes.
	 * @param instance The {@code WorldMapInstance} where the group will be registered.
	 * @param group The {@code PlayerGroup} to register.
	 */
	public static void registerGroupWithInstance(WorldMapInstance instance, PlayerGroup group)
	{
		instance.registerGroup(group);
	}
	
	/**
	 * Registers a {@link PlayerAlliance} with a specific {@link WorldMapInstance}.<br>
	 * This method links the alliance to the instance for group management.
	 * @param instance The {@code WorldMapInstance} where the alliance will be registered.
	 * @param group The {@code PlayerAlliance} object to register.
	 */
	public static void registerAllianceWithInstance(WorldMapInstance instance, PlayerAlliance group)
	{
		instance.registerGroup(group);
	}
	
	/**
	 * Registers a {@link League} with a specific {@link WorldMapInstance}.<br>
	 * This method links the league group to the provided instance.
	 * @param instance The {@code WorldMapInstance} where the league will be registered.
	 * @param group The {@code League} object to register.
	 */
	public static void registerLeagueWithInstance(WorldMapInstance instance, League group)
	{
		instance.registerGroup(group);
	}
	
	/**
	 * Retrieves a {@link WorldMapInstance} that is registered to a specific object.<br>
	 * This method searches through all instances of the given {@code worldId}.<br>
	 * It returns the first instance where the {@code objectId} is successfully registered.
	 * @param worldId The unique identifier for the world map.
	 * @param objectId The unique identifier for the object to check.
	 * @return The matching {@link WorldMapInstance}, or {@code null} if no match is found.
	 */
	public static WorldMapInstance getRegisteredInstance(int worldId, int objectId)
	{
		final Iterator<WorldMapInstance> iterator = World.getInstance().getWorldMap(worldId).iterator();
		while (iterator.hasNext())
		{
			final WorldMapInstance instance = iterator.next();
			
			if (instance.isRegistered(objectId))
			{
				return instance;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves a specific personal world map instance for a player.<br>
	 * This method searches through all instances of a given {@code worldId}.<br>
	 * It returns the instance that belongs to the specified {@code ownerId}.
	 * @param worldId The unique identifier for the world map.
	 * @param ownerId The unique identifier of the player who owns the instance.
	 * @return The matching {@link WorldMapInstance} or {@code null} if not found.
	 */
	public static WorldMapInstance getPersonalInstance(int worldId, int ownerId)
	{
		if (ownerId == 0)
		{
			return null;
		}
		
		final Iterator<WorldMapInstance> iterator = World.getInstance().getWorldMap(worldId).iterator();
		while (iterator.hasNext())
		{
			final WorldMapInstance instance = iterator.next();
			if (instance.isPersonal() && (instance.getOwnerId() == ownerId))
			{
				return instance;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves a specific beginner instance for the given world and ID.<br>
	 * This method calls {@code int)} to find the map.<br>
	 * It returns the instance only if it is marked as a beginner instance.
	 * @param worldId The unique identifier of the world.
	 * @param registeredId The unique identifier of the registered object.
	 * @return The {@code WorldMapInstance} if it is a beginner map, otherwise {@code null}.
	 */
	public static WorldMapInstance getBeginnerInstance(int worldId, int registeredId)
	{
		final WorldMapInstance instance = getRegisteredInstance(worldId, registeredId);
		if (instance == null)
		{
			return null;
		}
		
		return instance.isBeginnerInstance() ? instance : null;
	}
	
	/**
	 * Retrieves the unique identifier used to find a player's instance.<br>
	 * This method checks for group, alliance, or personal ownership status.<br>
	 * It returns the specific ID required by {@code int)}.
	 * @param player The {@code Player} object to check.
	 * @return The integer ID of the last registered entity.
	 */
	private static int getLastRegisteredId(Player player)
	{
		int lookupId;
		final boolean isPersonal = WorldMapType.getWorld(player.getWorldId()).isPersonal();
		if (player.isInGroup2())
		{
			lookupId = player.getPlayerGroup2().getTeamId();
		}
		else if (player.isInAlliance2())
		{
			lookupId = player.getPlayerAlliance2().getTeamId();
			if (player.isInLeague())
			{
				lookupId = player.getPlayerAlliance2().getLeague().getObjectId();
			}
		}
		else if (isPersonal && (player.getCommonData().getWorldOwnerId() != 0))
		{
			lookupId = player.getCommonData().getWorldOwnerId();
		}
		else
		{
			lookupId = player.getObjectId();
		}
		
		return lookupId;
	}
	
	/**
	 * Handles the logic for placing a {@link Player} into the correct world instance upon login.<br>
	 * It checks if the player should be placed in a beginner instance or a personal/registered instance.<br>
	 * If no valid instance is found, it moves the player to an exit point.
	 * @param player The {@code Player} object that is currently logging in.
	 */
	public static void onPlayerLogin(Player player)
	{
		final int worldId = player.getWorldId();
		final int lookupId = getLastRegisteredId(player);
		
		final WorldMapInstance beginnerInstance = getBeginnerInstance(worldId, lookupId);
		if (beginnerInstance != null)
		{
			// set to correct twin instanceId, not to #1
			World.getInstance().setPosition(player, worldId, beginnerInstance.getInstanceId(), player.getX(), player.getY(), player.getZ(), player.getHeading());
		}
		
		final WorldMapTemplate worldTemplate = DataManager.WORLD_MAPS_DATA.getTemplate(worldId);
		if (worldTemplate.isInstance())
		{
			final boolean isPersonal = WorldMapType.getWorld(player.getWorldId()).isPersonal();
			WorldMapInstance registeredInstance = isPersonal ? getPersonalInstance(worldId, lookupId) : getRegisteredInstance(worldId, lookupId);
			
			if (isPersonal)
			{
				if (registeredInstance == null)
				{
					registeredInstance = getNextAvailableInstance(player.getWorldId(), lookupId);
				}
				
				if (!registeredInstance.isRegistered(player.getObjectId()))
				{
					registerPlayerWithInstance(registeredInstance, player);
				}
			}
			
			if (registeredInstance != null)
			{
				World.getInstance().setPosition(player, worldId, registeredInstance.getInstanceId(), player.getX(), player.getY(), player.getZ(), player.getHeading());
				player.getPosition().getWorldMapInstance().getInstanceHandler().onPlayerLogin(player);
				return;
			}
			
			moveToExitPoint(player);
		}
	}
	
	/**
	 * Moves a {@link Player} to the exit point of their current instance.<br>
	 * This method uses {@code int, String)} to handle the movement.
	 * @param player The {@code Player} object that needs to be moved.
	 */
	public static void moveToExitPoint(Player player)
	{
		TeleportService2.moveToInstanceExit(player, player.getWorldId(), player.getRace());
	}
	
	/**
	 * Checks if a specific instance exists in the given world.<br>
	 * This method returns {@code true} if the instance is found.<br>
	 * It returns {@code false} if the instance does not exist.
	 * @param worldId The unique identifier for the world.
	 * @param instanceId The unique identifier for the instance.
	 * @return A boolean value indicating whether the instance exists.
	 */
	public static boolean isInstanceExist(int worldId, int instanceId)
	{
		return World.getInstance().getWorldMap(worldId).getWorldMapInstanceById(instanceId) != null;
	}
	
	/**
	 * Starts a background task to monitor the instance for empty states.<br>
	 * It schedules an {@code EmptyInstanceCheckerTask} using the {@link ThreadPoolManager}.<br>
	 * The check runs every 60 seconds after an initial delay of 150 seconds.
	 * @param worldMapInstance The {@code WorldMapInstance} to monitor for empty status.
	 */
	private static void startInstanceChecker(WorldMapInstance worldMapInstance)
	{
		System.out.println("Instance Checker Started");
		
		final int delay = 150000; // 2.5 minutes
		final int period = 60000; // 1 minute
		worldMapInstance.setEmptyInstanceTask(ThreadPoolManager.getInstance().scheduleAtFixedRate(new EmptyInstanceCheckerTask(worldMapInstance), delay, period));
	}
	
	private static class EmptyInstanceCheckerTask implements Runnable
	{
		private final WorldMapInstance worldMapInstance;
		private long soloInstanceDestroyTime;
		
		private EmptyInstanceCheckerTask(WorldMapInstance worldMapInstance)
		{
			this.worldMapInstance = worldMapInstance;
			soloInstanceDestroyTime = System.currentTimeMillis() + SOLO_INSTANCES_DESTROY_DELAY;
		}
		
		private boolean canDestroySoloInstance()
		{
			return System.currentTimeMillis() > soloInstanceDestroyTime;
		}
		
		private void updateSoloInstanceDestroyTime()
		{
			soloInstanceDestroyTime = System.currentTimeMillis() + SOLO_INSTANCES_DESTROY_DELAY;
		}
		
		@Override
		public void run()
		{
			final int instanceId = worldMapInstance.getInstanceId();
			final int worldId = worldMapInstance.getMapId();
			final WorldMap map = World.getInstance().getWorldMap(worldId);
			final PlayerGroup registeredGroup = worldMapInstance.getRegisteredGroup();
			if (registeredGroup == null)
			{
				if (worldMapInstance.playersCount() > 0)
				{
					updateSoloInstanceDestroyTime();
					return;
				}
				
				if (worldMapInstance.playersCount() == 0)
				{
					if (canDestroySoloInstance())
					{
						map.removeWorldMapInstance(instanceId);
						destroyInstance(worldMapInstance);
						return;
					}
					return;
				}
				
				final Iterator<Player> playerIterator = worldMapInstance.playerIterator();
				final int mapId = worldMapInstance.getMapId();
				while (playerIterator.hasNext())
				{
					final Player player = playerIterator.next();
					if (player.isOnline() && (player.getWorldId() == mapId))
					{
						return;
					}
				}
				
				map.removeWorldMapInstance(instanceId);
				destroyInstance(worldMapInstance);
			}
			else if (registeredGroup.size() == 0)
			{
				map.removeWorldMapInstance(instanceId);
				destroyInstance(worldMapInstance);
			}
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} logs out of the game.<br>
	 * This method notifies the instance handler to perform necessary cleanup.
	 * @param player The {@code Player} object that is logging out.
	 */
	public static void onLogOut(Player player)
	{
		player.getPosition().getWorldMapInstance().getInstanceHandler().onPlayerLogOut(player);
	}
	
	/**
	 * Handles the logic when a {@link Player} enters a new instance.<br>
	 * This method updates the player's zone and nearby quests.<br>
	 * It also removes items from the inventory that do not belong to the current world.
	 * @param player The {@code Player} object entering the instance.
	 */
	public static void onEnterInstance(Player player)
	{
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
		player.getPosition().getWorldMapInstance().getInstanceHandler().onEnterInstance(player);
		AutoGroupService.getInstance().onEnterInstance(player);
		for (Item item : player.getInventory().getItems())
		{
			if (item.getItemTemplate().getOwnershipWorld() == 0)
			{
				continue;
			}
			
			if (item.getItemTemplate().getOwnershipWorld() != player.getWorldId())
			{
				player.getInventory().decreaseByObjectId(item.getObjectId(), item.getItemCount());
			}
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} leaves an instance.<br>
	 * It notifies the instance handler to process the departure.<br>
	 * It removes items from the player's inventory if they belong to their current world.<br>
	 * It also triggers the {@link AutoGroupService} leave event if auto-grouping is enabled.
	 * @param player The {@code Player} object who is leaving the instance.
	 */
	public static void onLeaveInstance(Player player)
	{
		player.getPosition().getWorldMapInstance().getInstanceHandler().onLeaveInstance(player);
		for (Item item : player.getInventory().getItems())
		{
			if (item.getItemTemplate().getOwnershipWorld() == player.getWorldId())
			{
				player.getInventory().decreaseByObjectId(item.getObjectId(), item.getItemCount());
			}
		}
		
		if (AutoGroupConfig.AUTO_GROUP_ENABLE)
		{
			AutoGroupService.getInstance().onLeaveInstance(player);
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} enters a specific {@code ZoneInstance}.<br>
	 * This method triggers events related to entering a new area.
	 * @param player The {@link Player} who is moving into the zone.
	 * @param zone The {@code ZoneInstance} that was entered.
	 */
	public static void onEnterZone(Player player, ZoneInstance zone)
	{
		player.getPosition().getWorldMapInstance().getInstanceHandler().onEnterZone(player, zone);
	}
	
	/**
	 * Handles the logic when a {@link Player} leaves a specific {@code ZoneInstance}.<br>
	 * This method is called to update the game state after a player exits an area.
	 * @param player The {@link Player} who is leaving the zone.
	 * @param zone The {@code ZoneInstance} that the player is exiting.
	 */
	public static void onLeaveZone(Player player, ZoneInstance zone)
	{
		player.getPosition().getWorldMapInstance().getInstanceHandler().onLeaveZone(player, zone);
	}
	
	/**
	 * Checks if a specific map is set to aggressive mode.<br>
	 * This method verifies if the {@code mapId} exists in the internal aggro list.
	 * @param mapId The unique identifier of the map to check.
	 * @return {@code true} if the map is aggressive, {@code false} otherwise.
	 */
	public static boolean isAggro(int mapId)
	{
		return instanceAggro.contains(mapId);
	}
	
	/**
	 * Calculates the cooldown rate for a player entering an instance.<br>
	 * This method checks if the {@code Player} has specific permissions and filters by {@code mapId}.<br>
	 * It returns a value from {@link CustomConfig} or a default of {@code 1}.
	 * @param player The {@code Player} object to check for permissions.
	 * @param mapId The unique identifier for the map instance.
	 * @return The calculated cooldown rate as an {@code int}.
	 */
	public static int getInstanceRate(Player player, int mapId)
	{
		int instanceCooldownRate = player.havePermission(MembershipConfig.INSTANCES_COOLDOWN) && !instanceCoolDownFilter.contains(mapId) ? CustomConfig.INSTANCES_RATE : 1;
		if (instanceCoolDownFilter.contains(mapId))
		{
			instanceCooldownRate = 1;
		}
		
		return instanceCooldownRate;
	}
	
	/**
	 * Creates a new battle ground instance for a specific map.<br>
	 * This method initializes the {@link WorldMapInstance}, registers it with the engine,<br>
	 * and spawns necessary static doors.
	 * @param worldId The unique identifier of the world map.
	 * @param idInstance The unique identifier for this specific instance.
	 * @return The newly created {@code WorldMapInstance} object.
	 */
	public static synchronized WorldMapInstance createBattleGroundInstance(int worldId, int idInstance)
	{
		final WorldMap map = World.getInstance().getWorldMap(worldId);
		log.info("Creating new BG instance: " + worldId + " " + idInstance);
		final WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, idInstance);
		startInstanceChecker(worldMapInstance);
		map.addInstance(idInstance, worldMapInstance);
		InstanceEngine.getInstance().onInstanceCreate(worldMapInstance);
		StaticDoorSpawnManager.spawnTemplate(worldId, idInstance);
		return worldMapInstance;
	}
}
