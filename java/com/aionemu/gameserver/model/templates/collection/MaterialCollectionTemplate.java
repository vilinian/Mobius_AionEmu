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
package com.aionemu.gameserver.model.templates.collection;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the template data for a material collection.<br>
 * This class defines the properties and requirements for collecting specific materials in the game.<br>
 * It serves as a blueprint for {@code MaterialCollection}.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "MaterialCollectionTemplate")
public class MaterialCollectionTemplate
{
	@XmlAttribute(name = "items")
	protected List<Integer> items;
	@XmlAttribute(name = "count")
	protected int count;
	@XmlAttribute(name = "enchant")
	protected int enchant;
	
	/**
	 * Retrieves the list of item IDs from this template.<br>
	 * This method returns the {@code items} field.
	 * @return a {@code List<Integer>} containing the item identifiers.
	 */
	public List<Integer> getItems()
	{
		return items;
	}
	
	/**
	 * Retrieves the current count value.<br>
	 * This method returns the integer stored in the {@code count} field.
	 * @return The current count as an {@code int}.
	 */
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Retrieves the enchantment level of the material collection.<br>
	 * This value is stored in the {@code enchant} field.
	 * @return The current enchantment level as an {@code int}.
	 */
	public int getEnchant()
	{
		return enchant;
	}
}
