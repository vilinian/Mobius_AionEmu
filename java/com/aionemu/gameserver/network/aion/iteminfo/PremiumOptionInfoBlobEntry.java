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
 * Represents an entry within a blob containing premium option information.<br>
 * This class is used to parse and store specific data related to premium items.<br>
 * It extends {@link ItemBlobEntry} to provide specialized handling for these options.
 * @author Rolandas
 */
public class PremiumOptionInfoBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code PremiumOptionInfoBlobEntry}.<br>
	 * This constructor initializes the entry with the {@code PREMIUM_OPTION} type.<br>
	 * It is used to handle premium option data in the game server.
	 */
	public PremiumOptionInfoBlobEntry()
	{
		super(ItemBlobType.PREMIUM_OPTION);
	}
	
	/**
	 * Writes the premium option data into a {@code ByteBuffer}.<br>
	 * This method saves the bonus number and random count for the item.<br>
	 * It also writes a default value of {@code 0} to the buffer.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		writeC(buf, ownerItem.getBonusNumber());
		writeC(buf, ownerItem.getRandomCount());
		writeC(buf, 0);
	}
	
	/**
	 * Returns the total size of this blob entry.<br>
	 * The size is calculated as a constant value.
	 * @return The size of the entry as an {@code int}.
	 */
	@Override
	public int getSize()
	{
		return 1 + 2;
	}
}
