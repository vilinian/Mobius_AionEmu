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

import java.util.Map;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.loginserver.LoginServerConnection;
import com.aionemu.gameserver.network.loginserver.LsServerPacket;

/**
 * This packet is sent by the {@code GameServer} to provide a list of currently logged-in accounts.<br>
 * It helps the client display active users or manage account sessions.
 * @author SoulKeeper
 */
public class SM_ACCOUNT_LIST extends LsServerPacket
{
	/**
	 * Map with loaded accounts
	 */
	private final Map<Integer, AionConnection> accounts;
	
	/**
	 * Creates a new {@link SM_ACCOUNT_LIST} packet.<br>
	 * This method initializes the list of logged in accounts.
	 * @param accounts A map containing account IDs and their corresponding {@link AionConnection} objects.
	 */
	public SM_ACCOUNT_LIST(Map<Integer, AionConnection> accounts)
	{
		super(0x04);
		this.accounts = accounts;
	}
	
	@Override
	protected void writeImpl(LoginServerConnection con)
	{
		writeD(accounts.size());
		for (AionConnection ac : accounts.values())
		{
			writeS(ac.getAccount().getName());
		}
	}
}
