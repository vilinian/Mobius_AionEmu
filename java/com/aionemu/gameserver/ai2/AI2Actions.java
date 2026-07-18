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
package com.aionemu.gameserver.ai2;

import java.util.Collection;

import com.aionemu.gameserver.controllers.observer.DialogObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class contains a collection of common actions for the {@link com.aionemu.gameserver.ai2.AI2} system.<br>
 * These methods provide convenient access to logic that interacts with the owner's data and state.
 * @author ATracer
 */
public class AI2Actions
{
	/**
	 * This method removes the owner of an {@link AbstractAI} instance.<br>
	 * It triggers the {@code onDelete()} action on the owner's controller.<br>
	 * Use this to clean up resources when an AI is destroyed.
	 * @param ai2 The {@link AbstractAI} object containing the owner to be deleted.
	 */
	public static void deleteOwner(AbstractAI ai2)
	{
		ai2.getOwner().getController().onDelete();
	}
	
	/**
	 * This method kills a {@code Creature} without triggering standard death logic.<br>
	 * It notifies the target's controller that it has died by the owner of {@code ai2}.<br>
	 * Use this when you need to remove an entity quietly.
	 * @param ai2 The {@link AbstractAI} instance whose owner is performing the action.
	 * @param target The {@link Creature} to be killed.
	 */
	public static void killSilently(AbstractAI ai2, Creature target)
	{
		target.getController().onDie(ai2.getOwner());
	}
	
	/**
	 * This method handles the death of an owner without triggering standard death logic.<br>
	 * It notifies the controller that the owner has died by a specific attacker.<br>
	 * Use this when you need to trigger death events silently.
	 * @param ai2 The {@link AbstractAI} instance associated with the owner.
	 * @param attacker The {@link Creature} that caused the death.
	 */
	public static void dieSilently(AbstractAI ai2, Creature attacker)
	{
		ai2.getOwner().getController().onDie(attacker);
	}
	
	/**
	 * Makes the owner of an {@link AbstractAI} use a specific skill.<br>
	 * This method retrieves the controller from the owner to execute the action.
	 * @param ai2 The AI instance that owns the character.
	 * @param skillId The unique identifier for the skill to be used.
	 */
	public static void useSkill(AbstractAI ai2, int skillId)
	{
		ai2.getOwner().getController().useSkill(skillId);
	}
	
	/**
	 * Applies a specific skill effect to a target creature.<br>
	 * This method creates a new {@link Effect} based on the provided {@code template}.<br>
	 * It forces the effect to be applied immediately.
	 * @param ai2 The {@link AbstractAI} instance performing the action.
	 * @param template The {@link SkillTemplate} containing the skill data.
	 * @param target The {@link Creature} that will receive the effect.
	 */
	public static void applyEffect(AbstractAI ai2, SkillTemplate template, Creature target)
	{
		final Effect effect = new Effect(ai2.getOwner(), target, template, template.getLvl(), 0);
		effect.setIsForcedEffect(true);
		effect.initialize();
		effect.applyEffect();
	}
	
	/**
	 * Applies a skill effect to the owner of the {@link AbstractAI}.<br>
	 * This method retrieves the template using the provided {@code skillId}.<br>
	 * It then initializes and applies the effect to the owner.
	 * @param ai2 The AI instance that owns the creature receiving the effect.
	 * @param skillId The unique identifier for the skill to be applied.
	 */
	public static void applyEffectSelf(AbstractAI ai2, int skillId)
	{
		final SkillTemplate st = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		final Effect effect = new Effect(ai2.getOwner(), ai2.getOwner(), st, 1, st.getEffectsDuration(skillId));
		effect.initialize();
		effect.applyEffect();
	}
	
	/**
	 * Makes the owner of the {@code AbstractAI} target itself.<br>
	 * This method updates the target to be the owner object.
	 * @param ai2 The {@code AbstractAI} instance to process.
	 */
	public static void targetSelf(AbstractAI ai2)
	{
		ai2.getOwner().setTarget(ai2.getOwner());
	}
	
	/**
	 * Sets a specific target for the owner of an {@link AbstractAI}.<br>
	 * This method updates the target of the creature associated with the AI.
	 * @param ai2 The AI instance to use.
	 * @param target The {@link Creature} to be set as the new target.
	 */
	public static void targetCreature(AbstractAI ai2, Creature target)
	{
		ai2.getOwner().setTarget(target);
	}
	
	/**
	 * Handles the logic after a player finishes using an item on an NPC.<br>
	 * This method determines if the location is an instance or a world map.<br>
	 * It then calls the appropriate {@code WorldHandler} to process the action.
	 * @param ai2 The {@link AbstractAI} object representing the NPC.
	 * @param player The {@link Player} who used the item.
	 */
	public static void handleUseItemFinish(AbstractAI ai2, Player player)
	{
		if (ai2.getPosition().isInstanceMap())
		{
			ai2.getPosition().getWorldMapInstance().getInstanceHandler().handleUseItemFinish(player, (Npc) ai2.getOwner());
		}
		else
		{
			ai2.getPosition().getWorld().getWorldMap(ai2.getPosition().getMapId()).getWorldHandler().handleUseItemFinish(player, (Npc) ai2.getOwner());
		}
	}
	
	/**
	 * Triggers a specific event for an individual {@link Npc}.<br>
	 * This method notifies the target NPC about an action performed by the owner of the {@code ai2} object.
	 * @param ai2 The AI instance associated with the actor performing the action.
	 * @param target The NPC that will receive the event notification.
	 */
	public static void fireIndividualEvent(AbstractAI ai2, Npc target)
	{
		target.getAi2().onIndividualNpcEvent(ai2.getOwner());
	}
	
