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
import com.aionemu.gameserver.services.item.ItemRemodelService;

/**
 * Handles the client request to remodel an item.<br>
 * This packet triggers the {@link ItemRemodelService} to process the transformation.<br>
 * It is sent by the client to initiate a visual change on a specific item.
 * @author Sarynth
 */
public class CM_ITEM_REMODEL extends AionClientPacket
{
	private int keepItemId;
	private int extractItemId;
	
	/**
	 * This constructor initializes a new {@link CM_ITEM_REMODEL} packet.<br>
	 * It passes the network states to the parent class.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if they exist.
	 */
	public CM_ITEM_REMODEL(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		readD(); // npcId
		keepItemId = readD();
		extractItemId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		ItemRemodelService.remodelItem(activePlayer, keepItemId, extractItemId);
	}
}
