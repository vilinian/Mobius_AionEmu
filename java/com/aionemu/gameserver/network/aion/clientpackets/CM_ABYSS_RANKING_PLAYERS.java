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
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank.AbyssRankUpdateType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANKING_PLAYERS;
import com.aionemu.gameserver.services.abyss.AbyssRankingCache;

/**
 * Handles the client request to retrieve the ranking of players in the Abyss.<br>
 * This packet communicates with {@link AbyssRankingCache} to fetch and send the relevant data.
 * @author SheppeR
 */
public class CM_ABYSS_RANKING_PLAYERS extends AionClientPacket
{
	private Race queriedRace;
	private int raceId;
	private AbyssRankUpdateType updateType;
	private static final Logger log = LoggerFactory.getLogger(CM_ABYSS_RANKING_PLAYERS.class);
	
	/**
	 * This method initializes a new {@code CM_ABYSS_RANKING_PLAYERS} packet.<br>
	 * It sets the required network states for the client communication.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the player.
	 * @param restStates Additional connection states for the packet.
	 */
	public CM_ABYSS_RANKING_PLAYERS(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		raceId = readC();
		switch (raceId)
		{
			case 0:
				queriedRace = Race.ELYOS;
				updateType = AbyssRankUpdateType.PLAYER_ELYOS;
				break;
			case 1:
				queriedRace = Race.ASMODIANS;
				updateType = AbyssRankUpdateType.PLAYER_ASMODIANS;
				break;
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (queriedRace != null)
		{
			final Player player = getConnection().getActivePlayer();
			if (player.isAbyssRankListUpdated(updateType))
			{
				sendPacket(new SM_ABYSS_RANKING_PLAYERS(AbyssRankingCache.getInstance().getLastUpdate(), queriedRace));
			}
			else
			{
				final List<SM_ABYSS_RANKING_PLAYERS> results = AbyssRankingCache.getInstance().getPlayers(queriedRace);
				for (SM_ABYSS_RANKING_PLAYERS packet : results)
				{
					sendPacket(packet);
				}
				
				player.setAbyssRankListUpdated(updateType);
			}
		}
		else
		{
			log.warn("Received invalid raceId: " + raceId);
		}
	}
}
