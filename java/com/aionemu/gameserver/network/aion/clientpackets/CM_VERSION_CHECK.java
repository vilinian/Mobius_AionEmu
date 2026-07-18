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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AFTER_TIME_CHECK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UNK_16A;
import com.aionemu.gameserver.network.aion.serverpackets.SM_VERSION_CHECK;

/**
 * This packet is sent by the client to verify the server version.<br>
 * It allows the game server to ensure compatibility with the connected client.
 * @author -Nemesiss-
 */
public class CM_VERSION_CHECK extends AionClientPacket
{
	/**
	 * Aion Client version
	 */
	private int version;
	@SuppressWarnings("unused")
	private int subversion;
	@SuppressWarnings("unused")
	private int windowsEncoding;
	@SuppressWarnings("unused")
	private int windowsVersion;
	@SuppressWarnings("unused")
	private int windowsSubVersion;
	
	/**
	 * Creates a new instance of the {@link CM_VERSION_CHECK} packet.<br>
	 * This method initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if they exist.
	 */
	public CM_VERSION_CHECK(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		version = readH();
		subversion = readH();
		windowsEncoding = readD();
		windowsVersion = readD();
		windowsSubVersion = readD();
		readC(); // always 2?
	}
	
	@Override
	protected void runImpl()
	{
		sendPacket(new SM_VERSION_CHECK(version));
		sendPacket(new SM_AFTER_TIME_CHECK());
		sendPacket(new SM_UNK_16A()); // TODO
	}
}
