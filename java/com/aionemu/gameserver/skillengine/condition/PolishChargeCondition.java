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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class handles the specific logic for {@code PolishCharge} skill conditions.<br>
 * It extends {@link ChargeCondition} to provide specialized behavior for this skill type.
 * @author Rolandas
 * @modified Cheatkiller
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolishChargeCondition")
public class PolishChargeCondition extends ChargeCondition
{
	/**
	 * Checks if the effector has a weapon with an Idian Stone.<br>
	 * This method decreases the polish charge of the stone if found.<br>
	 * It returns {@code true} if the charge was successfully decreased.
	 * @param env The {@link Skill} object containing the current environment and targets.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean validate(Skill env)
	{
		if (env.getEffector() instanceof Player)
		{
			final Player effector = (Player) env.getEffector();
			for (Item item : effector.getEquipment().getEquippedItems())
			{
				if (item.getItemTemplate().isWeapon() && (item.getIdianStone() != null))
				{
					item.getIdianStone().decreasePolishCharge(effector, value);
					return true;
				}
			}
		}
		
		return false;
	}
}
