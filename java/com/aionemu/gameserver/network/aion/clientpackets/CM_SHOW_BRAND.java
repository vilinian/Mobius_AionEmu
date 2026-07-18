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
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHOW_BRAND;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the client request to display a brand.<br>
 * This packet is used to notify the server that a player wants to show their brand.<br>
 * It triggers the logic required to send {@link SM_SHOW_BRAND} to nearby players.
 * @author Sweetkr
 * @author Simple
 */
public class CM_SHOW_BRAND extends AionClientPacket
{
	@SuppressWarnings("unused")
	private int action;
	private int brandId;
	private int targetObjectId;
	
	/**
	 * This method initializes a new {@link CM_SHOW_BRAND} packet.<br>
	 * It sets the required network states for the client communication.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_SHOW_BRAND(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		action = readD();
		brandId = readD();
		targetObjectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		if (player.isInGroup2())
		{
			if (player.getPlayerGroup2().isLeader(player))
			{
				PlayerGroupService.showBrand(player, targetObjectId, brandId);
			}
		}
		else if (player.isInAlliance2())
		{
			if (player.getPlayerAlliance2().isSomeCaptain(player))
			{
				PlayerAllianceService.showBrand(player, targetObjectId, brandId);
			}
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_SHOW_BRAND(brandId, targetObjectId));
		}
	}
}
