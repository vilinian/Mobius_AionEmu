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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;

/**
 * Represents an item that can be dropped or rewarded randomly.<br>
 * It defines the logic for selecting items based on specific probabilities.<br>
 * This class is used by the game server to handle loot tables and random rewards.
 * @author vlog
 * @author GiGatR00n v4.7.5.x
 */
@XmlType(name = "RandomItem")
public class RandomItem
{
	@XmlAttribute(name = "type")
	protected RandomType type;
	@XmlAttribute(name = "count")
	protected int count;
	@XmlAttribute(name = "rnd_min")
	public int rndMin;
	@XmlAttribute(name = "rnd_max")
	public int rndMax;
	@XmlAttribute(name = "race")
	public Race race = Race.PC_ALL;
	@XmlAttribute(name = "player_class")
	public PlayerClass playerClass = PlayerClass.ALL;
	
	/**
	 * Retrieves the current count value.<br>
	 * This method returns the integer stored in the {@code count} field.
	 * @return The current count as an {@code int}.
	 */
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Retrieves the {@code RandomType} of this item.<br>
	 * This value determines how the random item behaves.
	 * @return The {@code RandomType} associated with this object.
	 */
	public RandomType getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the minimum random value for this item.<br>
	 * This value is used to determine the lower bound of a random range.
	 * @return The minimum random integer.
	 */
	public int getRndMin()
	{
		return rndMin;
	}
	
	/**
	 * Retrieves the maximum value for a random range.<br>
	 * This value is used to determine the upper limit of an item count.
	 * @return The {@code int} value of {@code rndMax}.
	 */
	public int getRndMax()
	{
		return rndMax;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the character class of the player.<br>
	 * This method returns the {@code PlayerClass} associated with this ranking result.
	 * @return The {@code PlayerClass} of the player.
	 */
	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}
	
	/**
	 * Calculates the final quantity of a {@code RandomItem}.<br>
	 * It determines the result based on the defined random range or fixed count.<br>
	 * If all values are zero, it defaults to 1.
	 * @return The calculated integer count for the item.
	 */
	public int getResultCount()
	{
		if ((count == 0) && (rndMin == 0) && (rndMax == 0))
		{
			return 1;
		}
		else if ((rndMin > 0) || (rndMax > 0))
		{
			if (rndMax < rndMin)
			{
				LoggerFactory.getLogger(RandomItem.class).warn("Wrong rnd result item definition {} {}", rndMin, rndMax);
				return 1;
			}
			
			return Rnd.get(rndMin, rndMax);
		}
		else
		{
			return count;
		}
	}
}
