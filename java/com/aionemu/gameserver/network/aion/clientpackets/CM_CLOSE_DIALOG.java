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

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HEADING_UPDATE;
import com.aionemu.gameserver.services.DialogService;
import com.aionemu.gameserver.services.player.PlayerMailboxState;

/**
 * Handles the client request to close an active dialog window.<br>
 * This packet informs the server that the player has finished interacting with a {@link Npc} or other interactive object.
 */
public class CM_CLOSE_DIALOG extends AionClientPacket
{
	/**
	 * Target object id that client wants to TALK WITH or 0 if wants to unselect
	 */
	private int targetObjectId;
	
	/**
	 * This method handles the closing of a dialog window.<br>
	 * It processes the request from the client to end an interaction.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} associated with the packet.
	 * @param restStates A variable number of additional {@link State} objects.
	 */
	public CM_CLOSE_DIALOG(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		targetObjectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		final VisibleObject obj = player.getKnownList().getObject(targetObjectId);
		final AionConnection client = getConnection();
		if (obj == null)
		{
			return;
		}
		
		if (obj instanceof Npc)
		{
			final Npc npc = (Npc) obj;
			npc.getAi2().onCreatureEvent(AIEventType.DIALOG_FINISH, player);
			DialogService.onCloseDialog(npc, player);
			
			ThreadPoolManager.getInstance().schedule((Runnable) () -> client.sendPacket(new SM_HEADING_UPDATE(targetObjectId, obj.getHeading())), 1200);
			
		}
		
		if (player.getMailbox().mailBoxState != 0)
		{
			player.getMailbox().mailBoxState = PlayerMailboxState.CLOSED;
		}
	}
}
