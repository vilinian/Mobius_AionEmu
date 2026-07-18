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
package com.aionemu.gameserver.services.conquerer_protector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.main.ConquerorProtectorConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerConquererProtectorData;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CONQUEROR_PROTECTOR;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * Manages the logic for the conqueror protection system.<br>
 * This service handles player data and provides protections related to {@link PlayerConquererProtectorData}.<br>
 * It ensures that specific rules are applied to players based on their conqueror status.
 * @author Kill3r
 * @modify Elo
 */
public class ConquerorsService
{
	private final Map<Integer, MapTypes> usedWorldMaps = new ConcurrentHashMap<>();
	private final Map<Integer, PlayerConquererProtectorData> players = new ConcurrentHashMap<>();
	private final Map<Integer, Future<?>> pduration = new ConcurrentHashMap<>();
	private final Map<Integer, Future<?>> cduration = new ConcurrentHashMap<>();
	private ProtectorBuff protectorBuff;
	private ConquerorBuff conquerorBuff;
	
	private static final Logger log = LoggerFactory.getLogger(ConquerorsService.class);
	
	public enum MapTypes
	{
		ELYOS,
		ASMODIANS;
	}
	
	/**
	 * Initializes the Conqueror and Protector buff system.<br>
	 * This method checks if {@code ENABLE_GUARDIAN_PVP} is enabled in the configuration.<br>
	 * It populates the {@code usedWorldMaps} collection based on the allowed maps defined in the config.
	 */
	public void initConquerorPvPSystem()
	{
		if (!ConquerorProtectorConfig.ENABLE_GUARDIAN_PVP)
		{
			return;
		}
		
		GameServer.log.info("[ConquerorsService] Initializing Conqueror/Protector Buff System...");
		
		protectorBuff = new ProtectorBuff();
		conquerorBuff = new ConquerorBuff();
		if (!ConquerorProtectorConfig.IGNORE_MAPS)
		{
			for (String worldids : ConquerorProtectorConfig.ENABLED_MAPS_GUARDIAN.split(","))
			{
				if (worldids.equals(""))
				{
					break;
				}
				
				final int worldId = Integer.parseInt(worldids); // world Id from Config
				int type = Integer.parseInt(String.valueOf(worldids.charAt(1))); // 220000000 , second number from worldId , to get Wat Type of World
				
				if (!((type == 1) || (type == 2)))
				{
					type = 3;
					log.info("[ConquerorsService] [CONQUEROR NOTE] Please Verify the Map Id's Given in conqueror.properties!!");
				}
				
				final MapTypes mType = type == 1 ? MapTypes.ELYOS : MapTypes.ASMODIANS;
				usedWorldMaps.put(worldId, mType);
			}
		}
	}
	
	/**
	 * Checks if a specific world map is designated for Conqueror PvP.<br>
	 * This method returns {@code true} if the map is in the active list or if maps are ignored in the config.
	 * @param worldid The unique identifier of the world map to check.
	 * @return {@code true} if the map is a Conqueror PvP map, otherwise {@code false}.
	 */
	public boolean isOnConquerorPvPMap(int worldid)
	{
		if (ConquerorProtectorConfig.IGNORE_MAPS)
		{
			return true;
		}
		
		return usedWorldMaps.containsKey(worldid);
	}
	
	/**
	 * Updates the kill count for a specific {@link Player}.<br>
	 * This method checks if the player is on an enemy map to determine which counter to update.
	 * @param player The {@code Player} whose kills are being updated.
	 * @param kills The number of kills to set.
	 */
	public void setKills(Player player, int kills)
	{
		if (isOnEnemyMap(player))
		{
			player.getConquerorProtectorData().setKillCountAsConquerer(kills);
		}
		else
		{
			player.getConquerorProtectorData().setKillCountAsProtector(kills);
		}
	}
	
