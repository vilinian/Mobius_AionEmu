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

/**
 * Represents the specific position data for the {@code GoldenCrucible} instance.<br>
 * This class extends {@link GenerealInstancePosition} to handle unique spatial logic for this area.
 * @author Falke_34
 */
public class GoldenCrucibleInstancePosition extends GenerealInstancePosition
{
	
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
		switch (position)
		{
			case 1:
				switch (zone)
				{
					case 0:
						teleport(player, 384.1f, 285.5f, 231.1f, (byte) 90);
						break;
					case 1:
						teleport(player, 385.0f, 226.6f, 231.1f, (byte) 35);
						break;
				}
				break;
		}
	}
}
