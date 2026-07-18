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
 * Represents the different quality levels that an item can have.<br>
 * This enum is used to categorize items based on their rarity and stats.
 * @author ATracer
 */
@XmlType(name = "quality")
@XmlEnum
public enum ItemQuality
{
	// TODO: Reorder and rename - requires ATracer parser update (?)
	
	JUNK(0), // Junk - Gray
	COMMON(1), // Common - White
	RARE(2), // Superior - Green
	LEGEND(3), // Heroic - Blue
	UNIQUE(4), // Fabled - Yellow
	EPIC(5), // Eternal - Orange
	MYTHIC(6), // Test - Purple
	ANCIENT(7), // Test - Light Yellow
	RELIC(8), // Test - Pink
	FINALITY(9); // Test - ??
	
	private final int qualityId;
	
	/**
	 * Creates a new {@link ItemQuality} instance.<br>
	 * This constructor assigns the internal ID to the object.
	 * @param qualityId The unique integer identifier for the quality type.
	 */
	private ItemQuality(int qualityId)
	{
		this.qualityId = qualityId;
	}
	
	/**
	 * Retrieves the unique identifier for this item quality.<br>
	 * This value corresponds to the internal ID used by the game engine.
	 * @return The {@code int} value of the quality ID.
	 */
	public int getQualityId()
	{
		return qualityId;
	}
}
