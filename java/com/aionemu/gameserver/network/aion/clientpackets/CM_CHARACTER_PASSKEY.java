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
import com.aionemu.gameserver.model.account.CharacterPasskey;
import com.aionemu.gameserver.model.account.CharacterPasskey.ConnectType;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHARACTER_SELECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_CHARACTER;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.player.PlayerEnterWorldService;
import com.aionemu.gameserver.services.player.PlayerService;

/**
 * Handles the client request to provide a character passkey for authentication.<br>
 * This packet is used during the {@code CHARACTER_SELECT} phase to verify access.<br>
 * It interacts with {@link PlayerPasskeyDAO} to validate the provided credentials.
 * @author ginho1
 */
public class CM_CHARACTER_PASSKEY extends AionClientPacket
{
	private int type;
	private int unk;
	private String passkey;
	private String newPasskey;
	
	/**
	 * This constructor initializes a new {@code CM_CHARACTER_PASSKEY} packet.<br>
	 * It sets the required network states for the packet.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state required for this packet.
	 * @param restStates Additional connection states required for this packet.
	 */
	public CM_CHARACTER_PASSKEY(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		type = readC(); // 0:new, 2:update, 3:input
		unk = readC();
		try
		{
			passkey = new String(readB(65), "UTF-8");
			if (type == 2)
			{
				newPasskey = new String(readB(65), "UTF-8");
			}
		}
		catch (Exception e)
		{
		}
	}
	
	@Override
	protected void runImpl()
	{
		final AionConnection client = getConnection();
		final CharacterPasskey chaPasskey = client.getAccount().getCharacterPasskey();
		
		switch (type)
		{
			case 0:
				chaPasskey.setIsPass(false);
				chaPasskey.setWrongCount(0);
				DAOManager.getDAO(PlayerPasskeyDAO.class).insertPlayerPasskey(client.getAccount().getId(), passkey);
				client.sendPacket(new SM_CHARACTER_SELECT(2, type, unk, chaPasskey.getWrongCount()));
				break;
			case 2:
				final boolean isSuccess = DAOManager.getDAO(PlayerPasskeyDAO.class).updatePlayerPasskey(client.getAccount().getId(), passkey, newPasskey);
				
				chaPasskey.setIsPass(false);
				if (isSuccess)
				{
					chaPasskey.setWrongCount(0);
					client.sendPacket(new SM_CHARACTER_SELECT(2, type, unk, chaPasskey.getWrongCount()));
				}
				else
				{
					chaPasskey.setWrongCount(chaPasskey.getWrongCount() + 1);
					checkBlock(client.getAccount().getId(), chaPasskey.getWrongCount());
					client.sendPacket(new SM_CHARACTER_SELECT(2, type, unk, chaPasskey.getWrongCount()));
				}
				break;
			case 3:
				final boolean isPass = DAOManager.getDAO(PlayerPasskeyDAO.class).checkPlayerPasskey(client.getAccount().getId(), passkey);
				
				if (isPass)
				{
					chaPasskey.setIsPass(true);
					chaPasskey.setWrongCount(0);
					client.sendPacket(new SM_CHARACTER_SELECT(2, type, unk, chaPasskey.getWrongCount()));
					
					if (chaPasskey.getConnectType() == ConnectType.ENTER)
					{
						PlayerEnterWorldService.startEnterWorld(chaPasskey.getObjectId(), client);
					}
					else if (chaPasskey.getConnectType() == ConnectType.DELETE)
					{
						final PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(chaPasskey.getObjectId());
						
						PlayerService.deletePlayer(playerAccData);
						client.sendPacket(new SM_DELETE_CHARACTER(chaPasskey.getObjectId(), playerAccData.getDeletionTimeInSeconds()));
					}
				}
				else
				{
					chaPasskey.setIsPass(false);
					chaPasskey.setWrongCount(chaPasskey.getWrongCount() + 1);
					checkBlock(client.getAccount().getId(), chaPasskey.getWrongCount());
					client.sendPacket(new SM_CHARACTER_SELECT(2, type, unk, chaPasskey.getWrongCount()));
				}
				break;
		}
	}
	
	/**
	 * Checks if an account should be blocked.<br>
	 * This method compares the {@code wrongCount} against the maximum limit defined in {@link SecurityConfig}.<br>
	 * If the limit is exceeded, it triggers a ban via {@code int, String, int, int)}.
	 * @param accountId The unique identifier for the account.
	 * @param wrongCount The number of incorrect passkey attempts.
	 */
	private void checkBlock(int accountId, int wrongCount)
	{
		if (wrongCount >= SecurityConfig.PASSKEY_WRONG_MAXCOUNT)
		{
			// TODO : Change the account to be blocked
			LoginServer.getInstance().sendBanPacket((byte) 2, accountId, "", 60 * 8, 0);
		}
	}
}
