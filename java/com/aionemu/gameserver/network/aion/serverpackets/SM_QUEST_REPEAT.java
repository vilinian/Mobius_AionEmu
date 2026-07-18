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

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client to handle quest repetition.<br>
 * It informs the player that a specific quest can be repeated.<br>
 * It inherits from {@link AionServerPacket}.
 * @author Ever' new 4.5 packet
 * @author FrozenKiller
 */
public class SM_QUEST_REPEAT extends AionServerPacket
{
	private final List<Integer> questList;
	
	/**
	 * Creates a new {@code SM_QUEST_REPEAT} packet.<br>
	 * This method initializes the packet with a list of quest IDs.
	 * @param questList A {@code List} containing the unique identifiers for the quests.
	 */
	public SM_QUEST_REPEAT(List<Integer> questList)
	{
		this.questList = questList;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(questList.size());
		for (Integer questId : questList)
		{
			writeD(questId);
		}
		
		questList.clear();
	}
}
