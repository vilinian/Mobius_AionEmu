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
 * This packet handles the initialization of the chat system for a connected client.<br>
 * It is sent to set up initial chat settings and configurations.
 * @author ATracer
 */
public class SM_CHAT_INIT extends AionServerPacket
{
	private final byte[] token;
	
	/**
	 * Initializes a new chat session.<br>
	 * This method sets the required security token for the chat system.
	 * @param token The {@code byte[]} array containing the unique session token.
	 */
	public SM_CHAT_INIT(byte[] token)
	{
		this.token = token;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(token.length);
		writeB(token);
	}
}
