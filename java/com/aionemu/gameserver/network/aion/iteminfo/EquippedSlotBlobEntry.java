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
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * Represents the equipment slot information for an item.<br>
 * This entry specifies the target slot index if the item is currently equipped.<br>
 * If the item is not equipped, it returns {@code 0}.
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class EquippedSlotBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code EquippedSlotBlobEntry}.<br>
	 * This entry handles data for items that are currently equipped.<br>
	 * It identifies the specific slot index or returns {@code 0} if not equipped.
	 */
	EquippedSlotBlobEntry()
	{
		super(ItemBlobType.EQUIPPED_SLOT);
	}
	
	/**
	 * Writes the equipment slot information into a {@code ByteBuffer}.<br>
	 * This method records the slot index if the item is equipped.<br>
	 * It writes {@code 0} if the item is not currently equipped.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		final Item item = ownerItem;
		writeQ(buf, item.isEquipped() ? item.getEquipmentSlot() : 0);
	}
	
	/**
	 * Returns the total size of the scripts.<br>
	 * This value is currently fixed at {@code 8}.
	 * @return The size of the scripts as an {@code int}.
	 */
	@Override
	public int getSize()
	{
		return 8;
	}
}
