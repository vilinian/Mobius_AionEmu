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

import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.world.World;

/**
 * Handles the client request to interact with a house object.<br>
 * This packet is triggered when a {@link Player} uses an object within a house.<br>
 * It processes the interaction logic for specific {@link HouseObject} instances.
 * @author Rolandas
 */
public class CM_USE_HOUSE_OBJECT extends AionClientPacket
{
	int itemObjectId;
	
	/**
	 * Creates a new {@link CM_USE_HOUSE_OBJECT} packet.<br>
	 * This constructor initializes the packet with specific network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_USE_HOUSE_OBJECT(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		itemObjectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		final VisibleObject visObject = World.getInstance().findVisibleObject(itemObjectId);
		if (visObject == null)
		{
			return;
		}
		
		if (visObject instanceof HouseObject<?>)
		{
			((HouseObject<?>) visObject).getController().onDialogRequest(player);
		}
	}
}
