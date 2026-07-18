/*
 * This file is part of the Mobius AionEmu project.
 * 
 * Mobius AionEmu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Mobius AionEmu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.siegeservice;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.npc.AbyssNpcType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SIEGE_LOCATION_STATE;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.world.World;

/**
 * Represents the core logic and data for a siege event in the game world.<br>
 * This class handles the state, rules, and mechanics associated with a {@link SiegeLocation}.
 * @author SoulKeeper, Source, A7xatomic, Mobius
 * @param <SL>
 */
public abstract class Siege<SL extends SiegeLocation>
{
	private static final Logger log = LoggerFactory.getLogger(Siege.class);
	private final AtomicBoolean finished = new AtomicBoolean();
	private final SiegeCounter siegeCounter = new SiegeCounter();
	private final SL siegeLocation;
	private boolean bossKilled;
	private SiegeNpc boss;
	private Date startTime;
	private boolean started;
	
	/**
	 * Creates a new instance of a {@link Siege}.<br>
	 * This constructor initializes the siege with a specific location.
	 * @param siegeLocation The {@code SL} object representing where the siege takes place.
	 */
	public Siege(SL siegeLocation)
	{
		this.siegeLocation = siegeLocation;
	}
	
	/**
	 * Starts the siege event for this location.<br>
	 * This method sets the start time and updates the {@code started} status.<br>
	 * It prevents multiple starts by checking if the siege is already active.<br>
	 * It also triggers the {@code onSiegeStart} logic and any relevant services.
	 */
	public void startSiege()
	{
		boolean doubleStart = false;
		
		// keeping synchronization as minimal as possible
		synchronized (this)
		{
			if (started)
			{
				doubleStart = true;
			}
			else
			{
				startTime = new Date();
				started = true;
			}
		}
		
		if (doubleStart)
		{
			log.error("[SiegeService] Attempt to start siege of SiegeLocation#" + siegeLocation.getLocationId() + " for 2 times");
			return;
		}
		
		onSiegeStart();
		
		// Check for Balaur Assault
		if (SiegeConfig.BALAUR_AUTO_ASSAULT)
		{
			BalaurAssaultService.getInstance().onSiegeStart(this);
		}
	}
	
	/**
	 * Starts a new siege event at the specified location.<br>
	 * This method calls {@code startSiege} to begin the process.
	 * @param locationId The unique identifier for the siege area.
	 */
	public void startSiege(int locationId)
	{
		SiegeService.getInstance().startSiege(locationId);
	}
	
	/**
	 * Stops the current siege event.<br>
	 * This method sets the {@code finished} state to {@code true}.<br>
	 * It triggers the {@code onSiegeFinish} logic and notifies relevant services.<br>
	 * If the siege is already finished, an error will be logged.
	 */
	public void stopSiege()
	{
		if (finished.compareAndSet(false, true))
		{
			onSiegeFinish();
			
			if (SiegeConfig.BALAUR_AUTO_ASSAULT)
			{
				BalaurAssaultService.getInstance().onSiegeFinish(this);
			}
		}
		else
		{
			log.error("[SiegeService] Attempt to stop siege of SiegeLocation#" + siegeLocation.getLocationId() + " for 2 times");
		}
	}
	
	/**
	 * Retrieves the current {@code SiegeLocation} for this siege.<br>
	 * This method returns the specific location data stored in the instance.
	 * @return The {@code SL} object representing the siege location.
	 */
	public SL getSiegeLocation()
	{
		return siegeLocation;
	}
	
	/**
	 * Retrieves the unique identifier for the current siege location.<br>
	 * This ID is used to identify which specific area is being contested.
	 * @return The {@code int} value of the siege location ID.
	 */
	public int getSiegeLocationId()
	{
		return siegeLocation.getLocationId();
	}
	
	/**
	 * Checks if the siege boss has been defeated.<br>
	 * This method returns the current state of the {@code bossKilled} flag.
	 * @return {@code true} if the boss is dead, {@code false} otherwise.
	 */
	public boolean isBossKilled()
	{
		return bossKilled;
	}
	
	/**
	 * Updates the status of whether the siege boss has been defeated.<br>
	 * This method sets the {@code bossKilled} field to the provided value.
	 * @param bossKilled The new state indicating if the boss is dead. Set to {@code true} if killed, or {@code false} otherwise.
	 */
	public void setBossKilled(boolean bossKilled)
	{
		this.bossKilled = bossKilled;
	}
	
	/**
	 * Retrieves the current boss for this siege.<br>
	 * This method returns the {@code SiegeNpc} object associated with the event.
	 * @return The {@code SiegeNpc} instance, or {@code null} if no boss exists.
	 */
	public SiegeNpc getBoss()
	{
		return boss;
	}
	
	/**
	 * Sets the main boss for this {@link Siege}.<br>
	 * This method assigns a {@code SiegeNpc} instance to the siege.
	 * @param boss The {@code SiegeNpc} object to be set as the boss.
	 */
	public void setBoss(SiegeNpc boss)
	{
		this.boss = boss;
	}
	
