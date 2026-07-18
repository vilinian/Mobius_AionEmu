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
package com.aionemu.gameserver.model.templates.pet;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class defines the rewards granted to players for obtaining a pet.<br>
 * It maps specific items or bonuses associated with {@code Pet} templates.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetRewards", propOrder =
{
	"results"
})
public class PetRewards
{
	@XmlElement(name = "result")
	protected List<PetFeedResult> results;
	@XmlAttribute(name = "group", required = true)
	protected FoodType type;
	@XmlAttribute
	protected boolean loved = false;
	
	/**
	 * Retrieves the list of pet feed results.<br>
	 * This method ensures that a non-null {@code List} is returned.<br>
	 * If the internal list is {@code null}, it initializes a new {@code ArrayList}.
	 * @return A {@code List} of {@link PetFeedResult} objects.
	 */
	public List<PetFeedResult> getResults()
	{
		if (results == null)
		{
			results = new ArrayList<>();
		}
		
		return results;
	}
	
	/**
	 * Retrieves the category of food associated with these rewards.<br>
	 * This method returns the {@code FoodType} value stored in this object.
	 * @return The {@code FoodType} of the reward.
	 */
	public FoodType getType()
	{
		return type;
	}
	
	/**
	 * Checks if the pet is currently in a loved state.<br>
	 * This method returns the value of the {@code loved} field.
	 * @return {@code true} if the pet is loved, otherwise {@code false}.
	 */
	public boolean isLoved()
	{
		return loved;
	}
}
