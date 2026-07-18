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
import com.aionemu.gameserver.taskmanager.AbstractFIFOPeriodicTaskManager;

/**
 * This service handles periodic updates for all {@link Creature} objects within the game world.<br>
 * It ensures that entity states are refreshed consistently using a task management system.
 * @author ATracer
 */
public class ZoneUpdateService extends AbstractFIFOPeriodicTaskManager<Creature>
{
	/**
	 * Private constructor for the {@link ZoneUpdateService} class.<br>
	 * This prevents other classes from creating new instances directly.<br>
	 * Use {@code getInstance} to get the singleton instance.
	 */
	private ZoneUpdateService()
	{
		super(500);
	}
	
	/**
	 * Updates the zone information for a specific {@link Creature}.<br>
	 * This method refreshes the zone data via the creature's controller.<br>
	 * It also triggers a level check if the {@code creature} is an instance of {@link Player}.
	 * @param creature The {@code Creature} object to update.
	 */
	@Override
	protected void callTask(Creature creature)
	{
		creature.getController().refreshZoneImpl();
		if (creature instanceof Player)
		{
			ZoneLevelService.checkZoneLevels((Player) creature);
		}
	}
	
	/**
	 * Returns the name of the method that was called by this task.<br>
	 * This is used for internal tracking and logging purposes.
	 * @return The {@code String} name of the executed method.
	 */
	@Override
	protected String getCalledMethodName()
	{
		return "ZoneUpdateService()";
	}
	
	/**
	 * Gets the single shared instance of the {@link ZoneUpdateService}.<br>
	 * This method follows the singleton pattern.
	 * @return The global {@code ZoneUpdateService} instance.
	 */
	public static ZoneUpdateService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ZoneUpdateService instance = new ZoneUpdateService();
	}
}
