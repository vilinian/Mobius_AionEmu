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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnSearchResult;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHOW_NPC_ON_MAP;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the client request to search for specific objects or NPCs in the game world.<br>
 * This packet processes the search criteria and triggers the appropriate server-side logic.<br>
 * It interacts with {@link NpcTemplate} and {@link SpawnSearchResult} to identify valid targets.
 * @author Lyahim
 */
public class CM_OBJECT_SEARCH extends AionClientPacket
{
	private int npcId;
	
	/**
	 * This method creates a new instance of the {@code CM_OBJECT_SEARCH} packet.<br>
	 * It initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates A variable number of additional states for the connection.
	 */
	public CM_OBJECT_SEARCH(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		npcId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(0, npcId);
		final Player player = getConnection().getActivePlayer();
		final NpcTemplate npc = DataManager.NPC_DATA.getNpcTemplate(npcId);
		if (searchResult != null)
		{
			sendPacket(new SM_SHOW_NPC_ON_MAP(npcId, searchResult.getWorldId(), searchResult.getSpot().getX(), searchResult.getSpot().getY(), searchResult.getSpot().getZ()));
			if (player.isGM())
			{
				final RequestResponseHandler responseHandler = new RequestResponseHandler(player)
				{
					
					@Override
					public void acceptRequest(Creature requester, Player responder)
					{
						TeleportService2.teleportToNpc(player, npcId);
					}
					
					@Override
					public void denyRequest(Creature requester, Player responder)
					{
						// Do nothing
					}
				};
				
				player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_HOTSPOT_CONFIRM_NO_COST, responseHandler);
				PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_HOTSPOT_CONFIRM_NO_COST, 0, 0, npc.getName()));
			}
		}
	}
}
