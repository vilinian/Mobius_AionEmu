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
package system.handlers.quest;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * This class serves as a template for creating new quest handlers.<br>
 * It demonstrates the required structure and how to use default methods from {@link QuestHandler}.<br>
 * Developers should replace all placeholder IDs with values specific to their own quests.
 * @author vlog
 */
public class _9999QuestHandlerTemplate extends QuestHandler
{
	private static final int questId = 9999;
	
	/**
	 * This is the default constructor for the {@link _9999QuestHandlerTemplate} class.<br>
	 * It initializes the handler with a specific quest ID.<br>
	 * Use this as a starting point when creating new quest handlers.
	 */
	public _9999QuestHandlerTemplate()
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
		// register needed events here
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
		final DialogAction dialog = env.getDialog();
		final int targetId = env.getTargetId();
		
		// If this is a mission, the qs should be != null and you will not need this
		if ((qs == null) || qs.canRepeat())
		{
			if (targetId == 000000)
			{
				// Viktor Logwin
				if (dialog == DialogAction.QUEST_SELECT)
				{
					return sendQuestDialog(env, 1011); // can be different
				}
				
				return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			final int var = qs.getQuestVarById(0);
			switch (targetId)
			{
				case 111111:
				{
					// Oliver
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							if (var == 0)
							{
								return sendQuestDialog(env, 1352);
							}
							else if (var == 4)
							{
								return sendQuestDialog(env, 1693);
							}
							else if (var == 5)
							{
								return sendQuestDialog(env, 2034);
							}
						}
						case SELECT_ACTION_1353:
						{
							playQuestMovie(env, 0);
							return sendQuestDialog(env, 1353);
						}
						case SETPRO1:
						{
							return defaultCloseDialog(env, 0, 1); // 1
						}
						case SETPRO2:
						{
							return defaultCloseDialog(env, 4, 5); // 5
						}
						case CHECK_USER_HAS_QUEST_ITEM:
						{
							return checkQuestItems(env, 5, 6, true, 2375, 10001);
						}
						case FINISH_DIALOG:
						{
							return sendQuestSelectionDialog(env);
						}
						default:
							break;
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 000000)
			{
				// Viktor Logwin
				if (dialog == DialogAction.USE_OBJECT)
				{
					return sendQuestDialog(env, 20001);
				}
				
				return sendQuestEndDialog(env);
			}
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
		return defaultOnKillEvent(env, 010101, 1, 3); // 1 - 3
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
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			if (player.isInsideZone(ZoneName.get("DF1A_ITEMUSEAREA_Q2016")))
			{
				// example zone
				return HandlerResult.fromBoolean(useQuestItem(env, item, 3, 4, false));
			}
		}
		
		return HandlerResult.FAILED;
	}
}
