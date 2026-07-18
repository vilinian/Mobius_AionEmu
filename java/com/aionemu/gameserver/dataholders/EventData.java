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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.event.EventTemplate;

import gnu.trove.map.hash.THashMap;

/**
 * <p/>
 * Java class for EventData complex type.
 * <p/>
 * The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * 
 * <pre>
 * &lt;complexType name="EventData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="event" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{}EventTemplate">
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventData", propOrder =
{
	"active",
	"events"
})
@XmlRootElement(name = "events_config")
public class EventData
{
	@XmlElement(required = true)
	protected String active;
	@XmlElementWrapper(name = "events")
	@XmlElement(name = "event")
	protected List<EventTemplate> events;
	@XmlTransient
	private final THashMap<String, EventTemplate> activeEvents = new THashMap<>();
	@XmlTransient
	private final THashMap<String, EventTemplate> allEvents = new THashMap<>();
	@XmlTransient
	private int counter = 0;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code activeEvents} and {@code allEvents} collections based on the {@code active} string.<br>
	 * The lists are cleared before being rebuilt from the provided {@code events}.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if ((active == null) || (events == null))
		{
			return;
		}
		
		counter = 0;
		allEvents.clear();
		activeEvents.clear();
		
		final Set<String> ae = new HashSet<>();
		Collections.addAll(ae, active.split(";"));
		
		for (EventTemplate ev : events)
		{
			if (ae.contains(ev.getName()) && ev.isActive())
			{
				activeEvents.put(ev.getName(), ev);
				counter++;
			}
			
			allEvents.put(ev.getName(), ev);
		}
		
		events.clear();
		events = null;
		active = null;
	}
	
	/**
	 * Returns the total number of events.<br>
	 * This value is retrieved from the internal {@code counter}.
	 * @return The current count of items.
	 */
	public int size()
	{
		return counter;
	}
	
	/**
	 * Retrieves the text for the currently active event.<br>
	 * This value is stored in the {@code active} field.
	 * @return The string representing the active event.
	 */
	public String getActiveText()
	{
		return active;
	}
	
	/**
	 * Retrieves a list of all available event templates.<br>
	 * This method returns a copy of the internal {@code allEvents} collection.<br>
	 * It ensures thread safety by using a {@code synchronized} block.
	 * @return A {@code List} containing all {@link EventTemplate} objects.
	 */
	public List<EventTemplate> getAllEvents()
	{
		final List<EventTemplate> result = new ArrayList<>();
		synchronized (allEvents)
		{
			result.addAll(allEvents.values());
		}
		
		return result;
	}
	
	/**
	 * Updates the list of events and the active status.<br>
	 * This method sets the internal event list and updates the {@code active} state.<br>
	 * It also synchronizes the start status for existing events in the system.
	 * @param events The list of {@link EventTemplate} objects to set.
	 * @param active The new active status string.
	 */
	public void setAllEvents(List<EventTemplate> events, String active)
	{
		if (events == null)
		{
			events = new ArrayList<>();
		}
		
		this.events = events;
		this.active = active;
		
		for (EventTemplate et : this.events)
		{
			if (allEvents.containsKey(et.getName()))
			{
				final EventTemplate oldEvent = allEvents.get(et.getName());
				if (oldEvent.isActive() && oldEvent.isStarted())
				{
					et.setStarted();
				}
			}
		}
		
		afterUnmarshal(null, null);
	}
	
	/**
	 * Retrieves a list of all currently active events.<br>
	 * This method returns a copy of the internal {@code activeEvents} collection.
	 * @return A {@code List} containing all {@link EventTemplate} objects that are active.
	 */
	public List<EventTemplate> getActiveEvents()
	{
		final List<EventTemplate> result = new ArrayList<>();
		synchronized (activeEvents)
		{
			result.addAll(activeEvents.values());
		}
		
		return result;
	}
	
	/**
	 * Checks if a specific event is currently active.<br>
	 * This method looks for the {@code eventName} within the active events list.
	 * @param eventName The name of the event to search for.
	 * @return {@code true} if the event exists in the active set, otherwise {@code false}.
	 */
	public boolean Contains(String eventName)
	{
		return activeEvents.containsKey(eventName);
	}
}
