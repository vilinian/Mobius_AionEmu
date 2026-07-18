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
package com.aionemu.gameserver.utils.collections;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides a thread-safe cache to store and track the most recently used items.<br>
 * It helps manage memory by keeping frequently accessed data readily available in a {@code ConcurrentHashMap}.
 * @author Rolandas
 * @param <K>
 * @param <V>
 */
@SuppressWarnings(
{
	"unchecked",
	"rawtypes"
})
public class LastUsedCache<K extends Comparable, V> implements ICache<K, V>, Serializable
{
	private static final long serialVersionUID = 3674312987828041877L;
	Map<K, Item> map = new ConcurrentHashMap<>();
	Item startItem = new Item();
	Item endItem = new Item();
	int maxSize;
	private final Object syncRoot = new Object();
	
	static class Item
	{
		public Item(Comparable k, Object v)
		{
			key = k;
			value = v;
		}
		
		public Item()
		{
		}
		
		public Comparable key;
		public Object value;
		public Item previous;
		public Item next;
	}
	
	/**
	 * Removes a specific {@code Item} from the cache.<br>
	 * This method updates the links between neighboring items.<br>
	 * It ensures the internal list remains connected correctly.
	 * @param item The {@code Item} to be removed from the collection.
	 */
	void removeItem(Item item)
	{
		synchronized (syncRoot)
		{
			item.previous.next = item.next;
			item.next.previous = item.previous;
		}
	}
	
	/**
	 * Adds a new {@code Item} to the beginning of the list.<br>
	 * This method updates the pointers for the {@code startItem}.<br>
	 * It ensures the new item becomes the first element in the cache.
	 * @param item The {@code Item} to insert at the head.
	 */
	void insertHead(Item item)
	{
		synchronized (syncRoot)
		{
			item.previous = startItem;
			item.next = startItem.next;
			startItem.next.previous = item;
			startItem.next = item;
		}
	}
	
	/**
	 * Moves the specified {@code Item} to the front of the cache.<br>
	 * This updates the internal linked list structure.<br>
	 * It ensures the item is marked as most recently used.
	 * @param item The {@code Item} to move to the head position.
	 */
	void moveToHead(Item item)
	{
		synchronized (syncRoot)
		{
			item.previous.next = item.next;
			item.next.previous = item.previous;
			item.previous = startItem;
			item.next = startItem.next;
			startItem.next.previous = item;
			startItem.next = item;
		}
	}
	
	/**
	 * Creates a new instance of {@link LastUsedCache}.<br>
	 * This constructor sets the maximum number of objects allowed in the cache.<br>
	 * It also initializes the internal linked list structure.
	 * @param maxObjects The maximum capacity for the cache.
	 */
	public LastUsedCache(int maxObjects)
	{
		maxSize = maxObjects;
		startItem.next = endItem;
		endItem.previous = startItem;
	}
	
	/**
	 * Retrieves all items currently stored in the cache.<br>
	 * The items are returned in their current order.
	 * @return an array of {@code CachePair} objects containing all entries.
	 */
	@Override
	public CachePair[] getAll()
	{
		final CachePair p[] = new CachePair[maxSize];
		int count = 0;
		
		synchronized (syncRoot)
		{
			Item cur = startItem.next;
			while (cur != endItem)
			{
				p[count] = new CachePair(cur.key, cur.value);
				count++;
				cur = cur.next;
			}
		}
		
		final CachePair np[] = new CachePair[count];
		System.arraycopy(p, 0, np, 0, count);
		return np;
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
		final Item cur = map.get(key);
		if (cur == null)
		{
			return null;
		}
		
		if (cur != startItem.next)
		{
			moveToHead(cur);
		}
		
		return (V) cur.value;
	}
	
	/**
	 * Adds a new key-value pair to the cache.<br>
	 * If the {@code key} already exists, its value is updated and moved to the head.<br>
	 * If the cache is full, the least recently used item is removed before adding the new entry.
	 * @param key The unique identifier for the data.
	 * @param value The data to be stored in the cache.
	 */
	@Override
	public void put(K key, V value)
	{
		Item cur = map.get(key);
		if (cur != null)
		{
			cur.value = value;
			moveToHead(cur);
			return;
		}
		
		if ((map.size() >= maxSize) && (maxSize != 0))
		{
			cur = endItem.previous;
			map.remove(cur.key);
			removeItem(cur);
		}
		
		final Item item = new Item(key, value);
		insertHead(item);
		map.put(key, item);
	}
	
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
		final Item cur = map.get(key);
		if (cur == null)
		{
			return;
		}
		
		map.remove(key);
		removeItem(cur);
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	@Override
	public int size()
	{
		return map.size();
	}
}
