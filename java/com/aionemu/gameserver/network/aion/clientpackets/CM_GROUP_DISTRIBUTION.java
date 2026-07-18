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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.restrictions.RestrictionsManager;

/**
 * Handles the distribution of group information to clients.<br>
 * This packet is used to synchronize group status and members between the server and the client.
 * @author Lyahim, Simple, xTz
 */
public class CM_GROUP_DISTRIBUTION extends AionClientPacket
{
	private long amount;
	private int partyType;
	
	/**
	 * This constructor initializes a new {@link CM_GROUP_DISTRIBUTION} packet.<br>
	 * It sets the required network states for the packet.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates A variable number of additional states for the connection.
	 */
	public CM_GROUP_DISTRIBUTION(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		amount = readQ();
		partyType = readC();
	}
	
	@Override
	protected void runImpl()
	{
		if (amount < 2)
		{
			return;
		}
		
		final Player player = getConnection().getActivePlayer();
		
		if (!RestrictionsManager.canTrade(player))
		{
			return;
		}
		
		switch (partyType)
		{
			case 1:
				if (player.isInAlliance2())
				{
					PlayerAllianceService.distributeKinahInGroup(player, amount);
				}
				else
				{
					PlayerGroupService.distributeKinah(player, amount);
				}
				break;
			case 2:
				PlayerAllianceService.distributeKinah(player, amount);
				break;
		}
	}
}
