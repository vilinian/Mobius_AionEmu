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
import com.aionemu.gameserver.dataholders.QuestsData;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.services.QuestService;

/**
 * Handles the client request to delete a specific quest.<br>
 * This packet is processed by the {@link QuestService} to remove the quest from the player's active list.
 */
public class CM_DELETE_QUEST extends AionClientPacket
{
	static QuestsData questsData = DataManager.QUEST_DATA;
	public int questId;
	
	/**
	 * This method creates a new {@code CM_DELETE_QUEST} packet.<br>
	 * It initializes the packet with the required network states.<br>
	 * Use this constructor to handle quest deletion requests from the client.
	 * @param opcode The unique identifier for the packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_DELETE_QUEST(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		questId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		final QuestTemplate qt = questsData.getQuestById(questId);
		
		if ((qt != null) && qt.isTimer())
		{
			player.getController().cancelTask(TaskId.QUEST_TIMER);
			sendPacket(new SM_QUEST_ACTION(questId, 0));
		}
		
		if (!QuestService.abandonQuest(player, questId))
		{
			return;
		}
		
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
}
