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
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TIME_CHECK;

/**
 * This packet handles the {@code TIME_CHECK} request from the client.<br>
 * It is likely used for synchronization or as a ping/pong mechanism to check connection latency.
 * @author -Nemesiss-
 */
public class CM_TIME_CHECK extends AionClientPacket
{
	/**
	 * Nano time / 1000000
	 */
	private int nanoTime;
	
	/**
	 * Creates a new instance of the {@link CM_TIME_CHECK} packet.<br>
	 * This packet is used to synchronize time between the client and server.<br>
	 * It initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if applicable.
	 */
	public CM_TIME_CHECK(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		nanoTime = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final AionConnection client = getConnection();
		
		// int timeNow = (int) (System.nanoTime() / 1000000);
		// int diff = timeNow - nanoTime;
		client.sendPacket(new SM_TIME_CHECK(nanoTime));
		
		// log.info("CM_TIME_CHECK: " + nanoTime + " =?= " + timeNow + " dif: " + diff);
	}
}
