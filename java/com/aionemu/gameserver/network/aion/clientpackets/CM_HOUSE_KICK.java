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
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * Handles the client request to kick a player from a {@link House}.<br>
 * This packet processes the logic for removing a user from a specific house instance.
 * @author Rolandas
 */
public class CM_HOUSE_KICK extends AionClientPacket
{
	int option;
	
	/**
	 * Handles the request to kick a player from a house.<br>
	 * This packet processes the removal of a user from a specific housing area.<br>
	 * It uses the provided {@code State} values to determine the action context.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary status associated with the kick request.
	 * @param restStates Additional states that may affect the house kick logic.
	 */
	public CM_HOUSE_KICK(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		option = readC();
		readH();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		final House house = player.getActiveHouse();
		if (house == null)
		{
			AuditLogger.info(player, "Trying to kick players from house, but haven't own house");
			return;
		}
		
		if (option == 1)
		{
			house.getController().kickVisitors(player, false, false);
		}
		else if (option == 2)
		{
			house.getController().kickVisitors(player, true, false);
		}
	}
}
