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
 * This packet is used to force a {@link Creature} to move to a specific location.<br>
 * It is typically sent by the server to synchronize movement or handle teleportation.
 * @author Sweetkr
 */
public class SM_FORCED_MOVE extends AionServerPacket
{
	private final Creature creature;
	private final int objectId;
	private final float x;
	private final float y;
	private final float z;
	
	/**
	 * Creates a forced move packet for a specific creature.<br>
	 * This method moves the {@code creature} to the location of the {@code target}.<br>
	 * It uses the coordinates from the {@link Creature} object.
	 * @param creature The creature that will be moved.
	 * @param target The creature whose position will be used as the destination.
	 */
	public SM_FORCED_MOVE(Creature creature, Creature target)
	{
		this(creature, target.getObjectId(), target.getX(), target.getY(), target.getZ());
	}
	
	/**
	 * This packet forces a {@link Creature} to move to a specific location.<br>
	 * It updates the position of an object based on provided coordinates.
	 * @param creature The {@code Creature} being moved.
	 * @param objectId The unique identifier for the target object.
	 * @param x The new X coordinate.
	 * @param y The new Y coordinate.
	 * @param z The new Z coordinate.
	 */
	public SM_FORCED_MOVE(Creature creature, int objectId, float x, float y, float z)
	{
		this.creature = creature;
		this.objectId = objectId;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(creature.getObjectId());
		writeD(objectId); // targets objectId
		writeC(16); // unk
		writeF(x);
		writeF(y);
		writeF(z);
	}
}
