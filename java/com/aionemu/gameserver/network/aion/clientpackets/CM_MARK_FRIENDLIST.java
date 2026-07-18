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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MARK_FRIENDLIST;

/**
 * Handles the client request to mark or update a friend list.<br>
 * This packet processes data sent from the client to manage {@link Player} friendship statuses.
 * @author xTz, Rolandas
 */
public class CM_MARK_FRIENDLIST extends AionClientPacket
{
	/**
	 * This method creates a new {@code CM_MARK_FRIENDLIST} packet.<br>
	 * It initializes the packet with specific network states.<br>
	 * Use this to handle friend list updates from the client.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the sender.
	 * @param restStates A variable number of additional connection states.
	 */
	public CM_MARK_FRIENDLIST(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		// nothing to read
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		if (activePlayer != null)
		{
			if (!activePlayer.getFriendList().getIsFriendListSent())
			{
				getConnection().sendPacket(new SM_FRIEND_LIST());
			}
			
			getConnection().sendPacket(new SM_MARK_FRIENDLIST());
		}
	}
}
