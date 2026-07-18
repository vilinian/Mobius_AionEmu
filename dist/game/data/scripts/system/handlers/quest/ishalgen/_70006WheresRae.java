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
package system.handlers.quest.ishalgen;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the quest logic for {@code 70006 Wheres Rae}.<br>
 * This class manages player interactions and progression steps within this specific quest.<br>
 * It extends {@link QuestHandler} to process game events related to the quest.
 * @author Falke_34
 */
public class _70006WheresRae extends QuestHandler
{
	private final static int questId = 70006;
	
	/**
	 * Initializes the quest handler for quest {@code 70006}.<br>
	 * This constructor calls the superclass constructor to register the quest ID.
	 */
	public _70006WheresRae()
	{
		super(questId);
	}
	
	/**
	 * Registers the required quest events.<br>
	 * This method tells the system which actions to listen for.<br>
	 * You should add your specific event listeners inside this method.
	 */
	@Override
	public void register()
	{
		qe.registerQuestNpc(203534).addOnTalkEvent(questId); // Dabi
		qe.registerQuestNpc(790002).addOnTalkEvent(questId); // Verdandi
		qe.registerQuestNpc(806813).addOnTalkEvent(questId); // Rae
		qe.registerQuestNpc(651860).addOnKillEvent(questId);
		qe.registerQuestItem(182216399, questId);
		qe.addHandlerSideQuestDrop(questId, 651774, 182216396, 1, 100);
		qe.addHandlerSideQuestDrop(questId, 651775, 182216396, 1, 100);
		qe.addHandlerSideQuestDrop(questId, 651777, 182216397, 1, 100);
		qe.addHandlerSideQuestDrop(questId, 651778, 182216397, 1, 100);
		qe.addHandlerSideQuestDrop(questId, 651779, 182216398, 1, 100);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnMovieEndQuest(52, questId);
	}
	
	/**
	 * This method handles the logic when a player levels up.<br>
	 * It checks if the level up triggers specific quest progress.<br>
	 * It calls {@code defaultOnLvlUpEvent} to process the event.
	 * @param env The current quest environment context.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		return defaultOnLvlUpEvent(env);
	}
	
	/**
	 * Checks if a player should start the quest when entering a specific world.<br>
	 * This method triggers for players who do not have the quest active.<br>
	 * It uses {@code startQuest} to begin the quest.
	 * @param env The current quest environment containing player data.
	 * @return {@code true} if the quest was successfully started, otherwise {@code false}.
	 */
	@Override
	public boolean onEnterWorldEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
		{
			env.setQuestId(questId);
			QuestService.startQuest(env);
		}
		
		return false;
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
		if (qs == null)
		{
			return false;
		}
		
		final int targetId = env.getTargetId();
		final DialogAction action = env.getDialog();
		final int var = qs.getQuestVars().getQuestVars();
		
		if (qs.getStatus() == QuestStatus.START)
		{
			if (targetId == 203534)
			{
				switch (action)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1012:
						return sendQuestDialog(env, 1012);
					case SETPRO1:
						qs.setQuestVar(1);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					default:
						break;
				}
			}
			else if (targetId == 790002)
			{
				switch (action)
				{
					case QUEST_SELECT:
						if (var == 2)
						{
							return sendQuestDialog(env, 1693);
						}
						else if (var == 3)
						{
							return sendQuestDialog(env, 2034);
						}
						else if (var == 5)
						{
							return sendQuestDialog(env, 2716);
						}
						
						return sendQuestDialog(env, 1352);
					case SELECT_ACTION_1353:
						return sendQuestDialog(env, 1353);
					case SELECT_ACTION_1354:
						return sendQuestDialog(env, 1354);
					case SELECT_ACTION_2034:
						return sendQuestDialog(env, 2035);
					case SELECT_ACTION_2716:
						playQuestMovie(env, 1, 3);
						return sendQuestDialog(env, 2717);
					case SETPRO4:
						qs.setQuestVar(4);
						updateQuestStatus(env);
						ItemService.addItem(player, 182216399, 1);
						return closeDialogWindow(env);
					case SETPRO2:
						qs.setQuestVar(2);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					case CHECK_USER_HAS_QUEST_ITEM:
						return checkQuestItems(env, 2, 3, false, 10000, 10001);
					case SET_SUCCEED:
						qs.setQuestVar(7);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						TeleportService2.teleportTo(player, 220010000, 951.5539f, 1696.2922f, 259.75f, (byte) 40, TeleportAnimation.BEAM_ANIMATION);
						return closeDialogWindow(env);
					default:
						break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 806813)
			{
				switch (action)
				{
					case USE_OBJECT:
						return sendQuestDialog(env, 10002);
					case SELECT_QUEST_REWARD:
						return sendQuestDialog(env, 5);
					case SELECTED_QUEST_NOREWARD:
						return sendQuestEndDialog(env);
					default:
						break;
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
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (item.getItemId() != 182216399)
		{
			return HandlerResult.FAILED;
		}
		
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 3000, 0, 0), true);
			ThreadPoolManager.getInstance().schedule(() ->
			{
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 1, 0), true);
				removeQuestItem(env, 182216399, 1);
				qs.setQuestVar(5);
				updateQuestStatus(env);
			}, 3000);
		}
		
		return HandlerResult.SUCCESS;
	}
	
	/**
	 * Handles the logic that occurs when a movie finishes playing.<br>
	 * It checks if the player should receive a reward based on the {@code movieId}.<br>
	 * This method is triggered by the quest engine to progress the story.
	 * @param env The current quest environment containing player data.
	 * @param movieId The unique identifier of the movie that just ended.
	 * @return {@code true} if the quest was successfully completed, otherwise {@code false}.
	 */
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId)
	{
		if (movieId != 3)
		{
			return false;
		}
		
		final Player player = env.getPlayer();
		if (player.getCommonData().getRace() != Race.ASMODIANS)
		{
			return false;
		}
		
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() != QuestStatus.START))
		{
			return false;
		}
		
		qs.setQuestVar(6);
		updateQuestStatus(env);
		return true;
	}
}
