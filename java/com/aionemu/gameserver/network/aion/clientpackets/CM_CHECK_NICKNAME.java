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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CREATE_CHARACTER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NICKNAME_CHECK_RESPONSE;
import com.aionemu.gameserver.services.NameRestrictionService;
import com.aionemu.gameserver.services.player.PlayerService;

/**
 * This packet handles the request from the {@code aion} client to check if a specific nickname is available.<br>
 * It verifies whether the provided name is free or violates any server restrictions.
 * @author -Nemesiss-
 * @modified cura
 */
public class CM_CHECK_NICKNAME extends AionClientPacket
{
	/**
	 * nick name that need to be checked
	 */
	private String nick;
	
	/**
	 * This method handles the request to check if a nickname is available.<br>
	 * It initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} of the connection.
	 * @param restStates Additional {@link State} objects for the connection.
	 */
	public CM_CHECK_NICKNAME(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		nick = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final AionConnection client = getConnection();
		
		if (!PlayerService.isFreeName(nick) || PlayerService.isOldName(nick))
		{
			if (GSConfig.CHARACTER_CREATION_MODE == 2)
			{
				client.sendPacket(new SM_NICKNAME_CHECK_RESPONSE(SM_CREATE_CHARACTER.RESPONSE_NAME_RESERVED));
			}
			else
			{
				client.sendPacket(new SM_NICKNAME_CHECK_RESPONSE(SM_CREATE_CHARACTER.RESPONSE_NAME_ALREADY_USED));
			}
		}
		else if (!NameRestrictionService.isValidName(nick))
		{
			client.sendPacket(new SM_NICKNAME_CHECK_RESPONSE(SM_CREATE_CHARACTER.RESPONSE_INVALID_NAME));
		}
		else if (NameRestrictionService.isForbiddenWord(nick))
		{
			client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_FORBIDDEN_CHAR_NAME));
		}
		else
		{
			client.sendPacket(new SM_NICKNAME_CHECK_RESPONSE(SM_CREATE_CHARACTER.RESPONSE_OK));
		}
	}
}
