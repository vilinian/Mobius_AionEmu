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
package com.aionemu.loginserver.network.aion.serverpackets;

import com.aionemu.loginserver.network.aion.AionAuthResponse;
import com.aionemu.loginserver.network.aion.AionServerPacket;
import com.aionemu.loginserver.network.aion.LoginConnection;

/**
 * This packet is sent to the client when a login attempt fails.<br>
 * It informs the user that their credentials or connection status are invalid.
 * @author KID
 */
public class SM_LOGIN_FAIL extends AionServerPacket
{
	/**
	 * response - why login fail
	 */
	private final AionAuthResponse response;
	
	/**
	 * Creates a new {@code SM_LOGIN_FAIL} packet.<br>
	 * This packet is sent when a login attempt fails.<br>
	 * It contains the reason for the failure from the auth response.
	 * @param response The {@link AionAuthResponse} containing the error details.
	 */
	public SM_LOGIN_FAIL(AionAuthResponse response)
	{
		super(0x01);
		this.response = response;
	}
	
	@Override
	protected void writeImpl(LoginConnection con)
	{
		writeD(response.getMessageId());
	}
}
