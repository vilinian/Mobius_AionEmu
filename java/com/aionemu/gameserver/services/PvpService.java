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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.configs.main.PunishmentConfig;
import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.controllers.attack.KillList;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.conquerer_protector.ConquerorsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.utils.stats.StatFunctions;

/**
 * This service manages all Player vs Player (PvP) related logic within the game.<br>
 * It handles mechanics such as combat interactions, rewards, and status updates for {@link Player} objects.
 * @author Sarynth
 */
public class PvpService
{
	private static Logger log = LoggerFactory.getLogger("KILL_LOG");
	
	/**
	 * Provides the global instance of the {@link PvpService}.<br>
	 * This method follows the singleton pattern.
	 * @return The single shared instance of {@code PvpService}.
	 */
	public static PvpService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private final Map<Integer, KillList> pvpKillLists;
	private ArrayList<String> antiZergMap;
	
	/**
	 * Private constructor for the {@link PvpService} class.<br>
	 * This prevents other classes from creating new instances of this service.<br>
	 * It initializes the internal {@code pvpKillLists} map.
	 */
	private PvpService()
	{
		pvpKillLists = new ConcurrentHashMap<>();
	}
	
	/**
	 * Retrieves the number of times a specific victim was killed by a winner.<br>
	 * This method checks the {@code pvpKillLists} for the given {@code winnerId}.<br>
	 * It returns {@code 0} if no kill list exists for that player.
	 * @param winnerId The unique identifier of the winning player.
	 * @param victimId The unique identifier of the defeated player.
	 * @return The total count of kills recorded for this specific matchup.
	 */
	private int getKillsFor(int winnerId, int victimId)
	{
		final KillList winnerKillList = pvpKillLists.get(winnerId);
		
		if (winnerKillList == null)
		{
			return 0;
		}
		
		return winnerKillList.getKillsFor(victimId);
	}
	
	/**
	 * Records a kill in the {@code KillList} for a specific player.<br>
	 * This method updates the statistics for the winning player.
	 * @param winnerId The unique identifier of the player who won the fight.
	 * @param victimId The unique identifier of the player who was defeated.
	 */
	private void addKillFor(int winnerId, int victimId)
	{
		KillList winnerKillList = pvpKillLists.get(winnerId);
		if (winnerKillList == null)
		{
			winnerKillList = new KillList();
			pvpKillLists.put(winnerId, winnerKillList);
		}
		
		winnerKillList.addKillFor(victimId);
	}
	
