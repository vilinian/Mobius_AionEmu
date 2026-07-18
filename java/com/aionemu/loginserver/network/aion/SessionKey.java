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
package com.aionemu.loginserver.network.aion;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.loginserver.model.Account;

/**
 * This class represents a unique session key used to identify and manage active user connections.<br>
 * It helps the {@link Account} system track authenticated sessions during login operations.
 * @author -Nemesiss-
 */
public class SessionKey
{
	/**
	 * accountId - will be used for authentication on Game Server side.
	 */
	public final int accountId;
	/**
	 * login ok key
	 */
	public final int loginOk;
	/**
	 * play ok1 key
	 */
	public final int playOk1;
	/**
	 * play ok2 key
	 */
	public final int playOk2;
	
	/**
	 * Creates a new {@link SessionKey} instance for a specific account.<br>
	 * This method generates random keys for the login and play status.<br>
	 * It uses the ID from the provided {@code Account} object.
	 * @param acc The {@code Account} used to initialize this session key.
	 */
	public SessionKey(Account acc)
	{
		accountId = acc.getId();
		loginOk = Rnd.nextInt();
		playOk1 = Rnd.nextInt();
		playOk2 = Rnd.nextInt();
	}
	
	/**
	 * Creates a new {@link SessionKey} instance.<br>
	 * This constructor initializes the session with specific security keys.
	 * @param accountId The unique identifier for the user account.
	 * @param loginOk The verification key for the login process.
	 * @param playOk1 The first verification key for gameplay.
	 * @param playOk2 The second verification key for gameplay.
	 */
	public SessionKey(int accountId, int loginOk, int playOk1, int playOk2)
	{
		this.accountId = accountId;
		this.loginOk = loginOk;
		this.playOk1 = playOk1;
		this.playOk2 = playOk2;
	}
	
	/**
	 * Verifies if the provided credentials match the current session.<br>
	 * This method compares both {@code accountId} and {@code loginOk}.
	 * @param accountId The ID of the account to check.
	 * @param loginOk The status key for the login attempt.
	 * @return {@code true} if both values match, otherwise {@code false}.
	 */
	public boolean checkLogin(int accountId, int loginOk)
	{
		return (this.accountId == accountId) && (this.loginOk == loginOk);
	}
	
	/**
	 * Verifies if the provided {@code SessionKey} is valid.<br>
	 * It compares all internal values against the current session data.
	 * @param key The {@code SessionKey} object to validate.
	 * @return {@code true} if all keys match, otherwise {@code false}.
	 */
	public boolean checkSessionKey(SessionKey key)
	{
		return ((playOk1 == key.playOk1) && (accountId == key.accountId) && (playOk2 == key.playOk2) && (loginOk == key.loginOk));
	}
}
