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

import java.util.List;

import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.ranking.PlayerRankingResult;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the transmission of ranking lists to the client.<br>
 * It contains data for {@link PlayerRankingResult} objects sent from the server.
 */
public class SM_RANK_LIST extends AionServerPacket
{
	private final int tableId;
	private final int server_switch;
	private final List<PlayerRankingResult> data;
	private final int lastUpdate;
	
	/**
	 * This constructor initializes a new {@code SM_RANK_LIST} packet.<br>
	 * It prepares the ranking data to be sent to the client.
	 * @param tableId The unique identifier for the ranking table.
	 * @param s_switch The server switch value.
	 * @param data A {@code List} of {@link PlayerRankingResult} objects containing the rank details.
	 * @param lastUpdate The timestamp of the last update.
	 */
	public SM_RANK_LIST(int tableId, int s_switch, List<PlayerRankingResult> data, int lastUpdate)
	{
		this.tableId = tableId;
		this.data = data;
		this.lastUpdate = lastUpdate;
		server_switch = s_switch;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(tableId);
		writeC(server_switch);
		writeC(0);
		writeD(lastUpdate);
		writeH(data.size());
		for (PlayerRankingResult rs : data)
		{
			writeD(tableId);
			writeD(rs.getPoints());
			writeD(rs.getRank());
			writeD(rs.getOldRank());
			writeD(0); // Sex ? 0=male / 1=female
			writeD(rs.getPlayerId());
			writeD(rs.getPlayerRace());
			writeD(rs.getPlayerClass().getClassId());
			writeC(NetworkConfig.GAMESERVER_ID);
			writeS(rs.getPlayerName(), 52); // Player Name
		}
	}
}
