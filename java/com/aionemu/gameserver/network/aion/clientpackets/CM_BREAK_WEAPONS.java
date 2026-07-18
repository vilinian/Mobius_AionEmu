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
import com.aionemu.gameserver.services.ArmsfusionService;

/**
 * Handles the client request to break weapons.<br>
 * This packet is used to initiate the destruction of a weapon item.<br>
 * It interacts with the {@link ArmsfusionService} to process the logic.
 * @author zdead
 */
public class CM_BREAK_WEAPONS extends AionClientPacket
{
	/**
	 * This method handles the weapon breaking request from the client.<br>
	 * It initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states associated with the packet.
	 */
	public CM_BREAK_WEAPONS(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	private int weaponToBreakUniqueId;
	
	@Override
	protected void readImpl()
	{
		readD();
		weaponToBreakUniqueId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		ArmsfusionService.breakWeapons(getConnection().getActivePlayer(), weaponToBreakUniqueId);
	}
}
