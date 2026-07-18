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
package com.aionemu.gameserver.model.rift;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.base.BaseLocation;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.base.BaseTemplate;
import com.aionemu.gameserver.model.templates.rift.RiftTemplate;

/**
 * Represents the spatial coordinates and positioning data for a {@link RiftTemplate}.<br>
 * This class is used to manage where specific rifts are located within the game world.
 * @author Source
 */
public class RiftLocation
{
	private boolean opened;
	protected RiftTemplate template;
	private final List<VisibleObject> spawned = new ArrayList<>();
	
	/**
	 * Creates a new instance of {@link RiftLocation}.<br>
	 * This constructor initializes the object with default values.
	 */
	public RiftLocation()
	{
	}
	
	/**
	 * Creates a new {@link RiftLocation} instance.<br>
	 * This constructor initializes the location using a specific {@code RiftTemplate}.
	 * @param template The {@code RiftTemplate} used to define this rift's properties.
	 */
	public RiftLocation(RiftTemplate template)
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
	 * Checks if the rift location is currently open.<br>
	 * Returns {@code true} if it is open.<br>
	 * Returns {@code false} if it is closed.
	 * @return The current open status of the rift.
	 */
	public boolean isOpened()
	{
		return opened;
	}
	
	/**
	 * Updates the open status of this rift location.<br>
	 * Sets the {@code opened} field to the provided value.
	 * @param state The new open status to set.
	 */
	public void setOpened(boolean state)
	{
		opened = state;
	}
	
	/**
	 * Retrieves the list of objects currently spawned at this location.<br>
	 * This method returns all {@link VisibleObject} instances associated with the portal.
	 * @return a {@code List} containing all {@code VisibleObject} items.
	 */
	public List<VisibleObject> getSpawned()
	{
		return spawned;
	}
}
