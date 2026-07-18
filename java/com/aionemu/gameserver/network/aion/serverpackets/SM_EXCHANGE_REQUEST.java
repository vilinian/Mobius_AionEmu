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
 * This packet handles requests sent from the client to initiate an exchange with another player.<br>
 * It contains the necessary data to identify the target and the items involved in the trade.
 * @author -Avol-
 */
public class SM_EXCHANGE_REQUEST extends AionServerPacket
{
	private final String receiver;
	
	/**
	 * Creates a new {@code SM_EXCHANGE_REQUEST} packet.<br>
	 * This packet is used to initiate an exchange request with another player.
	 * @param receiver The unique identifier of the target player.
	 */
	public SM_EXCHANGE_REQUEST(String receiver)
	{
		this.receiver = receiver;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeS(receiver);
	}
}
