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
 * This packet handles the megaphone system in the game.<br>
 * It is used to broadcast messages from a {@link Player} to other nearby players.
 * @author Ever'
 * @reworked Alcapwnd
 */
public class SM_MEGAPHONE extends AionServerPacket
{
	private final Player player;
	private final String message;
	private final int itemId;
	private final boolean isAll;
	
	/**
	 * Creates a new megaphone packet for a specific player.<br>
	 * This packet handles the display of a public or private message.
	 * @param player The {@link Player} who is sending the message.
	 * @param message The text content to be displayed in the megaphone.
	 * @param itemId The unique identifier for the item used to trigger the megaphone.
	 * @param isAll A boolean where {@code true} sends to everyone and {@code false} sends to a specific group.
	 */
	public SM_MEGAPHONE(Player player, String message, int itemId, boolean isAll)
	{
		this.player = player;
		this.message = message;
		this.itemId = itemId;
		this.isAll = isAll;
	}
	
	@Override
	protected void writeImpl(AionConnection client)
	{
		writeS(player.getName());
		writeS(message);
		writeD(itemId);
		writeC(isAll ? player.getRace().getRaceId() : 255);
	}
}
