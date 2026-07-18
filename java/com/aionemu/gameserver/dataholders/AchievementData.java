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
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.model.templates.achievement.AchievementTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link AchievementTemplate} objects.<br>
 * It manages the collection of all available achievements within the game server.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement(name = "achievement_templates")
public class AchievementData
{
	@XmlElement(name = "achievement_template", required = true)
	protected List<AchievementTemplate> achievements;
	@XmlTransient
	List<AchievementTemplate> daily = new ArrayList<>();
	@XmlTransient
	List<AchievementTemplate> weekly = new ArrayList<>();
	@XmlTransient
	private final TIntObjectHashMap<AchievementTemplate> custom = new TIntObjectHashMap<>();
	
	/**
	 * Retrieves a specific achievement template from the custom collection.<br>
	 * This method looks up the template using the provided {@code id}.
	 * @param id The unique identifier for the achievement.
	 * @return The {@link AchievementTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public AchievementTemplate getAchievementId(int id)
	{
		return custom.get(id);
	}
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code custom} map using the list of {@link AchievementTemplate} objects.<br>
	 * It also sorts the templates into {@code daily} and {@code weekly} lists based on their type.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (AchievementTemplate it : achievements)
		{
			getCustomMap().put(it.getId(), it);
			if (it.getType() == AchievementType.DAILY)
			{
				daily.add(it);
			}
			else
			{
				weekly.add(it);
			}
		}
	}
	
	/**
	 * Retrieves the list of daily achievements.<br>
	 * This method returns all {@link AchievementTemplate} objects for daily tasks.
	 * @return a {@code List} containing all daily achievement templates.
	 */
	public List<AchievementTemplate> getDaily()
	{
		return daily;
	}
	
	/**
	 * Retrieves the list of weekly achievement templates.<br>
	 * This method returns all achievements categorized as weekly tasks.
	 * @return a {@code List} containing all {@link AchievementTemplate} objects for the week.
	 */
	public List<AchievementTemplate> getWeekly()
	{
		return weekly;
	}
	
	/**
	 * Retrieves the internal map of custom achievements.<br>
	 * This method provides access to the {@code custom} collection.
	 * @return a {@link TIntObjectHashMap} containing {@link AchievementTemplate} objects.
	 */
	private TIntObjectHashMap<AchievementTemplate> getCustomMap()
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
}
