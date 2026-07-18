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
 * This packet handles the transmission of CAPTCHA verification data to the client.<br>
 * It is used to prevent automated bots from accessing the game server.
 * @author Cura
 */
public class SM_CAPTCHA extends AionServerPacket
{
	private final int type;
	private int count;
	private int size;
	private byte[] data;
	private boolean isCorrect;
	private int banTime;
	
	/**
	 * Creates a new {@code SM_CAPTCHA} packet.<br>
	 * This constructor initializes the captcha with specific data.
	 * @param count The number of attempts allowed.
	 * @param data The raw byte array containing the captcha information.
	 */
	public SM_CAPTCHA(int count, byte[] data)
	{
		type = 1;
		this.count = count;
		size = data.length;
		this.data = data;
	}
	
	/**
	 * Creates a new {@link SM_CAPTCHA} packet.<br>
	 * This constructor sets the result of the captcha check.<br>
	 * It also defines how long the user will be banned if they fail.
	 * @param isCorrect The boolean value indicating if the captcha was solved correctly.
	 * @param banTime The number of seconds to ban the user.
	 */
	public SM_CAPTCHA(boolean isCorrect, int banTime)
	{
		type = 3;
		this.isCorrect = isCorrect;
		this.banTime = banTime;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(type);
		
		switch (type)
		{
			case 0x01:
				writeC(count);
				writeD(size);
				writeB(data);
				break;
			case 0x03:
				writeH(isCorrect ? 1 : 0);
				
				// time setting can't be extracted (retail server default value:3000 sec)
				writeD(banTime);
				break;
		}
	}
}
