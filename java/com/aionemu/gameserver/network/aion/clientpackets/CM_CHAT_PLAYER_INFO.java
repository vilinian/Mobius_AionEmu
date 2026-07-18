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
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHAT_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * This packet handles requests from the client to retrieve information about a specific player.<br>
 * It is used to display player details within the chat interface.
 * @author prix
 */
public class CM_CHAT_PLAYER_INFO extends AionClientPacket
{
	private String playerName;
	
	/**
	 * This constructor initializes a new {@code CM_CHAT_PLAYER_INFO} packet.<br>
	 * It sets the required network information for the client communication.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the player.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_CHAT_PLAYER_INFO(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		playerName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		final Player target = World.getInstance().findPlayer(playerName);
		if (target == null)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ASK_PCINFO_LOGOFF);
			return;
		}
		
		if (!player.getKnownList().knowns(target))
		{
			PacketSendUtility.sendPacket(player, new SM_CHAT_WINDOW(target, false));
		}
	}
}
