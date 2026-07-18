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

import com.aionemu.loginserver.controller.AccountTimeController;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.gameserver.GsClientPacket;

/**
 * This packet informs the {@link com.aionemu.loginserver.LoginServer} that an account has been disconnected from the GameServer.<br>
 * It is used to synchronize the connection status between the two servers.
 * @author -Nemesiss-
 */
public class CM_ACCOUNT_DISCONNECTED extends GsClientPacket
{
	/**
	 * AccountId of account that was disconnected form GameServer.
	 */
	private int accountId;
	
	@Override
	protected void readImpl()
	{
		accountId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Account account = getConnection().getGameServerInfo().removeAccountFromGameServer(accountId);
		
		/**
		 * account can be null if a player logged out from gs {@link CM_ACCOUNT_RECONNECT_KEY
		 */
		if (account != null)
		{
			AccountTimeController.updateOnLogout(account);
		}
	}
}
