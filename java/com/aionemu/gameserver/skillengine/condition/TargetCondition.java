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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.properties.FirstTargetAttribute;
import com.aionemu.gameserver.skillengine.properties.TargetRangeAttribute;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Represents the conditions required to validate a target for a skill.<br>
 * It is used by the {@link com.aionemu.gameserver.skillengine.model.Skill} system to determine if an action can be performed on a specific entity.
 * @author ATracer, kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetCondition")
public class TargetCondition extends Condition
{
	@XmlAttribute(required = true)
	protected TargetAttribute value;
	
	/**
	 * Retrieves the current {@code TargetAttribute} for this condition.<br>
	 * This method returns the specific attribute assigned to the target.
	 * @return The {@link TargetAttribute} object.
	 */
	public TargetAttribute getValue()
	{
		return value;
	}
	
	/**
	 * Checks if the provided {@link Skill} meets the required target conditions.<br>
	 * This method validates the skill against specific target types and attributes.
	 * @param skill The {@code Skill} object to be validated.
	 * @return {@code true} if the skill is valid, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill skill)
	{
		if ((value == TargetAttribute.NONE) || (value == TargetAttribute.ALL) || skill.getSkillTemplate().getProperties().getTargetType().equals(TargetRangeAttribute.AREA))
		{
			return true;
		}
		
		if (((skill.getSkillTemplate().getProperties().getFirstTarget() != FirstTargetAttribute.TARGET) && (skill.getSkillTemplate().getProperties().getFirstTarget() != FirstTargetAttribute.TARGETORME)) || ((skill.getSkillTemplate().getProperties().getFirstTarget() == FirstTargetAttribute.TARGETORME) && (skill.getEffector() == skill.getFirstTarget())))
		{
			return true;
		}
		
		boolean result = false;
		switch (value)
		{
			case NPC:
				result = (skill.getFirstTarget() instanceof Npc);
				break;
			case PC:
				result = (skill.getFirstTarget() instanceof Player);
				break;
			default:
				break;
		}
		
		if (!result && (skill.getEffector() instanceof Player))
		{
			PacketSendUtility.sendPacket((Player) skill.getEffector(), SM_SYSTEM_MESSAGE.STR_SKILL_TARGET_IS_NOT_VALID);
		}
		
		return result;
	}
}