	/**
	 * Retrieves the current {@code SiegeCounter} instance.<br>
	 * This object tracks progress and statistics for the active siege.
	 * @return The {@code SiegeCounter} associated with this siege.
	 */
	public SiegeCounter getSiegeCounter()
	{
		return siegeCounter;
	}
	
	protected abstract void onSiegeStart();
	
	protected abstract void onSiegeFinish();
	
	public abstract boolean isEndless();
	
	public abstract void addAbyssPoints(Player player, int abysPoints);
	
	public abstract void addGloryPoints(Player player, int gloryPoints);
	
	/**
	 * Checks if the siege event has been started.<br>
	 * This method returns {@code true} if the siege is currently active.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if the siege has started, {@code false} otherwise.
	 */
	public boolean isStarted()
	{
		return started;
	}
	
	/**
	 * Checks if the current process has completed.<br>
	 * This method retrieves the status from the internal {@code Future}.
	 * @return {@code true} if the process is finished, {@code false} otherwise.
	 */
	public boolean isFinished()
	{
		return finished.get();
	}
	
	/**
	 * Retrieves the date and time when the siege began.<br>
	 * This value is set when {@code startSiege} is called.
	 * @return The {@code Date` object representing the start time.}
	 */
	public Date getStartTime()
	{
		return startTime;
	}
	
	/**
	 * Registers the death handler for the siege boss.<br>
	 * It wires the boss AI to stop the siege when the boss dies.
	 */
	protected void registerSiegeBossListeners()
	{
		((AbstractAI) getBoss().getAi2()).setOnDeathAfter(() ->
		{
			setBossKilled(true);
			SiegeService.getInstance().stopSiege(getSiegeLocationId());
		});
	}
	
	/**
	 * Removes the listeners associated with the siege boss.<br>
	 * It ensures that the death callback is removed from the boss's AI components.
	 */
	protected void unregisterSiegeBossListeners()
	{
		if (getBoss() != null)
		{
			((AbstractAI) getBoss().getAi2()).setOnDeathAfter(null);
		}
	}
	
	/**
	 * Initializes the boss NPC for the current siege.<br>
	 * This method searches for a valid {@link SiegeNpc} in the world based on its template or AI type.<br>
	 * It sets the identified boss and registers the necessary listeners.
	 */
	protected void initSiegeBoss()
	{
		SiegeNpc boss = null;
		
		final Collection<SiegeNpc> npcs = World.getInstance().getLocalSiegeNpcs(getSiegeLocationId());
		for (SiegeNpc npc : npcs)
		{
			if (npc.getObjectTemplate().getAbyssNpcType().equals(AbyssNpcType.BOSS) || npc.getObjectTemplate().getAi().equals("artifact_protector") || npc.getObjectTemplate().getAi().equals("siege_protector"))
			{
				// TODO: AbyssNpcType for artifacts
				
				if (boss != null)
				{
					throw new SiegeException("[SiegeService] Found 2 siege bosses for outpost " + getSiegeLocationId() + " NPC " + npc.getNpcId());
				}
				
				boss = npc;
			}
		}
		
		if (boss == null)
		{
			throw new SiegeException("[SiegeService] Siege Boss not found for siege " + getSiegeLocationId());
		}
		
		setBoss(boss);
		registerSiegeBossListeners();
	}
	
	/**
	 * Spawns NPCs for a specific siege location.<br>
	 * This method calls the {@link SiegeService} to handle the spawning logic.
	 * @param locationId The unique identifier for the siege location.
	 * @param race The type of race for the NPCs to spawn.
	 * @param type The modification type applied to the spawned NPCs.
	 */
	protected void spawnNpcs(int locationId, SiegeRace race, SiegeModType type)
	{
		SiegeService.getInstance().spawnNpcs(locationId, race, type);
	}
	
	/**
	 * Removes all NPCs from a specific siege area.<br>
	 * This method calls {@code deSpawnNpcs} to clear the location.
	 * @param locationId The unique identifier for the siege location.
	 */
	protected void deSpawnNpcs(int locationId)
	{
		SiegeService.getInstance().deSpawnNpcs(locationId);
	}
	
	/**
	 * Sends the current state of a siege to all connected clients.<br>
	 * This method uses {@link SiegeService} to broadcast the {@code SM_SIEGE_LOCATION_STATE} packet.
	 * @param location The {@code SiegeLocation} object containing the data to be sent.
	 */
	protected void broadcastState(SiegeLocation location)
	{
		SiegeService.getInstance().broadcast(new SM_SIEGE_LOCATION_STATE(location), null);
	}
	
	/**
	 * Sends a status update for the siege to all connected clients.<br>
	 * This method calls {@code broadcastUpdate}.
	 * @param location The {@code SiegeLocation} object containing current data.
	 */
	protected void broadcastUpdate(SiegeLocation location)
	{
		SiegeService.getInstance().broadcastUpdate(location);
	}
	
	/**
	 * Sends a siege update to all connected clients.<br>
	 * This method uses {@link SiegeService} to broadcast the current state.
	 * @param location The {@code SiegeLocation} object containing the data.
	 * @param nameId The unique identifier for the description name.
	 */
	protected void broadcastUpdate(SiegeLocation location, int nameId)
	{
		SiegeService.getInstance().broadcastUpdate(location, new DescriptionId(nameId));
	}
}
