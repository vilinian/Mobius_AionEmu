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

import com.aionemu.gameserver.model.templates.event.EventsWindow;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data structure for the events window.<br>
 * It serves as a container for {@link com.aionemu.gameserver.model.templates.event.EventsWindow} objects.<br>
 * It is used to map and manage event information within the game server.
 * @author Ghostfur (Aion-Unique)
 */
@XmlRootElement(name = "events_window")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class EventsWindowData
{
	@XmlElement(name = "event_window")
	private List<EventsWindow> events_window;
	
	@XmlTransient
	private final TIntObjectHashMap<EventsWindow> eventData = new TIntObjectHashMap<>();
	
	@XmlTransient
	private final Map<Integer, EventsWindow> eventDataMap = new HashMap<>(1);
	
	/**
	 * This method is called after the object is unmarshalled from XML.<br>
	 * It populates internal maps using data from the {@code events_window} list.
	 * @param unmarshaller The {@link Unmarshaller} used to read the data.
	 * @param object The {@code Object} that was just unmarshalled.
	 */
	void afterUnmarshal(Unmarshaller unmarshaller, Object object)
	{
		for (EventsWindow eventsWindow : events_window)
		{
			eventData.put(eventsWindow.getId(), eventsWindow);
			eventDataMap.put(eventsWindow.getId(), eventsWindow);
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return eventData.size();
	}
	
	/**
	 * Retrieves a specific {@link EventsWindow} based on its ID.<br>
	 * This method looks up the data in the internal map.
	 * @param EventData The unique integer ID of the event window.
	 * @return The corresponding {@code EventsWindow} object, or {@code null} if not found.
	 */
	public EventsWindow getEventWindowId(int EventData)
	{
		return eventData.get(EventData);
	}
	
	/**
	 * Retrieves all the events currently loaded in the system.<br>
	 * This method returns a map where the keys are unique IDs.
	 * @return A {@code Map<Integer, EventsWindow>} containing all event data.
	 */
	public Map<Integer, EventsWindow> getAllEvents()
	{
		return eventDataMap;
	}
}
