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

import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatArmorMasteryFunction;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This class handles the calculation of armor mastery effects for skills.<br>
 * It applies specific modifiers to a character's defense based on their mastery stats.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArmorMasteryEffect")
public class ArmorMasteryEffect extends BuffEffect
{
	@XmlAttribute(name = "armor")
	private ArmorType armorType;
	
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
		
		final List<IStatFunction> modifiers = getModifiers(effect);
		final List<IStatFunction> masteryModifiers = new ArrayList<>(modifiers.size());
		for (IStatFunction modifier : modifiers)
		{
			masteryModifiers.add(new StatArmorMasteryFunction(armorType, modifier.getName(), modifier.getValue(), modifier.isBonus()));
		}
		
		if (masteryModifiers.size() > 0)
		{
			effect.getEffected().getGameStats().addEffect(effect, masteryModifiers);
		}
	}
}
