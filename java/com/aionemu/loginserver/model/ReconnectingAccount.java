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
package com.aionemu.loginserver.model;

import com.aionemu.gameserver.GameServer;

/**
 * This class stores an {@link com.aionemu.loginserver.model.Account} and its associated {@code reconnectionKey}.<br>
 * It is used to manage clients reconnecting to the {@code LoginServer} from a {@code GameServer} using the fast reconnect feature.
 * @author -Nemesiss-
 */
public class ReconnectingAccount
{
	/**
	 * Account object of account that will be reconnecting.
	 */
	private final Account account;
	/**
	 * Reconnection Key that will be used for authenticating
	 */
	private final int reconnectionKey;
	
	/**
	 * Creates a new {@code ReconnectingAccount} object.<br>
	 * This is used for the fast reconnect feature between the {@link GameServer} and {@code LoginServer}.
	 * @param account The {@code Account} object of the user who is reconnecting.
	 * @param reconnectionKey The unique integer key used to authenticate the connection.
	 */
	public ReconnectingAccount(Account account, int reconnectionKey)
	{
		this.account = account;
		this.reconnectionKey = reconnectionKey;
	}
	
	/**
	 * Retrieves the {@link Account} associated with this connection.
	 * @return The current {@code Account} object.
	 */
	public Account getAccount()
	{
		return account;
	}
	
	/**
	 * Retrieves the unique key used for fast reconnection.<br>
	 * This key allows a client to reconnect from the {@link GameServer}.
	 * @return The {@code int} value of the reconnection key.
	 */
	public int getReconnectionKey()
	{
		return reconnectionKey;
	}
}
