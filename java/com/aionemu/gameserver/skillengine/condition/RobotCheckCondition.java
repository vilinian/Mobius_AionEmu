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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class checks if a player is currently in a {@code robot} state.<br>
 * It is used by the skill engine to determine if specific skills can be executed based on this status.
 * @author Ever'
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RobotCheckCondition")
public class RobotCheckCondition extends Condition
{
	/**
	 * Validates if a {@link Skill} can be used by checking the target player.<br>
	 * It ensures the player has a robot ID and is equipped with a keyblade.<br>
	 * If requirements are not met, it sends a system message to the player.
	 * @param skill The {@code Skill} object to be validated.
	 * @return {@code true} if the skill is valid, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill skill)
	{
		if (skill.getEffector() instanceof Player)
		{
			final Player player = (Player) skill.getEffector();
			if (player.getEquipment().isKeybladeEquipped() && (player.getRobotId() != 0))
			{
				return true;
			}
			
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SKILL_NO_ROBOT);
			return false;
		}
		
		return true;
	}
}
