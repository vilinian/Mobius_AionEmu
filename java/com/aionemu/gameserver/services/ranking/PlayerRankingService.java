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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerRankingDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.ranking.ArenaOfCooperationRank;
import com.aionemu.gameserver.model.gameobjects.player.ranking.ArenaOfDisciplineRank;
import com.aionemu.gameserver.model.ranking.PlayerRankingEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MY_DOCUMENTATION;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service manages the retrieval and processing of player rankings.<br>
 * It handles data for various competitive modes like {@link ArenaOfCooperationRank} and {@link ArenaOfDisciplineRank}.<br>
 * Use this class to interact with {@link PlayerRankingDAO} to fetch or update ranking information.
 */
public class PlayerRankingService
{
	// private int lastUpdate;
	// private final Map<Integer, List<SM_RANK_LIST>> players = new HashMap<>();
	
	/**
	 * Loads the ranking data for a specific player based on a table ID.<br>
	 * This method identifies the correct score to load using the {@code tableid}.<br>
	 * It calls either {@code loadArenaOfDisciplineScore} or {@code loadArenaOfCooperationScore} depending on the value.
	 * @param player The {@code Player} object for whom the data is being loaded.
	 * @param tableid The unique identifier for the ranking table.
	 */
	public void loadPacketPlayer(Player player, int tableid)
	{
		switch (tableid)
		{
			case 541:
			{
				loadArenaOfDisciplineScore(player);
				break;
			}
			case 741:
			{
				loadArenaOfCooperationScore(player);
				break;
			}
			default:
				break;
		}
	}
	
	/**
	 * Loads the Arena of Discipline rank for a specific player.<br>
	 * This method fetches data from the database using {@link PlayerRankingDAO}.<br>
	 * It updates the {@code Player} object and sends a notification packet.
	 * @param player The {@code Player} whose ranking needs to be loaded.
	 */
	public void loadArenaOfDisciplineScore(Player player)
	{
		final ArenaOfDisciplineRank rank = DAOManager.getDAO(PlayerRankingDAO.class).loadArenaOfDisciplineRank(player.getObjectId(), PlayerRankingEnum.ARENA_OF_DISCIPLINE.getId());
		player.setDisciplineRank(rank);
		PacketSendUtility.sendPacket(player, new SM_MY_DOCUMENTATION(PlayerRankingEnum.ARENA_OF_DISCIPLINE.getId(), player.getDisciplineRank()));
	}
	
	/**
	 * Loads the {@code ArenaOfCooperationRank} for a specific player.<br>
	 * This method retrieves the rank from the database.<br>
	 * It then updates the {@link Player} object with the new data.<br>
	 * Finally, it sends a documentation packet to the player.
	 * @param player The {@code Player} whose ranking needs to be loaded.
	 */
	public void loadArenaOfCooperationScore(Player player)
	{
		final ArenaOfCooperationRank rank = DAOManager.getDAO(PlayerRankingDAO.class).loadArenaOfCooperationRank(player.getObjectId(), PlayerRankingEnum.ARENA_OF_COOPERATION.getId());
		player.setCooperationRank(rank);
		PacketSendUtility.sendPacket(player, new SM_MY_DOCUMENTATION(PlayerRankingEnum.ARENA_OF_COOPERATION.getId(), player.getCooperationRank()));
	}
	
	/**
	 * Gets the global instance of the {@link PlayerRankingService}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access ranking features throughout the application.
	 * @return The single instance of {@code PlayerRankingService}.
	 */
	public static PlayerRankingService getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PlayerRankingService INSTANCE = new PlayerRankingService();
	}
}
