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
package com.aionemu.gameserver.ai2;

/**
 * This class defines the constant names used for various AI entities.<br>
 * It serves as a central registry to ensure consistency across the {@code ai2} package.
 * @author ATracer
 */
public enum AiNames
{
	GENERAL_NPC("general"),
	DUMMY_NPC("dummy"),
	AGGRESSIVE_NPC("aggressive");
	
	private final String name;
	
	/**
	 * Creates a new {@code AiNames} constant.<br>
	 * This constructor assigns the internal name string.
	 * @param name The unique identifier for the AI type.
	 */
	AiNames(String name)
	{
		this.name = name;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
}
