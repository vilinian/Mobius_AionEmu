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

import com.aionemu.gameserver.model.templates.event.BoostEvents;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the configuration data for boost events.<br>
 * It serves as a data container for {@link BoostEvents} information.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "boost_events")
public class BoostEventData
{
	@XmlElement(name = "boost_event")
	protected List<BoostEvents> bonusServiceBonusattr;
	@XmlTransient
	private final TIntObjectHashMap<BoostEvents> templates = new TIntObjectHashMap<>();
	@XmlTransient
	private final Map<Integer, BoostEvents> templatesMap = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code templates} and {@code templatesMap} using the list of {@link BoostEvents}.<br>
	 * The {@code bonusServiceBonusattr} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (BoostEvents boostEvents : bonusServiceBonusattr)
		{
			templates.put(boostEvents.getId(), (boostEvents));
			templatesMap.put(boostEvents.getId(), boostEvents);
		}
		
		bonusServiceBonusattr.clear();
		bonusServiceBonusattr = null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return templates.size();
	}
	
	/**
	 * Retrieves the {@code BoostEvents} object for a specific buffer ID.<br>
	 * This method looks up the data in the internal template map.
	 * @param buffId The unique identifier of the buff to look up.
	 * @return The {@code BoostEvents} associated with the given ID, or {@code null} if not found.
	 */
	public BoostEvents getInstanceBonusattr(int buffId)
	{
		return templates.get(buffId);
	}
	
	/**
	 * Retrieves all boost events from the data holder.<br>
	 * This method returns the internal map of {@code BoostEvents}.
	 * @return A {@code Map} where the key is an {@code Integer} ID and the value is a {@link BoostEvents} object.
	 */
	public Map<Integer, BoostEvents> getAll()
	{
		return templatesMap;
	}
}
