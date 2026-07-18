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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.controllers.HouseController;
import com.aionemu.gameserver.dao.HousesDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.HouseDecoration;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerHouseOwnerFlags;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.house.HouseStatus;
import com.aionemu.gameserver.model.templates.housing.Building;
import com.aionemu.gameserver.model.templates.housing.BuildingType;
import com.aionemu.gameserver.model.templates.housing.HouseAddress;
import com.aionemu.gameserver.model.templates.housing.HousingLand;
import com.aionemu.gameserver.model.templates.spawns.SpawnType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_ACQUIRE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_OWNER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MARK_FRIENDLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Manages the core logic for player housing systems.<br>
 * This service handles house ownership, decorations, and interactions between {@link Player} objects and {@link House} entities.
 * @author Rolandas
 */
public class HousingService
{
	private static final Logger log = LoggerFactory.getLogger(HousingService.class);
	
	// Contains non-instance houses initially (which are spawned)
	private static final Map<Integer, List<House>> housesByMapId = new HashMap<>();
	
	// Contains all houses by their addresses
	private final Map<Integer, House> customHouses;
	private final Map<Integer, House> studios;
	
	private static class SingletonHolder
	{
		protected static final HousingService instance = new HousingService();
	}
	
	/**
	 * Provides the global instance of the {@link HousingService}.<br>
	 * This method follows the singleton pattern to ensure only one service exists.
	 * @return The active {@code HousingService} instance.
	 */
	public static HousingService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor for the {@link HousingService} class.<br>
	 * This constructor initializes the housing data from the database.<br>
	 * It populates the {@code customHouses} and {@code studios} maps during startup.
	 */
	private HousingService()
	{
		log.debug("[HousingService] Loading housing data...");
		customHouses = new ConcurrentHashMap<>(DAOManager.getDAO(HousesDAO.class).loadHouses(DataManager.HOUSE_DATA.getLands(), false));
		studios = new ConcurrentHashMap<>(DAOManager.getDAO(HousesDAO.class).loadHouses(DataManager.HOUSE_DATA.getLands(), true));
		log.debug("[HousingService] Housing Service loaded.");
	}
	
	/**
	 * Spawns all available houses and studios into the game world.<br>
	 * This method handles both custom houses and player studios based on the provided IDs.<br>
	 * It ensures that objects are correctly positioned and registered in the current instance.
	 * @param worldId The unique identifier for the world map.
	 * @param instanceId The specific instance ID where houses should be spawned.
	 * @param registeredId The ID of the player who owns a studio, used if no lands are found.
	 */
	public void spawnHouses(int worldId, int instanceId, int registeredId)
	{
		final Set<HousingLand> lands = DataManager.HOUSE_DATA.getLandsForWorldId(worldId);
		if (lands == null)
		{
			if (registeredId > 0)
			{
				House studio;
				synchronized (studios)
				{
					studio = studios.get(registeredId);
				}
				
				if (studio == null)
				{
					return;
				}
				
				final HouseAddress addr = studio.getAddress();
				if (addr.getMapId() != worldId)
				{
					return;
				}
				
				final VisibleObject existing = World.getInstance().findVisibleObject(studio.getObjectId());
				WorldPosition position = null;
				if (existing != null)
				{
					position = existing.getPosition();
				}
				
				if (position == null)
				{
					position = World.getInstance().createPosition(addr.getMapId(), addr.getX(), addr.getY(), addr.getZ(), (byte) 0, instanceId);
					studio.setPosition(position);
				}
				
				if (!position.isSpawned())
				{
					SpawnEngine.bringIntoWorld(studio);
				}
				
				// spawn only npcs
				studio.spawn(instanceId);
				
				final Player enteredPlayer = World.getInstance().findPlayer(registeredId);
				if (enteredPlayer != null)
				{
					enteredPlayer.setHouseRegistry(studio.getRegistry());
				}
			}
			return;
		}
		
		int spawnedCounter = 0;
		for (HousingLand land : lands)
		{
			final Building defaultBuilding = land.getDefaultBuilding();
			if (defaultBuilding.getType() == BuildingType.PERSONAL_INS)
			{
				continue; // ignore studios
			}
			
			for (HouseAddress address : land.getAddresses())
			{
				if (address.getMapId() != worldId)
				{
					continue;
				}
				
				House customHouse = customHouses.get(address.getId());
				if (customHouse == null)
				{
					customHouse = new House(defaultBuilding, address, instanceId);
					
					// house without owner when acquired will be inserted to DB
					customHouse.setPersistentState(PersistentState.NEW);
					customHouses.put(address.getId(), customHouse);
				}
				
				customHouse.spawn(instanceId);
				spawnedCounter++;
				
				List<House> housesForMap = housesByMapId.get(worldId);
				if (housesForMap == null)
				{
					housesForMap = new ArrayList<>();
					housesByMapId.put(worldId, housesForMap);
				}
				
				housesForMap.add(customHouse);
			}
		}
		
		final String worldName = DataManager.WORLD_MAPS_DATA.getTemplate(worldId).getName() + " (id:" + worldId + ")";
		if ((spawnedCounter > 0) && (instanceId == 1))
		{
			GameServer.log.info("[HousingService] Spawned " + spawnedCounter + " houses in " + worldName);
		}
	}
	
