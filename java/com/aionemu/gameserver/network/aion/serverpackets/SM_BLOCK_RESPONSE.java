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
 * This packet handles the server response for block list related requests.<br>
 * It informs the client of the result when a user attempts to manage their blocked list.
 * @author Ben
 */
public class SM_BLOCK_RESPONSE extends AionServerPacket
{
	/**
	 * You have blocked %0
	 */
	public static final int BLOCK_SUCCESSFUL = 0;
	/**
	 * You have unblocked %0
	 */
	public static final int UNBLOCK_SUCCESSFUL = 1;
	/**
	 * That character does not exist.
	 */
	public static final int TARGET_NOT_FOUND = 2;
	/**
	 * Your Block List is full.
	 */
	public static final int LIST_FULL = 3;
	/**
	 * You cannot block yourself.
	 */
	public static final int CANT_BLOCK_SELF = 4;
	private final int code;
	private final String playerName;
	
	/**
	 * Creates a new {@link SM_BLOCK_RESPONSE} packet.<br>
	 * This method initializes the response with a specific status and target name.
	 * @param code The result code from the class constants.
	 * @param playerName The name of the player involved in the block action.
	 */
	public SM_BLOCK_RESPONSE(int code, String playerName)
	{
		this.code = code;
		this.playerName = playerName;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeS(playerName);
		writeD(code);
	}
}
