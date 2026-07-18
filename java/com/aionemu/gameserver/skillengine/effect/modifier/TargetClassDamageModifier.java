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
package com.aionemu.gameserver.skillengine.effect.modifier;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This class modifies the damage dealt to a target based on their {@link PlayerClass}.<br>
 * It allows for specific damage adjustments when attacking different types of characters.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetClassDamageModifier")
public class TargetClassDamageModifier extends ActionModifier
{
	@XmlAttribute(name = "class")
	private PlayerClass skillTargetClass;
	
	/**
	 * This method calculates the modified value of an {@link Effect}.<br>
	 * It takes the base value and adds a bonus based on the skill level.
	 * @param effect The {@code Effect} object to be analyzed.
	 * @return The final calculated integer value.
	 */
	@Override
	public int analyze(Effect effect)
	{
		final Creature effected = effect.getEffected();
		if (effected instanceof Player)
		{
			final Player player = (Player) effected;
			if (player.getPlayerClass() == skillTargetClass)
			{
				return value + (effect.getSkillLevel() * delta);
			}
		}
		
		return 0;
	}
	
	/**
	 * Verifies if the target of an {@code Effect} matches a specific class.<br>
	 * This method checks if the {@link Player} belongs to the required {@code PlayerClass}.
	 * @param effect The {@code Effect} to be checked.
	 * @return {@code true} if the player's class matches, otherwise {@code false}.
	 */
	@Override
	public boolean check(Effect effect)
	{
		final Creature effected = effect.getEffected();
		if (effected instanceof Player)
		{
			final Player player = (Player) effected;
			return player.getPlayerClass() == skillTargetClass;
		}
		
		return false;
	}
}
