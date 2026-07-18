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
 * This packet provides information about the current number of instances.<br>
 * It is used to synchronize instance counts between the server and the client.
 * @author xTz
 */
public class SM_INSTANCE_COUNT_INFO extends AionServerPacket
{
	private final int mapId;
	private final int instanceId;
	
	/**
	 * Creates a new {@code SM_INSTANCE_COUNT_INFO} packet.<br>
	 * This packet stores information about a specific map and its instance.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the instance.
	 */
	public SM_INSTANCE_COUNT_INFO(int mapId, int instanceId)
	{
		this.mapId = mapId;
		this.instanceId = instanceId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(mapId);
		writeD(instanceId);
		writeD(1); // 1 solo 31 group 61 alliance unk for league
	}
}
