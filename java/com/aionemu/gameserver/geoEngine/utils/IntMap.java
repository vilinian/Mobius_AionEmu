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
package com.aionemu.gameserver.geoEngine.utils;

import java.util.Iterator;

import com.aionemu.gameserver.geoEngine.utils.IntMap.Entry;

/**
 * A specialized map implementation that uses {@code Integer} keys.<br>
 * It provides an efficient way to store and retrieve values associated with integer identifiers.
 * @author Nate
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public final class IntMap<T> implements Iterable<Entry>, Cloneable
{
	private Entry[] table;
	private final float loadFactor;
	private int size, mask, capacity, threshold;
	
	/**
	 * Creates a new empty {@link IntMap} instance.<br>
	 * This constructor uses default settings for capacity and load factor.<br>
	 * The map is initialized with an initial capacity of {@code 16}.
	 */
	public IntMap()
	{
		this(16, 0.75f);
	}
	
	/**
	 * Creates a new {@link IntMap} with a specific starting size.<br>
	 * This helps prevent frequent resizing of the internal table.<br>
	 * The default load factor is set to {@code 0.75f}.
	 * @param initialCapacity The initial number of slots in the map.
	 */
	public IntMap(int initialCapacity)
	{
		this(initialCapacity, 0.75f);
	}
	
	/**
	 * Creates a new {@link IntMap} with a specific size and growth rate.<br>
	 * This constructor initializes the internal table based on your settings.
	 * @param initialCapacity The starting number of slots for the map.
	 * @param loadFactor The ratio used to determine when to resize the map.
	 */
	public IntMap(int initialCapacity, float loadFactor)
	{
		if (initialCapacity > (1 << 30))
		{
			throw new IllegalArgumentException("initialCapacity is too large.");
		}
		
		if ((initialCapacity < 0) || (loadFactor <= 0))
		{
			throw new IllegalArgumentException("initialCapacity must be greater than zero.");
		}
		
		capacity = 1;
		while (capacity < initialCapacity)
		{
			capacity <<= 1;
		}
		
		this.loadFactor = loadFactor;
		threshold = (int) (capacity * loadFactor);
		table = new Entry[capacity];
		mask = capacity - 1;
	}
	
	/**
	 * Creates a deep copy of this {@link IntMap}.<br>
	 * This method copies all entries into a new instance.<br>
	 * It ensures that the internal table is independent of the original.
	 * @return A new {@code IntMap} containing the same data, or {@code null} if cloning fails.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public IntMap<T> clone()
	{
		try
		{
			final IntMap<T> clone = (IntMap<T>) super.clone();
			final Entry[] newTable = new Entry[table.length];
			for (int i = table.length - 1; i >= 0; i--)
			{
				if (table[i] != null)
				{
					newTable[i] = table[i].clone();
				}
			}
			
			clone.table = newTable;
			return clone;
		}
		catch (CloneNotSupportedException ex)
		{
		}
		
		return null;
	}
	
	/**
	 * Checks if the map contains a specific value.<br>
	 * It searches through all entries in the {@link IntMap}.<br>
	 * Returns {@code true} if a match is found.
	 * @param value The object to search for.
	 * @return {@code true} if the value exists, otherwise {@code false}.
	 */
	public boolean containsValue(Object value)
	{
		final Entry[] table = this.table;
		for (int i = table.length; i-- > 0;)
		{
			for (Entry e = table[i]; e != null; e = e.next)
			{
				if (e.value.equals(value))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the map contains a specific key.<br>
	 * It searches for the provided {@code int} value in the internal table.
	 * @param key The integer key to search for.
	 * @return {@code true} if the key exists, otherwise {@code false}.
	 */
	public boolean containsKey(int key)
	{
		final int index = (key) & mask;
		for (Entry e = table[index]; e != null; e = e.next)
		{
			if (e.key == key)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves the value associated with a specific key.<br>
	 * This method searches for the entry matching {@code key}.<br>
	 * It returns {@code null} if the key is not found.
	 * @param key The integer key to look up.
	 * @return The value associated with the key, or {@code null} if it does not exist.
	 */
	@SuppressWarnings("unchecked")
	public T get(int key)
	{
		final int index = key & mask;
		for (Entry e = table[index]; e != null; e = e.next)
		{
			if (e.key == key)
			{
				return (T) e.value;
			}
		}
		
		return null;
	}
	
	/**
	 * Associates the specified {@code value} with the given {@code key}.<br>
	 * If the {@code key} already exists, the old value is replaced.<br>
	 * This method may trigger a rehash if the internal capacity is exceeded.
	 * @param key The integer identifier for the entry.
	 * @param value The object to store in the map.
	 * @return The previous value associated with {@code key}, or {@code null} if there was no mapping.
	 */
	@SuppressWarnings("unchecked")
	public T put(int key, T value)
	{
		int index = key & mask;
		
		// Check if key already exists.
		for (Entry e = table[index]; e != null; e = e.next)
		{
			if (e.key != key)
			{
				continue;
			}
			
			final Object oldValue = e.value;
			e.value = value;
			return (T) oldValue;
		}
		
		table[index] = new Entry(key, value, table[index]);
		if (size++ >= threshold)
		{
			// Rehash.
			final int newCapacity = 2 * capacity;
			final Entry[] newTable = new Entry[newCapacity];
			final Entry[] src = table;
			final int bucketmask = newCapacity - 1;
			for (int j = 0; j < src.length; j++)
			{
				Entry e = src[j];
				if (e != null)
				{
					src[j] = null;
					do
					{
						final Entry next = e.next;
						index = e.key & bucketmask;
						e.next = newTable[index];
						newTable[index] = e;
						e = next;
					}
					while (e != null);
				}
			}
			
			table = newTable;
			capacity = newCapacity;
			threshold = (int) (newCapacity * loadFactor);
			mask = capacity - 1;
		}
		
		return null;
	}
	
	/**
	 * Removes the entry associated with the specified {@code key}.<br>
	 * This method updates the internal size of the map.
	 * @param key The integer key to be removed from the map.
	 * @return The value that was previously associated with the {@code key}, or {@code null} if the key did not exist.
	 */
	@SuppressWarnings("unchecked")
	public T remove(int key)
	{
		final int index = key & mask;
		Entry prev = table[index];
		Entry e = prev;
		while (e != null)
		{
			final Entry next = e.next;
			if (e.key == key)
			{
				size--;
				if (prev == e)
				{
					table[index] = next;
				}
				else
				{
					prev.next = next;
				}
				
				return (T) e.value;
			}
			
			prev = e;
			e = next;
		}
		
		return null;
	}
	
	/**
	 * Returns the number of elements in this map.<br>
	 * This value represents the current count of key-value pairs stored.
	 * @return The total number of items currently in the map.
	 */
	public int size()
	{
		return size;
	}
	
	/**
	 * Removes all elements from the map.<br>
	 * The {@code size()} will become 0 after this call.<br>
	 * This method does not affect other collections.
	 */
	public void clear()
	{
		final Entry[] table = this.table;
		for (int index = table.length; --index >= 0;)
		{
			table[index] = null;
		}
		
		size = 0;
	}
	
	/**
	 * Returns an {@link Iterator} to traverse the entries in this map.<br>
	 * Use this to loop through all key-value pairs.
	 * @return An {@code Iterator} of {@link Entry} objects.
	 */
	@Override
	public Iterator<Entry> iterator()
	{
		return new IntMapIterator();
	}
	
	final class IntMapIterator implements Iterator<Entry>
	{
		/**
		 * Current entry.
		 */
		private Entry cur;
		/**
		 * Entry in the table
		 */
		private int idx = 0;
		/**
		 * Element in the entry
		 */
		private int el = 0;
		
		public IntMapIterator()
		{
			cur = table[0];
		}
		
		@Override
		public boolean hasNext()
		{
			return el < size;
		}
		
		@Override
		public Entry next()
		{
			if (el >= size)
			{
				throw new IllegalStateException("No more elements!");
			}
			
			if (cur != null)
			{
				final Entry e = cur;
				cur = cur.next;
				el++;
				return e;
			}
			
			// if (cur != null && cur.next != null){
			// If there is a current entry, move to the next one in the list.
			// el++;
			// return cur;
			// }
			
			do
			{
				// Find another non-null entry because the current entry list is exhausted or the entry was null.
				cur = table[++idx];
			}
			while (cur == null);
			final Entry e = cur;
			cur = cur.next;
			el++;
			return e;
		}
		
		@Override
		public void remove()
		{
		}
	}
	
	public static final class Entry<T> implements Cloneable
	{
		final int key;
		T value;
		Entry next;
		
		Entry(int k, T v, Entry n)
		{
			key = k;
			value = v;
			next = n;
		}
		
		public int getKey()
		{
			return key;
		}
		
		public T getValue()
		{
			return value;
		}
		
		@Override
		public String toString()
		{
			return key + " => " + value;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Entry<T> clone()
		{
			try
			{
				final Entry<T> clone = (Entry<T>) super.clone();
				clone.next = next != null ? next.clone() : null;
				return clone;
			}
			catch (CloneNotSupportedException ex)
			{
			}
			
			return null;
		}
	}
}
