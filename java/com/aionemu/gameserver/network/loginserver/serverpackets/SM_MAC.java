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
package com.aionemu.gameserver.network.loginserver.serverpackets;

import com.aionemu.gameserver.network.loginserver.LoginServerConnection;
import com.aionemu.gameserver.network.loginserver.LsServerPacket;

/**
 * This packet handles the Message Authentication Code (MAC) verification during the login process.<br>
 * It ensures that the communication between the client and the {@link LoginServerConnection} is secure.
 * @author nrg, Alex
 */
public class SM_MAC extends LsServerPacket
{
	private final int accountId;
	private final String address;
	private final String hdd;
	
	/**
	 * Creates a new {@code SM_MAC} packet.<br>
	 * This packet stores login information for an account.<br>
	 * It is used by the {@link LsServerPacket} system.
	 * @param accountId The unique identifier for the user account.
	 * @param address The IP address of the connecting client.
	 * @param hdd The hardware ID of the client device.
	 */
	public SM_MAC(int accountId, String address, String hdd)
	{
		super(13);
		this.accountId = accountId;
		this.address = address;
		this.hdd = hdd;
	}
	
	@Override
	protected void writeImpl(LoginServerConnection con)
	{
		writeD(accountId);
		writeS(address);
		writeS(hdd);
	}
}
