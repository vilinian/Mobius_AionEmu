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
 * This packet handles the announcement of windstream data to the client.<br>
 * It is used to synchronize environmental effects within the game world.
 * @author LokiReborn
 */
public class SM_WINDSTREAM_ANNOUNCE extends AionServerPacket
{
	private final int bidirectional;
	private final int mapId;
	private final int streamId;
	private final int state;
	
	/**
	 * This constructor initializes a new {@code SM_WINDSTREAM_ANNOUNCE} packet.<br>
	 * It sets the required network and map data for the wind stream announcement.
	 * @param bidirectional The directionality of the stream.
	 * @param mapId The unique identifier for the map.
	 * @param streamId The specific ID of the stream.
	 * @param state The current status or state of the stream.
	 */
	public SM_WINDSTREAM_ANNOUNCE(int bidirectional, int mapId, int streamId, int state)
	{
		this.bidirectional = bidirectional;
		this.mapId = mapId;
		this.streamId = streamId;
		this.state = state;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(bidirectional);
		writeD(mapId);
		writeD(streamId);
		writeC(state);
	}
}
