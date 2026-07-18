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
import com.aionemu.gameserver.services.item.ItemSplitService;

/**
 * Handles the client request to split an item into multiple parts.<br>
 * This packet triggers the {@link ItemSplitService} to process the division logic.
 * @author kosyak
 */
public class CM_SPLIT_ITEM extends AionClientPacket
{
	int sourceItemObjId;
	byte sourceStorageType;
	long itemAmount;
	int destinationItemObjId;
	byte destinationStorageType;
	short slotNum;
	
	/**
	 * This constructor initializes a new {@link CM_SPLIT_ITEM} packet.<br>
	 * It sets the required network states for the packet.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_SPLIT_ITEM(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		sourceItemObjId = readD();
		itemAmount = readD();
		
		readB(4); // Nothing
		
		sourceStorageType = readSC();
		destinationItemObjId = readD();
		destinationStorageType = readSC();
		slotNum = readSH();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		ItemSplitService.splitItem(player, sourceItemObjId, destinationItemObjId, itemAmount, slotNum, sourceStorageType, destinationStorageType);
	}
}
