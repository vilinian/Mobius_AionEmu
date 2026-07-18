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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;

/**
 * Represents the data template for a housing building.<br>
 * This class defines the properties and configurations for buildings within the housing system.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"parts"
})
@XmlRootElement(name = "building")
public class Building
{
	private Parts parts;
	@XmlAttribute(name = "default")
	protected boolean isDefault;
	@XmlAttribute(name = "parts_match")
	protected String partsMatch;
	@XmlAttribute
	protected String size;
	@XmlAttribute
	protected BuildingType type;
	@XmlAttribute(required = true)
	protected int id;
	
	/**
	 * Checks if this building is set as the default.<br>
	 * Returns {@code true} if it is a default building.<br>
	 * Returns {@code false} otherwise.
	 * @return The default status of the building.
	 */
	public boolean isDefault()
	{
		return isDefault;
	}
	
	@XmlTransient
	Map<PartType, Integer> partsByType = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code partsByType} map using the values from the {@code parts} object.<br>
	 * The method checks if each part type is non-null or non-zero before adding it to the map.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (parts == null)
		{
			return;
		}
		
		if (parts.getDoor() != 0)
		{
			partsByType.put(PartType.DOOR, parts.getDoor());
		}
		
		if (parts.getFence() != null)
		{
			partsByType.put(PartType.FENCE, parts.getFence());
		}
		
		if (parts.getFrame() != null)
		{
			partsByType.put(PartType.FRAME, parts.getFrame());
		}
		
		if (parts.getGarden() != null)
		{
			partsByType.put(PartType.GARDEN, parts.getGarden());
		}
		
		if (parts.getInfloor() != 0)
		{
			partsByType.put(PartType.INFLOOR_ANY, parts.getInfloor());
		}
		
		if (parts.getInwall() != 0)
		{
			partsByType.put(PartType.INWALL_ANY, parts.getInwall());
		}
		
		if (parts.getOutwall() != null)
		{
			partsByType.put(PartType.OUTWALL, parts.getOutwall());
		}
		
		if (parts.getRoof() != null)
		{
			partsByType.put(PartType.ROOF, parts.getRoof());
		}
	}
	
	// All DataManager methods ensure integrity when called from housing land templates because they only contain ID and isDefault values for buildings, whereas building templates contain full information except for the land's isDefault value.
	/**
	 * Retrieves the tag used to match building parts.<br>
	 * It returns the {@code partsMatch} value if it is not empty.<br>
	 * If {@code partsMatch} is null or empty, it fetches the tag from the global data manager.
	 * @return The matching tag as a {@code String}.
	 */
	public String getPartsMatchTag()
	{
		if ((partsMatch == null) || partsMatch.isEmpty())
		{
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getPartsMatchTag();
		}
		
		return partsMatch;
	}
	
	/**
	 * Retrieves the size of the building.<br>
	 * It returns the {@code size} field if it is not empty.<br>
	 * If the field is empty, it fetches the size from {@link DataManager}.
	 * @return The size as a {@code String}.
	 */
	public String getSize()
	{
		if ((size == null) || size.isEmpty())
		{
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getSize();
		}
		
		return size;
	}
	
	/**
	 * Retrieves the {@code BuildingType} of this building.<br>
	 * If the local {@code type} is {@code null}, it fetches the type from the {@link DataManager}.
	 * @return The {@code BuildingType} associated with this building.
	 */
	public BuildingType getType()
	{
		if (type == null)
		{
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getType();
		}
		
		return type;
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
	 * Retrieves the default ID for a specific part type.<br>
	 * This method looks up the building data using the current {@code id}.<br>
	 * It returns the value associated with the provided {@code partType}.
	 * @param partType The type of the part to look up.
	 * @return The default ID as an {@code Integer}, or {@code null} if not found.
	 */
	public Integer getDefaultPartId(PartType partType)
	{
		return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).partsByType.get(partType);
	}
}
