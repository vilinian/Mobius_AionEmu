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
package com.aionemu.gameserver.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.configs.schedule.SiegeSchedule;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.Influence;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.siege.SourceLocation;
import com.aionemu.gameserver.model.stats.container.NpcLifeStats;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_ARTIFACT_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORTRESS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORTRESS_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INFLUENCE_RATIO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHIELD_EFFECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SIEGE_LOCATION_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UNK_127;
import com.aionemu.gameserver.services.siegeservice.ArtifactSiege;
import com.aionemu.gameserver.services.siegeservice.FortressSiege;
import com.aionemu.gameserver.services.siegeservice.Siege;
import com.aionemu.gameserver.services.siegeservice.SiegeException;
import com.aionemu.gameserver.services.siegeservice.SiegeStartRunnable;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Manages the core logic and lifecycle of siege events within the game world.<br>
 * This service handles various siege types including fortress and artifact competitions.<br>
 * It coordinates NPC spawning, influence tracking, and synchronization with {@link SiegeSchedule}.
 * @author SoulKeeper, Source, Mobius
 */
public class SiegeService
{
	/**
	 * Just a logger
	 */
	private static final Logger log = LoggerFactory.getLogger("SIEGE_LOG");
	/**
	 * We should broadcast fortress status every hour Actually only influence packet must be sent, but that doesn't matter
	 */
	private static final String SIEGE_LOCATION_STATUS_BROADCAST_SCHEDULE = "0 0 * ? * *";
	/**
	 * Singleton that is loaded on the class initialization. Guys, we really do not SingletonHolder classes
	 */
	private static final SiegeService instance = new SiegeService();
	/**
	 * Map that holds fortressId to Siege. We can easily know what fortresses is under siege ATM :)
	 */
	private final Map<Integer, Siege<?>> activeSieges = new ConcurrentHashMap<>();
	/**
	 * Object that holds siege schedule.<br>
	 * And maybe other useful information (in future).
	 */
	private SiegeSchedule siegeSchedule;
	
	// Player list on RVR Event.
	private List<Player> rvrPlayersOnEvent = new ArrayList<>();
	
	/**
	 * Retrieves the singleton instance of the {@link SiegeService}.<br>
	 * This method provides a global access point to the siege management system.
	 * @return The active {@code SiegeService} instance.
	 */
	public static SiegeService getInstance()
	{
		return instance;
	}
	
	private Map<Integer, ArtifactLocation> artifacts;
	private Map<Integer, FortressLocation> fortresses;
	private Map<Integer, SiegeLocation> locations;
	
	/**
	 * Initializes the siege locations from the data manager.<br>
	 * This method loads artifacts, fortresses, and siege locations into memory.<br>
	 * It checks if sieges are enabled in {@code SiegeConfig}.<br>
	 * If disabled, it initializes all collections as empty maps.
	 */
	public void initSiegeLocations()
	{
		if (SiegeConfig.SIEGE_ENABLED)
		{
			if (siegeSchedule != null)
			{
				log.error("[SiegeService] SiegeService should not be initialized two times!");
				return;
			}
			
			// initialize current siege locations
			artifacts = DataManager.SIEGE_LOCATION_DATA.getArtifacts();
			fortresses = DataManager.SIEGE_LOCATION_DATA.getFortress();
			locations = DataManager.SIEGE_LOCATION_DATA.getSiegeLocations();
			DAOManager.getDAO(SiegeDAO.class).loadSiegeLocations(locations);
			log.info("[SiegeService] Loaded " + locations.size() + " siege locations");
		}
		else
		{
			artifacts = Collections.emptyMap();
			fortresses = Collections.emptyMap();
			locations = Collections.emptyMap();
			log.info("Sieges are disabled in config.");
		}
	}
	
