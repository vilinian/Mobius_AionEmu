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
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.vortexspawns.VortexSpawnTemplate;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import com.aionemu.gameserver.model.vortex.VortexStateType;
import com.aionemu.gameserver.services.rift.RiftInformer;
import com.aionemu.gameserver.services.rift.RiftManager;
import com.aionemu.gameserver.services.vortexservice.DimensionalVortex;
import com.aionemu.gameserver.services.vortexservice.Invasion;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * Manages the lifecycle and logic of dimensional vortexes within the game world.<br>
 * This service handles spawning, state transitions, and related events for {@link DimensionalVortex} entities.
 * @author Source
 */
public class VortexService
{
	private static final int duration = CustomConfig.VORTEX_DURATION;
	private final Map<Integer, DimensionalVortex<?>> activeInvasions = new ConcurrentHashMap<>();
	private Map<Integer, VortexLocation> vortex;
	private static final Logger log = LoggerFactory.getLogger(VortexService.class);
	
	/**
	 * Initializes the vortex locations from the data manager.<br>
	 * This method checks if vortices are enabled in {@code CustomConfig}.<br>
	 * It spawns all initial locations in a {@code PEACE} state.<br>
	 * It also schedules periodic invasions using {@link CronService}.
	 */
	public void initVortexLocations()
	{
		if (CustomConfig.VORTEX_ENABLED)
		{
			vortex = DataManager.VORTEX_DATA.getVortexLocations();
			
			// Spawn peace
			for (VortexLocation loc : getVortexLocations().values())
			{
				spawn(loc, VortexStateType.PEACE);
			}
			
			log.info("[VortexService] Loaded " + vortex.size() + " vortex locations");
			
			// Brusthonin schedule
			CronService.getInstance().schedule(new Runnable()
			{
				@Override
				public void run()
				{
					startInvasion(1);
				}
			}, CustomConfig.VORTEX_BRUSTHONIN_SCHEDULE);
			
			// Theobomos schedule
			CronService.getInstance().schedule(new Runnable()
			{
				@Override
				public void run()
				{
					startInvasion(0);
				}
			}, CustomConfig.VORTEX_THEOBOMOS_SCHEDULE);
		}
		else
		{
			vortex = Collections.emptyMap();
		}
	}
	
