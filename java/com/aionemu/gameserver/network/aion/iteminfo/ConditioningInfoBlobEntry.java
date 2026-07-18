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
 * This class represents a specific entry within an {@link ItemInfoBlob} that contains conditioning information.<br>
 * It is used to transmit data regarding item conditions to the client.
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class ConditioningInfoBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code ConditioningInfoBlobEntry}.<br>
	 * This entry is used to send conditioning information.<br>
	 * It initializes the blob type to {@code CONDITIONING_INFO}.
	 */
	ConditioningInfoBlobEntry()
	{
		super(ItemBlobType.CONDITIONING_INFO);
	}
	
	/**
	 * Writes the charge points of the owned item into a {@code ByteBuffer}.<br>
	 * This method uses the {@code writeD} helper to store the value.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		final Item item = ownerItem;
		
		writeD(buf, item.getChargePoints());
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
