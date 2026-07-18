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
package com.aionemu.gameserver.services.abyss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.RankingConfig;
import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.dao.ServerVariablesDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * This service handles the periodic updates for player abyss ranks.<br>
 * It processes ranking logic and synchronizes data with the {@link AbyssRankDAO}.<br>
 * It ensures that rank progression is correctly calculated and saved to the database.
 * @author ATracer
 * @author ThunderBolt - GloryPoints
 * @rework Phantom_KNA
 */
public class AbyssRankUpdateService
{
	private static final Logger log = LoggerFactory.getLogger(AbyssRankUpdateService.class);
	private static final String GP_UPDATA_TIME = "0 10 2 ? * *";
	private static final Logger debuglog = LoggerFactory.getLogger("ABYSSRANK_LOG");
	
	/**
	 * Private constructor for the {@link AbyssRankUpdateService} class.<br>
	 * This prevents other classes from creating new instances of this service.<br>
	 * Use {@code getInstance} to access the singleton instance.
	 */
	private AbyssRankUpdateService()
	{
	}
	
	/**
	 * Provides the global instance of the {@link AbyssRankUpdateService}.<br>
	 * This method follows the singleton pattern to ensure only one service exists.
	 * @return The single instance of {@code AbyssRankUpdateService}.
	 */
	public static AbyssRankUpdateService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Schedules a periodic task to update Glory Points.<br>
	 * This method uses {@link CronService} to run {@code loadGpRank()} at specific intervals.
	 */
	public void GpointUpdata()
	{
		CronService.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				AbyssRankUpdateService.this.loadGpRank();
			}
		}, GP_UPDATA_TIME);
	}
	
	/**
	 * Loads and processes the GP ranks for players.<br>
	 * This method retrieves players from {@link AbyssRankDAO}.<br>
	 * It then calls {@code reduceGP} to update their values.
	 */
	private void loadGpRank()
	{
		final List<Integer> rankPlayers = DAOManager.getDAO(AbyssRankDAO.class).RankPlayers(9);
		reduceGP(rankPlayers);
	}
	
	/**
	 * Reduces the Glory Points for players based on their current rank.<br>
	 * This method checks if a player is eligible for a reduction.<br>
	 * It updates the {@code AbyssRankDAO} or sends a packet to the online player.
	 * @param rankPlayers A {@code List<Integer>} containing the IDs of players to process.
	 */
	private void reduceGP(List<Integer> rankPlayers)
	{
		for (int playerId : rankPlayers)
		{
			final AbyssRank rank = DAOManager.getDAO(AbyssRankDAO.class).loadAbyssRank(playerId);
			final Player player = World.getInstance().findPlayer(playerId);
			final int lostGp = rank.getRank().getDailyReduceGp();
			
			// Only Rank Officer
			if (rank.getRank().getId() < AbyssRankEnum.STAR1_OFFICER.getId())
			{
				continue;
			}
			
			if (player != null)
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402082, new Object[0]));
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402209, player.getName(), rank.getRank().getDailyReduceGp()));
				AbyssPointsService.addGp(player, lostGp * -1);
			}
			else
			{
				int newGP = rank.getGp() - lostGp;
				if (newGP < 0)
				{
					newGP = 0;
				}
				
				debuglog.info("[GP REWARD LOG] Scheduled delete. Player: " + playerId + ". Last: " + rank.getGp() + ". New: " + newGP);
				DAOManager.getDAO(AbyssRankDAO.class).updateGloryPoints(playerId, newGP);
			}
		}
	}
	
	/**
	 * Schedules the periodic update of abyss ranks.<br>
	 * This method checks if an immediate update is required based on {@code ServerVariablesDAO}.<br>
	 * It then uses {@link CronService} to schedule future updates using the rule from {@link RankingConfig}.
	 */
	public void scheduleUpdate()
	{
		final ServerVariablesDAO dao = DAOManager.getDAO(ServerVariablesDAO.class);
		final int nextTime = dao.load("abyssRankUpdate");
		if (nextTime < (System.currentTimeMillis() / 1000))
		{
			performUpdate();
		}
		
		log.debug("[AbyssRankUpdateService] Starting ranking update task based on cron expression: " + RankingConfig.TOP_RANKING_UPDATE_RULE);
		CronService.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				performUpdate();
			}
		}, RankingConfig.TOP_RANKING_UPDATE_RULE, true);
	}
	
	/**
	 * Executes the periodic update of player abyss ranks.<br>
	 * This method iterates through all players to refresh their rank data.<br>
	 * It also updates limited ranks and reloads the {@link AbyssRankingCache}.
	 */
	public void performUpdate()
	{
		log.debug("[AbyssRankUpdateService] executing rank update");
		final long startTime = System.currentTimeMillis();
		
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				player.getAbyssRank().doUpdate();
				DAOManager.getDAO(AbyssRankDAO.class).storeAbyssRank(player);
			}
		});
		
		updateLimitedRanks();
		updateLimitedGpRanks();
		AbyssRankingCache.getInstance().reloadRankings();
		log.debug("[AbyssRankUpdateService] execution time: " + ((System.currentTimeMillis() - startTime) / 1000));
	}
	
	/**
	 * Updates the ranks for limited races.<br>
	 * This method calls {@code updateAllRanksForRace} for both {@code ASMODIANS} and {@code ELYOS}.<br>
	 * It uses the required AP from {@code AbyssRankEnum#GRADE9_SOLDIER} and the offline limit from {@link RankingConfig}.
	 */
	private void updateLimitedRanks()
	{
		updateAllRanksForRace(Race.ASMODIANS, AbyssRankEnum.GRADE9_SOLDIER.getRequiredAp(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS);
		updateAllRanksForRace(Race.ELYOS, AbyssRankEnum.GRADE9_SOLDIER.getRequiredAp(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS);
	}
	
	/**
	 * Updates the GP ranks for both Asmodian and Elyos races.<br>
	 * This method uses the required GP from {@code STAR1_OFFICER}.<br>
	 * It also applies the maximum offline days limit from {@link RankingConfig}.
	 */
	private void updateLimitedGpRanks()
	{
		updateAllRanksGpForRace(Race.ASMODIANS, AbyssRankEnum.STAR1_OFFICER.getRequiredGp(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS);
		updateAllRanksGpForRace(Race.ELYOS, AbyssRankEnum.STAR1_OFFICER.getRequiredGp(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS);
	}
	
	/**
	 * Updates the ranks for all players in a specific {@code Race}.<br>
	 * This method loads AP data and sorts players based on their scores.<br>
	 * It then assigns grades to each player and updates those with no quota.
	 * @param race The {@code Race} type to process.
	 * @param apLimit The maximum AP limit for the ranking.
	 * @param activeAfterDays The number of days a player must be active to qualify.
	 */
	private void updateAllRanksForRace(Race race, int apLimit, int activeAfterDays)
	{
		final Map<Integer, Integer> playerApMap = DAOManager.getDAO(AbyssRankDAO.class).loadPlayersAp(race, apLimit, activeAfterDays);
		final List<Entry<Integer, Integer>> playerApEntries = new ArrayList<>(playerApMap.entrySet());
		Collections.sort(playerApEntries, new PlayerApComparator<Integer, Integer>());
		
		selectRank(AbyssRankEnum.GRADE1_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE2_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE3_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE4_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE5_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE6_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE7_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE8_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE9_SOLDIER, playerApEntries);
		
		updateToNoQuotaRank(playerApEntries);
	}
	
	/**
	 * Updates the GP ranks for all players in a specific {@link Race}.<br>
	 * This method loads player data based on the provided limits.<br>
	 * It then sorts the entries and assigns ranks using {@code selectGpRank}.<br>
	 * Finally, it updates any remaining players to the no quota rank.
	 * @param race The specific {@code Race} type to process.
	 * @param gpLimit The maximum GP limit for ranking.
	 * @param activeAfterDays The number of days a player must be active.
	 */
	private void updateAllRanksGpForRace(Race race, int gpLimit, int activeAfterDays)
	{
		final Map<Integer, Integer> playerGpMap = DAOManager.getDAO(AbyssRankDAO.class).loadPlayersGp(race, gpLimit, activeAfterDays);
		final List<Entry<Integer, Integer>> playerGpEntries = new ArrayList<>(playerGpMap.entrySet());
		Collections.sort(playerGpEntries, new PlayerGpComparator<Integer, Integer>());
		
		selectGpRank(AbyssRankEnum.SUPREME_COMMANDER, playerGpEntries);
		selectGpRank(AbyssRankEnum.COMMANDER, playerGpEntries);
		selectGpRank(AbyssRankEnum.GREAT_GENERAL, playerGpEntries);
		selectGpRank(AbyssRankEnum.GENERAL, playerGpEntries);
		selectGpRank(AbyssRankEnum.STAR5_OFFICER, playerGpEntries);
		selectGpRank(AbyssRankEnum.STAR4_OFFICER, playerGpEntries);
		selectGpRank(AbyssRankEnum.STAR3_OFFICER, playerGpEntries);
		selectGpRank(AbyssRankEnum.STAR2_OFFICER, playerGpEntries);
		selectGpRank(AbyssRankEnum.STAR1_OFFICER, playerGpEntries);
		
		updateToNoQuotaGpRank(playerGpEntries);
	}
	
	/**
	 * Selects players to be assigned a specific {@link AbyssRankEnum}.<br>
	 * It checks the {@code playerApEntries} list against the rank's quota.<br>
	 * Players are updated using {@code int)} if they meet the AP requirements.
	 * @param rank The target rank to assign to players.
	 * @param playerApEntries A list of entries containing player IDs and their current AP values.
	 */
	private void selectRank(AbyssRankEnum rank, List<Entry<Integer, Integer>> playerApEntries)
	{
		final int quota = rank.getId() < 9 ? (rank.getQuota() - AbyssRankEnum.getRankById(rank.getId() + 1).getQuota()) : rank.getQuota();
		for (int i = 0; i < quota; i++)
		{
			if (playerApEntries.isEmpty())
			{
				return;
			}
			
			// check next player in list
			final Entry<Integer, Integer> playerAp = playerApEntries.get(0);
			
			// check if there are some players left in map
			if (playerAp == null)
			{
				return;
			}
			
			final int playerId = playerAp.getKey();
			final int ap = playerAp.getValue();
			
			// check if this (and the rest) player has required ap count
			if (ap < rank.getRequiredAp())
			{
				return;
			}
			
			// remove player and update its rank
			playerApEntries.remove(0);
			updateRankTo(rank, playerId);
		}
	}
	
	/**
	 * This method selects players for a specific {@link AbyssRankEnum} based on their GP scores.<br>
	 * It iterates through the provided list of player entries to find those who meet the required GP.<br>
	 * The selection is limited by the quota defined for the rank.
	 * @param rank The {@link AbyssRankEnum} type being processed.
	 * @param playerGpEntries A list of {@link Entry} objects containing player IDs and their corresponding GP values.
	 */
	private void selectGpRank(AbyssRankEnum rank, List<Entry<Integer, Integer>> playerGpEntries)
	{
		final int quota = ((rank.getId() > 9) && (rank.getId() < 18)) ? rank.getQuota() - AbyssRankEnum.getRankById(rank.getId() + 1).getQuota() : rank.getQuota();
		for (int i = 0; i < quota; i++)
		{
			if (playerGpEntries.isEmpty())
			{
				return;
			}
			
			// check next player in list
			final Entry<Integer, Integer> playerGp = playerGpEntries.get(0);
			
			// check if there are some players left in map
			if (playerGp == null)
			{
				return;
			}
			
			final int playerId = playerGp.getKey();
			final int gp = playerGp.getValue();
			
			// check if this (and the rest) player has required gp count
			if (gp < rank.getRequiredGp())
			{
				return;
			}
			
			// remove player and update its rankGp
			playerGpEntries.remove(0);
			updateGpRankTo(rank, playerId);
		}
	}
	
	/**
	 * Updates the rank of all players in the provided list.<br>
	 * Each player is set to the {@code GRADE1_SOLDIER} rank.<br>
	 * This method handles transitions for entries with no quota requirements.
	 * @param playerApEntries A list of {@code Entry<Integer, Integer>} containing player IDs and their AP values.
	 */
	private void updateToNoQuotaRank(List<Entry<Integer, Integer>> playerApEntries)
	{
		for (Entry<Integer, Integer> playerApEntry : playerApEntries)
		{
			updateRankTo(AbyssRankEnum.GRADE1_SOLDIER, playerApEntry.getKey());
		}
	}
	
	/**
	 * Updates the rank of all players in the provided list to {@code SUPREME_COMMANDER}.<br>
	 * This method iterates through each {@code Entry} and calls {@code int)}.
	 * @param playerGpEntries A list of entries containing player IDs and their GP values.
	 */
	private void updateToNoQuotaGpRank(List<Entry<Integer, Integer>> playerGpEntries)
	{
		for (Entry<Integer, Integer> playerGpEntry : playerGpEntries)
		{
			updateGpRankTo(AbyssRankEnum.SUPREME_COMMANDER, playerGpEntry.getKey());
		}
	}
	
	/**
	 * Updates the abyss rank for a specific player.<br>
	 * This method checks if the player is online to update their live data.<br>
	 * If the player is offline, it updates the rank in the database directly.
	 * @param newRank The {@code AbyssRankEnum} value to assign to the player.
	 * @param playerId The unique identifier of the player to update.
	 */
	protected void updateRankTo(AbyssRankEnum newRank, int playerId)
	{
		// check if rank is changed for online players
		final Player onlinePlayer = World.getInstance().findPlayer(playerId);
		if (onlinePlayer != null)
		{
			final AbyssRank abyssRank = onlinePlayer.getAbyssRank();
			final AbyssRankEnum currentRank = abyssRank.getRank();
			if (currentRank != newRank)
			{
				abyssRank.setRank(newRank);
				AbyssPointsService.checkRankChanged(onlinePlayer, currentRank, newRank);
			}
		}
		else
		{
			DAOManager.getDAO(AbyssRankDAO.class).updateAbyssRank(playerId, newRank);
		}
	}
	
	/**
	 * Updates the GP rank for a specific player.<br>
	 * This method checks if the player is online to update their live data.<br>
	 * If the player is offline, it updates the rank in the database.
	 * @param newRank The {@code AbyssRankEnum} value to assign.
	 * @param playerId The unique ID of the player.
	 */
	protected void updateGpRankTo(AbyssRankEnum newRank, int playerId)
	{
		// check if rankGp is changed for online players
		final Player onlinePlayer = World.getInstance().findPlayer(playerId);
		if (onlinePlayer != null)
		{
			final AbyssRank abyssRank = onlinePlayer.getAbyssRank();
			final AbyssRankEnum currentRank = abyssRank.getRank();
			if (currentRank != newRank)
			{
				abyssRank.setRank(newRank);
				AbyssPointsService.checkRankGpChanged(onlinePlayer, currentRank, newRank);
			}
		}
		else
		{
			DAOManager.getDAO(AbyssRankDAO.class).updateAbyssRank(playerId, newRank);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final AbyssRankUpdateService instance = new AbyssRankUpdateService();
	}
	
	private static class PlayerApComparator<K, V extends Comparable<V>> implements Comparator<Entry<K, V>>
	{
		@Override
		public int compare(Entry<K, V> o1, Entry<K, V> o2)
		{
			return -o1.getValue().compareTo(o2.getValue()); // descending order
		}
	}
	
	private static class PlayerGpComparator<K, V extends Comparable<V>> implements Comparator<Entry<K, V>>
	{
		@Override
		public int compare(Entry<K, V> o1, Entry<K, V> o2)
		{
			return -o1.getValue().compareTo(o2.getValue()); // descending order
		}
	}
}
