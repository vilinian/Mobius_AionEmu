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

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the distribution of loot among group members.<br>
 * It is sent to clients to synchronize shared rewards during a group activity.
 * @author Rhys2002
 */
public class SM_GROUP_LOOT extends AionServerPacket
{
	private final int groupId;
	private final int index;
	private final int unk2;
	private final int itemId;
	private final int unk3;
	private final int lootCorpseId;
	private final int distributionId;
	private final int playerId;
	private final long luck;
	
	/**
	 * Creates a new {@code SM_GROUP_LOOT} packet.<br>
	 * This packet handles group loot distribution data.<br>
	 * It initializes all required fields for the loot event.
	 * @param groupId The unique identifier for the group.
	 * @param playerId The unique identifier for the player.
	 * @param itemId The ID of the item being looted.
	 * @param lootCorpseId The ID of the corpse from which loot originated.
	 * @param distributionId The ID representing the specific distribution logic.
	 * @param luck The luck value applied to the loot calculation.
	 * @param index The sequence index for the loot item.
	 */
	public SM_GROUP_LOOT(int groupId, int playerId, int itemId, int lootCorpseId, int distributionId, long luck, int index)
	{
		this.groupId = groupId;
		this.index = index;
		unk2 = 1;
		this.itemId = itemId;
		unk3 = 0;
		this.lootCorpseId = lootCorpseId;
		this.distributionId = distributionId;
		this.playerId = playerId;
		this.luck = luck;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(groupId);
		writeD(index);
		writeD(unk2);
		writeD(itemId);
		writeC(unk3);
		writeC(0); // 3.0
		writeC(0); // 3.5
		writeD(lootCorpseId);
		writeC(distributionId);
		writeD(playerId);
		writeD((int) luck);
	}
}
