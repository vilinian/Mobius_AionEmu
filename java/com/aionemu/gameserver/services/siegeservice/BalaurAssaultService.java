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
package com.aionemu.gameserver.services.siegeservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.assemblednpc.AssembledNpc;
import com.aionemu.gameserver.model.assemblednpc.AssembledNpcPart;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.Influence;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.assemblednpc.AssembledNpcTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_ASSEMBLER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Manages the logic for the Balaur Assault siege event.<br>
 * This service handles NPC spawning, artifact locations, and progression mechanics during the assault.
 * @author synchro2
 * @reworked Luzien TODO: Send Peace Dredgion without assault TODO: Artifact Siege
 */
public class BalaurAssaultService
{
	private static final BalaurAssaultService instance = new BalaurAssaultService();
	private final Logger log = LoggerFactory.getLogger("SIEGE_LOG");
	private final Map<Integer, FortressAssault> fortressAssaults = new ConcurrentHashMap<>();
	// private final Map<Integer, ArtifactAssault> artifactAssaults = new ConcurrentHashMap<>();
	
	/**
	 * Provides the singleton instance of the {@link BalaurAssaultService}.<br>
	 * Use this method to access the global service for managing Balaur assaults.
	 * @return The single shared instance of {@code BalaurAssaultService}.
	 */
	public static BalaurAssaultService getInstance()
	{
		return instance;
	}
	
