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
 * This packet serves as the server response to a {@code CM_QUIT} request.<br>
 * It informs the client that the logout process has been acknowledged.
 * @author -Nemesiss-
 */
public class SM_QUIT_RESPONSE extends AionServerPacket
{
	private boolean edit_mode = false;
	
	/**
	 * Creates a new instance of the {@code SM_QUIT_RESPONSE} packet.<br>
	 * This is used to respond to a character's quit request.
	 */
	public SM_QUIT_RESPONSE()
	{
	}
	
	/**
	 * Creates a new {@link SM_QUIT_RESPONSE} packet.<br>
	 * This method initializes the response with a specific mode.
	 * @param edit_mode The state of the edit mode as a {@code boolean}.
	 */
	public SM_QUIT_RESPONSE(boolean edit_mode)
	{
		this.edit_mode = edit_mode;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(edit_mode ? 2 : 1); // 1 normal, 2 plastic surgery/gender switch
		writeC(0x00); // unk
		writeC(0xFF); // unk 3.0
		writeC(0xFF); // unk 3.0
		writeC(0xFF); // unk 3.0
		writeC(0xFF); // unk 3.0
	}
}