	/**
	 * Retrieves all houses owned by a specific player.<br>
	 * This method checks for the player's studio first.<br>
	 * It then searches through all custom houses to find matches.
	 * @param playerObjId The unique identifier of the player.
	 * @return A {@code List} of {@link House} objects belonging to the player.
	 */
	public List<House> searchPlayerHouses(int playerObjId)
	{
		final List<House> houses = new ArrayList<>();
		synchronized (studios)
		{
			if (studios.containsKey(playerObjId))
			{
				houses.add(studios.get(playerObjId));
				return houses;
			}
		}
		
		for (House house : customHouses.values())
		{
			if (house.getOwnerId() == playerObjId)
			{
				houses.add(house);
			}
		}
		
		return houses;
	}
	
	/**
	 * Retrieves the unique address ID for a player's studio or active house.<br>
	 * This method checks the {@code studios} map first.<br>
	 * If no studio is found, it searches through all custom houses.<br>
	 * It returns the ID of the first active or sell-waiting house owned by the player.
	 * @param playerId The unique identifier of the player to search for.
	 * @return The address ID as an {@code int}, or 0 if no valid house is found.
	 */
	public int getPlayerAddress(int playerId)
	{
		synchronized (studios)
		{
			if (studios.containsKey(playerId))
			{
				return studios.get(playerId).getAddress().getId();
			}
		}
		
		for (House house : customHouses.values())
		{
			if (house.getStatus() == HouseStatus.INACTIVE)
			{
				continue;
			}
			
			if ((house.getOwnerId() == playerId) && ((house.getStatus() == HouseStatus.ACTIVE) || (house.getStatus() == HouseStatus.SELL_WAIT)))
			{
				return house.getAddress().getId();
			}
		}
		
		return 0;
	}
	
	/**
	 * Resets the visual appearance of a specific house.<br>
	 * This method removes all custom decorations from the {@link House}.<br>
	 * It sets the persistent state of each decoration to {@code DELETED}.<br>
	 * Finally, it clears the custom parts from the house registry.
	 * @param house The {@link House} object to be reset.
	 */
	public void resetAppearance(House house)
	{
		final List<HouseDecoration> customParts = house.getRegistry().getCustomParts();
		for (HouseDecoration deco : customParts)
		{
			deco.setPersistentState(PersistentState.DELETED);
		}
		
		for (HouseDecoration deco : customParts)
		{
			house.getRegistry().removeCustomPart(deco.getObjectId());
		}
	}
	
