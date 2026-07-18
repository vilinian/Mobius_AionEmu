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
package com.aionemu.gameserver.model.templates.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.gameobjects.Kisk;

/**
 * This class defines the data template for {@code kisk_stats}.<br>
 * It stores configuration values used to manage statistics related to the {@code kisk} system.<br>
 * Use this model to map XML data into game server objects.
 * @author Sarynth
 */
@XmlRootElement(name = "kisk_stats")
@XmlAccessorType(XmlAccessType.FIELD)
public class KiskStatsTemplate
{
	@XmlAttribute(name = "usemask")
	private int useMask = 4;
	@XmlAttribute(name = "members")
	private int maxMembers = 6;
	@XmlAttribute(name = "resurrects")
	private int maxResurrects = 18;
	
	/**
	 * Retrieves the usage mask for this {@link Kisk}.<br>
	 * This value is fetched from the associated {@code kiskStatsTemplate}.
	 * @return The integer value of the use mask.
	 */
	public int getUseMask()
	{
		return useMask;
	}
	
	/**
	 * Retrieves the maximum number of members allowed for this {@link Kisk}.<br>
	 * This value is fetched from the associated {@code kiskStatsTemplate}.
	 * @return The maximum member limit as an {@code int}.
	 */
	public int getMaxMembers()
	{
		return maxMembers;
	}
	
	/**
	 * Returns the maximum number of allowed resurrections.<br>
	 * This value is retrieved from the {@code maxResurrects} field.
	 * @return The total count of allowed resurrections as an {@code int}.
	 */
	public int getMaxResurrects()
	{
		return maxResurrects;
	}
}
