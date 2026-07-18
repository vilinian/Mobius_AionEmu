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
package com.aionemu.commons.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Provides a set of utility methods for validating various data types.<br>
 * This class helps ensure that inputs meet specific criteria before processing.<br>
 * It simplifies common validation logic across the {@code utils} package.
 */
public class GenericValidator
{
	/**
	 * Checks if a {@code String} is either {@code null} or empty.<br>
	 * This method returns {@code true} if the input has no content.<br>
	 * It returns {@code false} if the string contains any characters.
	 * @param s The {@code String} to check.
	 * @return {@code true} if the string is {@code null} or empty, otherwise {@code false}.
	 */
	public static boolean isBlankOrNull(String s)
	{
		return (s == null) || s.isEmpty();
	}
	
	/**
	 * Checks if a {@code Collection} is {@code null} or empty.<br>
	 * This method returns {@code true} if the collection has no elements.<br>
	 * It returns {@code false} if the collection contains data.
	 * @param c The {@code Collection} to check.
	 * @return {@code true} if the collection is {@code null} or empty, otherwise {@code false}.
	 */
	public static boolean isBlankOrNull(Collection<?> c)
	{
		return (c == null) || c.isEmpty();
	}
	
	/**
	 * Checks if the provided {@code Map} is {@code null} or empty.<br>
	 * This method returns {@code true} if the map has no entries.
	 * @param m The {@code Map} to check.
	 * @return {@code true} if the map is {@code null} or empty, otherwise {@code false}.
	 */
	public static boolean isBlankOrNull(Map<?, ?> m)
	{
		return (m == null) || m.isEmpty();
	}
	
	/**
	 * Checks if a {@code Number} is {@code null} or equal to {@code 0}.<br>
	 * This method helps validate numeric inputs.
	 * @param n The {@code Number} to check.
	 * @return {@code true} if the value is {@code null} or zero, otherwise {@code false}.
	 */
	public static boolean isBlankOrNull(Number n)
	{
		return (n == null) || (n.doubleValue() == 0);
	}
	
	/**
	 * Checks if an array is {@code null} or empty.<br>
	 * Returns {@code true} if the array has no elements.<br>
	 * Returns {@code false} otherwise.
	 * @param a The array to check.
	 * @return {@code true} if the array is {@code null} or empty, {@code false} otherwise.
	 */
	public static boolean isBlankOrNull(Object[] a)
	{
		return (a == null) || (a.length == 0);
	}
}
