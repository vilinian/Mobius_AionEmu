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

import com.aionemu.gameserver.model.dynamicportal.DynamicPortalLocation;
import com.aionemu.gameserver.model.dynamicportal.DynamicPortalStateType;

/**
 * Represents a dynamic portal entity within the game world.<br>
 * This class handles the logic for portals that use {@link DynamicPortalLocation} to determine their position.
 * @author Falke_34
 */
public class Portal extends DynamicPortal<DynamicPortalLocation>
{
	/**
	 * Creates a new instance of a {@link Portal}.<br>
	 * This constructor initializes the portal using the provided location data.
	 * @param dynamicPortal The {@code DynamicPortalLocation} used to set up the portal.
	 */
	public Portal(DynamicPortalLocation dynamicPortal)
	{
		super(dynamicPortal);
	}
	
	/**
	 * Activates the dynamic portal in the game world.<br>
	 * This method updates the {@code DynamicPortalLocation} to link with this instance.<br>
	 * It then calls {@code despawn} and spawns the portal in an {@code OPEN} state.
	 */
	@Override
	public void startDynamicPortal()
	{
		getDynamicPortalLocation().setActiveDynamicPortal(this);
		despawn();
		spawn(DynamicPortalStateType.OPEN);
	}
	
	/**
	 * Stops the current dynamic portal.<br>
	 * This method sets the active location to {@code null}.<br>
	 * It then calls {@code despawn} and spawns a new state of {@code CLOSED}.
	 */
	@Override
	public void stopDynamicPortal()
	{
		getDynamicPortalLocation().setActiveDynamicPortal(null);
		despawn();
		spawn(DynamicPortalStateType.CLOSED);
	}
}
