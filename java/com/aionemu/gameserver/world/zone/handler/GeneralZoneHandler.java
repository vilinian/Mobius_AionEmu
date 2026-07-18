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
package com.aionemu.gameserver.world.zone.handler;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * This class handles general logic and events for {@link ZoneInstance} objects.<br>
 * It serves as a primary manager for common zone-related behaviors within the game world.
 * @author MrPoke
 */
public class GeneralZoneHandler implements ZoneHandler
{
	/**
	 * This method is called when a {@code Creature} enters a specific {@link ZoneInstance}.<br>
	 * It handles any logic required for entering the area.
	 * @param player The {@code Creature} that entered the zone.
	 * @param zone The {@link ZoneInstance} being entered.
	 */
	@Override
	public void onEnterZone(Creature player, ZoneInstance zone)
	{
	}
	
	/**
	 * This method is called when a {@code Creature} leaves a specific {@link ZoneInstance}.<br>
	 * It handles any logic required to update the game state after the player exits.
	 * @param player The {@code Creature} that is leaving the zone.
	 * @param zone The {@link ZoneInstance} that was just exited.
	 */
	@Override
	public void onLeaveZone(Creature player, ZoneInstance zone)
	{
	}
}
