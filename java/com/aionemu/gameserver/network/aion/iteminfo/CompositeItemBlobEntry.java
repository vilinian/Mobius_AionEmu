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
import java.util.ArrayList;
import java.util.Set;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * Represents a data entry for items that were fused into another item.<br>
 * This class provides information about the components used in a composite fusion process.
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class CompositeItemBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code CompositeItemBlobEntry}.<br>
	 * This class represents items fused with another item.<br>
	 * It uses the {@code COMPOSITE_ITEM} type.
	 */
	CompositeItemBlobEntry()
	{
		super(ItemBlobType.COMPOSITE_ITEM);
	}
	
	/**
	 * Writes the fusion information into a {@code ByteBuffer}.<br>
	 * This method records the ID of the fused item and any associated stones.<br>
	 * It also writes placeholder data for future use.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		final Item item = ownerItem;
		
		writeD(buf, item.getFusionedItemId());
		writeFusionStones(buf);
		writeH(buf, 0);
		writeB(buf, new byte[50]); // TODO
	}
	
	/**
	 * Writes the fusion stones of the owner item into the buffer.<br>
	 * This method handles up to 6 stones and pads the remaining space.<br>
	 * It skips the data if no fusion stones are present.
	 * @param buf The {@code ByteBuffer} where the stone data is written.
	 */
	private void writeFusionStones(ByteBuffer buf)
	{
		final Item item = ownerItem;
		int count = 0;
		
		if (item.hasFusionStones())
		{
			final Set<ManaStone> itemStones = item.getFusionStones();
			final ArrayList<ManaStone> basicStones = new ArrayList<>();
			
			for (ManaStone itemStone : itemStones)
			{
				basicStones.add(itemStone);
			}
			
			for (ManaStone basicFusionStone : basicStones)
			{
				if (count == 6)
				{
					break;
				}
				
				writeD(buf, basicFusionStone.getItemId());
				count++;
			}
			
			skip(buf, (6 - count) * 4);
		}
		else
		{
			skip(buf, 24);
		}
	}
	
	/**
	 * Returns the fixed size of this blob entry.<br>
	 * This value is used to determine how many bytes to read from the buffer.
	 * @return The size of the entry as an {@code int}.
	 */
	@Override
	public int getSize()
	{
		return 80;
		// return 12 * 2 + 6;
	}
}
