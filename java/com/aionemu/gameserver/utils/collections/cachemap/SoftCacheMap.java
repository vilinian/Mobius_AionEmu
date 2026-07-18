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
import java.lang.ref.SoftReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a simple map implementation designed for caching purposes.<br>
 * It stores values using {@code SoftReference} to ensure they are removed by the garbage collector during low memory conditions.<br>
 * Use this when you want to keep data as long as possible without preventing memory reclamation.
 * @author Luno
 * @param <K>
 * @param <V>
 */
class SoftCacheMap<K, V> extends AbstractCacheMap<K, V>
{
	private static final Logger log = LoggerFactory.getLogger(SoftCacheMap.class);
	
	/**
	 * This class is a {@link SoftReference} with additional responsibility of holding key object
	 * @author Luno
	 */
	private class SoftEntry extends SoftReference<V>
	{
		private final K key;
		
		SoftEntry(K key, V referent, ReferenceQueue<? super V> q)
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
	 * Creates a new instance of {@link SoftCacheMap}.<br>
	 * This constructor initializes the cache with specific names.
	 * @param cacheName The name used to identify this cache.
	 * @param valueName The name assigned to the values stored in this cache.
	 */
	SoftCacheMap(String cacheName, String valueName)
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
		SoftEntry en = null;
		while ((en = (SoftEntry) refQueue.poll()) != null)
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
	 * Creates a new {@code SoftEntry} for the cache.<br>
	 * This method wraps the value in a {@link SoftReference}.<br>
	 * It associates the provided key with the reference.
	 * @param key The key associated with the value.
	 * @param value The value to be stored in the cache.
	 * @param vReferenceQueue The queue used to track cleared references.
	 * @return A new {@code Reference} object containing the value.
	 */
	@Override
	protected Reference<V> newReference(K key, V value, ReferenceQueue<V> vReferenceQueue)
	{
		return new SoftEntry(key, value, vReferenceQueue);
	}
}
