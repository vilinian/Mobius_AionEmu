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
package com.aionemu.gameserver.model.templates.challenge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class serves as a data template for challenge quests in the game.<br>
 * It defines the static properties and configuration required to initialize a {@link com.aionemu.gameserver.model.challenge.ChallengeQuest}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChallengeQuest")
public class ChallengeQuestTemplate
{
	@XmlAttribute(required = true)
	protected int score;
	@XmlAttribute(name = "repeat_count", required = true)
	protected int repeatCount;
	@XmlAttribute(required = true)
	protected int id;
	
	/**
	 * Retrieves the current score for this challenge.<br>
	 * This value is stored in the {@code score} field.
	 * @return The integer value of the score.
	 */
	public int getScore()
	{
		return score;
	}
	
	/**
	 * Retrieves the number of times this challenge can be repeated.<br>
	 * This value is stored in the {@code repeatCount} field.
	 * @return The total count of allowed repetitions as an {@code int}.
	 */
	public int getRepeatCount()
	{
		return repeatCount;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
