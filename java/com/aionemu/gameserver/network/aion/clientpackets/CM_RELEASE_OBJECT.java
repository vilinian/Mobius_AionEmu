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

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.UseableItemObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.world.World;

/**
 * Handles the client request to release an object from a player.<br>
 * This packet is used when a {@link Player} interacts with a {@link VisibleObject}.<br>
 * It processes the logic for freeing up objects in the game world.
 * @author Rolandas
 */
public class CM_RELEASE_OBJECT extends AionClientPacket
{
	int targetObjectId;
	
	/**
	 * Creates a new {@link CM_RELEASE_OBJECT} packet.<br>
	 * This constructor initializes the packet with specific states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates A variable number of additional states.
	 */
	public CM_RELEASE_OBJECT(int opcode, State state, State... restStates)
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
		if (player == null)
		{
			return;
		}
		
		if (player.getController().hasTask(TaskId.HOUSE_OBJECT_USE))
		{
			final VisibleObject object = World.getInstance().findVisibleObject(targetObjectId);
			if ((object instanceof UseableItemObject) && !player.getController().hasScheduledTask(TaskId.HOUSE_OBJECT_USE))
			{
				// not cancelled
			}
			else
			{
				// mailboxes always show this message even if not cancelled
				player.getController().cancelTask(TaskId.HOUSE_OBJECT_USE);
				sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_CANCEL_USE);
			}
		}
	}
}
