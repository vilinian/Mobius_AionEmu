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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the client command for performing a roll action.<br>
 * This packet is processed by the server to execute character movement or animations related to rolling.
 * @author Rhys2002
 */
public class CM_CLIENT_COMMAND_ROLL extends AionClientPacket
{
	private int maxRoll;
	private int roll;
	
	/**
	 * This method creates a new {@code CM_CLIENT_COMMAND_ROLL} packet.<br>
	 * It initializes the packet with specific network states.
	 * @param opcode The unique identifier for this command.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_CLIENT_COMMAND_ROLL(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		maxRoll = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		roll = Rnd.get(1, maxRoll);
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400126, roll, maxRoll));
		PacketSendUtility.broadcastPacket(player, new SM_SYSTEM_MESSAGE(1400127, player.getName(), roll, maxRoll));
	}
}
