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
 * This packet notifies the client about specific package information.<br>
 * It is used to synchronize data regarding game packages between the server and the client.
 * @author Rolandas
 */
public class SM_PACKAGE_INFO_NOTIFY extends AionServerPacket
{
	private final int count;
	private int packId;
	private int time;
	
	/**
	 * Creates a new {@code SM_PACKAGE_INFO_NOTIFY} packet.<br>
	 * This constructor initializes the packet with a specific quantity.
	 * @param count The number of items to include in the notification.
	 */
	public SM_PACKAGE_INFO_NOTIFY(int count)
	{
		this.count = count;
	}
	
	/**
	 * This constructor initializes a new {@code SM_PACKAGE_INFO_NOTIFY} packet.<br>
	 * It sets the required notification data for the server.
	 * @param count The total number of items or packages.
	 * @param packId The unique identifier for the package.
	 * @param time The timestamp associated with the notification.
	 */
	public SM_PACKAGE_INFO_NOTIFY(int count, int packId, int time)
	{
		this.count = count;
		this.packId = packId;
		this.time = time;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(count);
		if (count > 0)
		{
			writeC(packId); // packId?
			writeD(time); // Gold-packet Expire
		}
	}
}
