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
 * This packet is sent to the client to remove a skill from the player's active list.<br>
 * It handles the synchronization of skill states between the server and the game client.
 * @author xTz
 */
public class SM_SKILL_REMOVE extends AionServerPacket
{
	private final int skillId;
	private final int skillLevel;
	private final boolean isStigma;
	private boolean isLinked;
	
	/**
	 * Removes a specific skill from the character.<br>
	 * This packet handles both standard skills and stigmas.
	 * @param skillId The unique identifier for the skill to remove.
	 * @param skillLevel The current level of the skill being removed.
	 * @param isStigma A boolean indicating if the skill is a stigma. Set to {@code true} for stigmas and {@code false} otherwise.
	 */
	public SM_SKILL_REMOVE(int skillId, int skillLevel, boolean isStigma)
	{
		this.skillId = skillId;
		this.skillLevel = skillLevel;
		this.isStigma = isStigma;
	}
	
	// linked skill
	/**
	 * This constructor initializes a new {@code SM_SKILL_REMOVE} packet.<br>
	 * It sets the specific skill details to be removed from the character.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The current level of the skill.
	 * @param isStigma Indicates if the skill is a stigma.
	 * @param isLinked Indicates if the skill is currently linked.
	 */
	public SM_SKILL_REMOVE(int skillId, int skillLevel, boolean isStigma, boolean isLinked)
	{
		this.skillId = skillId;
		this.skillLevel = skillLevel;
		this.isStigma = isStigma;
		this.isLinked = isLinked;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(skillId);
		if (((skillId >= 30001) && (skillId <= 30003)) || ((skillId >= 40001) && (skillId <= 40011)))
		{
			writeC(0);
			writeC(0);
		}
		else if (isStigma)
		{
			writeC(1);
			writeC(1);
		}
		else if (isLinked)
		{
			writeC(1);
			writeC(3);
		}
		else
		{
			// remove skills active or passive
			writeC(skillLevel);
		}
	}
}
