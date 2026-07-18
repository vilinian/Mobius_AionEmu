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
package com.aionemu.loginserver.network.gameserver.serverpackets;

import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;

/**
 * This packet represents the {@code LoginServer} response to a {@code CM_ACCOUNT_RECONNECT_KEY} request.<br>
 * It contains the account name and the unique {@code reconnectionKey}.
 * @author -Nemesiss-
 */
public class SM_ACCOUNT_RECONNECT_KEY extends GsServerPacket
{
	/**
	 * accountId of account that will be reconnecting.
	 */
	private final int accountId;
	/**
	 * ReconnectKey that will be used for authentication.
	 */
	private final int reconnectKey;
	
	/**
	 * Creates a new {@code SM_ACCOUNT_RECONNECT_KEY} packet.<br>
	 * This packet is used to handle account reconnection requests.
	 * @param accountId The unique identifier for the account.
	 * @param reconnectKey The key used to authenticate the reconnection.
	 */
	public SM_ACCOUNT_RECONNECT_KEY(int accountId, int reconnectKey)
	{
		this.accountId = accountId;
		this.reconnectKey = reconnectKey;
	}
	
	@Override
	protected void writeImpl(GsConnection con)
	{
		writeC(3);
		writeD(accountId);
		writeD(reconnectKey);
	}
}