	/**
	 * Checks for nearby enemy players on a conqueror map.<br>
	 * It identifies intruders based on race and buff levels.<br>
	 * The method sends an {@code SM_CONQUEROR_PROTECTOR} packet to the player.
	 * @param player The {@link Player} object to perform the scan for.
	 */
	public void scanForIntruders(Player player)
	{
		if (!isOnConquerorPvPMap(player.getWorldId()))
		{
			return;
		}
		
		final int protectorLevel = player.getConquerorProtectorData().getProtectorBuffLevel();
		final Collection<Player> players = new ArrayList<>();
		final Iterator<Player> ita = World.getInstance().getPlayersIterator();
		while (ita.hasNext())
		{
			final Player p1 = ita.next();
			if ((player.getWorldId() == p1.getWorldId()) && (player.getRace() != p1.getRace()) && (protectorLevel >= p1.getConquerorProtectorData().getConquerorBuffLevel()) && (MathUtil.getDistance(player, p1) <= 500))
			{
				players.add(p1);
			}
		}
		
		msgLog("Sending SM_SERIAL_KILLER with " + players.size() + " Players");
		PacketSendUtility.sendPacket(player, new SM_CONQUEROR_PROTECTOR(players, false));
		PacketSendUtility.sendPacket(player, new SM_CONQUEROR_PROTECTOR(players, true));
	}
	
	/**
	 * Retrieves the current kill count for a specific {@link Player}.<br>
	 * It checks if the player is currently acting as a protector.<br>
	 * If they are, it returns their protector kills.<br>
	 * Otherwise, it returns their conqueror kills.
	 * @param player The {@code Player} object to check.
	 * @return The number of kills for the player's current role.
	 */
	public int getKills(Player player)
	{
		final PlayerConquererProtectorData pcdd = player.getConquerorProtectorData();
		
		// msgLog("GetKills for "+player.getName()+": KillsAsProtector: "+pcdd.getKillCountAsProtector()+" KillsAsConquerer: "+pcdd.getKillCountAsConquerer()+" isProtector: "+pcdd.isProtector());
		if (pcdd.isProtector())
		{
			return pcdd.getKillCountAsProtector();
		}
		
		return pcdd.getKillCountAsConquerer();
	}
	
	/**
	 * Adds a specific number of kills to the {@link Player}.<br>
	 * This method updates either the protector or conqueror kill count based on the player's current map.<br>
	 * It logs the change in the total kill count for the player.
	 * @param player The {@code Player} whose kill count will be updated.
	 * @param kills The number of kills to add to the player's total.
	 */
	public void addKills(Player player, int kills)
	{
		final PlayerConquererProtectorData pcdd = player.getConquerorProtectorData();
		final int oldKills = getKills(player);
		if (isOnEnemyMap(player))
		{
			pcdd.setKillCountAsConquerer(pcdd.getKillCountAsConquerer() + kills);
		}
		else
		{
			pcdd.setKillCountAsProtector(pcdd.getKillCountAsProtector() + kills);
		}
		
		final int newKills = getKills(player);
		msgLog((oldKills > newKills ? "De" : "In") + "creased Kills for " + player.getName() + " from: " + oldKills + " to: " + newKills);
	}
	
	/**
	 * Checks if the player is currently on an enemy map.<br>
	 * This method determines if a {@link Player} is in a territory belonging to the opposing faction.<br>
	 * It considers both configuration settings and current world map data.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player is on an enemy map, {@code false} otherwise.
	 */
	public boolean isOnEnemyMap(Player player)
	{
		if (ConquerorProtectorConfig.IGNORE_MAPS)
		{
			final String worldidsAsString = String.valueOf(player.getWorldId());
			int type = worldidsAsString.charAt(1);
			
			if (!((type == 1) || (type == 2)))
			{
				type = 3;
				log.info("[ConquerorsService] [CONQUEROR NOTE] Please Verify the Map Id's Given in conqueror.properties OR In an Instance?");
			}
			
			final MapTypes mType = type == 1 ? MapTypes.ELYOS : MapTypes.ASMODIANS;
			final MapTypes tt = player.getRace().equals(Race.ASMODIANS) ? MapTypes.ASMODIANS : MapTypes.ELYOS;
			
			return !mType.equals(tt);
		}
		
		if (usedWorldMaps.containsKey(player.getWorldId()))
		{
			final MapTypes mType = player.getRace().equals(Race.ASMODIANS) ? MapTypes.ASMODIANS : MapTypes.ELYOS;
			return !usedWorldMaps.get(player.getWorldId()).equals(mType);
		}
		
		return false;
	}
	
