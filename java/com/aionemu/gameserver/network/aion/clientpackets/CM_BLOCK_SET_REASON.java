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

import com.aionemu.gameserver.model.gameobjects.player.BlockedPlayer;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.SocialService;

/**
 * This packet handles the request to set a reason for blocking a player.<br>
 * It allows a {@link Player} to specify why they are blocking another user.<br>
 * The server processes this request via the {@link SocialService}.
 * @author Ben
 */
public class CM_BLOCK_SET_REASON extends AionClientPacket
{
	String targetName;
	String reason;
	
	/**
	 * This method creates a new {@code CM_BLOCK_SET_REASON} packet.<br>
	 * It initializes the packet with specific network states.<br>
	 * Use this to set the reason for blocking a player.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_BLOCK_SET_REASON(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		targetName = readS();
		reason = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		final BlockedPlayer target = activePlayer.getBlockList().getBlockedPlayer(targetName);
		
		if (target == null)
		{
			sendPacket(SM_SYSTEM_MESSAGE.STR_BLOCKLIST_NOT_IN_LIST);
		}
		else
		{
			SocialService.setBlockedReason(activePlayer, target, reason);
		}
	}
}
