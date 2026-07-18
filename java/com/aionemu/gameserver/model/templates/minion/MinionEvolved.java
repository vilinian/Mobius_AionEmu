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
package com.aionemu.gameserver.model.templates.minion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the data model for an evolved minion entity.<br>
 * This class stores the specific attributes and properties of a {@code Minion} after it has undergone evolution.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MinionEvolved")
public class MinionEvolved
{
	@XmlAttribute(name = "itemId")
	private int itemId;
	@XmlAttribute(name = "evolvedNum")
	private int evolvedNum;
	@XmlAttribute(name = "evolvedCost")
	private int evolvedCost;
	
	/**
	 * Retrieves the unique identifier for this wardrobe item.<br>
	 * This value corresponds to the {@code itemId} assigned during object creation.
	 * @return The unique {@code int} ID of the item.
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Retrieves the number of evolutions for this minion.<br>
	 * This value is stored in the {@code evolvedNum} field.
	 * @return The current evolution count as an {@code int}.
	 */
	public int getEvolvedNum()
	{
		return evolvedNum;
	}
	
	/**
	 * Retrieves the cost required to evolve this minion.<br>
	 * This value is stored in the {@code evolvedCost} field.
	 * @return The evolution cost as an {@code int}.
	 */
	public int getEvolvedCost()
	{
		return evolvedCost;
	}
}
