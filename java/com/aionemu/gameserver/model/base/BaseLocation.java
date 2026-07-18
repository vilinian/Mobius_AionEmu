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
package com.aionemu.gameserver.model.base;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.base.BaseTemplate;

/**
 * Represents the base data structure for all geographical locations in the game world.<br>
 * It provides common properties shared by different types of {@link com.aionemu.gameserver.model.templates.base.BaseTemplate} instances.
 * @author Source
 */
public class BaseLocation
{
	protected BaseTemplate template;
	protected Race race = Race.NPC;
	
	/**
	 * Creates a new instance of {@link BaseLocation}.<br>
	 * This constructor initializes the object with default values.
	 */
	public BaseLocation()
	{
	}
	
	/**
	 * Creates a new {@link BaseLocation} instance using a provided template.<br>
	 * This constructor initializes the internal {@code template} field.
	 * @param template The {@link BaseTemplate} used to define this location.
	 */
	public BaseLocation(BaseTemplate template)
	{
		this.template = template;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link BaseLocation}.<br>
	 * This value is obtained from the underlying {@code template}.
	 * @return The integer ID of the location.
	 */
	public int getId()
	{
		return template.getId();
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return template.getWorldId();
	}
	
	/**
	 * Retrieves the name of the location.<br>
	 * This method returns the {@code String} name from the associated {@link BaseTemplate}.
	 * @return The name of the location as a {@code String}.
	 */
	public String getName()
	{
		return template.getName();
	}
	
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
	 * Sets the {@code race} for this location.<br>
	 * This updates the internal {@code race} field.
	 * @param race The new {@link Race} to assign.
	 */
	public void setRace(Race race)
	{
		this.race = race;
	}
	
}
