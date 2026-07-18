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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.FriendList.Status;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SEARCH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

/**
 * This packet is received from the client when a player performs a search using the social search panel.<br>
 * It handles requests to find other players based on specific criteria.
 * @author Ben
 */
public class CM_PLAYER_SEARCH extends AionClientPacket
{
	/**
	 * The max number of players to return as results
	 */
	public static final int MAX_RESULTS = 104; // 3.0
	private String name;
	private int region;
	private int classMask;
	private int minLevel;
	private int maxLevel;
	private int lfgOnly;
	
	/**
	 * This method initializes a new {@code CM_PLAYER_SEARCH} packet.<br>
	 * It sets the required network states for the request.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if needed.
	 */
	public CM_PLAYER_SEARCH(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		name = readS(52);
		if (name != null)
		{
			name = Util.convertName(name);
		}
		
		region = readD();
		classMask = readD();
		minLevel = readC();
		maxLevel = readC();
		lfgOnly = readC();
		readC(); // 0x00 in search pane 0x30 in /who?
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		
		final Iterator<Player> it = World.getInstance().getPlayersIterator();
		
		final List<Player> matches = new ArrayList<>(MAX_RESULTS);
		
		if (activePlayer.getLevel() < CustomConfig.LEVEL_TO_SEARCH)
		{
			sendPacket(SM_SYSTEM_MESSAGE.STR_CANT_WHO_LEVEL(String.valueOf(CustomConfig.LEVEL_TO_SEARCH)));
			return;
		}
		while (it.hasNext() && (matches.size() < MAX_RESULTS))
		{
			final Player player = it.next();
			if (!player.isSpawned())
			{
				continue;
			}
			else if (player.getFriendList().getStatus() == Status.OFFLINE)
			{
				continue;
			}
			else if (player.isGM() && !CustomConfig.SEARCH_GM_LIST)
			{
				continue;
			}
			else if ((lfgOnly == 1) && !player.isLookingForGroup())
			{
				continue;
			}
			else if (!name.isEmpty() && !player.getName().toLowerCase().contains(name.toLowerCase()))
			{
				continue;
			}
			else if ((minLevel != 0xFF) && (player.getLevel() < minLevel))
			{
				continue;
			}
			else if ((maxLevel != 0xFF) && (player.getLevel() > maxLevel))
			{
				continue;
			}
			else if ((classMask > 0) && ((player.getPlayerClass().getMask() & classMask) == 0))
			{
				continue;
			}
			else if ((region > 0) && (player.getActiveRegion().getMapId() != region))
			{
				continue;
			}
			else if ((player.getRace() != activePlayer.getRace()) && !CustomConfig.FACTIONS_SEARCH_MODE)
			{
				continue;
			}
			else if (player.getName() == activePlayer.getName())
			{
				continue;
			}
			else // This player matches criteria
			{
				matches.add(player);
			}
		}
		
		sendPacket(new SM_PLAYER_SEARCH(matches, region));
	}
}
