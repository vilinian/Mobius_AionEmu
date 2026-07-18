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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is used to remove a specific object from a player's house.<br>
 * It handles the synchronization of deleting house decorations or furniture for all relevant clients.
 * @author Rolandas
 */
public class SM_DELETE_HOUSE_OBJECT extends AionServerPacket
{
	private final int itemObjectId;
	
	/**
	 * Creates a new packet to delete a house object.<br>
	 * This method initializes the packet with a specific ID.
	 * @param itemObjectId The unique identifier of the house object to be removed.
	 */
	public SM_DELETE_HOUSE_OBJECT(int itemObjectId)
	{
		this.itemObjectId = itemObjectId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(itemObjectId);
	}
}
