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

import com.aionemu.gameserver.model.templates.atreianpassport.AtreianPassportTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link AtreianPassportTemplate} objects.<br>
 * It manages the collection of all available atreian passports within the game server.<br>
 * It is used to map and retrieve passport information from the configuration files.
 * @author Falke_34
 */
@XmlRootElement(name = "atreian_passports")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtreianPassportData
{
	@XmlElement(name = "atreian_passport")
	private List<AtreianPassportTemplate> tlist;
	
	@XmlTransient
	private final TIntObjectHashMap<AtreianPassportTemplate> passportData = new TIntObjectHashMap<>();
	
	@XmlTransient
	private final Map<Integer, AtreianPassportTemplate> passportDataMap = new HashMap<>(1);
	
	/**
	 * This method is called after the XML data is unmarshalled.<br>
	 * It populates internal maps using the list of {@link AtreianPassportTemplate} objects.
	 * @param paramUnmarshaller The {@code Unmarshaller} used to read the data.
	 * @param paramObject The object that was just unmarshalled.
	 */
	void afterUnmarshal(Unmarshaller paramUnmarshaller, Object paramObject)
	{
		for (AtreianPassportTemplate id : tlist)
		{
			passportData.put(id.getId(), id);
			passportDataMap.put(id.getId(), id);
		}
	}
	
	/**
	 * Returns the total number of passports stored in this data holder.<br>
	 * This method calls {@code size} to get the count.
	 * @return The number of items currently in the collection.
	 */
	public int size()
	{
		return passportData.size();
	}
	
	/**
	 * Retrieves a specific passport template from the data map.<br>
	 * This method uses the provided {@code id} to find the matching object.
	 * @param id The unique identifier for the passport.
	 * @return The {@link AtreianPassportTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public AtreianPassportTemplate getAtreianPassportId(int id)
	{
		return passportData.get(id);
	}
	
	/**
	 * Retrieves all available passport templates.<br>
	 * This method returns the internal map of data.
	 * @return a {@code Map} containing all {@link AtreianPassportTemplate} objects indexed by their IDs.
	 */
	public Map<Integer, AtreianPassportTemplate> getAll()
	{
		return passportDataMap;
	}
}
