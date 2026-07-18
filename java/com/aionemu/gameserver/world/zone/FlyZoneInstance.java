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
package com.aionemu.gameserver.world.zone;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.zone.ZoneInfo;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * Represents a specific instance of a zone where flying is permitted.<br>
 * This class extends {@link ZoneInstance} to handle unique behaviors for flight-enabled areas.
 * @author MrPoke
 */
public class FlyZoneInstance extends ZoneInstance
{
	/**
	 * Creates a new instance of a {@link FlyZoneInstance}.<br>
	 * This constructor initializes the zone using a specific map and template.
	 * @param mapId The unique identifier for the map.
	 * @param template The {@code ZoneInfo} object containing the zone configuration.
	 */
	public FlyZoneInstance(int mapId, ZoneInfo template)
	{
		super(mapId, template);
	}
	
	/**
	 * Handles the logic when a {@code Creature} enters this zone.<br>
	 * It checks if the creature can enter using the parent class method.<br>
	 * If successful, it sets the creature's zone type to {@code ZoneType.FLY}.
	 * @param creature The {@code Creature} entering the zone.
	 * @return {@code true} if the creature successfully entered, otherwise {@code false}.
	 */
	@Override
	public synchronized boolean onEnter(Creature creature)
	{
		if (super.onEnter(creature))
		{
			creature.setInsideZoneType(ZoneType.FLY);
			return true;
		}
		
		return false;
	}
	
	/**
	 * This method is called when a {@link Creature} leaves the zone.<br>
	 * It removes the flight status from the creature if they are leaving.<br>
	 * It logs an audit message if a {@link Player} leaves while still flying.
	 * @param creature The {@link Creature} that is leaving the zone.
	 * @return {@code true} if the leave action was successful, otherwise {@code false}.
	 */
	@Override
	public synchronized boolean onLeave(Creature creature)
	{
		if (super.onLeave(creature))
		{
			creature.unsetInsideZoneType(ZoneType.FLY);
			if (creature.isInState(CreatureState.FLYING) && !creature.isInState(CreatureState.FLIGHT_TELEPORT))
			{
				if (creature instanceof Player)
				{
					AuditLogger.info((Player) creature, "On leave Fly zone in fly state!!");
				}
			}
			
			return true;
		}
		
		return false;
	}
}
