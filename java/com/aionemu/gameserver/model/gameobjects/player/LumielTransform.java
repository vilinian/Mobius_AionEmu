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
package com.aionemu.gameserver.model.gameobjects.player;

/**
 * Represents the transformation data for a {@link Player} character in the game world.<br>
 * This class handles the spatial properties and visual state of the player entity.
 */
public class LumielTransform
{
	private final int id;
	private long points;
	
	/**
	 * Creates a new instance of {@link LumielTransform}.<br>
	 * This constructor initializes the object with specific values.
	 * @param id The unique identifier for the transform.
	 * @param points The initial point value assigned to the transform.
	 */
	public LumielTransform(int id, long points)
	{
		this.id = id;
		this.points = points;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the current point total for this {@link LumielTransform}.<br>
	 * This value represents the accumulated score.
	 * @return The current number of points as a {@code long}.
	 */
	public long getPoints()
	{
		return points;
	}
	
	/**
	 * Updates the current point total.<br>
	 * This method sets the {@code points} field to a new value.
	 * @param points The new number of points to assign.
	 */
	public void setPoints(long points)
	{
		this.points = points;
	}
}
