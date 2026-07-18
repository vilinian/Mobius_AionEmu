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

import java.util.Collections;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplateType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.services.TribeRelationService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * This class handles the logic for NPC aggression and target acquisition.<br>
 * It processes {@link AIEventType} events to determine if an {@link Npc} should attack a {@link Player} or other creatures.<br>
 * It manages how entities become hostile based on proximity, tribe relations, and specific NPC templates.
 * @author ATracer
 */
public class AggroEventHandler
{
	/**
	 * Handles the logic when an NPC becomes aggressive toward a target.<br>
	 * It checks if the target is a friend or neutral before reacting.<br>
	 * If valid, it broadcasts an attack packet and schedules a notification.
	 * @param npcAI The {@link NpcAI2} instance of the NPC.
	 * @param myTarget The {@link Creature} that the NPC is targeting.
	 */
	public static void onAggro(NpcAI2 npcAI, Creature myTarget)
	{
		final Npc owner = npcAI.getOwner();
		
		// TODO move out?
		if ((myTarget.getAdminNeutral() == 1) || (myTarget.getAdminNeutral() == 3) || (myTarget.getAdminEnmity() == 1) || (myTarget.getAdminEnmity() == 3) || TribeRelationService.isFriend(owner, myTarget))
		{
			return;
		}
		
		PacketSendUtility.broadcastPacket(owner, new SM_ATTACK(owner, myTarget, 0, 633, 0, Collections.singletonList(new AttackResult(0, AttackStatus.NORMALHIT))));
		
		ThreadPoolManager.getInstance().schedule(new AggroNotifier(owner, myTarget, true), 500);
	}
	
	/**
	 * Checks if a creature requires support from an NPC.<br>
	 * This method verifies if the creature is a friend and within range.<br>
	 * It also checks if the creature has a valid target to attack.
	 * @param npcAI The {@link NpcAI2} instance of the owner.
	 * @param notMyTarget The {@link Creature} being checked for support needs.
	 * @return {@code true} if the NPC should provide support, otherwise {@code false}.
	 */
	public static boolean onCreatureNeedsSupport(NpcAI2 npcAI, Creature notMyTarget)
	{
		final Npc owner = npcAI.getOwner();
		if (TribeRelationService.isSupport(notMyTarget, owner) && MathUtil.isInRange(owner, notMyTarget, owner.getAggroRange()) && GeoService.getInstance().canSee(owner, notMyTarget))
		{
			final VisibleObject myTarget = notMyTarget.getTarget();
			if ((myTarget != null) && (myTarget instanceof Creature))
			{
				final Creature targetCreature = (Creature) myTarget;
				PacketSendUtility.broadcastPacket(owner, new SM_ATTACK(owner, targetCreature, 0, 633, 0, Collections.singletonList(new AttackResult(0, AttackStatus.NORMALHIT))));
				ThreadPoolManager.getInstance().schedule(new AggroNotifier(owner, targetCreature, false), 500);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Determines if an {@link NpcAI2} should protect a player from an attacker.<br>
	 * This method checks if the NPC is a guard type.<br>
	 * It verifies if the NPC can see the attacker and is within range of the player.
	 * @param npcAI The AI instance of the NPC to check.
	 * @param attacker The creature currently attacking the target.
	 * @return {@code true} if the NPC should start hating the attacker, otherwise {@code false}.
	 */
	public static boolean onGuardAgainstAttacker(NpcAI2 npcAI, Creature attacker)
	{
		final Npc owner = npcAI.getOwner();
		final TribeClass tribe = owner.getTribe();
		if (!tribe.isGuard() && (owner.getObjectTemplate().getNpcTemplateType() != NpcTemplateType.GUARD))
		{
			return false;
		}
		
		final VisibleObject target = attacker.getTarget();
		if ((target != null) && (target instanceof Player))
		{
			final Player playerTarget = (Player) target;
			if (!owner.isEnemy(playerTarget) && owner.isEnemy(attacker) && MathUtil.isInRange(owner, playerTarget, owner.getAggroRange()) && GeoService.getInstance().canSee(owner, attacker))
			{
				owner.getAggroList().startHate(attacker);
				return true;
			}
		}
		
		return false;
	}
	
	private static final class AggroNotifier implements Runnable
	{
		private Npc aggressive;
		private Creature target;
		private final boolean broadcast;
		
		AggroNotifier(Npc aggressive, Creature target, boolean broadcast)
		{
			this.aggressive = aggressive;
			this.target = target;
			this.broadcast = broadcast;
		}
		
		@Override
		public void run()
		{
			aggressive.getAggroList().addHate(target, 1);
			if (broadcast)
			{
				aggressive.getKnownList().doOnAllNpcs(new Visitor<Npc>()
				{
					
					@Override
					public void visit(Npc object)
					{
						object.getAi2().onCreatureEvent(AIEventType.CREATURE_NEEDS_SUPPORT, aggressive);
					}
				});
			}
			
			aggressive = null;
			target = null;
		}
	}
}
