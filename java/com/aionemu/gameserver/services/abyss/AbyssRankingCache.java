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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.main.RankingConfig;
import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.model.AbyssRankingResult;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANKING_LEGIONS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANKING_PLAYERS;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.world.World;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides an in-memory cache for {@link AbyssRankingResult} data.<br>
 * It improves performance by reducing frequent database queries to the {@link AbyssRankDAO}.<br>
 * The cache stores rankings for different {@link Race} types and legions.
 * @author VladimirZ
 */
public class AbyssRankingCache
{
	private int lastUpdate;
	private final Map<Race, List<SM_ABYSS_RANKING_PLAYERS>> players = new ConcurrentHashMap<>();
	private final Map<Race, SM_ABYSS_RANKING_LEGIONS> legions = new ConcurrentHashMap<>();
	
	/**
	 * Refreshes the abyss ranking data from the database.<br>
	 * This method updates both player and legion rankings for all races.<br>
	 * It also resets the update status for all players in the {@link World}.
	 */
	public void reloadRankings()
	{
		GameServer.log.debug("[AbyssRankingCache] Updating abyss ranking cache");
		lastUpdate = (int) (System.currentTimeMillis() / 1000);
		getDAO().updateRankList(RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS);
		
		renewPlayerRanking(Race.ASMODIANS);
		renewPlayerRanking(Race.ELYOS);
		
		renewLegionRanking();
		
		World.getInstance().doOnAllPlayers(player -> player.resetAbyssRankListUpdated());
	}
	
	/**
	 * Updates the cached legion rankings from the database.<br>
	 * This method clears existing data and populates it for both {@code Race.ELYOS} and {@code Race.ASMODIANS}.<br>
	 * It also triggers a ranking update via {@code getInstance}.
	 */
	private void renewLegionRanking()
	{
		final Map<Integer, Integer> newLegionRankingCache = new HashMap<>();
		final ArrayList<AbyssRankingResult> elyosRanking = getDAO().getAbyssRankingLegions(Race.ELYOS), asmoRanking = getDAO().getAbyssRankingLegions(Race.ASMODIANS);
		
		legions.clear();
		legions.put(Race.ASMODIANS, new SM_ABYSS_RANKING_LEGIONS(lastUpdate, asmoRanking, Race.ASMODIANS));
		legions.put(Race.ELYOS, new SM_ABYSS_RANKING_LEGIONS(lastUpdate, elyosRanking, Race.ELYOS));
		
		for (AbyssRankingResult result : elyosRanking)
		{
			newLegionRankingCache.put(result.getLegionId(), result.getRankPos());
		}
		
		for (AbyssRankingResult result : asmoRanking)
		{
			newLegionRankingCache.put(result.getLegionId(), result.getRankPos());
		}
		
		LegionService.getInstance().performRankingUpdate(newLegionRankingCache);
	}
	
	/**
	 * Updates the player ranking cache for a specific race.<br>
	 * This method calls {@code generatePacketsForRace} to get new data.<br>
	 * It then replaces the old list in the {@code players} map with the new results.
	 * @param race The {@code Race} type to update.
	 */
	private void renewPlayerRanking(Race race)
	{
		// delete not before new list is created
		List<SM_ABYSS_RANKING_PLAYERS> newlyCalculated;
		newlyCalculated = generatePacketsForRace(race);
		players.remove(race);
		players.put(race, newlyCalculated);
	}
	
	/**
	 * Creates a list of ranking packets for a specific {@code Race}.<br>
	 * This method fetches data from the database and splits it into pages.<br>
	 * Each page contains up to 44 players.
	 * @param race The {@code Race} type to generate rankings for.
	 * @return A {@code List} of {@link SM_ABYSS_RANKING_PLAYERS} packets.
	 */
	private List<SM_ABYSS_RANKING_PLAYERS> generatePacketsForRace(Race race)
	{
		// players orderd by ap
		final ArrayList<AbyssRankingResult> list = getDAO().getAbyssRankingPlayers(race, RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS);
		int page = 1;
		final List<SM_ABYSS_RANKING_PLAYERS> playerPackets = new ArrayList<>();
		for (int i = 0; i < list.size(); i += 44)
		{
			if (list.size() > (i + 44))
			{
				playerPackets.add(new SM_ABYSS_RANKING_PLAYERS(lastUpdate, list.subList(i, i + 44), race, page, false));
			}
			else
			{
				playerPackets.add(new SM_ABYSS_RANKING_PLAYERS(lastUpdate, list.subList(i, list.size()), race, page, true));
			}
			
			page++;
		}
		
		return playerPackets;
	}
	
	/**
	 * Retrieves the cached ranking list for a specific race.<br>
	 * This method returns the {@code SM_ABYSS_RANKING_PLAYERS} objects associated with the provided {@code Race}.
	 * @param race The {@code Race} type to filter the rankings by.
	 * @return A {@code List} of {@code SM_ABYSS_RANKING_PLAYERS} for the given race.
	 */
	public List<SM_ABYSS_RANKING_PLAYERS> getPlayers(Race race)
	{
		return players.get(race);
	}
	
	/**
	 * Retrieves the legion rankings for a specific race.<br>
	 * This method fetches data from the internal cache.
	 * @param race The {@code Race} type to filter the rankings by.
	 * @return The {@code SM_ABYSS_RANKING_LEGIONS} object containing the ranking data.
	 */
	public SM_ABYSS_RANKING_LEGIONS getLegions(Race race)
	{
		return legions.get(race);
	}
	
	/**
	 * Retrieves the timestamp of the most recent update.<br>
	 * This value is stored in seconds since the epoch.
	 * @return The {@code int} value representing the last update time.
	 */
	public int getLastUpdate()
	{
		return lastUpdate;
	}
	
	/**
	 * Retrieves the {@link AbyssRankDAO} instance from the database manager.<br>
	 * This method provides access to the data access object for abyss rankings.
	 * @return The {@code AbyssRankDAO} instance.
	 */
	private AbyssRankDAO getDAO()
	{
		return DAOManager.getDAO(AbyssRankDAO.class);
	}
	
	/**
	 * Provides the global instance of the {@link AbyssRankingCache}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access ranking data from anywhere in the application.
	 * @return The single instance of {@code AbyssRankingCache}.
	 */
	public static AbyssRankingCache getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AbyssRankingCache INSTANCE = new AbyssRankingCache();
	}
}
