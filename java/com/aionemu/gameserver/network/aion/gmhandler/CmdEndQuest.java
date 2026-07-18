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
package com.aionemu.gameserver.network.aion.gmhandler;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.QuestStateList;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_COMPLETED_LIST;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the GM command to manually complete a quest for a specific player.<br>
 * It updates the {@link QuestStateList} and notifies the client via {@link SM_QUEST_COMPLETED_LIST}.
 * @author Alcapwnd
 */
public class CmdEndQuest extends AbstractGMHandler
{
	/**
	 * This constructor initializes the {@code CmdEndQuest} handler.<br>
	 * It sets up the admin player and the command parameters.<br>
	 * It then triggers the execution of the quest completion logic.
	 * @param admin The {@link Player} who is executing the command.
	 * @param params The string containing the arguments for the command.
	 */
	public CmdEndQuest(Player admin, String params)
	{
		super(admin, params);
		run();
	}
	
	/**
	 * Executes the logic to complete a specific quest for a player.<br>
	 * It validates the {@code questID} and checks if the target has an active quest.<br>
	 * If valid, it updates the status to {@code REWARD} and triggers the reward process.
	 */
	private void run()
	{
		final Player t = target != null ? target : admin;
		
		final Integer questID = Integer.parseInt(params);
		if (questID <= 0)
		{
			return;
		}
		
		DataManager.getInstance();
		final QuestTemplate qt = DataManager.QUEST_DATA.getQuestById(questID);
		if (qt == null)
		{
			PacketSendUtility.sendMessage(admin, "Quest with ID: " + questID + " was not found");
			return;
		}
		
		final QuestStateList list = t.getQuestStateList();
		if ((list == null) || (list.getQuestState(questID) == null))
		{
			PacketSendUtility.sendMessage(admin, "Quest not founded for target " + t.getName());
			return;
		}
		
		if (list.getQuestState(questID).getStatus() == QuestStatus.COMPLETE)
		{
			PacketSendUtility.sendMessage(admin, "Quest allready finished");
			return;
		}
		
		list.getQuestState(questID).setStatus(QuestStatus.REWARD);
		t.getController().updateNearbyQuests();
		final QuestEnv env = new QuestEnv(null, t, questID, 0);
		QuestService.finishQuest(env);
		PacketSendUtility.sendPacket(t, new SM_QUEST_COMPLETED_LIST(t.getQuestStateList().getAllFinishedQuests()));
		t.getController().updateNearbyQuests();
	}
	
}
