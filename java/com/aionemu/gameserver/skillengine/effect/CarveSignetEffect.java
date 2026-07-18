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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * Handles the specific logic for the {@code CarveSignet} skill effect.<br>
 * This class extends {@link DamageEffect} to define how damage is applied for this particular skill.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CarveSignetEffect")
public class CarveSignetEffect extends DamageEffect
{
	@XmlAttribute(required = true)
	protected int signetlvlstart;
	@XmlAttribute(required = true)
	protected int signetlvl;
	@XmlAttribute(required = true)
	protected int signetid;
	@XmlAttribute(required = true)
	protected String signet;
	@XmlAttribute(required = true)
	protected int prob = 100;
	private int nextSignetLevel = 1;
	
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
		
		if (Rnd.get(0, 100) > prob)
		{
			return;
		}
		
		final Effect placedSignet = effect.getEffected().getEffectController().getAnormalEffect(signet);
		
		if (placedSignet != null)
		{
			placedSignet.endEffect();
		}
		
		final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate((signetid + nextSignetLevel) - 1);
		final Effect newEffect = new Effect(effect.getEffector(), effect.getEffected(), template, nextSignetLevel, 0);
		newEffect.initialize();
		newEffect.applyEffect();
	}
	
	/**
	 * Calculates the attributes for a specific {@code Effect}.<br>
	 * This method updates the {@code effect} to include an AP boost.<br>
	 * It also links this instance as a success effect.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		if (!super.calculate(effect, DamageType.PHYSICAL))
		{
			return;
		}
		
		final Effect placedSignet = effect.getEffected().getEffectController().getAnormalEffect(signet);
		nextSignetLevel = signetlvlstart > 0 ? signetlvlstart : 1;
		effect.setCarvedSignet(nextSignetLevel);
		if (placedSignet != null)
		{
			nextSignetLevel = (placedSignet.getSkillId() - signetid) + 2;
			if ((signetlvlstart > 0) && (nextSignetLevel < signetlvlstart))
			{
				nextSignetLevel = signetlvlstart;
			}
			
			effect.setCarvedSignet(nextSignetLevel);
			if ((nextSignetLevel > signetlvl) || (nextSignetLevel > 5))
			{
				nextSignetLevel--;
			}
		}
	}
}
