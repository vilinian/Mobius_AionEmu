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
package com.aionemu.gameserver.skillengine.periodicaction;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 * This class manages a collection of periodic actions for skills.<br>
 * It handles the execution of effects that repeat over time.<br>
 * Use this class to define how recurring skill behaviors are processed.
 * @author antness
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PeriodicActions", propOrder = "periodicActions")
public class PeriodicActions
{
	/**
	 * Represents the periodic actions for a skill.<br>
	 * This class contains data for recurring effects like HP or MP usage.
	 */
	@XmlElements(
	{
		@XmlElement(name = "hpuse", type = HpUsePeriodicAction.class),
		@XmlElement(name = "mpuse", type = MpUsePeriodicAction.class)
	})
	protected List<PeriodicAction> periodicActions;
	@XmlAttribute(name = "checktime")
	protected int checktime;
	
	@XmlAttribute(name = "endingtime")
	protected int endingtime;
	
	/**
	 * Retrieves the list of all periodic actions.<br>
	 * This method returns the internal collection of {@link PeriodicAction} objects.
	 * @return a {@code List} containing all {@code PeriodicAction} instances.
	 */
	public List<PeriodicAction> getPeriodicActions()
	{
		return periodicActions;
	}
	
	/**
	 * Retrieves the time interval for checking periodic actions.<br>
	 * This value determines how often the system evaluates these actions.
	 * @return The current {@code checktime} as an {@code int}.
	 */
	public int getChecktime()
	{
		return checktime;
	}
	
	/**
	 * Retrieves the time when the periodic action ends.<br>
	 * This value is stored in the {@code endingtime} field.
	 * @return The end time as an {@code int}.
	 */
	public int getEndingtime()
	{
		return endingtime;
	}
}
