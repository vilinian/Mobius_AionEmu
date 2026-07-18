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
import java.lang.ref.WeakReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a simple map implementation designed for caching purposes.<br>
 * Values are automatically removed during the first garbage collection cycle if no strong references to them exist.
 * @author Luno
 * @param <K>
 * @param <V>
 */
class WeakCacheMap<K, V> extends AbstractCacheMap<K, V>
{
	private static final Logger log = LoggerFactory.getLogger(WeakCacheMap.class);
	
	/**
	 * This class is a {@link WeakReference} with additional responsibility of holding key object
	 * @author Luno
	 */
	private class Entry extends WeakReference<V>
	{
		private final K key;
		
		Entry(K key, V referent, ReferenceQueue<? super V> q)
		{
			super(referent, q);
			this.key = key;
		}
		
		K getKey()
		{
			return key;
		}
	}
	
	/**
	 * Creates a new instance of {@code WeakCacheMap}.<br>
	 * This constructor initializes the cache with specific names.
	 * @param cacheName The name assigned to the cache.
	 * @param valueName The name assigned to the values within the cache.
	 */
	WeakCacheMap(String cacheName, String valueName)
	{
		super(cacheName, valueName, log);
	}
	
	/**
	 * This method removes expired entries from the internal cache.<br>
	 * It processes all items currently waiting in the {@code ReferenceQueue}.<br>
	 * Each removed item is deleted from the underlying map using its key.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected synchronized void cleanQueue()
	{
		Entry en = null;
		while ((en = (Entry) refQueue.poll()) != null)
		{
			final K key = en.getKey();
			if (log.isDebugEnabled())
			{
				log.debug(cacheName + " : cleaned up " + valueName + " for key: " + key);
			}
			
			cacheMap.remove(key);
		}
	}
	
	/**
	 * Creates a new {@code Entry} for the cache.<br>
	 * This method wraps the value in a {@link WeakReference}.<br>
	 * It associates the provided key with the reference.
	 * @param key The key associated with the value.
	 * @param value The value to be stored in the cache.
	 * @param vReferenceQueue The queue used to track cleared references.
	 * @return A new {@code Reference} object containing the value.
	 */
	@Override
	protected Reference<V> newReference(K key, V value, ReferenceQueue<V> vReferenceQueue)
	{
		return new Entry(key, value, vReferenceQueue);
	}
}
