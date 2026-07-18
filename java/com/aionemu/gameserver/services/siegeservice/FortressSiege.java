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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeLegionReward;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeReward;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.BaseService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.mail.AbyssSiegeLevel;
import com.aionemu.gameserver.services.mail.MailFormatter;
import com.aionemu.gameserver.services.mail.SiegeResult;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * This class manages the logic and state for a specific fortress siege.<br>
 * Each {@code FortressSiege} instance is non-reusable and represents a single unique event.
 * @author SoulKeeper
 */
public class FortressSiege extends Siege<FortressLocation>
{
	private static final Logger log = LoggerFactory.getLogger("SIEGE_LOG");
	
	/**
	 * Creates a new instance of {@code FortressSiege}.<br>
	 * This constructor initializes the siege using a specific fortress location.
	 * @param fortress The {@link FortressLocation} where the siege will take place.
	 */
	public FortressSiege(FortressLocation fortress)
	{
		super(fortress);
	}
	
	/**
	 * Initializes the siege event when it begins.<br>
	 * This method sets the fortress to a vulnerable state and clears existing enemies.<br>
	 * It spawns necessary NPCs and registers listeners for abyss points and glory.<br>
	 * If the location is specific, it also captures base posts.
	 */
	@Override
	public void onSiegeStart()
	{
		if (getSiegeLocation().getOccupyCount() == 2)
		{
			log.info("Max Occupy reached(" + getSiegeLocation().getOccupyCount() + ") , Fortress reset to default (Balaur)");
			OccupyCount();
		}
		else if (LoggingConfig.LOG_SIEGE)
		{
			log.info("[FortressSiege] Siege started. [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "]" + " [LegionId:" + getSiegeLocation().getLegionId() + "]" + " [OccupyCount: " + getSiegeLocation().getOccupyCount() + "]");
		}
		
		// Mark fortress as vulnerable
		getSiegeLocation().setVulnerable(true);
		
		// Let the world know where the siege are
		broadcastState(getSiegeLocation());
		
		// Clear fortress from enemys
		getSiegeLocation().clearLocation();
		
		// Remove all and spawn siege NPCs
		deSpawnNpcs(getSiegeLocationId());
		spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.SIEGE);
		initSiegeBoss();
		
