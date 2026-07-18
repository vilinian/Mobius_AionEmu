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
package com.aionemu.gameserver.ai2.handler;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplateType;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.TribeRelationService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * This class handles various events related to {@link Creature} entities within the AI system.<br>
 * It processes logic for {@link Npc}, {@link Player}, and other creature types based on their current state.
 * @author ATracer
 */
public class CreatureEventHandler
{
	/**
	 * This method is called when a {@link Creature} moves.<br>
	 * It triggers an aggression check for the {@code npcAI}.<br>
	 * If the creature is a {@link Player}, it updates the quest distance.
	 * @param npcAI The AI instance of the NPC.
	 * @param creature The creature that moved.
	 */
	public static void onCreatureMoved(NpcAI2 npcAI, Creature creature)
	{
		checkAggro(npcAI, creature);
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			QuestEngine.getInstance().onAtDistance(new QuestEnv(npcAI.getOwner(), player, 0, 0));
		}
	}
	
	/**
	 * This method is called when an {@link NpcAI2} sees a {@link Creature}.<br>
	 * It checks if the creature should become aggressive.<br>
	 * It also triggers distance events for players via the {@link QuestEngine}.
	 * @param npcAI The AI instance of the NPC that spotted the creature.
	 * @param creature The creature that was seen by the NPC.
	 */
	public static void onCreatureSee(NpcAI2 npcAI, Creature creature)
	{
		checkAggro(npcAI, creature);
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			QuestEngine.getInstance().onAtDistance(new QuestEnv(npcAI.getOwner(), player, 0, 0));
		}
	}
	
	/**
	 * Checks if an {@link Creature} should trigger an aggressive response from the NPC.<br>
	 * This method validates distance, visibility, and tribe relations.<br>
	 * It triggers a {@code CREATURE_AGGRO} event if all conditions are met.
	 * @param ai The {@link NpcAI2} instance handling the logic.
	 * @param creature The {@link Creature} being checked for aggression.
	 */
	protected static void checkAggro(NpcAI2 ai, Creature creature)
	{
		final Npc owner = ai.getOwner();
		
		if (ai.isInState(AIState.FIGHT) || creature.getLifeStats().isAlreadyDead() || !owner.canSee(creature) || !owner.getActiveRegion().isMapRegionActive())
		{
			return;
		}
		
		boolean isInAggroRange = false;
		
		if (ai.poll(AIQuestion.CAN_SHOUT))
		{
			final int shoutRange = owner.getObjectTemplate().getMinimumShoutRange();
			final double distance = MathUtil.getDistance(owner, creature);
			if (distance <= shoutRange)
			{
				ShoutEventHandler.onSee(ai, creature);
				isInAggroRange = shoutRange <= owner.getAggroRange();
			}
		}
		
		if (!ai.isInState(AIState.FIGHT) && (isInAggroRange || MathUtil.isIn3dRange(owner, creature, owner.getAggroRange())))
		{
			if (checkAggroRelation(owner, creature) && GeoService.getInstance().canSee(owner, creature))
			{
				if (!ai.isInState(AIState.RETURNING))
				{
					ai.getOwner().getMoveController().storeStep();
				}
				
				if (ai.canThink())
				{
					ai.onCreatureEvent(AIEventType.CREATURE_AGGRO, creature);
				}
			}
		}
	}
	
	/**
	 * Determines if an {@link Npc} should attack a specific {@link Creature}.<br>
	 * It checks the tribe relations and level differences between both entities.<br>
	 * Special rules apply for {@code ABYSS_GUARD} types.
	 * @param owner The {@link Npc} that owns the AI logic.
	 * @param creature The {@link Creature} being evaluated for aggression.
	 * @return {@code true} if the {@link Npc} should be aggressive, otherwise {@code false}.
	 */
	private static boolean checkAggroRelation(Npc owner, Creature creature)
	{
		if (TribeRelationService.isAggressive(owner, creature))
		{
			if (((creature.getLevel() - owner.getLevel()) <= 10) || (owner.getObjectTemplate().getNpcTemplateType() == NpcTemplateType.ABYSS_GUARD))
			{
				return true;
			}
		}
		
		return false;
	}
}
