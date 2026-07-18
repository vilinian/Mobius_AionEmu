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
 * This packet is sent to the client when a {@code Godstone} is destroyed.<br>
 * It handles the visual and state updates for removing a stone from the world.
 * @author Falke_34
 */
public class SM_GODSTONE_DESTROY extends AionServerPacket
{
	private final int objectId;
	private final int godstoneId;
	private final int time;
	
	/**
	 * This packet handles the destruction of a godstone.<br>
	 * It sends information about which object is being removed.<br>
	 * It also includes the specific godstone identifier and the timestamp.
	 * @param objectId The unique ID of the game object.
	 * @param godstoneId The unique ID of the godstone.
	 * @param time The server time when the destruction occurred.
	 */
	public SM_GODSTONE_DESTROY(int objectId, int godstoneId, int time)
	{
		this.objectId = objectId;
		this.godstoneId = godstoneId;
		this.time = time;
	}
	
	@Override
	protected void writeImpl(AionConnection aionConnection)
	{
		writeD(objectId);
		writeD(godstoneId);
		writeD(time);
	}
}
