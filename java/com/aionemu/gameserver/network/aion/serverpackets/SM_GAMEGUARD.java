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
 * This packet handles communication related to the {@code GAMEGUARD} anti-cheat system.<br>
 * It is used by the server to manage security checks and integrity verification.
 * @author Alcapwnd
 */
public class SM_GAMEGUARD extends AionServerPacket
{
	private final int size;
	
	/**
	 * Creates a new instance of {@link SM_GAMEGUARD}.<br>
	 * This constructor initializes the packet with a specific size.
	 * @param size The size value to be assigned to this packet.
	 */
	public SM_GAMEGUARD(int size)
	{
		this.size = size;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(size);
		writeB(new byte[size]);
	}
}
