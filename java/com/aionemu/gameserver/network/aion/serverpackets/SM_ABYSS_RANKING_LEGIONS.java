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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aionemu.gameserver.model.AbyssRankingResult;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the transmission of Abyss ranking data for different legions.<br>
 * It allows the client to display the leaderboard results provided by {@link AbyssRankingResult}.
 * @author zdead, LokiReborn
 */
public class SM_ABYSS_RANKING_LEGIONS extends AionServerPacket
{
	private final List<AbyssRankingResult> data;
	private final Race race;
	private final int updateTime;
	private int sendData = 0;
	
	/**
	 * Creates a new {@link SM_ABYSS_RANKING_LEGIONS} packet.<br>
	 * This packet contains ranking information for specific legions.<br>
	 * It initializes the data with the provided results and race type.
	 * @param updateTime The timestamp of the last update.
	 * @param data The list of {@link AbyssRankingResult} objects to include.
	 * @param race The {@link Race} category for these rankings.
	 */
	public SM_ABYSS_RANKING_LEGIONS(int updateTime, ArrayList<AbyssRankingResult> data, Race race)
	{
		this.updateTime = updateTime;
		this.data = data;
		this.race = race;
		sendData = 1;
	}
	
	/**
	 * Creates a new ranking packet for legions.<br>
	 * This method initializes the packet with an empty data list.<br>
	 * It is used to send abyss rankings to the client.
	 * @param updateTime The timestamp of the last update.
	 * @param race The specific {@link Race} type for the ranking.
	 */
	public SM_ABYSS_RANKING_LEGIONS(int updateTime, Race race)
	{
		this.updateTime = updateTime;
		data = Collections.emptyList();
		this.race = race;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(race.getRaceId()); // 0:Elyos 1:Asmo
		writeD(updateTime); // Date
		writeD(sendData); // 0:Nothing 1:Update Table
		writeD(sendData); // 0:Nothing 1:Update Table
		writeH(data.size()); // list size
		for (AbyssRankingResult rs : data)
		{
			writeD(rs.getRankPos()); // Current Rank
			writeD((rs.getOldRankPos() == 0) ? 76 : rs.getOldRankPos()); // Old Rank
			writeD(rs.getLegionId()); // Legion Id
			writeD(race.getRaceId()); // 0:Elyos 1:Asmo
			writeC(rs.getLegionLevel()); // Legion Level
			writeD(rs.getLegionMembers()); // Legion Members
			writeQ(rs.getLegionCP()); // Contribution Points
			writeS(rs.getLegionName(), 82); // Legion Name
		}
	}
}
