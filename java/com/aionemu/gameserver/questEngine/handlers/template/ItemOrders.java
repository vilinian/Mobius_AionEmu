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

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class handles quest logic related to item orders.<br>
 * It manages the requirements and actions associated with specific {@link Item} objects within a quest.<br>
 * It extends {@link QuestHandler} to process these interactions.
 * @author Altaress, Bobobear
 */
public class ItemOrders extends QuestHandler
{
	private final int questId;
	private final int startItemId;
	private final int talkNpc1;
	private final int talkNpc2;
	private final int endNpcId;
	
	/**
	 * Initializes a new {@link ItemOrders} quest handler.<br>
	 * This constructor sets up the required IDs for the quest flow.
	 * @param questId The unique identifier for the quest.
	 * @param startItemId The ID of the item that triggers the quest.
	 * @param talkNpc1 The ID of the first NPC to speak with.
	 * @param talkNpc2 The ID of the second NPC to speak with.
	 * @param endNpcId The ID of the final NPC to complete the quest.
	 */
	public ItemOrders(int questId, int startItemId, int talkNpc1, int talkNpc2, int endNpcId)
	{
		super(questId);
		this.startItemId = startItemId;
		this.questId = questId;
		this.talkNpc1 = talkNpc1;
		this.talkNpc2 = talkNpc2;
		this.endNpcId = endNpcId;
	}
	
	/**
	 * Registers the required quest events.<br>
	 * This method tells the system which actions to listen for.<br>
	 * You should add your specific event listeners inside this method.
	 */
	@Override
	public void register()
	{
		qe.registerQuestNpc(endNpcId).addOnTalkEvent(questId);
		qe.registerQuestItem(startItemId, questId);
		if (talkNpc1 != 0)
		{
			qe.registerQuestNpc(talkNpc1).addOnTalkEvent(questId);
		}
		
		if (talkNpc2 != 0)
		{
			qe.registerQuestNpc(talkNpc2).addOnTalkEvent(questId);
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
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
		{
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (targetId == 0)
		{
			if (env.getDialogId() == 1002)
			{
				QuestService.startQuest(env);
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
				return true;
			}
		}
		else if (((targetId == talkNpc1) && (talkNpc1 != 0)) || ((targetId == talkNpc2) && (talkNpc2 != 0)))
		{
			if (qs != null)
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT)
				{
					return sendQuestDialog(env, 1352);
				}
				else if (env.getDialog() == DialogAction.SETPRO1)
				{
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
				{
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (targetId == endNpcId)
		{
			if (qs != null)
			{
				if ((env.getDialog() == DialogAction.QUEST_SELECT) && (qs.getStatus() == QuestStatus.START))
				{
					return sendQuestDialog(env, 2375);
				}
				else if ((env.getDialogId() == 1009) && (qs.getStatus() != QuestStatus.COMPLETE) && (qs.getStatus() != QuestStatus.NONE))
				{
					removeQuestItem(env, startItemId, 1);
					qs.setQuestVar(1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestEndDialog(env);
				}
				else
				{
					return sendQuestEndDialog(env);
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Handles the logic when a player uses an {@code Item}.<br>
	 * This method checks if the quest is in the {@code START} status.<br>
	 * It also verifies if the player is in the correct zone before processing.
	 * @param env The current quest environment context.
	 * @param item The specific {@code Item} that was used by the player.
	 * @return A {@code HandlerResult} indicating if the action succeeded or failed.
	 */
	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item)
	{
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();
		
		if (id != startItemId)
		{
			return HandlerResult.UNKNOWN;
		}
		
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, itemObjId, id, 3000, 0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, itemObjId, id, 0, 1), true);
				sendQuestDialog(env, 4);
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}
}
