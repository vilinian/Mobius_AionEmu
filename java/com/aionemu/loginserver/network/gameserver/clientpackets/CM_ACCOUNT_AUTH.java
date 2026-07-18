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
package com.aionemu.loginserver.network.gameserver.clientpackets;

import com.aionemu.loginserver.controller.AccountController;
import com.aionemu.loginserver.network.aion.SessionKey;
import com.aionemu.loginserver.network.gameserver.GsClientPacket;

/**
 * This packet is used by the {@code Gameserver} to verify an account's session key with the {@code Loginserver}.<br>
 * It checks if a user authenticating on the {@code Gameserver} has already been successfully authenticated on the {@code Loginserver} side.
 * @author -Nemesiss-
 */
public class CM_ACCOUNT_AUTH extends GsClientPacket
{
	/**
	 * SessionKey that GameServer needs to check if is valid at Loginserver side.
	 */
	private SessionKey sessionKey;
	
	@Override
	protected void readImpl()
	{
		final int accountId = readD();
		final int loginOk = readD();
		final int playOk1 = readD();
		final int playOk2 = readD();
		
		sessionKey = new SessionKey(accountId, loginOk, playOk1, playOk2);
	}
	
	@Override
	protected void runImpl()
	{
		AccountController.checkAuth(sessionKey, getConnection());
	}
}