	/**
	 * Handles the logic when a player kills another player on a PvP map.<br>
	 * It updates kill counts and manages buff levels for both players.<br>
	 * This method also schedules countdowns and logs the event details.
	 * @param player The {@code Player} who performed the kill.
	 * @param diedPlayer The {@code Player} who was killed.
	 */
	public void onKill(Player player, Player diedPlayer)
	{
		if (!isOnConquerorPvPMap(player.getWorldId()))
		{
			return;
		}
		
		final PlayerConquererProtectorData pcdd_killer = player.getConquerorProtectorData();
		
		// Add new player if he's not in players and give first buff lvl
		if (!players.containsKey(player.getObjectId()))
		{
			players.put(player.getObjectId(), pcdd_killer);
			msgLog("Added New Player : " + player.getName() + " to playerList of PvP.");
			msgLog(player.getName() + "'s Buff Lvl's " + players.get(player.getObjectId()) + " {ProtectorBuffLvl = ConquerorBuffLvl}.");
		}
		
		msgLog("Current Kill Count : " + getKills(player));
		addKills(player, 1);
		msgLog("New Kill Count : " + getKills(player));
		
		// check if player got enough kills for next bufflevel and update it in case.
		checkKillCountAndUpdateBuffLvls(player);
		
		// set timer kiilDownCount
		sheduleCountDownKills(player);
		
		// only for Msg
		checkIfTargetIsHighestRankIntruder(player, diedPlayer);
		
		if (getKills(diedPlayer) > 0)
		{
			addKills(diedPlayer, -1);
			checkKillCountAndUpdateBuffLvls(diedPlayer);
			sheduleCountDownKills(diedPlayer);
		}
	}
	
	/**
	 * Updates the status of a player's conqueror or protector tag for all nearby players.<br>
	 * This method identifies if the {@code Player} is a protector or a conqueror.<br>
	 * It then sends the appropriate {@link SM_CONQUEROR_PROTECTOR} packet to everyone else in the same map instance.
	 * @param player The {@code Player} whose status needs to be broadcasted.
	 */
	public void updateTagPacketToNearby(Player player) // NEED TO RE-CHECK
	{
		final PlayerConquererProtectorData pcdd = player.getConquerorProtectorData();
		World.getInstance().getWorldMap(player.getWorldId()).getWorldMapInstanceById(player.getInstanceId()).doOnAllPlayers(p ->
		{
			if (player != p)
			{
				if (pcdd.isProtector())
				{ // Protector
					PacketSendUtility.sendPacket(p, new SM_CONQUEROR_PROTECTOR(player, true, true, pcdd.getProtectorBuffLevel()));
				}
				else
				{ // Conqueror
					PacketSendUtility.sendPacket(p, new SM_CONQUEROR_PROTECTOR(player, false, true, pcdd.getConquerorBuffLevel()));
				}
			}
		});
	}
	
	/**
	 * Sends a system message to all players on the same map as {@code player}.<br>
	 * This method notifies everyone when a specific player kills an intruder.<br>
	 * It checks the race of {@code diedPlayer} to determine the correct message ID.
	 * @param player The player who performed the kill.
	 * @param diedPlayer The player who was killed.
	 */
	public void sendPacketToEveryoneInMap(Player player, Player diedPlayer)
	{
		final Iterator<Player> ita = World.getInstance().getPlayersIterator();
		while (ita.hasNext())
		{
			final Player p1 = ita.next();
			if (player.getWorldId() == p1.getWorldId())
			{
				if ((diedPlayer.getRace() == Race.ELYOS) && !isOnEnemyMap(player))
				{
					// Hero of Asmodian %0 killed the Divinely Punished Intruder %1.
					PacketSendUtility.sendPacket(p1, new SM_SYSTEM_MESSAGE(1400141, player.getName(), diedPlayer.getName()));
				}
				else if ((diedPlayer.getRace() == Race.ASMODIANS) && !isOnEnemyMap(player))
				{
					// Hero of Elyos %0 killed the Divinely Punished Intruder %1.
					PacketSendUtility.sendPacket(p1, new SM_SYSTEM_MESSAGE(1400142, player.getName(), diedPlayer.getName()));
				}
			}
		}
	}
	
	/**
	 * Checks if the killed player has a high rank buff.<br>
	 * It verifies if either the {@code ProtectorBuffLevel} or {@code ConquerorBuffLevel} is equal to 3.<br>
	 * If true, it triggers a notification to everyone on the map.
	 * @param player The player who performed the kill.
	 * @param diedPlayer The player who was killed.
	 */
	public void checkIfTargetIsHighestRankIntruder(Player player, Player diedPlayer)
	{
		if ((diedPlayer.getConquerorProtectorData().getProtectorBuffLevel() == 3) || (diedPlayer.getConquerorProtectorData().getConquerorBuffLevel() == 3))
		{
			sendPacketToEveryoneInMap(player, diedPlayer);
		}
	}
	
