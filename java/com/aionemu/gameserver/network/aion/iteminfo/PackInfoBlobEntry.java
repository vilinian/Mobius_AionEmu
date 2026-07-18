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

import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * Represents an individual entry within a {@link ItemInfoBlob} that contains pack information.<br>
 * This class provides the data structure needed to parse specific item metadata from the game's network packets.
 * @author Ranastic
 */
public class PackInfoBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code PackInfoBlobEntry}.<br>
	 * This class represents an entry for pack information in the item blob.<br>
	 * It initializes the type to {@code PACK_INFO}.
	 */
	PackInfoBlobEntry()
	{
		super(ItemBlobType.PACK_INFO);
	}
	
	/**
	 * Writes the packing information for an item into a {@code ByteBuffer}.<br>
	 * This method handles how many items are in a pack.<br>
	 * It writes a positive count if packed, a negative count if not packed, or 0 otherwise.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		if ((ownerItem.getItemTemplate().getPackCount() > 0) && ownerItem.isPacked())
		{
			writeC(buf, ownerItem.getPackCount());
		}
		else if (!ownerItem.isPacked())
		{
			writeC(buf, ownerItem.getPackCount() * -1);
		}
		else
		{
			writeC(buf, 0);
		}
	}
	
	/**
	 * Returns the size of this entry.<br>
	 * This method always returns {@code 1}.
	 * @return The size as an {@code int}.
	 */
	@Override
	public int getSize()
	{
		return 1;
	}
}
