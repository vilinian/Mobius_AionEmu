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
package com.aionemu.gameserver.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.AbyssRankingResult;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

/**
 * This class provides data access operations for {@link AbyssRank} objects.<br>
 * It handles the persistence and retrieval of abyss ranking information from the database.
 * @author ATracer
 * @rework Phantom_KNA
 */
public abstract class AbyssRankDAO implements DAO
{
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return AbyssRankDAO.class.getName();
	}
	
	public abstract List<Integer> RankPlayers(int rank); // DailyReduceGp
	
	public abstract void updateGloryPoints(int playerId, int gp); // DailyReduceGp
	
	public abstract void loadAbyssRank(Player player);
	
	public abstract AbyssRank loadAbyssRank(int playerId);
	
	public abstract boolean storeAbyssRank(Player player);
	
	public abstract ArrayList<AbyssRankingResult> getAbyssRankingPlayers(Race race, int maxOfflineDays);
	
	public abstract ArrayList<AbyssRankingResult> getAbyssRankingLegions(Race race);
	
	public abstract Map<Integer, Integer> loadPlayersAp(Race race, int lowerApLimit, int maxOfflineDays);
	
	public abstract Map<Integer, Integer> loadPlayersGp(Race race, int lowerGpLimit, int maxOfflineDays);
	
	public abstract void updateAbyssRank(int playerId, AbyssRankEnum rankEnum);
	
	public abstract void updateRankList(int maxOfflineDays);
}
