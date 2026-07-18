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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;

/**
 * This class serves as the base template for all dispel effects in the skill engine.<br>
 * It provides common functionality for removing various types of status effects from targets.
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractDispelEffect")
public class AbstractDispelEffect extends EffectTemplate
{
	@XmlAttribute
	protected int dpower;
	@XmlAttribute
	protected int power;
	@XmlAttribute(name = "dispel_level")
	protected int dispelLevel;
	
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		// nothing to do, its overriden
	}
	
	/**
	 * Applies a specific effect to a target.<br>
	 * This method calculates the power and count based on the skill level.<br>
	 * It then removes the effect from the controller using the provided category and slot.
	 * @param effect The {@code Effect} object to be processed.
	 * @param type The {@code DispelCategoryType} used to identify the effect category.
	 * @param slot The {@code SkillTargetSlot} where the effect is located.
	 */
	public void applyEffect(Effect effect, DispelCategoryType type, SkillTargetSlot slot)
	{
		final boolean isItemTriggered = (effect.getItemTemplate() != null);
		final int count = value + (delta * effect.getSkillLevel());
		final int finalPower = power + (dpower * effect.getSkillLevel());
		
		effect.getEffected().getEffectController().removeEffectByDispelCat(type, slot, count, dispelLevel, finalPower, isItemTriggered);
	}
}
