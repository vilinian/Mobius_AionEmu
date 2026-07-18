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

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.Guides.GuideTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for HTML-based guides within the game.<br>
 * It serves as a container for {@link GuideTemplate} objects loaded from XML files.
 * @author xTz
 */
@XmlRootElement(name = "guides")
@XmlAccessorType(XmlAccessType.FIELD)
public class GuideHtmlData
{
	@XmlElement(name = "guide", type = GuideTemplate.class)
	private List<GuideTemplate> guideTemplates;
	private final TIntObjectHashMap<ArrayList<GuideTemplate>> templates = new TIntObjectHashMap<>();
	private final int CLASS_ALL = 255;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code templates} map using the list of {@link GuideTemplate} objects.<br>
	 * The {@code guideTemplates} list is set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (GuideTemplate template : guideTemplates)
		{
			addTemplate(template);
		}
		
		guideTemplates = null;
	}
	
	/**
	 * Adds a {@link GuideTemplate} to the internal storage.<br>
	 * This method calculates a hash based on class, race, and level.<br>
	 * It then stores the template in the appropriate list within the {@code templates} map.
	 * @param template The {@code GuideTemplate} object to be added.
	 */
	private void addTemplate(GuideTemplate template)
	{
		Race race = template.getRace();
		if (race == null)
		{
			race = Race.PC_ALL;
		}
		
		final int classId = template.getPlayerClass() == null ? CLASS_ALL : template.getPlayerClass().ordinal();
		
		final int hash = makeHash(classId, race.ordinal(), template.getLevel());
		ArrayList<GuideTemplate> value = templates.get(hash);
		if (value == null)
		{
			value = new ArrayList<>();
			templates.put(hash, value);
		}
		
		value.add(template);
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
	 * Retrieves the collection of guide templates.<br>
	 * This map uses an integer key to group {@link GuideTemplate} objects.
	 * @return a {@code TIntObjectHashMap} containing lists of {@link GuideTemplate}s.
	 */
	public TIntObjectHashMap<ArrayList<GuideTemplate>> getTemplates()
	{
		return templates;
	}
	
	/**
	 * Finds a specific guide based on its title.<br>
	 * This method searches through all available templates.<br>
	 * It returns the first match it finds.
	 * @param title The exact name of the guide to search for.
	 * @return The matching {@link GuideTemplate} object or {@code null} if no match exists.
	 */
	public GuideTemplate getTemplateByTitle(String title)
	{
		for (int templateHash : templates.keys())
		{
			for (GuideTemplate template : templates.get(templateHash))
			{
				if (template.getTitle().equals(title))
				{
					return template;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves a list of {@link GuideTemplate} objects based on player attributes.<br>
	 * This method searches for templates matching specific combinations of class, race, and level.<br>
	 * It falls back to more general templates if specific ones are not found.
	 * @param playerClass The {@code PlayerClass} of the character.
	 * @param race The {@code Race} of the character.
	 * @param level The current level of the character.
	 * @return An array of {@code GuideTemplate} objects matching the criteria.
	 */
	public GuideTemplate[] getTemplatesFor(PlayerClass playerClass, Race race, int level)
	{
		final List<GuideTemplate> guideTemplate = new ArrayList<>();
		
		final List<GuideTemplate> classRaceSpecificTemplates = templates.get(makeHash(playerClass.ordinal(), race.ordinal(), level));
		final List<GuideTemplate> classSpecificTemplates = templates.get(makeHash(playerClass.ordinal(), Race.PC_ALL.ordinal(), level));
		final List<GuideTemplate> raceSpecificTemplates = templates.get(makeHash(CLASS_ALL, race.ordinal(), level));
		final List<GuideTemplate> generalTemplates = templates.get(makeHash(CLASS_ALL, Race.PC_ALL.ordinal(), level));
		
		if (classRaceSpecificTemplates != null)
		{
			guideTemplate.addAll(classRaceSpecificTemplates);
		}
		
		if (classSpecificTemplates != null)
		{
			guideTemplate.addAll(classSpecificTemplates);
		}
		
		if (raceSpecificTemplates != null)
		{
			guideTemplate.addAll(raceSpecificTemplates);
		}
		
		if (generalTemplates != null)
		{
			guideTemplate.addAll(generalTemplates);
		}
		
		return guideTemplate.toArray(new GuideTemplate[guideTemplate.size()]);
	}
	
	/**
	 * Generates a unique hash code for a specific character configuration.<br>
	 * This method combines the {@code classType}, {@code race}, and {@code level} into a single integer.<br>
	 * It is used to efficiently look up templates in the internal map.
	 * @param classType The numerical ID of the player class.
	 * @param race The numerical ID of the character race.
	 * @param level The current level of the character.
	 * @return A unique {@code int} hash representing the combination of all three parameters.
	 */
	private static int makeHash(int classType, int race, int level)
	{
		int result = classType << 8;
		result = (result | race) << 8;
		return result | level;
	}
}
