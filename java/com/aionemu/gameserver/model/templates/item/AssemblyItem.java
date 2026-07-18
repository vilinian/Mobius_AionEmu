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
package com.aionemu.gameserver.model.templates.item;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents an item that is part of a larger assembly or crafting recipe.<br>
 * This class defines the properties for components used in complex item construction.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssemblyItem")
public class AssemblyItem
{
	@XmlAttribute(required = true)
	protected int id;
	
	@XmlAttribute(name = "parts_num")
	protected int partsNum;
	
	@XmlAttribute(name = "proc_assembly")
	protected int procAssembly;
	
	@XmlAttribute(required = true)
	protected List<Integer> parts;
	
	/**
	 * Retrieves the list of part IDs for this assembly item.<br>
	 * If the internal list is {@code null}, it initializes a new {@code ArrayList}.
	 * @return A {@code List<Integer>} containing the parts.
	 */
	public List<Integer> getParts()
	{
		if (parts == null)
		{
			parts = new ArrayList<>();
		}
		
		return parts;
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
	 * Sets the unique identifier for this {@link AssemblyItem}.<br>
	 * This updates the internal {@code id} field.
	 * @param value The new integer ID to assign.
	 */
	public void setId(int value)
	{
		id = value;
	}
	
	/**
	 * Retrieves the total number of parts for this assembly item.<br>
	 * This value corresponds to the {@code parts_num} attribute.
	 * @return The count of parts as an {@code int}.
	 */
	public int getPartsNum()
	{
		return partsNum;
	}
	
	/**
	 * Sets the number of parts for this assembly item.<br>
	 * This updates the {@code partsNum} field.
	 * @param value The new number of parts to set.
	 */
	public void setPartsNum(int value)
	{
		partsNum = value;
	}
	
	/**
	 * Retrieves the processing assembly value for this item.<br>
	 * This value is stored in the {@code proc_assembly} attribute.
	 * @return The integer value of the processing assembly.
	 */
	public int getProcAssembly()
	{
		return procAssembly;
	}
	
	/**
	 * Sets the assembly processing value.<br>
	 * This updates the {@code procAssembly} field of this object.
	 * @param procAssembly The new integer value to set for the assembly process.
	 */
	public void setProcAssembly(int procAssembly)
	{
		this.procAssembly = procAssembly;
	}
}
