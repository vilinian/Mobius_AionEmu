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
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * Handles the {@code CM_LUNA_IDENTIFICATION} packet sent from the client.<br>
 * This packet is used to identify a specific Luna character or entity.<br>
 * It allows the server to process identification requests for game objects.
 * @author Falke_34
 */
public class CM_LUNA_IDENTIFICATION extends AionClientPacket
{
	private int itemObjectId;
	@SuppressWarnings("unused")
	private int statId;
	
	/**
	 * This constructor initializes a new {@link CM_LUNA_IDENTIFICATION} packet.<br>
	 * It sets the required network properties for the client communication.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the packet.
	 * @param restStates A variable number of additional states associated with the packet.
	 */
	public CM_LUNA_IDENTIFICATION(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		itemObjectId = readD();
		statId = readH();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		final Storage inventory = player.getInventory();
		final Item item = inventory.getItemByObjId(itemObjectId);
		if (item == null)
		{
			return;
		}
		
		// TODO
	}
}
