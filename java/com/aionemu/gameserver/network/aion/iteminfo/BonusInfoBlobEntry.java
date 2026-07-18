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

import com.aionemu.gameserver.configs.administration.DeveloperConfig;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * Represents an individual entry within a bonus information blob.<br>
 * This class is used to store and process specific item bonuses provided by {@link ItemInfoBlob}.
 * @author Rolandas
 */
public class BonusInfoBlobEntry extends ItemBlobEntry
{
	/**
	 * Creates a new instance of {@code BonusInfoBlobEntry}.<br>
	 * This entry is used to handle {@code STAT_BONUSES} data.
	 */
	public BonusInfoBlobEntry()
	{
		super(ItemBlobType.STAT_BONUSES);
	}
	
	/**
	 * Writes the item statistic data into a {@code ByteBuffer}.<br>
	 * This method handles different logic based on the {@code DeveloperConfig.ITEM_STAT_ID} value.<br>
	 * It writes the modifier name, value, and type to the buffer.
	 * @param buf The {@code ByteBuffer} where the data will be written.
	 */
	@Override
	public void writeThisBlob(ByteBuffer buf)
	{
		if (DeveloperConfig.ITEM_STAT_ID > 0)
		{
			writeH(buf, DeveloperConfig.ITEM_STAT_ID);
			writeD(buf, 10);
			writeC(buf, 0);
		}
		else
		{
			writeH(buf, modifier.getName().getItemStoneMask());
			writeD(buf, modifier.getValue() * modifier.getName().getSign());
			writeC(buf, modifier instanceof StatRateFunction ? 1 : 0);
		}
	}
	
	/**
	 * Returns the fixed size of this entry.<br>
	 * This value is always {@code 7}.
	 * @return The size of the entry as an {@code int}.
	 */
	@Override
	public int getSize()
	{
		return 7;
	}
}
