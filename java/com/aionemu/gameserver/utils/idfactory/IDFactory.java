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
package com.aionemu.gameserver.utils.idfactory;

import java.util.BitSet;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.dao.GuideDAO;
import com.aionemu.gameserver.dao.HousesDAO;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.LegionDAO;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.dao.PlayerAchievementActionDAO;
import com.aionemu.gameserver.dao.PlayerAchievementDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerRegisteredItemsDAO;

/**
 * This class handles unique ID generation for all objects within the Aion-Emu system.<br>
 * It is fully thread-safe and enforces strict rules on ID usage.<br>
 * Any illegal operations will result in an {@link IDFactoryError}.
 * @author SoulKeeper
 */
public class IDFactory
{
	private static final Logger log = LoggerFactory.getLogger(IDFactory.class);
	/**
	 * Bitset that is used for all id's.<br>
	 * We are allowing BitSet to grow over time, so in the end it can be as big as {@code MAX_VALUE}
	 */
	private final BitSet idList;
	/**
	 * Synchronization of bitset
	 */
	private final ReentrantLock lock;
	/**
	 * Id that will be used as minimal on next id request
	 */
	private volatile int nextMinId = 1;
	
	/**
	 * Private constructor for the {@link IDFactory} class.<br>
	 * This constructor initializes the internal bitset and lock.<br>
	 * It also loads all existing IDs from various database DAOs.
	 */
	private IDFactory()
	{
		idList = new BitSet();
		lock = new ReentrantLock();
		lockIds(0);
		
		// Initialize used values in IDFactory by calling all IDFactoryAwareDAO implementations.
		lockIds(DAOManager.getDAO(PlayerDAO.class).getUsedIDs());
		lockIds(DAOManager.getDAO(InventoryDAO.class).getUsedIDs());
		lockIds(DAOManager.getDAO(PlayerRegisteredItemsDAO.class).getUsedIDs());
		lockIds(DAOManager.getDAO(LegionDAO.class).getUsedIDs());
		lockIds(DAOManager.getDAO(MailDAO.class).getUsedIDs());
		lockIds(DAOManager.getDAO(GuideDAO.class).getUsedIDs());
		lockIds(DAOManager.getDAO(HousesDAO.class).getUsedIDs());
		lockIds(DAOManager.getDAO(PlayerAchievementDAO.class).getUsedIDs());
		lockIds(DAOManager.getDAO(PlayerAchievementActionDAO.class).getUsedIDs());
		log.info("IDFactory: " + getUsedCount() + " id's used.");
	}
	
	/**
	 * Provides the global instance of the {@link IDFactory}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access the shared ID generation service.
	 * @return The single instance of {@code IDFactory}.
	 */
	public static IDFactory getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Retrieves the next available unique identifier.<br>
	 * This method finds a free bit in the internal {@code BitSet}.<br>
	 * It marks the selected ID as used and updates the minimum search index.
	 * @return The next available {@code int} id.
	 */
	public int nextId()
	{
		try
		{
			lock.lock();
			
			int id;
			if (nextMinId == Integer.MIN_VALUE)
			{
				// Error will be thrown few lines later, we have no more free id's.
				// BitSet will throw IllegalArgumentException if nextMinId is negative
				id = Integer.MIN_VALUE;
			}
			else
			{
				id = idList.nextClearBit(nextMinId);
			}
			
			// If the BitSet reaches Integer.MAX_VALUE and returns the last free ID before that, it will return Integer.MIN_VALUE as the next ID; therefore, we must catch this case and throw an error indicating no free IDs are left.
			if (id == Integer.MIN_VALUE)
			{
				throw new IDFactoryError("All id's are used, please clear your database");
			}
			
			idList.set(id);
			
			// It ok to have Integer OverFlow here, on next ID request IDFactory will throw error
			nextMinId = id + 1;
			return id;
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Reserves a set of unique identifiers in the system.<br>
	 * This method ensures that each {@code id} is not already taken.<br>
	 * It throws an {@link IDFactoryError} if any provided {@code id} is currently in use.
	 * @param ids The array of integer IDs to be locked.
	 */
	private void lockIds(int... ids)
	{
		try
		{
			lock.lock();
			for (int id : ids)
			{
				final boolean status = idList.get(id);
				if (status)
				{
					throw new IDFactoryError("ID " + id + " is already taken, fatal error!!!");
				}
				
				idList.set(id);
			}
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Reserves a set of unique identifiers in the system.<br>
	 * This method marks each ID as taken within the internal {@code BitSet}.<br>
	 * It throws an {@link IDFactoryError} if any provided ID is already in use.
	 * @param ids The collection of {@code Integer} values to lock.
	 */
	public void lockIds(Iterable<Integer> ids)
	{
		try
		{
			lock.lock();
			for (int id : ids)
			{
				final boolean status = idList.get(id);
				if (status)
				{
					throw new IDFactoryError("ID " + id + " is already taken, fatal error!!!");
				}
				
				idList.set(id);
			}
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Frees a specific ID so it can be reused by the {@code nextId} method.<br>
	 * This method updates the internal bitset and adjusts the minimum available ID.<br>
	 * It will throw an {@code IDFactoryError} if the provided ID is not currently in use.
	 * @param id The unique identifier to release.
	 */
	public void releaseId(int id)
	{
		try
		{
			lock.lock();
			final boolean status = idList.get(id);
			if (!status)
			{
				throw new IDFactoryError("ID " + id + " is not taken, can't release it.");
			}
			
			idList.clear(id);
			if ((id < nextMinId) || (nextMinId == Integer.MIN_VALUE))
			{
				nextMinId = id;
			}
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Releases a collection of IDs back into the pool.<br>
	 * This method marks the specified {@code Integer} values as available for reuse.<br>
	 * It updates the {@code nextMinId} if any released ID is smaller than the current minimum.
	 * @param ids The collection of {@link Integer} IDs to release.
	 */
	public void releaseIds(Collection<Integer> ids)
	{
		if (GenericValidator.isBlankOrNull(ids))
		{
			return;
		}
		
		try
		{
			lock.lock();
			for (Integer id : ids)
			{
				final boolean status = idList.get(id);
				if (!status)
				{
					throw new IDFactoryError("ID " + id + " is not taken, can't release it.");
				}
				
				idList.clear(id);
				if ((id < nextMinId) || (nextMinId == Integer.MIN_VALUE))
				{
					nextMinId = id;
				}
			}
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Returns the total number of IDs currently in use.<br>
	 * This method calculates the cardinality of the internal {@code BitSet}.
	 * @return The count of used IDs as an {@code int}.
	 */
	public int getUsedCount()
	{
		try
		{
			lock.lock();
			return idList.cardinality();
		}
		finally
		{
			lock.unlock();
		}
	}
	
	private static class SingletonHolder
	{
		protected static final IDFactory instance = new IDFactory();
	}
}
