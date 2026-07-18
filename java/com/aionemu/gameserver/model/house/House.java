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
package com.aionemu.gameserver.model.house;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.HousingConfig;
import com.aionemu.gameserver.controllers.HouseController;
import com.aionemu.gameserver.dao.HouseScriptsDAO;
import com.aionemu.gameserver.dao.HousesDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerRegisteredItemsDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.HouseDecoration;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.SummonedHouseNpc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.PlayerHouseOwnerFlags;
import com.aionemu.gameserver.model.gameobjects.player.PlayerScripts;
import com.aionemu.gameserver.model.templates.housing.Building;
import com.aionemu.gameserver.model.templates.housing.BuildingType;
import com.aionemu.gameserver.model.templates.housing.HouseAddress;
import com.aionemu.gameserver.model.templates.housing.HouseType;
import com.aionemu.gameserver.model.templates.housing.HousingLand;
import com.aionemu.gameserver.model.templates.housing.PartType;
import com.aionemu.gameserver.model.templates.housing.Sale;
import com.aionemu.gameserver.model.templates.spawns.HouseSpawn;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnType;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.services.HousingBidService;
import com.aionemu.gameserver.services.HousingService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.knownlist.PlayerAwareKnownList;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneService;

/**
 * Represents a player's house within the game world.<br>
 * This class manages the properties, decorations, and state of a specific housing instance.<br>
 * It extends {@link VisibleObject} to handle its presence in the game environment.
 * @author Rolandas
 */
public class House extends VisibleObject
{
	private static final Logger log = LoggerFactory.getLogger(House.class);
	private HousingLand land;
	private final HouseAddress address;
	private Building building;
	private final String name;
	private int playerObjectId;
	private Timestamp acquiredTime;
	private int permissions;
	private HouseStatus status;
	private boolean feePaid = true;
	private Timestamp nextPay;
	private Timestamp sellStarted;
	private final Map<SpawnType, Npc> spawns = new HashMap<>(3);
	private HouseRegistry houseRegistry;
	private byte houseOwnerInfoFlags = PlayerHouseOwnerFlags.SINGLE_HOUSE.getId();
	private PlayerScripts playerScripts;
	private PersistentState persistentState;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
	private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	private ByteArrayOutputStream signNoticeStream;
	public static final int NOTICE_LENGTH = 130;
	
	/**
	 * Creates a new {@link House} instance with a unique ID.<br>
	 * This constructor automatically generates the next available object ID.
	 * @param building The {@link Building} template for this house.
	 * @param address The {@link HouseAddress} location of the house.
	 * @param instanceId The specific instance identifier for the house.
	 */
	public House(Building building, HouseAddress address, int instanceId)
	{
		this(IDFactory.getInstance().nextId(), building, address, instanceId);
	}
	
	/**
	 * Creates a new {@link House} instance with a specific object ID.<br>
	 * This constructor initializes the house with its building, address, and unique instance identifier.<br>
	 * It also sets up the internal controller and persistent state.
	 * @param objectId The unique identification number for this object in the game world.
	 * @param building The {@link Building} template used to define the house structure.
	 * @param address The {@link HouseAddress} representing the physical location of the house.
	 * @param instanceId The specific instance ID assigned to this house.
	 */
	public House(int objectId, Building building, HouseAddress address, int instanceId)
	{
		super(objectId, new HouseController(), null, null, null);
		getController().setOwner(this);
		this.address = address;
		this.building = building;
		name = "HOUSE_" + address.getId();
		setKnownlist(new PlayerAwareKnownList(this));
		setPersistentState(PersistentState.UPDATED);
		getRegistry();
	}
	
	/**
	 * Retrieves the controller associated with this house.<br>
	 * This method casts the base controller to a {@link HouseController}.
	 * @return the {@code HouseController} instance for this object.
	 */
	@Override
	public HouseController getController()
	{
		return (HouseController) super.getController();
	}
	
