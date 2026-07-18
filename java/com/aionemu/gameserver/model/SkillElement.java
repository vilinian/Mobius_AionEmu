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
package com.aionemu.gameserver.model;

import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * Represents the different elemental types associated with skills.<br>
 * This enum is used to determine how skills interact with specific attributes or effects.
 * @author xavier
 */
public enum SkillElement
{
	NONE(0),
	FIRE(1),
	WATER(2),
	WIND(3),
	EARTH(4),
	LIGHT(5),
	DARK(6);
	
	private final int element;
	
	/**
	 * Creates a new instance of {@link SkillElement}.<br>
	 * This constructor assigns the internal ID to the element.
	 * @param id The unique integer identifier for the element.
	 */
	private SkillElement(int id)
	{
		element = id;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link PetitionStatus}.<br>
	 * This value is used to map the status to its database representation.
	 * @return The integer ID of the status.
	 */
	public int getElementId()
	{
		return element;
	}
	
	/**
	 * This method retrieves the correct {@link StatEnum} for a specific element.<br>
	 * It maps each {@code SkillElement} to its corresponding resistance type.<br>
	 * Use this to determine which stat to check when an elemental skill is used.
	 * @param element The {@code SkillElement} to look up.
	 * @return The matching {@link StatEnum} or {@code null} if no match is found.
	 */
	public static StatEnum getResistanceForElement(SkillElement element)
	{
		switch (element)
		{
			case FIRE:
				return StatEnum.FIRE_RESISTANCE;
			case WATER:
				return StatEnum.WATER_RESISTANCE;
			case WIND:
				return StatEnum.WIND_RESISTANCE;
			case EARTH:
				return StatEnum.EARTH_RESISTANCE;
			case LIGHT:
				return StatEnum.ELEMENTAL_RESISTANCE_LIGHT;
			case DARK:
				return StatEnum.ELEMENTAL_RESISTANCE_DARK;
			default:
				break;
		}
		
		return null;
		
	}
}
