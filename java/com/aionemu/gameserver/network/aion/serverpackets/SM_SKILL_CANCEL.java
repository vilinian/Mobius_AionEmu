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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client to cancel a currently active skill.<br>
 * It informs the user that their skill execution has been interrupted.
 * @author Sweetkr
 */
public class SM_SKILL_CANCEL extends AionServerPacket
{
	private final Creature creature;
	private final int skillId;
	
	/**
	 * This packet cancels a specific skill for a creature.<br>
	 * It is used to stop an active ability on the server side.
	 * @param creature The {@link Creature} who is performing the skill.
	 * @param skillId The unique identifier of the skill to be cancelled.
	 */
	public SM_SKILL_CANCEL(Creature creature, int skillId)
	{
		this.creature = creature;
		this.skillId = skillId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(creature.getObjectId());
		writeH(skillId);
	}
}
