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
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the command to manually start a quest for a player.<br>
 * It allows Game Masters to trigger {@link QuestService} actions on a specific {@link Player}.<br>
 * This class processes the request and updates the character's quest status.
 * @author Alcapwnd
 */
public class CmdStartQuest extends AbstractGMHandler
{
	/**
	 * Initializes the quest starting command handler.<br>
	 * This method sets up the {@code admin} and {@code params} for the request.<br>
	 * It then triggers the {@code run} method to execute the logic.
	 * @param admin The {@code Player} object who is sending the command.
	 * @param params The string containing the quest details.
	 */
	public CmdStartQuest(Player admin, String params)
	{
		super(admin, params);
		run();
	}
	
	/**
	 * Executes the logic to start a specific quest for a player.<br>
	 * It parses the {@code params} to get the quest ID and validates it.<br>
	 * If the quest exists, it creates a new {@link QuestEnv} and starts it via {@link QuestService}.
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
			PacketSendUtility.sendMessage(admin, "Quest with ID: " + questID + "was not founded");
			return;
		}
		
		final QuestEnv env = new QuestEnv(null, t, questID, 0);
		QuestService.startQuest(env, 0);
	}
	
}
