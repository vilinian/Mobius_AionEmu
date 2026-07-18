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
 * This class represents an unknown server packet with the identifier {@code 13B}.<br>
 * It is used to handle specific network communication received from the client.
 * @author Falke_34
 */
public class SM_UNK_13B extends AionServerPacket
{
	private final int value1;
	private final int value2;
	private final int value3;
	
	/**
	 * Creates a new instance of {@code SM_UNK_13B}.<br>
	 * This constructor initializes the packet with three integer values.
	 * @param value1 The first integer value for the packet.
	 * @param value2 The second integer value for the packet.
	 * @param value3 The third integer value for the packet.
	 */
	public SM_UNK_13B(int value1, int value2, int value3)
	{
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(value1);
		writeC(value2);
		writeC(value3);
	}
}
