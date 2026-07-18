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
package com.aionemu.gameserver.model.templates.quest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.rewards.BonusType;

/**
 * @author Rolandas
 *
 */

/**
 * <p/>
 * Java class for QuestBonuses complex type.
 * <p/>
 * The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * 
 * <pre>
 * &lt;complexType name="QuestBonuses">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="type" use="required" type="{}BonusType" />
 *       &lt;attribute name="level" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="skill" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestBonuses")
public class QuestBonuses
{
	@XmlAttribute(required = true)
	protected BonusType type;
	@XmlAttribute
	protected Integer level;
	@XmlAttribute
	protected Integer skill;
	
	/**
	 * Retrieves the category of the bonus.<br>
	 * This method returns the {@code BonusType} associated with this quest reward.
	 * @return The {@link BonusType} of the bonus.
	 */
	public BonusType getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the current level of the quest bonus.<br>
	 * This value is stored as an {@code Integer}.
	 * @return the level value or {@code null} if it has not been set.
	 */
	public Integer getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the skill value associated with this quest bonus.<br>
	 * This method returns the {@code Integer} value stored in the {@code skill} field.
	 * @return the skill level as an {@code Integer} or {@code null} if not set.
	 */
	public Integer getSkill()
	{
		return skill;
	}
}
