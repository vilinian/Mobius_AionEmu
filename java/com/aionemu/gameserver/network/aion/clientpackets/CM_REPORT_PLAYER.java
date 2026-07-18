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
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * This packet is received when a player reports another player using the {@code /ReportAutoHunting} command.<br>
 * It handles the initial request to flag a user for automated hunting behavior.
 * @author Jego
 */
public class CM_REPORT_PLAYER extends AionClientPacket
{
	private String player;
	
	/**
	 * This method creates a new {@link CM_REPORT_PLAYER} packet.<br>
	 * It handles reports sent when a player uses the /ReportAutoHunting command.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary status of the report.
	 * @param restStates Additional status states associated with the report.
	 */
	public CM_REPORT_PLAYER(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		readB(1); // unknown byte.
		player = readS(); // the name of the reported person.
	}
	
	@Override
	protected void runImpl()
	{
		final Player p = getConnection().getActivePlayer();
		AuditLogger.info(p, "Reports the player: " + player);
	}
}
