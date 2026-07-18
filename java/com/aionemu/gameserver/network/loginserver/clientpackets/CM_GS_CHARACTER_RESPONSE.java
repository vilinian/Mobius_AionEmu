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
package com.aionemu.gameserver.network.loginserver.clientpackets;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.network.loginserver.LsClientPacket;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_GS_CHARACTER;

/**
 * This packet handles the response from the game server regarding character data.<br>
 * It is sent to the client after a successful character selection or retrieval request.<br>
 * It contains information necessary for the client to initialize the character state.
 * @author cura
 */
public class CM_GS_CHARACTER_RESPONSE extends LsClientPacket
{
	/**
	 * This constructor initializes a new {@code CM_GS_CHARACTER_RESPONSE} packet.<br>
	 * It sets the operation code for the network communication.
	 * @param opCode The unique identifier for this specific operation.
	 */
	public CM_GS_CHARACTER_RESPONSE(int opCode)
	{
		super(opCode);
	}
	
	private int accountId;
	
	@Override
	public void readImpl()
	{
		accountId = readD();
	}
	
	@Override
	public void runImpl()
	{
		final int characterCount = DAOManager.getDAO(PlayerDAO.class).getCharacterCountOnAccount(accountId);
		sendPacket(new SM_GS_CHARACTER(accountId, characterCount));
	}
}
