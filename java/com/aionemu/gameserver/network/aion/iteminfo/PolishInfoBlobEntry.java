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

import com.aionemu.gameserver.model.items.IdianStone;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * Represents a specific entry within an {@code ItemBlob} that contains polishing information.<br>
 * This class is used to handle data related to item enhancement and polishing mechanics.
 * @author Rolandas
 */
public class PolishInfoBlobEntry extends ItemBlobEntry
{
	
	/**
	 * Creates a new instance of {@code PolishInfoBlobEntry}.<br>
	 * This class represents the polish information for an item.<br>
	 * It initializes the entry with the {@code POLISH_INFO} type.
	 */
	PolishInfoBlobEntry()
	{
		super(ItemBlobType.POLISH_INFO);
	}
	
	/**
	 * Writes the {@code IdianStone} charge value into a {@code ByteBuffer}.<br>
	 * It checks if the {@code ownerItem} has an associated {@link IdianStone}.<br>
	 * If no stone exists, it writes the value {@code 0}.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		// Idian charge value
		final IdianStone stone = ownerItem.getIdianStone();
		writeD(buf, stone == null ? 0 : stone.getPolishCharge());
	}
	
	/**
	 * Returns the fixed size of this blob.<br>
	 * This value is always {@code 4}.
	 * @return The size of the entry as an {@code int}.
	 */
	@Override
	public int getSize()
	{
		return 4;
	}
}
