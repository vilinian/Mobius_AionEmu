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
package com.aionemu.gameserver.utils.collections.cachemap;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

/**
 * This is the base class for {@link WeakCacheMap} and {@link SoftCacheMap}.<br>
 * It provides common functionality for specialized cache map implementations.
 * @param <K>
 * @param <V>
 * @author Luno
 */
abstract class AbstractCacheMap<K, V> implements CacheMap<K, V>
{
	private final Logger log;
	protected final String cacheName;
	protected final String valueName;
	/**
	 * Map storing references to cached objects
	 */
	protected final Map<K, Reference<V>> cacheMap = new HashMap<>();
	protected final ReferenceQueue<V> refQueue = new ReferenceQueue<>();
	
	/**
	 * Initializes a new instance of the abstract cache map.<br>
	 * Sets up the internal names and logger for the cache.
	 * @param cacheName The name used to identify the cache in logs.
	 * @param valueName The name assigned to the values stored in this cache.
	 * @param log The {@code Logger} instance used for recording events.
	 */
	AbstractCacheMap(String cacheName, String valueName, Logger log)
	{
		this.cacheName = "#CACHE  [" + cacheName + "]#  ";
		this.valueName = valueName;
		this.log = log;
	}
	
	/**
	 * Adds a new entry to the cache.<br>
	 * This method cleans the reference queue before adding the item.<br>
	 * It throws an {@code IllegalArgumentException} if the {@code key} already exists.
	 * @param key The unique identifier for the data.
	 * @param value The data to be stored in the cache.
	 */
	@Override
	public void put(K key, V value)
	{
		cleanQueue();
		
		if (cacheMap.containsKey(key))
		{
			throw new IllegalArgumentException("Key: " + key + " already exists in map");
		}
		
		final Reference<V> entry = newReference(key, value, refQueue);
		
		cacheMap.put(key, entry);
		
		if (log.isDebugEnabled())
		{
			log.debug(cacheName + " : added " + valueName + " for key: " + key);
		}
	}
	
	/**
	 * Retrieves the value associated with the specified {@code key}.<br>
	 * This method moves the accessed item to the head of the cache.<br>
	 * It returns {@code null} if the key is not found.
	 * @param key The key to look up in the cache.
	 * @return The value associated with the key, or {@code null} if it does not exist.
	 */
	@Override
	public V get(K key)
	{
		cleanQueue();
		
		final Reference<V> reference = cacheMap.get(key);
		
		if (reference == null)
		{
			return null;
		}
		
		final V res = reference.get();
		
		if ((res != null) && log.isDebugEnabled())
		{
			log.debug(cacheName + " : obtained " + valueName + " for key: " + key);
		}
		
		return res;
	}
	
	/**
	 * Checks if the map contains a specific key.<br>
	 * This method cleans up expired references before checking.
	 * @param key The key to search for in the map.
	 * @return {@code true} if the key exists, otherwise {@code false}.
	 */
	@Override
	public boolean contains(K key)
	{
		cleanQueue();
		return cacheMap.containsKey(key);
	}
	
	protected abstract void cleanQueue();
	
	/**
	 * Removes the entry associated with the specified {@code key}.<br>
	 * This method checks if the {@code key} exists in the map.<br>
	 * If it exists, it removes both the mapping and the internal item.<br>
	 * If the {@code key} is {@code null}, no action is taken.
	 * @param key The unique identifier for the entry to be removed.
	 */
	@Override
	public void remove(K key)
	{
		cacheMap.remove(key);
	}
	
	protected abstract Reference<V> newReference(K key, V value, ReferenceQueue<V> queue);
}
