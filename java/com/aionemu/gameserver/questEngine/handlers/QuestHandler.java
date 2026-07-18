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
package com.aionemu.gameserver.questEngine.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.DialogPage;
import com.aionemu.gameserver.model.EmotionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.challenge.ChallengeQuestTemplate;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.quest.QuestDrop;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.quest.QuestWorkItems;
import com.aionemu.gameserver.model.templates.quest.XMLStartCondition;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.task.QuestTasks;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * This class serves as a base handler for processing various quest-related actions and events.<br>
 * It provides the core logic for managing how {@link QuestTemplate} objectives are executed within the game world.
 * @author MrPoke
 * @modified vlog
 */
public abstract class QuestHandler extends AbstractQuestHandler implements ConstantSpawnHandler
{
	protected final int questId;
	protected QuestEngine qe;
	protected List<QuestItems> workItems;
	protected HashSet<Integer> actionItems;
	protected HashSet<Integer> constantSpawns;
	
	/**
	 * Initializes a new {@link QuestHandler} for a specific quest.<br>
	 * This constructor sets the {@code questId}.<br>
	 * It also loads all required work items and action items from the engine.
	 * @param questId The unique identifier for the quest to be handled.
	 */
	protected QuestHandler(int questId)
	{
		this.questId = questId;
		qe = QuestEngine.getInstance();
		loadWorkItems();
		loadActionItems();
		onWorkItemsLoaded();
	}
	
	/**
	 * Loads the work items for a specific quest.<br>
	 * This method retrieves data from {@link DataManager}.<br>
	 * It populates the {@code workItems} field if the template exists.
	 */
	private void loadWorkItems()
	{
		final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		if (template == null)
		{
			return; // Some artificial quests have dummy questIds
		}
		
		final QuestWorkItems qwi = DataManager.QUEST_DATA.getQuestById(questId).getQuestWorkItems();
		if (qwi == null)
		{
			return;
		}
		
		workItems = qwi.getQuestWorkItem();
	}
	
	/**
	 * Loads the action items for a specific quest.<br>
	 * This method retrieves {@link QuestDrop} data from {@link DataManager}.<br>
	 * It filters drops based on a specific NPC ID prefix and adds them to the {@code actionItems} set.
	 */
	private void loadActionItems()
	{
		final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		if (template == null)
		{
			return; // Some artificial quests have dummy questIds
		}
		
		final List<QuestDrop> qDrop = DataManager.QUEST_DATA.getQuestById(questId).getQuestDrop();
		if (qDrop == null)
		{
			return;
		}
		
		for (QuestDrop drop : qDrop)
		{
			if ((drop.getNpcId() / 100000) != 7)
			{
				continue;
			}
			
			if (actionItems == null)
			{
				actionItems = new HashSet<>();
			}
			
			actionItems.add(drop.getNpcId());
		}
	}
	
	/**
	 * This method is called after the work items have been loaded.<br>
	 * It handles any necessary initialization for quest work items.
	 */
	protected void onWorkItemsLoaded()
	{
	}
	
	/**
	 * Updates the current quest status for a player.<br>
	 * This method sends an update packet to the client.<br>
	 * It ensures thread safety during the update process.
	 * @param env The {@code QuestEnv} object containing the current quest context.
	 */
	public synchronized void updateQuestStatus(QuestEnv env)
	{
		sendUpdatePacket(env);
	}
	
	/**
	 * Updates the current progress of a quest for a specific environment.<br>
	 * This method moves the player from one {@code int} step to another.<br>
	 * It also handles whether a reward should be granted during this transition.
	 * @param env The {@link QuestEnv} object containing the current quest context.
	 * @param step The current quest step identifier.
	 * @param nextStep The new quest step to move to.
	 * @param reward A {@code boolean} indicating if a reward is given.
	 */
	public void changeQuestStep(QuestEnv env, int step, int nextStep, boolean reward)
	{
		changeQuestStep(env, step, nextStep, reward, 0);
	}
	
