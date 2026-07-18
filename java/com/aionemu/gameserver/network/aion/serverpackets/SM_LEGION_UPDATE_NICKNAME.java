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
 * This packet handles the update of a nickname for a specific legion.<br>
 * It is sent to clients to synchronize name changes within the game world.
 * @author Simple
 */
public class SM_LEGION_UPDATE_NICKNAME extends AionServerPacket
{
	private final int playerObjId;
	private final String newNickname;
	
	/**
	 * Updates the nickname for a specific player in the legion.<br>
	 * This packet handles the name change request.
	 * @param playerObjId The unique ID of the player object.
	 * @param newNickname The new name to be assigned to the player.
	 */
	public SM_LEGION_UPDATE_NICKNAME(int playerObjId, String newNickname)
	{
		this.playerObjId = playerObjId;
		this.newNickname = newNickname;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(playerObjId);
		writeS(newNickname);
	}
}
