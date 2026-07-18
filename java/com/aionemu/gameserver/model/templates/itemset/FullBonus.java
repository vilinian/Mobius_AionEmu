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
package com.aionemu.gameserver.model.templates.itemset;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * Represents the full set of bonuses associated with an item set.<br>
 * It contains a list of {@link ModifiersTemplate} that define the attributes granted to the player.
 * @author ATracer
 */
@XmlRootElement(name = "FullBonus")
@XmlAccessorType(XmlAccessType.FIELD)
public class FullBonus
{
	@XmlElement(name = "modifiers", required = false)
	protected ModifiersTemplate modifiers;
	private int totalnumberofitems;
	
	/**
	 * Retrieves the list of stat modifiers for this {@link FullBonus}.<br>
	 * This method returns all active effects applied to the item.
	 * @return a {@code List} of {@link StatFunction} objects.
	 */
	public List<StatFunction> getModifiers()
	{
		return modifiers != null ? modifiers.getModifiers() : null;
	}
	
	/**
	 * Retrieves the total number of items.<br>
	 * This method returns the value stored in the {@code totalnumberofitems} field.
	 * @return The total count as an {@code int}.
	 */
	public int getCount()
	{
		return totalnumberofitems;
	}
	
	/**
	 * Sets the total count of items in this collection.<br>
	 * This updates the {@code totalnumberofitems} field.
	 * @param number The new quantity to assign.
	 */
	public void setNumberOfItems(int number)
	{
		totalnumberofitems = number;
	}
}
