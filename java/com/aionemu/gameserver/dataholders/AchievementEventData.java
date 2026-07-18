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

import com.aionemu.gameserver.model.templates.achievement.AchievementEventTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link AchievementEventTemplate} objects.<br>
 * It is used to manage and store achievement event configurations within the game server.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement(name = "achievement_event_templates")
public class AchievementEventData
{
	@XmlElement(name = "achievement_event_template", required = true)
	protected List<AchievementEventTemplate> achievements;
	@XmlTransient
	private final TIntObjectHashMap<AchievementEventTemplate> custom = new TIntObjectHashMap<>();
	
	/**
	 * Retrieves a specific achievement template using its unique identifier.<br>
	 * This method looks up the ID in the {@code custom} map.
	 * @param id The unique integer ID of the achievement to find.
	 * @return The {@link AchievementEventTemplate} associated with the provided ID, or {@code null} if not found.
	 */
	public AchievementEventTemplate getAchievementId(int id)
	{
		return custom.get(id);
	}
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code custom} map using the list of {@link AchievementEventTemplate} objects.<br>
	 * The {@code custom} map is updated with each template from the achievements list.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (AchievementEventTemplate it : achievements)
		{
			getCustomMap().put(it.getId(), it);
		}
	}
	
	/**
	 * Retrieves the internal map of custom achievement templates.<br>
	 * This method provides access to the {@code custom} collection.
	 * @return a {@link TIntObjectHashMap} containing the custom {@link AchievementEventTemplate} objects.
	 */
	private TIntObjectHashMap<AchievementEventTemplate> getCustomMap()
	{
		return custom;
	}
	
	/**
	 * Returns the number of elements in the custom map.<br>
	 * This method calls {@code getCustomMap} to retrieve the internal collection.
	 * @return The total count of custom achievement action templates.
	 */
	public int size()
	{
		return custom.size();
	}
	
	/**
	 * Retrieves the list of all achievement event templates.<br>
	 * This method returns the internal {@code achievements} collection.
	 * @return a {@code List} containing all {@link AchievementEventTemplate} objects.
	 */
	public List<AchievementEventTemplate> getAchievements()
	{
		return achievements;
	}
}
