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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerScripts;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_SCRIPTS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the client request to execute a script associated with a {@link House}.<br>
 * This packet triggers specific logic defined in {@link PlayerScripts} for house interactions.
 * @author Rolandas
 */
public class CM_HOUSE_SCRIPT extends AionClientPacket
{
	int address;
	int scriptIndex;
	int totalSize;
	int compressedSize;
	int uncompressedSize;
	byte[] stream;
	
	/**
	 * Creates a new instance of the {@code CM_HOUSE_SCRIPT} packet.<br>
	 * This constructor initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_HOUSE_SCRIPT(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		address = readD();
		scriptIndex = readC();
		totalSize = readH();
		if (totalSize > 0)
		{
			compressedSize = readD();
			if (compressedSize < 8150)
			{
				uncompressedSize = readD();
				stream = readB(compressedSize);
			}
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		if (compressedSize > 8149)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_SCRIPT_OVERFLOW);
		}
		
		final House house = player.getActiveHouse();
		if (house == null)
		{
			return;
		}
		
		final PlayerScripts scripts = house.getPlayerScripts();
		
		if (totalSize <= 0)
		{
			// Deposit should perhaps send 0 while delete sends -1, but the client currently sends the same packets for both.
			scripts.addScript(scriptIndex, new byte[0], 0);
		}
		else
		{
			scripts.addScript(scriptIndex, stream, uncompressedSize);
		}
		
		PacketSendUtility.sendPacket(player, new SM_HOUSE_SCRIPTS(address, scripts, scriptIndex, scriptIndex));
	}
}
