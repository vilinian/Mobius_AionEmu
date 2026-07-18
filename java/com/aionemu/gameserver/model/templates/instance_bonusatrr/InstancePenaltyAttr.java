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
package com.aionemu.gameserver.model.templates.instance_bonusatrr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.abyss_bonus.AbyssPenaltyAttr;
import com.aionemu.gameserver.skillengine.change.Func;

/**
 * Represents a penalty attribute applied to an instance.<br>
 * This class defines how specific stats are modified or restricted within a given instance context.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstancePenaltyAttr")
public class InstancePenaltyAttr
{
	@XmlAttribute(required = true)
	protected StatEnum stat;
	@XmlAttribute(required = true)
	protected Func func;
	@XmlAttribute(required = true)
	protected int value;
	
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
	 * Sets the {@code stat} for this attribute.<br>
	 * This updates the internal state of the {@link AbyssPenaltyAttr}.
	 * @param value The new {@code StatEnum} to assign.
	 */
	public void setStat(StatEnum value)
	{
		stat = value;
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
	 * Sets the {@code func} property of this object.<br>
	 * This method updates the function used for calculations.
	 * @param value The new {@link Func} to assign.
	 */
	public void setFunc(Func value)
	{
		func = value;
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
	 * Sets the numerical value for this attribute.<br>
	 * This updates the {@code value} field of the current object.
	 * @param value The new integer value to assign.
	 */
	public void setValue(int value)
	{
		this.value = value;
	}
}
