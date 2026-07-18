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
import com.aionemu.gameserver.services.player.FatigueService;

/**
 * Handles the client request to recover player fatigue.<br>
 * This packet interacts with {@link FatigueService} to update the character's status.
 * @author Alcapwnd
 */
public class CM_FATIGUE_RECOVER extends AionClientPacket
{
	private int removeCount;
	
	/**
	 * Handles the recovery of player fatigue.<br>
	 * This method processes a packet to restore energy levels.<br>
	 * It uses {@link State} to manage the current connection status.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@code State} of the connection.
	 * @param restStates A variable number of additional {@code State} objects.
	 */
	public CM_FATIGUE_RECOVER(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		removeCount = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		FatigueService.getInstance().removeRecoverCount(player, removeCount);
	}
}
