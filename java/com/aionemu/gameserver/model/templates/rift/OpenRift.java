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
package com.aionemu.gameserver.model.templates.rift;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the data model for an {@link com.aionemu.gameserver.model.templates.rift.OpenRift} entity.<br>
 * This class holds the configuration and properties for open rifts within the game world.
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OpenRift")
public class OpenRift
{
	@XmlAttribute(name = "schedule")
	protected String schedule;
	@XmlAttribute(name = "spawn")
	protected boolean guards;
	
	/**
	 * Retrieves the current schedule for this {@link OpenRift}.<br>
	 * This value is stored in the {@code schedule} field.
	 * @return The schedule as a {@code String}.
	 */
	public String getSchedule()
	{
		return schedule;
	}
	
	/**
	 * Checks if the rift should spawn guards.<br>
	 * This method retrieves the value of the {@code guards} field.
	 * @return {@code true} if guards are enabled, {@code false} otherwise.
	 */
	public boolean spawnGuards()
	{
		return guards;
	}
}
