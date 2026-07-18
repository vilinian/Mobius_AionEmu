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
package com.aionemu.gameserver.services.ranking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerRankingDAO;
import com.aionemu.gameserver.model.ranking.PlayerRankingEnum;
import com.aionemu.gameserver.model.ranking.PlayerRankingResult;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RANK_LIST;

/**
 * This service handles the logic for updating player rankings in the game.<br>
 * It manages data synchronization between the database and the active ranking lists.<br>
 * Use this class to refresh {@link PlayerRankingResult} data across the server.
 */
public class PlayerRankingUpdateService
{
	private static final Logger log = LoggerFactory.getLogger(PlayerRankingUpdateService.class);
	private int lastUpdate;
	private final Map<Integer, List<SM_RANK_LIST>> players = new ConcurrentHashMap<>();
	
	/**
	 * Starts the ranking update service.<br>
	 * This method refreshes the rankings for both Arena of Discipline and Arena of Cooperation.<br>
	 * It logs a message once the data is loaded.
	 */
	public void onStart()
	{
		renewPlayerRanking(PlayerRankingEnum.ARENA_OF_DISCIPLINE.getId());
		renewPlayerRanking(PlayerRankingEnum.ARENA_OF_COOPERATION.getId());
		log.info("[PlayerRankingUpdateService] Player Ranking Loaded");
	}
	
	/**
	 * Updates the ranking data for a specific table.<br>
	 * This method refreshes the {@code players} map with new results.<br>
	 * It calls {@code loadRankPacket} to fetch the latest data.
	 * @param tableId The unique identifier of the ranking table to update.
	 */
	private void renewPlayerRanking(int tableId)
	{
		List<SM_RANK_LIST> newlyCalculated;
		newlyCalculated = loadRankPacket(tableId);
		players.remove(tableId);
		players.put(tableId, newlyCalculated);
		log.info("[PlayerRankingUpdateService] Player Ranking Updated");
	}
	
	/**
	 * Fetches ranking data from the database for a specific table.<br>
	 * This method converts {@link PlayerRankingResult} objects into {@code SM_RANK_LIST} packets.<br>
	 * It splits the results into chunks of 94 items to create multiple pages.
	 * @param tableid The unique identifier for the ranking table.
	 * @return A list of {@code SM_RANK_LIST} packets containing the ranked players.
	 */
	private List<SM_RANK_LIST> loadRankPacket(int tableid)
	{
		final ArrayList<PlayerRankingResult> list = DAOManager.getDAO(PlayerRankingDAO.class).getCompetitionRankingPlayers(tableid);
		
		// int page = 1;
		final List<SM_RANK_LIST> playerPackets = new ArrayList<>();
		for (int i = 0; i < list.size(); i += 94)
		{
			if (list.size() > (i + 94))
			{
				playerPackets.add(new SM_RANK_LIST(tableid, 0, list.subList(i, i + 94), lastUpdate));
				playerPackets.add(new SM_RANK_LIST(tableid, 1, list.subList(i, i + 94), lastUpdate));
			}
			else
			{
				playerPackets.add(new SM_RANK_LIST(tableid, 0, list.subList(i, list.size()), lastUpdate));
				playerPackets.add(new SM_RANK_LIST(tableid, 1, list.subList(i, list.size()), lastUpdate));
			}
			
			// page++;
		}
		
		return playerPackets;
	}
	
	/**
	 * Retrieves the list of ranked players for a specific table.<br>
	 * This method looks up the data using the provided {@code tableId}.
	 * @param tableId The unique identifier for the ranking table.
	 * @return A {@code List} of {@link SM_RANK_LIST} objects, or {@code null} if no data exists.
	 */
	public List<SM_RANK_LIST> getPlayers(int tableId)
	{
		return players.get(tableId);
	}
	
	/**
	 * Gets the single shared instance of this service.<br>
	 * This follows the singleton design pattern.<br>
	 * Use this method to access the {@link PlayerRankingUpdateService}.
	 * @return The global instance of {@code PlayerRankingUpdateService}.
	 */
	public static PlayerRankingUpdateService getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PlayerRankingUpdateService INSTANCE = new PlayerRankingUpdateService();
	}
}
