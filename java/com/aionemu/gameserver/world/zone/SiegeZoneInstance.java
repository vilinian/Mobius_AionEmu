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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.zone.ZoneInfo;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Represents a specific instance of a siege zone within the game world.<br>
 * This class handles the unique logic and state for areas where large-scale siege warfare occurs. It extends {@link ZoneInstance} to provide specialized behavior for these zones.
 * @author MrPoke
 */
public class SiegeZoneInstance extends ZoneInstance
{
	private static final Logger log = LoggerFactory.getLogger(SiegeZoneInstance.class);
	private final Map<Integer, Player> players = new HashMap<>();
	
	/**
	 * Creates a new instance of a {@link SiegeZoneInstance}.<br>
	 * This constructor initializes the zone using specific map and template data.
	 * @param mapId The unique identifier for the map.
	 * @param template The {@code ZoneInfo} object containing the zone configuration.
	 */
	public SiegeZoneInstance(int mapId, ZoneInfo template)
	{
		super(mapId, template);
	}
	
	/**
	 * Handles the logic when a {@code Creature} enters this zone.<br>
	 * It checks if the creature can enter using the parent class method.<br>
	 * If successful, it adds the player to the internal tracking list.
	 * @param creature The {@code Creature} entering the zone.
	 * @return {@code true} if the creature successfully entered, otherwise {@code false}.
	 */
	@Override
	public synchronized boolean onEnter(Creature creature)
	{
		if (super.onEnter(creature))
		{
			if (creature instanceof Player)
			{
				players.put(creature.getObjectId(), (Player) creature);
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * This method is called when a {@link Creature} leaves the zone.<br>
	 * It removes the creature from the internal player list if it is a {@link Player}.
	 * @param creature The {@link Creature} that is leaving the zone.
	 * @return {@code true} if the leave action was successful, otherwise {@code false}.
	 */
	@Override
	public synchronized boolean onLeave(Creature creature)
	{
		if (super.onLeave(creature))
		{
			if (creature instanceof Player)
			{
				players.remove(creature.getObjectId());
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Iterates through all {@link Player} objects in the current location.<br>
	 * Applies the provided {@code Visitor} to each non-null player found.<br>
	 * Logs an error if any exception occurs during the process.
	 * @param visitor The {@code Visitor} to apply to every player.
	 */
	public void doOnAllPlayers(Visitor<Player> visitor)
	{
		try
		{
			for (Player player : players.values())
			{
				if (player != null)
				{
					visitor.visit(player);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("Exception when running visitor on all players" + ex);
		}
	}
}
