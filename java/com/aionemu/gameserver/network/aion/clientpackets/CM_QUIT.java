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
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.player.PlayerLeaveWorldService;

/**
 * This packet is sent by the {@code Aion} client to request permission to disconnect.<br>
 * It informs the server that the player intends to quit the game.
 * @author -Nemesiss-
 */
public class CM_QUIT extends AionClientPacket
{
	/**
	 * Logout - if true player is wanted to go to character selection.
	 */
	private boolean logout;
	
	/**
	 * This method creates a new instance of the {@link CM_QUIT} packet.<br>
	 * It is used when a client requests to disconnect from the server.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state associated with the request.
	 * @param restStates Additional states that may be included in the packet.
	 */
	public CM_QUIT(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		logout = readC() == 1;
	}
	
	@Override
	protected void runImpl()
	{
		final AionConnection client = getConnection();
		
		Player player = null;
		if (client.getState() == State.IN_GAME)
		{
			player = client.getActivePlayer();
			
			// TODO! check if may quit
			if (!logout)
			{
				LoginServer.getInstance().aionClientDisconnected(client.getAccount().getId());
			}
			
			PlayerLeaveWorldService.startLeaveWorld(player);
			client.setActivePlayer(null);
		}
		
		if (logout)
		{
			if ((player != null) && player.isInEditMode())
			{
				sendPacket(new SM_QUIT_RESPONSE(true));
				player.setEditMode(false);
			}
			else
			{
				sendPacket(new SM_QUIT_RESPONSE());
			}
		}
		else
		{
			client.close(new SM_QUIT_RESPONSE(), false);
		}
	}
}
