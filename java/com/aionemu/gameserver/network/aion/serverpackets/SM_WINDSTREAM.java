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
 * This packet handles the wind stream data sent from the server to the client.<br>
 * It is used to synchronize environmental effects like wind direction and intensity.
 */
public class SM_WINDSTREAM extends AionServerPacket
{
	private final int unk1;
	private final int unk2;
	
	/**
	 * Constructs a new {@code SM_WINDSTREAM} packet.<br>
	 * This constructor initializes the internal state of the packet.
	 * @param unk1 The first unknown integer value for the packet.
	 * @param unk2 The second unknown integer value for the packet.
	 */
	public SM_WINDSTREAM(int unk1, int unk2)
	{
		this.unk1 = unk1;
		this.unk2 = unk2;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(unk1);
		writeC(unk2);
	}
}
