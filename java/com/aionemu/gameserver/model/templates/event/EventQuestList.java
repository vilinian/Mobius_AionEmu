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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a collection of {@code EventQuest} objects.<br>
 * It serves as a data model for managing multiple event quests within the game server.
 * @author Rolandas
 */
@XmlType(name = "EventQuestList", propOrder =
{
	"startable",
	"maintainable"
})
@XmlAccessorType(XmlAccessType.FIELD)
public class EventQuestList
{
	protected String startable;
	protected String maintainable;
	@XmlTransient
	private List<Integer> startQuests;
	@XmlTransient
	private List<Integer> maintainQuests;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code startQuests} and {@code maintainQuests} lists.<br>
	 * The values are parsed from the {@code startable} and {@code maintainable} strings.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (startable != null)
		{
			startQuests = getQuestsFromData(startable);
		}
		
		if (maintainable != null)
		{
			maintainQuests = getQuestsFromData(maintainable);
		}
	}
	
	/**
	 * Parses a string of quest IDs into a list.<br>
	 * The input string should contain IDs separated by semicolons.<br>
	 * This method returns an empty {@code List<Integer>} if the data is empty.
	 * @param data A semicolon-separated string of quest IDs.
	 * @return A {@code List<Integer>} containing the parsed quest IDs.
	 */
	List<Integer> getQuestsFromData(String data)
	{
		final Set<String> q = new HashSet<>();
		Collections.addAll(q, data.split(";"));
		List<Integer> result = new ArrayList<>();
		
		if (q.size() > 0)
		{
			result = new ArrayList<>();
			final Iterator<String> it = q.iterator();
			while (it.hasNext())
			{
				result.add(Integer.parseInt(it.next()));
			}
		}
		
		return result;
	}
	
	/**
	 * Retrieves the list of quests that can be started.<br>
	 * This method ensures the internal {@code startQuests} list is initialized.
	 * @return a {@code List<Integer>} containing the IDs of all startable quests.
	 */
	public List<Integer> getStartableQuests()
	{
		if (startQuests == null)
		{
			startQuests = new ArrayList<>();
		}
		
		return startQuests;
	}
	
	/**
	 * Retrieves the list of quests that can be maintained.<br>
	 * This method ensures the {@code maintainQuests} list is initialized.
	 * @return a {@code List<Integer>} containing the IDs of maintainable quests.
	 */
	public List<Integer> getMaintainQuests()
	{
		if (maintainQuests == null)
		{
			maintainQuests = new ArrayList<>();
		}
		
		return maintainQuests;
	}
}
