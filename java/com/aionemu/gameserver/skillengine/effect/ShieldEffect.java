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

import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.controllers.observer.AttackShieldObserver;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * Represents a shield effect applied to a character.<br>
 * This class handles the logic for providing defensive protection against incoming attacks.<br>
 * It extends {@link EffectTemplate} to integrate with the skill engine.
 * @author ATracer modified by Wakizashi, Sippolo, kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShieldEffect")
public class ShieldEffect extends EffectTemplate
{
	@XmlAttribute
	protected int hitdelta;
	@XmlAttribute
	protected int hitvalue;
	@XmlAttribute
	protected boolean percent;
	@XmlAttribute
	protected int radius = 0;
	@XmlAttribute
	protected int minradius = 0;
	@XmlAttribute
	protected Race condrace = null;
	
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		// check for condition race, skillId: 10317,10318
		if ((condrace != null) && (effect.getEffected().getRace() != condrace))
		{
			return;
		}
		
		effect.addToEffectedController();
	}
	
	/**
	 * Links this instance as a success effect.<br>
	 * This updates the provided {@code Effect} object.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		effect.addSucessEffect(this);
	}
	
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
		
		final AttackShieldObserver asObserver = new AttackShieldObserver(hitValueWithDelta, valueWithDelta, percent, effect, hitType, getType(), hitTypeProb);
		
		effect.getEffected().getObserveController().addAttackCalcObserver(asObserver);
		effect.setAttackShieldObserver(asObserver, position);
		effect.getEffected().getEffectController().setUnderShield(true);
	}
	
	/**
	 * Stops a specific {@code Effect} from being active.<br>
	 * This method removes the associated observers from the target controller.<br>
	 * Use this to clean up effects when they expire or are removed.
	 * @param effect The {@code Effect} object to stop.
	 */
	@Override
	public void endEffect(Effect effect)
	{
		final AttackCalcObserver acObserver = effect.getAttackShieldObserver(position);
		if (acObserver != null)
		{
			effect.getEffected().getObserveController().removeAttackCalcObserver(acObserver);
		}
		
		effect.getEffected().getEffectController().setUnderShield(false);
	}
	
	/**
	 * Returns the unique identifier for this effect type.<br>
	 * This value is used to identify {@link ShieldEffect} instances in the system.
	 * @return The integer value representing the effect type.
	 */
	public int getType()
	{
		return 2;
	}
}
