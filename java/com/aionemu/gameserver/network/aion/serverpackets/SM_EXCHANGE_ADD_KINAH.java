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
 * This packet handles the addition of {@code Kinah} to a player's balance.<br>
 * It is used by the server to update currency amounts during an exchange.<br>
 * It extends {@link AionServerPacket}.
 * @author Avol
 */
public class SM_EXCHANGE_ADD_KINAH extends AionServerPacket
{
	private final long itemCount;
	private final int action;
	
	/**
	 * Creates a new {@code SM_EXCHANGE_ADD_KINAH} packet.<br>
	 * This packet is used to handle Kinah additions during an exchange.
	 * @param itemCount The number of items involved in the transaction.
	 * @param action The specific type of action being performed.
	 */
	public SM_EXCHANGE_ADD_KINAH(long itemCount, int action)
	{
		this.itemCount = itemCount;
		this.action = action;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(action); // 0 -self 1-other
		writeD((int) itemCount); // itemId
		writeD(0); // unk
	}
}
