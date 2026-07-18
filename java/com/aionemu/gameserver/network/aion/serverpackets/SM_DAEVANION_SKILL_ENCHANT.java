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
 * This packet handles the Daevonion skill enchantment process.<br>
 * It is sent to the client to synchronize the results of a skill enhancement.
 * @author Falke_34
 */
public class SM_DAEVANION_SKILL_ENCHANT extends AionServerPacket
{
	private final int skillLevel;
	private final int skillId;
	private final int newSkillLevel;
	
	/**
	 * This method creates a packet for enchanting a Daevonion skill.<br>
	 * It updates the skill level of a specific skill ID.
	 * @param skillLevel The current level of the skill.
	 * @param skillId The unique identifier for the skill.
	 * @param newSkillLevel The target level to set for the skill.
	 */
	public SM_DAEVANION_SKILL_ENCHANT(int skillLevel, int skillId, int newSkillLevel)
	{
		this.newSkillLevel = newSkillLevel;
		this.skillId = skillId;
		this.skillLevel = skillLevel;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(skillLevel); // Skill Level
		writeH(skillId); // Skill Id
		writeD(newSkillLevel); // new Skill Level
	}
}
