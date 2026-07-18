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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.base.BaseLocation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FLAG_INFO;
import com.aionemu.gameserver.services.base.Base;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * This class serves as the abstract base for all service components in the game server.<br>
 * It provides common functionality and shared logic for various {@link com.aionemu.gameserver.services.base.Base} implementations.
 * @author Source
 */
public class BaseService
{
	private static final Logger log = LoggerFactory.getLogger(BaseService.class);
	private final Map<Integer, Base<?>> active = new ConcurrentHashMap<>();
	private Map<Integer, BaseLocation> bases;
	
	/**
	 * Initializes the base locations from the data manager.<br>
	 * This method populates the {@code bases} map with all available location data.<br>
	 * It logs the total number of bases loaded to the server console.
	 */
	public void initBaseLocations()
	{
		bases = DataManager.BASE_DATA.getBaseLocations();
		log.info("[BaseService] Loaded " + bases.size() + " Bases");
	}
	
	/**
	 * Initializes all base locations in the game world.<br>
	 * This method iterates through every {@code BaseLocation}.<br>
	 * It calls the {@code start} method for each base found.
	 */
	public void initBases()
	{
		log.info("[BaseService] started ...");
		for (BaseLocation base : getBaseLocations().values())
		{
			start(base.getId());
		}
	}
	
	/**
	 * Disables all active bases in the game world.<br>
	 * This method logs a message to indicate that the service is shutting down.
	 */
	public void basesDisabled()
	{
		log.info("[BaseService] Disabled ...");
	}
	
	/**
	 * Retrieves the map of all registered base locations.<br>
	 * Each {@code Integer} key represents a unique base ID.
	 * @return A {@link Map} containing IDs and their corresponding {@link BaseLocation} objects.
	 */
	public Map<Integer, BaseLocation> getBaseLocations()
	{
		return bases;
	}
	
	/**
	 * Retrieves a specific {@link BaseLocation} using its unique identifier.<br>
	 * This method looks up the location in the internal base map.
	 * @param id The unique integer ID of the base to find.
	 * @return The {@code BaseLocation} associated with the provided ID, or {@code null} if not found.
	 */
	public BaseLocation getBaseLocation(int id)
	{
		return bases.get(id);
	}
	
	/**
	 * Starts a specific base instance.<br>
	 * This method checks if the base is already active.<br>
	 * If it is not active, it creates and starts a new {@code Base} object.
	 * @param id The unique identifier of the base to start.
	 */
	public void start(int id)
	{
		final Base<?> base;
		
		synchronized (this)
		{
			if (active.containsKey(id))
			{
				return;
			}
			
			base = new Base<>(getBaseLocation(id));
			active.put(id, base);
		}
		
		base.start();
	}
	
	/**
	 * Stops the active base associated with a specific ID.<br>
	 * This method removes the base from the active list and calls its {@code stop()} method.<br>
	 * It then attempts to restart the base using {@code start}.
	 * @param id The unique identifier of the base to stop.
	 */
	public void stop(int id)
	{
		if (!isActive(id))
		{
			log.info("[BaseService] Trying to stop not active base:" + id);
			return;
		}
		
		Base<?> base;
		synchronized (this)
		{
			base = active.remove(id);
		}
		
		if ((base == null) || base.isFinished())
		{
			log.info("[BaseService] Trying to stop null or finished base:" + id);
			return;
		}
		
		base.stop();
		start(id);
	}
	
	/**
	 * This method updates the owner of a specific base.<br>
	 * It sets the new {@code Race} for the base identified by {@code id}.<br>
	 * The method then stops the current activity and broadcasts the update.
	 * @param id The unique identifier of the base to capture.
	 * @param race The {@link Race} that is capturing the base.
	 */
	public void capture(int id, Race race)
	{
		if (!isActive(id))
		{
			log.info("[BaseService] Detecting not active base capture baseId: " + id);
			return;
		}
		
		getActiveBase(id).setRace(race);
		stop(id);
		broadcastUpdate(getBaseLocation(id));
	}
	
	/**
	 * Checks if a specific base is currently active.<br>
	 * This method looks up the {@code id} in the internal active map.
	 * @param id The unique identifier of the base to check.
	 * @return {@code true} if the base is active, {@code false} otherwise.
	 */
	public boolean isActive(int id)
	{
		return active.containsKey(id);
	}
	
	/**
	 * Retrieves the {@link Base} object for a specific ID.<br>
	 * This method looks up the base in the active collection.<br>
	 * It returns {@code null} if no active base is found.
	 * @param id The unique identifier of the base to retrieve.
	 * @return The {@code Base} instance associated with the given {@code id}, or {@code null}.
	 */
	public Base<?> getActiveBase(int id)
	{
		return active.get(id);
	}
	
	/**
	 * This method is called when a {@link Player} enters a base world.<br>
	 * It checks all active bases in the current world.<br>
	 * If a match is found, it sends a flag update and refreshes the player's zone and quests.
	 * @param player The {@code Player} object who entered the base world.
	 */
	public void onEnterBaseWorld(Player player)
	{
		for (BaseLocation baseLocation : getBaseLocations().values())
		{
			if ((baseLocation.getWorldId() == player.getWorldId()) && isActive(baseLocation.getId()))
			{
				final Base<?> base = getActiveBase(baseLocation.getId());
				PacketSendUtility.sendPacket(player, new SM_FLAG_INFO(1, base.getFlag()));
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		}
	}
	
	/**
	 * Sends a status update to all players in the same world as the base.<br>
	 * This method checks if the {@code BaseLocation} is currently active.<br>
	 * If active, it updates player flags and refreshes zone information.
	 * @param baseLocation The {@link BaseLocation} object containing the update data.
	 */
	public void broadcastUpdate(BaseLocation baseLocation)
	{
		World.getInstance().getWorldMap(baseLocation.getWorldId()).getMainWorldMapInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				if (isActive(baseLocation.getId()))
				{
					final Base<?> base = getActiveBase(baseLocation.getId());
					PacketSendUtility.sendPacket(player, new SM_FLAG_INFO(1, base.getFlag()));
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
				}
			}
		});
	}
	
	/**
	 * Retrieves the singleton instance of {@link BaseService}.<br>
	 * Use this method to access the global service manager.
	 * @return The single shared instance of {@code BaseService}.
	 */
	public static BaseService getInstance()
	{
		return BaseServiceHolder.INSTANCE;
	}
	
	private static class BaseServiceHolder
	{
		private static final BaseService INSTANCE = new BaseService();
	}
}
