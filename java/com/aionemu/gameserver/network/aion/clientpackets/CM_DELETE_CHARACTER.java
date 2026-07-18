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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.dao.PlayerPasskeyDAO;
import com.aionemu.gameserver.model.account.CharacterPasskey.ConnectType;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHARACTER_SELECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_CHARACTER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.player.PlayerService;

/**
 * This packet handles the request from the {@code Aion} client to delete a specific character.<br>
 * It processes the deletion logic and triggers the appropriate server responses.
 * @author -Nemesiss-
 */
public class CM_DELETE_CHARACTER extends AionClientPacket
{
	/**
	 * PlayOk2 - we dont care...
	 */
	@SuppressWarnings("unused")
	private int playOk2;
	/**
	 * ObjectId of character that should be deleted.
	 */
	private int chaOid;
	
	/**
	 * This method creates a new {@link CM_DELETE_CHARACTER} packet.<br>
	 * It is used when the client requests to delete a character.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the client.
	 * @param restStates Additional states associated with the connection.
	 */
	public CM_DELETE_CHARACTER(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		playOk2 = readD();
		chaOid = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final AionConnection client = getConnection();
		final PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(chaOid);
		
		if ((playerAccData != null) && !playerAccData.isLegionMember())
		{
			// passkey check
			if (SecurityConfig.PASSKEY_ENABLE && !client.getAccount().getCharacterPasskey().isPass())
			{
				client.getAccount().getCharacterPasskey().setConnectType(ConnectType.DELETE);
				client.getAccount().getCharacterPasskey().setObjectId(chaOid);
				final boolean isExistPasskey = DAOManager.getDAO(PlayerPasskeyDAO.class).existCheckPlayerPasskey(client.getAccount().getId());
				
				if (!isExistPasskey)
				{
					client.sendPacket(new SM_CHARACTER_SELECT(0));
				}
				else
				{
					client.sendPacket(new SM_CHARACTER_SELECT(1));
				}
			}
			else
			{
				PlayerService.deletePlayer(playerAccData);
				client.sendPacket(new SM_DELETE_CHARACTER(chaOid, playerAccData.getDeletionTimeInSeconds()));
			}
		}
		else
		{
			client.sendPacket(SM_SYSTEM_MESSAGE.STR_GUILD_DISPERSE_STAYMODE_CANCEL_1);
		}
	}
}
