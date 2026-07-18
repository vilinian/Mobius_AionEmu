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
package com.aionemu.loginserver.network.aion.serverpackets;

import com.aionemu.loginserver.network.aion.AionServerPacket;
import com.aionemu.loginserver.network.aion.LoginConnection;
import com.aionemu.loginserver.network.aion.SessionKey;

/**
 * This packet is sent to the client to confirm a successful login.<br>
 * It informs the user that their credentials have been verified and they can proceed.<br>
 * It inherits from {@link AionServerPacket}.
 * @author -Nemesiss-
 */
public class SM_LOGIN_OK extends AionServerPacket
{
	/**
	 * accountId is part of session key - its used for security purposes
	 */
	private final int accountId;
	/**
	 * loginOk is part of session key - its used for security purposes
	 */
	private final int loginOk;
	
	/**
	 * Creates a new {@code SM_LOGIN_OK} packet.<br>
	 * This method initializes the packet using data from a {@link SessionKey}.<br>
	 * It sets the internal security fields for the login process.
	 * @param key The {@code SessionKey} containing account details.
	 */
	public SM_LOGIN_OK(SessionKey key)
	{
		super(3);
		accountId = key.accountId;
		loginOk = key.loginOk;
	}
	
	@Override
	protected void writeImpl(LoginConnection con)
	{
		writeD(accountId);
		writeD(loginOk);
		writeD(0);
		writeD(0);
		writeD(1002);
		writeD(126282165);
		writeB(new byte[47]);
	}
}
