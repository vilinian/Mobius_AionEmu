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
package com.aionemu.gameserver.model.instance.instanceposition;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * This class handles the general positioning logic for instances.<br>
 * It serves as a standard implementation of {@link InstancePositionHandler}.<br>
 * Use this class to manage basic coordinate data within an instance.
 * @author xTz
 */
public class GenerealInstancePosition implements InstancePositionHandler
{
	protected int mapId;
	protected int instanceId;
	
	/**
	 * This method sets up the initial values for this position.<br>
	 * It assigns the provided {@code mapId} and {@code instanceId} to the object.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the specific instance.
	 */
	@Override
	public void initsialize(Integer mapId, int instanceId)
	{
		this.mapId = mapId;
		this.instanceId = instanceId;
	}
	
	/**
	 * Teleports a {@link Player} to a specific location based on the provided zone and position.<br>
	 * This method uses hardcoded coordinates for different map areas.
	 * @param player The {@code Player} object to be moved.
	 * @param zone The integer ID of the target zone.
	 * @param position The specific coordinate index within the zone.
	 */
	@Override
	public void port(Player player, int zone, int position)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	/**
	 * Moves a {@link Player} to a specific location.<br>
	 * This method calls the {@code teleportTo} service.
	 * @param player The {@code Player} object to move.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 * @param z The target Z coordinate.
	 * @param h The target heading value.
	 */
	protected void teleport(Player player, float x, float y, float z, byte h)
	{
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
}