	/**
	 * Initializes the siege system components.<br>
	 * This method checks if sieges are enabled in {@code SiegeConfig}.<br>
	 * It clears existing NPCs and spawns new ones for fortresses and artifacts.<br>
	 * It loads the {@link SiegeSchedule} and sets up cron jobs via {@link CronService}.<br>
	 * Finally, it schedules periodic status broadcasts to all players.
	 */
	public void initSieges()
	{
		if (!SiegeConfig.SIEGE_ENABLED)
		{
			return;
		}
		
		GameServer.log.info("[SiegeService] started ...");
		
		// despawn all NPCs spawned by spawn engine.
		// Siege spawns should be controlled by siege service
		for (Integer i : getSiegeLocations().keySet())
		{
			deSpawnNpcs(i);
		}
		
		// spawn fortress common npcs
		for (FortressLocation f : getFortresses().values())
		{
			spawnNpcs(f.getLocationId(), f.getRace(), SiegeModType.PEACE);
		}
		
		// spawn artifacts
		for (ArtifactLocation a : getStandaloneArtifacts().values())
		{
			spawnNpcs(a.getLocationId(), a.getRace(), SiegeModType.PEACE);
		}
		
		// initialize siege schedule
		siegeSchedule = SiegeSchedule.load();
		
		// Schedule fortresses sieges protector spawn
		for (SiegeSchedule.Fortress f : siegeSchedule.getFortressesList())
		{
			for (String siegeTime : f.getSiegeTimes())
			{
				CronService.getInstance().schedule(new SiegeStartRunnable(f.getId()), siegeTime);
				log.debug("[SiegeService] Scheduled siege of fortressID " + f.getId() + " based on cron expression: " + siegeTime);
			}
		}
		
		// Start siege of artifacts
		for (ArtifactLocation artifact : artifacts.values())
		{
			if (artifact.isStandAlone())
			{
				log.debug("[SiegeService] Starting siege of artifact #" + artifact.getLocationId());
				startSiege(artifact.getLocationId());
			}
			else
			{
				log.debug("[SiegeService] Artifact #" + artifact.getLocationId() + " siege was not started, it belongs to fortress");
			}
		}
		
		// Set a valid next state for the fortress on startup since there are currently no players on the server and broadcasting is unnecessary.
		updateFortressNextState();
		
		// Schedule siege status broadcast (every hour)
		CronService.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				updateFortressNextState();
				World.getInstance().doOnAllPlayers(new Visitor<Player>()
				{
					
					@Override
					public void visit(Player player)
					{
						for (FortressLocation fortress : getFortresses().values())
						{
							PacketSendUtility.sendPacket(player, new SM_FORTRESS_INFO(fortress.getLocationId(), false));
						}
						
						PacketSendUtility.sendPacket(player, new SM_FORTRESS_STATUS());
						
						for (FortressLocation fortress : getFortresses().values())
						{
							PacketSendUtility.sendPacket(player, new SM_FORTRESS_INFO(fortress.getLocationId(), true));
						}
					}
				});
			}
		}, SIEGE_LOCATION_STATUS_BROADCAST_SCHEDULE);
		log.debug("[SiegeService] Broadcasting Siege Location status based on expression: " + SIEGE_LOCATION_STATUS_BROADCAST_SCHEDULE);
	}
	
	/**
	 * Checks if a siege should begin at the specified location.<br>
	 * This method triggers {@code startSiege} for the given ID.
	 * @param locationId The unique identifier of the siege location to check.
	 */
	public void checkSiegeStart(int locationId)
	{
		startSiege(locationId);
	}
	
	/**
	 * Starts a new siege for the specified location.<br>
	 * This method ensures that only one siege runs per {@code siegeLocationId} at a time.<br>
	 * It schedules an automatic end if the siege is not marked as endless.
	 * @param siegeLocationId The unique identifier of the siege location to start.
	 */
	public void startSiege(int siegeLocationId)
	{
		log.debug("[SiegeService] Starting siege of siege location: " + siegeLocationId);
		
		// Siege should not be started two times. Never.
		Siege<?> siege;
		synchronized (this)
		{
			if (activeSieges.containsKey(siegeLocationId))
			{
				log.error("[SiegeService] Attempt to start siege twice for siege location: " + siegeLocationId);
				return;
			}
			
			siege = newSiege(siegeLocationId);
			activeSieges.put(siegeLocationId, siege);
		}
		
		siege.startSiege();
		
		// Certain sieges are endless and should only end manually upon the siege boss's death.
		if (siege.isEndless())
		{
			return;
		}
		
		// schedule siege end
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				stopSiege(siegeLocationId);
			}
		}, siege.getSiegeLocation().getSiegeDuration() * 1000);
	}
	
	/**
	 * Stops the active siege for a specific location.<br>
	 * This method removes the siege from the active list and calls its stop logic.<br>
	 * It checks if the siege is currently in progress before attempting to stop it.
	 * @param siegeLocationId The unique identifier of the siege location to stop.
	 */
	public void stopSiege(int siegeLocationId)
	{
		log.debug("[SiegeService] Stopping siege of siege location: " + siegeLocationId);
		
		// Just a check here...
		// If the fortress is 99% captured, the siege timer will return here without a concurrent race.
		if (!isSiegeInProgress(siegeLocationId))
		{
			log.debug("[SiegeService] Siege of siege location " + siegeLocationId + " is not in progress, it was captured earlier?");
			return;
		}
		
		// We need synchronization here for that 1% of cases :)
		// The fortress siege might be stopped simultaneously by two different threads—one for killing the boss and one for the schedule—which could result in the siege object being null.
		Siege<?> siege;
		synchronized (this)
		{
			siege = activeSieges.remove(siegeLocationId);
		}
		
		if ((siege == null) || siege.isFinished())
		{
			return;
		}
		
		siege.stopSiege();
	}
	
	/**
	 * Distributes earned abyss points to active fortress sieges.<br>
	 * For each active {@link FortressSiege} where the player is inside the siege location,<br>
	 * the points are added to that siege's counter.
	 * @param player The {@link Player} who earned the points.
	 * @param value The amount of abyss points earned.
	 */
	public void onSiegeApGained(Player player, int value)
	{
		for (Siege<?> siege : activeSieges.values())
		{
			if ((siege instanceof FortressSiege) && siege.getSiegeLocation().isInsideLocation(player))
			{
				siege.addAbyssPoints(player, value);
			}
		}
	}
	
	/**
	 * Distributes earned glory points to active fortress sieges.<br>
	 * For each active {@link FortressSiege} where the player is inside the siege location,<br>
	 * the points are added to that siege's counter.
	 * @param player The {@link Player} who earned the points.
	 * @param value The amount of glory points earned.
	 */
	public void onSiegeGpGained(Player player, int value)
	{
		for (Siege<?> siege : activeSieges.values())
		{
			if ((siege instanceof FortressSiege) && siege.getSiegeLocation().isInsideLocation(player))
			{
				siege.addGloryPoints(player, value);
			}
		}
	}
	
	/**
	 * Updates the next state for all fortress siege locations.<br>
	 * This method calculates whether a fortress should be vulnerable or invulnerable in one hour.<br>
	 * It uses {@link CronService} to determine upcoming siege start times and compares them against current time.
	 */
	protected void updateFortressNextState()
	{
		// get current hour and add 1 hour
		final Calendar currentHourPlus1 = Calendar.getInstance();
		currentHourPlus1.set(Calendar.MINUTE, 0);
		currentHourPlus1.set(Calendar.SECOND, 0);
		currentHourPlus1.set(Calendar.MILLISECOND, 0);
		currentHourPlus1.add(Calendar.HOUR, 1);
		
		// filter fortress siege start runnables
		Map<Runnable, JobDetail> siegeStartRunables = CronService.getInstance().getRunnables();
		final Map<Runnable, JobDetail> filtered = new HashMap<>();
		for (Map.Entry<Runnable, JobDetail> entry : siegeStartRunables.entrySet())
		{
			final Runnable r = entry.getKey();
			if (r instanceof SiegeStartRunnable)
			{
				filtered.put(entry.getKey(), entry.getValue());
			}
		}
		siegeStartRunables = filtered;
		
		// Create map FortressId-To-AllTriggers
		final Map<Integer, List<Trigger>> siegeIdToStartTriggers = new HashMap<>();
		for (Map.Entry<Runnable, JobDetail> entry : siegeStartRunables.entrySet())
		{
			final SiegeStartRunnable fssr = (SiegeStartRunnable) entry.getKey();
			
			List<Trigger> storage = siegeIdToStartTriggers.get(fssr.getLocationId());
			if (storage == null)
			{
				storage = new ArrayList<>();
				siegeIdToStartTriggers.put(fssr.getLocationId(), storage);
			}
			
			storage.addAll(CronService.getInstance().getJobTriggers(entry.getValue()));
		}
		
		// update each fortress next state
		for (Map.Entry<Integer, List<Trigger>> entry : siegeIdToStartTriggers.entrySet())
		{
			final List<Date> nextFireDates = new ArrayList<>(entry.getValue().size());
			for (Trigger trigger : entry.getValue())
			{
				nextFireDates.add(trigger.getNextFireTime());
			}
			
			Collections.sort(nextFireDates);
			
			// clear non-required times
			final Date nextSiegeDate = nextFireDates.get(0);
			final Calendar siegeStartHour = Calendar.getInstance();
			siegeStartHour.setTime(nextSiegeDate);
			siegeStartHour.set(Calendar.MINUTE, 0);
			siegeStartHour.set(Calendar.SECOND, 0);
			siegeStartHour.set(Calendar.MILLISECOND, 0);
			
			// update fortress state that will be valid in 1 h
			final SiegeLocation fortress = getSiegeLocation(entry.getKey());
			
			// check if siege duration is > than 1 Hour
			final Calendar siegeCalendar = Calendar.getInstance();
			siegeCalendar.set(Calendar.MINUTE, 0);
			siegeCalendar.set(Calendar.SECOND, 0);
			siegeCalendar.set(Calendar.MILLISECOND, 0);
			siegeCalendar.add(Calendar.HOUR, 0);
			siegeCalendar.add(Calendar.SECOND, getRemainingSiegeTimeInSeconds(fortress.getLocationId()));
			
			if (fortress instanceof SourceLocation)
			{
				siegeStartHour.add(Calendar.HOUR, 1);
			}
			
			if ((currentHourPlus1.getTimeInMillis() == siegeStartHour.getTimeInMillis()) || (siegeCalendar.getTimeInMillis() > currentHourPlus1.getTimeInMillis()))
			{
				fortress.setNextState(SiegeLocation.STATE_VULNERABLE);
			}
			else
			{
				fortress.setNextState(SiegeLocation.STATE_INVULNERABLE);
			}
		}
	}
	
	/**
	 * Calculates the number of seconds remaining until the current hour ends.<br>
	 * This method uses {@code Calendar} to determine the time.
	 * @return The total number of seconds left in the current hour as an {@code int}.
	 */
	public int getSecondsBeforeHourEnd()
	{
		final Calendar c = Calendar.getInstance();
		final int minutesAsSeconds = c.get(Calendar.MINUTE) * 60;
		final int seconds = c.get(Calendar.SECOND);
		return 3600 - (minutesAsSeconds + seconds);
	}
	
	/**
	 * Calculates the time remaining for a specific siege.<br>
	 * It returns the duration in seconds based on the {@code siegeLocationId}.<br>
	 * If the siege is not active or has finished, it returns 0.<br>
	 * Returns -1 if the siege is configured to be endless.
	 * @param siegeLocationId The unique identifier for the siege location.
	 * @return The number of seconds remaining until the siege ends.
	 */
	public int getRemainingSiegeTimeInSeconds(int siegeLocationId)
	{
		final Siege<?> siege = getSiege(siegeLocationId);
		if ((siege == null) || siege.isFinished())
		{
			return 0;
		}
		
		if (!siege.isStarted())
		{
			return siege.getSiegeLocation().getSiegeDuration();
		}
		
		// endless siege
		if (siege.getSiegeLocation().getSiegeDuration() == -1)
		{
			return -1;
		}
		
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, siege.getSiegeLocation().getSiegeDuration());
		
		final int result = (int) ((calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000);
		return (result > 0) ? result : 0;
	}
	
	/**
	 * Retrieves the {@link Siege} object for a specific location.<br>
	 * This method looks up the siege based on the ID provided in the {@code loc} parameter.
	 * @param loc The {@link SiegeLocation} to search for.
	 * @return The {@link Siege} instance associated with the location, or {@code null} if none exists.
	 */
	public Siege<?> getSiege(SiegeLocation loc)
	{
		return activeSieges.get(loc.getLocationId());
	}
	
	/**
	 * Retrieves a {@link Siege} object based on its unique location ID.<br>
	 * This method looks up the siege data from the internal active sieges map.
	 * @param siegeLocationId The unique identifier for the siege location.
	 * @return The {@link Siege} object associated with the provided ID, or {@code null} if not found.
	 */
	public Siege<?> getSiege(Integer siegeLocationId)
	{
		return activeSieges.get(siegeLocationId);
	}
	
	/**
	 * Checks if a siege is currently active for a specific fortress.<br>
	 * This method looks up the {@code fortressId} in the internal list of active sieges.
	 * @param fortressId The unique identifier of the fortress to check.
	 * @return {@code true} if a siege is happening, {@code false} otherwise.
	 */
	public boolean isSiegeInProgress(int fortressId)
	{
		return activeSieges.containsKey(fortressId);
	}
	
	/**
	 * Retrieves all available fortress locations.<br>
	 * This method returns a map of IDs to {@link FortressLocation} objects.
	 * @return A {@code Map} containing the ID and location data for every fortress.
	 */
	public Map<Integer, FortressLocation> getFortresses()
	{
		return fortresses;
	}
	
	/**
	 * Retrieves a specific fortress based on its unique identifier.<br>
	 * This method looks up the {@code FortressLocation} from the internal map.
	 * @param id The unique integer ID of the fortress to retrieve.
	 * @return The {@link FortressLocation} associated with the provided ID, or {@code null} if not found.
	 */
	public FortressLocation getFortress(int id)
	{
		return fortresses.get(id);
	}
	
	/**
	 * Retrieves all available artifact locations.<br>
	 * This method returns a map where the key is the unique identifier for an artifact.
	 * @return A {@code Map<Integer, ArtifactLocation>} containing all artifacts.
	 */
	public Map<Integer, ArtifactLocation> getArtifacts()
	{
		return artifacts;
	}
	
	/**
	 * Retrieves a specific artifact location based on its unique identifier.<br>
	 * This method looks up the {@code id} in the internal map of artifacts.
	 * @param id The unique identifier for the artifact to retrieve.
	 * @return The {@link ArtifactLocation} associated with the provided {@code id}, or {@code null} if not found.
	 */
	public ArtifactLocation getArtifact(int id)
	{
		return getArtifacts().get(id);
	}
	
	/**
	 * Retrieves a map of all standalone artifacts.<br>
	 * This method filters the available artifacts to include only those that are not tied to specific fortresses.
	 * @return A {@code Map} where the key is the artifact ID and the value is the {@link ArtifactLocation}.
	 */
	public Map<Integer, ArtifactLocation> getStandaloneArtifacts()
	{
		final Map<Integer, ArtifactLocation> result = new HashMap<>();
		for (Map.Entry<Integer, ArtifactLocation> entry : artifacts.entrySet())
		{
			final ArtifactLocation artifact = entry.getValue();
			if ((artifact != null) && artifact.isStandAlone())
			{
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}
	
	/**
	 * Retrieves all artifacts that are owned by a fortress.<br>
	 * This method filters the global artifact list to include only those with an associated {@link FortressLocation}.
	 * @return A map where the key is the artifact ID and the value is the {@link ArtifactLocation}.
	 */
	public Map<Integer, ArtifactLocation> getFortressArtifacts()
	{
		final Map<Integer, ArtifactLocation> result = new HashMap<>();
		for (Map.Entry<Integer, ArtifactLocation> entry : artifacts.entrySet())
		{
			final ArtifactLocation artifact = entry.getValue();
			if ((artifact != null) && (artifact.getOwningFortress() != null))
			{
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}
	
	/**
	 * Retrieves all available siege locations.<br>
	 * This method returns a map where the key is the unique ID and the value is the {@link SiegeLocation}.
	 * @return A map containing all {@link SiegeLocation} objects.
	 */
	public Map<Integer, SiegeLocation> getSiegeLocations()
	{
		return locations;
	}
	
	/**
	 * Retrieves a specific {@link SiegeLocation} by its unique identifier.<br>
	 * This method looks up the location in the internal map using the provided {@code id}.
	 * @param id The unique identification number of the siege location.
	 * @return The {@link SiegeLocation} object associated with the given {@code id}, or {@code null} if not found.
	 */
	public SiegeLocation getSiegeLocation(int id)
	{
		return locations.get(id);
	}
	
	/**
	 * Retrieves all siege locations for a specific world.<br>
	 * This method filters the global list of {@link SiegeLocation} objects.<br>
	 * It returns a map where the key is the location ID.
	 * @param worldId The unique identifier of the world to filter by.
	 * @return A map containing the siege locations for the specified world.
	 */
	public Map<Integer, SiegeLocation> getSiegeLocations(int worldId)
	{
		final Map<Integer, SiegeLocation> mapLocations = new HashMap<>();
		for (SiegeLocation location : getSiegeLocations().values())
		{
			if (location.getWorldId() == worldId)
			{
				mapLocations.put(location.getLocationId(), location);
			}
		}
		
		return mapLocations;
	}
	
	/**
	 * Creates a new {@link Siege} object based on the provided ID.<br>
	 * This method determines if the location is a fortress or an artifact.<br>
	 * It returns the appropriate subclass for that specific siege type.
	 * @param siegeLocationId The unique identifier for the siege location.
	 * @return A new instance of {@link Siege} corresponding to the location.
	 */
	protected Siege<?> newSiege(int siegeLocationId)
	{
		if (fortresses.containsKey(siegeLocationId))
		{
			return new FortressSiege(fortresses.get(siegeLocationId));
		}
		else if (artifacts.containsKey(siegeLocationId))
		{
			return new ArtifactSiege(artifacts.get(siegeLocationId));
		}
		else
		{
			throw new SiegeException("[SiegeService] Unknown siege handler for siege location: " + siegeLocationId);
		}
	}
	
	/**
	 * Resets the {@code legionId} for a specific siege location.<br>
	 * This method searches through all active locations and sets the ID to {@code 0}.
	 * @param legionId The unique identifier of the legion to clear.
	 */
	public void cleanLegionId(int legionId)
	{
		for (SiegeLocation loc : this.getSiegeLocations().values())
		{
			if (loc.getLegionId() == legionId)
			{
				loc.setLegionId(0);
				break;
			}
		}
	}
	
	/**
	 * Spawns NPCs for a specific siege location based on the provided race and type.<br>
	 * This method filters spawn templates from {@code DataManager}.<br>
	 * It also updates nearby quests for all players after spawning.
	 * @param siegeLocationId The unique identifier for the siege location.
	 * @param race The specific race of NPCs to spawn.
	 * @param type The modification type for the spawned NPCs.
	 */
	public void spawnNpcs(int siegeLocationId, SiegeRace race, SiegeModType type)
	{
		final List<SpawnGroup2> siegeSpawns = DataManager.SPAWNS_DATA2.getSiegeSpawnsByLocId(siegeLocationId);
		if (siegeSpawns == null)
		{
			return;
		}
		
		for (SpawnGroup2 group : siegeSpawns)
		{
			for (SpawnTemplate template : group.getSpawnTemplates())
			{
				final SiegeSpawnTemplate siegetemplate = (SiegeSpawnTemplate) template;
				if (siegetemplate.getSiegeRace().equals(race) && siegetemplate.getSiegeModType().equals(type))
				{
					final Npc npc = (Npc) SpawnEngine.spawnObject(siegetemplate, 1);
					if (SiegeConfig.SIEGE_HEALTH_MOD_ENABLED)
					{
						final NpcTemplate templ = npc.getObjectTemplate();
						if (templ.getRating().equals(NpcRating.LEGENDARY))
						{
							final NpcLifeStats life = npc.getLifeStats();
							final int maxHpPercent = (int) (life.getMaxHp() * SiegeConfig.SIEGE_HEALTH_MULTIPLIER);
							templ.getStatsTemplate().setMaxHp(maxHpPercent);
							life.setCurrentHpPercent(100);
						}
					}
				}
			}
		}
		
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				player.getController().updateNearbyQuests();
			}
		});
	}
	
	/**
	 * Removes all NPCs associated with a specific siege location.<br>
	 * This method finds all {@code SiegeNpc} objects in the local area.<br>
	 * It then calls the {@code onDelete()} method on each NPC controller.
	 * @param siegeLocationId The unique identifier for the siege location to clear.
	 */
	public void deSpawnNpcs(int siegeLocationId)
	{
		final Collection<SiegeNpc> siegeNpcs = World.getInstance().getLocalSiegeNpcs(siegeLocationId);
		for (SiegeNpc npc : siegeNpcs)
		{
			npc.getController().onDelete();
		}
	}
	
	/**
	 * Checks if a specific {@link Npc} is currently active in an ongoing siege.<br>
	 * This method verifies if the NPC is a {@code SiegeNpc} and belongs to a vulnerable fortress.<br>
	 * It also checks respawn timing based on the current hour end for certain states.
	 * @param npc The {@link Npc} object to check.
	 * @return {@code true} if the NPC is active in an active siege, otherwise {@code false}.
	 */
	public boolean isSiegeNpcInActiveSiege(Npc npc)
	{
		if (npc instanceof SiegeNpc)
		{
			final FortressLocation fort = getFortress(((SiegeNpc) npc).getSiegeId());
			if (fort != null)
			{
				if (fort.isVulnerable())
				{
					return true;
				}
				else if (fort.getNextState() == 1)
				{
					return npc.getSpawn().getRespawnTime() >= getSecondsBeforeHourEnd();
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Sends a broadcast message to all players.<br>
	 * This method notifies everyone about the current siege location information.
	 */
	public void broadcastUpdate()
	{
		broadcast(new SM_SIEGE_LOCATION_INFO(), null);
	}
	
	/**
	 * Updates the siege location information for all players.<br>
	 * This method recalculates the influence and sends a broadcast packet.
	 * @param loc The {@code SiegeLocation} object to be updated.
	 */
	public void broadcastUpdate(SiegeLocation loc)
	{
		Influence.getInstance().recalculateInfluence();
		broadcast(new SM_SIEGE_LOCATION_INFO(loc), new SM_INFLUENCE_RATIO());
	}
	
	/**
	 * Sends specific packets to all players in the world.<br>
	 * This method also updates fortress buffs for every player.
	 * @param pkt1 The first {@code AionServerPacket} to send, or {@code null}.
	 * @param pkt2 The second {@code AionServerPacket} to send, or {@code null}.
	 */
	public void broadcast(AionServerPacket pkt1, AionServerPacket pkt2)
	{
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				fortressBuffRemove(player);
				fortressBuffApply(player);
				if (pkt1 != null)
				{
					PacketSendUtility.sendPacket(player, pkt1);
				}
				
				if (pkt2 != null)
				{
					PacketSendUtility.sendPacket(player, pkt2);
				}
			}
		});
	}
	
	/**
	 * Sends a broadcast update to all players regarding a specific siege location.<br>
	 * This method creates and sends both a data packet and a system message.
	 * @param loc The {@code SiegeLocation} object containing the siege details.
	 * @param nameId The {@code DescriptionId} used for the display name in the message.
	 */
	public void broadcastUpdate(SiegeLocation loc, DescriptionId nameId)
	{
		final SM_SIEGE_LOCATION_INFO pkt = new SM_SIEGE_LOCATION_INFO(loc);
		final SM_SYSTEM_MESSAGE info = loc.getLegionId() == 0 ? new SM_SYSTEM_MESSAGE(1301039, loc.getRace().getDescriptionId(), nameId) : new SM_SYSTEM_MESSAGE(1301038, LegionService.getInstance().getLegion(loc.getLegionId()).getLegionName(), nameId);
		broadcast(pkt, info, loc.getRace());
	}
	
	/**
	 * Sends specific packets to all players in the world.<br>
	 * It updates fortress buffs for every player.<br>
	 * Only players of a specific race receive the {@code info} packet.
	 * @param pkt The general packet to send to every player.
	 * @param info The special packet to send only to players matching the specified race.
	 * @param race The {@code SiegeRace} used to filter which players receive the {@code info} packet.
	 */
	private void broadcast(AionServerPacket pkt, AionServerPacket info, SiegeRace race)
	{
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				fortressBuffRemove(player);
				fortressBuffApply(player);
				if (player.getRace().getRaceId() == race.getRaceId())
				{
					PacketSendUtility.sendPacket(player, info);
				}
				
				PacketSendUtility.sendPacket(player, pkt);
			}
		});
	}
	
	/**
	 * This method is called when a {@link Player} logs into the instance.<br>
	 * It handles any initialization logic required for the player's session.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void onPlayerLogin(Player player)
	{
		// Do not send the packet on login.
		// PacketSendUtility.sendPacket(player, new SM_ABYSS_ARTIFACT_INFO2(getSiegeLocations().values()));
		
		// Check login status when the teleporter is dead for each fortress location.
		// Remove teleportation to dead teleporters if the location cannot teleport the player.
		// PacketSendUtility.sendPacket(player, new SM_FORTRESS_INFO(loc.getLocationId(), false));
		// }
		// First part will be sent to all
		if (SiegeConfig.SIEGE_ENABLED)
		{
			PacketSendUtility.sendPacket(player, new SM_INFLUENCE_RATIO());
			PacketSendUtility.sendPacket(player, new SM_SIEGE_LOCATION_INFO());
			PacketSendUtility.sendPacket(player, new SM_UNK_127());
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} enters a siege world.<br>
	 * It filters locations and artifacts based on the player's current world ID.<br>
	 * The method then sends specific shield effects and artifact information to the player.
	 * @param player The {@link Player} object who entered the siege world.
	 */
	public void onEnterSiegeWorld(Player player)
	{
		// Second part only for siege world
		final Map<Integer, SiegeLocation> worldLocations = new HashMap<>();
		final Map<Integer, ArtifactLocation> worldArtifacts = new HashMap<>();
		
		for (SiegeLocation location : getSiegeLocations().values())
		{
			if (location.getWorldId() == player.getWorldId())
			{
				worldLocations.put(location.getLocationId(), location);
			}
		}
		
		for (ArtifactLocation artifact : getArtifacts().values())
		{
			if (artifact.getWorldId() == player.getWorldId())
			{
				worldArtifacts.put(artifact.getLocationId(), artifact);
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_SHIELD_EFFECT(worldLocations.values()));
		PacketSendUtility.sendPacket(player, new SM_ABYSS_ARTIFACT_INFO(worldArtifacts.values()));
	}
	
	/**
	 * Applies specific fortress buffs to a {@link Player} based on their world and race.<br>
	 * This method checks if the player is in a siege world.<br>
	 * It grants different effects depending on whether the player matches the required race for a location.
	 * @param player The {@code Player} object to receive the buff.
	 */
	public void fortressBuffApply(Player player)
	{
		if (player.isInSiegeWorld())
		{
			final SiegeLocation location7011 = getSiegeLocation(7011);
			final SiegeLocation location10111 = getSiegeLocation(10111);
			final SiegeLocation location10211 = getSiegeLocation(10211);
			final SiegeLocation location10311 = getSiegeLocation(10311);
			final SiegeLocation location10411 = getSiegeLocation(10411);
			if (player.getWorldId() == 400020000)
			{
				if (location10111.getRace() == SiegeRace.getByRace(player.getRace()))
				{
					// Gab01's Commendation
					SkillEngine.getInstance().applyEffectDirectly(12160, player, player, 0);
				}
				else if ((location10111.getRace() != SiegeRace.getByRace(player.getRace())) && (location10111.getRace() != SiegeRace.BALAUR))
				{
					// Gab01's Encouragement
					SkillEngine.getInstance().applyEffectDirectly(12161, player, player, 0);
				}
			}
			else if (player.getWorldId() == 400040000)
			{
				if (location10211.getRace() == SiegeRace.getByRace(player.getRace()))
				{
					// Gab02's Commendation
					SkillEngine.getInstance().applyEffectDirectly(12162, player, player, 0);
				}
				else if ((location10211.getRace() != SiegeRace.getByRace(player.getRace())) && (location10211.getRace() != SiegeRace.BALAUR))
				{
					// Gab02's Encouragement
					SkillEngine.getInstance().applyEffectDirectly(12163, player, player, 0);
				}
			}
			else if (player.getWorldId() == 400050000)
			{
				if (location10311.getRace() == SiegeRace.getByRace(player.getRace()))
				{
					// Gab03's Commendation
					SkillEngine.getInstance().applyEffectDirectly(12164, player, player, 0);
				}
				else if ((location10311.getRace() != SiegeRace.getByRace(player.getRace())) && (location10311.getRace() != SiegeRace.BALAUR))
				{
					// Gab03's Encouragement
					SkillEngine.getInstance().applyEffectDirectly(12165, player, player, 0);
				}
			}
			else if (player.getWorldId() == 400060000)
			{
				if (location10411.getRace() == SiegeRace.getByRace(player.getRace()))
				{
					// Gab04's Commendation
					SkillEngine.getInstance().applyEffectDirectly(12166, player, player, 0);
				}
				else if ((location10411.getRace() != SiegeRace.getByRace(player.getRace())) && (location10411.getRace() != SiegeRace.BALAUR))
				{
					// Gab04's Encouragement
					SkillEngine.getInstance().applyEffectDirectly(12167, player, player, 0);
				}
			}
			else if (player.getWorldId() == 600090000)
			{
				if (location7011.getRace() == SiegeRace.getByRace(player.getRace()))
				{
					// Kaldor's Commendation
					SkillEngine.getInstance().applyEffectDirectly(12168, player, player, 0);
				}
				else if ((location7011.getRace() != SiegeRace.getByRace(player.getRace())) && (location7011.getRace() != SiegeRace.BALAUR))
				{
					// Kaldor's Encouragement
					SkillEngine.getInstance().applyEffectDirectly(12169, player, player, 0);
				}
			}
		}
	}
	
	/**
	 * Removes specific fortress-related abnormal effects from a player.<br>
	 * This method checks for several effect IDs and removes the first one it finds.
	 * @param player The {@code Player} object to modify.
	 */
	public void fortressBuffRemove(Player player)
	{
		if (player.getEffectController().hasAbnormalEffect(12161))
		{
			player.getEffectController().removeEffect(12161);
		}
		else if (player.getEffectController().hasAbnormalEffect(12162))
		{
			player.getEffectController().removeEffect(12163);
		}
		else if (player.getEffectController().hasAbnormalEffect(12163))
		{
			player.getEffectController().removeEffect(12163);
		}
		else if (player.getEffectController().hasAbnormalEffect(12164))
		{
			player.getEffectController().removeEffect(12164);
		}
		else if (player.getEffectController().hasAbnormalEffect(12165))
		{
			player.getEffectController().removeEffect(12165);
		}
		else if (player.getEffectController().hasAbnormalEffect(12166))
		{
			player.getEffectController().removeEffect(12166);
		}
		else if (player.getEffectController().hasAbnormalEffect(12167))
		{
			player.getEffectController().removeEffect(12167);
		}
		else if (player.getEffectController().hasAbnormalEffect(12168))
		{
			player.getEffectController().removeEffect(12168);
		}
		else if (player.getEffectController().hasAbnormalEffect(12169))
		{
			player.getEffectController().removeEffect(12169);
		}
	}
	
	/**
	 * Retrieves the unique fortress ID associated with a specific location. <br>
	 * This method maps various {@code locId} values to their corresponding fortress IDs.<br>
	 * It returns {@code 0} if the provided location is not mapped to any fortress.
	 * @param locId The unique identifier of the location to check.
	 * @return The integer ID of the fortress, or {@code 0} if no mapping exists.
	 */
	public int getFortressId(int locId)
	{
		switch (locId)
		{
			case 49:
			case 61:
				return 1011; // Divine Fortress
			case 36:
			case 54:
				return 1131; // Siel's Western Fortress
			case 37:
			case 55:
				return 1132; // Siel's Eastern Fortress
			case 39:
			case 56:
				return 1141; // Sulfur Archipelago
			case 45:
			case 57:
			case 72:
			case 75:
				return 1221; // Krotan Refuge
			case 46:
			case 58:
			case 73:
			case 76:
				return 1231; // Kysis Fortress
			case 47:
			case 59:
			case 74:
			case 77:
				return 1241; // Miren Fortress
				
			case 102:
				return 7011; // Anoha Fortress
				
			case 103:
				return 10111; // Belus Fortress
			case 104:
				return 10211; // Aspida Fortress
			case 105:
				return 10311; // Atanaos Fortress
			case 106:
				return 10411; // Disillon Fortress
		}
		
		return 0;
	}
	
	// return RVR Event players list
	/**
	 * Retrieves the list of players currently participating in a Raid vs Raid event.<br>
	 * This method returns the internal collection of {@link Player} objects.
	 * @return A {@code List} of {@link Player} objects involved in the event.
	 */
	public List<Player> getRvrPlayersOnEvent()
	{
		return rvrPlayersOnEvent;
	}
	
	// check if player is in RVR event list, if not the player is added.
	/**
	 * Checks if a {@link Player} is currently participating in an event.<br>
	 * Adds the player to the {@code rvrPlayersOnEvent} list if they are not already present.<br>
	 * This method ensures that only unique players are tracked for the current event.
	 * @param player The {@link Player} object to check and add to the tracking list.
	 */
	public void checkRvrPlayerOnEvent(Player player)
	{
		if ((player != null) && !rvrPlayersOnEvent.contains(player))
		{
			rvrPlayersOnEvent.add(player);
		}
	}
	
	// clear RVR event players list
	/**
	 * Resets the list of players involved in a specific event.<br>
	 * This method initializes {@code rvrPlayersOnEvent} as a new empty {@code ArrayList}.
	 */
	public void clearRvrPlayersOnEvent()
	{
		rvrPlayersOnEvent = new ArrayList<>();
	}
}
