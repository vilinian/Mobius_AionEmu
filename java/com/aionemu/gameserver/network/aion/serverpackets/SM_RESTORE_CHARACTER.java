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
 * This packet represents the server's response to a {@code CM_RESTORE_CHARACTER} request.<br>
 * It informs the client about the result of a character restoration attempt.
 * @author -Nemesiss-
 */
public class SM_RESTORE_CHARACTER extends AionServerPacket
{
	/**
	 * Character object id.
	 */
	private final int chaOid;
	/**
	 * True if player was restored.
	 */
	private final boolean success;
	
	/**
	 * Creates a new {@code SM_RESTORE_CHARACTER} packet.<br>
	 * This packet informs the client about the result of a character restoration attempt.
	 * @param chaOid The unique identifier for the character.
	 * @param success A boolean indicating if the restoration was successful.
	 */
	public SM_RESTORE_CHARACTER(int chaOid, boolean success)
	{
		this.chaOid = chaOid;
		this.success = success;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(success ? 0x00 : 0x10); // unk
		writeD(chaOid);
	}
}
