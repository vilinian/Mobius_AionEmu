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
package com.aionemu.gameserver.skillengine.properties;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.properties.Properties.CastState;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * Defines the range for selecting the first target of a skill.<br>
 * This property determines which {@link Creature} is targeted based on proximity.<br>
 * It helps the {@link Skill} engine identify valid targets within a specific distance.
 * @author ATracer
 */
public class FirstTargetRangeProperty
{
	/**
	 * Validates if the first target is within the required range for a skill.<br>
	 * This method checks distance, weapon range, and line of sight.<br>
	 * It sends a system message to the {@code Player} if the target is too far or blocked.
	 * @param skill The {@link Skill} being executed.
	 * @param properties The {@link Properties} containing range configuration.
	 * @param castState The current {@link CastState} of the action.
	 * @return {@code true} if the target is valid, {@code false} otherwise.
	 */
	public static boolean set(Skill skill, Properties properties, CastState castState)
	{
		float firstTargetRange = properties.getFirstTargetRange();
		if (!skill.isFirstTargetRangeCheck())
		{
			return true;
		}
		
		final Creature effector = skill.getEffector();
		final Creature firstTarget = skill.getFirstTarget();
		
		if (firstTarget == null)
		{
			return false;
		}
		
		// Add Weapon Range to distance
		if (properties.isAddWeaponRange())
		{
			firstTargetRange += skill.getEffector().getGameStats().getAttackRange().getCurrent() / 1000f;
		}
		
		// on end cast check add revision distance value
		if (!castState.isCastStart())
		{
			firstTargetRange += properties.getRevisionDistance();
		}
		
		if (firstTarget.getObjectId() == effector.getObjectId())
		{
			return true;
		}
		
		if (!MathUtil.isInAttackRange(effector, firstTarget, firstTargetRange + 2))
		{
			if (effector instanceof Player)
			{
				PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_ATTACK_TOO_FAR_FROM_TARGET);
			}
			
			return false;
		}
		
		// TODO check for all targets in the Summon Group Member exception.
		if (skill.getSkillTemplate().getSkillId() != 1606)
		{
			if (!GeoService.getInstance().canSee(effector, firstTarget))
			{
				if (effector instanceof Player)
				{
					PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_SKILL_OBSTACLE);
				}
				
				return false;
			}
		}
		
		return true;
	}
}
