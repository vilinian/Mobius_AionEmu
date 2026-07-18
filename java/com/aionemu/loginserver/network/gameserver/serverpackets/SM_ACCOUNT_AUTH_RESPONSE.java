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
package com.aionemu.loginserver.network.gameserver.serverpackets;

import com.aionemu.loginserver.model.AccountTime;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;

/**
 * This packet represents the response from the {@code LoginServer} to a {@code GameServer} request.<br>
 * It confirms whether the authentication data is valid and provides the account name of the authenticating user.
 * @author -Nemesiss-
 */
public class SM_ACCOUNT_AUTH_RESPONSE extends GsServerPacket
{
	/**
	 * Account id
	 */
	private final int accountId;
	/**
	 * True if account is authenticated.
	 */
	private final boolean ok;
	/**
	 * account name
	 */
	private final String accountName;
	/**
	 * Access level
	 */
	private final byte accessLevel;
	/**
	 * Membership
	 */
	private final byte membership;
	/**
	 * Toll
	 */
	private final long toll;
	/**
	 * luna
	 */
	private final long luna;
	
	private final byte isReturn;
	
	/**
	 * Creates a new {@link SM_ACCOUNT_AUTH_RESPONSE} packet.<br>
	 * This constructor initializes the authentication data for an account.
	 * @param accountId The unique identifier for the account.
	 * @param ok A boolean indicating if the authentication was successful.
	 * @param accountName The name of the user account.
	 * @param accessLevel The permission level assigned to the account.
	 * @param membership The current membership status of the account.
	 * @param toll The toll value associated with the account.
	 * @param luna The luna value associated with the account.
	 * @param isReturn A flag indicating if this is a return packet.
	 */
	public SM_ACCOUNT_AUTH_RESPONSE(int accountId, boolean ok, String accountName, byte accessLevel, byte membership, long toll, long luna, byte isReturn)
	{
		this.accountId = accountId;
		this.ok = ok;
		this.accountName = accountName;
		this.accessLevel = accessLevel;
		this.membership = membership;
		this.toll = toll;
		this.luna = luna;
		this.isReturn = isReturn;
	}
	
	@Override
	protected void writeImpl(GsConnection con)
	{
		writeC(1);
		writeD(accountId);
		writeC(ok ? 1 : 0);
		
		if (ok)
		{
			writeS(accountName);
			
			final AccountTime accountTime = con.getGameServerInfo().getAccountFromGameServer(accountId).getAccountTime();
			
			writeQ(accountTime.getAccumulatedOnlineTime());
			writeQ(accountTime.getAccumulatedRestTime());
			writeC(accessLevel);
			writeC(membership);
			writeQ(toll);
			writeQ(luna);
			writeC(isReturn);
		}
	}
}
