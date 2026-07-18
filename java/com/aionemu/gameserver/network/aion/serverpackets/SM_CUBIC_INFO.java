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
 * This packet handles the transmission of cubic information to the client.<br>
 * It provides data regarding specific cubic objects within the game world.
 * @author FrozenKiller
 */
public class SM_CUBIC_INFO extends AionServerPacket
{
	private final int count;
	
	/**
	 * Creates a new {@code SM_CUBIC_INFO} packet.<br>
	 * This constructor initializes the packet with a specific quantity.
	 * @param count The number of items to include in the packet.
	 */
	public SM_CUBIC_INFO(int count)
	{
		this.count = count;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		for (int cubicId = 1; cubicId <= count; cubicId++)
		{
			// should be Cube 1 - 45
			writeD(cubicId); // Cube
			writeD(0); // Rank
			writeD(0); // Level
			writeD(0); // Count of Cubic to Register
		}
		
	}
}
