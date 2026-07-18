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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.dynamicportal.DynamicPortalLocation;
import com.aionemu.gameserver.model.dynamicportal.DynamicPortalStateType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.dynamicportalspawns.DynamicPortalSpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.dynamicportal.DynamicPortal;
import com.aionemu.gameserver.services.dynamicportal.Portal;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * Manages the lifecycle and logic of dynamic portals within the game world.<br>
 * This service handles portal spawning, state transitions, and interactions with {@link Player} objects.
 * @author Falke_34
 */
public class DynamicPortalService
{
	private Map<Integer, DynamicPortalLocation> dynamicPortal;
	private static final int duration = CustomConfig.DYNAMIC_PORTAL_DURATION;
	private final Map<Integer, DynamicPortal<?>> activeDynamicPortal = new ConcurrentHashMap<>();
	private static final Logger log = LoggerFactory.getLogger(DynamicPortalService.class);
	
	/**
	 * Initializes the dynamic portal locations from the data manager.<br>
	 * This method checks if dynamic portals are enabled in {@code CustomConfig}.<br>
	 * It spawns all locations in a {@code CLOSED} state.<br>
	 * It also schedules a recurring task to start the first portal and notify players.
	 */
	public void initDynamicPortalLocations()
	{
		if (CustomConfig.DYNAMIC_PORTAL_ENABLED)
		{
			dynamicPortal = DataManager.DYNAMIC_PORTAL_DATA.getDynamicPortalLocations();
			
			for (DynamicPortalLocation loc : getDynamicPortalLocations().values())
			{
				spawn(loc, DynamicPortalStateType.CLOSED);
			}
			
			log.info("[DynamicPortalService] Loaded " + dynamicPortal.size() + " dynamic portal locations");
			
			CronService.getInstance().schedule(() ->
			{
				startDynamicPortal(1);
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_Open_01));
			}, CustomConfig.DYNAMIC_PORTAL_KATALAM_SCHEDULE);
		}
		else
		{
			dynamicPortal = Collections.emptyMap();
		}
	}
	
	/**
	 * Starts a new dynamic portal based on the provided ID.<br>
	 * This method creates a {@code Portal} object and adds it to the active list.<br>
	 * It also schedules an automatic shutdown after the configured duration.
	 * @param id The unique identifier for the dynamic portal.
	 */
	public void startDynamicPortal(int id)
	{
		final DynamicPortal<?> portal;
		
		synchronized (this)
		{
			if (activeDynamicPortal.containsKey(id))
			{
				return;
			}
			
			portal = new Portal(dynamicPortal.get(id));
			activeDynamicPortal.put(id, portal);
		}
		
		portal.start();
		
		ThreadPoolManager.getInstance().schedule(() -> stopDynamicPortal(id), duration * 3600 * 1000);
	}
	
	/**
	 * Stops an active dynamic portal based on its unique identifier.<br>
	 * This method removes the portal from the {@code activeDynamicPortal} map.<br>
	 * It calls the {@code stop} method if the portal is still open.
	 * @param id The unique identifier of the dynamic portal to stop.
	 */
	public void stopDynamicPortal(int id)
	{
		if (!isDynamicPortalInProgress(id))
		{
			return;
		}
		
		DynamicPortal<?> portal;
		synchronized (this)
		{
			portal = activeDynamicPortal.remove(id);
		}
		
		if ((portal == null) || portal.isClosed())
		{
			return;
		}
		
		portal.stop();
	}
	
	/**
	 * Spawns objects for a dynamic portal based on its location and state.<br>
	 * This method checks the {@code DynamicPortalSpawnTemplate} data.<br>
	 * It adds spawned objects to the {@link DynamicPortalLocation} list.
	 * @param loc The {@code DynamicPortalLocation} where the portal is located.
	 * @param dstate The current {@code DynamicPortalStateType} of the portal.
	 */
	public void spawn(DynamicPortalLocation loc, DynamicPortalStateType dstate)
	{
		if (dstate.equals(DynamicPortalStateType.OPEN))
		{
		}
		
		final List<SpawnGroup2> locSpawns = DataManager.SPAWNS_DATA2.getDynamicPortalSpawnsByLocId(loc.getId());
		for (SpawnGroup2 group : locSpawns)
		{
			for (SpawnTemplate st : group.getSpawnTemplates())
			{
				final DynamicPortalSpawnTemplate dynamicPortaltemplate = (DynamicPortalSpawnTemplate) st;
				if (dynamicPortaltemplate.getDStateType().equals(dstate))
				{
					loc.getSpawned().add(SpawnEngine.spawnObject(dynamicPortaltemplate, 1));
				}
			}
		}
	}
	
	/**
	 * Removes all NPCs spawned at a specific location.<br>
	 * This method cancels the respawn tasks for those entities.<br>
	 * It also clears the list of spawned objects from the {@code DynamicPortalLocation}.
	 * @param loc The {@code DynamicPortalLocation} to clear.
	 */
	public void despawn(DynamicPortalLocation loc)
	{
		for (VisibleObject npc : loc.getSpawned())
		{
			((Npc) npc).getController().cancelTask(TaskId.RESPAWN);
			npc.getController().onDelete();
		}
		
		loc.getSpawned().clear();
	}
	
	/**
	 * Checks if a dynamic portal is currently active.<br>
	 * This method looks up the provided {@code id} in the internal map of active portals.
	 * @param id The unique identifier of the dynamic portal to check.
	 * @return {@code true} if the portal with the given {@code id} is in progress, {@code false} otherwise.
	 */
	public boolean isDynamicPortalInProgress(int id)
	{
		return activeDynamicPortal.containsKey(id);
	}
	
	/**
	 * Retrieves the collection of currently active dynamic portals.<br>
	 * The map uses an {@code Integer} ID as the key for each portal.
	 * @return A {@link Map} containing all active {@link DynamicPortal} objects.
	 */
	public Map<Integer, DynamicPortal<?>> getActiveDynamicPortal()
	{
		return activeDynamicPortal;
	}
	
	/**
	 * Retrieves the total duration for a dynamic portal.<br>
	 * This value is fetched from {@code CustomConfig}.
	 * @return The duration as an {@code int}.
	 */
	public int getDuration()
	{
		return duration;
	}
	
	/**
	 * Retrieves the location of a specific dynamic portal.<br>
	 * This method looks up the data using the provided {@code id}.<br>
	 * It returns {@code null} if no portal is found for that ID.
	 * @param id The unique identifier of the dynamic portal.
	 * @return The {@link DynamicPortalLocation} associated with the given {@code id}, or {@code null}.
	 */
	public DynamicPortalLocation getDynamicPortalLocation(int id)
	{
		return dynamicPortal.get(id);
	}
	
	/**
	 * Retrieves all current dynamic portal locations.<br>
	 * The map uses the unique ID as the key.
	 * @return A {@code Map<Integer, DynamicPortalLocation>} containing all active locations.
	 */
	public Map<Integer, DynamicPortalLocation> getDynamicPortalLocations()
	{
		return dynamicPortal;
	}
	
	/**
	 * Provides access to the singleton instance of {@link DynamicPortalService}.<br>
	 * Use this method to get the global service for managing dynamic portals.
	 * @return The single instance of {@code DynamicPortalService}.
	 */
	public static DynamicPortalService getInstance()
	{
		return DynamicPortalServiceHolder.INSTANCE;
	}
	
	private static class DynamicPortalServiceHolder
	{
		private static final DynamicPortalService INSTANCE = new DynamicPortalService();
	}
}
