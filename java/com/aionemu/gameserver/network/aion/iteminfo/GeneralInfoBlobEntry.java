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
 * This class represents a general information block sent with all {@link Item} objects.<br>
 * It serves as the first and only block for non-equipable items.<br>
 * For equipable items, it is positioned as the final blob in the sequence.
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class GeneralInfoBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code GeneralInfoBlobEntry}.<br>
	 * This entry handles general information for items.<br>
	 * It is used for all non-equipable items and as part of equipable item data.
	 */
	GeneralInfoBlobEntry()
	{
		super(ItemBlobType.GENERAL_INFO);
	}
	
	/**
	 * Writes the general item information into a {@code ByteBuffer}.<br>
	 * This method saves data such as the item mask, count, and creator.<br>
	 * It also records expiration times and unseal status.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{// TODO what with kinah?
		final Item item = ownerItem;
		writeH(buf, item.getItemMask(owner));
		writeQ(buf, item.getItemCount());
		writeS(buf, item.getItemCreator()); // Creator name
		writeC(buf, 0);
		writeD(buf, item.getExpireTimeRemaining()); // Disappears time
		writeD(buf, 0);
		writeD(buf, item.getTemporaryExchangeTimeRemaining());
		writeH(buf, item.getUnSeal());
		writeD(buf, 0);
	}
	
	/**
	 * Calculates the total size of this blob entry.<br>
	 * It accounts for the fixed header and the length of the creator name.
	 * @return The calculated size as an {@code int}.
	 */
	@Override
	public int getSize()
	{
		return 29 + (ownerItem.getItemCreator().length() * 2) + 2;
	}
}
