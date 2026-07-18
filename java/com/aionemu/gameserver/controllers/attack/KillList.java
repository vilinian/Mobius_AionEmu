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
package com.aionemu.gameserver.controllers.attack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.configs.main.CustomConfig;

/**
 * Manages a collection of targets for specific combat actions.<br>
 * It allows the system to track and process entities that need to be targeted or eliminated.
 * @author Sarynth
 */
public class KillList
{
	private final Map<Integer, List<Long>> killList;
	
	/**
	 * Creates a new instance of the {@code KillList} class.<br>
	 * This initializes an empty map to store kill records.
	 */
	public KillList()
	{
		killList = new HashMap<>();
	}
	
	/**
	 * Retrieves the number of kills for a specific victim.<br>
	 * This method filters out old kills based on {@code CustomConfig.PVP_DAY_DURATION}.<br>
	 * It returns 0 if no kill records exist for the given ID.
	 * @param victimId The unique identifier of the victim to check.
	 * @return The total count of valid kills within the allowed duration.
	 */
	public int getKillsFor(int victimId)
	{
		final List<Long> killTimes = killList.get(victimId);
		
		if (killTimes == null)
		{
			return 0;
		}
		
		final long now = System.currentTimeMillis();
		int killCount = 0;
		
		for (Iterator<Long> i = killTimes.iterator(); i.hasNext();)
		{
			if ((now - i.next().longValue()) > CustomConfig.PVP_DAY_DURATION)
			{
				i.remove();
			}
			else
			{
				killCount++;
			}
		}
		
		return killCount;
	}
	
	/**
	 * Records a new kill for a specific victim.<br>
	 * This method updates the {@code killList} with the current system time.<br>
	 * If the {@code victimId} does not exist, it creates a new entry.
	 * @param victimId The unique identifier of the victim.
	 */
	public void addKillFor(int victimId)
	{
		List<Long> killTimes = killList.get(victimId);
		if (killTimes == null)
		{
			killTimes = new ArrayList<>();
			killList.put(victimId, killTimes);
		}
		
		killTimes.add(System.currentTimeMillis());
	}
}