	/**
	 * Triggers a death event for an {@link Npc} within an instance.<br>
	 * This method notifies the {@code InstanceHandler} that the owner of {@code ai2} has died.<br>
	 * It is used to handle specific logic when an NPC is killed in an instanced area.
	 * @param ai2 The {@code AbstractAI} object associated with the NPC.
	 * @param player The {@link Player} who performed the action.
	 */
	public static void fireNpcKillInstanceEvent(AbstractAI ai2, Player player)
	{
		ai2.getPosition().getWorldMapInstance().getInstanceHandler().onDie((Npc) ai2.getOwner());
	}
	
	/**
	 * Registers a drop for a specific player.<br>
	 * This method links the {@code Player} to an item drop from an {@link AbstractAI}.<br>
	 * It updates the {@code DropRegistrationService} with the provided list of players.
	 * @param ai2 The AI instance associated with the NPC owner.
	 * @param player The player who will receive or interact with the drop.
	 * @param registeredPlayers A collection of all players currently registered for this drop.
	 */
	public static void registerDrop(AbstractAI ai2, Player player, Collection<Player> registeredPlayers)
	{
		DropRegistrationService.getInstance().registerDrop((Npc) ai2.getOwner(), player, registeredPlayers);
	}
	
	/**
	 * Schedules a respawn for the owner of the {@code NpcAI2}.<br>
	 * This method calls the {@code scheduleRespawn()} method on the owner's controller.
	 * @param ai2 The {@code NpcAI2} instance to process.
	 */
	public static void scheduleRespawn(NpcAI2 ai2)
	{
		ai2.getOwner().getController().scheduleRespawn();
	}
	
	/**
	 * This method handles the selection of a specific dialog option.<br>
	 * It creates a {@code QuestEnv} and processes it through the {@link QuestEngine}.
	 * @param ai2 The {@code AbstractAI} instance owning the action.
	 * @param player The {@code Player} who is interacting with the NPC.
	 * @param questId The unique identifier for the current quest.
	 * @param dialogId The specific ID of the dialog being selected.
	 * @return A {@code SelectDialogResult} containing the success status and environment.
	 */
	public static SelectDialogResult selectDialog(AbstractAI ai2, Player player, int questId, int dialogId)
	{
		final QuestEnv env = new QuestEnv(ai2.getOwner(), player, questId, dialogId);
		final boolean result = QuestEngine.getInstance().onDialog(env);
		return new SelectDialogResult(result, env);
	}
	
	public static final class SelectDialogResult
	{
		private final boolean success;
		private final QuestEnv env;
		
		private SelectDialogResult(boolean success, QuestEnv env)
		{
			this.success = success;
			this.env = env;
		}
		
		public boolean isSuccess()
		{
			return success;
		}
		
		public QuestEnv getEnv()
		{
			return env;
		}
	}
	
	/**
	 * Adds a new request to the {@link AbstractAI} instance.<br>
	 * This method links a specific {@code AI2Request} to a {@link Player}.<br>
	 * It uses the unique object ID of the AI for identification.
	 * @param ai2 The {@link AbstractAI} instance performing the action.
	 * @param player The {@link Player} who initiated the request.
	 * @param requestId The unique identifier for the request.
	 * @param request The {@code AI2Request} object to be added.
	 * @param requestParams Optional additional parameters for the request.
	 */
	public static void addRequest(AbstractAI ai2, Player player, int requestId, AI2Request request, Object... requestParams)
	{
		addRequest(ai2, player, requestId, ai2.getObjectId(), request, requestParams);
	}
	
	/**
	 * Registers a new request for a specific player.<br>
	 * This method handles the logic for sending a question window to the user.<br>
	 * It also sets up an observer if a specific range is provided.
	 * @param ai2 The {@link AbstractAI} instance associated with the owner.
	 * @param player The {@link Player} who will receive the request.
	 * @param requestId The unique identifier for the request.
	 * @param senderId The ID of the entity sending the request.
	 * @param range The distance limit for the interaction.
	 * @param request The {@link AI2Request} object containing the logic to execute.
	 * @param requestParams Variable arguments passed with the request.
	 */
	public static void addRequest(AbstractAI ai2, Player player, int requestId, int senderId, int range, AI2Request request, Object... requestParams)
	{
		final boolean requested = player.getResponseRequester().putRequest(requestId, new RequestResponseHandler(ai2.getOwner())
		{
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				request.denyRequest(requester, responder);
			}
			
			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				request.acceptRequest(requester, responder);
			}
		});
		
		if (requested)
		{
			if (range > 0)
			{
				player.getObserveController().addObserver(new DialogObserver(ai2.getOwner(), player, range)
				{
					
					@Override
					public void tooFar(Creature requester, Player responder)
					{
						request.denyRequest(requester, responder);
					}
				});
			}
			
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(requestId, senderId, range, requestParams));
		}
	}
	
	/**
	 * Adds a new request to the {@link AbstractAI} system.<br>
	 * This method registers a specific {@code AI2Request} for processing.<br>
	 * It links the request to a {@link Player} and an owner.
	 * @param ai2 The {@link AbstractAI} instance that owns the request.
	 * @param player The {@link Player} associated with this request.
	 * @param requestId The unique identifier for the request.
	 * @param senderId The ID of the entity sending the request.
	 * @param request The {@code AI2Request} object to be added.
	 * @param requestParams Variable arguments providing extra data for the request.
	 */
	public static void addRequest(AbstractAI ai2, Player player, int requestId, int senderId, AI2Request request, Object... requestParams)
	{
		addRequest(ai2, player, requestId, senderId, 0, request, requestParams);
	}
}
