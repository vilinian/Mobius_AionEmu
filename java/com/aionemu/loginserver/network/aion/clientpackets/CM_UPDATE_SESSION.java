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
package com.aionemu.loginserver.network.aion.clientpackets;

import java.nio.ByteBuffer;

import com.aionemu.loginserver.controller.AccountController;
import com.aionemu.loginserver.network.aion.AionClientPacket;
import com.aionemu.loginserver.network.aion.LoginConnection;

/**
 * This packet is sent when a client transitions from the game server back to the login server.<br>
 * It handles the session update during a reconnection process.
 * @author -Nemesiss-
 */
public class CM_UPDATE_SESSION extends AionClientPacket
{
	/**
	 * accountId is part of session key - its used for security purposes
	 */
	private int accountId;
	/**
	 * loginOk is part of session key - its used for security purposes
	 */
	private int loginOk;
	/**
	 * reconectKey is key that server sends to client for fast reconnection to login server - we will check if this key is valid.
	 */
	private int reconnectKey;
	
	/**
	 * This method initializes a new {@code CM_UPDATE_SESSION} packet.<br>
	 * It processes the incoming data from the buffer and links it to the specific client.<br>
	 * Use this constructor when receiving session update information during reconnection.
	 * @param buf The {@code ByteBuffer} containing the raw packet data.
	 * @param client The {@link LoginConnection} object representing the connected user.
	 */
	public CM_UPDATE_SESSION(ByteBuffer buf, LoginConnection client)
	{
		super(buf, client, 0x08);
	}
	
	@Override
	protected void readImpl()
	{
		accountId = readD();
		loginOk = readD();
		reconnectKey = readD();
	}
	
	@Override
	protected void runImpl()
	{
		AccountController.authReconnectingAccount(accountId, loginOk, reconnectKey, getConnection());
	}
}
