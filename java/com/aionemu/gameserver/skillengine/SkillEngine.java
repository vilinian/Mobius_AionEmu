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
package com.aionemu.gameserver.skillengine;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.skillengine.model.ActivationAttribute;
import com.aionemu.gameserver.skillengine.model.ChargeSkill;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * This class serves as the core engine for processing all skill logic within the game.<br>
 * It handles the execution of {@link Skill} actions and manages their associated effects.<br>
 * It coordinates interactions between {@link Player} entities and other game objects.
 * @author ATracer
 */
public class SkillEngine
{
	public static final SkillEngine skillEngine = new SkillEngine();
	
	/**
	 * Private constructor to prevent direct instantiation.<br>
	 * Use {@code getInstance} to access the singleton instance.
	 */
	private SkillEngine()
	{
	}
	
	/**
	 * Retrieves a {@link Skill} for a specific player based on an ID.<br>
	 * This method looks up the skill template from the data manager.<br>
	 * It then resolves the final skill instance using the provided target.
	 * @param player The {@link Player} who is performing the action.
	 * @param skillId The unique identifier for the skill to retrieve.
	 * @param firstTarget The primary {@link VisibleObject} targeted by the skill.
	 * @return The resolved {@link Skill} object, or {@code null} if the ID is invalid.
	 */
	public Skill getSkillFor(Player player, int skillId, VisibleObject firstTarget)
	{
		final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		
		if (template == null)
		{
			return null;
		}
		
		return getSkillFor(player, template, firstTarget);
	}
	
	/**
	 * Retrieves a {@link Skill} object for a specific player and template.<br>
	 * This method checks if the player owns the skill or is provoked.<br>
	 * It also identifies the primary target from the provided object.
	 * @param player The {@link Player} who will use the skill.
	 * @param template The {@link SkillTemplate} defining the skill properties.
	 * @param firstTarget The initial {@link VisibleObject} targeted by the skill.
	 * @return A new {@link Skill} instance or {@code null} if the skill is unavailable.
	 */
	public Skill getSkillFor(Player player, SkillTemplate template, VisibleObject firstTarget)
	{
		// player doesn't have such skill and ist not provoked
		if (template.getActivationAttribute() != ActivationAttribute.PROVOKED)
		{
			if (!player.getSkillList().isSkillPresent(template.getSkillId()))
			{
				return null;
			}
		}
		
		Creature target = null;
		if (firstTarget instanceof Creature)
		{
			target = (Creature) firstTarget;
		}
		
		return new Skill(template, player, target);
	}
	
	/**
	 * Creates a new {@link Skill} instance for a specific player.<br>
	 * This method uses the provided {@code template} and {@code skillLevel}.<br>
	 * It identifies the primary target from the {@code firstTarget} object.
	 * @param player The {@link Player} who is casting the skill.
	 * @param template The {@link SkillTemplate} defining the skill properties.
	 * @param firstTarget The initial {@link VisibleObject} targeted by the skill.
	 * @param skillLevel The specific level of the skill to be used.
	 * @return A new {@link Skill} object ready for execution.
	 */
	public Skill getSkillFor(Player player, SkillTemplate template, VisibleObject firstTarget, int skillLevel)
	{
		Creature target = null;
		if (firstTarget instanceof Creature)
		{
			target = (Creature) firstTarget;
		}
		
		return new Skill(template, player, target, skillLevel);
	}
	
	/**
	 * Retrieves a specific {@link Skill} for a given creature.<br>
	 * This method looks up the skill based on its ID and level.<br>
	 * It also considers the first target to determine the correct variation.
	 * @param creature The {@code Creature} who owns the skill.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The current level of the skill being used.
	 * @param firstTarget The primary {@link VisibleObject} targeted by the skill.
	 * @return The resulting {@link Skill} object.
	 */
	public Skill getSkill(Creature creature, int skillId, int skillLevel, VisibleObject firstTarget)
	{
		return getSkill(creature, skillId, skillLevel, firstTarget, null);
	}
	
