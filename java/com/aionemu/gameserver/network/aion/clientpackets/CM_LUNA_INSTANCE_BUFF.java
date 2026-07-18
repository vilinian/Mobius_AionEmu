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
import com.aionemu.gameserver.services.player.LunaShopService;

/**
 * Handles the client request to apply a buff within the Luna instance.<br>
 * This packet interacts with {@link LunaShopService} to process the requested effect.
 */
public class CM_LUNA_INSTANCE_BUFF extends AionClientPacket
{
	private int buffId;
	
	/**
	 * This constructor initializes a new {@code CM_LUNA_INSTANCE_BUFF} packet.<br>
	 * It passes the required network information to the parent class.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the client.
	 * @param restStates A variable number of additional connection states.
	 */
	public CM_LUNA_INSTANCE_BUFF(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		buffId = readD();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
		readC();
	}
	
	@Override
	protected void runImpl()
	{
		LunaShopService.getInstance().buyLunaBuff(getConnection().getActivePlayer(), buffId);
	}
}
