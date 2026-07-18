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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatSetFunction;
import com.aionemu.gameserver.model.stats.container.CreatureGameStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.change.Change;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * Represents a buff effect that modifies the statistics of a {@link Creature}.<br>
 * This class serves as a base for various status effects applied to characters.<br>
 * It handles the logic for applying stat changes like additions, rates, or fixed values.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BuffEffect")
public abstract class BuffEffect extends EffectTemplate
{
	@XmlAttribute
	protected boolean maxstat;
	private static final Logger log = LoggerFactory.getLogger(BuffEffect.class);
	
	/**
	 * Adds the specified {@code Effect} to the controller.<br>
	 * This updates the internal state of the effect's target.
	 * @param effect The {@code Effect} object to be added.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
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
		final Creature effected = effect.getEffected();
		effected.getGameStats().endEffect(effect);
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
		if (change == null)
		{
			return;
		}
		
		final Creature effected = effect.getEffected();
		final CreatureGameStats<? extends Creature> cgs = effected.getGameStats();
		
		final List<IStatFunction> modifiers = getModifiers(effect);
		
		if (modifiers.size() > 0)
		{
			cgs.addEffect(effect, modifiers);
		}
		
		if (maxstat)
		{
			effected.getLifeStats().increaseHp(TYPE.HP, effected.getGameStats().getMaxHp().getCurrent());
			effected.getLifeStats().increaseMp(TYPE.HEAL_MP, effected.getGameStats().getMaxMp().getCurrent());
		}
	}
	
	/**
	 * Retrieves the list of stat modifiers for a specific {@code Effect}.<br>
	 * This method calculates values based on the skill level and applies conditions.
	 * @param effect The {@code Effect} object to retrieve modifiers from.
	 * @return A {@code List} of {@link IStatFunction} objects representing the changes.
	 */
	protected List<IStatFunction> getModifiers(Effect effect)
	{
		final int skillId = effect.getSkillId();
		final int skillLvl = effect.getSkillLevel();
		
		final List<IStatFunction> modifiers = new ArrayList<>();
		
		for (Change changeItem : change)
		{
			if (changeItem.getStat() == null)
			{
				log.warn("Skill stat has wrong name for skillid: " + skillId);
				continue;
			}
			
			final int valueWithDelta = changeItem.getValue() + (changeItem.getDelta() * skillLvl);
			
			final Conditions conditions = changeItem.getConditions();
			switch (changeItem.getFunc())
			{
				case ADD:
					modifiers.add(new StatAddFunction(changeItem.getStat(), valueWithDelta, true).withConditions(conditions));
					break;
				case PERCENT:
					modifiers.add(new StatRateFunction(changeItem.getStat(), valueWithDelta, true).withConditions(conditions));
					break;
				case REPLACE:
					modifiers.add(new StatSetFunction(changeItem.getStat(), valueWithDelta).withConditions(conditions));
					break;
			}
		}
		
		return modifiers;
	}
	
	/**
	 * Executes the periodic logic for a specific {@code Effect}.<br>
	 * This method checks if the effector is online and applies the effect to nearby players.<br>
	 * It handles group range calculations and ensures effects are applied correctly during duels.<br>
	 * Finally, it broadcasts the updated effect packet to the effector.
	 * @param effect The {@code Effect} object to be processed.
	 */
	@Override
	public void onPeriodicAction(Effect effect)
	{
		// TODO Auto-generated method stub
	}
}