	/**
	 * Retrieves a {@link Skill} object based on the provided parameters.<br>
	 * This method creates a new instance of a skill using a template from the data manager.<br>
	 * It returns {@code null} if the requested {@code skillId} is not found.
	 * @param creature The {@link Creature} who owns or performs the skill.
	 * @param skillId The unique identifier for the skill to retrieve.
	 * @param skillLevel The level of the skill to be used.
	 * @param firstTarget The primary target of the skill, cast to a {@link Creature} if possible.
	 * @param itemTemplate The template of an item required for this skill.
	 * @return A new {@link Skill} instance or {@code null} if the template does not exist.
	 */
	public Skill getSkill(Creature creature, int skillId, int skillLevel, VisibleObject firstTarget, ItemTemplate itemTemplate)
	{
		final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		
		if (template == null)
		{
			return null;
		}
		
		Creature target = null;
		if (firstTarget instanceof Creature)
		{
			target = (Creature) firstTarget;
		}
		
		return new Skill(template, creature, skillLevel, target, itemTemplate);
	}
	
	/**
	 * Retrieves a {@code ChargeSkill} based on the provided parameters.<br>
	 * This method finds the specific skill instance for a player.<br>
	 * It uses the {@code skillId}, {@code skillLevel}, and target information.
	 * @param creature The {@link Player} who is performing the action.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The current level of the skill being used.
	 * @param firstTarget The primary {@link VisibleObject} targeted by the skill.
	 * @return The resulting {@code ChargeSkill} object.
	 */
	public ChargeSkill getChargeSkill(Player creature, int skillId, int skillLevel, VisibleObject firstTarget)
	{
		return getChargeSkill(creature, skillId, skillLevel, firstTarget, null);
	}
	
	/**
	 * Retrieves a {@code ChargeSkill} object based on the provided parameters.<br>
	 * This method looks up the skill template using the {@code skillId}.<br>
	 * It creates a new instance of {@code ChargeSkill} for the specified player and target.
	 * @param creature The {@link Player} who is performing the action.
	 * @param skillId The unique identifier for the skill.
	 * @param skillLevel The level of the skill to be used.
	 * @param firstTarget The primary object targeted by the skill.
	 * @param itemTemplate The template of the item involved in the skill, if any.
	 * @return A new {@code ChargeSkill} instance or {@code null} if the skill template is not found.
	 */
	public ChargeSkill getChargeSkill(Player creature, int skillId, int skillLevel, VisibleObject firstTarget, ItemTemplate itemTemplate)
	{
		final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		
		if (template == null)
		{
			return null;
		}
		
		Creature target = null;
		if (firstTarget instanceof Creature)
		{
			target = (Creature) firstTarget;
		}
		
		return new ChargeSkill(template, creature, skillLevel, target, itemTemplate);
	}
	
	/**
	 * Provides access to the singleton instance of {@link SkillEngine}.<br>
	 * Use this method to get the global engine for handling skills.
	 * @return The single shared instance of {@code SkillEngine}.
	 */
	public static SkillEngine getInstance()
	{
		return skillEngine;
	}
	
	/**
	 * Applies a specific skill effect directly to a target creature.<br>
	 * This method bypasses normal skill logic to trigger an effect immediately.
	 * @param skillId The unique identifier for the {@link Skill}.
	 * @param effector The {@link Creature} who is casting or initiating the effect.
	 * @param effected The {@link Creature} who will receive the effect.
	 * @param duration The length of time the effect should last in seconds.
	 */
	public void applyEffectDirectly(int skillId, Creature effector, Creature effected, int duration)
	{
		applyEffectDirectly(skillId, effector, effected, duration, false);
	}
	
	/**
	 * Applies a skill effect directly to a target creature.<br>
	 * This method bypasses normal skill logic and forces the effect immediately.<br>
	 * It uses the template associated with the provided {@code skillId}.
	 * @param skillId The unique identifier for the skill to apply.
	 * @param effector The creature that is casting or initiating the effect.
	 * @param effected The creature that will receive the effect.
	 * @param duration The length of time the effect should last.
	 * @param noRemoveAtDie Determines if the effect persists after a creature dies. Set to {@code true} to keep it, or {@code false} to remove it.
	 */
	public void applyEffectDirectly(int skillId, Creature effector, Creature effected, int duration, boolean noRemoveAtDie)
	{
		final SkillTemplate st = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (st == null)
		{
			return;
		}
		
		final Effect ef = new Effect(effector, effected, st, st.getLvl(), duration);
		ef.setIsForcedEffect(true);
		ef.initialize();
		if (duration > 0)
		{
			ef.setForcedDuration(true);
		}
		
		ef.getSkillTemplate().setNoRemoveAtDie(noRemoveAtDie);
		ef.applyEffect();
	}
}
