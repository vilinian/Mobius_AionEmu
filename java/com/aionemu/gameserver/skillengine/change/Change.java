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
package com.aionemu.gameserver.skillengine.change;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.condition.Conditions;

/**
 * Represents a modification to a character's statistics or state.<br>
 * This class is used by the {@link com.aionemu.gameserver.skillengine.condition.Conditions} system to apply specific effects.<br>
 * It defines how values are altered during skill execution.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Change")
public class Change
{
	@XmlAttribute(required = true)
	private StatEnum stat;
	@XmlAttribute(required = true)
	private Func func;
	@XmlAttribute(required = true)
	private int value;
	@XmlAttribute
	private int delta;
	@XmlElement(name = "conditions")
	private Conditions conditions;
	
	/**
	 * Retrieves the {@code StatEnum} associated with this bonus.<br>
	 * This method returns the specific type of statistic for the random bonus.
	 * @return The {@link StatEnum} value.
	 */
	public StatEnum getStat()
	{
		return stat;
	}
	
	/**
	 * Retrieves the {@code Func} associated with this attribute.<br>
	 * This method returns the function used for calculations.
	 * @return The {@code Func} object.
	 */
	public Func getFunc()
	{
		return func;
	}
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	public int getValue()
	{
		return value;
	}
	
	/**
	 * Retrieves the change amount for this effect.<br>
	 * This value represents the {@code delta} associated with the {@link Change} object.
	 * @return The current {@code delta} value as an {@code int}.
	 */
	public int getDelta()
	{
		return delta;
	}
	
	/**
	 * Retrieves the {@code Conditions} associated with this change.<br>
	 * This method returns the requirements that must be met for the effect to occur.
	 * @return the {@link Conditions} object or {@code null} if no conditions are set.
	 */
	public Conditions getConditions()
	{
		return conditions;
	}
}
