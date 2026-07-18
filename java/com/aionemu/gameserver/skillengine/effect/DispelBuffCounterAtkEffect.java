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

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This effect removes counter-attack buffs from a target.<br>
 * It is used to prevent the target from performing counter-attacks after being hit by a skill.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DispelBuffCounterAtkEffect")
public class DispelBuffCounterAtkEffect extends DamageEffect
{
	@XmlAttribute
	protected int dpower;
	@XmlAttribute
	protected int power;
	@XmlAttribute
	protected int hitvalue;
	@XmlAttribute
	protected int hitdelta;
	@XmlAttribute(name = "dispel_level")
	protected int dispelLevel;
	private int i;
	private int finalPower;
	
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		super.applyEffect(effect);
		effect.getEffected().getEffectController().dispelBuffCounterAtkEffect(i, dispelLevel, finalPower);
	}
	
	/**
	 * Calculates the final values for a specific {@code Effect}.<br>
	 * This method determines the hit value based on the number of active effects.<br>
	 * It then uses {@code calculateMagicalSkillResult} to apply the result.
	 * @param effect The {@code Effect} object to be processed.
	 */
	@Override
	public void calculate(Effect effect)
	{
		if (!super.calculate(effect, null, null))
		{
			return;
		}
		
		final Creature effected = effect.getEffected();
		final int count = value + (delta * effect.getSkillLevel());
		finalPower = power + (dpower * effect.getSkillLevel());
		
		i = effected.getEffectController().calculateNumberOfEffects(dispelLevel);
		i = (i < count ? i : count);
		
		int newValue = 0;
		if (i == 1)
		{
			newValue = hitvalue;
		}
		else if (i > 1)
		{
			newValue = hitvalue + ((hitvalue / 2) * (i - 1));
		}
		
		final int valueWithDelta = newValue + (hitdelta * effect.getSkillLevel());
		
		final ActionModifier modifier = getActionModifiers(effect);
		
		AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, modifier, getElement(), true, true, false, Func.ADD, 0, 0, shared, true); // critprob 0, dispels can not crit
	}
}
