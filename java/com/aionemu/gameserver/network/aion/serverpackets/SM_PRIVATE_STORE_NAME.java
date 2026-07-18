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
 * This packet handles the private store name information.<br>
 * It is sent to the client to display the specific name of a player's private store.
 * @author Simple
 */
public class SM_PRIVATE_STORE_NAME extends AionServerPacket
{
	/**
	 * Private store Information *
	 */
	private final int playerObjId;
	private final String name;
	
	/**
	 * Creates a new {@code SM_PRIVATE_STORE_NAME} packet.<br>
	 * This packet contains the name of a private store.<br>
	 * It links the store to a specific player object.
	 * @param playerObjId The unique ID of the player owner.
	 * @param name The display name of the private store.
	 */
	public SM_PRIVATE_STORE_NAME(int playerObjId, String name)
	{
		this.playerObjId = playerObjId;
		this.name = name;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(playerObjId);
		writeS(name);
	}
}
