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

import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;

/**
 * This packet is used by the {@code LoginServer} to request that an account be kicked from the {@code GameServer}.<br>
 * It facilitates communication between servers to disconnect specific users.
 * @author -Nemesiss-
 */
public class SM_REQUEST_KICK_ACCOUNT extends GsServerPacket
{
	/**
	 * Account that must be kicked at GameServer side.
	 */
	private final int accountId;
	
	/**
	 * This packet requests the {@code GameServer} to kick a specific account.<br>
	 * It identifies the target user by their unique ID.
	 * @param accountId The unique identifier of the account to be kicked.
	 */
	public SM_REQUEST_KICK_ACCOUNT(int accountId)
	{
		this.accountId = accountId;
	}
	
	@Override
	protected void writeImpl(GsConnection con)
	{
		writeC(2);
		writeD(accountId);
	}
}
