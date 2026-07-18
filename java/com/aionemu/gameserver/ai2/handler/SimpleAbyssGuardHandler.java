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
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * Handles the basic AI behavior for Abyss Guard NPCs.<br>
 * This class manages how these guards react to players and other entities in the game world.
 * @author Rolandas
 */
public class SimpleAbyssGuardHandler
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
		if (!(creature instanceof Npc))
		{
			CreatureEventHandler.checkAggro(ai, creature);
			return;
		}
		
		final Npc owner = ai.getOwner();
		if (creature.getLifeStats().isAlreadyDead() || !owner.canSee(creature))
		{
			return;
		}
		
		final Npc npc = ((Npc) creature);
		
		// Creatures which are under attack not handled
		if (!npc.isEnemy(creature) || (npc.getLevel() < 2) || (creature.getTarget() != null) || !owner.getActiveRegion().isMapRegionActive())
		{
			return;
		}
		
		if (!ai.isInState(AIState.FIGHT) && (MathUtil.isIn3dRange(owner, creature, owner.getAggroRange())))
		{
			if (GeoService.getInstance().canSee(owner, creature))
			{
				if (!ai.isInState(AIState.RETURNING))
				{
					ai.getOwner().getMoveController().storeStep();
				}
				
				ai.onCreatureEvent(AIEventType.CREATURE_AGGRO, creature);
			}
		}
	}
}
