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

import java.sql.Timestamp;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the synchronization of time between the client and the server.<br>
 * It ensures that both sides are using a consistent timestamp for game events.
 * @author -Nemesiss-
 */
public class SM_TIME_CHECK extends AionServerPacket
{
	// Do not be fooled by the empty class; this packet only sends an opcode without any content, as version 1.5.x sends 8 bytes.
	private final int nanoTime;
	private final int time;
	private final Timestamp dateTime;
	
	/**
	 * Creates a new {@code SM_TIME_CHECK} packet.<br>
	 * This method initializes the internal time values based on the current system clock.
	 * @param nanoTime The nanosecond value to be stored in the packet.
	 */
	public SM_TIME_CHECK(int nanoTime)
	{
		dateTime = new Timestamp((new java.util.Date()).getTime());
		this.nanoTime = nanoTime;
		time = (int) dateTime.getTime();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(time);
		writeD(nanoTime);
	}
}
