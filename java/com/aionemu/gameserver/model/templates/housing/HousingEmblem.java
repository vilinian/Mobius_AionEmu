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
package com.aionemu.gameserver.model.templates.housing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a decorative emblem used within the housing system.<br>
 * This class defines the properties for an emblem that can be placed as a {@link PlaceableHouseObject}.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HousingEmblem")
public class HousingEmblem extends PlaceableHouseObject
{
	@XmlAttribute(name = "level", required = true)
	private int level;
	
	/**
	 * Retrieves the unique identifier for this object type.<br>
	 * This value is used to identify the housing emblem in the game world.
	 * @return The {@code byte} ID of the housing emblem, which is always {@code 11}.
	 */
	@Override
	public byte getTypeId()
	{
		return 11;
	}
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
}
