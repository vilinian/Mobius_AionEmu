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
package com.aionemu.gameserver.model.templates.vortex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.base.BaseTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Represents a specific starting location within the game world.<br>
 * This class stores coordinates and metadata for {@link WorldPosition} data.<br>
 * It is used to define where players or NPCs begin their journey.
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StartPoint")
public class StartPoint
{
	@XmlAttribute(name = "map")
	protected int map;
	@XmlAttribute(name = "x")
	protected float x;
	@XmlAttribute(name = "y")
	protected float y;
	@XmlAttribute(name = "z")
	protected float z;
	@XmlAttribute(name = "h")
	protected byte h;
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return map;
	}
	
	/**
	 * Retrieves the starting coordinates for this point.<br>
	 * It creates a new {@link WorldPosition} object using the internal map and coordinate values.
	 * @return The {@code WorldPosition} representing the start location.
	 */
	public WorldPosition getStartPoint()
	{
		final WorldPosition start = new WorldPosition(map);
		start.setXYZH(x, y, z, h);
		return start;
	}
}
