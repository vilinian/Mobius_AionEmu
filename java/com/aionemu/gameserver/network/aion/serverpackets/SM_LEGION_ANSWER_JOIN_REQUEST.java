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
 * This packet handles the server's response to a player's request to join a legion.<br>
 * It informs the client whether the join attempt was successful or failed.
 * @author CoolyT
 */
public class SM_LEGION_ANSWER_JOIN_REQUEST extends AionServerPacket
{
	private final int requesterId;
	private final boolean allowed;
	
	/**
	 * Creates a new request to join a legion.<br>
	 * This packet handles the response for a player's join attempt.
	 * @param requesterId The unique ID of the player who sent the request.
	 * @param allowed Set to {@code true} if the join is successful, or {@code false} otherwise.
	 */
	public SM_LEGION_ANSWER_JOIN_REQUEST(int requesterId, boolean allowed)
	{
		this.requesterId = requesterId;
		this.allowed = allowed;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(requesterId);
		writeC(allowed ? 1 : 0);
	}
}
