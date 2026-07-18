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

/**
 * Handles the request from a client to leave an instance.<br>
 * This packet is processed by the server to transition the {@link Player} out of their current instance.
 * @author xTz
 */
public class CM_INSTANCE_LEAVE extends AionClientPacket
{
	/**
	 * Handles a request to leave an instance.<br>
	 * This method initializes the packet with the required states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} associated with the action.
	 * @param restStates Additional {@link State} objects if needed.
	 */
	public CM_INSTANCE_LEAVE(int opcode, State state, State... restStates)
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
		final Player player = getConnection().getActivePlayer();
		if (player.isInInstance())
		{
			player.getPosition().getWorldMapInstance().getInstanceHandler().onExitInstance(player);
		}
	}
}