	/**
	 * Processes the rewards and consequences for a player who has been defeated.<br>
	 * This method identifies the winner based on damage dealt to the {@code victim}.<br>
	 * It grants items to the winner, updates kill records, and handles AP loss.<br>
	 * It also checks for power leveling and notifies relevant services like quests and conquerors.
	 * @param victim The {@link Player} who was defeated in combat.
	 */
	public void doReward(Player victim)
	{
		// winner is the player that receives the kill count
		final Player winner = victim.getAggroList().getMostPlayerDamage();
		
		final int totalDamage = victim.getAggroList().getTotalDamage();
		
		if ((totalDamage == 0) || (winner == null) || (winner.getRace() == victim.getRace()))
		{
			return;
		}
		
		// Add Player Kill to record.
		if (getKillsFor(winner.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS)
		{
			winner.getAbyssRank().setAllKill();
			final int kills = winner.getAbyssRank().getAllKill();
			
			// Pvp Kill Reward.
			if (CustomConfig.ENABLE_KILL_REWARD)
			{
				if ((kills % CustomConfig.KILLS_NEEDED1) == 1)
				{
					ItemService.addItem(winner, CustomConfig.REWARD1, 1);
					PacketSendUtility.sendMessage(winner, "Congratulations, you have won " + "[item: " + CustomConfig.REWARD1 + "] for having killed " + CustomConfig.KILLS_NEEDED1 + " players !");
					log.info("[REWARD] Player [" + winner.getName() + "] win 2 [" + CustomConfig.REWARD1 + "]");
				}
				
				if ((kills % CustomConfig.KILLS_NEEDED2) == 3)
				{
					ItemService.addItem(winner, CustomConfig.REWARD2, 1);
					PacketSendUtility.sendMessage(winner, "Congratulations, you have won " + "[item: " + CustomConfig.REWARD2 + "] for having killed " + CustomConfig.KILLS_NEEDED2 + " players !");
					log.info("[REWARD] Player [" + winner.getName() + "] win 4 [" + CustomConfig.REWARD2 + "]");
				}
				
				if ((kills % CustomConfig.KILLS_NEEDED3) == 5)
				{
					ItemService.addItem(winner, CustomConfig.REWARD3, 1);
					PacketSendUtility.sendMessage(winner, "Congratulations, you have won " + "[item: " + CustomConfig.REWARD3 + "] for having killed " + CustomConfig.KILLS_NEEDED3 + " players !");
					log.info("[REWARD] Player [" + winner.getName() + "] win 6 [" + CustomConfig.REWARD3 + "]");
				}
			}
		}
		
		// Announce that player has died.
		PacketSendUtility.broadcastPacketAndReceive(victim, SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_FRIENDLY_DEATH_TO_B(victim.getName(), winner.getName()));
		
		// Pvp Kill Reward.
		int reduceap = PunishmentConfig.PUNISHMENT_REDUCEAP;
		if (reduceap < 0)
		{
			reduceap *= -1;
		}
		
		if (reduceap > 100)
		{
			reduceap = 100;
		}
		
		// Kill-log
		if (LoggingConfig.LOG_KILL)
		{
			log.info("[KILL] Player [" + winner.getName() + "] killed [" + victim.getName() + "]");
		}
		
		if ((LoggingConfig.LOG_PL) || (reduceap > 0))
		{
			final String ip1 = winner.getClientConnection().getIP();
			final String mac1 = winner.getClientConnection().getMacAddress();
			final String ip2 = victim.getClientConnection().getIP();
			final String mac2 = victim.getClientConnection().getMacAddress();
			if ((mac1 != null) && (mac2 != null) && (winner.getAccessLevel() < 3) && (victim.getAccessLevel() < 3))
			{
				if ((ip1.equalsIgnoreCase(ip2)) && (mac1.equalsIgnoreCase(mac2)))
				{
					AuditLogger.info(winner, "Power Leveling : " + winner.getName() + " with " + victim.getName() + ", They have the sames ip=" + ip1 + " and mac=" + mac1 + ".");
					if (reduceap > 0)
					{
						final int win_ap = (winner.getAbyssRank().getAp() * reduceap) / 100;
						final int vic_ap = (victim.getAbyssRank().getAp() * reduceap) / 100;
						AbyssPointsService.addAp(winner, -win_ap);
						AbyssPointsService.addAp(victim, -vic_ap);
						PacketSendUtility.sendMessage(winner, "[PL-AP] You lost " + reduceap + "% of your total ap");
						PacketSendUtility.sendMessage(victim, "[PL-AP] You lost " + reduceap + "% of your total ap");
					}
					return;
				}
				
				if (ip1.equalsIgnoreCase(ip2))
				{
					AuditLogger.info(winner, "Possible Power Leveling : " + winner.getName() + " with " + victim.getName() + ", They have the sames ip=" + ip1 + ".");
					AuditLogger.info(winner, "Check if " + winner.getName() + " and " + victim.getName() + " are Brothers-Sisters-Lovers-dogs-cats...");
				}
			}
		}
		
		// Keep track of player damage to calculate AP removal.
		int playerDamage = 0;
		boolean success;
		
		// Distribute AP to groups and players that had damage.
		for (AggroInfo aggro : victim.getAggroList().getFinalDamageList(true))
		{
			success = false;
			if (aggro.getAttacker() instanceof Player)
			{
				success = rewardPlayer(victim, totalDamage, aggro);
			}
			else if (aggro.getAttacker() instanceof PlayerGroup)
			{
				success = rewardPlayerGroup(victim, totalDamage, aggro);
			}
			else if (aggro.getAttacker() instanceof PlayerAlliance)
			{
				success = rewardPlayerAlliance(victim, totalDamage, aggro);
			}
			
			// Add damage last, so we don't include damage from same race. (Duels, Arena)
			if (success)
			{
				playerDamage += aggro.getDamage();
			}
		}
		
		ConquerorsService.getInstance().onKill(winner, victim);
		
		// notify Quest engine for winner + his group
		notifyKillQuests(winner, victim);
		
		// Apply lost AP to defeated player
		final int apLost = StatFunctions.calculatePvPApLost(victim, winner);
		final int apActuallyLost = (apLost * playerDamage) / totalDamage;
		
		if (apActuallyLost > 0)
		{
			AbyssPointsService.addAp(victim, -apActuallyLost);
		}
	}
	
	/**
	 * Calculates and distributes rewards to a group of players based on damage dealt.<br>
	 * This method checks for valid targets, handles zerg protection, and applies rewards like AP, XP, and DP.<br>
	 * It returns {@code true} if the rewards were successfully distributed or {@code false} otherwise.
	 * @param victim The player who was defeated.
	 * @param totalDamage The total damage dealt to the victim.
	 * @param aggro The information regarding the attackers and their damage contribution.
	 * @return A boolean indicating whether the group was rewarded.
	 */
	private boolean rewardPlayerGroup(Player victim, int totalDamage, AggroInfo aggro)
	{
		// Reward Group
		final PlayerGroup group = ((PlayerGroup) aggro.getAttacker());
		
		// Don't Reward Player of Same Faction.
		if (group.getRace() == victim.getRace())
		{
			return false;
		}
		
		// Find group members in range
		final List<Player> players = new ArrayList<>();
		
		// Find highest rank and level in local group
		int maxRank = AbyssRankEnum.GRADE9_SOLDIER.getId();
		int maxLevel = 0;
		
		for (Player member : group.getMembers())
		{
			if (MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE))
			{
				// Don't distribute AP to a dead player!
				if (!member.getLifeStats().isAlreadyDead())
				{
					players.add(member);
					if (member.getLevel() > maxLevel)
					{
						maxLevel = member.getLevel();
					}
					
					if (member.getAbyssRank().getRank().getId() > maxRank)
					{
						maxRank = member.getAbyssRank().getRank().getId();
					}
				}
				
				if ((Zerg(member, victim) > CustomConfig.ANTI_ZERG_COUNT) && !victim.isInGroup2())
				{
					final PlayerGroup grp = member.getPlayerGroup2();
					for (Player members : grp.getMembers())
					{
						PacketSendUtility.sendWhiteMessageOnCenter(members, "Zerg system : No AP won ! Do not be more than " + CustomConfig.ANTI_ZERG_COUNT + " of the same person!");
						members.getEffectController().setAbnormal(AbnormalState.SLEEP.getId());
						members.getEffectController().updatePlayerEffectIcons();
						members.getEffectController().broadCastEffects();
						ThreadPoolManager.getInstance().schedule(() ->
						{
							members.getEffectController().unsetAbnormal(AbnormalState.SLEEP.getId());
							members.getEffectController().updatePlayerEffectIcons();
							members.getEffectController().broadCastEffects();
						}, 13 * 1000);
						
					}
					
					return false;
				}
			}
		}
		
		// They are all dead or out of range.
		if (players.isEmpty())
		{
			return false;
		}
		
		final int baseApReward = StatFunctions.calculatePvpApGained(victim, maxRank, maxLevel);
		final int baseXpReward = StatFunctions.calculatePvpXpGained(victim, maxRank, maxLevel);
		final int baseDpReward = StatFunctions.calculatePvpDpGained(victim, maxRank, maxLevel);
		final float groupPercentage = (float) aggro.getDamage() / totalDamage;
		final int apRewardPerMember = Math.round((baseApReward * groupPercentage) / players.size());
		final int xpRewardPerMember = Math.round((baseXpReward * groupPercentage) / players.size());
		final int dpRewardPerMember = Math.round((baseDpReward * groupPercentage) / players.size());
		
		for (Player member : players)
		{
			int memberApGain = 1;
			int memberXpGain = 1;
			int memberDpGain = 1;
			if (getKillsFor(member.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS)
			{
				if (apRewardPerMember > 0)
				{
					memberApGain = Math.round(RewardType.AP_PLAYER.calcReward(member, apRewardPerMember));
				}
				
				if (xpRewardPerMember > 0)
				{
					memberXpGain = Math.round(xpRewardPerMember * member.getRates().getXpPlayerGainRate());
				}
				
				if (dpRewardPerMember > 0)
				{
					memberDpGain = Math.round(StatFunctions.adjustPvpDpGained(dpRewardPerMember, victim.getLevel(), member.getLevel()) * member.getRates().getDpPlayerRate());
				}
				
			}
			
			final Player partner = member.findPartner();
			if (member.isMarried() && (member.getPlayerGroup2().getMembers() == partner) && (member.getPlayerGroup2().getMembers().size() == 2))
			{
				AbyssPointsService.addAp(member, victim, memberApGain + ((memberApGain * 20) / 100)); // 20% more AP for weddings
			}
			else
			{
				AbyssPointsService.addAp(member, victim, memberApGain);
			}
			
			member.getCommonData().addExp(memberXpGain, RewardType.PVP_KILL, victim.getName());
			member.getCommonData().addDp(memberDpGain);
			AchievementService.getInstance().onUpdateAchievementAction(member, 0, 1, AchievementActionType.PVP);
			
			addKillFor(member.getObjectId(), victim.getObjectId());
		}
		
		return true;
	}
	
	/**
	 * Distributes rewards to members of a {@link PlayerAlliance}.<br>
	 * It checks if the alliance is from a different race than the {@code victim}.<br>
	 * Only online players within range who are not dead receive rewards.<br>
	 * Rewards are calculated based on damage percentage and member ranks.
	 * @param victim The player who was defeated.
	 * @param totalDamage The total amount of damage dealt to the victim.
	 * @param aggro The {@link AggroInfo} containing attacker details and damage stats.
	 * @return {@code true} if rewards were successfully distributed, otherwise {@code false}.
	 */
	private boolean rewardPlayerAlliance(Player victim, int totalDamage, AggroInfo aggro)
	{
		// Reward Alliance
		final PlayerAlliance alliance = ((PlayerAlliance) aggro.getAttacker());
		
		// Don't Reward Player of Same Faction.
		if (alliance.getLeaderObject().getRace() == victim.getRace())
		{
			return false;
		}
		
		// Find group members in range
		final List<Player> players = new ArrayList<>();
		
		// Find highest rank and level in local group
		int maxRank = AbyssRankEnum.GRADE9_SOLDIER.getId();
		int maxLevel = 0;
		
		for (Player member : alliance.getMembers())
		{
			if (!member.isOnline())
			{
				continue;
			}
			
			if (MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE))
			{
				// Don't distribute AP to a dead player!
				if (!member.getLifeStats().isAlreadyDead())
				{
					players.add(member);
					if (member.getLevel() > maxLevel)
					{
						maxLevel = member.getLevel();
					}
					
					if (member.getAbyssRank().getRank().getId() > maxRank)
					{
						maxRank = member.getAbyssRank().getRank().getId();
					}
				}
			}
		}
		
		// They are all dead or out of range.
		if (players.isEmpty())
		{
			return false;
		}
		
		final int baseApReward = StatFunctions.calculatePvpApGained(victim, maxRank, maxLevel);
		final int baseXpReward = StatFunctions.calculatePvpXpGained(victim, maxRank, maxLevel);
		final int baseDpReward = StatFunctions.calculatePvpDpGained(victim, maxRank, maxLevel);
		final float groupPercentage = (float) aggro.getDamage() / totalDamage;
		final int apRewardPerMember = Math.round((baseApReward * groupPercentage) / players.size());
		final int xpRewardPerMember = Math.round((baseXpReward * groupPercentage) / players.size());
		final int dpRewardPerMember = Math.round((baseDpReward * groupPercentage) / players.size());
		
		for (Player member : players)
		{
			int memberApGain = 1;
			int memberXpGain = 1;
			int memberDpGain = 1;
			if (getKillsFor(member.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS)
			{
				if (apRewardPerMember > 0)
				{
					memberApGain = Math.round(RewardType.AP_PLAYER.calcReward(member, apRewardPerMember));
				}
				
				if (xpRewardPerMember > 0)
				{
					memberXpGain = Math.round(xpRewardPerMember * member.getRates().getXpPlayerGainRate());
				}
				
				if (dpRewardPerMember > 0)
				{
					memberDpGain = Math.round(StatFunctions.adjustPvpDpGained(dpRewardPerMember, victim.getLevel(), member.getLevel()) * member.getRates().getDpPlayerRate());
				}
			}
			
			AbyssPointsService.addAp(member, victim, memberApGain);
			member.getCommonData().addExp(memberXpGain, RewardType.PVP_KILL, victim.getName());
			member.getCommonData().addDp(memberDpGain);
			AchievementService.getInstance().onUpdateAchievementAction(member, 0, 1, AchievementActionType.PVP);
			
			addKillFor(member.getObjectId(), victim.getObjectId());
		}
		
		return true;
	}
	
	/**
	 * Calculates and distributes rewards to the player who defeated a victim.<br>
	 * It checks if the winner is eligible based on race, distance, and life status.<br>
	 * Rewards are distributed proportionally based on the damage dealt by the attacker.
	 * @param victim The {@code Player} object of the person who was defeated.
	 * @param totalDamage The total amount of damage dealt during the encounter.
	 * @param aggro The {@code AggroInfo} containing details about the attackers and their damage.
	 * @return {@code true} if rewards were successfully granted, or {@code false} otherwise.
	 */
	private boolean rewardPlayer(Player victim, int totalDamage, AggroInfo aggro)
	{
		// Reward Player
		final Player winner = ((Player) aggro.getAttacker());
		
		// Don't Reward Player out of range/dead/same faction
		if ((winner.getRace() == victim.getRace()) || !MathUtil.isIn3dRange(winner, victim, GroupConfig.GROUP_MAX_DISTANCE) || winner.getLifeStats().isAlreadyDead())
		{
			return false;
		}
		
		int baseApReward = 1;
		int baseXpReward = 1;
		int baseDpReward = 1;
		
		if (getKillsFor(winner.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS)
		{
			baseApReward = StatFunctions.calculatePvpApGained(victim, winner.getAbyssRank().getRank().getId(), winner.getLevel());
			baseXpReward = StatFunctions.calculatePvpXpGained(victim, winner.getAbyssRank().getRank().getId(), winner.getLevel());
			baseDpReward = StatFunctions.calculatePvpDpGained(victim, winner.getAbyssRank().getRank().getId(), winner.getLevel());
		}
		
		int apPlayerReward = Math.round((baseApReward * aggro.getDamage()) / totalDamage);
		apPlayerReward = (int) RewardType.AP_PLAYER.calcReward(winner, apPlayerReward);
		final int xpPlayerReward = Math.round((baseXpReward * winner.getRates().getXpPlayerGainRate() * aggro.getDamage()) / totalDamage);
		final int dpPlayerReward = Math.round((baseDpReward * winner.getRates().getDpPlayerRate() * aggro.getDamage()) / totalDamage);
		
		AbyssPointsService.addAp(winner, victim, apPlayerReward);
		winner.getCommonData().addExp(xpPlayerReward, RewardType.PVP_KILL, victim.getName());
		winner.getCommonData().addDp(dpPlayerReward);
		AchievementService.getInstance().onUpdateAchievementAction(winner, 0, 1, AchievementActionType.PVP);
		addKillFor(winner.getObjectId(), victim.getObjectId());
		return true;
	}
	
	/**
	 * Notifies the quest system about a kill between two players.<br>
	 * This method checks if the players are of different races.<br>
	 * It identifies all eligible rewarded players based on group or alliance status.<br>
	 * It then triggers quest updates for those within range and not dead.
	 * @param winner The player who performed the kill.
	 * @param victim The player who was killed.
	 */
	private void notifyKillQuests(Player winner, Player victim)
	{
		if (winner.getRace() == victim.getRace())
		{
			return;
		}
		
		final List<Player> rewarded = new ArrayList<>();
		final int worldId = victim.getWorldId();
		
		if (winner.isInGroup2())
		{
			rewarded.addAll(winner.getPlayerGroup2().getOnlineMembers());
		}
		else if (winner.isInAlliance2())
		{
			rewarded.addAll(winner.getPlayerAllianceGroup2().getOnlineMembers());
		}
		else
		{
			rewarded.add(winner);
		}
		
		for (Player p : rewarded)
		{
			if (!MathUtil.isIn3dRange(p, victim, GroupConfig.GROUP_MAX_DISTANCE) || p.getLifeStats().isAlreadyDead())
			{
				continue;
			}
			
			QuestEngine.getInstance().onKillInWorld(new QuestEnv(victim, p, 0, 0), worldId);
			QuestEngine.getInstance().onKillRanked(new QuestEnv(victim, p, 0, 0), victim.getAbyssRank().getRank());
		}
		
		rewarded.clear();
	}
	
	/**
	 * Calculates the number of participants involved in a zerg attack.<br>
	 * This method checks if anti-zerg settings are enabled and valid for the current world.<br>
	 * It counts how many players from the same team as the winner are attacking the victim.
	 * @param winner The {@link Player} who won the encounter.
	 * @param victim The {@link Player} who was defeated.
	 * @return The total count of zerg participants, or 0 if no zerg is detected.
	 */
	private int Zerg(Player winner, Player victim)
	{
		if (!CustomConfig.ANTI_ZERG_ENABLED)
		{
			return 0;
		}
		
		antiZergMap = new ArrayList<>();
		
		if (!Objects.equals(CustomConfig.ANTI_ZERG_MAP, ""))
		{
			try
			{
				Collections.addAll(antiZergMap, CustomConfig.ANTI_ZERG_MAP.split(","));
				
				if (!antiZergMap.contains(String.valueOf(winner.getWorldId())) && !antiZergMap.contains(String.valueOf(victim.getWorldId())))
				{
					return 0;
				}
				
			}
			catch (Exception ex)
			{
				log.error(ex.getMessage());
				return 0;
			}
		}
		
		int zergeurs = 0;
		
		for (AggroInfo ai : victim.getAggroList().getList())
		{
			final Creature master = ((Creature) ai.getAttacker()).getMaster();
			if (master instanceof Player)
			{
				final Player player = (Player) master;
				if (player.isInGroup2())
				{
					if (player.isInSameTeam(winner))
					{
						zergeurs++;
					}
				}
			}
			
		}
		
		return zergeurs;
	}
	
	private static class SingletonHolder
	{
		protected static final PvpService instance = new PvpService();
	}
}
