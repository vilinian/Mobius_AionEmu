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
 * This packet handles the update of notes within the game client.<br>
 * It is used to synchronize note information between the server and the player.
 * @author xavier
 */
public class SM_UPDATE_NOTE extends AionServerPacket
{
	private final int targetObjId;
	private final String note;
	
	/**
	 * Creates a new {@code SM_UPDATE_NOTE} packet.<br>
	 * This packet updates the note for a specific object.
	 * @param targetObjId The unique identifier of the target object.
	 * @param note The text content of the note to be displayed.
	 */
	public SM_UPDATE_NOTE(int targetObjId, String note)
	{
		this.targetObjId = targetObjId;
		this.note = note;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(targetObjId);
		writeS(note);
	}
}
