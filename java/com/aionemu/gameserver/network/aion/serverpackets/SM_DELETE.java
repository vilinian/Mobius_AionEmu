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

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet informs the client that a specific {@link AionObject} is no longer visible.<br>
 * It is used to remove objects from the player's current view.
 * @author -Nemesiss-
 * @update FrozenKiller
 */
public class SM_DELETE extends AionServerPacket
{
	/**
	 * Object that is no longer visible.
	 */
	private final int objectId;
	private final int time;
	
	/**
	 * This method creates a new {@code SM_DELETE} packet.<br>
	 * It informs the client that an {@link AionObject} is no longer visible.<br>
	 * The packet stores the object ID and a specific time value.
	 * @param object The {@code AionObject} to be removed from view.
	 * @param time The timestamp associated with the deletion.
	 */
	public SM_DELETE(AionObject object, int time)
	{
		objectId = object.getObjectId();
		this.time = time;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(objectId);
		writeC(time); // removal animation speed
		writeC(time == 15 ? 0x00 : 0xFF);
	}
}