		if (getSiegeLocation().getLocationId() == 7011)
		{
			captureBasePosts();
		}
	}
	
	/**
	 * Resets the occupancy count for the current siege location.<br>
	 * This method updates the {@code SiegeLocation} data in the database.<br>
	 * It then broadcasts the updated information to all players.<br>
	 * Finally, it triggers the {@code onSiegeStart} process.
	 */
	private void OccupyCount()
	{
		// unregisterSiegeBossListeners();
		getSiegeLocation().setRace(SiegeRace.BALAUR);
		getSiegeLocation().setLegionId(0);
		getArtifact().setLegionId(0);
		getSiegeLocation().setOccupyCount(0);
		DAOManager.getDAO(SiegeDAO.class).updateLocation(getSiegeLocation());
		broadcastUpdate(getSiegeLocation());
		onSiegeStart();
	}
	
	/**
	 * This method handles the capture logic for various base posts.<br>
	 * It calls {@code Race)} for specific locations.<br>
	 * The captures are applied to both Asmodian and Elyos races based on configuration.
	 */
	private void captureBasePosts()
	{
		BaseService.getInstance().capture(90, Race.ASMODIANS);
		BaseService.getInstance().capture(91, Race.ELYOS);
		BaseService.getInstance().capture(113, Race.getRaceByString(getSiegeLocation().getRace().toString()));
		BaseService.getInstance().capture(114, Race.getRaceByString(getSiegeLocation().getRace().toString()));
		BaseService.getInstance().capture(115, Race.getRaceByString(getSiegeLocation().getRace().toString()));
	}
	
	/**
	 * Handles the logic for ending a fortress siege.<br>
	 * This method cleans up listeners and resets the fortress state.<br>
	 * It distributes rewards to winners and updates the database.
	 */
	@Override
	public void onSiegeFinish()
	{
		final SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();
		final SiegeRace looser = getSiegeLocation().getRace();
		if (LoggingConfig.LOG_SIEGE)
		{
			if (winner != null)
			{
				log.info("[FortressSiege] Siege finished. [FORTRESS:" + getSiegeLocationId() + "] [OLD RACE: " + getSiegeLocation().getRace() + "] [OLD LegionId:" + getSiegeLocation().getLegionId() + "] [NEW RACE: " + winner.getSiegeRace() + "] [NEW LegionId:" + (winner.getWinnerLegionId() == null ? 0 : winner.getWinnerLegionId()) + "]");
			}
			else
			{
				log.info("[FortressSiege] Siege finished. No winner found [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] [LegionId:" + getSiegeLocation().getLegionId() + "]");
			}
		}
		
		// Unregister siege boss listeners for cleanup.
		unregisterSiegeBossListeners();
		
		// despawn protectors and make fortress invulnerable
		SiegeService.getInstance().deSpawnNpcs(getSiegeLocationId());
		getSiegeLocation().setVulnerable(false);
		getSiegeLocation().setUnderShield(false);
		
		// Guardian deity general was not killed, fortress stays with previous
		if (isBossKilled())
		{
			onCapture();
			applyBuff();
			broadcastUpdate(getSiegeLocation());
		}
		else
		{
			getSiegeLocation().setOccupyCount(getSiegeLocation().getOccupyCount() + 1);
			broadcastUpdate(getSiegeLocation());
			broadcastState(getSiegeLocation());
		}
		
		SiegeService.getInstance().spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.PEACE);
		
		// Reward players who own a legion if the fortress was not captured by Balaur.
		if (SiegeRace.BALAUR != getSiegeLocation().getRace())
		{
			giveRewardsToLegion();
			giveRewardsToPlayers(getSiegeCounter().getRaceCounter(getSiegeLocation().getRace()));
		}
		
		// Remove gp for players that lost the fortress
		if ((winner != null) && (winner.getSiegeRace() != looser))
		{
			giveLossToPlayers(looser);
		}
		
		// Update data in the DB
		DAOManager.getDAO(SiegeDAO.class).updateSiegeLocation(getSiegeLocation());
		
		getSiegeLocation().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				player.unsetInsideZoneType(ZoneType.SIEGE);
				if (isBossKilled() && (SiegeRace.getByRace(player.getRace()) == getSiegeLocation().getRace()))
				{
					QuestEngine.getInstance().onKill(new QuestEnv(getBoss(), player, 0, 0));
				}
			}
		});
		
		if (getSiegeLocation().getLocationId() == 7011)
		{
			BaseService.getInstance().capture(113, Race.NPC);
			BaseService.getInstance().capture(114, Race.NPC);
			BaseService.getInstance().capture(115, Race.NPC);
		}
	}
	
	/**
	 * Handles the logic for capturing a siege location.<br>
	 * This method updates the winning race and sets the correct legion ID.<br>
	 * It also handles specific logic for the {@code BALAUR} race type.
	 */
	public void onCapture()
	{
		final SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();
		
		// SiegeRace looser = getSiegeLocation().getRace();
		getSiegeLocation().setRace(winner.getSiegeRace());
		getArtifact().setRace(winner.getSiegeRace());
		if (SiegeRace.BALAUR == winner.getSiegeRace())
		{
			getSiegeLocation().setLegionId(0);
			getArtifact().setLegionId(0);
		}
		else
		{
			final Integer topLegionId = winner.getWinnerLegionId();
			getSiegeLocation().setLegionId(topLegionId != null ? topLegionId : 0);
			getArtifact().setLegionId(topLegionId != null ? topLegionId : 0);
		}
	}
	
	/**
	 * Applies the appropriate buffs to all players based on the siege results.<br>
	 * This method updates the winning race and legion information.<br>
	 * It then grants specific effects to players depending on their race.
	 */
	public void applyBuff()
	{
		final SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();
		getSiegeLocation().setRace(winner.getSiegeRace());
		getArtifact().setRace(winner.getSiegeRace());
		if (SiegeRace.BALAUR == winner.getSiegeRace())
		{
			getSiegeLocation().setLegionId(0);
			getArtifact().setLegionId(0);
		}
		else
		{
			final Integer topLegionId = winner.getWinnerLegionId();
			getSiegeLocation().setLegionId(topLegionId != null ? topLegionId : 0);
			getArtifact().setLegionId(topLegionId != null ? topLegionId : 0);
		}
		
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				// Buff for Both Race.
				if (player.getEffectController().hasAbnormalEffect(getSiegeLocation().getBuffId()))
				{
					player.getEffectController().removeEffect(getSiegeLocation().getBuffId());
				}
				else
				{
					SkillEngine.getInstance().applyEffectDirectly(getSiegeLocation().getBuffId(), player, player, 0);
				}
				
				// Buff for Asmodians or Elyos.
				if (player.getEffectController().hasAbnormalEffect(getSiegeLocation().getBuffIdA()))
				{
					player.getEffectController().removeEffect(getSiegeLocation().getBuffIdA());
				}
				
				if (player.getEffectController().hasAbnormalEffect(getSiegeLocation().getBuffIdE()))
				{
					player.getEffectController().removeEffect(getSiegeLocation().getBuffIdE());
				}
				
				if (player.getCommonData().getRace() == Race.ASMODIANS)
				{
					SkillEngine.getInstance().applyEffectDirectly(getSiegeLocation().getBuffIdA(), player, player, 0);
				}
				
				if (player.getCommonData().getRace() == Race.ELYOS)
				{
					SkillEngine.getInstance().applyEffectDirectly(getSiegeLocation().getBuffIdE(), player, player, 0);
				}
			}
		});
	}
	
	/**
	 * Checks if the siege is an endless type.<br>
	 * This method always returns {@code false}.
	 * @return {@code false} if the siege has no end condition.
	 */
	@Override
	public boolean isEndless()
	{
		return false;
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
		getSiegeCounter().addAbyssPoints(player, abysPoints);
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
		getSiegeCounter().addGloryPoints(player, gloryPoints);
	}
	
	/**
	 * Distributes rewards to the legion that owns the fortress.<br>
	 * This method checks if a boss was killed or if the fortress is unowned before processing.<br>
	 * It sends an abyss reward mail to the B General of the owning legion.
	 */
	protected void giveRewardsToLegion()
	{
		// We do not give rewards if fortress was captured for first time
		if (isBossKilled())
		{
			if (LoggingConfig.LOG_SIEGE)
			{
				log.info("[FortressSiege] [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] [LEGION :" + getSiegeLocation().getLegionId() + "] Legion Reward not sending because fortress was captured(siege boss killed).");
			}
			return;
		}
		
		// Legion with id 0 = not exists?
		if (getSiegeLocation().getLegionId() == 0)
		{
			if (LoggingConfig.LOG_SIEGE)
			{
				log.info("[FortressSiege] [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] [LEGION :" + getSiegeLocation().getLegionId() + "] Legion Reward not sending because fortress not owned by any legion.");
			}
			return;
		}
		
		final List<SiegeLegionReward> legionRewards = getSiegeLocation().getLegionReward();
		final int legionBGeneral = LegionService.getInstance().getLegionBGeneral(getSiegeLocation().getLegionId());
		if (legionBGeneral != 0)
		{
			final PlayerCommonData BGeneral = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(legionBGeneral);
			if (LoggingConfig.LOG_SIEGE)
			{
				log.info("[FortressSiege] [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] Legion Reward in process... LegionId:" + getSiegeLocation().getLegionId() + " General Name:" + BGeneral.getName());
			}
			
			if (legionRewards != null)
			{
				for (SiegeLegionReward medalsType : legionRewards)
				{
					if (LoggingConfig.LOG_SIEGE)
					{
						log.info("[FortressSiege] [Legion Reward to: " + BGeneral.getName() + "] ITEM RETURN " + medalsType.getItemId() + " ITEM COUNT " + (medalsType.getCount() * SiegeConfig.SIEGE_MEDAL_RATE));
					}
					
					MailFormatter.sendAbyssRewardMail(getSiegeLocation(), BGeneral, AbyssSiegeLevel.NONE, SiegeResult.PROTECT, System.currentTimeMillis(), medalsType.getItemId(), medalsType.getCount() * SiegeConfig.SIEGE_MEDAL_RATE, 0);
				}
			}
		}
	}
	
	/**
	 * Deducts Abyss Points from players based on their rank.<br>
	 * This method identifies players belonging to the specified {@code SiegeRace}.<br>
	 * It applies a penalty to each player's score depending on their current rank.<br>
	 * The calculation is performed for all players currently at the siege location.
	 * @param race The {@code SiegeRace} type used to determine which players receive the loss.
	 */
	protected void giveLossToPlayers(SiegeRace race)
	{
		if (race == SiegeRace.BALAUR)
		{ // this shouldn't happen, but secure is secure :)
			return;
		}
		
		getSiegeLocation().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				if (player.getRace().name() == race.name())
				{// dont know if this works...
					switch (player.getAbyssRank().getRank())
					{
						case SUPREME_COMMANDER:
							AbyssPointsService.addGp(player, -300);
							break;
						case COMMANDER:
							AbyssPointsService.addGp(player, -250);
							break;
						case GREAT_GENERAL:
							AbyssPointsService.addGp(player, -200);
							break;
						case GENERAL:
							AbyssPointsService.addGp(player, -150);
							break;
						case STAR5_OFFICER:
							AbyssPointsService.addGp(player, -100);
							break;
						case STAR4_OFFICER:
							AbyssPointsService.addGp(player, -50);
							break;
						case STAR3_OFFICER:
							AbyssPointsService.addGp(player, -25);
							break;
						case STAR2_OFFICER:
							AbyssPointsService.addGp(player, -20);
							break;
						case STAR1_OFFICER:
							AbyssPointsService.addGp(player, -10);
							break;
						default:
							break;
					}
				}
				else
				{
					return;
				}
			}
		});
	}
	
	/**
	 * Distributes rewards to players based on their performance in a siege.<br>
	 * This method calculates the ranking of players using {@code winnerDamage}.<br>
	 * It sends reward mails and grants GP points depending on the siege outcome.<br>
	 * Players who do not qualify for rewards still receive an empty notification mail.
	 * @param winnerDamage The counter containing player abyss points used to determine rankings.
	 */
	protected void giveRewardsToPlayers(SiegeRaceCounter winnerDamage)
	{
		// Get the map with playerId to siege reward
		final Map<Integer, Long> playerAbyssPoints = winnerDamage.getPlayerAbyssPoints();
		final List<Integer> topPlayersIds = new ArrayList<>(playerAbyssPoints.keySet());
		final Map<Integer, String> playerNames = PlayerService.getPlayerNames(playerAbyssPoints.keySet());
		final SiegeResult result = isBossKilled() ? SiegeResult.OCCUPY : SiegeResult.DEFENDER;
		
		// Black Magic Here :)
		int i = 0;
		final List<SiegeReward> playerRewards = getSiegeLocation().getReward();
		int rewardLevel = 0;
		for (SiegeReward topGrade : playerRewards)
		{
			final AbyssSiegeLevel level = AbyssSiegeLevel.getLevelById(++rewardLevel);
			for (int rewardedPC = 0; (i < topPlayersIds.size()) && (rewardedPC < topGrade.getTop()); ++i)
			{
				final Integer playerId = topPlayersIds.get(i);
				final PlayerCommonData pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(playerId);
				++rewardedPC;
				if (LoggingConfig.LOG_SIEGE)
				{
					log.info("[FortressSiege] [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] Player Reward to: " + playerNames.get(playerId) + "] ITEM RETURN " + topGrade.getItemId() + " ITEM COUNT " + (topGrade.getCount() * SiegeConfig.SIEGE_MEDAL_RATE));
				}
				
				MailFormatter.sendAbyssRewardMail(getSiegeLocation(), pcd, level, result, System.currentTimeMillis(), topGrade.getItemId(), topGrade.getCount() * SiegeConfig.SIEGE_MEDAL_RATE, 0);
				
				switch (level)
				{// gp reward for fotress occupation
					case HERO_DECORATION:
						AbyssPointsService.addGp(pcd.getPlayer(), 300);
						break;
					case MEDAL:
						AbyssPointsService.addGp(pcd.getPlayer(), 200);
						break;
					case ELITE_SOLDIER:
						AbyssPointsService.addGp(pcd.getPlayer(), 150);
						break;
					case VETERAN_SOLDIER:
						AbyssPointsService.addGp(pcd.getPlayer(), 100);
						break;
					default:
						AbyssPointsService.addGp(pcd.getPlayer(), 50);
						break;
				}
			}
		}
		
		if (!isBossKilled())
		{
			while (i < topPlayersIds.size())
			{
				i++;
				final Integer playerId = topPlayersIds.get(i);
				final PlayerCommonData pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(playerId);
				
				// Send Announcement Mails without reward to the rest
				MailFormatter.sendAbyssRewardMail(getSiegeLocation(), pcd, AbyssSiegeLevel.NONE, SiegeResult.EMPTY, System.currentTimeMillis(), 0, 0, 0);
			}
		}
	}
	
	/**
	 * Sends system messages to all players regarding the Temple Gate status.<br>
	 * This method schedules multiple countdown packets for the gate opening.<br>
	 * It notifies players when the gate will open in 5 minutes, 1 minute, 30 seconds, and 10 seconds.<br>
	 * Finally, it sends a message confirming that the gate has opened.
	 */
	public void templeGateMsg()
	{
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				// The Temple Gate will open in 5 minutes
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_Gab1_START01, 0);
				
				// The Temple Gate will open in 1 minute
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_Gab1_START02, 240000);
				
				// The Temple Gate will open in 30 seconds
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_Gab1_START03, 270000);
				
				// The Temple Gate will open in 10 seconds
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_Gab1_START04, 290000);
				
				// The Temple Gate has opened
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_Gab1_START05, 300000);
			}
		});
	}
	
	/**
	 * Retrieves the current location of the artifact for this siege.<br>
	 * It fetches the data from the {@link SiegeService}.
	 * @return the {@code ArtifactLocation} object.
	 */
	protected ArtifactLocation getArtifact()
	{
		return SiegeService.getInstance().getFortressArtifacts().get(getSiegeLocationId());
	}
	
	/**
	 * Checks if an artifact is currently present.<br>
	 * This method calls {@code getArtifact} to verify the result.
	 * @return {@code true} if an artifact exists, otherwise {@code false}.
	 */
	protected boolean hasArtifact()
	{
		return getArtifact() != null;
	}
}
