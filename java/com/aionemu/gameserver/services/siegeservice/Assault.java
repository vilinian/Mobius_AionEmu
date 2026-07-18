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
package com.aionemu.gameserver.services.siegeservice;

import java.util.concurrent.Future;

import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.base.BaseTemplate;

/**
 * This class provides the base logic for handling assault mechanics during sieges.<br>
 * It manages interactions between players and {@link SiegeNpc} entities within a {@link SiegeLocation}.
 * @author Luzien
 * @param <siege>
 */
public abstract class Assault<siege extends Siege<?>>
{
	protected final SiegeLocation siegeLocation;
	protected final int locationId;
	protected final SiegeNpc boss;
	protected final int worldId;
	protected Future<?> dredgionTask;
	protected Future<?> spawnTask;
	
	/**
	 * Creates a new {@code Assault} instance.<br>
	 * This constructor initializes the assault data from a {@link Siege} object.<br>
	 * It extracts the location, boss, and world ID information.
	 * @param siege The {@code Siege} object containing the required data.
	 */
	public Assault(Siege<?> siege)
	{
		siegeLocation = siege.getSiegeLocation();
		boss = siege.getBoss();
		locationId = siege.getSiegeLocationId();
		worldId = siege.getSiegeLocation().getWorldId();
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return worldId;
	}
	
	/**
	 * Starts the assault sequence after a specific wait time.<br>
	 * This method calls {@code scheduleAssault} to begin the process.
	 * @param delay The amount of time in milliseconds to wait before starting.
	 */
	public void startAssault(int delay)
	{
		scheduleAssault(delay);
	}
	
	/**
	 * Ends the current assault process.<br>
	 * This method cancels any active tasks like {@code dredgionTask} or {@code spawnTask}.<br>
	 * It then triggers the final logic based on whether the objective was met.
	 * @param captured Indicates if the players successfully took control of the location.
	 */
	public void finishAssault(boolean captured)
	{
		if ((dredgionTask != null) && !dredgionTask.isDone())
		{
			dredgionTask.cancel(true);
		}
		
		if ((spawnTask != null) && !spawnTask.isDone())
		{
			spawnTask.cancel(true);
		}
		
		onAssaultFinish(captured && siegeLocation.getRace().equals(SiegeRace.BALAUR));
	}
	
	protected abstract void onAssaultFinish(boolean captured);
	
	protected abstract void scheduleAssault(int delay);
}
