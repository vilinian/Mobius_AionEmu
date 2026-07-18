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

import com.aionemu.gameserver.controllers.observer.RoadObserver;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.road.Road;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class manages the logic and behavior for {@link Road} objects in the game world.<br>
 * It handles interactions related to roads as part of the {@code VisibleObjectController} system.
 * @author SheppeR
 */
public class RoadController extends VisibleObjectController<Road>
{
	Map<Integer, RoadObserver> observed = new ConcurrentHashMap<>();
	
	/**
	 * This method registers a {@link Player} as an observer.<br>
	 * It creates a new {@code FlyRingObserver} for the target.<br>
	 * The observer is added to the player's observation controller.
	 * @param object The {@code VisibleObject} representing the player to observe.
	 */
	@Override
	public void see(VisibleObject object)
	{
		final Player p = (Player) object;
		final RoadObserver observer = new RoadObserver(getOwner(), p);
		p.getObserveController().addObserver(observer);
		observed.put(p.getObjectId(), observer);
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
		final Player p = (Player) object;
		final RoadObserver observer = observed.remove(p.getObjectId());
		if (isOutOfRange)
		{
			observer.moved();
		}
		
		p.getObserveController().removeObserver(observer);
	}
}
