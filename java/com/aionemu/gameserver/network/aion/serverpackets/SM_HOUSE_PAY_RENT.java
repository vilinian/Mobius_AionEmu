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

import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the payment of house rent by a player.<br>
 * It is sent from the server to notify the client about the rental transaction.
 * @author Rolandas
 */
public class SM_HOUSE_PAY_RENT extends AionServerPacket
{
	private final int weeksPaid;
	
	/**
	 * Creates a new {@code SM_HOUSE_PAY_RENT} packet.<br>
	 * This method sets the number of weeks paid for house rent.
	 * @param weeksPaid The number of weeks the rent is paid for.
	 */
	public SM_HOUSE_PAY_RENT(int weeksPaid)
	{
		this.weeksPaid = weeksPaid;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		PacketLoggerService.getInstance().logPacketSM(getPacketName());
		writeC(0);
		writeC(weeksPaid);
	}
}
