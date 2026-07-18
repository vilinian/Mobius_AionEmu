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

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.rewards.BonusType;
import com.aionemu.gameserver.questEngine.model.QuestActionType;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * This class serves as a base for all specific quest logic.<br>
 * It provides the structure that concrete quest handlers must override to define unique behaviors.
 * @author vlog
 */
public abstract class AbstractQuestHandler
{
	public abstract void register();
	
	/**
	 * Handles events triggered by dialog interactions.<br>
	 * This method is called when a player interacts with a dialog.<br>
	 * It allows for custom logic to be executed during these events.
	 * @param questEnv The environment context containing quest data.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	public boolean onDialogEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	/**
	 * This method is called when a player enters a world event.<br>
	 * It allows for custom logic to be executed during this specific trigger.
	 * @param questEnv The environment context containing quest data.
	 * @return {@code true} if the event was handled, or {@code false} otherwise.
	 */
	public boolean onEnterWorldEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	/**
	 * This method is called when a player enters a specific zone.<br>
	 * It allows for custom logic to be triggered based on the location.
	 * @param questEnv The environment context of the current quest.
	 * @param zoneName The name of the zone the player entered.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	public boolean onEnterZoneEvent(QuestEnv questEnv, ZoneName zoneName)
	{
		return false;
	}
	
	/**
	 * This method is called when a player leaves a specific zone.<br>
	 * It allows for custom logic to trigger upon exiting an area.
	 * @param questEnv The environment context of the current quest.
	 * @param zoneName The name of the zone being exited.
	 * @return {@code true} if the event was handled, or {@code false} otherwise.
	 */
	public boolean onLeaveZoneEvent(QuestEnv questEnv, ZoneName zoneName)
	{
		return false;
	}
	
	/**
	 * Handles the logic when a player uses an {@code Item}.<br>
	 * This method is triggered by the quest engine to check for specific item interactions.
	 * @param questEnv The current environment context of the quest.
	 * @param item The {@code Item} object that was used.
	 * @return A {@code HandlerResult} indicating the outcome of the action.
	 */
	public HandlerResult onItemUseEvent(QuestEnv questEnv, Item item)
	{
		return HandlerResult.UNKNOWN;
	}
	
	/**
	 * Handles events triggered when a player uses an item inside a house.<br>
	 * This method is called by the quest engine to check for specific interactions.
	 * @param env The {@code QuestEnv} object containing current quest data.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	public boolean onHouseItemUseEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * This method is called when a player receives an item.<br>
	 * It allows for custom logic to trigger during this event.
	 * @param questEnv The environment context containing quest data.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	public boolean onGetItemEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	/**
	 * Handles the event when a player uses a specific skill.<br>
	 * This method is triggered by the {@link QuestEnv}.
	 * @param questEnv The environment context for the current quest.
	 * @param skillId The unique identifier of the skill being used.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	public boolean onUseSkillEvent(QuestEnv questEnv, int skillId)
	{
		return false;
	}
	
	/**
	 * This method is called when a kill event occurs during a quest.<br>
	 * It allows you to define custom logic for monster deaths.
	 * @param questEnv The environment data for the current quest.
	 * @return {@code true} if the event was handled, or {@code false} otherwise.
	 */
	public boolean onKillEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	/**
	 * Handles logic when a player performs an attack action.<br>
	 * This method is triggered by the {@link AbstractQuestHandler} system.
	 * @param questEnv The environment context for the current quest.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	public boolean onAttackEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	/**
	 * Handles the logic when a player levels up during a quest.<br>
	 * This method is called by the {@link com.aionemu.gameserver.questEngine.model.QuestEnv} system.
	 * @param questEnv The environment containing current quest data.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	public boolean onLvlUpEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	/**
	 * This method is called when a zone mission ends.<br>
	 * It allows for custom logic to execute at this specific moment.
	 * @param env The {@code QuestEnv} object containing the current quest context.
	 * @return Returns {@code true} if the event was handled, or {@code false} otherwise.
	 */
	public boolean onZoneMissionEndEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * This method is triggered when a player dies during a quest.<br>
	 * It allows for custom logic to execute at the moment of death.
	 * @param questEnv The environment context containing current quest data.
	 * @return {@code true} if the event was handled, or {@code false} otherwise.
	 */
	public boolean onDieEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	/**
	 * Handles the logic when a player logs out.<br>
	 * This method checks if the quest progress can be updated.<br>
	 * It updates {@code QuestState} variables based on specific conditions.
	 * @param env The {@link QuestEnv} containing the current quest environment.
	 * @return {@code true} if the logout event was handled successfully, otherwise {@code false}.
	 */
	public boolean onLogOutEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * This method is triggered when an NPC reaches its target.<br>
	 * It allows for custom logic to execute at this specific moment.
	 * @param env The {@code QuestEnv} object containing the current quest context.
	 * @return Returns {@code false} by default unless overridden.
	 */
	public boolean onNpcReachTargetEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * This method is triggered when an {@code NPC} loses its target.<br>
	 * It allows for custom logic to execute during this specific quest event.
	 * @param env The {@link QuestEnv} object containing the current quest context.
	 * @return Returns {@code false} by default.
	 */
	public boolean onNpcLostTargetEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * Handles the logic that occurs when a movie finishes playing.<br>
	 * This method is triggered by the quest engine.
	 * @param questEnv The environment context for the current quest.
	 * @param movieId The unique identifier of the movie that just ended.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	public boolean onMovieEndEvent(QuestEnv questEnv, int movieId)
	{
		return false;
	}
	
