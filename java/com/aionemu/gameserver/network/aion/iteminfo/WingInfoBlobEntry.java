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
 * Represents a data entry for clothing items within an {@link ItemInfoBlob}.<br>
 * It stores information regarding the specific slots where a piece of cloth can be equipped.
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class WingInfoBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code WingInfoBlobEntry}.<br>
	 * This entry is used to handle data for wing slots.<br>
	 * It initializes the blob type to {@code SLOTS_WING}.
	 */
	WingInfoBlobEntry()
	{
		super(ItemBlobType.SLOTS_WING);
	}
	
	/**
	 * Writes the item slot information into a {@code ByteBuffer}.<br>
	 * This method handles data for items that occupy specific slots.<br>
	 * It uses {@code getSlotFor} to retrieve the correct mask.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		final Item item = ownerItem;
		
		writeQ(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).getSlotIdMask());
		writeQ(buf, 0); // no secondary slot
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
