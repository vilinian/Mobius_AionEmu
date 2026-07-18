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

import java.util.List;

import com.aionemu.commons.utils.MTRandom;

/**
 * This utility class provides methods to handle random selections from a {@code List}.<br>
 * It simplifies picking elements randomly for game logic.<br>
 * You can use it to retrieve items or indices without manual randomization logic.
 * @author Alcapwnd
 */
public class RndArray
{
	private static final MTRandom rnd = new MTRandom();
	
	/**
	 * Generates a random floating-point number.<br>
	 * This method uses the internal {@code MTRandom} instance.
	 * @return The generated {@code float} value.
	 */
	public static float get()
	{
		return rnd.nextFloat();
	}
	
	/**
	 * Generates a random integer between {@code 0} and the specified value.<br>
	 * This method uses the internal {@code rnd} instance to calculate the result.
	 * @param n The upper bound for the random number.
	 * @return A random integer from {@code 0} up to, but not including, {@code n}.
	 */
	public static int get(int n)
	{
		return (int) Math.floor(rnd.nextDouble() * n);
	}
	
	/**
	 * Generates a random integer between two values.<br>
	 * The result is inclusive of both {@code min} and {@code max}.
	 * @param min The lower bound of the range.
	 * @param max The upper bound of the range.
	 * @return A random integer between {@code min} and {@code max}.
	 */
	public static int get(int min, int max)
	{
		return min + (int) Math.floor(rnd.nextDouble() * ((max - min) + 1));
	}
	
	/**
	 * Determines if a random event occurs based on a percentage.<br>
	 * The input represents the success rate out of {@code 100}.<br>
	 * It returns {@code true} if the roll succeeds and {@code false} otherwise.
	 * @param chance The probability of success as an integer between {@code 1} and {@code 100}.
	 * @return {@code true} if the random check passes, or {@code false} if it fails.
	 */
	public static boolean chance(int chance)
	{
		return (chance >= 1) && ((chance > 99) || ((nextInt(99) + 1) <= chance));
	}
	
	/**
	 * Determines if a random event occurs based on a percentage.<br>
	 * This method uses {@code nextDouble} to calculate the result.<br>
	 * It returns {@code true} if the roll succeeds and {@code false} otherwise.
	 * @param chance The probability of success as a value between 0.0 and 100.0.
	 * @return {@code true} if the random check passes, or {@code false} if it fails.
	 */
	public static boolean chance(double chance)
	{
		return nextDouble() <= (chance / 100.0D);
	}
	
	/**
	 * Picks a random element from the provided array.<br>
	 * It uses {@code get} to determine the index.
	 * @param <E> The type of elements in the array.
	 * @param list The array containing the items to choose from.
	 * @return A random element from the {@code list}.
	 */
	public static <E> E get(E[] list)
	{
		return list[get(list.length)];
	}
	
	/**
	 * Returns a random element from an array of integers.<br>
	 * This method uses the length of {@code list} to pick a valid index.
	 * @param list The array of integers to choose from.
	 * @return A random integer from the provided {@code int[]} array.
	 */
	public static int get(int[] list)
	{
		return list[get(list.length)];
	}
	
	/**
	 * Returns a random element from the provided {@code List}.<br>
	 * It uses an internal random generator to pick an index.
	 * @param <E> The type of elements in the list.
	 * @param list The {@code List} containing the items to choose from.
	 * @return A random element from the {@code list}.
	 */
	public static <E> E get(List<E> list)
	{
		return list.get(get(list.size()));
	}
	
	/**
	 * Generates a random integer between {@code 0} and the specified limit.<br>
	 * This method uses the internal {@code get} logic to produce a value.<br>
	 * The result is inclusive of {@code 0} but exclusive of {@code n}.
	 * @param n The upper bound for the random number.
	 * @return A random integer from {@code 0} up to, but not including, {@code n}.
	 */
	public static int nextInt(int n)
	{
		return (int) Math.floor(rnd.nextDouble() * n);
	}
	
	/**
	 * Generates a random integer.<br>
	 * This method uses the internal {@code rnd} instance to produce a value.
	 * @return The generated {@code int}.
	 */
	public static int nextInt()
	{
		return rnd.nextInt();
	}
	
	/**
	 * Generates a random {@code double} value.<br>
	 * This method uses the internal {@code rnd} instance to produce a result.<br>
	 * The value is typically between {@code 0.0} and {@code 1.0}.
	 * @return A randomly generated {@code double}.
	 */
	public static double nextDouble()
	{
		return rnd.nextDouble();
	}
	
	/**
	 * Generates a random number following a Gaussian distribution.<br>
	 * This method uses the internal {@link MTRandom} instance to produce the value.<br>
	 * The result is centered around 0.0 with a standard deviation of 1.0.
	 * @return A random {@code double} value from a normal distribution.
	 */
	public static double nextGaussian()
	{
		return rnd.nextGaussian();
	}
	
	/**
	 * Generates a random {@code boolean} value.<br>
	 * This method returns either {@code true} or {@code false}.<br>
	 * It uses the internal {@code rnd} instance to provide randomness.
	 * @return The next random {@code boolean} value.
	 */
	public static boolean nextBoolean()
	{
		return rnd.nextBoolean();
	}
}
