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
import com.aionemu.gameserver.services.ArmsfusionService;

/**
 * Handles the client request to initiate a weapon fusion process.<br>
 * This packet communicates with {@link ArmsfusionService} to manage item merging.
 * @author zdead modified by Wakizashi
 */
public class CM_FUSION_WEAPONS extends AionClientPacket
{
	/**
	 * Creates a new instance of the {@link CM_FUSION_WEAPONS} packet.<br>
	 * This constructor initializes the packet with specific network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state associated with the packet.
	 * @param restStates A variable number of additional states for the packet.
	 */
	public CM_FUSION_WEAPONS(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	private int firstItemId;
	private int secondItemId;
	
	@Override
	protected void readImpl()
	{
		readD();
		firstItemId = readD();
		secondItemId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		ArmsfusionService.fusionWeapons(getConnection().getActivePlayer(), firstItemId, secondItemId);
	}
}
