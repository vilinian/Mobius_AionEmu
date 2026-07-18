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

import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.mail.MailService;

/**
 * Handles the client request to send a mail message.<br>
 * This packet processes the data sent by the player to deliver a letter via {@link MailService}.
 * @author Aion Gates, xTz
 */
public class CM_SEND_MAIL extends AionClientPacket
{
	private String recipientName;
	private String title;
	private String message;
	private int itemObjId;
	private long itemCount;
	private long kinahCount;
	private int idLetterType;
	
	/**
	 * This method initializes a new {@code CM_SEND_MAIL} packet.<br>
	 * It sets the required network states for the mail sending action.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates A variable number of additional connection states.
	 */
	public CM_SEND_MAIL(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		recipientName = readS();
		title = readS();
		message = readS();
		itemObjId = readD();
		itemCount = readQ();
		kinahCount = readQ();
		idLetterType = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (!player.isTrading() && (kinahCount < 1000000000) && (kinahCount > -1) && (itemCount > -2))
		{
			MailService.getInstance().sendMail(player, recipientName, title, message, itemObjId, itemCount, kinahCount, LetterType.getLetterTypeById(idLetterType));
		}
	}
}
