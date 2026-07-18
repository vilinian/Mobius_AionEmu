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
 * This packet is used by the {@code Gameserver} to verify an account session key with the {@code LoginServer}.<br>
 * It checks if a user authenticating on the {@code Gameserver} has already been authenticated on the {@code Loginserver} side.
 * @author -Nemesiss-
 */
public class SM_ACCOUNT_AUTH extends LsServerPacket
{
	/**
	 * accountId [part of session key]
	 */
	private final int accountId;
	/**
	 * loginOk [part of session key]
	 */
	private final int loginOk;
	/**
	 * playOk1 [part of session key]
	 */
	private final int playOk1;
	/**
	 * playOk2 [part of session key]
	 */
	private final int playOk2;
	
	/**
	 * Creates a new instance of the {@code SM_ACCOUNT_AUTH} packet.<br>
	 * This packet is used to verify account session keys between servers.
	 * @param accountId The unique identifier for the account.
	 * @param loginOk The status of the login authentication.
	 * @param playOk1 The first status flag for play permissions.
	 * @param playOk2 The second status flag for play permissions.
	 */
	public SM_ACCOUNT_AUTH(int accountId, int loginOk, int playOk1, int playOk2)
	{
		super(0x01);
		this.accountId = accountId;
		this.loginOk = loginOk;
		this.playOk1 = playOk1;
		this.playOk2 = playOk2;
	}
	
	@Override
	protected void writeImpl(LoginServerConnection con)
	{
		writeD(accountId);
		writeD(loginOk);
		writeD(playOk1);
		writeD(playOk2);
	}
}
