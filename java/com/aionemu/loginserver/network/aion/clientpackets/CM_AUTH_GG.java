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

import com.aionemu.loginserver.network.aion.AionAuthResponse;
import com.aionemu.loginserver.network.aion.AionClientPacket;
import com.aionemu.loginserver.network.aion.LoginConnection;
import com.aionemu.loginserver.network.aion.LoginConnection.State;
import com.aionemu.loginserver.network.aion.serverpackets.SM_AUTH_GG;
import com.aionemu.loginserver.network.aion.serverpackets.SM_LOGIN_FAIL;

/**
 * This packet handles the "Good Game" signal from the client during the authentication process.<br>
 * It is used to transition the connection state after a successful login sequence.
 * @author -Nemesiss-
 */
public class CM_AUTH_GG extends AionClientPacket
{
	/**
	 * session id - its should match sessionId that was send in Init packet.
	 */
	private int sessionId;
	
	/*
	 * private final int data1; private final int data2; private final int data3; private final int data4;
	 */
	/**
	 * Creates a new instance of the {@code CM_AUTH_GG} packet.<br>
	 * This constructor initializes the packet using data from a buffer and a connection.
	 * @param buf The {@code java.nio.ByteBuffer} containing the raw packet data.
	 * @param client The {@link LoginConnection} object representing the current client.
	 */
	public CM_AUTH_GG(java.nio.ByteBuffer buf, LoginConnection client)
	{
		super(buf, client, 0x07);
	}
	
	@Override
	protected void readImpl()
	{
		sessionId = readD();
		readB(27);
	}
	
	@Override
	protected void runImpl()
	{
		final LoginConnection con = getConnection();
		if (con.getSessionId() == sessionId)
		{
			con.setState(State.AUTHED_GG);
			con.sendPacket(new SM_AUTH_GG(sessionId));
		}
		else
		{
			/**
			 * Session id is not ok - inform client that smth went wrong - dc client
			 */
			con.close(new SM_LOGIN_FAIL(AionAuthResponse.SYSTEM_ERROR), false);
		}
	}
}
