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
 * This packet handles the transmission of a client's MAC address to the server.<br>
 * It is used by the {@link AionConnection} to identify unique hardware devices.
 * @author Falke_34, FrozenKiller
 */
public class SM_MAC_ADDRESS extends AionServerPacket
{
	private final String macAddress;
	private final String hdd_serial;
	private final String local_ip;
	
	/**
	 * Creates a new instance of {@code SM_MAC_ADDRESS}.<br>
	 * This packet stores hardware identification details.
	 * @param macAddress The unique MAC address of the device.
	 * @param hdd_serial The serial number of the hard drive.
	 * @param local_ip The local IP address of the client.
	 */
	public SM_MAC_ADDRESS(String macAddress, String hdd_serial, String local_ip)
	{
		this.macAddress = macAddress;
		this.hdd_serial = hdd_serial;
		this.local_ip = local_ip;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeS(macAddress);
		writeS(hdd_serial);
		writeS(local_ip);
	}
}
