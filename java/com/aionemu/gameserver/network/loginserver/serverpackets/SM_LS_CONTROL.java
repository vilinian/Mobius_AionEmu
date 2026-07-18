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
 * This packet handles control messages sent from the login server to the client.<br>
 * It is used to manage various server-side commands and synchronization tasks.
 * @author Aionchs-Wylovech
 */
public class SM_LS_CONTROL extends LsServerPacket
{
	private final String accountName;
	private final String adminName;
	private final String playerName;
	private final int param;
	private final int type;
	
	/**
	 * This constructor initializes a new {@code SM_LS_CONTROL} packet.<br>
	 * It sets the necessary data for login server control actions.
	 * @param accountName The name of the user account.
	 * @param playerName The name of the player character.
	 * @param adminName The name of the administrator performing the action.
	 * @param param A specific numeric parameter for the control command.
	 * @param type The type of control operation to be performed.
	 */
	public SM_LS_CONTROL(String accountName, String playerName, String adminName, int param, int type)
	{
		super(0x05);
		this.accountName = accountName;
		this.param = param;
		this.playerName = playerName;
		this.adminName = adminName;
		this.type = type;
		
	}
	
	@Override
	protected void writeImpl(LoginServerConnection con)
	{
		writeC(type);
		writeS(adminName);
		writeS(accountName);
		writeS(playerName);
		writeC(param);
	}
}
