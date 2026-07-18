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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SECURITY_TOKEN;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This packet handles the security token sent from the client to the server.<br>
 * It is used during the authentication process to verify the connection.<br>
 * It corresponds to the {@link SM_SECURITY_TOKEN} server response.
 * @author Falke_34, CoolyT
 */
public class CM_SECURITY_TOKEN extends AionClientPacket
{
	/**
	 * Creates a new instance of {@link CM_SECURITY_TOKEN}.<br>
	 * This constructor initializes the packet with specific states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@code State} associated with the token.
	 * @param restStates A variable number of additional {@code State} objects.
	 */
	public CM_SECURITY_TOKEN(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		// empty
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		final Account acc = getConnection().getAccount();
		if ((acc.getSecurityToken() == null) || acc.getSecurityToken().isEmpty())
		{
			return;
		}
		
		PacketSendUtility.sendPacket(player, new SM_SECURITY_TOKEN(acc.getSecurityToken()));
	}
}
