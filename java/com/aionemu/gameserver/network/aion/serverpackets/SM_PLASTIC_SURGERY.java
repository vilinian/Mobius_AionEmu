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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the request for a character to undergo plastic surgery.<br>
 * It processes the data sent by the client to update the player's appearance.
 * @author IlBuono
 */
public class SM_PLASTIC_SURGERY extends AionServerPacket
{
	private final int playerObjId;
	private final byte check_ticket;
	private final byte change_sex;
	
	/**
	 * Handles the plastic surgery request for a player.<br>
	 * This packet processes gender changes and ticket verification.
	 * @param player The {@link Player} object who is requesting the surgery.
	 * @param check_ticket A {@code byte} representing the validation ticket.
	 * @param change_sex A {@code byte} indicating the target sex for the character.
	 */
	public SM_PLASTIC_SURGERY(Player player, byte check_ticket, byte change_sex)
	{
		playerObjId = player.getObjectId();
		this.check_ticket = check_ticket;
		this.change_sex = change_sex;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(playerObjId);
		writeC(check_ticket);
		writeC(change_sex);
	}
}
