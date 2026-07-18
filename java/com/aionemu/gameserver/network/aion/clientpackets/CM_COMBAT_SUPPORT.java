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
 * This class handles the {@code CM_COMBAT_SUPPORT} packet sent from the client to the server.<br>
 * It is used to provide support information for combat actions and mechanics.
 * @author Falke_34
 */
public class CM_COMBAT_SUPPORT extends AionClientPacket
{
	/**
	 * Creates a new instance of {@link CM_COMBAT_SUPPORT}.<br>
	 * This constructor initializes the packet with specific states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@code State} associated with the packet.
	 * @param restStates A variable number of additional {@code State} objects.
	 */
	public CM_COMBAT_SUPPORT(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		readC();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
	}
	
	@Override
	protected void runImpl()
	{
	}
}
