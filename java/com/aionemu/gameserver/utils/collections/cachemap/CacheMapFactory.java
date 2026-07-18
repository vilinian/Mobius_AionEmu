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

import com.aionemu.gameserver.configs.main.CacheConfig;

/**
 * This factory class provides methods to create and initialize various {@code Map} implementations.<br>
 * It simplifies the creation of cache maps based on the configurations defined in {@link CacheConfig}.
 * @author Luno
 */
public class CacheMapFactory
{
	/**
	 * Creates a new instance of a {@link CacheMap}.<br>
	 * It chooses between soft or weak cache types based on the system configuration.
	 * @param <K> The type of keys to be stored in the map.
	 * @param <V> The type of values to be stored in the map.
	 * @param cacheName The unique name for this specific cache.
	 * @param valueName A descriptive name for the values held within the cache.
	 * @return A new {@link CacheMap} instance based on the current configuration.
	 */
	public static <K, V> CacheMap<K, V> createCacheMap(String cacheName, String valueName)
	{
		if (CacheConfig.SOFT_CACHE_MAP)
		{
			return createSoftCacheMap(cacheName, valueName);
		}
		
		return createWeakCacheMap(cacheName, valueName);
	}
	
	/**
	 * Creates a new instance of a {@link SoftCacheMap}.<br>
	 * This map uses soft references for its values.<br>
	 * It is useful for memory-sensitive caching.
	 * @param <K> The type of keys to store in the map.
	 * @param <V> The type of values to store in the map.
	 * @param cacheName The unique name assigned to this cache.
	 * @param valueName A descriptive name for the values stored within.
	 * @return A new {@code SoftCacheMap} instance.
	 */
	public static <K, V> CacheMap<K, V> createSoftCacheMap(String cacheName, String valueName)
	{
		return new SoftCacheMap<>(cacheName, valueName);
	}
	
	/**
	 * Creates a new instance of a {@link WeakCacheMap}.<br>
	 * This map uses weak references for its entries.<br>
	 * It is useful for preventing memory leaks in the cache.
	 * @param <K> The type of keys maintained by this map.
	 * @param <V> The type of mapped values.
	 * @param cacheName The unique name assigned to this cache.
	 * @param valueName A descriptive name for the values stored within.
	 * @return A new {@link CacheMap} instance.
	 */
	public static <K, V> CacheMap<K, V> createWeakCacheMap(String cacheName, String valueName)
	{
		return new WeakCacheMap<>(cacheName, valueName);
	}
}
