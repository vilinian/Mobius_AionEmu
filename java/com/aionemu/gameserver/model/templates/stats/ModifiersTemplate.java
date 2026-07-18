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
package com.aionemu.gameserver.model.templates.stats;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatSetFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatSubFunction;

/**
 * This class serves as a template for defining various stat modifiers.<br>
 * It stores the data required to apply different types of effects like additions, subtractions, or percentage changes to character statistics.
 * @author xavier
 * @modified Rolandas
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "modifiers")
public class ModifiersTemplate
{
	@XmlElements(
	{
		@XmlElement(name = "sub", type = StatSubFunction.class),
		@XmlElement(name = "add", type = StatAddFunction.class),
		@XmlElement(name = "rate", type = StatRateFunction.class),
		@XmlElement(name = "set", type = StatSetFunction.class)
	})
	private List<StatFunction> modifiers;
	@XmlAttribute
	private float chance = 100f;
	@XmlAttribute
	private int level;
	
	/**
	 * Retrieves the list of stat modifiers for this {@link ManaStone}.<br>
	 * This method returns all active effects applied to the item.
	 * @return a {@code List} of {@link StatFunction} objects.
	 */
	public List<StatFunction> getModifiers()
	{
		return modifiers;
	}
	
	/**
	 * Retrieves the probability of this drop occurring.<br>
	 * The value is stored as a {@code float}.
	 * @return The drop chance value.
	 */
	public float getChance()
	{
		return chance;
	}
	
	/**
	 * Retrieves the current level of the modifier.<br>
	 * This value is used to calculate specific stat changes.
	 * @return The {@code float} value representing the level.
	 */
	public float getLevel()
	{
		return level;
	}
}
