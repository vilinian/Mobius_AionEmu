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
 * This packet handles the activation of a skill by a player.<br>
 * It is sent from the server to the client to notify them that a specific skill has been triggered.
 * @author Sweetkr
 */
public class SM_SKILL_ACTIVATION extends AionServerPacket
{
	private final boolean isActive;
	private final int unk;
	private final int skillId;
	
	/**
	 * Creates a new {@code SM_SKILL_ACTIVATION} packet.<br>
	 * This is used to handle toggle skills in the game.
	 * @param skillId The unique identifier for the skill.
	 * @param isActive The current state of the skill, either {@code true} or {@code false}.
	 */
	public SM_SKILL_ACTIVATION(int skillId, boolean isActive)
	{
		this.skillId = skillId;
		this.isActive = isActive;
		unk = 0;
	}
	
	/**
	 * Creates a new {@code SM_SKILL_ACTIVATION} packet.<br>
	 * This constructor is used for skills that are activated by default.<br>
	 * It sets the {@code isActive} state to {@code true}.
	 * @param skillId The unique identifier for the skill.
	 */
	public SM_SKILL_ACTIVATION(int skillId)
	{
		this.skillId = skillId;
		isActive = true;
		unk = 1;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(skillId);
		writeD(unk);
		writeC(isActive ? 1 : 0);
		writeC(0); // 4.8
	}
}
