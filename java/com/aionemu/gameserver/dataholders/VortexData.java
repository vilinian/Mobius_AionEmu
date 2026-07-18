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

import com.aionemu.gameserver.model.templates.vortex.VortexTemplate;
import com.aionemu.gameserver.model.vortex.VortexLocation;

/**
 * This class serves as a data holder for dimensional vortex information.<br>
 * It maps XML data to {@link VortexTemplate} and {@link VortexLocation} objects.
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dimensional_vortex")
public class VortexData
{
	@XmlElement(name = "vortex_location")
	private List<VortexTemplate> vortexTemplates;
	@XmlTransient
	private final Map<Integer, VortexLocation> vortex = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code vortex} map using the list of {@link VortexTemplate} templates.<br>
	 * The {@code vortex} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (VortexTemplate template : vortexTemplates)
		{
			vortex.put(template.getId(), new VortexLocation(template));
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return vortex.size();
	}
	
	/**
	 * Retrieves the {@link VortexLocation} for a specific world.<br>
	 * It searches through all available locations based on the provided ID.
	 * @param invasionWorldId The unique identifier for the invasion world.
	 * @return The matching {@code VortexLocation} object, or {@code null} if no match is found.
	 */
	public VortexLocation getVortexLocation(int invasionWorldId)
	{
		for (VortexLocation loc : vortex.values())
		{
			if (loc.getInvasionWorldId() == invasionWorldId)
			{
				return loc;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves all registered vortex locations.<br>
	 * This method returns a map where the key is an {@code Integer} ID.<br>
	 * The values are instances of {@link VortexLocation}.
	 * @return A {@code FastMap} containing all {@code VortexLocation} objects.
	 */
	public Map<Integer, VortexLocation> getVortexLocations()
	{
		return vortex;
	}
}
