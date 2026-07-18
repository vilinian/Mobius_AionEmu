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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.items.RandomBonusResult;
import com.aionemu.gameserver.model.templates.item.bonuses.RandomBonus;
import com.aionemu.gameserver.model.templates.item.bonuses.StatBonusType;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for random bonuses that can be applied to items.<br>
 * It maps {@link StatBonusType} values to their corresponding {@link ModifiersTemplate}.<br>
 * Use this class to manage how randomized stats are distributed during item generation.
 * @author Rolandas
 * @fixed Eloann
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"randomBonuses"
})
@XmlRootElement(name = "random_bonuses")
public class ItemRandomBonusData
{
	@XmlElement(name = "random_bonus", required = true)
	protected List<RandomBonus> randomBonuses;
	@XmlTransient
	private final TIntObjectHashMap<RandomBonus> inventoryRandomBonusData = new TIntObjectHashMap<>();
	@XmlTransient
	private final TIntObjectHashMap<RandomBonus> polishRandomBonusData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the internal maps using the list of {@link RandomBonus} objects.<br>
	 * The {@code randomBonuses} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (RandomBonus bonus : randomBonuses)
		{
			getBonusMap(bonus.getBonusType()).put(bonus.getId(), bonus);
		}
		
		randomBonuses.clear();
		randomBonuses = null;
	}
	
	/**
	 * Retrieves the map of random bonuses based on the provided type.<br>
	 * It returns {@code inventoryRandomBonusData} for {@code INVENTORY}.<br>
	 * Otherwise, it returns {@code polishRandomBonusData}.
	 * @param bonusType The type of stat bonus to filter by.
	 * @return A map containing the relevant {@link RandomBonus} objects.
	 */
	private TIntObjectHashMap<RandomBonus> getBonusMap(StatBonusType bonusType)
	{
		if (bonusType == StatBonusType.INVENTORY)
		{
			return inventoryRandomBonusData;
		}
		
		return polishRandomBonusData;
	}
	
	/**
	 * Retrieves a random set of modifiers based on the provided type and option set.<br>
	 * This method calculates the result using a weighted chance system.
	 * @param bonusType The {@link StatBonusType} category to search for bonuses.
	 * @param rndOptionSet The unique identifier for the specific random option set.
	 * @return A {@link RandomBonusResult} containing the selected template and its index, or {@code null} if no bonus is found.
	 */
	public RandomBonusResult getRandomModifiers(StatBonusType bonusType, int rndOptionSet)
	{
		final RandomBonus bonus = getBonusMap(bonusType).get(rndOptionSet);
		if (bonus == null)
		{
			return null;
		}
		
		final List<ModifiersTemplate> modifiersGroup = bonus.getModifiers();
		
		final int chance = Rnd.get(10000);
		int current = 0;
		ModifiersTemplate template = null;
		int number = 0;
		
		for (int i = 0; i < modifiersGroup.size(); i++)
		{
			final ModifiersTemplate modifiers = modifiersGroup.get(i);
			
			current += modifiers.getChance() * 100;
			if (current >= chance)
			{
				template = modifiers;
				number = i + 1;
				break;
			}
		}
		
		return template == null ? null : new RandomBonusResult(template, number);
	}
	
	/**
	 * Retrieves a specific modifier template based on the provided criteria.<br>
	 * This method looks up a {@link RandomBonus} using the {@code bonusType} and {@code rndOptionSet}.<br>
	 * It then returns the modifier at the specified index from that bonus.
	 * @param bonusType The type of stat bonus to look for.
	 * @param rndOptionSet The unique identifier for the random option set.
	 * @param number The 1-based index of the modifier to retrieve.
	 * @return The {@code ModifiersTemplate} if found, or {@code null} if no bonus exists.
	 */
	public ModifiersTemplate getTemplate(StatBonusType bonusType, int rndOptionSet, int number)
	{
		final RandomBonus bonus = getBonusMap(bonusType).get(rndOptionSet);
		if (bonus == null)
		{
			return null;
		}
		
		return bonus.getModifiers().get(number - 1);
	}
	
	/**
	 * Returns the total number of random bonuses.<br>
	 * This count includes both inventory and polish data.
	 * @return The sum of all stored bonus entries.
	 */
	public int size()
	{
		return inventoryRandomBonusData.size() + polishRandomBonusData.size();
	}
}
