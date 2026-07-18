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
 * This class represents an unknown server packet with the identifier {@code 7E}.<br>
 * It is used to handle specific data received from the client that does not have a dedicated handler.<br>
 * It extends {@link AionServerPacket} to integrate into the network communication system.
 * @author Falke_34
 */
public class SM_UNK_7E extends AionServerPacket
{
	private final int value;
	
	/**
	 * Creates a new instance of {@code SM_UNK_7E}.<br>
	 * This constructor initializes the packet with a specific integer.
	 * @param value The integer value to be stored in this packet.
	 */
	public SM_UNK_7E(int value)
	{
		this.value = value;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(value);
		writeC(1);
	}
}
