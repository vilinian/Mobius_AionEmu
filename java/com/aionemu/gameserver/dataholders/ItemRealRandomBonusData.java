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

import com.aionemu.gameserver.model.templates.item.bonuses.RealItemRandomBonus;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds data for real random bonuses associated with items.<br>
 * It maps item IDs to their corresponding {@link RealItemRandomBonus} templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "real_random_bonuses")
public class ItemRealRandomBonusData
{
	@XmlElement(name = "real_random_bonus", required = true)
	protected List<RealItemRandomBonus> randomBonuses;
	private final TIntObjectHashMap<RealItemRandomBonus> bonuslistData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code bonuslistData} map using the list of {@link RealItemRandomBonus} objects.<br>
	 * The {@code randomBonuses} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (RealItemRandomBonus bonus : randomBonuses)
		{
			bonuslistData.put(bonus.getId(), bonus);
		}
		
		randomBonuses.clear();
		randomBonuses = null;
	}
	
	/**
	 * Retrieves a specific random bonus based on its unique identifier.<br>
	 * This method looks up the data in the internal {@code bonuslistData} map.
	 * @param id The unique integer ID of the bonus to find.
	 * @return The {@link RealItemRandomBonus} object associated with the given {@code id}, or {@code null} if not found.
	 */
	public RealItemRandomBonus getRealBonusById(int id)
	{
		return bonuslistData.get(id);
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return bonuslistData.size();
	}
}
