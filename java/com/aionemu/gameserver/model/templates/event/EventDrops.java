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
package com.aionemu.gameserver.model.templates.event;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the loot tables associated with a specific game event.<br>
 * It defines which items can be dropped and their respective probabilities during an event.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventDrops")
public class EventDrops
{
	@XmlElement(name = "event_drop")
	protected List<EventDrop> eventDrops;
	
	/**
	 * Retrieves the list of {@link EventDrop} objects.<br>
	 * If the internal list is {@code null}, it returns a new empty {@code ArrayList}.
	 * @return A {@code List} of all event drops.
	 */
	public List<EventDrop> getEventDrops()
	{
		if (eventDrops == null)
		{
			eventDrops = new ArrayList<>();
		}
		
		return eventDrops;
	}
}
