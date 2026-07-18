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
package com.aionemu.gameserver.model.templates.pet;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a specific bonus attribute provided by a {@code Pet}.<br>
 * This class stores the data for various buffs or stats granted to the owner.<br>
 * It is used to define how pets influence character statistics in the game.
 * @author Ace on 01/08/2016
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetBonusAttr", propOrder =
{
	"penaltyAttr"
})
public class PetBonusAttr
{
	@XmlElement(name = "penalty_attr")
	protected List<PetPenaltyAttr> penaltyAttr;
	
	@XmlAttribute(name = "buff_id", required = true)
	protected int buffId;
	
	@XmlAttribute(name = "food_count", required = true)
	protected int foodCount;
	
	/**
	 * Retrieves the list of penalty attributes for this pet.<br>
	 * If the list is {@code null}, it returns a new empty {@code ArrayList}.
	 * @return A {@code List} of {@link PetPenaltyAttr} objects.
	 */
	public List<PetPenaltyAttr> getPenaltyAttr()
	{
		if (penaltyAttr == null)
		{
			penaltyAttr = new ArrayList<>();
		}
		
		return penaltyAttr;
	}
	
	/**
	 * Retrieves the unique buffer identifier.<br>
	 * This value is stored in the {@code buffId} field.
	 * @return The integer ID of the buff.
	 */
	public int getBuffId()
	{
		return buffId;
	}
	
	/**
	 * Sets the unique identifier for the buff.<br>
	 * This updates the {@code buffId} field in this object.
	 * @param value The new integer ID to assign to the buff.
	 */
	public void setBuffId(int value)
	{
		buffId = value;
	}
	
	/**
	 * Retrieves the total number of food items.<br>
	 * This method returns the {@code foodCount} value from this object.
	 * @return The current count of food as an {@code int}.
	 */
	public int getFoodCount()
	{
		return foodCount;
	}
}
