/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 * <p>
 * Aion-Lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Aion-Lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. *
 * You should have received a copy of the GNU General Public License
 * along with Aion-Lightning.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.dataholders;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.ExpTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the experience requirements for each level a player can obtain.<br>
 * It serves as a data container for mapping levels to their respective {@code ExpTemplate} values.
 * @author Luno
 */
@XmlRootElement(name = "player_experience_table")
@XmlAccessorType(XmlAccessType.NONE)
public class PlayerExperienceTable
{
	/**
	 * Exp table
	 */
	@XmlElement(name = "exp")
	private List<ExpTemplate> expTemp;
	private TIntObjectHashMap<ExpTemplate> exp;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code exp} map using the list of {@link ExpTemplate} objects.<br>
	 * The {@code expTemp} list is set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		exp = new TIntObjectHashMap<>();
		for (ExpTemplate expT : expTemp)
		{
			exp.put(expT.getLevel() - 1, expT);
		}
		
		expTemp = null;
	}
	
	/**
	 * Retrieves the experience template for a specific player level.<br>
	 * This method looks up the data in the internal {@code exp} map.
	 * @param level The integer level to look up.
	 * @return The {@link ExpTemplate} associated with the given level, or {@code null} if not found.
	 */
	public ExpTemplate getExpTemplate(int level)
	{
		return exp.get(level);
	}
	
	/**
	 * Retrieves the starting experience required for a specific level.<br>
	 * This method checks if the provided {@code level} is within the valid range.<br>
	 * It returns the experience value from the internal map.
	 * @param level The level to check.
	 * @return The amount of experience needed to reach the specified level.
	 */
	public long getStartExpForLevel(int level)
	{
		if (level > exp.size())
		{
			throw new IllegalArgumentException("The given level is higher than possible max");
		}
		
		return level == 0 ? 0 : exp.get(level - 1).getExp();
	}
	
	/**
	 * Calculates the player level based on a given experience value.<br>
	 * This method searches through the {@code exp} table to find the highest reached level.<br>
	 * It ensures the result does not exceed the maximum possible level.
	 * @param expValue The total amount of experience points owned by the player.
	 * @return The integer level corresponding to the provided experience value.
	 */
	public int getLevelForExp(long expValue)
	{
		int level = 0;
		for (int i = exp.size(); i > 0; i--)
		{
			if (expValue >= exp.get(i - 1).getExp())
			{
				level = i;
				break;
			}
		}
		
		if (getMaxLevel() <= level)
		{
			return getMaxLevel() - 1;
		}
		
		return level;
	}
	
	/**
	 * Retrieves the maximum level available in the experience table.<br>
	 * It checks if the {@code exp} map is initialized.<br>
	 * If it is null, it returns {@code 0}.<br>
	 * Otherwise, it returns the total number of entries.
	 * @return The highest level value as an {@code int}.
	 */
	public int getMaxLevel()
	{
		return exp == null ? 0 : exp.size();
	}
	
}
