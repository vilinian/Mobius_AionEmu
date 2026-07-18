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
package com.aionemu.gameserver.geoEngine.math;

/**
 * This class represents a 3D vector using an array of {@code float} values.<br>
 * It provides basic mathematical operations for spatial coordinates in the game engine.
 * @author MrPoke
 */
public class Array3f
{
	public float a = 0;
	public float b = 0;
	public float c = 0;
	
	/**
	 * Resets all coordinates to their default values.<br>
	 * This method sets {@code a}, {@code b}, and {@code c} to {@code 0}.<br>
	 * Use this to clear the current state of the {@link Array3f} instance.
	 */
	public void reset()
	{
		a = 0;
		b = 0;
		c = 0;
	}
	
	/**
	 * Creates a new instance of {@link Array3f}.
	 * @return A new {@code Array3f} instance.
	 */
	public static Array3f newInstance()
	{
		return new Array3f();
	}
	
	/**
	 * Recycles the provided {@code Array3f} instance.
	 * @param instance The {@code Array3f} object to be recycled.
	 */
	public static void recycle(Array3f instance)
	{
		// pooling removed
	}
}
