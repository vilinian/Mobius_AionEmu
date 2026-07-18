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
package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.shield.Shield;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.services.ShieldService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the logic and behavior for {@link Shield} objects in the game world.<br>
 * This controller handles interactions related to siege mechanics and shield placement.
 * @author Source
 */
public class ShieldController extends VisibleObjectController<Shield>
{
	Map<Integer, ActionObserver> observed = new ConcurrentHashMap<>();
	
	/**
	 * This method registers a {@link Player} as an observer.<br>
	 * It creates a new {@code FlyRingObserver} for the target.<br>
	 * The observer is added to the player's observation controller.
	 * @param object The {@code VisibleObject} representing the player to observe.
	 */
	@Override
	public void see(VisibleObject object)
	{
		final FortressLocation loc = SiegeService.getInstance().getFortress(getOwner().getId());
		final Player player = (Player) object;
		
		if (loc.isUnderShield())
		{
			if (loc.getRace() != SiegeRace.getByRace(player.getRace()))
			{
				final ActionObserver observer = ShieldService.getInstance().createShieldObserver(loc.getLocationId(), player);
				if (observer != null)
				{
					player.getObserveController().addObserver(observer);
					observed.put(player.getObjectId(), observer);
				}
			}
		}
	}
	
	/**
	 * Updates the visibility status of a specific object.<br>
	 * This method also clears the target if the object is the current target.
	 * @param object The {@code VisibleObject} that is no longer seen.
	 * @param isOutOfRange Whether the object is outside of the visible range.
	 */
	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange)
	{
		final FortressLocation loc = SiegeService.getInstance().getFortress(getOwner().getId());
		final Player player = (Player) object;
		
		if (loc.isUnderShield())
		{
			if (loc.getRace() != SiegeRace.getByRace(player.getRace()))
			{
				final ActionObserver observer = observed.remove(player.getObjectId());
				if (observer != null)
				{
					if (isOutOfRange)
					{
						observer.moved();
					}
					
					player.getObserveController().removeObserver(observer);
				}
			}
		}
	}
	
	/**
	 * Stops all active observations for this controller.<br>
	 * It removes all {@link ActionObserver} entries from the internal map.<br>
	 * Any associated {@link Player} will have their observer removed.
	 */
	public void disable()
	{
		for (Map.Entry<Integer, ActionObserver> e : observed.entrySet())
		{
			final ActionObserver observer = observed.remove(e.getKey());
			final Player player = World.getInstance().findPlayer(e.getKey());
			if (player != null)
			{
				player.getObserveController().removeObserver(observer);
			}
		}
	}
}
