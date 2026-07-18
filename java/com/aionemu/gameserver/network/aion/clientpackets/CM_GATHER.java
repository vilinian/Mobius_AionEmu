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

import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * Handles the client request to gather an object from the game world.<br>
 * This packet processes interactions with {@link Gatherable} objects by a {@link Player}.
 * @author ATracer
 */
public class CM_GATHER extends AionClientPacket
{
	boolean isStartGather = false;
	
	/**
	 * This constructor initializes a new {@link CM_GATHER} packet.<br>
	 * It sets the required network properties for the request.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the sender.
	 * @param restStates A variable number of additional states associated with the packet.
	 */
	public CM_GATHER(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		final int action = readD();
		if (action == 0)
		{
			isStartGather = true;
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		final VisibleObject target = player.getTarget();
		if ((target != null) && target.getPosition().isSpawned() && (target instanceof Gatherable))
		{
			if (isStartGather)
			{
				((Gatherable) target).getController().onStartUse(player);
			}
			else
			{
				((Gatherable) target).getController().finishGathering(player);
			}
		}
	}
}
