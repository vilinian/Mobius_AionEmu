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
package com.aionemu.gameserver.model.templates.spawns;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class manages the configuration for house-related spawn points.<br>
 * It defines where specific entities or objects appear within residential areas.<br>
 * Use this class to handle {@link com.aionemu.gameserver.model.templates.spawns.Spawn} data for houses.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"spawns"
})
@XmlRootElement(name = "house")
public class HouseSpawns implements Comparable<HouseSpawns>
{
	@XmlElement(name = "spawn", required = true)
	protected List<HouseSpawn> spawns;
	@XmlAttribute(name = "address", required = true)
	protected int address;
	
	/**
	 * Retrieves the list of {@link HouseSpawn} objects for this house.<br>
	 * If the internal list is null, it initializes a new {@code ArrayList}.
	 * @return A {@code List} of {@link HouseSpawn} objects.
	 */
	public List<HouseSpawn> getSpawns()
	{
		if (spawns == null)
		{
			spawns = new ArrayList<>();
		}
		
		return spawns;
	}
	
	/**
	 * Retrieves the unique address of the house bid entry.<br>
	 * This value is used to identify the specific location in the game world.
	 * @return The {@code int} representing the house address.
	 */
	public int getAddress()
	{
		return address;
	}
	
	/**
	 * Sets the unique address for this house.<br>
	 * This updates the {@code address} field.
	 * @param value The new integer value to assign to the address.
	 */
	public void setAddress(int value)
	{
		address = value;
	}
	
	/**
	 * Compares this {@link HouseSpawns} object with another one.<br>
	 * It uses the {@code address} field to determine the order.
	 * @param o The other {@code HouseSpawns} object to compare against.
	 * @return A negative integer if this is greater, zero if equal, or a positive integer if smaller.
	 */
	@Override
	public int compareTo(HouseSpawns o)
	{
		return o.address - address;
	}
}
