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
package com.aionemu.gameserver.network.loginserver.serverpackets;

import com.aionemu.gameserver.network.loginserver.LoginServerConnection;
import com.aionemu.gameserver.network.loginserver.LsServerPacket;

/**
 * This packet is sent by the {@code GameServer} when a player requests a fast reconnect to the login server.<br>
 * The {@code LoginServer} will respond with a unique reconnect key.
 * @author -Nemesiss-
 */
public class SM_ACCOUNT_RECONNECT_KEY extends LsServerPacket
{
	/**
	 * AccountId of client that is requested reconnection to LoginServer.
	 */
	private final int accountId;
	
	/**
	 * Creates a new {@code SM_ACCOUNT_RECONNECT_KEY} packet.<br>
	 * This is used when a player requests a fast reconnect to the login server.
	 * @param accountId The unique identifier for the client account.
	 */
	public SM_ACCOUNT_RECONNECT_KEY(int accountId)
	{
		super(0x02);
		this.accountId = accountId;
	}
	
	@Override
	protected void writeImpl(LoginServerConnection con)
	{
		writeD(accountId);
	}
}
