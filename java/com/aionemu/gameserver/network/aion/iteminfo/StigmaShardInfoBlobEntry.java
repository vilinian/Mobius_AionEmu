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
 * Represents a specific entry within an {@link ItemInfoBlob} for Stigma Shards.<br>
 * This class handles the data structure required to process shard information from the game server.
 * @author Rolandas
 */
public class StigmaShardInfoBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code StigmaShardInfoBlobEntry}.<br>
	 * This constructor initializes the entry with the {@code STIGMA_SHARD} blob type.
	 */
	public StigmaShardInfoBlobEntry()
	{
		super(ItemBlobType.STIGMA_SHARD);
	}
	
	/**
	 * Writes this specific blob data into the provided {@code ByteBuffer}.<br>
	 * This method handles the serialization of the entry.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		writeD(buf, 0);
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