	/**
	 * This method is called when a quest timer expires.<br>
	 * It allows for custom logic to execute at the end of a timed event.
	 * @param questEnv The environment context containing quest data.
	 * @return {@code true} if the event was handled, or {@code false} otherwise.
	 */
	public boolean onQuestTimerEndEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	/**
	 * This method is called when an invisible timer finishes.<br>
	 * It allows for custom logic to execute at this specific moment.
	 * @param questEnv The environment context of the current quest.
	 * @return {@code false} by default, or {@code true} if the event was handled.
	 */
	public boolean onInvisibleTimerEndEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	/**
	 * Handles the event when a player uses a flying ring.<br>
	 * This method is triggered by the quest engine to check for specific ring interactions.
	 * @param questEnv The current environment context of the quest.
	 * @param flyingRing The unique identifier or name of the flying ring used.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	public boolean onPassFlyingRingEvent(QuestEnv questEnv, String flyingRing)
	{
		return false;
	}
	
	/**
	 * Handles the event when a player kills a ranked monster.<br>
	 * This method is called by the {@link AbstractQuestHandler}.
	 * @param env The current quest environment context.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	public boolean onKillRankedEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * This method is triggered when a player kills an enemy in the world.<br>
	 * It allows for custom logic to be executed during this specific event.
	 * @param env The {@code QuestEnv} object containing current quest data.
	 * @return Returns {@code false} by default unless overridden.
	 */
	public boolean onKillInWorldEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * This method is called when a crafting attempt fails.<br>
	 * It allows you to handle custom logic for failed crafts.
	 * @param env The current quest environment context.
	 * @param itemId The unique identifier of the item being crafted.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	public boolean onFailCraftEvent(QuestEnv env, int itemId)
	{
		return false;
	}
	