	/**
	 * Checks the current kill count of a {@link Player}.<br>
	 * Updates the buff levels based on the {@code ConquerorProtectorConfig} thresholds.<br>
	 * This method determines if the player is a protector or conqueror and applies the correct level.<br>
	 * It calls {@code int, int)} only if the buff level has changed.
	 * @param player The {@link Player} whose kill count and buffs need to be updated.
	 */
	public void checkKillCountAndUpdateBuffLvls(Player player)
	{
		final PlayerConquererProtectorData pcdd = player.getConquerorProtectorData();
		final int kills = getKills(player);
		final boolean isProtector = pcdd.isProtector();
		int buffLevel = 0;
		final int oldBuffLevel = isProtector ? pcdd.getProtectorBuffLevel() : pcdd.getConquerorBuffLevel();
		
		if (isProtector)
		{
			if (((kills >= ConquerorProtectorConfig.PROTECTOR_LVL1_KILLCOUNT) && (kills < ConquerorProtectorConfig.PROTECTOR_LVL2_KILLCOUNT)))
			{
				buffLevel = 1; // sets Protector lvl 1 buff
			}
			else if (((kills >= ConquerorProtectorConfig.PROTECTOR_LVL2_KILLCOUNT) && (kills < ConquerorProtectorConfig.PROTECTOR_LVL3_KILLCOUNT)))
			{
				buffLevel = 2; // sets Protector lvl 2 Buff
			}
			else if (kills >= ConquerorProtectorConfig.PROTECTOR_LVL3_KILLCOUNT)
			{
				buffLevel = 3; // sets Protector lvl 3 Buff
			}
		}
		else
		{
			if (((kills >= ConquerorProtectorConfig.CONQUEROR_LVL1_KILLCOUNT) && (kills < ConquerorProtectorConfig.CONQUEROR_LVL2_KILLCOUNT)))
			{
				buffLevel = 1; // sets Conquerur lvl 1 buff
			}
			else if (((kills >= ConquerorProtectorConfig.CONQUEROR_LVL2_KILLCOUNT) && (kills < ConquerorProtectorConfig.CONQUEROR_LVL3_KILLCOUNT)))
			{
				buffLevel = 2; // sets Conquerur lvl 2 buff
			}
			else if (kills >= ConquerorProtectorConfig.CONQUEROR_LVL3_KILLCOUNT)
			{
				buffLevel = 3; // sets Conquerur lvl 3 buff
			}
		}
		
		if (buffLevel != oldBuffLevel)
		{
			int protectorBuffLevel = 0;
			int conquerorBuffLevel = 0;
			
			if (isProtector)
			{
				protectorBuffLevel = buffLevel;
				conquerorBuffLevel = pcdd.getConquerorBuffLevel();
			}
			else
			{
				conquerorBuffLevel = buffLevel;
				protectorBuffLevel = pcdd.getProtectorBuffLevel();
			}
			
			msgLog("---------------------------------- [ setBuffLevels ] -----------------------------------");
			msgLog("Player : " + player.getName() + " Setting ProtectorBuffLvl : " + protectorBuffLevel + " , Setting ConquerorBuffLvl : " + conquerorBuffLevel);
			msgLog("Player : " + player.getName() + " KILL COUNT : " + getKills(player) + " on Enemy Map : " + isOnEnemyMap(player) + " is Protector: " + pcdd.isProtector());
			msgLog("---------------------------------- [ endBuffLevels ] -----------------------------------");
			setBuffLvls(player, protectorBuffLevel, conquerorBuffLevel);
		}
	}
	
