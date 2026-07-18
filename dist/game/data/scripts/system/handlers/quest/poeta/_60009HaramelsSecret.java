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
package system.handlers.quest.poeta;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Handles the logic for quest {@code 60009} titled "Haramel's Secret".<br>
 * This class manages specific quest interactions and state transitions.<br>
 * It extends {@link QuestHandler} to process player actions within this quest.
 * @author FrozenKiller
 */
public class _60009HaramelsSecret extends QuestHandler
{
	private final static int questId = 60009;
	private final static int[] mobs =
	{
		653196,
		653205,
		653218
	};
	
	/**
	 * Initializes the quest handler for Haramels Secret.<br>
	 * This constructor sets the {@code questId} to 60009.<br>
	 * It calls the superclass constructor to register the quest.
	 */
	public _60009HaramelsSecret()
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
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(820012).addOnTalkEvent(questId); // Third Odium Transport Track
		qe.registerQuestNpc(820133).addOnTalkEvent(questId); // Royer
		qe.registerQuestNpc(799524).addOnTalkEvent(questId); // Gestanerk
		qe.registerQuestNpc(820006).addOnTalkEvent(questId); // Kasis
		qe.registerQuestNpc(700834).addOnTalkEvent(questId); // Odella
		qe.registerOnEnterWorld(questId);
		qe.registerOnEnterZone(ZoneName.get("HARAMEL_TOWER_300200000"), questId);
		for (int mob : mobs)
		{
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
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
		return defaultOnLvlUpEvent(env, 60000, false);
	}
	
	/**
	 * Updates the quest progress when a player enters a specific world.<br>
	 * This method checks if the player has the quest in {@code START} status.<br>
	 * It verifies specific quest variables and the current world ID before updating.
	 * @param env The current quest environment containing player data.
	 * @return {@code true} if the quest progress was updated, otherwise {@code false}.
	 */
	@Override
	public boolean onEnterWorldEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
		{
			return false;
		}
		
		if (qs.getStatus() == QuestStatus.START)
		{
			if ((qs.getQuestVarById(0) == 1) && (player.getWorldId() == 300200000))
			{
				qs.setQuestVar(2);
				updateQuestStatus(env);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * This method handles the logic when a player enters a specific zone.<br>
	 * It checks if the player is currently on the correct quest step.<br>
	 * If the conditions are met, it updates the quest status to {@code REWARD}.
	 * @param env The current quest environment containing player data.
	 * @param zoneName The name of the zone the player just entered.
	 * @return {@code true} if the quest progress was updated, otherwise {@code false}.
	 */
	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName)
	{
		final Player player = env.getPlayer();
		if (player == null)
		{
			return false;
		}
		
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			final int var = qs.getQuestVarById(0);
			if ((var == 4) && (zoneName == ZoneName.get("HARAMEL_TOWER_300200000")))
			{
				changeQuestStep(env, 4, 5, false); // 5
				return true;
			}
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
		final DialogAction dialog = env.getDialog();
		final int targetId = env.getTargetId();
		
		if (qs == null)
		{
			return false;
		}
		
		if (qs.getStatus() == QuestStatus.START)
		{
			switch (targetId)
			{
				case 820012:
				{
					switch (dialog)
					{
						case USE_OBJECT:
						{
							qs.setQuestVar(1);
							updateQuestStatus(env);
							return false;
						}
						default:
							break;
					}
					break;
				}
				case 820133:
				{
					switch (dialog)
					{
						case USE_OBJECT:
						{
							return sendQuestDialog(env, 1693);
						}
						case SETPRO3:
						{
							qs.setQuestVar(3);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						default:
							break;
					}
				}
				case 700834:
				{
					switch (dialog)
					{
						case USE_OBJECT:
						{
							ItemService.addItem(player, 182216580, 1);
							return true;
						}
						default:
							break;
					}
				}
				case 799524:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							if (qs.getQuestVarById(0) == 6)
							{
								return sendQuestDialog(env, 3057);
							}
							
							return sendQuestDialog(env, 3398);
						}
						case CHECK_USER_HAS_QUEST_ITEM:
						{
							if (QuestService.collectItemCheck(env, true))
							{
								qs.setQuestVar(7);
								updateQuestStatus(env);
								return sendQuestDialog(env, 10000);
							}
							
							return sendQuestDialog(env, 10001);
						}
						case SETPRO8:
						{
							qs.setQuestVar(8);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
						default:
							break;
					}
					break;
				}
				default:
					break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 820006)
			{
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
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			final int var = qs.getQuestVarById(0);
			
			// Step 3 has count 1 and mob 653196, step 5 has count 1 and mob 653205, and step 8 has count 1 and mob 653218.
			
			switch (var)
			{
				case 3:
				{
					return defaultOnKillEvent(env, 653196, 3, 4, 0);
				}
				case 5:
				{
					return defaultOnKillEvent(env, 653205, 5, 6, 0);
				}
				case 8:
				{
					qs.setQuestVar(9);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
				default:
					break;
			}
			
			return false;
		}
		
		return false;
	}
}