	/**
	 * Finds a {@link House} based on its unique name.<br>
	 * This method searches through all custom houses.<br>
	 * It returns the first matching house or {@code null} if no match is found.
	 * @param houseName The name of the house to search for.
	 * @return The {@link House} object if found, otherwise {@code null}.
	 */
	public House getHouseByName(String houseName)
	{
		for (House house : customHouses.values())
		{
			if (house.getName().equals(houseName))
			{
				return house;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves a {@link House} object based on its unique address ID.<br>
	 * This method searches through all custom houses to find a match.<br>
	 * It returns {@code null} if no house is found with the given address.
	 * @param address The unique identifier for the house address.
	 * @return The matching {@link House} object or {@code null}.
	 */
	public House getHouseByAddress(int address)
	{
		for (House house : customHouses.values())
		{
			if (house.getAddress().getId() == address)
			{
				return house;
			}
		}
		
		return null;
	}
	
	/**
	 * Activates a previously purchased house for the specified player.<br>
	 * This method finds an {@code INACTIVE} house owned by the player and updates its status.<br>
	 * It sets the fee as paid and saves the changes to the database.
	 * @param playerId The unique identifier of the player who owns the house.
	 * @return The activated {@link House} object, or {@code null} if no inactive house is found.
	 */
	public House activateBoughtHouse(int playerId)
	{
		for (House house : customHouses.values())
		{
			if ((house.getOwnerId() == playerId) && (house.getStatus() == HouseStatus.INACTIVE))
			{
				house.revokeOwner();
				house.setOwnerId(playerId);
				house.setFeePaid(true);
				house.setNextPay(null);
				house.setSellStarted(null);
				house.reloadHouseRegistry();
				house.save();
				return house;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the studio associated with a specific player.<br>
	 * This method checks if the {@code playerId} exists in the internal studio map.<br>
	 * It returns {@code null} if no studio is found for that player.
	 * @param playerId The unique identifier of the player.
	 * @return The {@link House} object representing the player's studio, or {@code null}.
	 */
	public House getPlayerStudio(int playerId)
	{
		synchronized (studios)
		{
			if (studios.containsKey(playerId))
			{
				return studios.get(playerId);
			}
		}
		
		return null;
	}
	
	/**
	 * Removes the studio associated with a specific player.<br>
	 * This method updates the internal {@code studios} collection.<br>
	 * It does nothing if the {@code playerId} is {@code 0}.
	 * @param playerId The unique identifier of the player to remove.
	 */
	public void removeStudio(int playerId)
	{
		if (playerId != 0)
		{
			synchronized (studios)
			{
				studios.remove(playerId);
			}
		}
	}
	
	/**
	 * Registers a studio for the specified {@link Player}.<br>
	 * This method calls the internal {@code createStudio} logic.
	 * @param player The {@code Player} object to register.
	 */
	public void registerPlayerStudio(Player player)
	{
		createStudio(player);
	}
	
	/**
	 * Recreates the player studio for a specific {@link Player}.<br>
	 * This method checks if the player has enough Kinah to pay the required fee.<br>
	 * If successful, it calls {@code createStudio} and deducts the gold price from the inventory.
	 * @param player The {@code Player} object for whom the studio will be recreated.
	 */
	public void recreatePlayerStudio(Player player)
	{
		// Price for both races is the same, use any template
		final HousingLand land = DataManager.HOUSE_DATA.getLand(329001);
		final long fee = land.getSaleOptions().getGoldPrice();
		if (player.getInventory().getKinah() < fee)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NOT_ENOUGH_MONEY);
			return;
		}
		
		createStudio(player);
		
		player.getInventory().decreaseKinah(fee);
	}
	
	/**
	 * Initializes a new studio for the specified {@link Player}.<br>
	 * This method sets up the default house data and ownership.<br>
	 * It also sends the necessary network packets to notify the player.
	 * @param player The {@code Player} object for whom the studio is being created.
	 */
	private void createStudio(Player player)
	{
		if (getPlayerAddress(player.getObjectId()) != 0) // should not happen
		{
			return;
		}
		
		final HousingLand land = DataManager.HOUSE_DATA.getLand(player.getRace() == Race.ELYOS ? 329001 : 339001);
		final House studio = new House(land.getDefaultBuilding(), land.getAddresses().get(0), 0);
		studio.setOwnerId(player.getObjectId());
		
		synchronized (studios)
		{
			studios.put(player.getObjectId(), studio);
		}
		
		studio.setStatus(HouseStatus.ACTIVE);
		studio.setAcquiredTime(new Timestamp(System.currentTimeMillis()));
		studio.setFeePaid(true);
		studio.setNextPay(null);
		studio.setPersistentState(PersistentState.NEW);
		player.setBuildingOwnerState(PlayerHouseOwnerFlags.HOUSE_OWNER.getId());
		PacketSendUtility.sendPacket(player, new SM_HOUSE_ACQUIRE(player.getObjectId(), studio.getAddress().getId(), true));
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_INS_OWN_SUCCESS);
		PacketSendUtility.sendPacket(player, new SM_HOUSE_OWNER_INFO(player, studio));
	}
	
	/**
	 * Changes the building type of an existing house.<br>
	 * This method updates the {@code House} with a new {@code Building} template.<br>
	 * It saves the registry and refreshes the house appearance for all players.
	 * @param currentHouse The {@link House} object to be modified.
	 * @param newBuildingId The unique identifier of the new building type.
	 */
	public void switchHouseBuilding(House currentHouse, int newBuildingId)
	{
		final Building otherBuilding = DataManager.HOUSE_BUILDING_DATA.getBuilding(newBuildingId);
		currentHouse.setBuilding(otherBuilding);
		// currentHouse.getRegistry().despawnObjects(false);
		currentHouse.getRegistry().save();
		currentHouse.reloadHouseRegistry(); // load new defaults
		DAOManager.getDAO(HousesDAO.class).storeHouse(currentHouse);
		final HouseController controller = currentHouse.getController();
		controller.broadcastAppearance();
		controller.spawnObjects();
	}
	
	/**
	 * Retrieves a list of all custom houses from the server.<br>
	 * This method collects every {@link House} stored in the internal maps.
	 * @return A {@code List} containing all house objects.
	 */
	public List<House> getCustomHouses()
	{
		final List<House> houses = new ArrayList<>();
		for (List<House> mapHouses : housesByMapId.values())
		{
			houses.addAll(mapHouses);
		}
		
		return houses;
	}
	
	/**
	 * Handles the cleanup of a house studio when an instance is destroyed.<br>
	 * This method resets specific spawn types for the studio owned by the given ID.<br>
	 * It ensures that manager, teleport, and sign spawns are cleared from the database.
	 * @param ownerId The unique identifier of the player who owns the studio.
	 */
	public void onInstanceDestroy(int ownerId)
	{
		House studio;
		synchronized (studios)
		{
			studio = studios.get(ownerId);
		}
		
		if (studio != null)
		{
			studio.setSpawn(SpawnType.MANAGER, null);
			studio.setSpawn(SpawnType.TELEPORT, null);
			studio.setSpawn(SpawnType.SIGN, null);
			studio.save();
		}
	}
	
	/**
	 * This method is called when a {@link Player} logs into the instance.<br>
	 * It determines the player's house ownership status and updates their building state.<br>
	 * It also sends necessary packets regarding house info and friend lists to the client.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void onPlayerLogin(Player player)
	{
		House activeHouse = null;
		byte buildingState = PlayerHouseOwnerFlags.BUY_STUDIO_ALLOWED.getId();
		for (House house : player.getHouses())
		{
			if ((house.getStatus() == HouseStatus.ACTIVE) || (house.getStatus() == HouseStatus.SELL_WAIT))
			{
				activeHouse = house;
			}
		}
		
		if (activeHouse == null)
		{
			QuestState qs;
			qs = player.getQuestStateList().getQuestState(player.getRace() == Race.ELYOS ? 18802 : 28802);
			if ((qs != null) && qs.getStatus().equals(QuestStatus.COMPLETE))
			{
				buildingState |= PlayerHouseOwnerFlags.BIDDING_ALLOWED.getId();
			}
		}
		else
		{
			if (activeHouse.getStatus() == HouseStatus.SELL_WAIT)
			{
				buildingState = PlayerHouseOwnerFlags.SELLING_HOUSE.getId();
			}
			else
			{
				buildingState = PlayerHouseOwnerFlags.HOUSE_OWNER.getId();
			}
		}
		
		player.setBuildingOwnerState(buildingState);
		
		PacketSendUtility.sendPacket(player, new SM_HOUSE_OWNER_INFO(player, activeHouse));
		if (!player.getFriendList().getIsFriendListSent())
		{
			PacketSendUtility.sendPacket(player, new SM_FRIEND_LIST());
		}
		
		PacketSendUtility.sendPacket(player, new SM_MARK_FRIENDLIST());
	}
}
