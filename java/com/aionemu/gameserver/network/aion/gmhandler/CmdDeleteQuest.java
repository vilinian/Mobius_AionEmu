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
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.QuestStateList;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.QuestCategory;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_COMPLETED_LIST;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

/**
 * Handles the command to delete a specific quest from a player's progress.<br>
 * It removes the {@code QuestState} and updates the player's {@link QuestStateList}.<br>
 * This class is used by Game Masters to manage player quest data.
 * @author Alcapwnd
 */
public class CmdDeleteQuest extends AbstractGMHandler
{
	/**
	 * This constructor initializes a new {@link CmdDeleteQuest} instance.<br>
	 * It takes an administrator and the command parameters to process the request.
	 * @param admin The {@code Player} object representing the administrator who sent the command.
	 * @param params The string containing the specific quest details to be deleted.
	 */
	public CmdDeleteQuest(Player admin, String params)
	{
		super(admin, params);
		run();
	}
	
	/**
	 * Executes the logic to delete a specific quest from a player.<br>
	 * It identifies the target player and retrieves the quest by its {@code questID}.<br>
	 * If the quest exists, it resets the progress and updates the player's state.
	 */
	private void run()
	{
		Player t = admin;
		
		if ((admin.getTarget() != null) && (admin.getTarget() instanceof Player))
		{
			t = World.getInstance().findPlayer(Util.convertName(admin.getTarget().getName()));
		}
		
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
			PacketSendUtility.sendMessage(admin, "Quest not deleted for target " + t.getName());
			return;
		}
		
		final QuestState qs = list.getQuestState(questID);
		qs.setQuestVar(0);
		qs.setCompleteCount(0);
		if (qt.getCategory() == QuestCategory.MISSION)
		{
			qs.setStatus(QuestStatus.START);
		}
		else
		{
			qs.setStatus(null);
		}
		
		if (qs.getPersistentState() != PersistentState.NEW)
		{
			qs.setPersistentState(PersistentState.DELETED);
		}
		
		PacketSendUtility.sendPacket(t, new SM_QUEST_COMPLETED_LIST(t.getQuestStateList().getAllFinishedQuests()));
		t.getController().updateNearbyQuests();
	}
	
}
