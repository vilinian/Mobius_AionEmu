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
 * This class represents a data entry for armor items within an {@link ItemInfoBlob}.<br>
 * It stores information regarding the specific slots where an armor piece can be equipped.
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class ArmorInfoBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code ArmorInfoBlobEntry}.<br>
	 * This entry handles data for armor equipment slots.<br>
	 * It uses the {@code SLOTS_ARMOR} type.
	 */
	ArmorInfoBlobEntry()
	{
		super(ItemBlobType.SLOTS_ARMOR);
	}
	
	/**
	 * Writes the armor slot information into a {@code ByteBuffer}.<br>
	 * This method handles data for items like armor pieces.<br>
	 * It uses the {@code getSlotFor} method to find valid slots.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		final Item item = ownerItem;
		
		writeQ(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).getSlotIdMask());
		writeQ(buf, 0); // TODO! secondary slot?
		writeC(buf, item.getItemTemplate().isItemDyePermitted() ? 1 : 0);
		writeC(buf, (item.getItemColor() & 0xFF0000) >> 16);
		writeC(buf, (item.getItemColor() & 0xFF00) >> 8);
		writeC(buf, (item.getItemColor() & 0xFF));
	}
	
	/**
	 * Returns the fixed size of this blob entry.<br>
	 * This value is used to determine how many bytes to read from the buffer.
	 * @return The size of the entry as an {@code int}.
	 */
	@Override
	public int getSize()
	{
		return 20;
	}
}
