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
 * Handles the request from a client to retrieve an attachment from a mail item.<br>
 * This packet interacts with {@link MailService} to fetch and return the specific data.
 * @author kosyachok
 */
public class CM_GET_MAIL_ATTACHMENT extends AionClientPacket
{
	private int mailObjId;
	private int attachmentType;
	
	/**
	 * This method handles the request to retrieve a mail attachment.<br>
	 * It processes the packet sent from the client to the server.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} of the connection.
	 * @param restStates Additional {@link State} values for the connection.
	 */
	public CM_GET_MAIL_ATTACHMENT(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		mailObjId = readD();
		attachmentType = readC(); // 0 - item , 1 - kinah
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		MailService.getInstance().getAttachments(player, mailObjId, attachmentType);
	}
}
