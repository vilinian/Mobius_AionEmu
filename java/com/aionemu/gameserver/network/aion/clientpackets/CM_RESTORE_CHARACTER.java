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

import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RESTORE_CHARACTER;
import com.aionemu.gameserver.services.player.PlayerService;

/**
 * This packet handles the request from the {@code Aion} client to cancel a character deletion.<br>
 * It allows the player to restore a character that was in the process of being deleted.
 * @author -Nemesiss-
 */
public class CM_RESTORE_CHARACTER extends AionClientPacket
{
	/**
	 * PlayOk2 - we dont care...
	 */
	@SuppressWarnings("unused")
	private int playOk2;
	/**
	 * ObjectId of character that deletion should be canceled
	 */
	private int chaOid;
	
	/**
	 * Creates a new instance of the {@link CM_RESTORE_CHARACTER} packet.<br>
	 * This packet is used to request the cancellation of a character deletion.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_RESTORE_CHARACTER(int opcode, State state, State... restStates)
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
		final Account account = getConnection().getAccount();
		final PlayerAccountData pad = account.getPlayerAccountData(chaOid);
		
		final boolean success = (pad != null) && PlayerService.cancelPlayerDeletion(pad);
		sendPacket(new SM_RESTORE_CHARACTER(chaOid, success));
	}
}
