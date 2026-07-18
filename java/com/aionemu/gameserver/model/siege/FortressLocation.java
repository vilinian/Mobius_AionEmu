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

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeLegionReward;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeReward;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * Represents a specific location for a fortress within the siege system.<br>
 * This class extends {@link SiegeLocation} to provide data related to fortress-specific mechanics.
 * @author Source
 */
public class FortressLocation extends SiegeLocation
{
	protected List<SiegeReward> siegeRewards;
	protected List<SiegeLegionReward> siegeLegionRewards;
	protected boolean isUnderAssault;
	
	/**
	 * Creates a new instance of {@link FortressLocation}.<br>
	 * This is the default constructor for fortress locations.
	 */
	public FortressLocation()
	{
	}
	
	/**
	 * Creates a new {@link FortressLocation} using a provided template.<br>
	 * This constructor initializes the rewards based on the {@code SiegeLocationTemplate}.
	 * @param template The {@code SiegeLocationTemplate} used to build this location.
	 */
	public FortressLocation(SiegeLocationTemplate template)
	{
		super(template);
		siegeRewards = template.getSiegeRewards() != null ? template.getSiegeRewards() : null;
		siegeLegionRewards = template.getSiegeLegionRewards() != null ? template.getSiegeLegionRewards() : null;
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
	 * Retrieves the list of rewards for a legion.<br>
	 * This method returns all {@link SiegeLegionReward} objects associated with this location.
	 * @return A {@code List} of {@link SiegeLegionReward} objects.
	 */
	public List<SiegeLegionReward> getLegionReward()
	{
		return siegeLegionRewards;
	}
	
	/**
	 * Determines if the given {@code Creature} is an enemy.<br>
	 * It compares the race ID of the {@code Creature} with the current object's race ID.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the races are different, {@code false} otherwise.
	 */
	public boolean isEnemy(Creature creature)
	{
		return creature.getRace().getRaceId() != getRace().getRaceId();
	}
	
	/**
	 * Checks if a {@link Player} is allowed to teleport into this location.<br>
	 * It verifies the player's race matches the required race for this area.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player can teleport, otherwise {@code false}.
	 */
	@Override
	public boolean isCanTeleport(Player player)
	{
		if (player == null)
		{
			return canTeleport;
		}
		
		return canTeleport && (player.getRace().getRaceId() == getRace().getRaceId());
	}
	
	/**
	 * Retrieves the {@link DescriptionId} associated with this location's name.<br>
	 * This ID is obtained from the underlying template.
	 * @return The {@code DescriptionId} of the location name.
	 */
	public DescriptionId getNameAsDescriptionId()
	{
		return new DescriptionId(template.getNameId());
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
	
	/**
	 * Clears all enemy entities from the current location.<br>
	 * It kills any {@code Kisk} objects found in the area.<br>
	 * It teleports all enemy {@link Player} instances to their bind locations.
	 */
	@Override
	public void clearLocation()
	{
		// TODO: not allow to place Kisk if siege will be soon
		for (Creature creature : getCreatures().values())
		{
			if (isEnemy(creature))
			{
				if (creature instanceof Kisk)
				{
					final Kisk kisk = (Kisk) creature;
					kisk.getController().die();
				}
			}
		}
		
		for (Player player : getPlayers().values())
		{
			if (isEnemy(player))
			{
				TeleportService2.moveToBindLocation(player, true);
			}
		}
	}
}
