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
 * This packet handles the exchange of data between group members.<br>
 * It is used to synchronize information within a party or group structure.
 * @author xTz
 */
public class SM_GROUP_DATA_EXCHANGE extends AionServerPacket
{
	private final byte[] byteData;
	private final int action;
	private int unk2;
	
	/**
	 * This constructor initializes a new {@code SM_GROUP_DATA_EXCHANGE} packet.<br>
	 * It sets the required data fields for group communication.
	 * @param byteData The raw byte array containing the packet payload.
	 * @param action The specific action type to be performed.
	 * @param unk2 An unknown integer value used by the server protocol.
	 */
	public SM_GROUP_DATA_EXCHANGE(byte[] byteData, int action, int unk2)
	{
		this.action = action;
		this.byteData = byteData;
		this.unk2 = unk2;
	}
	
	/**
	 * Creates a new {@code SM_GROUP_DATA_EXCHANGE} packet.<br>
	 * This constructor sets the default action to {@code 1}.<br>
	 * It initializes the packet with the provided raw data.
	 * @param byteData The raw bytes contained in the packet.
	 */
	public SM_GROUP_DATA_EXCHANGE(byte[] byteData)
	{
		action = 1;
		this.byteData = byteData;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(action); // action
		
		if (action != 1)
		{
			writeC(unk2); // unk
		}
		
		writeD(byteData.length);
		writeB(byteData);
	}
}
