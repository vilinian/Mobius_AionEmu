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
package com.aionemu.gameserver.model.templates.achievement;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class defines the rewards granted to players upon completing an achievement.<br>
 * It serves as a data model for mapping specific prizes to {@code Achievement} entries.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "AchievementRewards")
public class AchievementRewards
{
	@XmlAttribute(name = "gold")
	protected Integer gold;
	@XmlAttribute(name = "exp")
	protected Integer exp;
	@XmlAttribute(name = "ap")
	protected Integer ap;
	@XmlAttribute(name = "gp")
	protected Integer gp;
	@XmlElement(name = "reward_item")
	protected List<AchievementItems> achievementItems;
	
	/**
	 * Retrieves the amount of gold awarded for an achievement.<br>
	 * This value is stored in the {@code gold} field.
	 * @return The amount of gold as an {@code Integer}.
	 */
	public Integer getGold()
	{
		return gold;
	}
	
	/**
	 * Retrieves the amount of AP awarded for an achievement.<br>
	 * This value is stored in the {@code ap} field.
	 * @return The amount of AP as an {@code Integer}.
	 */
	public Integer getAp()
	{
		return ap;
	}
	
	/**
	 * Retrieves the experience points awarded for an achievement.<br>
	 * This value is stored in the {@code exp} field.
	 * @return The amount of experience as an {@code Integer}.
	 */
	public Integer getExp()
	{
		return exp;
	}
	
	/**
	 * Retrieves the amount of GP rewarded for an achievement.<br>
	 * This value is stored in the {@code gp} field.
	 * @return The amount of GP as an {@code Integer}.
	 */
	public Integer getGp()
	{
		return gp;
	}
	
	/**
	 * Retrieves the list of items rewarded for an achievement.<br>
	 * This method returns all {@link AchievementItems} associated with these rewards.
	 * @return A {@code List} of {@code AchievementItems}.
	 */
	public List<AchievementItems> getAchievementItems()
	{
		return achievementItems;
	}
}
