/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 * <p/>
 * Aion-Lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.pet.PetBonusAttr;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for pet buffs within the game server.<br>
 * It stores information regarding various attributes and bonuses applied to pets.
 * @author Ace on 01/08/2016
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"petBonusattr"
})
@XmlRootElement(name = "pet_bonusattrs")
public class PetBuffData
{
	@XmlElement(name = "pet_bonusattr")
	protected List<PetBonusAttr> petBonusattr;
	
	@XmlTransient
	private final TIntObjectHashMap<PetBonusAttr> templates = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code templates} map using the list of {@link PetBonusAttr} objects.<br>
	 * The {@code petBonusattr} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (PetBonusAttr template : petBonusattr)
		{
			templates.put(template.getBuffId(), template);
			templates.put(template.getFoodCount(), template);
		}
		
		petBonusattr.clear();
		petBonusattr = null;
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
	 * Retrieves a specific pet bonus attribute based on its unique ID.<br>
	 * This method looks up the data in the internal {@code templates} map.
	 * @param buffId The unique identifier for the buff to retrieve.
	 * @return The {@link PetBonusAttr} associated with the given {@code buffId}, or {@code null} if not found.
	 */
	public PetBonusAttr getPetBonusattr(int buffId)
	{
		return templates.get(buffId);
	}
	
	/**
	 * Retrieves a {@link PetBonusAttr} based on the provided food count.<br>
	 * This method looks up the value in the internal template map.
	 * @param count The specific food count to search for.
	 * @return The corresponding {@code PetBonusAttr} object, or {@code null} if not found.
	 */
	public PetBonusAttr getFoodCount(int count)
	{
		return templates.get(count);
	}
}
