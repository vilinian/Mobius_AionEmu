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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * This packet handles the display of a dialogue window to the client.<br>
 * It is triggered when an {@link Npc} or other entity initiates a conversation with a {@link Player}.
 * @author alexa026, Avol modified by ATracer
 */
public class CM_SHOW_DIALOG extends AionClientPacket
{
	private int targetObjectId;
	
	/**
	 * This method creates a new {@code CM_SHOW_DIALOG} packet.<br>
	 * It initializes the packet with specific network states.<br>
	 * Use this to handle dialogue display requests from the client.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the packet.
	 * @param restStates A variable number of additional connection states.
	 */
	public CM_SHOW_DIALOG(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		targetObjectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player.isTrading())
		{
			return;
		}
		
		final VisibleObject obj = player.getKnownList().getObject(targetObjectId);
		
		if (obj instanceof Npc)
		{
			((Npc) obj).getController().onDialogRequest(player);
		}
	}
}
