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
 * This packet handles fast track movement for characters.<br>
 * It is used to synchronize rapid position updates between the server and the client.
 * @author Alcapwnd
 * @TODO need to implement this packet fully, its only dummy actual
 */
public class SM_FAST_TRACK_MOVE extends AionServerPacket
{
	private final int currentServerId;
	private final int newServerId;
	private final int mapId;
	
	/**
	 * This method creates a new {@code SM_FAST_TRACK_MOVE} packet.<br>
	 * It handles the movement of a player between different servers.
	 * @param currentServer The ID of the server where the player is currently located.
	 * @param newServerId The ID of the destination server for the move.
	 * @param mapId The specific map ID on the destination server.
	 */
	public SM_FAST_TRACK_MOVE(int currentServer, int newServerId, int mapId)
	{
		currentServerId = currentServer;
		this.newServerId = newServerId;
		this.mapId = mapId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(newServerId);
		writeD(currentServerId);
		writeC(0);
		writeD(mapId);
	}
}
