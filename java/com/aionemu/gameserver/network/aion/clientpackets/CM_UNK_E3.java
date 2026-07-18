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

/**
 * This class handles the {@code CM_UNK_E3} client packet.<br>
 * It serves as a placeholder for an unknown or undefined communication type from the client.
 * @author Falke_34
 */
public class CM_UNK_E3 extends AionClientPacket
{
	/**
	 * Creates a new instance of {@link CM_UNK_E3}.<br>
	 * This constructor initializes the packet with specific states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} associated with the packet.
	 * @param restStates A variable number of additional {@link State} objects.
	 */
	public CM_UNK_E3(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		// empty
	}
	
	@Override
	protected void runImpl()
	{
		// empty
	}
}