	/**
	 * This method is called when a player equips an item.<br>
	 * It allows for custom logic to trigger based on the {@code itemId}.
	 * @param env The current quest environment context.
	 * @param itemId The unique identifier of the item being equipped.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	public boolean onEquipItemEvent(QuestEnv env, int itemId)
	{
		return false;
	}
	
	/**
	 * Checks if the player can perform a specific quest action.<br>
	 * This method verifies that the quest is currently in the {@code START} status.
	 * @param env The current quest environment containing player and quest data.
	 * @param questEventType The type of quest event being triggered.
	 * @param objects Variable arguments for additional context needed by the action.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	public boolean onCanAct(QuestEnv env, QuestActionType questEventType, Object... objects)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(env.getQuestId());
		return (qs != null) && (qs.getStatus() == QuestStatus.START);
	}
	
	/**
	 * Handles the event when an entity is added to an aggro list.<br>
	 * This method allows for custom logic during quest progression.
	 * @param questEnv The environment context containing quest data.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	public boolean onAddAggroListEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	/**
	 * Handles events triggered when a player reaches a specific distance.<br>
	 * This method is called by the quest engine to check for proximity conditions.
	 * @param questEnv The environment context containing current quest data.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	public boolean onAtDistanceEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	/**
	 * Handles the event when a player enters a specific world stream.<br>
	 * This method is triggered by the {@link AbstractQuestHandler}.
	 * @param questEnv The environment context for the current quest.
	 * @param worldId The unique identifier of the world stream.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	public boolean onEnterWindStreamEvent(QuestEnv questEnv, int worldId)
	{
		return false;
	}
	
	/**
	 * Checks if a player can perform a riding action.<br>
	 * This method validates the ride request based on the provided environment and item ID.
	 * @param questEnv The current quest environment context.
	 * @param rideItemId The unique identifier of the mount or ride item.
	 * @return {@code true} if the action is successful, otherwise {@code false}.
	 */
	public boolean rideAction(QuestEnv questEnv, int rideItemId)
	{
		return false;
	}
	
	/**
	 * Handles the reward event for a Dredgion quest.<br>
	 * This method is triggered when a player receives rewards from a Dredgion.
	 * @param env The {@code QuestEnv} containing the current quest environment data.
	 * @return Returns {@code true} if the event was handled, or {@code false} otherwise.
	 */
	public boolean onDredgionRewardEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * Handles the event when a player receives a reward from a quest.<br>
	 * This method is called by the {@link com.aionemu.gameserver.questEngine.model.QuestEnv} system.
	 * @param env The environment context containing quest data.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	public boolean onKamarRewardEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * Handles the event when a player receives an Ophidan reward.<br>
	 * This method is called by the {@link com.aionemu.gameserver.questEngine.model.QuestEnv} system.
	 * @param env The current quest environment context.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	public boolean onOphidanRewardEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * Handles the event when a player receives a reward from a bastion.<br>
	 * This method is called by the quest engine to check for specific logic.
	 * @param env The {@code QuestEnv} object containing current quest data.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	public boolean onBastionRewardEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * Handles events triggered when a bonus is applied to a player.<br>
	 * This method searches for active quests associated with the given {@code BonusType}.<br>
	 * It executes the specific logic defined in the matching quest handler.
	 * @param env The current quest environment context.
	 * @param bonusType The type of bonus being applied.
	 * @param rewardItems The list of items included in the reward.
	 * @return A {@code HandlerResult} indicating if the event was processed successfully.
	 */
	public HandlerResult onBonusApplyEvent(QuestEnv env, BonusType bonusType, List<QuestItems> rewardItems)
	{
		return HandlerResult.UNKNOWN;
	}
	
	/**
	 * Handles the logic when a protection period ends.<br>
	 * This method is triggered by the quest engine to check for specific actions.
	 * @param env The {@code QuestEnv} object containing current quest data.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	public boolean onProtectEndEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * This method is called when a protection fails during a quest.<br>
	 * It allows for custom logic to be executed in this specific scenario.
	 * @param env The {@code QuestEnv} object containing the current quest context.
	 * @return Returns {@code false} by default to indicate the event was not handled.
	 */
	public boolean onProtectFailEvent(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * This method handles events related to creativity points.<br>
	 * It is called by the {@link com.aionemu.gameserver.questEngine.model.QuestEnv} system.<br>
	 * You can override this method to add custom logic for these specific events.
	 * @param questEnv The environment context containing current quest data.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	public boolean onCreativityPointEvent(QuestEnv questEnv)
	{
		return false;
	}
}
