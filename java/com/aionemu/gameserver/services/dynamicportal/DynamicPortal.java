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
package com.aionemu.gameserver.services.dynamicportal;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.model.dynamicportal.DynamicPortalLocation;
import com.aionemu.gameserver.model.dynamicportal.DynamicPortalStateType;
import com.aionemu.gameserver.services.DynamicPortalService;

/**
 * This class provides the base logic for managing dynamic portals in the game world.<br>
 * It handles the core behavior and state transitions for {@link DynamicPortalLocation} objects.
 * @author Falke_34
 * @param <DL>
 */
public abstract class DynamicPortal<DL extends DynamicPortalLocation>
{
	private boolean started;
	private final DL dynamicPortalLocation;
	
	protected abstract void stopDynamicPortal();
	
	protected abstract void startDynamicPortal();
	
	private final AtomicBoolean closed = new AtomicBoolean();
	
	/**
	 * Creates a new instance of a {@link DynamicPortal}.<br>
	 * This constructor initializes the portal with its specific location data.
	 * @param dynamicPortalLocation The {@code DL} object containing the portal's coordinates.
	 */
	public DynamicPortal(DL dynamicPortalLocation)
	{
		this.dynamicPortalLocation = dynamicPortalLocation;
	}
	
	/**
	 * Starts the dynamic portal.<br>
	 * This method checks if the portal is already running.<br>
	 * If it is not started, it calls {@code startDynamicPortal}.
	 */
	public void start()
	{
		boolean doubleStart = false;
		synchronized (this)
		{
			if (started)
			{
				doubleStart = true;
			}
			else
			{
				started = true;
			}
		}
		
		if (doubleStart)
		{
			return;
		}
		
		startDynamicPortal();
	}
	
	/**
	 * Stops the dynamic portal and cleans up its resources.<br>
	 * This method sets the {@code closed} state to {@code true}.<br>
	 * It then calls the {@code stopDynamicPortal} method.
	 */
	public void stop()
	{
		if (closed.compareAndSet(false, true))
		{
			stopDynamicPortal();
		}
	}
	
	/**
	 * Spawns a dynamic portal into the game world.<br>
	 * This method uses {@link DynamicPortalService} to handle the creation.
	 * @param type The {@code DynamicPortalStateType} of the portal to spawn.
	 */
	protected void spawn(DynamicPortalStateType type)
	{
		DynamicPortalService.getInstance().spawn(getDynamicPortalLocation(), type);
	}
	
	/**
	 * Removes the portal from the game world.<br>
	 * This method tells the {@link DynamicPortalService} to despawn the portal at its current location.
	 */
	protected void despawn()
	{
		DynamicPortalService.getInstance().despawn(getDynamicPortalLocation());
	}
	
	/**
	 * Checks if the dynamic portal is currently closed.<br>
	 * This method returns the current state of the {@code closed} flag.
	 * @return {@code true} if the portal is closed, {@code false} otherwise.
	 */
	public boolean isClosed()
	{
		return closed.get();
	}
	
	/**
	 * Retrieves the location of the current dynamic portal.<br>
	 * This method returns the {@code DL} object associated with this instance.
	 * @return The {@code DL} location of the dynamic portal.
	 */
	public DL getDynamicPortalLocation()
	{
		return dynamicPortalLocation;
	}
	
	/**
	 * Retrieves the unique identifier for the current portal location.<br>
	 * This value is fetched from the {@link DynamicPortalLocation} object.
	 * @return The {@code int} ID of the dynamic portal location.
	 */
	public int getDynamicPortalLocationId()
	{
		return dynamicPortalLocation.getId();
	}
}
