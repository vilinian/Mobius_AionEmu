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
 * This packet is sent to the client to notify it that a {@link Creature} has been resurrected.<br>
 * It handles the visual and state updates required when an entity returns to life.
 * @author ATracer
 * @author Jego
 */
public class SM_RESURRECT extends AionServerPacket
{
	private final String name;
	private final int skillId;
	
	/**
	 * Creates a new {@link SM_RESURRECT} packet for a specific creature.<br>
	 * This method uses a default skill ID of {@code 0}.
	 * @param creature The {@code Creature} object to be resurrected.
	 */
	public SM_RESURRECT(Creature creature)
	{
		this(creature, 0);
	}
	
	/**
	 * This packet handles the resurrection of a {@link Creature}.<br>
	 * It stores the name of the entity and the specific {@code skillId} used.
	 * @param creature The {@code Creature} object to be resurrected.
	 * @param skillId The unique identifier for the resurrection skill.
	 */
	public SM_RESURRECT(Creature creature, int skillId)
	{
		name = creature.getName();
		this.skillId = skillId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeS(name);
		writeH(skillId); // unk
		writeD(0);
	}
}
