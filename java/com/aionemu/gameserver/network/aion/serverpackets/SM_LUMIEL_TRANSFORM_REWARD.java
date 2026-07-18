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

import com.aionemu.gameserver.model.templates.lumiel_transform.LumielRewardItem;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the delivery of rewards for a Lumiel transformation.<br>
 * It sends the relevant {@link LumielRewardItem} data to the client.
 */
public class SM_LUMIEL_TRANSFORM_REWARD extends AionServerPacket
{
	private final int lumielId;
	private final LumielRewardItem rewardItem;
	
	/**
	 * Creates a new {@code SM_LUMIEL_TRANSFORM_REWARD} packet.<br>
	 * This packet handles rewards for Lumiel transformations.
	 * @param lumielId The unique identifier for the Lumiel.
	 * @param rewardItem The specific item to be rewarded.
	 */
	public SM_LUMIEL_TRANSFORM_REWARD(int lumielId, LumielRewardItem rewardItem)
	{
		this.lumielId = lumielId;
		this.rewardItem = rewardItem;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(lumielId);
		writeD(rewardItem.getItemId());
		writeQ(rewardItem.getCount());
	}
}
