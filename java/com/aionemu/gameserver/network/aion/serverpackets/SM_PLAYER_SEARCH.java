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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to populate the search panel within a player's social window.<br>
 * It serves as the server response to a {@code CM_PLAYER_SEARCH} request.
 * @author Ben
 */
public class SM_PLAYER_SEARCH extends AionServerPacket
{
	private static final Logger log = LoggerFactory.getLogger(SM_PLAYER_SEARCH.class);
	private final List<Player> players;
	private final int region;
	
	/**
	 * Creates a packet to populate the player search panel.<br>
	 * This is used in response to a {@link SM_PLAYER_SEARCH} request.
	 * @param players The list of {@code Player} objects to include in the results.
	 * @param region The specific region ID for the search.
	 */
	public SM_PLAYER_SEARCH(List<Player> players, int region)
	{
		this.players = new ArrayList<>(players);
		this.region = region;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(players.size());
		for (Player player : players)
		{
			if (player.getActiveRegion() == null)
			{
				log.warn("CHECKPOINT: null active region for " + player.getObjectId() + "-" + player.getX() + "-" + player.getY() + "-" + player.getZ());
			}
			
			writeD(player.getActiveRegion() == null ? region : player.getActiveRegion().getMapId());
			writeF(player.getPosition().getX());
			writeF(player.getPosition().getY());
			writeF(player.getPosition().getZ());
			writeC(player.getPlayerClass().getClassId());
			writeC(player.getGender().getGenderId());
			writeC(player.getLevel());
			if (player.isInGroup2())
			{
				writeC(3);
			}
			else if (player.isLookingForGroup())
			{
				writeC(2);
			}
			else
			{
				writeC(0);
			}
			
			writeS(player.getName(), 56);
			
		}
	}
}
