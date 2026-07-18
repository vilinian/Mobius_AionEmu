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
 * This packet handles the request to add a selected item to the player's inventory.<br>
 * It is used by the server to process item acquisition actions from the client.
 * @author Alcapwnd
 */
public class SM_SELECT_ITEM_ADD extends AionServerPacket
{
	private final int uniqueItemId;
	private final int index;
	
	/**
	 * Creates a new {@code SM_SELECT_ITEM_ADD} packet.<br>
	 * This packet is used to add an item to the selection.
	 * @param uniqueItemId The unique identifier for the item.
	 * @param index The position or index of the item.
	 */
	public SM_SELECT_ITEM_ADD(int uniqueItemId, int index)
	{
		this.uniqueItemId = uniqueItemId;
		this.index = index;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(uniqueItemId);
		writeD(0);
		writeC(index);
	}
}
