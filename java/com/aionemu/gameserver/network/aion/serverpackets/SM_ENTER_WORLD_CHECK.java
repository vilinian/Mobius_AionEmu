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

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * Handles the server-side check when a player attempts to enter the game world.<br>
 * This packet validates the connection state before allowing the character to join.
 * @author -Nemesiss-
 */
public class SM_ENTER_WORLD_CHECK extends AionServerPacket
{
	private byte msg = 0x00;
	
	/**
	 * This method creates a new {@code SM_ENTER_WORLD_CHECK} packet.<br>
	 * It initializes the packet with a specific message byte.
	 * @param msg The message identifier to be sent in the packet.
	 */
	public SM_ENTER_WORLD_CHECK(byte msg)
	{
		this.msg = msg;
	}
	
	/**
	 * This method creates a new instance of the {@code SM_ENTER_WORLD_CHECK} packet.<br>
	 * It is used to handle world entry checks for players.<br>
	 * Use this constructor when no specific message byte is required.
	 */
	public SM_ENTER_WORLD_CHECK()
	{
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(msg);
		writeC(0x00);
		writeC(0x00);
	}
}
