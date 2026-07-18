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
package com.aionemu.gameserver.model.siege;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.IActor;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.ShieldService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.handler.ZoneHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a shield object used during siege events.<br>
 * This class handles the behavior of shields which have a material ID of {@code 11} in the geo engine.
 * @author Rolandas
 */
public class SiegeShield implements ZoneHandler
{
	Map<Integer, IActor> observed = new HashMap<>();
	private final Spatial geometry;
	private int siegeLocationId;
	private boolean isEnabled = false;
	
	/**
	 * Creates a new {@link SiegeShield} instance.<br>
	 * This constructor initializes the shield with specific spatial data.
	 * @param geometry The {@code Spatial} object defining the shield area.
	 */
	public SiegeShield(Spatial geometry)
	{
		this.geometry = geometry;
	}
	
	/**
	 * Retrieves the spatial data for this shield.<br>
	 * This method returns the {@code Spatial} object used to define the area.
	 * @return The {@link Spatial} geometry of the siege shield.
	 */
	public Spatial getGeometry()
	{
		return geometry;
	}
	
	/**
	 * Handles logic when a {@code Creature} enters a specific {@code ZoneInstance}.<br>
	 * This method checks if the creature is a non-GM {@link Player}.<br>
	 * It kills players who enter the wrong faction base.
	 * @param creature The {@code Creature} entering the zone.
	 * @param zone The {@code ZoneInstance} being entered.
	 */
	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone)
	{
		if (!(creature instanceof Player))
		{
			return;
		}
		
		final Player player = (Player) creature;
		if (isEnabled || (siegeLocationId == 0))
		{
			final FortressLocation loc = SiegeService.getInstance().getFortress(siegeLocationId);
			if ((loc == null) || (loc.getRace() != SiegeRace.getByRace(player.getRace())))
			{
				final ActionObserver actor = ShieldService.getInstance().createShieldObserver(this, creature);
				if (actor instanceof IActor)
				{
					creature.getObserveController().addObserver(actor);
					observed.put(creature.getObjectId(), (IActor) actor);
				}
			}
		}
	}
	
	/**
	 * This method is called when a {@link Creature} leaves a {@link ZoneInstance}.<br>
	 * It removes the observer from the creature if it is a non-GM player.<br>
	 * The logic ensures that observation effects are cleared correctly.
	 * @param creature The {@link Creature} that is leaving the zone.
	 * @param zone The {@link ZoneInstance} being exited.
	 */
	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone)
	{
		final IActor actor = observed.get(creature.getObjectId());
		if (actor != null)
		{
			creature.getObserveController().removeObserver((ActionObserver) actor);
			observed.remove(creature.getObjectId());
			actor.abort();
		}
	}
	
	/**
	 * Updates the active status of this actor.<br>
	 * Use {@code true} to turn it on and {@code false} to turn it off.
	 * @param enable The new status for the actor.
	 */
	public void setEnabled(boolean enable)
	{
		isEnabled = enable;
	}
	
	/**
	 * Checks if the shield is currently active.<br>
	 * This method returns the current state of the {@code isEnabled} flag.
	 * @return {@code true} if the shield is enabled, {@code false} otherwise.
	 */
	public boolean isEnabled()
	{
		return isEnabled;
	}
	
	/**
	 * Retrieves the unique identifier for the current siege location.<br>
	 * This ID is used to identify which specific area is being contested.
	 * @return The {@code int} value of the siege location ID.
	 */
	public int getSiegeLocationId()
	{
		return siegeLocationId;
	}
	
	/**
	 * Sets the unique identifier for the siege location.<br>
	 * This value is used to identify which specific area is being targeted.
	 * @param siegeLocationId The {@code int} ID of the siege location.
	 */
	public void setSiegeLocationId(int siegeLocationId)
	{
		this.siegeLocationId = siegeLocationId;
	}
	
	/**
	 * Returns a string representation of the shield.<br>
	 * This includes the location ID, name, and world bounds.
	 * @return A formatted string containing the shield details.
	 */
	@Override
	public String toString()
	{
		return "LocId=" + siegeLocationId + "; Name=" + geometry.getName() + "; Bounds=" + geometry.getWorldBound();
	}
}
