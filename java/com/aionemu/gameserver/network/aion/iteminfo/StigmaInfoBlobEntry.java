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
import com.aionemu.gameserver.model.templates.item.Stigma;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * Represents a specific entry within an {@link ItemInfoBlob} that contains stigma information.<br>
 * This class is used to store and handle data related to {@link com.aionemu.gameserver.model.templates.item.Stigma} objects.
 * @author -Nemesiss-
 * @modified Rolandas
 * @reworked Kill3r
 */
public class StigmaInfoBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code StigmaInfoBlobEntry}.<br>
	 * This class represents an entry in the item information blob specifically for stigmas.<br>
	 * It initializes the type to {@code STIGMA_INFO}.
	 */
	StigmaInfoBlobEntry()
	{
		super(ItemBlobType.STIGMA_INFO);
	}
	
	/**
	 * Writes the stigma information into a {@code ByteBuffer}.<br>
	 * This method saves skill IDs and kinah values for the item.<br>
	 * It handles cases with one or two skills automatically.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		final Item item = ownerItem;
		final Stigma stigma = item.getItemTemplate().getStigma();
		
		writeD(buf, stigma.getSkills().get(0).getSkillId()); // skill id 1
		if (stigma.getSkills().size() >= 2)
		{
			writeD(buf, stigma.getSkills().get(1).getSkillId()); // skill id 2
		}
		else
		{
			writeD(buf, 0);
		}
		
		writeD(buf, stigma.getKinah());
		
		skip(buf, 192);
		writeH(buf, 0x1); // unk
		writeH(buf, 0);
		skip(buf, 96);
		writeH(buf, 0); // unk
	}
	
	/**
	 * Returns the total size of this blob entry.<br>
	 * This value is used to determine how many bytes to read from the buffer.
	 * @return The total size as an {@code int}.
	 */
	@Override
	public int getSize()
	{
		return 8 + 4 + 192 + 4 + 96 + 2;
	}
}
