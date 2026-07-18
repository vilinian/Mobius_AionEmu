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
 * This packet handles the character rename request.<br>
 * It is sent from the server to the client to confirm a name change.
 * @author Rhys2002
 */
public class SM_RENAME extends AionServerPacket
{
	private final int playerObjectId;
	private final String oldName;
	private final String newName;
	
	/**
	 * This packet handles a request to change a character name.<br>
	 * It stores the current identity and the requested name change.
	 * @param playerObjectId The unique ID of the player.
	 * @param oldName The current name of the character.
	 * @param newName The new name to be assigned.
	 */
	public SM_RENAME(int playerObjectId, String oldName, String newName)
	{
		this.playerObjectId = playerObjectId;
		this.oldName = oldName;
		this.newName = newName;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(0); // unk
		writeD(0); // unk - 0 or 3
		writeD(playerObjectId);
		writeS(oldName);
		writeS(newName);
	}
}
