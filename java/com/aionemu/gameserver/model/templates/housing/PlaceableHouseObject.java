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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a decorative or functional object that can be placed within a house.<br>
 * This class defines the properties for items used in the housing system.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PlaceableHouseObject")
@XmlSeeAlso(
{
	HousingJukeBox.class,
	HousingPicture.class,
	HousingPostbox.class,
	HousingChair.class,
	HousingStorage.class,
	HousingNpc.class,
	HousingMoveableItem.class,
	HousingUseableItem.class,
	HousingPassiveItem.class,
	HousingEmblem.class
})
public abstract class PlaceableHouseObject extends AbstractHouseObject
{
	@XmlAttribute(name = "use_days")
	protected Integer useDays;
	@XmlAttribute
	protected LimitType limit;
	@XmlAttribute
	protected PlaceLocation location;
	@XmlAttribute
	protected PlaceArea area;
	
	/**
	 * Retrieves the number of days an object can be used.<br>
	 * Returns {@code 0} if there is no limit set.
	 * @return The number of allowed use days.
	 */
	public int getUseDays()
	{
		if (useDays == null)
		{
			return 0;
		}
		
		return useDays;
	}
	
	/**
	 * Retrieves the placement limit for this house object.<br>
	 * It returns {@code LimitType.NONE} if no specific limit is set.
	 * @return the current {@link LimitType} of the object.
	 */
	public LimitType getPlacementLimit()
	{
		if (limit == null)
		{
			return LimitType.NONE;
		}
		
		return limit;
	}
	
	/**
	 * Retrieves the current location of this house object.<br>
	 * This method returns a {@link PlaceLocation} object.
	 * @return the {@code location} of the object.
	 */
	public PlaceLocation getLocation()
	{
		return location;
	}
	
	/**
	 * Retrieves the {@code PlaceArea} associated with this object.<br>
	 * This method returns the specific area where the house object is located.
	 * @return the {@code PlaceArea} of the object, or {@code null} if no area is assigned.
	 */
	public PlaceArea getArea()
	{
		return area;
	}
	
	public abstract byte getTypeId();
}
