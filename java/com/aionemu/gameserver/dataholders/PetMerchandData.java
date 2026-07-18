/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 * <p/>
 * Aion-Lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * Aion-Lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. *
 * You should have received a copy of the GNU General Public License
 * along with Aion-Lightning.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.dataholders;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.pet.PetMerchandEntry;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for pet merchants within the game world.<br>
 * It maps merchant IDs to their corresponding {@link PetMerchandEntry} objects.<br>
 * Use this class to manage and retrieve information about where pets can be purchased.
 * @author Ace on 01/08/2016
 */
@XmlRootElement(name = "merchants")
@XmlAccessorType(XmlAccessType.FIELD)
public class PetMerchandData
{
	@XmlElement(name = "merchant")
	private List<PetMerchandEntry> list;
	
	@XmlTransient
	private final TIntObjectHashMap<PetMerchandEntry> merchandsById = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code merchandsById} map using the list of {@link PetMerchandEntry} objects.<br>
	 * The {@code list} is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (PetMerchandEntry merch : list)
		{
			merchandsById.put(merch.getId(), merch);
		}
		
		list.clear();
		list = null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return merchandsById.size();
	}
	
	/**
	 * Retrieves a specific pet merchant template from the data map.<br>
	 * This method uses the provided {@code id} to find the matching entry.
	 * @param id The unique identifier of the merchant template.
	 * @return The {@link PetMerchandEntry} associated with the given {@code id}, or {@code null} if not found.
	 */
	public PetMerchandEntry getMerchandTemplate(int id)
	{
		return merchandsById.get(id);
	}
}
