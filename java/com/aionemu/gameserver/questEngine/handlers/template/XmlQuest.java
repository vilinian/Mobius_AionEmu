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
package com.aionemu.gameserver.questEngine.handlers.template;

import java.util.Iterator;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.models.Monster;
import com.aionemu.gameserver.questEngine.handlers.models.XmlQuestData;
import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.events.OnKillEvent;
import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.events.OnTalkEvent;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * This class handles quests that are defined using {@code XML} configuration files.<br>
 * It serves as a template for processing quest logic and data provided by {@link XmlQuestData}.
 * @author Mr. Poke, modified Bobobear
 */
public class XmlQuest extends QuestHandler
{
	private final XmlQuestData xmlQuestData;
	
	/**
	 * Creates a new instance of {@link XmlQuest}.<br>
	 * This constructor initializes the quest using the provided data.<br>
	 * It sets up the internal state for the quest handler.
	 * @param xmlQuestData The {@code XmlQuestData} object containing the quest configuration.
	 */
	public XmlQuest(XmlQuestData xmlQuestData)
	{
		super(xmlQuestData.getId());
		this.xmlQuestData = xmlQuestData;
	}
	
	/**
	 * Registers the required quest events.<br>
	 * This method tells the system which actions to listen for.<br>
	 * You should add your specific event listeners inside this method.
	 */
	@Override
	public void register()
	{
		if (xmlQuestData.getStartNpcId() != null)
		{
			qe.registerQuestNpc(xmlQuestData.getStartNpcId()).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(xmlQuestData.getStartNpcId()).addOnTalkEvent(getQuestId());
		}
		
		if (xmlQuestData.getEndNpcId() != null)
		{
			qe.registerQuestNpc(xmlQuestData.getEndNpcId()).addOnTalkEvent(getQuestId());
		}
		
		for (OnTalkEvent talkEvent : xmlQuestData.getOnTalkEvent())
		{
			for (int npcId : talkEvent.getIds())
			{
				qe.registerQuestNpc(npcId).addOnTalkEvent(getQuestId());
			}
		}
		
		for (OnKillEvent killEvent : xmlQuestData.getOnKillEvent())
		{
			for (Monster monster : killEvent.getMonsters())
			{
				final Iterator<Integer> iterator = monster.getNpcIds().iterator();
				while (iterator.hasNext())
				{
					final int monsterId = iterator.next();
					qe.registerQuestNpc(monsterId).addOnKillEvent(getQuestId());
				}
			}
		}
	}
	
	/**
	 * Handles dialog events for the quest.<br>
	 * This method checks the current {@link QuestState} and {@code targetId}.<br>
	 * It determines which dialog to send based on the {@link DialogAction}.
	 * @param env The environment containing player data and current quest context.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		env.setQuestId(getQuestId());
		for (OnTalkEvent talkEvent : xmlQuestData.getOnTalkEvent())
		{
			if (talkEvent.operate(env))
			{
				return true;
			}
		}
		
		final Player player = env.getPlayer();
		final int targetId = env.getTargetId();
		final QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
		{
			if (targetId == xmlQuestData.getStartNpcId())
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT)
				{
					return sendQuestDialog(env, 1011);
				}
				
				return sendQuestStartDialog(env);
			}
		}
		else if ((qs.getStatus() == QuestStatus.REWARD) && (targetId == xmlQuestData.getEndNpcId()))
		{
			return sendQuestEndDialog(env);
		}
		
		return false;
	}
	
	/**
	 * This method is triggered when a player kills a target.<br>
	 * It checks if the kill meets the requirements for the quest.<br>
	 * Use this to progress the quest state.
	 * @param env The {@link QuestEnv} object containing current quest data.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		env.setQuestId(getQuestId());
		for (OnKillEvent killEvent : xmlQuestData.getOnKillEvent())
		{
			if (killEvent.operate(env))
			{
				return true;
			}
		}
		
		return false;
	}
}
