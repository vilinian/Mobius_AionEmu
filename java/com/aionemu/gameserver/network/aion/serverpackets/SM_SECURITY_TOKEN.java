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
 * This packet handles the security token exchange between the client and the server.<br>
 * It is used to verify the authenticity of a connection during the login process.
 * @author xXMashUpXx
 */
public class SM_SECURITY_TOKEN extends AionServerPacket
{
	private final String token;
	
	/**
	 * Creates a new {@code SM_SECURITY_TOKEN} object.<br>
	 * This constructor initializes the security token for the packet.
	 * @param token The unique string used as the security token.
	 */
	public SM_SECURITY_TOKEN(String token)
	{
		this.token = token;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeS(token, 64);
	}
}
