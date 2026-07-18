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
 * This packet handles the alliance ready check mechanism.<br>
 * It is used to verify if an alliance is prepared for specific actions.<br>
 * It extends {@link AionServerPacket} to communicate with the client.
 * @author Sarynth (Thx Rhys2002 for Packets)
 */
public class SM_ALLIANCE_READY_CHECK extends AionServerPacket
{
	private final int playerObjectId;
	private final int statusCode;
	
	/**
	 * This method creates a new {@code SM_ALLIANCE_READY_CHECK} packet.<br>
	 * It initializes the packet with specific player and status data.
	 * @param playerObjectId The unique ID of the player involved in the check.
	 * @param statusCode The current status code for the alliance readiness.
	 */
	public SM_ALLIANCE_READY_CHECK(int playerObjectId, int statusCode)
	{
		this.playerObjectId = playerObjectId;
		this.statusCode = statusCode;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(playerObjectId);
		writeC(statusCode);
	}
}
