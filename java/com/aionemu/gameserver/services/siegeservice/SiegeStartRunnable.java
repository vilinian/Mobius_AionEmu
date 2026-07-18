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
package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.gameserver.services.SiegeService;

/**
 * This class handles the background execution of siege start logic.<br>
 * It implements {@link Runnable} to allow the {@link SiegeService} to initiate sieges asynchronously.
 * @author Source
 */
public class SiegeStartRunnable implements Runnable
{
	private final int locationId;
	
	/**
	 * Initializes a new instance of {@link SiegeStartRunnable}.<br>
	 * This constructor sets the specific siege area.
	 * @param locationId The unique identifier for the siege location.
	 */
	public SiegeStartRunnable(int locationId)
	{
		this.locationId = locationId;
	}
	
	@Override
	public void run()
	{
		SiegeService.getInstance().checkSiegeStart(getLocationId());
	}
	
	/**
	 * Retrieves the unique identifier for this siege location.<br>
	 * This ID is defined by NCSoft.
	 * @return The {@code int} value of the {@code locationId}.
	 */
	public int getLocationId()
	{
		return locationId;
	}
}
