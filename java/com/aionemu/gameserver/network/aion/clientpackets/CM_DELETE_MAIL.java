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
import com.aionemu.gameserver.services.mail.MailService;

/**
 * Handles the request from a client to delete a specific mail message.<br>
 * It interacts with {@link MailService} to remove the selected item from the player's mailbox.
 * @author kosyachok
 */
public class CM_DELETE_MAIL extends AionClientPacket
{
	int[] mailObjId;
	
	/**
	 * This method creates a new {@code CM_DELETE_MAIL} packet.<br>
	 * It initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_DELETE_MAIL(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		final int count = readC();
		mailObjId = new int[count];
		for (int i = 0; i < count; i++)
		{
			readC(); // unk
			mailObjId[i] = readD();
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		MailService.getInstance().deleteMail(player, mailObjId);
	}
}
