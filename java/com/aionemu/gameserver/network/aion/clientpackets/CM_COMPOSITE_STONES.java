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
import com.aionemu.gameserver.model.templates.item.actions.CompositionAction;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.restrictions.RestrictionsManager;

/**
 * Handles the client request for processing composite stones.<br>
 * This packet is used to manage the composition of items into new materials.
 */
public class CM_COMPOSITE_STONES extends AionClientPacket
{
	private int compinationToolItemObjectId;
	private int firstItemObjectId;
	private int secondItemObjectId;
	
	/**
	 * Creates a new instance of the {@code CM_COMPOSITE_STONES} packet.<br>
	 * This constructor initializes the basic connection states.<br>
	 * You must manually set the {@code ByBuffer} and {@code ClientConnection} fields after creation.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the client.
	 * @param restStates Additional connection states if required.
	 */
	public CM_COMPOSITE_STONES(int opcode, AionConnection.State state, AionConnection.State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		compinationToolItemObjectId = readD();
		firstItemObjectId = readD();
		secondItemObjectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.isProtectionActive())
		{
			player.getController().stopProtectionActiveTask();
		}
		
		if (player.isCasting())
		{
			player.getController().cancelCurrentSkill();
		}
		
		final Item tools = player.getInventory().getItemByObjId(compinationToolItemObjectId);
		if (tools == null)
		{
			return;
		}
		
		final Item first = player.getInventory().getItemByObjId(firstItemObjectId);
		if (first == null)
		{
			return;
		}
		
		final Item second = player.getInventory().getItemByObjId(secondItemObjectId);
		if ((second == null) || !RestrictionsManager.canUseItem(player, tools))
		{
			return;
		}
		
		final CompositionAction action = new CompositionAction();
		
		if (!action.canAct(player, tools, first, second))
		{
			return;
		}
		
		action.act(player, tools, first, second);
	}
}
