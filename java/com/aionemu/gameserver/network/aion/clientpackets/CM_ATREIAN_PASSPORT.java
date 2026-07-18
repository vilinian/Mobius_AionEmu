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
import com.aionemu.gameserver.services.AtreianPassportService;

/**
 * Handles the client request to obtain an Atreian Passport.<br>
 * This packet interacts with the {@link AtreianPassportService} to process the player's passport status.
 * @author Falke_34
 */
public class CM_ATREIAN_PASSPORT extends AionClientPacket
{
	@SuppressWarnings("unused")
	private int passportId;
	
	/**
	 * Creates a new instance of the {@code CM_ATREIAN_PASSPORT} packet.<br>
	 * This constructor initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state associated with the packet.
	 * @param restStates A variable number of additional states for the packet.
	 */
	public CM_ATREIAN_PASSPORT(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		passportId = readD(); // Id
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		AtreianPassportService.getInstance().getReward(player, 11);
	}
}
