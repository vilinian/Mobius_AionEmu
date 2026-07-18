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
 * This packet handles the teleportation of a player to their house.<br>
 * It is sent from the server to the client to update the character's location.
 * @author Rolandas
 */
public class SM_HOUSE_TELEPORT extends AionServerPacket
{
	int address;
	int playerId;
	
	/**
	 * This packet handles the teleportation of a player to a specific house.<br>
	 * It stores the destination address and the unique identifier for the player.
	 * @param houseAddress The unique ID of the house destination.
	 * @param playerId The unique ID of the player being moved.
	 */
	public SM_HOUSE_TELEPORT(int houseAddress, int playerId)
	{
		address = houseAddress;
		this.playerId = playerId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(address);
		writeD(playerId);
	}
}
