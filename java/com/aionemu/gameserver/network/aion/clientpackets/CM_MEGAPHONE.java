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

import java.util.Iterator;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MEGAPHONE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * Handles the client request to use a megaphone.<br>
 * This packet processes the message sent by a {@link Player} and broadcasts it to other players.<br>
 * It utilizes {@link SM_MEGAPHONE} to send the resulting server response back to the client.
 * @author Alcapwnd
 * @rework Ever'
 */
public class CM_MEGAPHONE extends AionClientPacket
{
	private String chatMessage;
	private int itemObjectId;
	private boolean isAll = false;
	
	/**
	 * Creates a new {@link CM_MEGAPHONE} packet.<br>
	 * This constructor initializes the packet with specific states.<br>
	 * It uses the base class logic to set up the network data.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_MEGAPHONE(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		chatMessage = readS();
		itemObjectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		if (activePlayer == null)
		{
			return;
		}
		
		final Item item = activePlayer.getInventory().getItemByObjId(itemObjectId);
		
		if (item == null)
		{
			return;
		}
		
		if ((item.getItemId() >= 188910000) && (item.getItemId() <= 188910009))
		{
			isAll = true;
		}
		
		if ((item.getItemId() >= 188930000) && (item.getItemId() <= 188930008))
		{
			isAll = true;
		}
		
		final boolean deleteItem = activePlayer.getInventory().decreaseByObjectId(itemObjectId, 1);
		if (!deleteItem)
		{
			return;
		}
		
		final Iterator<Player> players = World.getInstance().getPlayersIterator();
		while (players.hasNext())
		{
			final Player p = players.next();
			if (isAll)
			{
				PacketSendUtility.sendPacket(p, new SM_MEGAPHONE(activePlayer, chatMessage, item.getItemId(), isAll));
			}
			else if (activePlayer.getRace() == p.getRace())
			{
				PacketSendUtility.sendPacket(p, new SM_MEGAPHONE(activePlayer, chatMessage, item.getItemId(), isAll));
			}
		}
	}
}
