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

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeReward;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * Represents a specific location where resources or items are sourced during siege events.<br>
 * This class extends {@link SiegeLocation} to provide specialized data for source-based mechanics.
 * @author Source
 */
public class SourceLocation extends SiegeLocation
{
	protected List<SiegeReward> siegeRewards;
	private boolean status;
	
	/**
	 * Creates a new instance of the {@code SourceLocation} class.<br>
	 * This is the default constructor for initializing a location object.
	 */
	public SourceLocation()
	{
	}
	
	/**
	 * Creates a new {@code SourceLocation} instance using a provided template.<br>
	 * This constructor initializes the rewards from the {@link SiegeLocationTemplate}.
	 * @param template The {@code SiegeLocationTemplate} used to populate this location.
	 */
	public SourceLocation(SiegeLocationTemplate template)
	{
		super(template);
		siegeRewards = template.getSiegeRewards() != null ? template.getSiegeRewards() : null;
	}
	
	/**
	 * Retrieves the list of rewards for this fortress location.<br>
	 * This method returns the {@code siegeRewards} collection.
	 * @return a {@code List} of {@link SiegeReward} objects.
	 */
	public List<SiegeReward> getReward()
	{
		return siegeRewards;
	}
	
	/**
	 * Checks if the current location is in the preparation phase.<br>
	 * This method returns the value of the {@code status} field.
	 * @return {@code true} if preparations are active, {@code false} otherwise.
	 */
	public boolean isPreparations()
	{
		return status;
	}
	
	/**
	 * Updates the preparation status of this location.<br>
	 * This method sets whether the area is currently ready for use.
	 * @param status The new {@code boolean} value to set for the preparation state.
	 */
	public void setPreparation(boolean status)
	{
		this.status = status;
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
		super.onEnterZone(creature, zone);
		if (isVulnerable())
		{
			creature.setInsideZoneType(ZoneType.SIEGE);
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
		super.onLeaveZone(creature, zone);
		if (isVulnerable())
		{
			creature.unsetInsideZoneType(ZoneType.SIEGE);
		}
	}
}
