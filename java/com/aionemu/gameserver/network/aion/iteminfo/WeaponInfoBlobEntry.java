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
 * This class represents a data entry for weapons within an {@link ItemInfoBlob}.<br>
 * It stores information regarding the specific slots where a weapon can be equipped.
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class WeaponInfoBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code WeaponInfoBlobEntry}.<br>
	 * This entry handles data for weapon equipment slots.<br>
	 * It uses the {@code SLOTS_WEAPON} type.
	 */
	WeaponInfoBlobEntry()
	{
		super(ItemBlobType.SLOTS_WEAPON);
	}
	
	/**
	 * Writes the weapon slot information into a {@code ByteBuffer}.<br>
	 * This method determines which slots an item can occupy based on its type.<br>
	 * It uses the {@code getSlotsFor} method to identify valid slots.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		final Item item = ownerItem;
		
		final ItemSlot[] slots = ItemSlot.getSlotsFor(item.getItemTemplate().getItemSlot());
		if (slots.length == 1)
		{
			writeQ(buf, slots[0].getSlotIdMask());
			writeQ(buf, 0);
		}
		else if (item.getItemTemplate().isTwoHandWeapon())
		{
			// must occupy two slots
			writeQ(buf, slots[0].getSlotIdMask() | slots[1].getSlotIdMask());
			writeQ(buf, 0);
		}
		else
		{
			// primary and secondary slots
			writeQ(buf, slots[0].getSlotIdMask());
			writeQ(buf, slots[1].getSlotIdMask());
		}
		
		// TODO: Check if the writeQ logic for fused items is correct.
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