	/**
	 * Handles the cleanup of data when a {@code Player} logs out.<br>
	 * This method checks if {@code ConquerorProtectorConfig.ENABLE_GUARDIAN_PVP} is enabled.<br>
	 * It removes the player from the active list unless they have an ongoing duration.
	 * @param player The {@code Player} object who is logging out.
	 */
	public void onLogOut(Player player)
	{
		if (!ConquerorProtectorConfig.ENABLE_GUARDIAN_PVP)
		{
			return;
		}
		
		if (players.containsKey(player.getObjectId()))
		{
			players.remove(player.getObjectId());
		}
		
		if (pduration.containsKey(player.getObjectId()) || cduration.containsKey(player.getObjectId()))
		{
			players.put(player.getObjectId(), player.getConquerorProtectorData());
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} enters a world.<br>
	 * It restores saved data if the player was previously tracked.<br>
	 * This method updates buff levels and sends the current status to the player.
	 * @param player The {@link Player} object that entered the world.
	 */
	public void onEnterWorld(Player player)
	{
		if (!ConquerorProtectorConfig.ENABLE_GUARDIAN_PVP)
		{
			return;
		}
		
		// if already in list - update players data with the saved one.
		if (players.containsKey(player.getObjectId()))
		{
			final PlayerConquererProtectorData savedData = players.get(player.getObjectId());
			msgLog("Found saved Data for Player : " + player.getName() + "Kills: " + getKills(player) + " ConquererBuffLevel: " + savedData.getConquerorBuffLevel() + " ProtectorBuffLevel: " + savedData.getProtectorBuffLevel());
			player.setConquerorDefenderData(savedData);
		}
		
		final PlayerConquererProtectorData pcdd = player.getConquerorProtectorData();
		checkKillCountAndUpdateBuffLvls(player);
		
		sendPacket(player, pcdd.getProtectorBuffLevel(), pcdd.getConquerorBuffLevel());
		msgLog("==You're Last Activity [" + player.getName() + "]==");
		msgLog("Kills : " + getKills(player));
		msgLog("Buffs Protector : " + pcdd.getProtectorBuffLevel());
		msgLog("Buffs Conqueror : " + pcdd.getConquerorBuffLevel());
	}
	
	/**
	 * Schedules a recurring task to decrease the kill count of a {@code Player}.<br>
	 * This method manages the countdown for both Protector and Conqueror buffs.<br>
	 * It cancels any existing timers before starting a new one based on the current buff level.<br>
	 * The task automatically updates the player's status via {@code checkKillCountAndUpdateBuffLvls}.
	 * @param player The {@code Player} whose kill count will be managed.
	 */
	public void sheduleCountDownKills(Player player)
	{
		Future<?> timer = null;
		int time = 0;
		final PlayerConquererProtectorData pcdd = player.getConquerorProtectorData();
		
		if (pcdd.isProtector() && pduration.containsKey(player.getObjectId()))
		{
			timer = pduration.get(player.getObjectId());
			pduration.remove(player.getObjectId());
		}
		else if (cduration.containsKey(player.getObjectId()))
		{
			timer = cduration.get(player.getObjectId());
			cduration.remove(player.getObjectId());
		}
		
		if (pcdd.isProtector())
		{
			switch (pcdd.getProtectorBuffLevel())
			{
				case 1:
					time = ConquerorProtectorConfig.DURATION_PBUFF1;
					break;
				case 2:
					time = ConquerorProtectorConfig.DURATION_PBUFF2;
					break;
				case 3:
					time = ConquerorProtectorConfig.DURATION_PBUFF3;
					break;
			}
		}
		else
		{
			switch (pcdd.getConquerorBuffLevel())
			{
				case 1:
					time = ConquerorProtectorConfig.DURATION_CBUFF1;
					break;
				case 2:
					time = ConquerorProtectorConfig.DURATION_CBUFF2;
					break;
				case 3:
					time = ConquerorProtectorConfig.DURATION_CBUFF3;
					break;
			}
		}
		
		if (timer != null)
		{
			timer.cancel(true);
		}
		
		timer = ThreadPoolManager.getInstance().schedule(() ->
		{
			if (getKills(player) <= 0)
			{
				return;
			}
			
			// Decrease kills by one
			if (getKills(player) > 1)
			{
				msgLog("Runnable : decreasing " + player.getName() + "'s KillCount by 1");
				addKills(player, -1);
			}
			else if (getKills(player) == 1)
			{
				addKills(player, -1);
				
				// Kills should be 0 now .. so we can remove te player from players list ...
				msgLog("Removed Player: " + player.getName() + " from PVPList... because KillCount is : " + getKills(player));
				players.remove(player.getObjectId());
			}
			
			// Call CheckRoutine
			checkKillCountAndUpdateBuffLvls(player);
			
			// Shedule next downCount
			sheduleCountDownKills(player);
		}, time * 1000 * 60); // Need to find retail time , for how long it takes for kill countdown each bufflevel ...
		
		if (pcdd.isProtector())
		{
			pduration.put(player.getObjectId(), timer);
		}
		else
		{
			cduration.put(player.getObjectId(), timer);
		}
	}
	
	/**
	 * Updates the buff levels for a specific player.<br>
	 * This method sets both the {@code ProtectorBuffLvl} and {@code ConquerorBuffLvl}.<br>
	 * It also triggers an update to the player's tags and logs the change.
	 * @param player The {@link Player} object to modify.
	 * @param ProtectorBuffLvl The new level for the protector buff.
	 * @param ConquerorBuffLvl The new level for the conqueror buff.
	 */
	public void setBuffLvls(Player player, int ProtectorBuffLvl, int ConquerorBuffLvl)
	{
		final PlayerConquererProtectorData pcdd = player.getConquerorProtectorData();
		pcdd.setConquerorBuffId(ConquerorBuffLvl);
		pcdd.setProtectorBuffId(ProtectorBuffLvl);
		updateBuffLvls(player);
		updateTagPacketToNearby(player);
		msgLog("Set Player " + player.getName() + "'s Buffs to Protector Lvl: " + pcdd.getProtectorBuffLevel() + " | Conquerors Lvl: " + pcdd.getConquerorBuffLevel());
	}
	
	/**
	 * Updates the buff levels for a specific player.<br>
	 * This method retrieves data from {@link PlayerConquererProtectorData}.<br>
	 * It applies effects using {@link ProtectorBuff} and {@link ConquerorBuff}.<br>
	 * Finally, it updates the visual stats of the {@code player}.
	 * @param player The {@code Player} object to update.
	 */
	public void updateBuffLvls(Player player)
	{
		final PlayerConquererProtectorData pcdd = player.getConquerorProtectorData();
		sendPacket(player, pcdd.getProtectorBuffLevel(), pcdd.getConquerorBuffLevel());
		protectorBuff.applyEffect(player, pcdd.getProtectorBuffLevel());
		conquerorBuff.applyEffect(player, pcdd.getConquerorBuffLevel());
		
		msgLog("Updated Player " + player.getName() + "'s Buffs to Protector Lvl: " + pcdd.getProtectorBuffLevel() + " | Conquerors Lvl: " + pcdd.getConquerorBuffLevel());
		player.getGameStats().updateStatsAndSpeedVisually();
	}
	
	/**
	 * Sends the {@code SM_CONQUEROR_PROTECTOR} packet to a specific player.<br>
	 * This method updates the visual status for both Conqueror and Protector roles.
	 * @param player The {@link Player} who will receive the network packet.
	 * @param ProtectorLvl The level of the Protector buff to include in the packet.
	 * @param ConquerorLvl The level of the Conqueror buff to include in the packet.
	 */
	public void sendPacket(Player player, int ProtectorLvl, int ConquerorLvl)
	{
		// Packet for the Conqueror
		PacketSendUtility.sendPacket(player, new SM_CONQUEROR_PROTECTOR(player, false, false, ConquerorLvl));
		
		// Packet for the Protector
		PacketSendUtility.sendPacket(player, new SM_CONQUEROR_PROTECTOR(player, true, false, ProtectorLvl));
	}
	
	/**
	 * Logs a message to the server console.<br>
	 * This method only executes if {@code ENABLE_CONQUEROR_DEBUGMODE} is set to {@code true}.
	 * @param msg The text to be logged.
	 */
	public void msgLog(String msg)
	{
		if (ConquerorProtectorConfig.ENABLE_CONQUEROR_DEBUGMODE)
		{
			log.info("[ConquerorsService] " + msg);
		}
	}
	
	/**
	 * Sends a standard log message to the server console.<br>
	 * This method uses the {@code Logger} to record information.
	 * @param msg The text content to be logged.
	 */
	public void sendNormalLogMsg(String msg)
	{
		log.info("[ConquerorsService] " + msg);
	}
	
	/**
	 * Retrieves the singleton instance of the {@link ConquerorsService}.<br>
	 * This method provides a global access point to the service.
	 * @return The active {@code ConquerorsService} instance.
	 */
	public static ConquerorsService getInstance()
	{
		return ConquerorsService.SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ConquerorsService instance = new ConquerorsService();
	}
	
}
