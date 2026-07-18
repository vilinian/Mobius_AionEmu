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
package com.aionemu.gameserver.skillengine.condition;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.Skill.SkillMethod;

/**
 * This class defines conditions related to the weapon used by a {@link Creature}.<br>
 * It allows the skill engine to check for specific requirements involving {@link WeaponType} or other weapon-based attributes.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeaponCondition")
public class WeaponCondition extends Condition
{
	@XmlAttribute(name = "weapon")
	private List<WeaponType> weaponType;
	
	/**
	 * Validates the weapon condition for a skill.<br>
	 * This method checks if the effector has a valid weapon during casting.<br>
	 * It returns {@code true} if the skill is not a cast or if the weapon is valid.
	 * @param env The {@link Skill} object containing the current environment and targets.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill env)
	{
		if (env.getSkillMethod() != SkillMethod.CAST)
		{
			return true;
		}
		
		return isValidWeapon(env.getEffector());
	}
	
	/**
	 * Validates whether the owner of the {@code Stat2} meets the weapon requirements.<br>
	 * This method checks the creature associated with the provided statistic.
	 * @param stat The {@code Stat2} object containing the owner information.
	 * @param statFunction The {@link IStatFunction} being processed.
	 * @return {@code true} if the owner is valid, {@code false} otherwise.
	 */
	@Override
	public boolean validate(Stat2 stat, IStatFunction statFunction)
	{
		return isValidWeapon(stat.getOwner());
	}
	
	/**
	 * Checks if the {@code creature} is using a valid weapon type.<br>
	 * This method verifies the main hand weapon of a {@link Player}.<br>
	 * It returns {@code true} for all non-player creatures.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the weapon is valid, otherwise {@code false}.
	 */
	private boolean isValidWeapon(Creature creature)
	{
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			return weaponType.contains(player.getEquipment().getMainHandWeaponType());
		}
		
		// for npcs we don't validate weapon, though in templates they are present
		return true;
	}
}
