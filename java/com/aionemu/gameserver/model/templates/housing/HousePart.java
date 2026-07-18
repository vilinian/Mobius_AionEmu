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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.item.ItemQuality;

/**
 * Represents a specific component or structural element of a house.<br>
 * This class defines the properties and data for individual parts used in housing templates.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "house_part")
public class HousePart
{
	@XmlAttribute(name = "building_tags", required = true)
	private List<String> buildingTags;
	@XmlAttribute(required = true)
	protected PartType type;
	@XmlAttribute(required = true)
	protected ItemQuality quality;
	@XmlAttribute
	protected String name;
	@XmlAttribute(required = true)
	protected int id;
	@XmlTransient
	protected Set<String> tagsSet = new HashSet<>(1);
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code tagsSet} from the {@code buildingTags} list.<br>
	 * The {@code buildingTags} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (buildingTags == null)
		{
			return;
		}
		
		for (String tag : buildingTags)
		{
			tagsSet.add(tag);
		}
		
		buildingTags.clear();
		buildingTags = null;
	}
	
	/**
	 * Retrieves the {@code PartType} of this house part.<br>
	 * This method returns the specific category assigned to the object.
	 * @return The {@code PartType} of the current house part.
	 */
	public PartType getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the quality level of this house object.<br>
	 * This method calls {@code getQuality} on the underlying template.
	 * @return the {@code ItemQuality} of the object.
	 */
	public ItemQuality getQuality()
	{
		return quality;
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
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the set of tags associated with this house part.<br>
	 * This method returns the internal {@code tagsSet}.
	 * @return a {@code Set} containing all tag strings.
	 */
	public Set<String> getTags()
	{
		return tagsSet;
	}
	
	/**
	 * Checks if the current {@link HousePart} belongs to a specific {@link Building}.<br>
	 * It compares the part's tags against the building's match tag.
	 * @param building The {@link Building} object to check against.
	 * @return {@code true} if the part matches the building, otherwise {@code false}.
	 */
	public boolean isForBuilding(Building building)
	{
		return tagsSet.contains(building.getPartsMatchTag());
	}
}
