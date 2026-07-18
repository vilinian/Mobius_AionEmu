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
 * This packet represents the response from the {@link com.aionemu.loginserver.network.gameserver.GsConnection} to a ban request.<br>
 * It is used by the LoginServer to acknowledge actions initiated by the GameServer.
 * @author Watson
 */
public class SM_BAN_RESPONSE extends GsServerPacket
{
	private final byte type;
	private final int accountId;
	private final String ip;
	private final int time;
	private final int adminObjId;
	private final boolean result;
	
	/**
	 * Constructs a new {@code SM_BAN_RESPONSE} packet.<br>
	 * This packet sends the result of a ban request back to the game server.
	 * @param type The packet type identifier.
	 * @param accountId The unique ID of the account being banned.
	 * @param ip The IP address associated with the account.
	 * @param time The duration or timestamp for the ban.
	 * @param adminObjId The ID of the administrator who performed the action.
	 * @param result The success status of the operation, either {@code true} or {@code false}.
	 */
	public SM_BAN_RESPONSE(byte type, int accountId, String ip, int time, int adminObjId, boolean result)
	{
		this.type = type;
		this.accountId = accountId;
		this.ip = ip;
		this.time = time;
		this.adminObjId = adminObjId;
		this.result = result;
	}
	
	@Override
	protected void writeImpl(GsConnection con)
	{
		writeC(5);
		writeC(type);
		writeD(accountId);
		writeS(ip);
		writeD(time);
		writeD(adminObjId);
		writeC(result ? 1 : 0);
	}
}
