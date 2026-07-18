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
 * Handles the client request to check the size of a specific mail.<br>
 * This packet allows the client to verify if the content fits within the allowed limits.
 */
public class CM_CHECK_MAIL_SIZE extends AionClientPacket
{
	public int onlyExpress;
	
	/**
	 * This method initializes a new {@code CM_CHECK_MAIL_SIZE} packet.<br>
	 * It sets the required network information for the client request.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional states associated with the connection.
	 */
	public CM_CHECK_MAIL_SIZE(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		onlyExpress = readC();
	}
	
	@Override
	protected void runImpl()
	{
		// We will still use postboxai2 for handle this. it's maybe better :)
	}
}
