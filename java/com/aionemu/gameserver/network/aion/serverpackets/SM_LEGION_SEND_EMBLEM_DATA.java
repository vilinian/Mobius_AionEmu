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
 * This packet sends emblem data to the client.<br>
 * It is used to synchronize legion-related emblems with the player's interface.
 * @author cura
 */
public class SM_LEGION_SEND_EMBLEM_DATA extends AionServerPacket
{
	private final int size;
	private final byte[] data;
	
	/**
	 * This method creates a new {@code SM_LEGION_SEND_EMBLEM_DATA} packet.<br>
	 * It initializes the packet with specific emblem information.
	 * @param size The size of the data array.
	 * @param data The byte array containing the emblem data.
	 */
	public SM_LEGION_SEND_EMBLEM_DATA(int size, byte[] data)
	{
		this.size = size;
		this.data = data;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(size);
		writeB(data);
	}
}
