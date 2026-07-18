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

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.pet.PetTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a container for all {@link PetTemplate} instances.<br>
 * It provides centralized access to pet data throughout the server.
 * @author IlBuono
 */
@XmlRootElement(name = "pets")
@XmlAccessorType(XmlAccessType.FIELD)
public class PetData
{
	@XmlElement(name = "pet")
	private List<PetTemplate> pets;
	/**
	 * A map containing all pet templates
	 */
	private final TIntObjectHashMap<PetTemplate> petData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code petData} map using the list of {@link PetTemplate} objects.<br>
	 * The {@code pets} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (PetTemplate pet : pets)
		{
			petData.put(pet.getId(), pet);
		}
		
		pets.clear();
		pets = null;
	}
	
	/**
	 * Returns the total number of {@link PetTemplate} objects stored in this container.<br>
	 * This method calls {@code petData.size()} to retrieve the count.
	 * @return The number of pets currently loaded.
	 */
	public int size()
	{
		return petData.size();
	}
	
	/**
	 * Retrieves a specific {@link PetTemplate} from the data map.<br>
	 * This method uses the provided unique identifier to find the template.
	 * @param id The unique integer ID of the pet template.
	 * @return The {@code PetTemplate} associated with the given ID, or {@code null} if not found.
	 */
	public PetTemplate getPetTemplate(int id)
	{
		return petData.get(id);
	}
}
