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
 * This packet informs the {@code LoginServer} that a specific account has been disconnected from the {@code GameServer}.<br>
 * It is used to synchronize connection states between the two servers.
 * @author -Nemesiss-
 */
public class SM_ACCOUNT_DISCONNECTED extends LsServerPacket
{
	/**
	 * AccountId of account that is no longer on GameServer.
	 */
	private final int accountId;
	
	/**
	 * Creates a new packet to notify the login server that an account has disconnected.<br>
	 * This is used when a {@code GameServer} loses connection with a specific user.
	 * @param accountId The unique identifier of the disconnected account.
	 */
	public SM_ACCOUNT_DISCONNECTED(int accountId)
	{
		super(0x03);
		this.accountId = accountId;
	}
	
	@Override
	protected void writeImpl(LoginServerConnection con)
	{
		writeD(accountId);
	}
}
