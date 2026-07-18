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
package com.aionemu.gameserver.dataholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.dynamicportal.DynamicPortalLocation;
import com.aionemu.gameserver.model.templates.dynamicportal.DynamicPortalTemplate;

/**
 * This class holds the data for dynamic portals within the game world.<br>
 * It serves as a data container that maps {@link DynamicPortalTemplate} information to specific locations.<br>
 * Use this class to manage and retrieve active portal instances.
 * @author Falke_34
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dynamic_rift")
public class DynamicPortalData
{
	@XmlElement(name = "dynamic_location")
	private List<DynamicPortalTemplate> dynamicPortalTemplates;
	
	@XmlTransient
	private final Map<Integer, DynamicPortalLocation> dynamicPortal = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code dynamicPortal} map using the list of {@link DynamicPortalTemplate} templates.<br>
	 * The {@code dynamicPortal} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (DynamicPortalTemplate template : dynamicPortalTemplates)
		{
			dynamicPortal.put(template.getId(), new DynamicPortalLocation(template));
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return dynamicPortal.size();
	}
	
	/**
	 * Retrieves the map of all active dynamic portal locations.<br>
	 * The keys in this {@code FastMap} represent unique identifiers.
	 * @return A {@code FastMap} containing {@link DynamicPortalLocation} objects.
	 */
	public Map<Integer, DynamicPortalLocation> getDynamicPortalLocations()
	{
		return dynamicPortal;
	}
}
