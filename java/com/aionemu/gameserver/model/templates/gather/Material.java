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
package com.aionemu.gameserver.model.templates.gather;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a gatherable material template in the game world.<br>
 * This class defines the properties for items that players can collect from the environment.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Material")
public class Material implements Comparable<Material>
{
	@XmlAttribute
	protected String name;
	@XmlAttribute
	protected int itemid;
	@XmlAttribute
	protected int nameid;
	@XmlAttribute
	protected int rate;
	
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
	 * Retrieves the unique identifier for this material.<br>
	 * This value corresponds to the {@code itemid} field.
	 * @return The integer ID of the item.
	 */
	public int getItemid()
	{
		return itemid;
	}
	
	/**
	 * Retrieves the calculated identifier for the name.<br>
	 * This value is derived from the {@code nameid} field.
	 * @return The calculated integer ID.
	 */
	public int getNameid()
	{
		return (nameid * 2) + 1;
	}
	
	/**
	 * Retrieves the gathering rate for this material.<br>
	 * This value determines how often an item is produced.
	 * @return The current {@code int} value of the rate.
	 */
	public int getRate()
	{
		return rate;
	}
	
	/**
	 * Compares this {@link Material} with another {@code Material} based on their rates.<br>
	 * This method is used to determine the sorting order of materials.
	 * @param o The other {@code Material} to compare against.
	 * @return A negative integer if this rate is higher, a positive integer if lower, or zero if they are equal.
	 */
	@Override
	public int compareTo(Material o)
	{
		return o.rate - rate;
	}
}
