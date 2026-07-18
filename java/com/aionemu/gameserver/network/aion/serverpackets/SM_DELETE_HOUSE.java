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
 * This packet is sent to the client to notify it that a house has been deleted.<br>
 * It handles the removal of house data from the game world.
 * @author Rolandas
 */
public class SM_DELETE_HOUSE extends AionServerPacket
{
	private final int address;
	
	/**
	 * This packet is used to delete a house from the game world.<br>
	 * It identifies which house to remove using its unique ID.
	 * @param address The unique identifier of the house to be deleted.
	 */
	public SM_DELETE_HOUSE(int address)
	{
		this.address = address;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(address);
	}
}
