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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.BaseService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Handles the logic for artifact-based siege events.<br>
 * This class manages specific mechanics related to {@link ArtifactLocation} objectives.
 * @author SoulKeeper
 */
public class ArtifactSiege extends Siege<ArtifactLocation>
{
	private static final Logger log = LoggerFactory.getLogger(ArtifactSiege.class.getName());
	
	/**
	 * Creates a new instance of an {@link ArtifactSiege}.<br>
	 * This constructor initializes the siege with a specific location.
	 * @param siegeLocation The {@code ArtifactLocation} where the siege takes place.
	 */
	public ArtifactSiege(ArtifactLocation siegeLocation)
	{
		super(siegeLocation);
	}
	
	/**
	 * This method is called when a siege begins.<br>
	 * It initializes the {@code SiegeBoss} for the current event.
	 */
	@Override
	protected void onSiegeStart()
	{
		initSiegeBoss();
	}
	
	/**
	 * Handles the cleanup and finalization logic when a siege ends.<br>
	 * This method removes listeners, despawns NPCs, and updates the database.<br>
	 * It also triggers {@code onCapture} if the boss was defeated.
	 */
	@Override
	protected void onSiegeFinish()
	{
		// cleanup
		unregisterSiegeBossListeners();
		
		// despawn npcs
		deSpawnNpcs(getSiegeLocationId());
		
		// for artifact should be always true
		if (isBossKilled())
		{
			onCapture();
		}
		else
		{
			log.error("Artifact siege (artifactId:" + getSiegeLocationId() + ") ended without killing a boss.");
		}
		
		// add new spawns
		spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.PEACE);
		
		// Store siege results in DB
		DAOManager.getDAO(SiegeDAO.class).updateLocation(getSiegeLocation());
		
		broadcastUpdate(getSiegeLocation());
		startSiege(getSiegeLocationId());
	}
	
	/**
	 * Handles the logic for when a siege location is captured.<br>
	 * This method updates the winner counter and sets the winning race and legion.<br>
	 * It also sends system messages to all players based on the victory results.<br>
	 * Finally, it captures the base if the location ID falls within specific ranges.
	 */
	protected void onCapture()
	{
		// Update winner counter
		final SiegeRaceCounter wRaceCounter = getSiegeCounter().getWinnerRaceCounter();
		getSiegeLocation().setRace(wRaceCounter.getSiegeRace());
		
		// Update legion
		final Integer wLegionId = wRaceCounter.getWinnerLegionId();
		getSiegeLocation().setLegionId(wLegionId != null ? wLegionId : 0);
		
		// misc stuff to send player system message
		if (getSiegeLocation().getRace() == SiegeRace.BALAUR)
		{
			final AionServerPacket lRacePacket = new SM_SYSTEM_MESSAGE(1320004, getSiegeLocation().getNameAsDescriptionId(), getSiegeLocation().getRace().getDescriptionId());
			World.getInstance().doOnAllPlayers(new Visitor<Player>()
			{
				@Override
				public void visit(Player object)
				{
					PacketSendUtility.sendPacket(object, lRacePacket);
				}
			});
		}
		else
		{
			// Prepare packet data
			String wPlayerName = "";
			final Race wRace = wRaceCounter.getSiegeRace() == SiegeRace.ELYOS ? Race.ELYOS : Race.ASMODIANS;
			final Legion wLegion = wLegionId != null ? LegionService.getInstance().getLegion(wLegionId) : null;
			if (!wRaceCounter.getPlayerDamageCounter().isEmpty())
			{
				final Integer wPlayerId = wRaceCounter.getPlayerDamageCounter().keySet().iterator().next();
				wPlayerName = PlayerService.getPlayerName(wPlayerId);
			}
			
			final String winnerName = wLegion != null ? wLegion.getLegionName() : wPlayerName;
			
			// prepare packets, we can use single packet instance
			final AionServerPacket wRacePacket = new SM_SYSTEM_MESSAGE(1320002, wRace.getRaceDescriptionId(), winnerName, getSiegeLocation().getNameAsDescriptionId());
			final AionServerPacket lRacePacket = new SM_SYSTEM_MESSAGE(1320004, getSiegeLocation().getNameAsDescriptionId(), wRace.getRaceDescriptionId());
			
			// send update to players
			World.getInstance().doOnAllPlayers(new Visitor<Player>()
			{
				@Override
				public void visit(Player player)
				{
					PacketSendUtility.sendPacket(player, player.getRace().equals(wRace) ? wRacePacket : lRacePacket);
				}
			});
		}
		
		if ((getSiegeLocation().getLocationId() >= 1511) && (getSiegeLocation().getLocationId() <= 1519))
		{
			if (getSiegeLocation().getRace() == SiegeRace.BALAUR)
			{
				BaseService.getInstance().capture(getSiegeLocation().getBaseId(), Race.NPC);
			}
			
			if (getSiegeLocation().getRace() == SiegeRace.ASMODIANS)
			{
				BaseService.getInstance().capture(getSiegeLocation().getBaseId(), Race.ASMODIANS);
			}
			
			if (getSiegeLocation().getRace() == SiegeRace.ELYOS)
			{
				BaseService.getInstance().capture(getSiegeLocation().getBaseId(), Race.ELYOS);
			}
		}
	}
	
	/**
	 * Checks if the siege is an endless type.<br>
	 * This method always returns {@code true}.
	 * @return {@code true} if the siege has no end condition.
	 */
	@Override
	public boolean isEndless()
	{
		return true;
	}
	
	/**
	 * Adds a specific amount of abyss points to a {@link Player}.<br>
	 * This method updates the player's score during an artifact siege.
	 * @param player The {@code Player} object to receive the points.
	 * @param abysPoints The number of points to add to the player.
	 */
	@Override
	public void addAbyssPoints(Player player, int abysPoints)
	{
		// No need to control AP
	}
	
	/**
	 * Adds a specific amount of glory points to a {@link Player}.<br>
	 * This method updates the player's score during an artifact siege.
	 * @param player The {@code Player} object to receive the points.
	 * @param gloryPoints The number of points to add to the player.
	 */
	@Override
	public void addGloryPoints(Player player, int gloryPoints)
	{
		// No need to control GP
	}
}
