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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.AttackManager;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npcshout.NpcShout;
import com.aionemu.gameserver.model.templates.npcshout.ShoutEventType;
import com.aionemu.gameserver.model.templates.npcshout.ShoutType;
import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class handles the logic for NPC shout events within the game world.<br>
 * It processes different {@link ShoutType} actions and manages how NPCs interact with players based on {@link NpcShout} templates.
 * @author Rolandas
 */
public final class ShoutEventHandler
{
	/**
	 * This method is triggered when an NPC sees a target.<br>
	 * It checks if the {@link NpcAI2} owner has any specific shout data for this event.<br>
	 * If shouts exist, they are played using the {@link NpcShoutsService}.
	 * @param npcAI The AI controller of the NPC.
	 * @param target The creature that was seen by the NPC.
	 */
	public static void onSee(NpcAI2 npcAI, Creature target)
	{
		final Npc npc = npcAI.getOwner();
		if (DataManager.NPC_SHOUT_DATA.hasAnyShout(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.SEE))
		{
			final List<NpcShout> shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.SEE, null, 0);
			NpcShoutsService.getInstance().shout(npc, target, shouts, 0, false);
			shouts.clear();
		}
	}
	
	/**
	 * This method is called before an NPC despawns.<br>
	 * It checks if the NPC has any specific shout data for this event.<br>
	 * If shouts exist, it triggers them using {@link NpcShoutsService}.
	 * @param npcAI The {@code NpcAI2} instance of the NPC being processed.
	 */
	public static void onBeforeDespawn(NpcAI2 npcAI)
	{
		final Npc npc = npcAI.getOwner();
		if (DataManager.NPC_SHOUT_DATA.hasAnyShout(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.BEFORE_DESPAWN))
		{
			final List<NpcShout> shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.BEFORE_DESPAWN, null, 0);
			NpcShoutsService.getInstance().shout(npc, null, shouts, 0, false);
			shouts.clear();
		}
	}
	
	/**
	 * This method is called when an NPC reaches a specific walk point.<br>
	 * It checks if the NPC should perform a shout based on its movement.<br>
	 * If a random check passes, it triggers a shout via {@link NpcShoutsService}.
	 * @param npcAI The {@code NpcAI2} instance of the NPC that reached the walk point.
	 */
	public static void onReachedWalkPoint(NpcAI2 npcAI)
	{
		final Npc npc = npcAI.getOwner();
		final WalkerTemplate tp = DataManager.WALKER_DATA.getWalkerTemplate(npc.getSpawn().getWalkerId());
		final int stepCount = tp.getRouteSteps().size();
		final ShoutEventType shoutType = npc.getMoveController().isChangingDirection() ? ShoutEventType.WALK_DIRECTION : ShoutEventType.WALK_WAYPOINT;
		if (DataManager.NPC_SHOUT_DATA.hasAnyShout(npc.getPosition().getMapId(), npc.getNpcId(), shoutType))
		{
			if (Rnd.get(stepCount) < 2)
			{
				final List<NpcShout> shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(npc.getPosition().getMapId(), npc.getNpcId(), shoutType, null, 0);
				if (npc.getTarget() instanceof Creature)
				{
					NpcShoutsService.getInstance().shout(npc, (Creature) npc.getTarget(), shouts, 0, false);
				}
				else
				{
					NpcShoutsService.getInstance().shout(npc, null, shouts, 0, false);
				}
				
				shouts.clear();
			}
		}
	}
	
	/**
	 * This method handles events when an {@link NpcAI2} changes its target.<br>
	 * It checks if the NPC has any specific shouts for switching targets.<br>
	 * If shouts exist, it triggers them using {@link NpcShoutsService}.
	 * @param npcAI The AI instance of the NPC performing the action.
	 * @param creature The new target creature that was switched to.
	 */
	public static void onSwitchedTarget(NpcAI2 npcAI, Creature creature)
	{
		final Npc npc = npcAI.getOwner();
		if (DataManager.NPC_SHOUT_DATA.hasAnyShout(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.SWITCH_TARGET))
		{
			final List<NpcShout> shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.SWITCH_TARGET, null, 0);
			NpcShoutsService.getInstance().shout(npc, creature, shouts, 0, false);
			shouts.clear();
		}
	}
	
	/**
	 * Handles the logic when an NPC dies.<br>
	 * This method checks if there are any specific shouts for the {@code NpcAI2} owner.<br>
	 * If shouts exist, it triggers them via {@link NpcShoutsService}.
	 * @param npcAI The AI instance of the NPC that died.
	 */
	public static void onDied(NpcAI2 npcAI)
	{
		final Npc owner = npcAI.getOwner();
		if (DataManager.NPC_SHOUT_DATA.hasAnyShout(owner.getPosition().getMapId(), owner.getNpcId(), ShoutEventType.DIED))
		{
			final List<NpcShout> shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(owner.getPosition().getMapId(), owner.getNpcId(), ShoutEventType.DIED, null, 0);
			if (shouts.size() > 0)
			{
				NpcShoutsService.getInstance().shout(owner, (Creature) owner.getTarget(), shouts, 0, false);
			}
			
			shouts.clear();
		}
	}
	
	// TODO: Figure out what the difference between ATTACK_BEGIN and HELP; HELPCALL should make NPC run
	
	/**
	 * This method is triggered when an NPC starts attacking a target.<br>
	 * It checks if the {@link NpcAI2} owner has any specific shouts for this event.<br>
	 * If shouts exist, they are played using the {@link NpcShoutsService}.
	 * @param npcAI The AI instance of the NPC performing the action.
	 * @param creature The target creature being attacked.
	 */
	public static void onAttackBegin(NpcAI2 npcAI, Creature creature)
	{
		final Npc npc = npcAI.getOwner();
		if (DataManager.NPC_SHOUT_DATA.hasAnyShout(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.ATTACK_BEGIN))
		{
			final List<NpcShout> shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.ATTACK_BEGIN, null, 0);
			NpcShoutsService.getInstance().shout(npc, creature, shouts, 0, false);
			shouts.clear();
			return;
		}
	}
	
	/**
	 * Handles the logic for an NPC calling for help.<br>
	 * This method checks if the {@code NpcAI2} owner has any shouts defined for being attacked or requesting help.<br>
	 * If valid shouts exist, they are triggered via {@link NpcShoutsService}.
	 * @param npcAI The AI instance of the NPC performing the action.
	 * @param creature The target creature that triggered this event.
	 */
	public static void onHelp(NpcAI2 npcAI, Creature creature)
	{
		// TODO: [RR] change AI or randomise behaviour for "cowards" and "fanatics" ???
		final Npc npc = npcAI.getOwner();
		if (npc.getAttackedCount() == 0)
		{
			if (DataManager.NPC_SHOUT_DATA.hasAnyShout(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.ATTACKED))
			{
				final List<NpcShout> shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.ATTACKED, null, 0);
				NpcShoutsService.getInstance().shout(npc, creature, shouts, 0, false);
				shouts.clear();
				return;
			}
			
			if (DataManager.NPC_SHOUT_DATA.hasAnyShout(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.HELPCALL))
			{
				final List<NpcShout> shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.HELPCALL, null, 0);
				NpcShoutsService.getInstance().shout(npc, creature, shouts, 0, false);
				shouts.clear();
			}
		}
	}
	
	/**
	 * Handles the logic when an NPC is attacked by an enemy.<br>
	 * This method checks for available shouts and broadcasts them to nearby players.<br>
	 * It uses {@link NpcShoutsService} to send the messages.
	 * @param npcAI The AI instance of the NPC being attacked.
	 * @param target The creature that initiated the attack.
	 */
	public static void onEnemyAttack(NpcAI2 npcAI, Creature target)
	{
		final Npc npc = npcAI.getOwner();
		if (!DataManager.NPC_SHOUT_DATA.hasAnyShout(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.ATTACKED))
		{
			return;
		}
		
		final List<NpcShout> shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.ATTACKED, null, 0);
		
		final List<NpcShout> finalShouts = new ArrayList<>();
		for (NpcShout s : shouts)
		{
			if (s.getShoutType() == ShoutType.SAY)
			{
				finalShouts.add(s);
			}
		}
		
		if (finalShouts.size() == 0)
		{
			return;
		}
		
		final int randomShout = Rnd.get(finalShouts.size());
		final NpcShout shout = finalShouts.get(randomShout);
		finalShouts.clear();
		shouts.clear();
		
		if (!npc.mayShout(shout.getPollDelay() / 1000))
		{
			return;
		}
		
		ThreadPoolManager.getInstance().schedule(() ->
		{
			final Iterator<Player> iter = npc.getKnownList().getKnownPlayers().values().iterator();
			while (iter.hasNext())
			{
				final Player kObj = iter.next();
				if (kObj.getLifeStats().isAlreadyDead())
				{
					return;
				}
				
				NpcShoutsService.getInstance().shout(npc, kObj, shout, shout.getPollDelay() / 1000);
			}
		}, 0);
	}
	
	/**
	 * Handles the logic when an {@link NpcAI2} casts a spell.<br>
	 * This method triggers specific events related to casting actions.
	 * @param npcAI The AI instance of the NPC performing the action.
	 * @param creature The target creature involved in the cast.
	 */
	public static void onCast(NpcAI2 npcAI, Creature creature)
	{
		handleNumericEvent(npcAI, creature, ShoutEventType.CAST_K);
	}
	
	/**
	 * Handles the logic when an NPC starts attacking a target.<br>
	 * This method validates the {@code creature} and updates the AI state.<br>
	 * It stops walking behaviors and initiates the attack sequence via {@link AttackManager}.
	 * @param npcAI The {@link NpcAI2} instance performing the action.
	 * @param creature The {@link Creature} being attacked.
	 */
	public static void onAttack(NpcAI2 npcAI, Creature creature)
	{
		handleNumericEvent(npcAI, creature, ShoutEventType.ATTACK_K);
	}
	
	/**
	 * Processes numeric events for an NPC to trigger specific shouts.<br>
	 * This method filters {@link NpcShout} data based on the owner's skill number.<br>
	 * It selects valid shouts and triggers them via {@link NpcShoutsService}.
	 * @param npcAI The AI instance of the NPC performing the action.
	 * @param creature The target creature involved in the event.
	 * @param eventType The type of shout event that occurred.
	 */
	private static void handleNumericEvent(NpcAI2 npcAI, Creature creature, ShoutEventType eventType)
	{
		final Npc owner = npcAI.getOwner();
		final List<NpcShout> shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(owner.getPosition().getMapId(), owner.getNpcId(), eventType, null, 0);
		if (shouts == null)
		{
			return;
		}
		
		List<NpcShout> validShouts = new ArrayList<>();
		final List<NpcShout> nonNumberedShouts = new ArrayList<>();
		for (NpcShout shout : shouts)
		{
			if (shout.getSkillNo() == 0)
			{
				nonNumberedShouts.add(shout);
			}
			else if (shout.getSkillNo() == owner.getSkillNumber())
			{
				validShouts.add(shout);
			}
		}
		
		if (validShouts.size() == 0)
		{
			validShouts.clear();
			validShouts = nonNumberedShouts;
		}
		else
		{
			nonNumberedShouts.clear();
		}
		
		if (validShouts.size() > 0)
		{
			NpcShoutsService.getInstance().shout(owner, creature, validShouts, 0, false);
		}
		
		validShouts.clear();
		shouts.clear();
	}
	
	/**
	 * This method is called when an NPC finishes an attack.<br>
	 * It checks if the {@link Npc} has any specific shouts assigned for this event.<br>
	 * If shouts exist, it triggers them using the {@link NpcShoutsService}.
	 * @param npcAI The {@code NpcAI2} instance of the NPC that finished attacking.
	 */
	public static void onAttackEnd(NpcAI2 npcAI)
	{
		final Npc npc = npcAI.getOwner();
		if (DataManager.NPC_SHOUT_DATA.hasAnyShout(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.ATTACK_END))
		{
			final List<NpcShout> shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(npc.getPosition().getMapId(), npc.getNpcId(), ShoutEventType.ATTACK_END, null, 0);
			NpcShoutsService.getInstance().shout(npc, null, shouts, 0, false);
			shouts.clear();
		}
	}
}
