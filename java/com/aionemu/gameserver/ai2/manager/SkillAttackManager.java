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
package com.aionemu.gameserver.ai2.manager;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.skill.NpcSkillList;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.model.SkillType;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Manages the logic for NPC skill attacks within the {@link NpcAI2} system.<br>
 * It handles selecting and executing skills based on current combat conditions.
 * @author ATracer
 */
public class SkillAttackManager
{
	/**
	 * Executes an attack for a specific {@link NpcAI2} instance.<br>
	 * This method checks if the target is in range before scheduling the action.<br>
	 * It handles both immediate attacks and delayed actions based on the provided time.
	 * @param npcAI The AI instance that will perform the attack.
	 * @param delay The amount of time in milliseconds to wait before attacking.
	 */
	public static void performAttack(NpcAI2 npcAI, int delay)
	{
		if (npcAI.getOwner().getObjectTemplate().getAttackRange() == 0)
		{
			if ((npcAI.getOwner().getTarget() != null) && !MathUtil.isInRange(npcAI.getOwner(), npcAI.getOwner().getTarget(), npcAI.getOwner().getAggroRange()))
			{
				npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
				npcAI.getOwner().getController().abortCast();
				return;
			}
		}
		
		if (npcAI.setSubStateIfNot(AISubState.CAST))
		{
			if (delay > 0)
			{
				ThreadPoolManager.getInstance().schedule(new SkillAction(npcAI), delay + DataManager.SKILL_DATA.getSkillTemplate(npcAI.getSkillId()).getDuration());
			}
			else
			{
				skillAction(npcAI);
			}
		}
	}
	
	/**
	 * Executes the skill action for a specific NPC.<br>
	 * This method checks if the target is within range and valid.<br>
	 * It then attempts to use the skill based on its template type.<br>
	 * If the skill fails or is already active, it triggers {@code afterUseSkill}.
	 * @param npcAI The {@code NpcAI2} instance performing the action.
	 */
	protected static void skillAction(NpcAI2 npcAI)
	{
		final Creature target = (Creature) npcAI.getOwner().getTarget();
		if (npcAI.getOwner().getObjectTemplate().getAttackRange() == 0)
		{
			if ((npcAI.getOwner().getTarget() != null) && !MathUtil.isInRange(npcAI.getOwner(), npcAI.getOwner().getTarget(), npcAI.getOwner().getAggroRange()))
			{
				npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
				npcAI.getOwner().getController().abortCast();
				return;
			}
		}
		
		if ((target != null) && !target.getLifeStats().isAlreadyDead())
		{
			final int skillId = npcAI.getSkillId();
			final int skillLevel = npcAI.getSkillLevel();
			
			final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
			final int duration = template.getDuration();
			if (npcAI.isLogging())
			{
				AI2Logger.info(npcAI, "Using skill " + skillId + " level: " + skillLevel + " duration: " + duration);
			}
			
			switch (template.getSubType())
			{
				case BUFF:
					switch (template.getProperties().getFirstTarget())
					{
						case ME:
							if (npcAI.getOwner().getEffectController().isAbnormalPresentBySkillId(skillId))
							{
								afterUseSkill(npcAI);
								return;
							}
							break;
						default:
							if (target.getEffectController().isAbnormalPresentBySkillId(skillId))
							{
								afterUseSkill(npcAI);
								return;
							}
					}
					break;
				default:
					break;
			}
			
			final boolean success = npcAI.getOwner().getController().useSkill(skillId, skillLevel);
			if (!success)
			{
				afterUseSkill(npcAI);
			}
		}
		else
		{
			npcAI.setSubStateIfNot(AISubState.NONE);
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
		}
		
	}
	
	/**
	 * Handles the logic that occurs after an {@link NpcAI2} finishes using a skill.<br>
	 * It resets the NPC state to {@code NONE}.<br>
	 * It triggers the {@code ATTACK_COMPLETE} event for the AI.
	 * @param npcAI The {@link NpcAI2} instance that just finished its skill action.
	 */
	public static void afterUseSkill(NpcAI2 npcAI)
	{
		npcAI.setSubStateIfNot(AISubState.NONE);
		npcAI.onGeneralEvent(AIEventType.ATTACK_COMPLETE);
	}
	
	/**
	 * Selects the next available skill for an NPC to use.<br>
	 * This method checks if the NPC is ready and not restricted by debuffs.<br>
	 * It returns a random valid skill from the owner's skill list.
	 * @param npcAI The {@link NpcAI2} instance of the NPC making the decision.
	 * @return The selected {@link NpcSkillEntry} or {@code null} if no skill can be used.
	 */
	public static NpcSkillEntry chooseNextSkill(NpcAI2 npcAI)
	{
		if (npcAI.isInSubState(AISubState.CAST))
		{
			return null;
		}
		
		final Npc owner = npcAI.getOwner();
		final NpcSkillList skillList = owner.getSkillList();
		if ((skillList == null) || (skillList.size() == 0))
		{
			return null;
		}
		
		if (owner.getGameStats().canUseNextSkill())
		{
			final NpcSkillEntry npcSkill = skillList.getRandomSkill();
			if (npcSkill != null)
			{
				final int currentHpPercent = owner.getLifeStats().getHpPercentage();
				
				if (npcSkill.isReady(currentHpPercent, System.currentTimeMillis() - owner.getGameStats().getFightStartingTime()))
				{
					// Check for Bind/Silence/Fear debuffs on npc
					final SkillTemplate template = npcSkill.getSkillTemplate();
					if (((template.getType() == SkillType.MAGICAL) && owner.getEffectController().isAbnormalSet(AbnormalState.SILENCE)) || ((template.getType() == SkillType.PHYSICAL) && owner.getEffectController().isAbnormalSet(AbnormalState.BIND)) || (owner.getEffectController().isUnderFear()))
					{
						return null;
					}
					
					npcSkill.setLastTimeUsed();
					
					return npcSkill;
				}
			}
		}
		
		return null;
	}
	
	private final static class SkillAction implements Runnable
	{
		private NpcAI2 npcAI;
		
		SkillAction(NpcAI2 npcAI)
		{
			this.npcAI = npcAI;
		}
		
		@Override
		public void run()
		{
			skillAction(npcAI);
			npcAI = null;
		}
	}
}
