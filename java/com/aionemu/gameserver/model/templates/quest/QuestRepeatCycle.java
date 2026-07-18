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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the repetition behavior for quests.<br>
 * This enum determines how often a quest can be repeated by a player.
 * @author vlog
 */
@XmlType(name = "QuestRepeatCycle")
@XmlEnum
public enum QuestRepeatCycle
{
	ALL(0),
	MON(1),
	TUE(2),
	WED(3),
	THU(4),
	FRI(5),
	SAT(6),
	SUN(7);
	
	private final int weekDay;
	
	/**
	 * Creates a new instance of {@link QuestRepeatCycle}.<br>
	 * This constructor maps the enum to a specific day of the week.
	 * @param weekDay The integer value representing the day.
	 */
	private QuestRepeatCycle(int weekDay)
	{
		this.weekDay = weekDay;
	}
	
	/**
	 * Retrieves the numerical value of the day.<br>
	 * This value corresponds to the {@code weekDay} field.
	 * @return The integer representation of the day.
	 */
	public int getDay()
	{
		return weekDay;
	}
}
