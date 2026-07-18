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
package com.aionemu.gameserver.network.loginserver.clientpackets;

import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.network.loginserver.LsClientPacket;

/**
 * This packet represents the {@link LoginServer} response to a {@code SM_ACCOUNT_RECONNECT_KEY} request.<br>
 * It contains the account name and the unique {@code reconnectionKey}.
 * @author -Nemesiss-
 */
public class CM_ACCOUNT_RECONNECT_KEY extends LsClientPacket
{
	/**
	 * This method creates a new instance of {@code CM_ACCOUNT_RECONNECT_KEY}.<br>
	 * It initializes the packet with the provided operation code.
	 * @param opCode The unique identifier for this specific network operation.
	 */
	public CM_ACCOUNT_RECONNECT_KEY(int opCode)
	{
		super(opCode);
	}
	
	/**
	 * accountId of account that will be reconnecting.
	 */
	private int accountId;
	/**
	 * ReconnectKey that will be used for authentication.
	 */
	private int reconnectKey;
	
	@Override
	public void readImpl()
	{
		accountId = readD();
		reconnectKey = readD();
	}
	
	@Override
	public void runImpl()
	{
		LoginServer.getInstance().authReconnectionResponse(accountId, reconnectKey);
	}
}
