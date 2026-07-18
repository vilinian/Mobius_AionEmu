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
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This class handles the instant drain of a target's spell attack power.<br>
 * It is an extension of {@link DamageEffect} used within the skill engine.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpellAtkDrainInstantEffect")
public class SpellAtkDrainInstantEffect extends DamageEffect
{
	@XmlAttribute(name = "hp_percent")
	protected int hp_percent;
	@XmlAttribute(name = "mp_percent")
	protected int mp_percent;
	
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
		if (hp_percent != 0)
		{
			effect.getEffector().getLifeStats().increaseHp(TYPE.HP, (effect.getReserved1() * hp_percent) / 100, effect.getSkillId(), LOG.SPELLATKDRAININSTANT);
		}
		
		if (mp_percent != 0)
		{
			effect.getEffector().getLifeStats().increaseMp(TYPE.ABSORBED_MP, (effect.getReserved1() * mp_percent) / 100, effect.getSkillId(), LOG.SPELLATKDRAININSTANT);
		}
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
		super.calculate(effect, DamageType.MAGICAL);
	}
}
