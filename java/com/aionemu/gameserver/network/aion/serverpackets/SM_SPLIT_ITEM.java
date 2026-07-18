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
 * This packet handles the request to split an item into multiple parts.<br>
 * It is sent from the client to the server to initiate the splitting process.
 * @author Falke_34
 */
public class SM_SPLIT_ITEM extends AionServerPacket
{
	// Sobald der Itemstapel voll ist, wird geprüft, ob ein neuer Stapel mit Machtscherben und Verzauberungsstaub begonnen werden kann.
	
	private final int itemId;
	private final long itemCount;
	
	/**
	 * Creates a new {@code SM_SPLIT_ITEM} packet.<br>
	 * This packet is used to handle the splitting of an item stack.<br>
	 * It stores the specific item ID and the count to be split.
	 * @param itemId The unique identifier for the item.
	 * @param itemCount The number of items in the stack.
	 */
	public SM_SPLIT_ITEM(int itemId, int itemCount)
	{
		this.itemId = itemId;
		this.itemCount = itemCount;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(0); // TODO
		writeD(itemId); // Item Id
		writeQ(itemCount); // count
	}
}
