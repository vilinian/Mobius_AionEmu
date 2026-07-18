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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Provides utility methods to split a {@link List} into multiple smaller sub-lists.<br>
 * This class helps simplify the process of partitioning collections based on specific sizes or criteria.
 * @author xTz
 * @param <T>
 */
public class ListSplitter<T>
{
	private T[] objects;
	private Class<?> componentType;
	private int splitCount;
	private int curentIndex = 0;
	private int length = 0;
	
	/**
	 * Creates a new {@link ListSplitter} from a provided collection.<br>
	 * This method prepares the data to be divided into smaller parts.<br>
	 * It initializes the internal array and sets the number of splits.
	 * @param collection The source collection of items to split.
	 * @param splitCount The total number of parts to divide the collection into.
	 */
	@SuppressWarnings("unchecked")
	public ListSplitter(Collection<T> collection, int splitCount)
	{
		if ((collection != null) && (collection.size() > 0))
		{
			this.splitCount = splitCount;
			length = collection.size();
			objects = collection.toArray((T[]) new Object[length]);
			componentType = objects.getClass().getComponentType();
		}
	}
	
	/**
	 * Retrieves the next chunk of items from the collection.<br>
	 * This method updates the internal {@code splitCount}.<br>
	 * It then calls the {@code getNext} method to return the list.
	 * @param splitCount The number of items to include in each chunk.
	 * @return A {@code List} containing the next set of elements.
	 */
	public List<T> getNext(int splitCount)
	{
		this.splitCount = splitCount;
		return getNext();
	}
	
	/**
	 * Retrieves the next chunk of items from the collection.<br>
	 * It moves the internal pointer forward after each call.<br>
	 * Returns an empty list if no more items are available.
	 * @return A {@code List<T>} containing the next set of elements.
	 */
	public List<T> getNext()
	{
		@SuppressWarnings("unchecked")
		final T[] subArray = (T[]) Array.newInstance(componentType, Math.min(splitCount, length - curentIndex));
		if (subArray.length > 0)
		{
			System.arraycopy(objects, curentIndex, subArray, 0, subArray.length);
			curentIndex += subArray.length;
		}
		
		return Arrays.asList(subArray);
	}
	
	/**
	 * Returns the total number of elements in this splitter.<br>
	 * This value represents the size of the original collection.
	 * @return The total count of items.
	 */
	public int size()
	{
		return length;
	}
	
	/**
	 * Checks if the current position is at the end of the list.<br>
	 * This method returns {@code true} if there are no more items to retrieve.<br>
	 * It returns {@code false} if more elements remain.
	 * @return {@code true} if this is the last part, otherwise {@code false}.
	 */
	public boolean isLast()
	{
		return curentIndex == length;
	}
}
