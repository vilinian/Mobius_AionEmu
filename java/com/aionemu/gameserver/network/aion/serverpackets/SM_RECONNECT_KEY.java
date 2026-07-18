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
 * This packet serves as the response to {@code CM_RECONNECT_AUTH}.<br>
 * It contains a unique key used for authentication at the {@code LoginServer}.
 * @author -Nemesiss-
 */
public class SM_RECONNECT_KEY extends AionServerPacket
{
	/**
	 * key for reconnection - will be used for authentication
	 */
	private final int key;
	
	/**
	 * Creates a new {@code SM_RECONNECT_KEY} packet.<br>
	 * This packet holds the authentication key for reconnection.
	 * @param key The unique integer used to authenticate the connection.
	 */
	public SM_RECONNECT_KEY(int key)
	{
		this.key = key;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(0x00);
		writeD(key);
	}
}
