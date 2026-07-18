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
package com.aionemu.gameserver.model.templates.event;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.aionemu.gameserver.model.Race;

/**
 * Represents an item drop event that occurs within a player's inventory.<br>
 * This class defines the data structure for items obtained through specific game events.
 * @author Rolandas
 */
@XmlType(name = "InventoryDrop")
@XmlAccessorType(XmlAccessType.FIELD)
public class InventoryDrop
{
	@XmlValue
	private int dropItem;
	@XmlAttribute(name = "startlevel", required = false)
	private int startLevel;
	@XmlAttribute(name = "endlevel", required = false)
	private int endLevel;
	@XmlAttribute(name = "interval", required = true)
	private int interval;
	@XmlAttribute(name = "maxCountOfDay", required = false)
	private int maxCountOfDay;
	@XmlAttribute(name = "cleanTime", required = false)
	private int cleanTime;
	
	@XmlAttribute
	private Race race = Race.PC_ALL;
	
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
	 * Retrieves the unique identifier for the item that can be dropped.<br>
	 * This value is stored in the {@code dropItem} field.
	 * @return The {@code int} ID of the dropped item.
	 */
	public int getDropItem()
	{
		return dropItem;
	}
	
	/**
	 * Retrieves the minimum level required for this drop.<br>
	 * This value is stored in the {@code startLevel} field.
	 * @return The starting level as an {@code int}.
	 */
	public int getStartLevel()
	{
		return startLevel;
	}
	
	/**
	 * Retrieves the maximum level for this drop.<br>
	 * This value defines the upper limit of the level range.
	 * @return The {@code int} value representing the end level.
	 */
	public int getEndLevel()
	{
		return endLevel;
	}
	
	/**
	 * Retrieves the time interval for this drop.<br>
	 * This value determines how often the item can be dropped.
	 * @return The current {@code interval} value as an {@code int}.
	 */
	public int getInterval()
	{
		return interval;
	}
	
	/**
	 * Retrieves the maximum number of items allowed per day.<br>
	 * This value is stored in the {@code dailyMaxCount} field.
	 * @return The maximum count for the current day as an {@code int}.
	 */
	public int getMaxCountOfDay()
	{
		return maxCountOfDay;
	}
	
	/**
	 * Retrieves the time required to clear an item.<br>
	 * This value is stored in the {@code cleanTime} field.
	 * @return The amount of time needed for cleaning as an {@code int}.
	 */
	public int getCleanTime()
	{
		return cleanTime;
	}
}
