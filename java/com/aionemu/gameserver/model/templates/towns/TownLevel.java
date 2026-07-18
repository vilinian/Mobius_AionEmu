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
package com.aionemu.gameserver.model.templates.towns;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.spawns.Spawn;

/**
 * Represents the data structure for a specific level within a town.<br>
 * It contains information about the layout and associated {@link Spawn} points.<br>
 * This class is used to define the spatial organization of towns in the game world.
 * @author ViAl
 */
@XmlType(name = "town_level")
public class TownLevel
{
	@XmlAttribute(name = "level")
	protected int level;
	@XmlElement(name = "spawn")
	protected List<Spawn> spawns;
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
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
