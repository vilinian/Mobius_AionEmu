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
 * This packet handles the response from the login server regarding control requests.<br>
 * It is sent to the game server to confirm or deny specific administrative actions.
 * @author Aionchs-Wylovech
 */
public class SM_LS_CONTROL_RESPONSE extends GsServerPacket
{
	private final int type;
	private final boolean result;
	private final String playerName;
	private final int param;
	private final String adminName;
	private final int accountId;
	
	/**
	 * Creates a new {@code SM_LS_CONTROL_RESPONSE} packet.<br>
	 * This constructor initializes the response data for the game server.
	 * @param type The type of control action.
	 * @param result The success status of the operation as {@code true} or {@code false}.
	 * @param playerName The name of the player involved.
	 * @param accountId The unique identifier for the account.
	 * @param param An additional parameter for the control action.
	 * @param adminName The name of the administrator performing the action.
	 */
	public SM_LS_CONTROL_RESPONSE(int type, boolean result, String playerName, int accountId, int param, String adminName)
	{
		this.type = type;
		this.result = result;
		this.playerName = playerName;
		this.param = param;
		this.adminName = adminName;
		this.accountId = accountId;
	}
	
	@Override
	protected void writeImpl(GsConnection con)
	{
		writeC(4);
		writeC(type);
		writeC(result ? 1 : 0);
		writeS(adminName);
		writeS(playerName);
		writeC(param);
		writeD(accountId);
	}
}