	/**
	 * Populates the default parts for the house based on the building configuration.<br>
	 * This method iterates through all {@code PartType} values to identify valid IDs.<br>
	 * It then registers each decoration into the registry at its correct floor level.
	 */
	private void putDefaultParts()
	{
		for (PartType partType : PartType.values())
		{
			final Integer partId = building.getDefaultPartId(partType);
			if (partId == null)
			{
				continue;
			}
			
			for (int line = partType.getStartLineNr(); line <= partType.getEndLineNr(); line++)
			{
				final int floor = partType.getEndLineNr() - line;
				final HouseDecoration decor = new HouseDecoration(0, partId, floor);
				getRegistry().putDefaultPart(decor, floor);
			}
		}
	}
	
	/**
	 * Retrieves the {@link HousingLand} associated with this house.<br>
	 * It searches for the land using the current house address ID.<br>
	 * If the land is not already loaded, it fetches it from {@link DataManager}.
	 * @return the {@code HousingLand} object or {@code null} if not found.
	 */
	public HousingLand getLand()
	{
		if (land == null)
		{
			for (HousingLand housingland : DataManager.HOUSE_DATA.getLands())
			{
				for (HouseAddress houseAddress : housingland.getAddresses())
				{
					if (getAddress().getId() == houseAddress.getId())
					{
						land = housingland;
						break;
					}
				}
			}
		}
		
		return land;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the unique location of this house.<br>
	 * It returns a {@link HouseAddress} object.
	 * @return the {@code HouseAddress} associated with this house.
	 */
	public HouseAddress getAddress()
	{
		return address;
	}
	
	/**
	 * Retrieves the {@link Building} associated with this house.<br>
	 * This method returns the template data for the structure.
	 * @return The {@code Building} object.
	 */
	public Building getBuilding()
	{
		return building;
	}
	
	/**
	 * Sets the {@code Building} associated with this house.<br>
	 * This updates the internal building reference to the provided {@link Building} object.
	 * @param building The {@code Building} template to assign.
	 */
	public void setBuilding(Building building)
	{
		this.building = building;
	}
	
	/**
	 * Spawns the house and its associated NPCs into the game world.<br>
	 * This method initializes player scripts and sets up building states.<br>
	 * It creates the house position if it is not already spawned.<br>
	 * Finally, it iterates through templates to spawn manager, teleport, or sign NPCs.
	 * @param instanceId The unique identifier for the world instance.
	 */
	public synchronized void spawn(int instanceId)
	{
		playerScripts = DAOManager.getDAO(HouseScriptsDAO.class).getPlayerScripts(getObjectId());
		
		if (((playerObjectId > 0) && (status == HouseStatus.ACTIVE)) || (status == HouseStatus.SELL_WAIT))
		{
			DAOManager.getDAO(PlayerRegisteredItemsDAO.class).loadRegistry(playerObjectId);
		}
		
		fixBuildingStates();
		
		// Studios are brought into world already, skip them
		if ((getPosition() == null) || !getPosition().isSpawned())
		{
			final WorldPosition position = World.getInstance().createPosition(address.getMapId(), address.getX(), address.getY(), address.getZ(), (byte) 0, instanceId);
			setPosition(position);
			SpawnEngine.bringIntoWorld(this);
		}
		
		final List<HouseSpawn> templates = DataManager.HOUSE_NPCS_DATA.getSpawnsByAddress(getAddress().getId());
		if (templates == null)
		{
			final Collection<ZoneInstance> zones = ZoneService.getInstance().getZoneInstancesByWorldId(getAddress().getMapId()).values();
			String msg = null;
			for (ZoneInstance zone : zones)
			{
				if ((zone.getZoneTemplate().getZoneType() != ZoneClassName.SUB) || (zone.getZoneTemplate().getPriority() > 20))
				{
					continue;
				}
				
				if (zone.isInsideCordinate(getAddress().getX(), getAddress().getY(), getAddress().getZ()))
				{
					msg = "zone=" + zone.getZoneTemplate().getXmlName();
					break;
				}
			}
			
			if (msg == null)
			{
				msg = "address=" + getAddress().getId() + "; map=" + getAddress().getMapId();
			}
			
			msg += "; x=" + getAddress().getX() + ", y=" + getAddress().getY() + ", z=" + getAddress().getZ();
			log.warn("Missing npcs for house: " + msg);
			return;
		}
		
		final int creatorId = getAddress().getId();
		String masterName = "";
		if (playerObjectId != 0)
		{
			final ArrayList<Integer> players = new ArrayList<>(1);
			players.add(playerObjectId);
			final Map<Integer, String> playerNames = DAOManager.getDAO(PlayerDAO.class).getPlayerNames(players);
			if (playerNames.containsKey(playerObjectId))
			{
				masterName = playerNames.get(playerObjectId);
			}
			else
			{
				revokeOwner(); // Something bad happened :P
			}
		}
		
		for (HouseSpawn spawn : templates)
		{
			SpawnTemplate t = null;
			if ((spawn.getType() == SpawnType.MANAGER) && (spawns.get(SpawnType.MANAGER) == null))
			{
				t = SpawnEngine.addNewSingleTimeSpawn(getAddress().getMapId(), getLand().getManagerNpcId(), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getH());
				final SummonedHouseNpc npc = VisibleObjectSpawner.spawnHouseNpc(t, getPosition().getInstanceId(), this, masterName);
				spawns.put(SpawnType.MANAGER, npc);
			}
			else if ((spawn.getType() == SpawnType.TELEPORT) && (spawns.get(SpawnType.TELEPORT) == null))
			{
				t = SpawnEngine.addNewSingleTimeSpawn(getAddress().getMapId(), getLand().getTeleportNpcId(), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getH());
				final SummonedHouseNpc npc = VisibleObjectSpawner.spawnHouseNpc(t, getPosition().getInstanceId(), this, masterName);
				spawns.put(SpawnType.TELEPORT, npc);
			}
			else if ((spawn.getType() == SpawnType.SIGN) && (spawns.get(SpawnType.SIGN) == null))
			{
				// Signs do not have master name displayed, but have creatorId
				t = SpawnEngine.addNewSingleTimeSpawn(getAddress().getMapId(), getCurrentSignNpcId(), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getH(), creatorId, "");
				spawns.put(SpawnType.SIGN, (Npc) SpawnEngine.spawnObject(t, getPosition().getInstanceId()));
			}
		}
	}
	
	/**
	 * Gets the distance at which this house can be seen by others.<br>
	 * This value is retrieved from {@link HousingConfig}.
	 * @return The visibility distance as a {@code float}.
	 */
	@Override
	public float getVisibilityDistance()
	{
		return HousingConfig.VISIBILITY_DISTANCE;
	}
	
	/**
	 * Gets the maximum vertical distance for visibility.<br>
	 * This value is retrieved from the {@link HousingConfig} class.
	 * @return The maximum Z distance for visibility as a {@code float}.
	 */
	@Override
	public float getMaxZVisibleDistance()
	{
		return HousingConfig.VISIBILITY_DISTANCE;
	}
	
	/**
	 * Retrieves the unique identifier of the owner.<br>
	 * This ID belongs to the player who owns this {@link House}.
	 * @return The {@code int} value representing the owner's ID.
	 */
	public int getOwnerId()
	{
		return playerObjectId;
	}
	
	/**
	 * Sets the unique identifier for the owner of this house.<br>
	 * This method updates the {@code playerObjectId} and resets the notice stream if necessary.<br>
	 * It also triggers a refresh of the building states.
	 * @param playerObjectId The new ID of the player who owns the house. Use 0 to clear the owner.
	 */
	public void setOwnerId(int playerObjectId)
	{
		if (this.playerObjectId != playerObjectId)
		{
			writeLock.lock();
			try
			{
				if (playerObjectId == 0)
				{
					signNoticeStream = null;
				}
				else
				{
					if (signNoticeStream == null)
					{
						signNoticeStream = new ByteArrayOutputStream(NOTICE_LENGTH);
					}
					
					signNoticeStream.reset();
					signNoticeStream.write(new byte[]
					{
						0,
						0
					}, 0, 2);
				}
				
				this.playerObjectId = playerObjectId;
			}
			finally
			{
				writeLock.unlock();
			}
		}
		
		fixBuildingStates();
	}
	
	/**
	 * Retrieves the time when this house was first acquired.<br>
	 * This returns a {@code Timestamp} object representing that moment.
	 * @return The {@code Timestamp} of the acquisition.
	 */
	public Timestamp getAcquiredTime()
	{
		return acquiredTime;
	}
	
	/**
	 * Sets the time when this house was first acquired.<br>
	 * This updates the {@code acquiredTime} field of the object.
	 * @param acquiredTime The {@code Timestamp} representing the acquisition date.
	 */
	public void setAcquiredTime(Timestamp acquiredTime)
	{
		this.acquiredTime = acquiredTime;
	}
	
	/**
	 * Retrieves the current permission level for this house.<br>
	 * This method also updates the door and notice states based on the owner status.
	 * @return The integer value representing the house permissions.
	 */
	public int getPermissions()
	{
		if (playerObjectId == 0)
		{
			setDoorState(status == HouseStatus.SELL_WAIT ? HousePermissions.DOOR_OPENED_ALL : HousePermissions.DOOR_CLOSED);
			setNoticeState(HousePermissions.NOT_SET);
		}
		else
		{
			if (permissions == 0)
			{
				setNoticeState(HousePermissions.SHOW_OWNER);
				if (getBuilding().getType() == BuildingType.PERSONAL_FIELD)
				{
					setDoorState(HousePermissions.DOOR_CLOSED);
				}
			}
		}
		
		return permissions;
	}
	
	/**
	 * Updates the permission level for this house.<br>
	 * This method sets the internal {@code permissions} value.
	 * @param permissions The new integer value representing the allowed actions.
	 */
	public void setPermissions(int permissions)
	{
		this.permissions = permissions;
	}
	
	/**
	 * Retrieves the current state of the house door.<br>
	 * This method uses the internal permissions to determine if the door is open or closed.
	 * @return the {@code HousePermissions} representing the door status.
	 */
	public HousePermissions getDoorState()
	{
		return HousePermissions.getDoorState(getPermissions());
	}
	
	/**
	 * Updates the current door state of the house.<br>
	 * This method modifies the internal permissions based on the provided {@code HousePermissions}.
	 * @param doorState The new door state to apply.
	 */
	public void setDoorState(HousePermissions doorState)
	{
		permissions = HousePermissions.setDoorState(permissions, doorState);
	}
	
	/**
	 * Retrieves the current notice state of the house.<br>
	 * This method uses the internal permissions to determine the status.
	 * @return the {@code HousePermissions} representing the notice state.
	 */
	public HousePermissions getNoticeState()
	{
		return HousePermissions.getNoticeState(getPermissions());
	}
	
	/**
	 * Updates the current notice state of the house.<br>
	 * This method modifies the internal permissions using {@code setNoticeState}.
	 * @param noticeState The new {@code HousePermissions} to apply for the notice state.
	 */
	public void setNoticeState(HousePermissions noticeState)
	{
		permissions = HousePermissions.setNoticeState(permissions, noticeState);
	}
	
	/**
	 * Retrieves the current status of the house.<br>
	 * This method returns the {@code HouseStatus} object associated with this instance.
	 * @return The current {@code HouseStatus}.
	 */
	public HouseStatus getStatus()
	{
		return status;
	}
	
	/**
	 * Updates the current status of this house.<br>
	 * This method synchronizes access to ensure thread safety during updates.<br>
	 * It also triggers internal logic to fix building states and refresh signs if necessary.
	 * @param status The new {@code HouseStatus} to apply to the house.
	 */
	public synchronized void setStatus(HouseStatus status)
	{
		if (this.status != status)
		{
			// fix invalid status from DB, or automatically remove sign from not auctioned houses
			if ((playerObjectId == 0) && (status == HouseStatus.ACTIVE))
			{
				status = HouseStatus.NOSALE;
			}
			
			this.status = status;
			fixBuildingStates();
			
			if (((status != HouseStatus.INACTIVE) || (getSellStarted() != null)) && (spawns.get(SpawnType.SIGN) != null))
			{
				Npc sign = spawns.get(SpawnType.SIGN);
				final int oldNpcId = sign.getNpcId();
				final int newNpcId = getCurrentSignNpcId();
				
				if (newNpcId != oldNpcId)
				{
					SpawnTemplate t = sign.getSpawn();
					sign.setSpawn(null);
					sign.getController().onDelete();
					t = SpawnEngine.addNewSingleTimeSpawn(t.getWorldId(), newNpcId, t.getX(), t.getY(), t.getZ(), t.getHeading());
					sign = (Npc) SpawnEngine.spawnObject(t, getPosition().getInstanceId());
					spawns.put(SpawnType.SIGN, sign);
				}
			}
		}
	}
	
	/**
	 * Checks if the required fee for this house has been paid.<br>
	 * Returns {@code true} if the payment is complete.<br>
	 * Returns {@code false} otherwise.
	 * @return The payment status of the house.
	 */
	public boolean isFeePaid()
	{
		return feePaid;
	}
	
	/**
	 * Updates the payment status of the house.<br>
	 * This method sets whether the required fee has been paid.
	 * @param feePaid The new payment status to set as {@code true} or {@code false}.
	 */
	public void setFeePaid(boolean feePaid)
	{
		this.feePaid = feePaid;
	}
	
	/**
	 * Retrieves the timestamp for the next scheduled payment.<br>
	 * This value is used to determine when the house fees are due.
	 * @return The {@code Timestamp} of the next payment date.
	 */
	public Timestamp getNextPay()
	{
		return nextPay;
	}
	
	/**
	 * Updates the scheduled time for the next payment.<br>
	 * This method sets the {@code nextPay} field to a new value.
	 * @param nextPay The {@code Timestamp} representing when the next payment occurs.
	 */
	public void setNextPay(Timestamp nextPay)
	{
		this.nextPay = nextPay;
	}
	
	/**
	 * Retrieves the timestamp when the sale of this house began.<br>
	 * This value is used to track the duration of an active listing.
	 * @return a {@code Timestamp} representing the start of the sale, or {@code null} if no sale has started.
	 */
	public Timestamp getSellStarted()
	{
		return sellStarted;
	}
	
	/**
	 * Sets the timestamp for when the sale of this house began.<br>
	 * This value is used to track the duration of a listing.
	 * @param sellStarted The {@code Timestamp} representing the start of the sale.
	 */
	public void setSellStarted(Timestamp sellStarted)
	{
		this.sellStarted = sellStarted;
	}
	
	/**
	 * Checks if the house is currently in a grace period.<br>
	 * This occurs when a player has two houses and a sale has started.<br>
	 * It verifies that the status is either {@code ACTIVE} or {@code SELL_WAIT}.<br>
	 * The check also ensures the sell start time is before the auction start time.
	 * @return {@code true} if the house is in the grace period, otherwise {@code false}.
	 */
	public boolean isInGracePeriod()
	{
		return (playerObjectId > 0) && (HousingService.getInstance().searchPlayerHouses(playerObjectId).size() == 2) && ((status == HouseStatus.ACTIVE) || (status == HouseStatus.SELL_WAIT)) && (sellStarted != null) && (sellStarted.getTime() <= HousingBidService.getInstance().getAuctionStartTime());
	}
	
	/**
	 * Retrieves the butler NPC for this house.<br>
	 * This method returns the {@code Npc} associated with the manager spawn type.
	 * @return the {@code Npc} object representing the butler, or {@code null} if not found.
	 */
	public synchronized Npc getButler()
	{
		return spawns.get(SpawnType.MANAGER);
	}
	
	/**
	 * Retrieves the race of the player based on the house butler.<br>
	 * This method returns {@code Race.NONE} if no butler exists.<br>
	 * It returns {@code ELYOS} for general tribes and {@code ASMODIANS} otherwise.
	 * @return The {@link Race} associated with the player's house butler.
	 */
	public Race getPlayerRace()
	{
		if (getButler() == null)
		{
			return Race.NONE;
		}
		
		if (getButler().getTribe() == TribeClass.GENERAL)
		{
			return Race.ELYOS;
		}
		
		return Race.ASMODIANS;
	}
	
	/**
	 * Retrieves the {@link Npc} associated with the teleport spawn.<br>
	 * This method returns the specific NPC used for teleportation.
	 * @return the {@code Npc} object or {@code null} if no teleport is defined.
	 */
	public synchronized Npc getTeleport()
	{
		return spawns.get(SpawnType.TELEPORT);
	}
	
	/**
	 * Retrieves the {@link Npc} object associated with the house sign.<br>
	 * This method is thread-safe and returns the sign from the internal spawn map.
	 * @return The {@code Npc} representing the house sign, or {@code null} if none exists.
	 */
	public synchronized Npc getCurrentSign()
	{
		return spawns.get(SpawnType.SIGN);
	}
	
	/**
	 * Updates the {@link Npc} associated with a specific {@code SpawnType}.<br>
	 * If the provided {@code npc} is {@code null}, it removes the existing NPC.<br>
	 * Otherwise, it replaces the current NPC for that type.
	 * @param type The category of the spawn point.
	 * @param npc The {@link Npc} object to assign to this type.
	 */
	public synchronized void setSpawn(SpawnType type, Npc npc)
	{
		if (npc == null)
		{
			npc = spawns.remove(type);
			if (npc != null)
			{
				npc.getController().onDelete();
			}
		}
		else
		{
			spawns.put(type, npc);
		}
	}
	
	/**
	 * Retrieves the unique identifier for the current sign NPC.<br>
	 * This method determines which NPC to display based on the house status.<br>
	 * It checks conditions such as bidding status and ownership.
	 * @return The {@code int} ID of the active sign NPC.
	 */
	public int getCurrentSignNpcId()
	{
		int npcId = getLand().getWaitingSignNpcId(); // bidding closed
		if (status == HouseStatus.NOSALE)
		{
			npcId = getLand().getNosaleSignNpcId(); // invisible npc
		}
		else if (status == HouseStatus.SELL_WAIT)
		{
			if (HousingBidService.getInstance().isBiddingAllowed())
			{
				npcId = getLand().getSaleSignNpcId(); // bidding open
			}
		}
		else if (playerObjectId != 0)
		{
			if (status == HouseStatus.ACTIVE)
			{
				npcId = getLand().getHomeSignNpcId(); // resident information
			}
		}
		
		return npcId;
	}
	
	/**
	 * Removes the current owner from this house.<br>
	 * This method handles different logic based on the building type.<br>
	 * It resets ownership data and updates the house status to {@code NOSALE}.
	 * @return {@code true} if the revocation was successful, or {@code false} if the player ID is {@code 0}.
	 */
	public synchronized boolean revokeOwner()
	{
		if (playerObjectId == 0)
		{
			return false;
		}
		
		getRegistry().despawnObjects();
		if (getBuilding().getType() == BuildingType.PERSONAL_INS)
		{
			HousingService.getInstance().removeStudio(playerObjectId);
			DAOManager.getDAO(HousesDAO.class).deleteHouse(playerObjectId);
			return true;
		}
		
		houseRegistry = null;
		acquiredTime = null;
		sellStarted = null;
		nextPay = null;
		feePaid = true;
		
		final Building defaultBuilding = getLand().getDefaultBuilding();
		setOwnerId(0);
		if (defaultBuilding != building)
		{
			HousingService.getInstance().switchHouseBuilding(this, defaultBuilding.getId());
		}
		
		setStatus(HouseStatus.NOSALE);
		save();
		return true;
	}
	
	/**
	 * Retrieves the {@link HouseRegistry} for this house.<br>
	 * This method initializes a new registry if it does not already exist.<br>
	 * It also populates the default parts during initialization.
	 * @return The {@code HouseRegistry} instance associated with this house.
	 */
	public HouseRegistry getRegistry()
	{
		if (houseRegistry == null)
		{
			houseRegistry = new HouseRegistry(this);
			putDefaultParts();
		}
		
		return houseRegistry;
	}
	
	/**
	 * Refreshes the house registry data from the database.<br>
	 * This method clears the current {@code houseRegistry} and reloads it.<br>
	 * It also loads the registry for a specific player if provided.
	 */
	public synchronized void reloadHouseRegistry()
	{
		houseRegistry = null;
		getRegistry();
		if (playerObjectId != 0)
		{
			DAOManager.getDAO(PlayerRegisteredItemsDAO.class).loadRegistry(playerObjectId);
		}
	}
	
	/**
	 * Retrieves a specific decoration part for the house.<br>
	 * This method looks up the part based on its type and floor level.
	 * @param partType The {@code PartType} of the decoration to find.
	 * @param floor The floor number where the decoration is located.
	 * @return The {@link HouseDecoration} object corresponding to the request.
	 */
	public HouseDecoration getRenderPart(PartType partType, int floor)
	{
		return getRegistry().getRenderPart(partType, floor);
	}
	
	/**
	 * Retrieves the default decoration for a specific part type and floor.<br>
	 * This method looks up the value in the internal registry.
	 * @param partType The {@code PartType} of the house component.
	 * @param floor The floor number where the part is located.
	 * @return The default {@link HouseDecoration} for the given parameters.
	 */
	public HouseDecoration getDefaultPart(PartType partType, int floor)
	{
		return getRegistry().getDefaultPartByType(partType, floor);
	}
	
	/**
	 * Retrieves the {@link PlayerScripts} associated with this house.<br>
	 * This object contains specific scripts for the player owner.
	 * @return the {@code PlayerScripts} object.
	 */
	public PlayerScripts getPlayerScripts()
	{
		return playerScripts;
	}
	
	/**
	 * Retrieves the {@link HouseType} for this house.<br>
	 * This method determines the type based on the size of the associated {@link Building}.
	 * @return The {@code HouseType} corresponding to the building size.
	 */
	public HouseType getHouseType()
	{
		return HouseType.fromValue(getBuilding().getSize());
	}
	
	/**
	 * Saves the current house data to the database.<br>
	 * This method also saves the {@code houseRegistry} if it is not {@code null}.<br>
	 * It uses a synchronized block to ensure thread safety during the save operation.
	 */
	public synchronized void save()
	{
		DAOManager.getDAO(HousesDAO.class).storeHouse(this);
		
		// save registry if needed
		if (houseRegistry != null)
		{
			houseRegistry.save();
		}
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the {@code persistentState} of this decoration.<br>
	 * This method assigns a new {@link PersistentState} to the object.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		this.persistentState = persistentState;
	}
	
	/**
	 * Retrieves the owner information flags for this house.<br>
	 * These flags determine specific permissions or states related to the owner.
	 * @return a {@code byte} representing the owner info flags.
	 */
	public byte getHouseOwnerInfoFlags()
	{
		return houseOwnerInfoFlags;
	}
	
	/**
	 * Checks if the house owner has a specific status flag.<br>
	 * This method compares the current flags against the provided {@code PlayerHouseOwnerFlags}.
	 * @param status The {@code PlayerHouseOwnerFlags} to check for.
	 * @return {@code true} if the status bit is set, otherwise {@code false}.
	 */
	public boolean isInHousingStatus(PlayerHouseOwnerFlags status)
	{
		return (houseOwnerInfoFlags & status.getId()) != 0;
	}
	
	/**
	 * Updates the {@code houseOwnerInfoFlags} based on current status.<br>
	 * This method checks if a player owns the house and its current {@link HouseStatus}.<br>
	 * It applies specific bitwise flags for active, selling, or bidding states.
	 */
	public void fixBuildingStates()
	{
		houseOwnerInfoFlags = PlayerHouseOwnerFlags.SINGLE_HOUSE.getId();
		if (playerObjectId != 0)
		{
			houseOwnerInfoFlags |= PlayerHouseOwnerFlags.HAS_OWNER.getId();
			if (status == HouseStatus.ACTIVE)
			{
				houseOwnerInfoFlags |= PlayerHouseOwnerFlags.BIDDING_ALLOWED.getId();
				houseOwnerInfoFlags &= ~PlayerHouseOwnerFlags.SINGLE_HOUSE.getId();
			}
		}
		else if (status == HouseStatus.SELL_WAIT)
		{
			houseOwnerInfoFlags = PlayerHouseOwnerFlags.SELLING_HOUSE.getId();
		}
	}
	
	/**
	 * Retrieves the raw bytes of the house sign notice.<br>
	 * This method returns an empty {@code byte[]} array if no notice exists.<br>
	 * It uses a read lock to ensure thread safety during access.
	 * @return a {@code byte[]} containing the notice data or an empty array.
	 */
	public byte[] getSignNotice()
	{
		byte[] notice;
		readLock.lock();
		if (signNoticeStream == null)
		{
			notice = new byte[0];
		}
		else
		{
			notice = signNoticeStream.toByteArray();
		}
		
		readLock.unlock();
		return notice;
	}
	
	/**
	 * Updates the sign notice content for this house.<br>
	 * This method writes the provided byte array into the internal stream.<br>
	 * It ensures the data does not exceed the maximum allowed length.
	 * @param noticeStream The {@code byte[]} containing the new notice text.
	 */
	public void setSignNotice(byte[] noticeStream)
	{
		writeLock.lock();
		if (signNoticeStream == null)
		{
			signNoticeStream = new ByteArrayOutputStream(NOTICE_LENGTH);
		}
		
		signNoticeStream.reset();
		try
		{
			signNoticeStream.write(noticeStream, 0, Math.min(noticeStream.length, NOTICE_LENGTH));
		}
		finally
		{
			writeLock.unlock();
		}
	}
	
	/**
	 * Retrieves the minimum level required to interact with this house.<br>
	 * It checks the {@link HousingLand} for sale options.<br>
	 * If no land is associated, it returns a default value of {@code 10}.
	 * @return The minimum level restriction as an {@code int}.
	 */
	public int getLevelRestrict()
	{
		return land != null ? land.getSaleOptions().getMinLevel() : 10;
	}
	
	/**
	 * Retrieves the starting price for an auction based on the house type.<br>
	 * It checks {@link HousingConfig} for specific minimum bids first.<br>
	 * If no configuration is set, it returns the default gold price from the land sale options.
	 * @return The default auction price as a {@code long}.
	 */
	public long getDefaultAuctionPrice()
	{
		final Sale saleOptions = getLand().getSaleOptions();
		switch (getHouseType())
		{
			case HOUSE:
				if (HousingConfig.HOUSE_MIN_BID > 0)
				{
					return HousingConfig.HOUSE_MIN_BID;
				}
				break;
			case MANSION:
				if (HousingConfig.MANSION_MIN_BID > 0)
				{
					return HousingConfig.MANSION_MIN_BID;
				}
				break;
			case ESTATE:
				if (HousingConfig.ESTATE_MIN_BID > 0)
				{
					return HousingConfig.ESTATE_MIN_BID;
				}
				break;
			case PALACE:
				if (HousingConfig.PALACE_MIN_BID > 0)
				{
					return HousingConfig.PALACE_MIN_BID;
				}
				break;
			default:
				break;
		}
		
		return saleOptions.getGoldPrice();
	}
	
	/**
	 * Returns the human-readable name of this house.<br>
	 * This is useful for logging or displaying the house identity in the UI.
	 * @return The {@code String} name of the house.
	 */
	@Override
	public String toString()
	{
		return name;
	}
}
