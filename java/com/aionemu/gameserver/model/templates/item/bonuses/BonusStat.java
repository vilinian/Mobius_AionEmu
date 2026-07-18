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
package com.aionemu.gameserver.model.templates.item.bonuses;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * Represents a specific attribute bonus applied to an item.<br>
 * It maps a {@link StatEnum} type to a numerical value for character statistics.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BonusStat")
public class BonusStat
{
	@XmlAttribute(name = "name", required = true)
	protected StatEnum name;
	@XmlAttribute(name = "min_value", required = true)
	protected int min;
	@XmlAttribute(name = "max_value", required = true)
	protected int max;
	
	/**
	 * Retrieves the name of the statistic associated with this bonus.<br>
	 * This returns a {@link StatEnum} value.
	 * @return The {@code StatEnum} representing the name.
	 */
	public StatEnum getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the minimum value for this bonus statistic.<br>
	 * This value is stored in the {@code min} field.
	 * @return The smallest possible integer value for the stat.
	 */
	public int getMin()
	{
		return min;
	}
	
	/**
	 * Retrieves the maximum value of this bonus.<br>
	 * This method returns the {@code max} field.
	 * @return The maximum integer value.
	 */
	public int getMax()
	{
		return max;
	}
}