	/**
	 * Starts a new invasion based on the provided unique identifier.<br>
	 * This method checks if an invasion with {@code id} is already active.<br>
	 * It initializes the {@link Invasion} and schedules its completion.
	 * @param id The unique identifier for the vortex location.
	 */
	public void startInvasion(int id)
	{
		final DimensionalVortex<?> invasion;
		
		synchronized (this)
		{
			if (activeInvasions.containsKey(id))
			{
				return;
			}
			
			invasion = new Invasion(vortex.get(id));
			activeInvasions.put(id, invasion);
		}
		
		invasion.start();
		
		// Scheduled invasion end
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				if (!invasion.isGeneratorDestroyed())
				{
					stopInvasion(id);
				}
			}
		}, duration * 3600 * 1000);
	}
	
	/**
	 * Stops an active invasion based on the provided identifier.<br>
	 * This method checks if the invasion is currently running before attempting to stop it.<br>
	 * It removes the invasion from the {@code activeInvasions} map and calls its stop logic.
	 * @param id The unique identifier of the invasion to stop.
	 */
	public void stopInvasion(int id)
	{
		if (!isInvasionInProgress(id))
		{
			return;
		}
		
		DimensionalVortex<?> invasion;
		synchronized (this)
		{
			invasion = activeInvasions.remove(id);
		}
		
		if ((invasion == null) || invasion.isFinished())
		{
			return;
		}
		
		invasion.stop();
	}
	
	/**
	 * Spawns a dimensional vortex and associated NPCs at a specific location.<br>
	 * This method checks the {@code state} to determine if it should trigger an invasion.<br>
	 * It uses {@link SpawnEngine} to create objects based on the provided {@code VortexLocation}.
	 * @param loc The {@code VortexLocation} where the vortex and NPCs will appear.
	 * @param state The {@code VortexStateType} used to filter which templates to spawn.
	 */
	public void spawn(VortexLocation loc, VortexStateType state)
	{
		// Spawn Dimensional Vortex
		if (state.equals(VortexStateType.INVASION))
		{
			RiftManager.getInstance().spawnVortex(loc);
			RiftInformer.sendRiftsInfo(loc.getHomeWorldId());
		}
		
		// Spawn NPC
		final List<SpawnGroup2> locSpawns = DataManager.SPAWNS_DATA2.getVortexSpawnsByLocId(loc.getId());
		for (SpawnGroup2 group : locSpawns)
		{
			for (SpawnTemplate st : group.getSpawnTemplates())
			{
				final VortexSpawnTemplate vortextemplate = (VortexSpawnTemplate) st;
				if (vortextemplate.getStateType().equals(state))
				{
					loc.getSpawned().add(SpawnEngine.spawnObject(vortextemplate, 1));
				}
			}
		}
	}
	
	/**
	 * Removes all active entities from a specific vortex location.<br>
	 * This method clears the spawned list and cancels respawn tasks for all {@link Npc} objects.<br>
	 * It also sets the {@code VortexController} of the provided location to {@code null}.
	 * @param loc The {@link VortexLocation} to be cleared.
	 */
	public void despawn(VortexLocation loc)
	{
		// Unset Vortex controller
		loc.setVortexController(null);
		
		// Despawn all NPC
		for (VisibleObject npc : loc.getSpawned())
		{
			((Npc) npc).getController().cancelTask(TaskId.RESPAWN);
			npc.getController().onDelete();
		}
		
		loc.getSpawned().clear();
	}
	
	/**
	 * Checks if an invasion is currently active for a specific ID.<br>
	 * It looks up the {@code id} in the internal map of active invasions.
	 * @param id The unique identifier of the invasion to check.
	 * @return {@code true} if the invasion is running, {@code false} otherwise.
	 */
	public boolean isInvasionInProgress(int id)
	{
		return activeInvasions.containsKey(id);
	}
	
	/**
	 * Retrieves all currently active invasions.<br>
	 * Each entry maps a unique ID to its corresponding {@link DimensionalVortex} object.
	 * @return A {@code Map} containing the IDs and objects of all active invasions.
	 */
	public Map<Integer, DimensionalVortex<?>> getActiveInvasions()
	{
		return activeInvasions;
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
	 * Removes a {@link Player} from the list of defenders in all active invasions.<br>
	 * This method checks every {@code DimensionalVortex} for the player's object ID.<br>
	 * If found, it calls {@code kickPlayer} to remove them from that specific invasion.
	 * @param player The {@code Player} object to be removed from the defender list.
	 */
	public void removeDefenderPlayer(Player player)
	{
		for (DimensionalVortex<?> invasion : activeInvasions.values())
		{
			if (invasion.getDefenders().containsKey(player.getObjectId()))
			{
				invasion.kickPlayer(player, false);
				return;
			}
		}
	}
	
	/**
	 * Removes a player from all active invasions.<br>
	 * This method checks if the {@code Player} is currently an invader.<br>
	 * If found, it calls {@code boolean)} to remove them.
	 * @param player The {@code Player} object to be removed from the invasion.
	 */
	public void removeInvaderPlayer(Player player)
	{
		for (DimensionalVortex<?> invasion : activeInvasions.values())
		{
			if (invasion.getInvaders().containsKey(player.getObjectId()))
			{
				invasion.kickPlayer(player, true);
				return;
			}
		}
	}
	
	/**
	 * Checks if the given {@link Player} is currently participating as an invader.<br>
	 * It iterates through all active invasions to find a match.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player is an invader, {@code false} otherwise.
	 */
	public boolean isInvaderPlayer(Player player)
	{
		for (DimensionalVortex<?> invasion : activeInvasions.values())
		{
			if (invasion.getInvaders().containsKey(player.getObjectId()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific player is currently standing inside a vortex zone.<br>
	 * This method verifies the world ID and checks the location data.
	 * @param player The {@link Player} object to check.
	 * @return {@code true} if the player is in a vortex, otherwise {@code false}.
	 */
	public boolean isInsideVortexZone(Player player)
	{
		final int playerWorldId = player.getWorldId();
		
		if ((playerWorldId == 210060000) || (playerWorldId == 220050000))
		{
			final VortexLocation loc = getLocationByWorld(playerWorldId);
			if (loc != null)
			{
				return loc.getPlayers().containsKey(player.getObjectId());
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the {@link VortexLocation} associated with a specific rift.<br>
	 * This method uses the {@code npcId} to determine the correct world index.<br>
	 * It then calls {@code getVortexLocation} to find the location.
	 * @param npcId The unique identifier of the NPC used to identify the rift.
	 * @return The {@code VortexLocation} for the identified rift.
	 */
	public VortexLocation getLocationByRift(int npcId)
	{
		return getVortexLocation(npcId == 831141 ? 1 : 0);
	}
	
	/**
	 * Retrieves the {@link VortexLocation} for a specific world.<br>
	 * This method maps the provided {@code worldId} to its corresponding vortex location.<br>
	 * It returns {@code null} if the ID does not match any known world.
	 * @param worldId The unique identifier of the world.
	 * @return The {@link VortexLocation} for the given world, or {@code null}.
	 */
	public VortexLocation getLocationByWorld(int worldId)
	{
		if (worldId == 210060000)
		{
			return getVortexLocation(0);
		}
		else if (worldId == 220050000)
		{
			return getVortexLocation(1);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Retrieves the {@link VortexLocation} associated with a specific ID.<br>
	 * This method looks up the location in the internal map.
	 * @param id The unique identifier for the vortex.
	 * @return The {@code VortexLocation} object, or {@code null} if not found.
	 */
	public VortexLocation getVortexLocation(int id)
	{
		return vortex.get(id);
	}
	
	/**
	 * Retrieves all current vortex locations.<br>
	 * The map uses the unique identifier as the key.
	 * @return a {@code Map} containing all {@link VortexLocation} objects.
	 */
	public Map<Integer, VortexLocation> getVortexLocations()
	{
		return vortex;
	}
	
	/**
	 * Checks if the {@code player} is in a valid login zone.<br>
	 * This method moves the {@code player} to the home point if they are an invader in an active vortex.<br>
	 * It uses {@code getLocationByWorld} to find the correct location.
	 * @param player The {@code Player} object to validate.
	 */
	public void validateLoginZone(Player player)
	{
		final VortexLocation loc = getLocationByWorld(player.getWorldId());
		if ((loc != null) && player.getRace().equals(loc.getInvadersRace()))
		{
			if (loc.isInsideLocation(player) && loc.isActive() && loc.getVortexController().getPassedPlayers().containsKey(player.getObjectId()))
			{
				return;
			}
			
			final int mapId = loc.getHomeWorldId();
			final float x = loc.getHomePoint().getX();
			final float y = loc.getHomePoint().getY();
			final float z = loc.getHomePoint().getZ();
			final byte h = loc.getHomePoint().getHeading();
			World.getInstance().setPosition(player, mapId, x, y, z, h);
		}
	}
	
	/**
	 * Provides the singleton instance of the {@link VortexService}.<br>
	 * Use this method to access the global vortex manager.
	 * @return The active {@code VortexService} instance.
	 */
	public static VortexService getInstance()
	{
		return VortexServiceHolder.INSTANCE;
	}
	
	private static class VortexServiceHolder
	{
		private static final VortexService INSTANCE = new VortexService();
	}
}
