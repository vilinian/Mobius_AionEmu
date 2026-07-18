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
package com.aionemu.gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for starting a quest when an item is used.<br>
 * It interacts with the {@link QuestEngine} to initiate the quest process for a {@link Player}.
 * @author Nemiroff Date: 17.12.2009
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestStartAction")
public class QuestStartAction extends AbstractItemAction
{
	@XmlAttribute
	protected int questid;
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It checks the current quest status and repeatability rules.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		final QuestState qs = player.getQuestStateList().getQuestState(questid);
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
		{
			return true;
		}
		else if (qs.getStatus() != QuestStatus.COMPLETE)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_QUEST_ACQUIRE_ERROR_WORKING_QUEST);
		}
		else if (!qs.canRepeat())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_QUEST_ACQUIRE_ERROR_NONE_REPEATABLE(DataManager.QUEST_DATA.getQuestById(questid).getName()));
		}
		
		return false;
	}
	
	/**
	 * Starts the quest dialog for the player.<br>
	 * This method triggers a {@link QuestEngine} dialog to ask the {@link Player} to accept a quest.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		QuestEngine.getInstance().onDialog(new QuestEnv(null, player, questid, DialogAction.ASK_QUEST_ACCEPT.id()));
	}
}
