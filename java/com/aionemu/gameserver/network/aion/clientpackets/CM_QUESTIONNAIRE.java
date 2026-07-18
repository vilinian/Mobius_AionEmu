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

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.HTMLService;

/**
 * This class handles the {@code CM_QUESTIONNAIRE} packet sent from the client to the server.<br>
 * It is used to process user responses during a questionnaire or survey.<br>
 * It interacts with the {@link HTMLService} to manage related web content.
 * @author xTz
 */
public class CM_QUESTIONNAIRE extends AionClientPacket
{
	private int objectId;
	private int itemId;
	private int itemSize;
	private List<Integer> items;
	@SuppressWarnings("unused")
	private String stringItemsId;
	
	/**
	 * Creates a new instance of {@link CM_QUESTIONNAIRE}.<br>
	 * This constructor initializes the packet with its required states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state associated with the packet.
	 * @param restStates A variable number of additional states for the packet.
	 */
	public CM_QUESTIONNAIRE(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
		itemSize = readH();
		items = new ArrayList<>();
		for (int i = 0; i < itemSize; i++)
		{
			itemId = readD();
			items.add(itemId);
		}
		
		stringItemsId = readS();
	}
	
	@Override
	protected void runImpl()
	{
		if (objectId > 0)
		{
			final Player player = getConnection().getActivePlayer();
			HTMLService.getReward(player, objectId, items);
		}
	}
}
