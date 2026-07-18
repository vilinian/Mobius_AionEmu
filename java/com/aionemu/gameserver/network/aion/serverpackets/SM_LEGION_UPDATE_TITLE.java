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
 * This packet updates the title of a specific legion.<br>
 * It is used to synchronize title changes between the server and the client.
 * @author sweetkr
 */
public class SM_LEGION_UPDATE_TITLE extends AionServerPacket
{
	private final int objectId;
	private final int legionId;
	private final String legionName;
	private final int rank;
	
	/**
	 * Updates the title information for a specific legion.<br>
	 * This packet sends data about the legion name and rank to the client.
	 * @param objectId The unique identifier of the target object.
	 * @param legionId The unique identifier of the legion.
	 * @param legionName The display name of the legion.
	 * @param rank The numerical rank within the legion.
	 */
	public SM_LEGION_UPDATE_TITLE(int objectId, int legionId, String legionName, int rank)
	{
		this.objectId = objectId;
		this.legionId = legionId;
		this.legionName = legionName;
		this.rank = rank;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(objectId);
		writeD(legionId);
		writeS(legionName);
		writeC(rank); // 0: commander(?), 1: centurion, 2: soldier
	}
}
