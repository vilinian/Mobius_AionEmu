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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.actions.AbstractItemAction;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * Handles the client request for automatic golden star processing.<br>
 * This packet is used to trigger specific game logic related to {@code GoldenStar} items.
 * @author FrozenKiller
 */
public class CM_AUTOMATIC_GOLDENSTAR extends AionClientPacket
{
	private int ItemObjectId;
	
	/**
	 * This constructor initializes a new {@link CM_AUTOMATIC_GOLDENSTAR} packet.<br>
	 * It passes the required network states to the parent class.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the sender.
	 * @param restStates Additional connection states associated with the packet.
	 */
	public CM_AUTOMATIC_GOLDENSTAR(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		ItemObjectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		final Item item = player.getInventory().getItemByObjId(ItemObjectId);
		final ItemActions itemActions = item.getItemTemplate().getActions();
		for (AbstractItemAction itemAction : itemActions.getItemActions())
		{
			if (itemAction.canAct(player, item, null))
			{
				itemAction.act(player, item, null);
			}
		}
	}
}