	/**
	 * Handles the logic for starting a Balaur assault when a siege begins.<br>
	 * This method checks if the {@code siege} is a fortress or artifact type.<br>
	 * It determines if an assault should occur based on specific calculations.<br>
	 * If successful, it schedules the assault and sends system messages to players.
	 * @param siege The {@link Siege} object representing the current siege event.
	 */
	public void onSiegeStart(Siege<?> siege)
	{
		if (siege instanceof FortressSiege)
		{
			if (!calculateFortressAssault(((FortressSiege) siege).getSiegeLocation()))
			{
				return;
			}
			
			World.getInstance().doOnAllPlayers(new Visitor<Player>()
			{
				@Override
				public void visit(Player player)
				{
					// The Balaur have destroyed the Castle Gate
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_FIELDABYSS_DRAGON_DOOR_BROKEN, 600000);
					
					// The Balaur have destroyed the Gate Guardian Stone
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_FIELDABYSS_DRAGON_REPAIR_BROKEN, 1500000);
					
					// The Balaur have destroyed the Aetheric Field Activation Stone
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_FIELDABYSS_DRAGON_SHIELD_BROKEN, 2100000);
				}
			});
		}
		else if (siege instanceof ArtifactSiege)
		{
			if (!calculateArtifactAssault(((ArtifactSiege) siege).getSiegeLocation()))
			{
				return;
			}
		}
		else
		{
			return;
		}
		
		newAssault(siege, Rnd.get(1, 600));
		if (LoggingConfig.LOG_SIEGE)
		{
			log.info("[SIEGE] Balaur Assault scheduled on Siege ID: " + siege.getSiegeLocationId() + "!");
		}
	}
	
	/**
	 * Handles the logic when a {@code Siege<?>} event ends.<br>
	 * It checks if the location is part of an active fortress assault.<br>
	 * If so, it updates the assault status and logs the result based on whether the boss was killed.<br>
	 * The specific assault data is then removed from the internal tracking map.
	 * @param siege The {@code Siege<?>} object representing the finished event.
	 */
	public void onSiegeFinish(Siege<?> siege)
	{
		final int locId = siege.getSiegeLocationId();
		if (fortressAssaults.containsKey(locId))
		{
			final Boolean bossIsKilled = siege.isBossKilled();
			fortressAssaults.get(locId).finishAssault(bossIsKilled);
			if (bossIsKilled && siege.getSiegeLocation().getRace().equals(SiegeRace.BALAUR))
			{
				log.info("[SIEGE] > [FORTRESS:" + siege.getSiegeLocationId() + "] has been captured by Balaur Assault!");
			}
			else
			{
				log.info("[SIEGE] > [FORTRESS:" + siege.getSiegeLocationId() + "] Balaur Assault finished without capture!");
			}
			
			fortressAssaults.remove(locId);
		}
	}
	
	/**
	 * Determines if a new Balaur assault can be started at a specific location.<br>
	 * This method checks the influence of the fortress and the current number of active assaults.<br>
	 * It ensures that limits are respected based on whether the map is in Balaurea or not.
	 * @param fortress The {@link FortressLocation} to check for assault eligibility.
	 * @return {@code true} if a new assault can be started, otherwise {@code false}.
	 */
	private boolean calculateFortressAssault(FortressLocation fortress)
	{
		final boolean isBalaurea = fortress.getWorldId() != 400010000;
		final int locationId = fortress.getLocationId();
		
		if (fortressAssaults.containsKey(locationId) || !calcFortressInfluence(isBalaurea, fortress))
		{
			return false;
		}
		
		int count = 0; // Allow only 2 Balaur attacks per map, 1 per Balaurea map
		for (FortressAssault fa : fortressAssaults.values())
		{
			if (fa.getWorldId() == fortress.getWorldId())
			{
				count++;
			}
		}
		
		return count < (isBalaurea ? 1 : 2);
	}
	
	/**
	 * Determines if an assault should occur at a specific artifact location.<br>
	 * This method currently returns {@code false} as it is not yet implemented.
	 * @param artifact The {@link ArtifactLocation} to check for assault status.
	 * @return {@code true} if the assault is valid, otherwise {@code false}.
	 */
	private boolean calculateArtifactAssault(ArtifactLocation artifact)
	{
		// TODO
		return false;
	}
	
	/**
	 * Starts a new assault for the specified location.<br>
	 * This method checks if an assault is already active before starting.<br>
	 * It sends a message to the {@code Player} if the location is busy.
	 * @param player The {@link Player} who initiated the action.
	 * @param location The unique identifier for the siege location.
	 * @param delay The time in milliseconds to wait before starting the assault.
	 */
	public void startAssault(Player player, int location, int delay)
	{
		if (fortressAssaults.containsKey(location) /* || artifactAssaults.containsKey(location) */)
		{
			PacketSendUtility.sendMessage(player, "Assault on " + location + " was already started");
			return;
		}
		
		newAssault(SiegeService.getInstance().getSiege(location), delay);
	}
	
	/**
	 * Initializes and starts a new assault based on the siege type.<br>
	 * It checks if the {@code siege} is a {@link FortressSiege} or an {@link ArtifactSiege}.<br>
	 * The corresponding assault logic is then executed with the provided {@code delay}.
	 * @param siege The current {@link Siege} object to process.
	 * @param delay The time in milliseconds to wait before starting the assault.
	 */
	private void newAssault(Siege<?> siege, int delay)
	{
		if (siege instanceof FortressSiege)
		{
			final FortressAssault assault = new FortressAssault((FortressSiege) siege);
			assault.startAssault(delay);
			fortressAssaults.put(siege.getSiegeLocationId(), assault);
		}
		else if (siege instanceof ArtifactSiege)
		{
			final ArtifactAssault assault = new ArtifactAssault((ArtifactSiege) siege);
			assault.startAssault(delay);
		}
	}
	
	/**
	 * Determines if a fortress assault should occur based on current influence.<br>
	 * This method checks the race of the location and calculates an influence value.<br>
	 * It uses {@code BALAUR_ASSAULT_RATE} to decide the final outcome.
	 * @param isBalaurea A boolean indicating if the current context is Balaurea.
	 * @param fortress The {@code FortressLocation} object being checked for assault.
	 * @return {@code true} if the random roll succeeds, {@code false} otherwise.
	 */
	private boolean calcFortressInfluence(boolean isBalaurea, FortressLocation fortress)
	{
		final SiegeRace locationRace = fortress.getRace();
		float influence;
		
		if (locationRace.equals(SiegeRace.BALAUR) || !fortress.isVulnerable())
		{
			return false;
		}
		
		int ownedForts = 0;
		if (isBalaurea)
		{
			for (FortressLocation fl : SiegeService.getInstance().getFortresses().values())
			{
				if ((fl.getWorldId() != 400010000) && !fortressAssaults.containsKey(fl.getLocationId()) && fl.getRace().equals(locationRace))
				{
					ownedForts++;
				}
			}
			
			influence = ownedForts >= 2 ? 0.25f : 0.1f;
		}
		else
		{
			influence = locationRace.equals(SiegeRace.ASMODIANS) ? Influence.getInstance().getGlobalAsmodiansInfluence() : Influence.getInstance().getGlobalElyosInfluence();
		}
		
		return Rnd.get() < (influence * SiegeConfig.BALAUR_ASSAULT_RATE);
	}
	
	/**
	 * Spawns a new Dredgion NPC into the game world.<br>
	 * This method uses the provided {@code spawnId} to load the correct template.<br>
	 * It notifies all online players about the new appearance.
	 * @param spawnId The unique identifier for the Dredgion template.
	 */
	public void spawnDredgion(int spawnId)
	{
		final AssembledNpcTemplate template = DataManager.ASSEMBLED_NPC_DATA.getAssembledNpcTemplate(spawnId);
		final List<AssembledNpcPart> assembledParts = new ArrayList<>();
		for (AssembledNpcTemplate.AssembledNpcPartTemplate npcPart : template.getAssembledNpcPartTemplates())
		{
			assembledParts.add(new AssembledNpcPart(IDFactory.getInstance().nextId(), npcPart));
		}
		
		final AssembledNpc npc = new AssembledNpc(template.getRouteId(), template.getMapId(), template.getLiveTime(), assembledParts);
		final Iterator<Player> iter = World.getInstance().getPlayersIterator();
		Player findedPlayer;
		while (iter.hasNext())
		{
			findedPlayer = iter.next();
			PacketSendUtility.sendPacket(findedPlayer, new SM_NPC_ASSEMBLER(npc));
			
			// A dredgion has appeared
			PacketSendUtility.sendPacket(findedPlayer, SM_SYSTEM_MESSAGE.STR_ABYSS_CARRIER_SPAWN);
		}
	}
}
