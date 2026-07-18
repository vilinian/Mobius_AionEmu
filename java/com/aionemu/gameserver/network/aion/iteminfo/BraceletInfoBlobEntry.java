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
package com.aionemu.gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This class represents a data entry for bracelet information.<br>
 * It stores details regarding the specific slots where bracelets can be equipped.
 * @author FrozenKiller
 */
public class BraceletInfoBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code BraceletInfoBlobEntry}.<br>
	 * This entry handles data for bracelet equipment slots.<br>
	 * It uses the {@code BRACELET_INFO} type.
	 */
	BraceletInfoBlobEntry()
	{
		super(ItemBlobType.BRACELET_INFO);
	}
	
	/**
	 * Writes the bracelet slot information into a {@code ByteBuffer}.<br>
	 * This method handles the data for items like rings or earrings.<br>
	 * It uses the {@code getSlotsFor} method to find valid slots.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		final Item item = ownerItem;
		
		writeQ(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).getSlotIdMask());
		writeQ(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).getSlotIdMask());
	}
	
	/**
	 * Returns the size of this blob entry.<br>
	 * This value is used to determine how many bytes are occupied in the buffer.
	 * @return The size of the entry as an {@code int}.
	 */
	@Override
	public int getSize()
	{
		return 16;
	}
}