	/**
	 * Updates the quest progress for a player based on specific variables.<br>
	 * This method checks if the current variable matches the expected step.<br>
	 * It then updates the variable to the next step and handles rewards if applicable.
	 * @param env The environment containing the player's quest data.
	 * @param step The required value for the quest variable.
	 * @param nextStep The new value to set for the quest variable.
	 * @param reward Whether a reward should be granted upon completion.
	 * @param varNum The specific quest variable ID to modify.
	 */
	public void changeQuestStep(QuestEnv env, int step, int nextStep, boolean reward, int varNum)
	{
		final QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getQuestVarById(varNum) == step))
		{
			if (reward)
			{
				// ignore nextStep
				if (nextStep != step)
				{
					qs.setQuestVarById(varNum, nextStep);
				}
				
				qs.setStatus(QuestStatus.REWARD);
			}
			else
			{
				// quest can be rolled back if nextStep < step
				if (nextStep != step)
				{
					qs.setQuestVarById(varNum, nextStep);
				}
			}
			
			updateQuestStatus(env);
		}
	}
	
	/**
	 * Sends a specific quest dialog to the player.<br>
	 * This method validates if the dialog is an exploit and checks the current quest status.<br>
	 * It uses {@link DialogPage} to identify reward windows.
	 * @param env The environment containing the player information.
	 * @param dialogId The unique identifier for the dialog to be sent.
	 * @return {@code true} if the dialog was sent successfully, or {@code false} if it was blocked.
	 */
	public boolean sendQuestDialog(QuestEnv env, int dialogId)
	{
		boolean isExploitDialog = false;
		if (DialogPage.getPageByAction(dialogId) != null)
		{
			switch (DialogPage.getPageByAction(dialogId))
			{
				case SELECT_QUEST_REWARD_WINDOW1:
				case SELECT_QUEST_REWARD_WINDOW2:
				case SELECT_QUEST_REWARD_WINDOW3:
				case SELECT_QUEST_REWARD_WINDOW4:
				case SELECT_QUEST_REWARD_WINDOW5:
				case SELECT_QUEST_REWARD_WINDOW6:
				case SELECT_QUEST_REWARD_WINDOW7:
				case SELECT_QUEST_REWARD_WINDOW8:
				case SELECT_QUEST_REWARD_WINDOW9:
				case SELECT_QUEST_REWARD_WINDOW10:
					isExploitDialog = true;
					break;
				default:
					break;
			}
		}
		
		if (isExploitDialog)
		{
			// reward packet exploitation fix
			final Player player = env.getPlayer();
			final QuestState qs = player.getQuestStateList().getQuestState(questId);
			if ((qs == null) || (qs.getStatus() != QuestStatus.REWARD))
			{
				return false;
			}
		}
		
		sendDialogPacket(env, dialogId);
		return true;
	}
	
	/**
	 * Sends a quest selection dialog to the player.<br>
	 * This method triggers the packet for selecting available quests.
	 * @param env The environment context containing player and quest data.
	 * @return {@code true} if the dialog was sent successfully, otherwise {@code false}.
	 */
	public boolean sendQuestSelectionDialog(QuestEnv env)
	{
		sendQuestSelectionPacket(env, 10);
		return true;
	}
	
	/**
	 * Closes the current quest dialog window for a player.<br>
	 * This method sends a packet to clear the selection UI.
	 * @param env The environment context containing player and quest data.
	 * @return {@code true} if the operation was successful, otherwise {@code false}.
	 */
	public boolean closeDialogWindow(QuestEnv env)
	{
		sendQuestSelectionPacket(env, 0);
		return true;
	}
	
	/**
	 * Sends the initial dialog for starting a quest.<br>
	 * This method calls {@code int, int, int)} using default values.
	 * @param env The environment containing the current quest data.
	 * @return {@code true} if the dialog was sent successfully, or {@code false} otherwise.
	 */
	public boolean sendQuestStartDialog(QuestEnv env)
	{
		return sendQuestStartDialog(env, 0, 0, 0);
	}
	
	/**
	 * Sends the starting dialog for a quest based on a specific step.<br>
	 * This method acts as a shortcut for sending a start dialog with zero items.
	 * @param env The {@code QuestEnv} object containing the current quest environment.
	 * @param step The specific quest step to trigger the dialog for.
	 * @return {@code true} if the dialog was sent successfully, otherwise {@code false}.
	 */
	public boolean sendQuestStartDialog(QuestEnv env, int step)
	{
		return sendQuestStartDialog(env, 0, 0, step);
	}
	
	/**
	 * Sends a quest start dialog to the player based on specific items.<br>
	 * This method checks if the player has the required {@code itemId} and {@code itemCount}.<br>
	 * It then triggers the starting dialogue for the quest.
	 * @param env The current environment context for the quest.
	 * @param itemId The unique identifier of the item required.
	 * @param itemCount The amount of the item needed to start the quest.
	 * @return {@code true} if the dialog was sent successfully, otherwise {@code false}.
	 */
	public boolean sendQuestStartDialog(QuestEnv env, int itemId, int itemCount)
	{
		return sendQuestStartDialog(env, itemId, itemCount, 0);
	}
	
	/**
	 * Sends the starting dialog for a quest based on specific conditions.<br>
	 * This method handles item rewards and updates the quest status via {@link QuestService}.<br>
	 * It checks if the player inventory is full before granting items.
	 * @param env The current quest environment context.
	 * @param itemId The unique identifier of the item to give as a reward.
	 * @param itemCount The quantity of the item to grant.
	 * @param step The specific quest step to activate.
	 * @return {@code true} if the dialog was successfully sent or handled, otherwise {@code false}.
	 */
	public boolean sendQuestStartDialog(QuestEnv env, int itemId, int itemCount, int step)
	{
		switch (env.getDialog())
		{
			case ASK_QUEST_ACCEPT:
			{
				return sendQuestDialog(env, 4);
			}
			case QUEST_ACCEPT_1:
			{
				if ((itemId != 0) && (itemCount != 0))
				{
					if (!env.getPlayer().getInventory().isFullSpecialCube())
					{
						if (QuestService.startQuest(env, step))
						{
							giveQuestItem(env, itemId, itemCount);
							return sendQuestDialog(env, 1003);
						}
					}
				}
				else
				{
					if (QuestService.startQuest(env, step))
					{
						if ((env.getVisibleObject() == null) || (env.getVisibleObject() instanceof Player))
						{
							return closeDialogWindow(env);
						}
						
						return sendQuestDialog(env, 1003);
					}
				}
			}
			case QUEST_ACCEPT_SIMPLE:
			{
				if ((itemId != 0) && (itemCount != 0))
				{
					if (!env.getPlayer().getInventory().isFullSpecialCube())
					{
						if (QuestService.startQuest(env, step))
						{
							giveQuestItem(env, itemId, itemCount);
							return closeDialogWindow(env);
						}
					}
				}
				else
				{
					if (QuestService.startQuest(env, step))
					{
						if ((env.getVisibleObject() == null) || (env.getVisibleObject() instanceof Player))
						{
							return closeDialogWindow(env);
						}
						
						return closeDialogWindow(env);
					}
				}
			}
			case QUEST_REFUSE_1:
			case QUEST_REFUSE_2:
			{
				return sendQuestDialog(env, 1004);
			}
			case QUEST_REFUSE_SIMPLE:
			{
				return closeDialogWindow(env);
			}
			case FINISH_DIALOG:
			{
				return sendQuestSelectionDialog(env);
			}
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Sends the final dialog for completing a quest.<br>
	 * This method removes specific items from the player's inventory before showing the dialog.<br>
	 * It calls {@code sendQuestEndDialog} to finish the process.
	 * @param env The environment containing the current player and quest data.
	 * @param questItemsToRemove An array of item IDs to be removed from the player.
	 * @return {@code true} if the dialog was sent successfully, or {@code false} otherwise.
	 */
	public boolean sendQuestEndDialog(QuestEnv env, int[] questItemsToRemove)
	{
		final Player player = env.getPlayer();
		for (int item : questItemsToRemove)
		{
			final long count = player.getInventory().getItemCountByItemId(item);
			if (count > 0)
			{
				player.getInventory().decreaseByItemId(item, count);
			}
		}
		
		return sendQuestEndDialog(env);
	}
	
	/**
	 * Sends the final dialog for a completed quest.<br>
	 * This method calls {@code int[])} with no items to remove.
	 * @param env The environment containing the current quest data.
	 * @return {@code true} if the dialog was sent successfully, otherwise {@code false}.
	 */
	public boolean sendQuestEndDialog(QuestEnv env)
	{
		return sendQuestEndDialog(env, 0);
	}
	
	/**
	 * Sends the final dialog for completing a quest.<br>
	 * This method handles rewards and closes the dialog window if necessary.<br>
	 * It prevents exploitation by checking the current {@code QuestStatus}.
	 * @param env The environment containing player and quest data.
	 * @param reward The reward value to be processed.
	 * @return {@code true} if the dialog was sent successfully, otherwise {@code false}.
	 */
	public boolean sendQuestEndDialog(QuestEnv env, int reward)
	{
		final Player player = env.getPlayer();
		final int dialogId = env.getDialogId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((dialogId >= DialogAction.SELECTED_QUEST_REWARD1.id()) && (dialogId <= DialogAction.SELECTED_QUEST_NOREWARD.id()))
		{
			if ((qs == null) || (qs.getStatus() != QuestStatus.REWARD))
			{
				return false; // reward packet exploitation fix
			}
			
			if (QuestService.finishQuest(env, reward))
			{
				final Npc npc = (Npc) env.getVisibleObject();
				if ("useitem".equals(npc.getAi2().getName()) || ("quest_use_item".equals(npc.getAi2().getName())))
				{
					return closeDialogWindow(env);
				}
				
				return closeDialogWindow(env);
			}
			
			return false;
		}
		else if ((dialogId == DialogAction.SELECT_QUEST_REWARD.id()) || (dialogId == DialogAction.USE_OBJECT.id()))
		{
			if ((qs != null) && (qs.getStatus() == QuestStatus.REWARD))
			{
				return sendQuestDialog(env, 5 + reward);
			}
		}
		
		return false;
	}
	
	/**
	 * Closes the current quest dialog using default parameters.<br>
	 * This method calls {@code int, int, boolean, boolean, int, int, int, int, int)} with default values.
	 * @param env The environment context for the quest.
	 * @param step The current quest step.
	 * @param nextStep The next quest step to transition to.
	 * @return {@code true} if the dialog was closed successfully, {@code false} otherwise.
	 */
	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep)
	{
		return defaultCloseDialog(env, step, nextStep, false, false, 0, 0, 0, 0, 0);
	}
	
	/**
	 * Closes the current quest dialog using default values.<br>
	 * This method simplifies calls by providing standard parameters for internal logic.
	 * @param env The {@code QuestEnv} object containing the current quest environment.
	 * @param step The current quest step number.
	 * @param nextStep The next quest step to transition to.
	 * @param varNum The specific variable index used for tracking progress.
	 * @return {@code true} if the dialog was closed successfully, {@code false} otherwise.
	 */
	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, int varNum)
	{
		return defaultCloseDialog(env, step, nextStep, false, false, 0, 0, 0, 0, 0, varNum);
	}
	
	/**
	 * Closes the current quest dialog window using default parameters.<br>
	 * This method simplifies the process of ending a dialog sequence.
	 * @param env The current quest environment context.
	 * @param step The current step of the quest.
	 * @param nextStep The next step to transition to.
	 * @param reward Whether a reward should be granted.
	 * @param sameNpc Whether the interaction is with the same NPC.
	 * @return {@code true} if the dialog was closed successfully, {@code false} otherwise.
	 */
	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, boolean reward, boolean sameNpc)
	{
		return defaultCloseDialog(env, step, nextStep, reward, sameNpc, 0, 0, 0, 0, 0);
	}
	
	/**
	 * Closes the current quest dialog and proceeds to the next step.<br>
	 * This method handles the transition logic for quests that do not have a specific end dialog.
	 * @param env The current quest environment context.
	 * @param step The current quest step number.
	 * @param nextStep The next quest step to move to.
	 * @param reward Whether a reward should be granted.
	 * @param sameNpc Indicates if the interaction is with the same NPC.
	 * @param rewardId The unique identifier for the reward item.
	 * @return {@code true} if the dialog was closed successfully, {@code false} otherwise.
	 */
	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, boolean reward, boolean sameNpc, int rewardId)
	{
		return defaultCloseDialog(env, step, nextStep, reward, sameNpc, rewardId, 0, 0, 0, 0);
	}
	
	/**
	 * Closes the current quest dialog and updates the player's progress.<br>
	 * This method handles item rewards and removals during a step transition.
	 * @param env The current quest environment context.
	 * @param step The current quest step being completed.
	 * @param nextStep The next quest step to move to.
	 * @param giveItemId The ID of the item to grant as a reward.
	 * @param giveItemCount The amount of the reward item to grant.
	 * @param removeItemId The ID of the item to take from the player.
	 * @param removeItemCount The amount of the required item to remove.
	 * @return {@code true} if the dialog was closed successfully, {@code false} otherwise.
	 */
	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, int giveItemId, int giveItemCount, int removeItemId, int removeItemCount)
	{
		return defaultCloseDialog(env, step, nextStep, false, false, 0, giveItemId, giveItemCount, removeItemId, removeItemCount);
	}
	
	/**
	 * Closes a quest dialog and updates the player's progress.<br>
	 * This method handles rewards, item distribution, and step transitions.
	 * @param env The current quest environment context.
	 * @param step The current quest step index.
	 * @param nextStep The next quest step to move to.
	 * @param reward Whether the player should receive a reward.
	 * @param sameNpc Whether the interaction occurs with the same NPC.
	 * @param giveItemId The ID of the item to give to the player.
	 * @param giveItemCount The amount of the given item.
	 * @param removeItemId The ID of the item to remove from the player.
	 * @param removeItemCount The amount of the removed item.
	 * @return {@code true} if the dialog was closed successfully, {@code false} otherwise.
	 */
	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, boolean reward, boolean sameNpc, int giveItemId, int giveItemCount, int removeItemId, int removeItemCount)
	{
		return defaultCloseDialog(env, step, nextStep, reward, sameNpc, 0, giveItemId, giveItemCount, removeItemId, removeItemCount);
	}
	
	/**
	 * Closes the current quest dialog and updates the player's progress.<br>
	 * This method handles rewards and item changes automatically.
	 * @param env The current quest environment context.
	 * @param step The current quest step number.
	 * @param nextStep The next quest step to move to.
	 * @param reward Whether the player should receive a reward.
	 * @param sameNpc Whether the interaction stays with the same NPC.
	 * @param rewardId The ID of the reward item.
	 * @param giveItemId The ID of the item to give to the player.
	 * @param giveItemCount The amount of the item to give.
	 * @param removeItemId The ID of the item to remove from the player.
	 * @param removeItemCount The amount of the item to remove.
	 * @return {@code true} if the dialog was closed successfully, {@code false} otherwise.
	 */
	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, boolean reward, boolean sameNpc, int rewardId, int giveItemId, int giveItemCount, int removeItemId, int removeItemCount)
	{
		return defaultCloseDialog(env, step, nextStep, reward, sameNpc, rewardId, giveItemId, giveItemCount, removeItemId, removeItemCount, 0);
	}
	
	/**
	 * Closes a quest dialog and updates the player's quest progress.<br>
	 * This method handles item rewards, removals, and step transitions.<br>
	 * It determines whether to show a selection dialog or close the window based on the NPC type.
	 * @param env The current quest environment context.
	 * @param step The current quest step being completed.
	 * @param nextStep The quest step to move to after completion.
	 * @param reward Whether a reward should be granted during this transition.
	 * @param sameNpc If {@code true}, the player remains at the same NPC for the next dialog.
	 * @param rewardId The ID of the reward to give if applicable.
	 * @param giveItemId The ID of the item to grant to the player.
	 * @param giveItemCount The quantity of the item to grant.
	 * @param removeItemId The ID of the item to take from the player.
	 * @param removeItemCount The quantity of the item to remove.
	 * @param varNum The value to set for a specific quest variable.
	 * @return {@code true
	 */
	@SuppressWarnings("javadoc")
	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, boolean reward, boolean sameNpc, int rewardId, int giveItemId, int giveItemCount, int removeItemId, int removeItemCount, int varNum)
	{
		final QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
		if (qs.getQuestVarById(0) == step)
		{
			if ((giveItemId != 0) && (giveItemCount != 0))
			{
				if (!giveQuestItem(env, giveItemId, giveItemCount))
				{
					return false;
				}
			}
			
			removeQuestItem(env, removeItemId, removeItemCount, qs.getStatus());
			changeQuestStep(env, step, nextStep, reward, varNum);
			if (sameNpc)
			{
				return sendQuestEndDialog(env, rewardId);
			}
			
			final Npc npc = (Npc) env.getVisibleObject();
			if ("useitem".equals(npc.getAi2().getName()))
			{
				return closeDialogWindow(env);
			}
			
			return sendQuestSelectionDialog(env);
		}
		
		return false;
	}
	
	/**
	 * Verifies if the player meets the required item conditions for a quest step.<br>
	 * This method checks the current inventory against the expected items.<br>
	 * It determines if the player can proceed to the next stage of the quest.
	 * @param env The current quest environment context.
	 * @param step The current quest step index.
	 * @param nextStep The target quest step index.
	 * @param reward A boolean indicating if a reward is granted upon success.
	 * @param checkOkId The ID to trigger if the item check passes.
	 * @param checkFailId The ID to trigger if the item check fails.
	 * @return {@code true} if the requirements are met, {@code false} otherwise.
	 */
	public boolean checkQuestItems(QuestEnv env, int step, int nextStep, boolean reward, int checkOkId, int checkFailId)
	{
		return checkQuestItems(env, step, nextStep, reward, checkOkId, checkFailId, 0, 0);
	}
	
	/**
	 * Checks if the player has collected the required items for a quest step.<br>
	 * If successful, it gives an item and advances the quest progress.<br>
	 * If unsuccessful, it displays a failure dialog.
	 * @param env The current quest environment context.
	 * @param step The current quest step being checked.
	 * @param nextStep The quest step to move to upon success.
	 * @param reward Whether the player should receive a reward for completing this step.
	 * @param checkOkId The dialog ID to send if the item check passes.
	 * @param checkFailId The dialog ID to send if the item check fails.
	 * @param giveItemId The ID of the item to grant to the player, or 0 for none.
	 * @param giveItemCount The amount of the item to grant, or 0 for none.
	 * @return {@code true} if the quest step was successfully updated and dialog sent; {@code false} otherwise.
	 */
	public boolean checkQuestItems(QuestEnv env, int step, int nextStep, boolean reward, int checkOkId, int checkFailId, int giveItemId, int giveItemCount)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getQuestVarById(0) == step)
		{
			if (QuestService.collectItemCheck(env, true))
			{
				if ((giveItemId != 0) && (giveItemCount != 0))
				{
					if (!giveQuestItem(env, giveItemId, giveItemCount))
					{
						return false;
					}
				}
				
				changeQuestStep(env, step, nextStep, reward);
				return sendQuestDialog(env, checkOkId);
			}
			
			return sendQuestDialog(env, checkFailId);
		}
		
		return false;
	}
	
	/**
	 * Checks if the player has collected the required items for a quest step.<br>
	 * If successful, it grants rewards and advances the quest progress.<br>
	 * This method handles item distribution and opens the next dialog window.
	 * @param env The current {@code QuestEnv} context.
	 * @param step The current quest step identifier.
	 * @param nextStep The next quest step to move to.
	 * @param reward Whether a reward should be granted.
	 * @param checkOkId The ID of the dialog to send upon success.
	 * @param giveItemId The ID of the item to grant, or {@code 0} for none.
	 * @param giveItemCount The amount of the item to grant, or {@code 0} for none.
	 * @return {@code true} if the quest step was successfully updated, {@code false} otherwise.
	 */
	public boolean checkQuestItemsSimple(QuestEnv env, int step, int nextStep, boolean reward, int checkOkId, int giveItemId, int giveItemCount)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getQuestVarById(0) == step)
		{
			if (QuestService.collectItemCheck(env, true))
			{
				if ((giveItemId != 0) && (giveItemCount != 0))
				{
					if (!giveQuestItem(env, giveItemId, giveItemCount))
					{
						return false;
					}
				}
				
				changeQuestStep(env, step, nextStep, reward);
				return sendQuestDialog(env, checkOkId);
			}
			
			return closeDialogWindow(env);
		}
		
		return false;
	}
	
	/**
	 * Checks if a player has the required items to progress a quest.<br>
	 * It validates the item count and handles optional rewards or removals.<br>
	 * If successful, it updates the quest step and sends a success dialog.<br>
	 * If unsuccessful, it sends a failure dialog.
	 * @param env The current {@code QuestEnv} context.
	 * @param step The current quest step to verify.
	 * @param nextStep The quest step to move to upon success.
	 * @param reward Whether to grant a reward after completion.
	 * @param itemId The unique ID of the item to check.
	 * @param itemCount The required quantity of the item.
	 * @param remove Whether to remove the items from the inventory.
	 * @param checkOkId The dialog ID to send on success.
	 * @param checkFailId The dialog ID to send on failure.
	 * @param giveItemId The ID of the item to grant if applicable.
	 * @param giveItemCount The quantity of the item to grant if applicable.
	 * @return {@code true} if the quest step was successfully
	 */
	public boolean checkItemExistence(QuestEnv env, int step, int nextStep, boolean reward, int itemId, int itemCount, boolean remove, int checkOkId, int checkFailId, int giveItemId, int giveItemCount)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getQuestVarById(0) == step)
		{
			if (checkItemExistence(env, itemId, itemCount, remove))
			{
				if ((giveItemId != 0) && (giveItemCount != 0))
				{
					if (!giveQuestItem(env, giveItemId, giveItemCount))
					{
						return false;
					}
				}
				
				changeQuestStep(env, step, nextStep, reward);
				return sendQuestDialog(env, checkOkId);
			}
			
			return sendQuestDialog(env, checkFailId);
		}
		
		return false;
	}
	
	/**
	 * Checks if a player has enough of a specific item in their inventory.<br>
	 * If {@code remove} is {@code true}, it attempts to subtract the items from the inventory.
	 * @param env The current quest environment context.
	 * @param itemId The unique identifier of the item to check.
	 * @param itemCount The minimum quantity required for the check.
	 * @param remove Whether to remove the items from the player's inventory if they exist.
	 * @return {@code true} if the requirements are met and removal is successful, {@code false} otherwise.
	 */
	public boolean checkItemExistence(QuestEnv env, int itemId, int itemCount, boolean remove)
	{
		final Player player = env.getPlayer();
		if (player.getInventory().getItemCountByItemId(itemId) >= itemCount)
		{
			if (remove)
			{
				if (!removeQuestItem(env, itemId, itemCount))
				{
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sends an emotion animation for a specific creature.<br>
	 * This method handles the visual feedback for quest actions.
	 * @param env The current {@link QuestEnv} context.
	 * @param emoteCreature The {@link Creature} that will perform the animation.
	 * @param emotion The {@link EmotionId} to be played.
	 * @param broadcast Set to {@code true} to send to all players, or {@code false} for private view.
	 */
	public void sendEmotion(QuestEnv env, Creature emoteCreature, EmotionId emotion, boolean broadcast)
	{
		final Player player = env.getPlayer();
		final int targetId = player.equals(emoteCreature) ? env.getVisibleObject().getObjectId() : player.getObjectId();
		
		// TODO: fix it, broadcast and direction sometimes do not work when the emoteCreature is NPC
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(emoteCreature, EmotionType.EMOTE, emotion.id(), targetId), broadcast);
	}
	
	/**
	 * Grants a specific quest item to the player.<br>
	 * This method updates the player's inventory based on the provided ID and count.
	 * @param env The current quest environment context.
	 * @param itemId The unique identifier of the item to give.
	 * @param itemCount The number of items to add to the inventory.
	 * @return {@code true} if the item was successfully given, {@code false} otherwise.
	 */
	public boolean giveQuestItem(QuestEnv env, int itemId, long itemCount)
	{
		return giveQuestItem(env, itemId, itemCount, ItemAddType.QUEST_WORK_ITEM, ItemUpdateType.INC_ITEM_COLLECT);
	}
	
	/**
	 * Gives a specific item to the player within the quest environment.<br>
	 * This method handles adding items based on the provided ID and count.
	 * @param env The current {@code QuestEnv} context for the quest.
	 * @param itemId The unique identifier of the item to give.
	 * @param itemCount The number of items to provide.
	 * @param addType The type of addition to perform on the inventory.
	 * @return {@code true} if the item was successfully given, {@code false} otherwise.
	 */
	public boolean giveQuestItem(QuestEnv env, int itemId, long itemCount, ItemAddType addType)
	{
		return giveQuestItem(env, itemId, itemCount, addType, ItemUpdateType.INC_ITEM_COLLECT);
	}
	
	/**
	 * Gives a specific quest item to the player based on the provided environment.<br>
	 * This method checks if the player already has enough items before adding more.<br>
	 * It returns {@code true} if the operation is successful or if the player already has sufficient items.<br>
	 * If the player has too many items, it sends a system message and still returns {@code true}.
	 * @param env The current quest environment context.
	 * @param itemId The unique identifier of the item to give.
	 * @param itemCount The total amount of the item the player should have after this operation.
	 * @param addType The type of addition for the item.
	 * @param updateType The type of update for the item.
	 * @return {@code true} if the items were processed successfully, {@code false} otherwise.
	 */
	public boolean giveQuestItem(QuestEnv env, int itemId, long itemCount, ItemAddType addType, ItemUpdateType updateType)
	{
		final Player player = env.getPlayer();
		final ItemTemplate item = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if ((itemId != 0) && (itemCount != 0))
		{
			final long existentItemCount = player.getInventory().getItemCountByItemId(itemId);
			if (existentItemCount < itemCount)
			{
				final long itemsToGive = itemCount - existentItemCount;
				final ItemService.ItemUpdatePredicate predicate = new ItemService.ItemUpdatePredicate(addType, updateType);
				return ItemService.addQuestItems(player, Collections.singletonList(new QuestItems(itemId, itemsToGive)), predicate);
			}
			
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CAN_NOT_GET_LORE_ITEM((new DescriptionId(item.getNameId()))));
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes a specific quantity of an item from the player's inventory.<br>
	 * This method checks if both {@code itemId} and {@code itemCount} are non-zero.<br>
	 * It then updates the quest status based on the current progress.
	 * @param env The environment containing the {@link Player} data.
	 * @param itemId The unique identifier of the item to remove.
	 * @param itemCount The amount of the item to subtract from the inventory.
	 * @return {@code true} if the items were successfully removed, otherwise {@code false}.
	 */
	public boolean removeQuestItem(QuestEnv env, int itemId, long itemCount)
	{
		final Player player = env.getPlayer();
		if ((itemId != 0) && (itemCount != 0))
		{
			final QuestState qs = player.getQuestStateList().getQuestState(questId);
			return player.getInventory().decreaseByItemId(itemId, itemCount, qs == null ? QuestStatus.START : qs.getStatus());
		}
		
		return false;
	}
	
	/**
	 * Removes a specific quantity of an item from the player's inventory.<br>
	 * This method checks if both {@code itemId} and {@code itemCount} are non-zero.<br>
	 * It then calls the decrease method on the player's inventory.
	 * @param env The current quest environment context.
	 * @param itemId The unique identifier of the item to remove.
	 * @param itemCount The amount of the item to subtract from the inventory.
	 * @param questStatus The status associated with this quest action.
	 * @return {@code true} if the items were successfully removed, {@code false} otherwise.
	 */
	public boolean removeQuestItem(QuestEnv env, int itemId, long itemCount, QuestStatus questStatus)
	{
		final Player player = env.getPlayer();
		if ((itemId != 0) && (itemCount != 0))
		{
			return player.getInventory().decreaseByItemId(itemId, itemCount, questStatus);
		}
		
		return false;
	}
	
	/**
	 * Plays a specific movie for the player in the current quest environment.<br>
	 * This method sends an {@code SM_PLAY_MOVIE} packet to the player.
	 * @param env The current quest environment containing the player information.
	 * @param MovieId The unique identifier of the movie to be played.
	 * @return Always returns {@code false}.
	 */
	public boolean playQuestMovie(QuestEnv env, int MovieId)
	{
		final Player player = env.getPlayer();
		PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, MovieId));
		return false;
	}
	
	/**
	 * Plays a specific movie for the player in the current environment.<br>
	 * This method sends an {@code SM_PLAY_MOVIE} packet to the client.
	 * @param env The quest environment containing the player information.
	 * @param Type The type of the movie to be played.
	 * @param MovieId The unique identifier for the movie.
	 * @return Always returns {@code false}.
	 */
	public boolean playQuestMovie(QuestEnv env, int Type, int MovieId)
	{
		final Player player = env.getPlayer();
		PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(Type, MovieId));
		return false;
	}
	
	/**
	 * Handles the default logic when an NPC is killed during a quest.<br>
	 * This method updates variables based on the kill event.
	 * @param env The current {@code QuestEnv} context.
	 * @param npcId The unique identifier of the NPC that was killed.
	 * @param startVar The starting value for the variable update.
	 * @param endVar The ending value for the variable update.
	 * @return {@code true} if the event was processed successfully, {@code false} otherwise.
	 */
	public boolean defaultOnKillEvent(QuestEnv env, int npcId, int startVar, int endVar)
	{
		final int[] mobids =
		{
			npcId
		};
		return defaultOnKillEvent(env, mobids, startVar, endVar);
	}
	
	/**
	 * Handles the quest logic when a specific NPC is killed.<br>
	 * This method checks if the kill event should trigger a quest update.<br>
	 * It uses the provided variable range to determine the progress.
	 * @param env The current quest environment context.
	 * @param npcIds An array of unique identifiers for the NPCs involved in the kill.
	 * @param startVar The starting value for the quest counter.
	 * @param endVar The target value required to complete the objective.
	 * @return {@code true} if the event was successfully processed, {@code false} otherwise.
	 */
	public boolean defaultOnKillEvent(QuestEnv env, int[] npcIds, int startVar, int endVar)
	{
		return defaultOnKillEvent(env, npcIds, startVar, endVar, 0);
	}
	
	/**
	 * Handles the default logic for a kill event when an NPC is defeated.<br>
	 * This method updates quest variables based on the specified range.
	 * @param env The current quest environment context.
	 * @param npcId The unique identifier of the NPC that was killed.
	 * @param startVar The starting value for the variable update.
	 * @param endVar The ending value for the variable update.
	 * @param varNum The index of the quest variable to modify.
	 * @return {@code true} if the event was processed successfully, {@code false} otherwise.
	 */
	public boolean defaultOnKillEvent(QuestEnv env, int npcId, int startVar, int endVar, int varNum)
	{
		final int[] mobids =
		{
			npcId
		};
		return defaultOnKillEvent(env, mobids, startVar, endVar, varNum);
	}
	
	/**
	 * Handles the default logic for a kill event in a quest.<br>
	 * It checks if the killed NPC is in the allowed list and updates the quest variable.<br>
	 * The variable is incremented only if it falls within the specified range.
	 * @param env The current {@code QuestEnv} context.
	 * @param npcIds An array of {@code int} IDs representing valid NPCs for this event.
	 * @param startVar The minimum value (inclusive) for the quest variable.
	 * @param endVar The maximum value (exclusive) for the quest variable.
	 * @param varNum The specific quest variable ID to update.
	 * @return {@code true} if the quest variable was successfully updated, otherwise {@code false}.
	 */
	public boolean defaultOnKillEvent(QuestEnv env, int[] npcIds, int startVar, int endVar, int varNum)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			final int var = qs.getQuestVarById(varNum);
			final int targetId = env.getTargetId();
			for (int id : npcIds)
			{
				if (targetId == id)
				{
					if ((var >= startVar) && (var < endVar))
					{
						qs.setQuestVarById(varNum, var + 1);
						updateQuestStatus(env);
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Sets the default behavior for a kill event.<br>
	 * This method handles what happens when an NPC is killed during a quest.<br>
	 * It updates the quest progress based on the provided variables.
	 * @param env The current quest environment context.
	 * @param npcId The unique identifier of the NPC to be killed.
	 * @param startVar The variable index used to track progress.
	 * @param reward Whether a reward should be granted upon completion.
	 * @return {@code true} if the event was processed successfully, {@code false} otherwise.
	 */
	public boolean defaultOnKillEvent(QuestEnv env, int npcId, int startVar, boolean reward)
	{
		final int[] mobids =
		{
			npcId
		};
		return (defaultOnKillEvent(env, mobids, startVar, reward, 0));
	}
	
	/**
	 * Handles the default quest event when a specific NPC is killed.<br>
	 * This method updates the quest progress based on the provided parameters.
	 * @param env The current {@code QuestEnv} context.
	 * @param npcId The unique identifier of the NPC to be killed.
	 * @param startVar The starting variable for the quest step.
	 * @param reward Whether a reward should be granted upon completion.
	 * @param varNum The specific variable number associated with this event.
	 * @return {@code true} if the event was processed successfully, {@code false} otherwise.
	 */
	public boolean defaultOnKillEvent(QuestEnv env, int npcId, int startVar, boolean reward, int varNum)
	{
		final int[] mobids =
		{
			npcId
		};
		return (defaultOnKillEvent(env, mobids, startVar, reward, varNum));
	}
	
	/**
	 * Handles the logic for a kill event in a quest.<br>
	 * This method checks if specific NPCs were killed to progress the quest.<br>
	 * It updates variables and handles rewards based on the provided parameters.
	 * @param env The current quest environment context.
	 * @param npcIds An array of IDs representing the NPCs that were killed.
	 * @param startVar The variable index to update in the quest data.
	 * @param reward A boolean indicating if a reward should be given upon completion.
	 * @return {@code true} if the event was processed successfully, {@code false} otherwise.
	 */
	public boolean defaultOnKillEvent(QuestEnv env, int[] npcIds, int startVar, boolean reward)
	{
		return (defaultOnKillEvent(env, npcIds, startVar, reward, 0));
	}
	
	/**
	 * Checks if a quest should progress when an NPC is killed.<br>
	 * This method verifies the target ID against the provided {@code npcIds}.<br>
	 * It updates the quest status or variable based on the {@code reward} flag.
	 * @param env The current quest environment context.
	 * @param npcIds An array of NPC IDs that trigger this event.
	 * @param startVar The required variable value to trigger the progression.
	 * @param reward Whether to set the status to {@code REWARD} or increment a variable.
	 * @param varNum The specific quest variable index to check and update.
	 * @return {@code true} if the quest progressed successfully, otherwise {@code false}.
	 */
	public boolean defaultOnKillEvent(QuestEnv env, int[] npcIds, int startVar, boolean reward, int varNum)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			final int var = qs.getQuestVarById(varNum);
			final int targetId = env.getTargetId();
			for (int id : npcIds)
			{
				if (targetId == id)
				{
					if (var == startVar)
					{
						if (reward)
						{
							qs.setStatus(QuestStatus.REWARD);
						}
						else
						{
							qs.setQuestVarById(varNum, var + 1);
						}
						
						updateQuestStatus(env);
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Automatically advances the quest step based on a kill count variable.<br>
	 * This method checks if the current variable is within the specified range.<br>
	 * It updates the quest progress or triggers a reward depending on the {@code reward} flag.
	 * @param env The current quest environment context.
	 * @param startVar The minimum value required to trigger an automatic step advance.
	 * @param endVar The target value that completes the current objective.
	 * @param reward Whether to set the quest status to {@code REWARD} when reaching the end value.
	 * @return {@code true} if the quest was successfully updated, otherwise {@code false}.
	 */
	public boolean defaultOnKillRankedEvent(QuestEnv env, int startVar, int endVar, boolean reward)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			final int var = qs.getQuestVarById(0);
			if ((var >= startVar) && (var < (endVar - 1)))
			{
				changeQuestStep(env, var, var + 1, false);
				return true;
			}
			else if (var == (endVar - 1))
			{
				if (reward)
				{
					qs.setStatus(QuestStatus.REWARD);
				}
				else
				{
					qs.setQuestVarById(0, var + 1);
				}
				
				updateQuestStatus(env);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a quest variable falls within a specific range to trigger a step change.<br>
	 * This method is used to automatically progress the quest when a skill event occurs.<br>
	 * It updates the quest state using {@code int, int, boolean, int)} if the condition is met.
	 * @param env The current quest environment context.
	 * @param startVar The minimum value required to trigger the change.
	 * @param endVar The maximum value limit (exclusive) for the range.
	 * @param varNum The specific variable ID to check in the quest state.
	 * @return {@code true} if the step was successfully updated, otherwise {@code false}.
	 */
	public boolean defaultOnUseSkillEvent(QuestEnv env, int startVar, int endVar, int varNum)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			final int var = qs.getQuestVarById(varNum);
			if ((var >= startVar) && (var < endVar))
			{
				changeQuestStep(env, var, var + 1, false, varNum);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Starts a follow event for an {@link Npc} follower.<br>
	 * This method updates the NPC info and triggers the follow behavior.<br>
	 * It also adds a quest task to check if the player is following the target.
	 * @param env The current quest environment.
	 * @param follower The NPC that will follow the player.
	 * @param targetNpcId The ID of the NPC to be followed.
	 * @param step The current quest step.
	 * @param nextStep The next quest step.
	 * @return {@code true} if the steps are both 0, otherwise it closes the dialog and returns the result.
	 */
	public boolean defaultStartFollowEvent(QuestEnv env, Npc follower, int targetNpcId, int step, int nextStep)
	{
		final Player player = env.getPlayer();
		if (!(env.getVisibleObject() instanceof Npc))
		{
			return false;
		}
		
		PacketSendUtility.sendPacket(player, new SM_NPC_INFO(follower, player));
		follower.getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, player);
		player.getController().addTask(TaskId.QUEST_FOLLOW, QuestTasks.newFollowingToTargetCheckTask(env, follower, targetNpcId));
		if ((step == 0) && (nextStep == 0))
		{
			return true;
		}
		
		return defaultCloseDialog(env, step, nextStep);
	}
	
	/**
	 * Starts a follow event where an {@link Npc} follows the player.<br>
	 * This method updates the NPC info and adds a quest task to track movement.<br>
	 * It returns {@code true} if the step remains at {@code 0}.
	 * @param env The current quest environment context.
	 * @param follower The {@link Npc} that will follow the player.
	 * @param x The target X coordinate for the task.
	 * @param y The target Y coordinate for the task.
	 * @param z The target Z coordinate for the task.
	 * @param step The current quest step.
	 * @param nextStep The next quest step.
	 * @return {@code true} if the step is still {@code 0}, otherwise {@code false}.
	 */
	public boolean defaultStartFollowEvent(QuestEnv env, Npc follower, float x, float y, float z, int step, int nextStep)
	{
		final Player player = env.getPlayer();
		if (!(env.getVisibleObject() instanceof Npc))
		{
			return false;
		}
		
		PacketSendUtility.sendPacket(player, new SM_NPC_INFO(follower, player));
		follower.getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, player);
		player.getController().addTask(TaskId.QUEST_FOLLOW, QuestTasks.newFollowingToTargetCheckTask(env, follower, x, y, z));
		if ((step == 0) && (nextStep == 0))
		{
			return true;
		}
		
		return defaultCloseDialog(env, step, nextStep);
	}
	
	/**
	 * Starts a follow event for an {@link Npc} follower.<br>
	 * This method updates the NPC info and triggers the follow behavior.<br>
	 * It also adds a quest task to track the following progress.
	 * @param env The current quest environment context.
	 * @param follower The {@link Npc} that will follow the player.
	 * @param zonename The name of the zone where the event occurs.
	 * @param step The current quest step number.
	 * @param nextStep The next quest step number.
	 * @return {@code true} if both steps are 0, otherwise it returns the result of {@code int, int)}.
	 */
	public boolean defaultStartFollowEvent(QuestEnv env, Npc follower, ZoneName zonename, int step, int nextStep)
	{
		final Player player = env.getPlayer();
		if (!(env.getVisibleObject() instanceof Npc))
		{
			return false;
		}
		
		PacketSendUtility.sendPacket(player, new SM_NPC_INFO(follower, player));
		follower.getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, player);
		player.getController().addTask(TaskId.QUEST_FOLLOW, QuestTasks.newFollowingToTargetCheckTask(env, follower, zonename));
		if ((step == 0) && (nextStep == 0))
		{
			return true;
		}
		
		return defaultCloseDialog(env, step, nextStep);
	}
	
	/**
	 * Starts a follow event for an {@link Npc} follower.<br>
	 * This method updates the NPC info and adds a quest task to track movement.<br>
	 * It checks if the current step is zero before closing any active dialogs.
	 * @param env The current quest environment context.
	 * @param follower The {@link Npc} that will follow the player.
	 * @param zone1 The first boundary zone for the follow task.
	 * @param zone2 The second boundary zone for the follow task.
	 * @param step The current quest step number.
	 * @param nextStep The next expected quest step number.
	 * @return {@code true} if the start conditions are met, otherwise {@code false}.
	 */
	public boolean defaultStartFollowEvent(QuestEnv env, Npc follower, ZoneName zone1, ZoneName zone2, int step, int nextStep)
	{
		final Player player = env.getPlayer();
		if (!(env.getVisibleObject() instanceof Npc))
		{
			return false;
		}
		
		PacketSendUtility.sendPacket(player, new SM_NPC_INFO(follower, player));
		follower.getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, player);
		player.getController().addTask(TaskId.QUEST_FOLLOW, QuestTasks.newFollowingToTargetCheckTask(env, follower, zone1, zone2));
		if ((step == 0) && (nextStep == 0))
		{
			return true;
		}
		
		return defaultCloseDialog(env, step, nextStep);
	}
	
	/**
	 * Handles the default end event for a quest step.<br>
	 * This method updates the quest progress and plays a movie if required.<br>
	 * It checks if the current quest variable matches the expected {@code step}.
	 * @param env The environment containing the player and quest data.
	 * @param step The current step value to check against the quest variable.
	 * @param nextStep The new step value to set after completion.
	 * @param reward Whether a reward should be granted upon completion.
	 * @param movie The ID of the movie to play, or {@code 0} if none is required.
	 * @return {@code true} if the quest step was successfully updated, {@code false} otherwise.
	 */
	public boolean defaultFollowEndEvent(QuestEnv env, int step, int nextStep, boolean reward, int movie)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			if (qs.getQuestVarById(0) == step)
			{
				changeQuestStep(env, step, nextStep, reward);
				if (movie != 0)
				{
					playQuestMovie(env, movie);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Handles the end event of a quest when no specific variable is provided.<br>
	 * This method calls {@code int, int, boolean, int)} with a default value.
	 * @param env The current quest environment.
	 * @param step The current quest step.
	 * @param nextStep The next quest step to move to.
	 * @param reward Whether the player should receive a reward.
	 * @return {@code true} if the operation was successful, {@code false} otherwise.
	 */
	public boolean defaultFollowEndEvent(QuestEnv env, int step, int nextStep, boolean reward)
	{
		return defaultFollowEndEvent(env, step, nextStep, reward, 0);
	}
	
	/**
	 * Checks if the quest is at a specific step and updates it to the next one.<br>
	 * This method verifies that the quest status is {@code START} before proceeding.<br>
	 * It calls {@code int, int, boolean)} if the current step matches.
	 * @param env The environment containing the player and quest data.
	 * @param step The current expected step of the quest.
	 * @param nextStep The new step to move the quest to.
	 * @param reward Whether a reward should be granted during the transition.
	 * @return {@code true} if the quest was successfully updated, otherwise {@code false}.
	 */
	public boolean defaultOnGetItemEvent(QuestEnv env, int step, int nextStep, boolean reward)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			if (qs.getQuestVarById(0) == step)
			{
				changeQuestStep(env, step, nextStep, reward);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Processes the interaction with a quest object.<br>
	 * This method updates the quest progress based on the provided parameters.
	 * @param env The current quest environment context.
	 * @param step The current quest step number.
	 * @param nextStep The next quest step to move to.
	 * @param reward Whether a reward should be granted.
	 * @param die Whether the character should die after this action.
	 * @return {@code true} if the operation was successful, {@code false} otherwise.
	 */
	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, boolean die)
	{
		return useQuestObject(env, step, nextStep, reward, 0, 0, 0, 0, 0, 0, die);
	}
	
	/**
	 * Processes the usage of a quest object for a specific player environment.<br>
	 * This method updates the quest progress and handles rewards or death states.
	 * @param env The current {@code QuestEnv} context.
	 * @param step The current quest step number.
	 * @param nextStep The target quest step to move to.
	 * @param reward Whether a reward should be granted.
	 * @param varNum The specific variable index to update.
	 * @param die Whether the player character should die after this action.
	 * @return {@code true} if the operation was successful, {@code false} otherwise.
	 */
	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, int varNum, boolean die)
	{
		return useQuestObject(env, step, nextStep, reward, varNum, 0, 0, 0, 0, 0, die);
	}
	
	/**
	 * Processes the usage of a quest object for a specific player environment.<br>
	 * This method updates the quest progress based on the provided step information.
	 * @param env The current {@code QuestEnv} context.
	 * @param step The current quest step number.
	 * @param nextStep The target quest step to move to.
	 * @param reward Whether a reward should be granted upon completion.
	 * @param varNum The specific variable index associated with this action.
	 * @return {@code true} if the operation was successful, {@code false} otherwise.
	 */
	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, int varNum)
	{
		return useQuestObject(env, step, nextStep, reward, varNum, 0, 0, 0, 0, 0, false);
	}
	
	/**
	 * Processes the usage of a quest object for a player.<br>
	 * This method updates the quest progress and handles rewards or items.
	 * @param env The current quest environment context.
	 * @param step The current quest step number.
	 * @param nextStep The next quest step to move to.
	 * @param reward Whether a reward should be granted.
	 * @param varNum A variable number associated with the quest action.
	 * @param addItemId The ID of the item to add if applicable.
	 * @param addItemCount The quantity of the item to add if applicable.
	 * @return {@code true} if the operation was successful, {@code false} otherwise.
	 */
	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, int varNum, int addItemId, int addItemCount)
	{
		return useQuestObject(env, step, nextStep, reward, varNum, addItemId, addItemCount, 0, 0, 0, false);
	}
	
	/**
	 * Processes a quest object interaction to progress the player's quest.<br>
	 * This method handles step transitions and item rewards or removals.
	 * @param env The current {@code QuestEnv} context for the player.
	 * @param step The current quest step number.
	 * @param nextStep The new quest step to move to.
	 * @param reward Whether a reward should be granted.
	 * @param varNum A variable number used for internal quest logic.
	 * @param addItemId The ID of the item to add to the player.
	 * @param addItemCount The quantity of the item to add.
	 * @param removeItemId The ID of the item to remove from the player.
	 * @param removeItemCount The quantity of the item to remove.
	 * @return {@code true} if the quest object was used successfully, {@code false} otherwise.
	 */
	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, int varNum, int addItemId, int addItemCount, int removeItemId, int removeItemCount)
	{
		return useQuestObject(env, step, nextStep, reward, varNum, addItemId, addItemCount, removeItemId, removeItemCount, 0, false);
	}
	
	/**
	 * Processes the usage of a quest object for a specific player environment.<br>
	 * This method handles transitions between quest steps and manages rewards.<br>
	 * It also triggers associated movie playback if required.
	 * @param env The current {@code QuestEnv} context for the player.
	 * @param step The current quest step index.
	 * @param nextStep The target quest step to move to.
	 * @param reward A {@code boolean} indicating if a reward should be granted.
	 * @param varNum The specific variable number associated with this action.
	 * @param movieId The ID of the movie to play during this action.
	 * @return {@code true} if the quest object was used successfully, {@code false} otherwise.
	 */
	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, int varNum, int movieId)
	{
		return useQuestObject(env, step, nextStep, reward, varNum, 0, 0, 0, 0, movieId, false);
	}
	
	/**
	 * Executes a specific quest action based on the current progress.<br>
	 * This method handles item rewards, removals, movie playback, and NPC death.<br>
	 * It updates the quest step only if the internal variable matches the required value.
	 * @param env The {@code QuestEnv} containing the player and environment data.
	 * @param step The current expected step for the quest action.
	 * @param nextStep The new step to set after completing this action.
	 * @param reward Whether a reward should be granted to the player.
	 * @param varNum The specific quest variable ID to check against the current step.
	 * @param addItemId The ID of the item to give, or {@code 0} if none.
	 * @param addItemCount The quantity of the item to give.
	 * @param removeItemId The ID of the item to remove, or {@code 0} if none.
	 * @param removeItemCount The quantity of the item to remove.
	 * @param movieId The ID of the movie to play, or {@code 0} if none.
	 * @param dieObject Whether the target NPC should be killed.
	 * @return {@code true} if
	 */
	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, int varNum, int addItemId, int addItemCount, int removeItemId, int removeItemCount, int movieId, boolean dieObject)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
		{
			return false;
		}
		
		if (qs.getQuestVarById(varNum) == step)
		{
			if ((addItemId != 0) && (addItemCount != 0))
			{
				if (!giveQuestItem(env, addItemId, addItemCount))
				{
					return false;
				}
			}
			
			if ((removeItemId != 0) && (removeItemCount != 0))
			{
				removeQuestItem(env, removeItemId, removeItemCount);
			}
			
			if (movieId != 0)
			{
				playQuestMovie(env, movieId);
			}
			
			if (dieObject)
			{
				final Npc npc = (Npc) player.getTarget();
				if ((npc == null) || (npc.getObjectId() != env.getVisibleObject().getObjectId()))
				{
					return false;
				}
				
				npc.getController().onDie(player);
			}
			
			changeQuestStep(env, step, nextStep, reward, varNum);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Processes the usage of a quest item by a player.<br>
	 * This method checks if the {@code item} is valid for the current quest step.<br>
	 * It updates the quest progress and handles rewards if applicable.
	 * @param env The current quest environment context.
	 * @param item The {@code Item} being used by the player.
	 * @param step The current quest step identifier.
	 * @param nextStep The quest step to move to after usage.
	 * @param reward Whether a reward should be granted upon successful use.
	 * @return {@code true} if the item was used successfully, {@code false} otherwise.
	 */
	public boolean useQuestItem(QuestEnv env, Item item, int step, int nextStep, boolean reward)
	{
		return useQuestItem(env, item, step, nextStep, reward, 0, 0, 0);
	}
	
	/**
	 * Processes the usage of a quest item to progress a quest.<br>
	 * This method checks if an {@link Item} can be used for a specific quest step.<br>
	 * It updates the quest state and handles rewards or new items if required.
	 * @param env The current quest environment context.
	 * @param item The {@link Item} being used by the player.
	 * @param step The current quest step index.
	 * @param nextStep The target quest step to move to.
	 * @param reward Whether a reward should be granted upon completion.
	 * @param addItemId The ID of the item to give as a reward.
	 * @param addItemCount The amount of the reward item to give.
	 * @return {@code true} if the quest item was successfully used, {@code false} otherwise.
	 */
	public boolean useQuestItem(QuestEnv env, Item item, int step, int nextStep, boolean reward, int addItemId, int addItemCount)
	{
		return useQuestItem(env, item, step, nextStep, reward, addItemId, addItemCount, 0);
	}
	
	/**
	 * Processes the usage of a quest item by a player.<br>
	 * This method updates the quest progress and handles rewards or movies.
	 * @param env The current quest environment context.
	 * @param item The {@code Item} being used by the player.
	 * @param step The current quest step number.
	 * @param nextStep The quest step to move to after usage.
	 * @param reward Whether a reward should be granted upon completion.
	 * @param movieId The ID of the movie to play if applicable.
	 * @return {@code true} if the item was used successfully, {@code false} otherwise.
	 */
	public boolean useQuestItem(QuestEnv env, Item item, int step, int nextStep, boolean reward, int movieId)
	{
		return useQuestItem(env, item, step, nextStep, reward, 0, 0, movieId);
	}
	
	/**
	 * Processes the usage of a quest item for a specific player.<br>
	 * This method updates the quest progress and handles rewards or animations.
	 * @param env The current quest environment context.
	 * @param item The {@code Item} being used by the player.
	 * @param step The current quest step number.
	 * @param nextStep The quest step to move to after usage.
	 * @param reward Whether a reward should be granted upon use.
	 * @param addItemId The ID of the item to give as a reward.
	 * @param addItemCount The amount of the reward item to give.
	 * @param movieId The ID of the movie to play during usage.
	 * @return {@code true} if the quest item was used successfully, {@code false} otherwise.
	 */
	public boolean useQuestItem(QuestEnv env, Item item, int step, int nextStep, boolean reward, int addItemId, int addItemCount, int movieId)
	{
		return useQuestItem(env, item, step, nextStep, reward, addItemId, addItemCount, movieId, 0);
	}
	
	/**
	 * Handles the logic for a player using a specific quest item.<br>
	 * This method checks if the current quest variable matches the required step.<br>
	 * It plays an animation, removes the used item, and updates the quest progress.
	 * @param env The environment context containing the player information.
	 * @param item The {@code Item} object being used by the player.
	 * @param step The required current step for this action.
	 * @param nextStep The new step to transition to after completion.
	 * @param reward Whether a reward should be granted upon success.
	 * @param addItemId The ID of the item to give if it is not {@code 0}.
	 * @param addItemCount The quantity of the item to give if it is not {@code 0}.
	 * @param movieId The movie ID to play if it is not {@code 0}.
	 * @param varNum The quest variable index to check against the current step.
	 * @return {@code true} if the item was successfully used, otherwise {@code false}.
	 */
	public boolean useQuestItem(QuestEnv env, Item item, int step, int nextStep, boolean reward, int addItemId, int addItemCount, int movieId, int varNum)
	{
		final Player player = env.getPlayer();
		if (player == null)
		{
			return false;
		}
		
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
		{
			return false;
		}
		
		final int itemId = item.getItemId();
		final int objectId = item.getObjectId();
		
		if (qs.getQuestVarById(varNum) == step)
		{
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, objectId, itemId, 3000, 0), true);
			ThreadPoolManager.getInstance().schedule(() ->
			{
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, objectId, itemId, 0, 1), true);
				removeQuestItem(env, itemId, 1);
				
				if ((addItemId != 0) && (addItemCount != 0))
				{
					if (!giveQuestItem(env, addItemId, addItemCount))
					{
						return;
					}
				}
				
				if (movieId != 0)
				{
					playQuestMovie(env, movieId);
				}
				
				changeQuestStep(env, step, nextStep, reward, varNum);
			}, 3000);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Handles the logic for when a zone mission ends.<br>
	 * This method triggers the default sequence of events for completing a mission.
	 * @param env The {@code QuestEnv} object containing the current quest environment.
	 * @return {@code true} if the event was processed successfully, {@code false} otherwise.
	 */
	public boolean defaultOnZoneMissionEndEvent(QuestEnv env)
	{
		final int[] quests =
		{
			0
		};
		return defaultOnZoneMissionEndEvent(env, quests);
	}
	
	/**
	 * Triggers the default event for a zone mission ending.<br>
	 * This method handles the standard logic when a quest finishes in a specific area.
	 * @param env The current {@code QuestEnv} context.
	 * @param quest The unique ID of the {@code quest}.
	 * @return {@code true} if the event was processed successfully, otherwise {@code false}.
	 */
	public boolean defaultOnZoneMissionEndEvent(QuestEnv env, int quest)
	{
		final int[] quests =
		{
			quest
		};
		return defaultOnZoneMissionEndEvent(env, quests);
	}
	
	/**
	 * Checks if a player can start a specific mission based on various requirements.<br>
	 * It verifies level, prerequisite quests, and custom XML conditions.<br>
	 * If all conditions are met, the quest is started with {@code QuestStatus.START}.
	 * @param env The current quest environment containing player data.
	 * @param quests An array of required quest IDs that must be completed first.
	 * @return {@code true} if the mission was successfully started, {@code false} otherwise.
	 */
	public boolean defaultOnZoneMissionEndEvent(QuestEnv env, int[] quests)
	{
		final Player player = env.getPlayer();
		env.setQuestId(questId);
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		// Only null quests can be started!
		// Check all player requirements
		if ((qs != null) || !QuestService.checkMissionStatConditions(env))
		{
			return false;
		}
		
		// Check, if the player has required level
		if (!QuestService.checkLevelRequirement(questId, player.getCommonData().getLevel()))
		{
			QuestService.startMission(env, QuestStatus.LOCKED);
			return false;
		}
		
		// Check the quests, that has to be done before starting this one
		for (int id : quests)
		{
			if (id != 0)
			{
				final QuestState qs2 = player.getQuestStateList().getQuestState(id);
				if ((qs2 == null) || (qs2.getStatus() != QuestStatus.COMPLETE))
				{
					QuestService.startMission(env, QuestStatus.LOCKED);
					return false;
				}
			}
		}
		
		// Check other start conditions; missions listed in the quest_data zone should already be locked.
		final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(env.getQuestId());
		for (XMLStartCondition startCondition : template.getXMLStartConditions())
		{
			if (!startCondition.check(player, false))
			{
				QuestService.startMission(env, QuestStatus.LOCKED);
				return false;
			}
		}
		
		// All conditions are done. Start the quest
		QuestService.startMission(env, QuestStatus.START);
		return true;
	}
	
	/**
	 * Handles the default behavior when a player levels up during a quest.<br>
	 * This method checks if any specific quests should be affected by the level up.<br>
	 * It uses {@code QuestEnv} to manage the current environment state.
	 * @param env The current quest environment context.
	 * @return {@code true} if the event was handled successfully, {@code false} otherwise.
	 */
	public boolean defaultOnLvlUpEvent(QuestEnv env)
	{
		final int[] quests =
		{
			0
		};
		return defaultOnLvlUpEvent(env, quests, false);
	}
	
	/**
	 * Triggers the default level up event for a specific quest.<br>
	 * This method handles standard logic when a player levels up during a quest.
	 * @param env The {@code QuestEnv} object containing the current quest environment.
	 * @param quest The unique ID of the quest to process.
	 * @return {@code true} if the event was processed successfully, {@code false} otherwise.
	 */
	public boolean defaultOnLvlUpEvent(QuestEnv env, int quest)
	{
		final int[] quests =
		{
			quest
		};
		return defaultOnLvlUpEvent(env, quests, false);
	}
	
	/**
	 * Handles the default logic for a level up event.<br>
	 * This method checks if an action should occur when a player levels up.<br>
	 * It uses the provided {@code QuestEnv} to determine the current context.
	 * @param env The environment containing the current quest state.
	 * @param quest The unique identifier for the quest.
	 * @param isZoneMission A boolean indicating if the quest is a zone mission.
	 * @return {@code true} if the event was successfully handled, {@code false} otherwise.
	 */
	public boolean defaultOnLvlUpEvent(QuestEnv env, int quest, boolean isZoneMission)
	{
		final int[] quests =
		{
			quest
		};
		return defaultOnLvlUpEvent(env, quests, isZoneMission);
	}
	
	/**
	 * Checks if a player can start a quest based on level and prerequisites.<br>
	 * This method validates requirements like level, completed quests, and other conditions.<br>
	 * It starts the quest or sets it to {@code LOCKED} if requirements are not met.
	 * @param env The current quest environment context.
	 * @param quests An array of required quest IDs that must be completed.
	 * @param isZoneMission A boolean flag indicating if the quest is a zone mission.
	 * @return {@code true} if the quest was successfully started, {@code false} otherwise.
	 */
	public boolean defaultOnLvlUpEvent(QuestEnv env, int[] quests, boolean isZoneMission)
	{
		final Player player = env.getPlayer();
		env.setQuestId(questId);
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		// Only null and LOCKED quests can be started
		
		// Check if the player meets all requirements, including the required level.
		if (((qs != null) && (qs.getStatus() != QuestStatus.LOCKED)) || !QuestService.checkMissionStatConditions(env) || !QuestService.checkLevelRequirement(questId, player.getCommonData().getLevel()))
		{
			return false;
		}
		
		// Check the required quests and set the quest status to LOCKED if they are not met; zone missions should already be LOCKED.
		// TEMPORARY till the new quest_data will be parsed
		for (int id : quests)
		{
			if (id != 0)
			{
				final QuestState qs2 = player.getQuestStateList().getQuestState(id);
				if ((qs2 == null) || (qs2.getStatus() != QuestStatus.COMPLETE))
				{
					if ((qs == null) && !isZoneMission)
					{
						QuestService.startMission(env, QuestStatus.LOCKED);
					}
					
					return false;
				}
			}
		}
		
		// Check other start conditions; missions listed in the quest_data zone should already be locked.
		final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(env.getQuestId());
		for (XMLStartCondition startCondition : template.getXMLStartConditions())
		{
			if (!startCondition.check(player, false))
			{
				if ((qs == null) && !isZoneMission)
				{
					QuestService.startMission(env, QuestStatus.LOCKED);
				}
				
				return false;
			}
		}
		
		// All conditions are done. Start the quest
		if (qs == null)
		{
			QuestService.startMission(env, QuestStatus.START);
		}
		else
		{
			qs.setStatus(QuestStatus.START);
			updateQuestStatus(env);
		}
		
		return true;
	}
	
	/**
	 * Checks if a quest should start when entering a specific zone.<br>
	 * This method verifies if the {@code currentZoneName} matches the required {@code questZoneName}.<br>
	 * If the player does not have the quest, it attempts to start it using {@code startQuest}.
	 * @param env The environment containing the player and quest data.
	 * @param currentZoneName The name of the zone where the player is currently located.
	 * @param questZoneName The required zone name for the quest to trigger.
	 * @return {@code true} if the quest was successfully started, otherwise {@code false}.
	 */
	public boolean defaultOnEnterZoneEvent(QuestEnv env, ZoneName currentZoneName, ZoneName questZoneName)
	{
		if (questZoneName == currentZoneName)
		{
			final Player player = env.getPlayer();
			if (player == null)
			{
				return false;
			}
			
			final QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs == null)
			{
				env.setQuestId(questId);
				if (QuestService.startQuest(env))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Sends a dialog window to the player for receiving a quest reward.<br>
	 * This method triggers the reward sequence using specific NPC and dialog IDs.
	 * @param env The current quest environment context.
	 * @param rewardNpcId The unique identifier of the NPC providing the reward.
	 * @param reportDialogId The ID of the dialog to display for the reward.
	 * @return {@code true} if the dialog was sent successfully, {@code false} otherwise.
	 */
	public boolean sendQuestRewardDialog(QuestEnv env, int rewardNpcId, int reportDialogId)
	{
		return sendQuestRewardDialog(env, rewardNpcId, reportDialogId, 0);
	}
	
	/**
	 * Sends a quest reward dialog to the player.<br>
	 * This method checks if the player is in the {@code REWARD} status and interacting with the correct NPC.<br>
	 * It determines whether to show a report dialog or finish the quest based on the action type.
	 * @param env The current quest environment context.
	 * @param rewardNpcId The unique ID of the NPC providing the reward.
	 * @param reportDialogId The ID of the dialog to send if the player uses an object.
	 * @param rewardId The ID of the reward to grant upon completing the quest.
	 * @return {@code true} if the dialog was successfully sent, otherwise {@code false}.
	 */
	public boolean sendQuestRewardDialog(QuestEnv env, int rewardNpcId, int reportDialogId, int rewardId)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (env.getTargetId() == rewardNpcId)
			{
				if ((env.getDialog() == DialogAction.USE_OBJECT) && (reportDialogId != 0))
				{
					return sendQuestDialog(env, reportDialogId);
				}
				
				return sendQuestEndDialog(env, rewardId);
			}
		}
		
		return false;
	}
	
	/**
	 * Sends a quest dialog that does not have specific requirements.<br>
	 * This method uses the {@code QuestTemplate} associated with the current quest.<br>
	 * It triggers the dialog for the specified NPC.
	 * @param env The environment context for the current quest.
	 * @param startNpcId The unique identifier of the NPC starting the dialog.
	 * @return {@code true} if the dialog was sent successfully, {@code false} otherwise.
	 */
	public boolean sendQuestNoneDialog(QuestEnv env, int startNpcId)
	{
		final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		return sendQuestNoneDialog(env, template, startNpcId, 1011);
	}
	
	/**
	 * Sends a specific quest dialog to the player without any additional conditions.<br>
	 * This method uses the provided {@code QuestEnv} and NPC information to trigger the dialogue.
	 * @param env The current environment context for the quest.
	 * @param startNpcId The unique identifier of the NPC starting the dialog.
	 * @param dialogId The specific ID of the dialog page to display.
	 * @return {@code true} if the dialog was sent successfully, {@code false} otherwise.
	 */
	public boolean sendQuestNoneDialog(QuestEnv env, int startNpcId, int dialogId)
	{
		final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		return sendQuestNoneDialog(env, template, startNpcId, dialogId);
	}
	
	/**
	 * Sends a quest dialog to the player when no specific dialog is required.<br>
	 * This method checks if the player can start or continue a quest.<br>
	 * It triggers {@code int)} or {@code sendQuestStartDialog} based on the current state.
	 * @param env The environment containing the current player and quest context.
	 * @param template The {@code QuestTemplate} associated with the quest.
	 * @param startNpcId The unique identifier of the NPC that can trigger this dialog.
	 * @param dialogId The specific ID of the dialog to send if the action is a selection.
	 * @return {@code true} if the dialog was successfully sent, otherwise {@code false}.
	 */
	public boolean sendQuestNoneDialog(QuestEnv env, QuestTemplate template, int startNpcId, int dialogId)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
		{
			if (env.getTargetId() == startNpcId)
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT)
				{
					return sendQuestDialog(env, dialogId);
				}
				
				return sendQuestStartDialog(env);
			}
		}
		
		return false;
	}
	
	/**
	 * Sends a quest dialog to the player without any specific conditions.<br>
	 * This method triggers a dialogue sequence involving an NPC and items.
	 * @param env The current quest environment context.
	 * @param startNpcId The unique identifier of the starting NPC.
	 * @param dialogId The ID of the dialog to be displayed.
	 * @param itemId The ID of the item involved in the dialogue.
	 * @param itemCout The count of the item required or given.
	 * @return {@code true} if the dialog was sent successfully, {@code false} otherwise.
	 */
	public boolean sendQuestNoneDialog(QuestEnv env, int startNpcId, int dialogId, int itemId, int itemCout)
	{
		final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		return sendQuestNoneDialog(env, template, startNpcId, dialogId, itemId, itemCout);
	}
	
	/**
	 * Sends a quest dialog that does not require any specific player selection.<br>
	 * This method handles the display of information to the player during a quest step.<br>
	 * It uses the provided NPC and item details to construct the correct message.
	 * @param env The current environment context for the quest.
	 * @param startNpcId The unique identifier of the NPC involved in the dialog.
	 * @param itemId The unique identifier of the item being referenced.
	 * @param itemCout The quantity of the item to be displayed.
	 * @return {@code true} if the dialog was sent successfully, otherwise {@code false}.
	 */
	public boolean sendQuestNoneDialog(QuestEnv env, int startNpcId, int itemId, int itemCout)
	{
		final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		return sendQuestNoneDialog(env, template, startNpcId, 1011, itemId, itemCout);
	}
	
	/**
	 * Sends a quest start dialog to the player when they interact with an NPC.<br>
	 * This method checks if the player is eligible to start the quest and handles item rewards.<br>
	 * It triggers specific actions based on the current {@link DialogAction} state.
	 * @param env The environment containing the current player and interaction data.
	 * @param template The {@code QuestTemplate} associated with the quest.
	 * @param startNpcId The unique ID of the NPC that initiates the quest.
	 * @param dialogId The specific ID of the dialog to display.
	 * @param itemId The ID of the item to give if required.
	 * @param itemCout The quantity of the item to give.
	 * @return {@code true} if the dialog was successfully sent, otherwise {@code false}.
	 */
	public boolean sendQuestNoneDialog(QuestEnv env, QuestTemplate template, int startNpcId, int dialogId, int itemId, int itemCout)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
		{
			if (env.getTargetId() == startNpcId)
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT)
				{
					return sendQuestDialog(env, dialogId);
				}
				
				if ((itemId != 0) && (itemCout != 0))
				{
					if (env.getDialog() == DialogAction.QUEST_ACCEPT_1)
					{
						if (giveQuestItem(env, itemId, itemCout))
						{
							return sendQuestStartDialog(env);
						}
						
						return true;
					}
					
					return sendQuestStartDialog(env);
				}
				
				return sendQuestStartDialog(env);
			}
		}
		
		return false;
	}
	
	/**
	 * Sends the dialog for starting a quest based on the current environment.<br>
	 * It checks the current {@code DialogId} to decide which action to take.<br>
	 * If the user accepts, it starts the quest and moves to the next selection.
	 * @param env The current quest environment context.
	 * @return {@code true} if the dialog was successfully sent, otherwise {@code false}.
	 */
	public boolean sendItemCollectingStartDialog(QuestEnv env)
	{
		switch (env.getDialog())
		{
			case QUEST_ACCEPT_1:
			{
				QuestService.startQuest(env);
				return sendQuestSelectionDialog(env);
			}
			case QUEST_REFUSE_1:
			{
				return sendQuestSelectionDialog(env);
			}
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the unique identifier for this quest.<br>
	 * This value is obtained from the {@link ChallengeQuestTemplate}.
	 * @return The {@code int} ID of the quest.
	 */
	@Override
	public int getQuestId()
	{
		return questId;
	}
	
	/**
	 * Sends a quest action packet to the player.<br>
	 * This method updates the client with the current {@code QuestState}.<br>
	 * It also refreshes the zone and nearby quests if the status is {@code COMPLETE} or {@code REWARD}.
	 * @param env The environment containing the {@link Player} context.
	 */
	private void sendUpdatePacket(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		if ((qs.getStatus() == QuestStatus.COMPLETE) || (qs.getStatus() == QuestStatus.REWARD))
		{
			player.getController().updateZone();
			player.getController().updateNearbyQuests();
		}
	}
	
	/**
	 * Sends a dialog window packet to the player.<br>
	 * This method retrieves the object ID from {@code QuestEnv}.<br>
	 * It then uses {@code sendPacket} to deliver the {@code SM_DIALOG_WINDOW}.
	 * @param env The current quest environment context.
	 * @param dialogId The unique identifier for the dialog to display.
	 */
	private void sendDialogPacket(QuestEnv env, int dialogId)
	{
		int objId = 0;
		if (env.getVisibleObject() != null)
		{
			objId = env.getVisibleObject().getObjectId();
		}
		
		// QuestId is not used because some quests, such as Kromede, handle events after completion and require the questId to be zero.
		PacketSendUtility.sendPacket(env.getPlayer(), new SM_DIALOG_WINDOW(objId, dialogId, env.getQuestId()));
	}
	
	/**
	 * Sends a quest selection packet to the player.<br>
	 * This method uses {@code sendPacket} to deliver an {@code SM_DIALOG_WINDOW}.<br>
	 * It retrieves the object ID from the current environment if it exists.
	 * @param env The current quest environment context.
	 * @param dialogId The unique identifier for the dialog to display.
	 */
	private void sendQuestSelectionPacket(QuestEnv env, int dialogId)
	{
		int objId = 0;
		if (env.getVisibleObject() != null)
		{
			objId = env.getVisibleObject().getObjectId();
		}
		
		PacketSendUtility.sendPacket(env.getPlayer(), new SM_DIALOG_WINDOW(objId, dialogId));
	}
	
	/**
	 * Updates the quest status to the reward state.<br>
	 * This method sets a specific variable value for the player's quest progress.<br>
	 * It then calls {@code updateQuestStatus} to refresh the data.
	 * @param env The current quest environment context.
	 * @param varNumbr The unique identifier of the quest variable to update.
	 * @param varRew The new value to assign to the specified quest variable.
	 */
	public void changeQuestStepRew(QuestEnv env, int varNumbr, int varRew)
	{
		final QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
		qs.setStatus(QuestStatus.REWARD);
		qs.setQuestVarById(varNumbr, varRew);
		updateQuestStatus(env);
	}
	
	/**
	 * @see com.aionemu.gameserver.questEngine.handlers.AbstractQuestHandler#register()
	 */
	@Override
	public abstract void register();
	
	/**
	 * Retrieves the unique identifiers for NPCs associated with this quest.<br>
	 * This method returns a {@code HashSet} of {@code Integer} values.
	 * @return A {@code HashSet} containing the NPC IDs.
	 */
	@Override
	public HashSet<Integer> getNpcIds()
	{
		return null;
	}
}
