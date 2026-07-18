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
 * This packet represents the server's response to a {@code CM_DELETE_CHARACTER} request.<br>
 * It informs the client whether the character deletion was successful.
 * @author -Nemesiss-
 */
public class SM_DELETE_CHARACTER extends AionServerPacket
{
	private final int playerObjId;
	private final int deletionTime;
	
	/**
	 * This method creates a new {@code SM_DELETE_CHARACTER} packet.<br>
	 * It handles the character deletion request from the server.
	 * @param playerObjId The unique identifier for the player object.
	 * @param deletionTime The timestamp when the character will be deleted.
	 */
	public SM_DELETE_CHARACTER(int playerObjId, int deletionTime)
	{
		this.playerObjId = playerObjId;
		this.deletionTime = deletionTime;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		if (playerObjId != 0)
		{
			writeD(0x00); // unk
			writeD(playerObjId);
			writeD(deletionTime);
		}
		else
		{
			writeD(0x10); // unk
			writeD(0x00);
			writeD(0x00);
		}
	}
}
