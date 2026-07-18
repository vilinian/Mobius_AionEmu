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
package com.aionemu.gameserver.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aionemu.commons.utils.Rnd;

/**
 * This utility class provides functionality to select a random element from a collection.<br>
 * It simplifies the process of picking items for game mechanics using {@link com.aionemu.commons.utils.Rnd}.
 * @param <E>
 */
public class RndSelector<E>
{
	private class RndNode<T> implements Comparable<RndNode<T>>
	{
		private final T value;
		private final int weight;
		
		public RndNode(T value, int weight)
		{
			this.value = value;
			this.weight = weight;
		}
		
		@Override
		public int compareTo(RndNode<T> o)
		{
			return weight - weight;
		}
	}
	
	private int totalWeight = 0;
	private final List<RndNode<E>> nodes;
	
	/**
	 * Creates a new instance of the {@code RndSelector} class.<br>
	 * Initializes an empty list to store weighted nodes.<br>
	 * Use this constructor to prepare a selector for random choices.
	 */
	public RndSelector()
	{
		nodes = new ArrayList<>();
	}
	
	/**
	 * Creates a new {@link RndSelector} with a specific starting size.<br>
	 * This sets the initial capacity for the internal list of nodes.
	 * @param initialCapacity The number of elements to hold initially.
	 */
	public RndSelector(int initialCapacity)
	{
		nodes = new ArrayList<>(initialCapacity);
	}
	
	/**
	 * Adds a new item to the selection list.<br>
	 * The item is assigned a specific weight for random selection.<br>
	 * This method ignores {@code null} values or weights less than or equal to {@code 0}.
	 * @param value The object to be added to the selector.
	 * @param weight The numerical weight assigned to this object.
	 */
	public void add(E value, int weight)
	{
		if ((value == null) || (weight <= 0))
		{
			return;
		}
		
		totalWeight += weight;
		nodes.add(new RndNode<>(value, weight));
	}
	
	/**
	 * Selects a random element based on its weight.<br>
	 * The selection is limited by the provided maximum weight.
	 * @param maxWeight The upper limit for the random selection.
	 * @return The selected element of type {@code E}, or {@code null} if no match is found.
	 */
	public E chance(int maxWeight)
	{
		if (maxWeight <= 0)
		{
			return null;
		}
		
		Collections.sort(nodes);
		
		final int r = Rnd.get(maxWeight);
		int weight = 0;
		for (int i = 0; i < nodes.size(); i++)
		{
			if ((weight += nodes.get(i).weight) > r)
			{
				return nodes.get(i).value;
			}
		}
		
		return null;
	}
	
	/**
	 * Selects a random element based on weighted probabilities.<br>
	 * This method uses a default weight of {@code 100}.
	 * @return The selected element of type {@code E}.
	 */
	public E chance()
	{
		return chance(100);
	}
	
	/**
	 * Selects a random element from the current collection.<br>
	 * This method uses the total weight of all added nodes to determine the result.<br>
	 * It is a convenient way to pick an item based on its relative probability.
	 * @return The selected element of type {@code E}.
	 */
	public E select()
	{
		return chance(totalWeight);
	}
	
	/**
	 * Removes all elements from the selector.<br>
	 * The {@code totalWeight} will be reset to 0.<br>
	 * This method clears the internal list of nodes.
	 */
	public void clear()
	{
		totalWeight = 0;
		nodes.clear();
	}
}
