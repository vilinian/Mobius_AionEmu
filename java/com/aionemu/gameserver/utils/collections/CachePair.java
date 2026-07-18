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

/**
 * A simple utility class that stores a key-value pair.<br>
 * It is commonly used for caching data in {@code Map} structures.<br>
 * This class provides an easy way to group two related objects together.
 * @author Rolandas
 * @param <K>
 * @param <V>
 */
@SuppressWarnings(
{
	"unchecked",
	"rawtypes"
})
public class CachePair<K extends Comparable, V> implements Comparable<CachePair>
{
	public CachePair(K key, V value)
	{
		this.key = key;
		this.value = value;
	}
	
	public K key;
	public V value;
	
	/**
	 * Compares this object with another object for equality.<br>
	 * It checks if the other object is an instance of {@link CachePair}.<br>
	 * Two pairs are equal if both their keys and values are equal.
	 * @param obj The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CachePair)
		{
			final CachePair p = (CachePair) obj;
			return key.equals(p.key) && value.equals(p.value);
		}
		
		return false;
	}
	
	/**
	 * Compares this {@code CachePair} with another {@code CachePair}.<br>
	 * It first compares the keys.<br>
	 * If the keys are equal, it compares the values if they are comparable.
	 * @param p The other {@code CachePair} to compare against.
	 * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(CachePair p)
	{
		final int v = key.compareTo(p.key);
		if ((v == 0) && (p.value instanceof Comparable))
		{
			return ((Comparable) value).compareTo(p.value);
		}
		
		return v;
	}
	
	/**
	 * Returns a hash code value for this {@link CachePair} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code key} and {@code value} fields.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		int result = key.hashCode();
		result = (37 * result) + value.hashCode();
		return result;
	}
	
	/**
	 * Returns a string representation of this {@code CachePair}.<br>
	 * It combines the key and the value into a single string.<br>
	 * The format is {@code key: value}.
	 * @return A string representing the pair.
	 */
	@Override
	public String toString()
	{
		return key + ": " + value;
	}
}
