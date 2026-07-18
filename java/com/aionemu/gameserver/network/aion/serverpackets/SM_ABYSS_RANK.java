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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

/**
 * This packet handles the synchronization of a player's {@link AbyssRank} data.<br>
 * It is sent to the client to update the current rank information in the game world.
 * @author Nemiroff Date: 25.01.2010
 */
public class SM_ABYSS_RANK extends AionServerPacket
{
	private final AbyssRank rank;
	private final int currentRankId;
	
	/**
	 * Creates a new {@code SM_ABYSS_RANK} packet.<br>
	 * This constructor initializes the packet with a specific {@link AbyssRank}.<br>
	 * It automatically sets the internal rank ID based on the provided object.
	 * @param rank The {@code AbyssRank} to be included in the packet.
	 */
	public SM_ABYSS_RANK(AbyssRank rank)
	{
		this.rank = rank;
		currentRankId = rank.getRank().getId();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeQ(rank.getAp()); // curAP
		writeD(rank.getGp()); // curGP
		writeD(currentRankId); // curRank
		writeD(rank.getTopRanking()); // curRating
		
		if (currentRankId <= 9)
		{
			final int nextRankId = currentRankId < AbyssRankEnum.values().length ? currentRankId + 1 : currentRankId;
			writeD((100 * rank.getAp()) / AbyssRankEnum.getRankById(nextRankId).getRequiredAp());
		}
		else if ((currentRankId > 9) && (currentRankId <= 18))
		{
			final int nextGpRankId = currentRankId < AbyssRankEnum.values().length ? currentRankId + 1 : currentRankId;
			writeD((100 * rank.getGp()) / AbyssRankEnum.getRankById(nextGpRankId).getRequiredGp());
		}
		
		writeD(rank.getAllKill()); // allKill
		writeD(rank.getMaxRank()); // maxRank
		
		writeD(rank.getDailyKill()); // dayKill
		writeQ(rank.getDailyAP()); // dayAP
		writeD(rank.getDailyGP()); // dayGP
		
		writeD(rank.getWeeklyKill()); // weekKill
		writeQ(rank.getWeeklyAP()); // weekAP
		writeD(rank.getWeeklyGP()); // weekGP
		
		writeD(rank.getLastKill()); // laterKill
		writeQ(rank.getLastAP()); // laterAP
		writeD(rank.getLastGP()); // laterGP
		
		writeD(0); // unk 5.3
		writeD(0); // unk 5.3
		writeC(0); // unk
	}
}
