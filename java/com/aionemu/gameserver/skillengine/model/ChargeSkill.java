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
package com.aionemu.gameserver.skillengine.model;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * Represents a skill that requires a charge mechanic.<br>
 * This class handles the logic for skills that build up power over time or through specific actions.
 * @author Cheatkiller
 */
public class ChargeSkill extends Skill
{
	/**
	 * Creates a new instance of a {@link ChargeSkill}.<br>
	 * This constructor initializes the skill with all required data.
	 * @param skillTemplate The template defining the skill properties.
	 * @param effector The {@code Player} who is performing the skill.
	 * @param skillLevel The current level of the skill being used.
	 * @param firstTarget The initial {@link Creature} targeted by the skill.
	 * @param itemTemplate The template for an associated item, if any.
	 */
	public ChargeSkill(SkillTemplate skillTemplate, Player effector, int skillLevel, Creature firstTarget, ItemTemplate itemTemplate)
	{
		super(skillTemplate, effector, skillLevel, firstTarget, null);
	}
	
	/**
	 * Calculates the total duration for a {@link ChargeSkill}.<br>
	 * This method updates the skill's time remaining based on current stats.
	 */
	@Override
	public void calculateSkillDuration()
	{
	}
	
	/**
	 * Executes the skill logic for the character.<br>
	 * It checks if the skill is available before starting.<br>
	 * This method updates the {@link Player} state and triggers observers.
	 * @return {@code true} if the skill was successfully started, otherwise {@code false}.
	 */
	@Override
	public boolean useSkill()
	{
		if (!canUseSkill())
		{
			return false;
		}
		
		effector.getObserveController().notifySkilluseObservers(this);
		effector.setCasting(this);
		startCast();
		effector.getObserveController().attach(conditionChangeListener);
		endCast();
		return true;
	}
}
