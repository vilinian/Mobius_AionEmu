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
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.pet.PetDopingEntry;

import gnu.trove.map.hash.TShortObjectHashMap;

/**
 * This class holds the configuration data for pet doping items.<br>
 * It maps {@code PetDopingEntry} objects to their respective identifiers.<br>
 * Use this class to access all registered doping information within the game server.
 * @author Rolandas
 */
@XmlRootElement(name = "dopings")
@XmlAccessorType(XmlAccessType.FIELD)
public class PetDopingData
{
	@XmlElement(name = "doping")
	private List<PetDopingEntry> list;
	@XmlTransient
	private final TShortObjectHashMap<PetDopingEntry> dopingsById = new TShortObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code dopingsById} map using the list of {@link PetDopingEntry} objects.<br>
	 * The {@code list} is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (PetDopingEntry dope : list)
		{
			dopingsById.put(dope.getId(), dope);
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
		return dopingsById.size();
	}
	
	/**
	 * Retrieves a specific pet doping template from the data map.<br>
	 * This method uses the provided unique identifier to find the entry.
	 * @param id The {@code short} ID of the doping template to retrieve.
	 * @return The corresponding {@link PetDopingEntry} object, or {@code null} if not found.
	 */
	public PetDopingEntry getDopingTemplate(short id)
	{
		return dopingsById.get(id);
	}
}
