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
package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for actions that consume {@code MP} from a character.<br>
 * It validates if the player has enough resources before executing the skill.<br>
 * This class is part of the {@link Action} system used by the skill engine.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MpUseAction")
public class MpUseAction extends Action
{
	@XmlAttribute(required = true)
	protected int value;
	@XmlAttribute
	protected int delta;
	@XmlAttribute
	protected boolean ratio;
	
	/**
	 * Executes the action for a specific {@link Skill}.<br>
	 * It checks if the player has enough MP to perform the skill.<br>
	 * If successful, it subtracts the required amount from the player's MP.
	 * @param skill The {@code Skill} object to be processed.
	 * @return {@code true} if the action was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean act(Skill skill)
	{
		final Creature effector = skill.getEffector();
		final int currentMp = effector.getLifeStats().getCurrentMp();
		int valueWithDelta = value + (delta * skill.getSkillLevel());
		if (ratio)
		{
			valueWithDelta = (skill.getEffector().getLifeStats().getMaxMp() * valueWithDelta) / 100;
		}
		
		final int changeMpPercent = skill.getBoostSkillCost();
		if (changeMpPercent != 0)
		{
			// changeMpPercent is negative
			valueWithDelta = valueWithDelta - ((valueWithDelta / ((100 / changeMpPercent))));
		}
		
		if (effector instanceof Player)
		{
			if ((currentMp <= 0) || (currentMp < valueWithDelta))
			{
				PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_SKILL_NOT_ENOUGH_MP);
				return false;
			}
		}
		
		effector.getLifeStats().reduceMp(valueWithDelta);
		return true;
	}
}
