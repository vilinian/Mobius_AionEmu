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

import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the character selection process for a player.<br>
 * It is sent to the client to display the list of available characters.<br>
 * It inherits from {@link AionServerPacket}.
 * @author cura
 */
public class SM_CHARACTER_SELECT extends AionServerPacket
{
	private final int type; // 0: new passkey input window, 1: passkey input window, 2: message window
	private int messageType; // 0: newpasskey complete, 2: passkey edit complete, 3: passkey input
	private int unk;
	private int wrongCount;
	
	/**
	 * Creates a new {@code SM_CHARACTER_SELECT} packet.<br>
	 * This method initializes the packet with a specific type.
	 * @param type The type of character selection window to display.
	 */
	public SM_CHARACTER_SELECT(int type)
	{
		this.type = type;
	}
	
	/**
	 * This constructor initializes a new {@code SM_CHARACTER_SELECT} packet.<br>
	 * It sets the required fields for character selection and passkey input.
	 * @param type The type of window to display.
	 * @param messageType The specific status or action type for the message.
	 * @param unk An unknown value used by the server.
	 * @param wrongCount The number of incorrect attempts made.
	 */
	public SM_CHARACTER_SELECT(int type, int messageType, int unk, int wrongCount)
	{
		this.type = type;
		this.messageType = messageType;
		this.unk = unk;
		this.wrongCount = wrongCount;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(type);
		
		switch (type)
		{
			case 0:
				break;
			case 1:
				break;
			case 2:
				writeC(messageType); // 0: newpasskey complete, 2: passkey edit complete, 3: passkey input
				writeC(unk);
				writeC(wrongCount > 0 ? 1 : 0); // 0: right passkey, 1: wrong passkey
				writeD(wrongCount); // wrong passkey input count
				writeD(SecurityConfig.PASSKEY_WRONG_MAXCOUNT); // Enter the number of possible wrong numbers (retail
				
				// server default value: 5)
				break;
		}
	}
}
