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
package com.aionemu.gameserver.services.gc;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.main.GSConfig;

/**
 * This class manages the periodic cleanup of unused objects in the game server.<br>
 * It runs as a background thread to prevent memory leaks and maintain performance.<br>
 * It ensures that the {@link GameServer} remains stable by reclaiming resources.
 * @author GiGatR00n v4.7.5.x
 */
public class GarbageCollector extends Thread
{
	private static final Logger log = LoggerFactory.getLogger(GarbageCollector.class);
	
	private static long g_Period = (30 * 60 * 1000); // 30 minutes
	
	/**
	 * Initializes a new instance of the {@link GarbageCollector}.<br>
	 * This constructor sets the memory optimization period based on {@code GSConfig.GC_OPTIMIZATION_TIME}.<br>
	 * It ensures the interval is valid for the background thread to run correctly.
	 */
	public GarbageCollector()
	{
		g_Period = (GSConfig.GC_OPTIMIZATION_TIME < 1) ? 30 : GSConfig.GC_OPTIMIZATION_TIME;
		g_Period = g_Period * 60 * 1000;
	}
	
	/**
	 * instantiate class
	 */
	private static class SingletonHolder
	{
		protected static final GarbageCollector instance = new GarbageCollector();
	}
	
	/**
	 * Retrieves the single instance of the {@link GarbageCollector}.<br>
	 * This method follows the singleton pattern.
	 * @return The global {@code GarbageCollector} instance.
	 */
	public static GarbageCollector getInstance()
	{
		return SingletonHolder.instance;
	}
	
	@Override
	public void run()
	{
		if (GSConfig.ENABLE_MEMORY_GC)
		{
			GameServer.log.info("[GarbageCollector] Garbage Collector is scheduled to start in " + String.valueOf((g_Period / 1000) / 60) + " minutes.");
			StartMemoryOptimization();
		}
		else
		{
			GameServer.log.info("[GarbageCollector] Garbage Collector is turned off by administrator.");
		}
	}
	
	/**
	 * Starts a background task to optimize memory usage.<br>
	 * This method schedules a {@code TimerTask} to run periodically.<br>
	 * It checks the {@code GSConfig.ENABLE_MEMORY_GC} setting before running {@code System.gc()}.<br>
	 * The interval is determined by {@code GSConfig.GC_OPTIMIZATION_TIME}.
	 */
	private void StartMemoryOptimization()
	{
		final Timer t = new Timer();
		t.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				try
				{
					// When we reload configs, it need to initialized again.
					g_Period = (GSConfig.GC_OPTIMIZATION_TIME < 1) ? 30 : GSConfig.GC_OPTIMIZATION_TIME;
					g_Period = g_Period * 60 * 1000;
					
					if (GSConfig.ENABLE_MEMORY_GC)
					{
						log.info("[GarbageCollector] Garbage Collector is optimizing memory to free unused heap memory.");
						System.gc();
						log.info("[GarbageCollector] Garbage Collector has finished optimizing memory.");
					}
				}
				catch (Exception e)
				{
					log.error("[GarbageCollector] Error on optimizing memory: " + e.getMessage());
				}
			}
		}, g_Period);
	}
}
