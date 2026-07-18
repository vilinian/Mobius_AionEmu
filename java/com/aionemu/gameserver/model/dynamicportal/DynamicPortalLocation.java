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
package com.aionemu.gameserver.model.dynamicportal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.dynamicportal.DynamicPortalTemplate;
import com.aionemu.gameserver.services.dynamicportal.DynamicPortal;

/**
 * Represents the spatial coordinates and positioning data for a {@link DynamicPortal}.<br>
 * This class stores where a dynamic portal is located within the game world.<br>
 * It is used by the server to manage the placement of these objects.
 * @author Falke_34
 */
public class DynamicPortalLocation
{
	protected int id;
	protected boolean isActive;
	protected DynamicPortalTemplate template;
	protected DynamicPortal<DynamicPortalLocation> activeDynamicPortal;
	protected Map<Integer, Player> players = new HashMap<>();
	private final List<VisibleObject> spawned = new ArrayList<>();
	
	/**
	 * Creates a new instance of {@code DynamicPortalLocation}.<br>
	 * This constructor initializes the object with default values.
	 */
	public DynamicPortalLocation()
	{
	}
	
	/**
	 * Creates a new {@code DynamicPortalLocation} using a specific template.<br>
	 * This constructor initializes the location with the ID from the provided {@link DynamicPortalTemplate}.
	 * @param template The {@code DynamicPortalTemplate} used to configure this location.
	 */
	public DynamicPortalLocation(DynamicPortalTemplate template)
	{
		this.template = template;
		id = template.getId();
	}
	
	/**
	 * Checks if the portal location is currently active.<br>
	 * This method returns the current state of the {@code isActive} field.
	 * @return {@code true} if the location is active, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return isActive;
	}
	
	/**
	 * Sets the active {@link DynamicPortal} for this location.<br>
	 * This method also updates the {@code isActive} status based on whether the provided portal is {@code null}.
	 * @param dynamicPortal The {@code DynamicPortal} to set as active. If {@code null}, the location becomes inactive.
	 */
	public void setActiveDynamicPortal(DynamicPortal<DynamicPortalLocation> dynamicPortal)
	{
		isActive = dynamicPortal != null;
		activeDynamicPortal = dynamicPortal;
	}
	
	/**
	 * Retrieves the current {@link DynamicPortal} associated with this location.<br>
	 * This method returns the portal that is currently active.
	 * @return The {@code DynamicPortal} object or {@code null} if no portal is active.
	 */
	public DynamicPortal<DynamicPortalLocation> getActiveDynamicPortal()
	{
		return activeDynamicPortal;
	}
	
	/**
	 * Retrieves the {@code DynamicPortalTemplate} associated with this location.<br>
	 * This method returns the base configuration for the portal.
	 * @return The {@link DynamicPortalTemplate} object.
	 */
	public DynamicPortalTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
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
	
	/**
	 * Retrieves the list of players currently at this location.<br>
	 * The map uses {@code Integer} IDs as keys to identify each {@link Player}.
	 * @return A {@code Map} containing all current {@code Player} objects.
	 */
	public Map<Integer, Player> getPlayers()
	{
		return players;
	}
}
