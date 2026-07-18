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
package com.aionemu.gameserver.model.templates.item.grind;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.PlayerClass;

/**
 * This class defines the data structure for combining items during grinding activities.<br>
 * It maps XML configuration data to internal game logic for item synthesis.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement(name = "GrindCombine")
public class GrindCombine
{
	@XmlElement(name = "reward_grind")
	protected List<GrindReward> rewards;
	
	@XmlAttribute(name = "id")
	protected int id;
	
	@XmlAttribute(name = "target_class")
	protected PlayerClass playerClass;
	
	@XmlAttribute(name = "price")
	protected int price;
	
	@XmlAttribute(name = "materiel_color_1")
	protected int color1;
	
	@XmlAttribute(name = "materiel_color_2")
	protected int color2;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the first material color value.<br>
	 * This value corresponds to the {@code materiel_color_1} attribute.
	 * @return The integer value of the first material color.
	 */
	public int getColor1()
	{
		return color1;
	}
	
	/**
	 * Retrieves the second material color value.<br>
	 * This value is stored in the {@code color2} field.
	 * @return The integer value of the second material color.
	 */
	public int getColor2()
	{
		return color2;
	}
	
	/**
	 * Retrieves the cost of this bind point.<br>
	 * This value is stored as an {@code int}.
	 * @return The current price of the template.
	 */
	public int getPrice()
	{
		return price;
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
	 * Retrieves the list of rewards for this grind combination.<br>
	 * This method returns all {@link GrindReward} objects associated with the template.
	 * @return a {@code List} of {@link GrindReward} objects.
	 */
	public List<GrindReward> getRewards()
	{
		return rewards;
	}
}
