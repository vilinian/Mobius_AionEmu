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

import java.util.ArrayList;
import java.util.Map;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client to synchronize skill cooldown information.<br>
 * It informs the player about the remaining time for specific skills.<br>
 * Use this class when a skill's cooldown state changes or needs updating.
 * @author ATracer, nrg, Eloann
 */
public class SM_SKILL_COOLDOWN extends AionServerPacket
{
	private final Map<Integer, Long> cooldowns;
	
	/**
	 * Creates a new {@code SM_SKILL_COOLDOWN} packet.<br>
	 * This method initializes the skill cooldown data.
	 * @param cooldowns A {@code Map} containing skill IDs and their remaining cooldown times.
	 */
	public SM_SKILL_COOLDOWN(Map<Integer, Long> cooldowns)
	{
		this.cooldowns = cooldowns;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(calculateSize());
		writeC(1); // unk 0 or 1
		final long currentTime = System.currentTimeMillis();
		for (Map.Entry<Integer, Long> entry : cooldowns.entrySet())
		{
			final int left = (int) ((entry.getValue() - currentTime) / 1000);
			final ArrayList<Integer> skillsWithCooldown = DataManager.SKILL_DATA.getSkillsForCooldownId(entry.getKey());
			
			for (int index = 0; index < skillsWithCooldown.size(); index++)
			{
				final int skillId = skillsWithCooldown.get(index);
				writeH(skillId);
				writeD(left > 0 ? left : 0);
				writeD(DataManager.SKILL_DATA.getSkillTemplate(skillId).getCooldown());
			}
		}
	}
	
	/**
	 * Calculates the total size of the packet.<br>
	 * It sums up the sizes of all skills associated with each cooldown ID.
	 * @return The total calculated size as an {@code int}.
	 */
	private int calculateSize()
	{
		int size = 0;
		for (Map.Entry<Integer, Long> entry : cooldowns.entrySet())
		{
			size += DataManager.SKILL_DATA.getSkillsForCooldownId(entry.getKey()).size();
		}
		
		return size;
	}
}
