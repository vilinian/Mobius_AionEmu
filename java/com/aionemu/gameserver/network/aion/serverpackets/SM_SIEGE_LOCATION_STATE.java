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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the synchronization of siege location states.<br>
 * It informs clients about the current status and positions of {@link SiegeLocation} objects.
 * @author Source
 */
public class SM_SIEGE_LOCATION_STATE extends AionServerPacket
{
	private final int locationId;
	private final int state;
	
	/**
	 * Creates a new {@code SM_SIEGE_LOCATION_STATE} packet.<br>
	 * This constructor extracts data from a {@link SiegeLocation} object.<br>
	 * It sets the location ID and determines the state based on vulnerability.
	 * @param location The {@code SiegeLocation} used to initialize the packet.
	 */
	public SM_SIEGE_LOCATION_STATE(SiegeLocation location)
	{
		locationId = location.getLocationId();
		state = location.isVulnerable() ? 1 : 0;
	}
	
	/**
	 * Creates a new {@code SM_SIEGE_LOCATION_STATE} packet.<br>
	 * This packet updates the status of a specific siege location.
	 * @param locationId The unique identifier for the siege location.
	 * @param state The current status or state of that location.
	 */
	public SM_SIEGE_LOCATION_STATE(int locationId, int state)
	{
		this.locationId = locationId;
		this.state = state;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(locationId);
		writeC(state);
	}
}
