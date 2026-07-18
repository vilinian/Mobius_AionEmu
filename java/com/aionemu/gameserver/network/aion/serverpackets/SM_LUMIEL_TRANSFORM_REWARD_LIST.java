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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import com.aionemu.gameserver.model.templates.lumiel_transform.LumielRewardItem;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet sends a list of rewards to the client for a Lumiel transformation.<br>
 * It contains data related to {@link LumielRewardItem} objects.
 */
public class SM_LUMIEL_TRANSFORM_REWARD_LIST extends AionServerPacket
{
	private final int lumielId;
	private final List<LumielRewardItem> itemList;
	
	/**
	 * Creates a new reward list packet for a Lumiel transformation.<br>
	 * This method initializes the packet with specific rewards.
	 * @param lumielId The unique identifier for the Lumiel.
	 * @param itemList The list of {@link LumielRewardItem} objects to be sent.
	 */
	public SM_LUMIEL_TRANSFORM_REWARD_LIST(int lumielId, List<LumielRewardItem> itemList)
	{
		this.lumielId = lumielId;
		this.itemList = itemList;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(lumielId);
		writeH(itemList.size());
		for (LumielRewardItem rewardItem : itemList)
		{
			writeD(rewardItem.getItemId());
			writeQ(rewardItem.getCount());
		}
	}
}
