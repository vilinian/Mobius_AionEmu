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
 * This packet handles the information sent when a player requests to join a legion.<br>
 * It contains the necessary data for the server to process the {@code SM_PLAYER_LEGION_JOIN_REQUEST} request.
 * @author CoolyT
 */
public class SM_PLAYER_LEGION_JOIN_REQUEST_INFO extends AionServerPacket
{
	private final int legionId;
	private final String legionName;
	
	/**
	 * Creates a new request for a player to join a specific legion.<br>
	 * This packet stores the unique identifier and name of the target legion.
	 * @param legionId The unique {@code int} ID of the legion.
	 * @param legionName The {@code String} name of the legion.
	 */
	public SM_PLAYER_LEGION_JOIN_REQUEST_INFO(int legionId, String legionName)
	{
		this.legionId = legionId;
		this.legionName = legionName;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(legionId);
		writeS(legionName);
	}
}
