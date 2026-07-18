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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the different categories of armor types available in the game.<br>
 * This enumeration is used to classify items within the item system.
 * @author ATracer
 */
@XmlType(name = "armor_type")
@XmlEnum
public enum ArmorType
{
	NO_ARMOR(new int[] {}),
	CHAIN(new int[]
	{
		42,
		49
	}),
	CLOTHES(new int[]
	{
		40
	}),
	LEATHER(new int[]
	{
		41,
		48
	}),
	PLATE(new int[]
	{
		54
	}),
	ROBE(new int[]
	{
		103,
		106
	}),
	SHIELD(new int[]
	{
		43,
		50
	}),
	ARROW(new int[] {}),
	WING(new int[] {}),
	PLUME(new int[] {}),
	ACCESSORY(new int[] {}),
	GLYPH(new int[] {});
	
	private final int[] requiredSkills;
	
	/**
	 * Creates a new instance of {@link ArmorType}.<br>
	 * This constructor sets the skills needed for this armor type.
	 * @param requiredSkills An array of {@code int} values representing the skill requirements.
	 */
	private ArmorType(int[] requiredSkills)
	{
		this.requiredSkills = requiredSkills;
	}
	
	/**
	 * Retrieves the list of skills needed for this armor type.<br>
	 * This method returns the internal {@code requiredSkills} array.
	 * @return an {@code int[]} containing the skill requirements.
	 */
	public int[] getRequiredSkills()
	{
		return requiredSkills;
	}
	
	/**
	 * Retrieves the bitmask associated with this {@code ArmorType}.<br>
	 * This value is used for bitwise AND operations.<br>
	 * It helps identify types when multiple options are allowed.
	 * @return The integer mask for this type.
	 */
	public int getMask()
	{
		return 1 << ordinal();
	}
}
