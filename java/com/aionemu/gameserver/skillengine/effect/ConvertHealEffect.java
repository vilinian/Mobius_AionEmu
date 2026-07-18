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

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.controllers.observer.AttackShieldObserver;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;

/**
 * This class handles the conversion of healing effects into shield values.<br>
 * It extends {@link ShieldEffect} to manage how specific {@link HealType} instances are processed.
 * @author kecimis
 */
public class ConvertHealEffect extends ShieldEffect
{
	@XmlAttribute
	protected HealType type;
	@XmlAttribute(name = "hitpercent")
	protected boolean hitPercent;
	
	/**
	 * Starts a new {@link Effect} instance.<br>
	 * This method initializes the effect and begins its execution.<br>
	 * It is a convenience method that passes {@code null} for the abnormal state.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		final int skillLvl = effect.getSkillLevel();
		final int valueWithDelta = value + (delta * skillLvl);
		final int hitValueWithDelta = hitvalue + (hitdelta * skillLvl);
		
		final AttackShieldObserver asObserver = new AttackShieldObserver(hitValueWithDelta, valueWithDelta, percent, hitPercent, effect, hitType, getType(), hitTypeProb, 0, 0, type, 0, 0);
		
		effect.getEffected().getObserveController().addAttackCalcObserver(asObserver);
		effect.setAttackShieldObserver(asObserver, position);
		effect.getEffected().getEffectController().setUnderShield(true);
	}
	
	/**
	 * Gets the type of this heal effect.<br>
	 * This value identifies the specific category of the effect.
	 * @return The integer value representing the effect type.
	 */
	@Override
	public int getType()
	{
		return 0;
	}
}
