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
package com.aionemu.gameserver.model.templates.spawns.riftspawns;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.base.BaseTemplate;
import com.aionemu.gameserver.model.templates.spawns.Spawn;

/**
 * Represents a spawn point specifically for rift entities within the game world.<br>
 * This class extends the functionality of {@link Spawn} to handle rift-specific data.
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RiftSpawn")
public class RiftSpawn
{
	@XmlAttribute(name = "id")
	private int id;
	@XmlAttribute(name = "world")
	private int world;
	@XmlElement(name = "spawn")
	private List<Spawn> spawns = new ArrayList<>();
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return world;
	}
	
	/**
	 * Retrieves the list of {@link Spawn} objects for this map.<br>
	 * If the internal list is {@code null}, it returns a new empty {@code ArrayList}.
	 * @return A {@code List} containing all {@code Spawn} entries.
	 */
	public List<Spawn> getSpawns()
	{
		return spawns;
	}
}
