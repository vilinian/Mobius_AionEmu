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
package com.aionemu.gameserver.services;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.PeriodicSaveConfig;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This service handles the automatic saving of game data at regular intervals.<br>
 * It ensures that player progress and world states are persisted to the database periodically.
 * @author ATracer
 */
public class PeriodicSaveService
{
	private static final Logger log = LoggerFactory.getLogger(PeriodicSaveService.class);
	private final Future<?> legionWhUpdateTask;
	
	/**
	 * Provides the global instance of the {@link PeriodicSaveService}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access the service from anywhere in the application.
	 * @return The single instance of {@code PeriodicSaveService}.
	 */
	public static PeriodicSaveService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor for the {@link PeriodicSaveService} class.<br>
	 * This prevents other classes from creating new instances of this service.<br>
	 * The service is accessed using the {@code getInstance} method.
	 */
	private PeriodicSaveService()
	{
		log.info("Init Periodic Save...");
		final int DELAY_LEGION_ITEM = PeriodicSaveConfig.LEGION_ITEMS * 1000;
		
		legionWhUpdateTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new LegionWhUpdateTask(), DELAY_LEGION_ITEM, DELAY_LEGION_ITEM);
	}
	
	private class LegionWhUpdateTask implements Runnable
	{
		@Override
		public void run()
		{
			log.debug("Legion WH update task started.");
			final long startTime = System.currentTimeMillis();
			final Iterator<Legion> legionsIterator = LegionService.getInstance().getCachedLegionIterator();
			int legionWhUpdated = 0;
			while (legionsIterator.hasNext())
			{
				final Legion legion = legionsIterator.next();
				final List<Item> allItems = legion.getLegionWarehouse().getItemsWithKinah();
				allItems.addAll(legion.getLegionWarehouse().getDeletedItems());
				try
				{
					/**
					 * 1. save items first
					 */
					DAOManager.getDAO(InventoryDAO.class).store(allItems, null, null, legion.getLegionId());
					
					/**
					 * 2. save item stones
					 */
					DAOManager.getDAO(ItemStoneListDAO.class).save(allItems);
				}
				catch (Exception ex)
				{
					log.error("Exception during periodic saving of legion WH", ex);
				}
				
				legionWhUpdated++;
			}
			
			final long workTime = System.currentTimeMillis() - startTime;
			log.debug("Legion WH update: " + workTime + " ms, legions: " + legionWhUpdated + ".");
		}
	}
	
	/**
	 * This method handles the cleanup process when the server shuts down.<br>
	 * It ensures that all remaining data is saved to the database.<br>
	 * It cancels any active tasks and runs a final save for legion warehouses.
	 */
	public void onShutdown()
	{
		log.info("Starting data save on shutdown.");
		
		// save legion warehouse
		legionWhUpdateTask.cancel(false);
		new LegionWhUpdateTask().run();
		log.info("Data successfully saved.");
	}
	
	private static class SingletonHolder
	{
		protected static final PeriodicSaveService instance = new PeriodicSaveService();
	}
}
