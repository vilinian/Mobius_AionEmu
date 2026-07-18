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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a random bonus applied to a real item.<br>
 * This class handles the logic for items that grant randomized attributes or effects.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RealItemRandomBonus")
public class RealItemRandomBonus
{
	@XmlElement(name = "rnd_stat")
	protected List<BonusStat> rndStat;
	@XmlAttribute(name = "id", required = true)
	protected int id;
	@XmlAttribute(name = "name", required = true)
	protected String name;
	@XmlAttribute(name = "rnd_count", required = true)
	protected int randomNumber;
	
	/**
	 * Retrieves the list of random statistics for this item.<br>
	 * If the list is {@code null}, it creates and returns a new {@code ArrayList}.
	 * @return A {@code List} of {@link BonusStat} objects.
	 */
	public List<BonusStat> getRndStat()
	{
		if (rndStat == null)
		{
			rndStat = new ArrayList<>();
		}
		
		return rndStat;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the random number associated with this bonus.<br>
	 * This value is stored in the {@code randomNumber} field.
	 * @return the current {@code int} value of the random number.
	 */
	public int getRandomNumber()
	{
		return randomNumber;
	}
}
