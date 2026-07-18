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
 * This packet handles the transmission of character data from the game server to the client.<br>
 * It is used during the login process to synchronize character information.
 * @author cura
 */
public class SM_GS_CHARACTER extends LsServerPacket
{
	private final int accountId;
	private final int characterCount;
	
	/**
	 * Creates a new {@link SM_GS_CHARACTER} packet.<br>
	 * This packet sends character information to the client.
	 * @param accountId The unique identifier for the user account.
	 * @param characterCount The total number of characters owned by the account.
	 */
	public SM_GS_CHARACTER(final int accountId, int characterCount)
	{
		super(0x08);
		this.accountId = accountId;
		this.characterCount = characterCount;
	}
	
	@Override
	protected void writeImpl(LoginServerConnection con)
	{
		writeD(accountId);
		writeC(characterCount);
	}
}
