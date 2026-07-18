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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.pet.PetFlavour;

/**
 * This class holds the data for pet food items within the game.<br>
 * It serves as a data container for mapping food types to their specific properties.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"flavours"
})
@XmlRootElement(name = "pet_feed")
public class PetFeedData
{
	@XmlElement(name = "flavour")
	protected List<PetFlavour> flavours;
	@XmlTransient
	private final Map<Integer, PetFlavour> petFlavoursById = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code petFlavoursById} map using the list of {@link PetFlavour} objects.<br>
	 * The {@code flavours} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (flavours == null)
		{
			return;
		}
		
		for (PetFlavour flavour : flavours)
		{
			petFlavoursById.put(flavour.getId(), flavour);
		}
		
		flavours.clear();
		flavours = null;
	}
	
	/**
	 * Retrieves a specific {@link PetFlavour} based on its unique ID.<br>
	 * This method looks up the flavor in the internal data map.
	 * @param flavourId The unique integer identifier for the pet flavor.
	 * @return The corresponding {@code PetFlavour} object, or {@code null} if not found.
	 */
	public PetFlavour getFlavourById(int flavourId)
	{
		return petFlavoursById.get(flavourId);
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return petFlavoursById.size();
	}
	
	/**
	 * Retrieves all available {@link PetFlavour} objects.<br>
	 * This method returns the values stored in the internal map.
	 * @return an array of {@code PetFlavour} objects.
	 */
	public PetFlavour[] getPetFlavours()
	{
		return petFlavoursById.values().toArray(new PetFlavour[0]);
	}
}
