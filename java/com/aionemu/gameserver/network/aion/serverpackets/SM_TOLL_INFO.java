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
 * This packet handles the transmission of toll information to the client.<br>
 * It is used to notify players about specific costs or fees associated with game areas.
 * @author xTz
 */
public class SM_TOLL_INFO extends AionServerPacket
{
	private final long tollCount;
	
	/**
	 * Creates a new {@link SM_TOLL_INFO} packet.<br>
	 * This constructor initializes the toll count value.
	 * @param tollCount The number of tolls to be recorded.
	 */
	public SM_TOLL_INFO(long tollCount)
	{
		this.tollCount = tollCount;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeQ(tollCount);
	}
}
