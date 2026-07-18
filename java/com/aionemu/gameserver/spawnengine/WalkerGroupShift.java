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
package com.aionemu.gameserver.spawnengine;

/**
 * This class manages the shifting logic for groups of walker NPCs.<br>
 * It ensures that walkers move in coordinated patterns within the {@code spawnengine} system.
 * @author Rolandas
 */
public class WalkerGroupShift
{
	private float sagittalShift; // left and right (sides)
	private float coronalShift; // or dorsoventral (back and front)
	private float angle; // if positioned in circle
	public static final float DISTANCE = 2; // 2 meters distance by default
	
	/**
	 * Creates a new {@code WalkerGroupShift} object.<br>
	 * This constructor sets the horizontal and vertical offsets for a group of walkers.
	 * @param leftRight The amount to shift the group along the sagittal axis.
	 * @param backFront The amount to shift the group along the coronal axis.
	 */
	public WalkerGroupShift(float leftRight, float backFront)
	{
		sagittalShift = leftRight;
		coronalShift = backFront;
	}
	
	/**
	 * Creates a new {@link WalkerGroupShift} instance using a circular position.<br>
	 * This constructor sets the rotation angle for the group.
	 * @param angle The rotation angle in degrees.
	 */
	public WalkerGroupShift(float angle)
	{
		this.angle = angle;
	}
	
	/**
	 * Gets the current sagittal shift value.<br>
	 * This represents the left and right side positioning.
	 * @return The {@code float} value of the sagittal shift.
	 */
	public float getSagittalShift()
	{
		return sagittalShift;
	}
	
	/**
	 * Retrieves the current coronal shift value.<br>
	 * This represents the back and front positioning of the group.
	 * @return The {@code float} value of the coronal shift.
	 */
	public float getCoronalShift()
	{
		return coronalShift;
	}
	
	/**
	 * Retrieves the current rotation angle of the walker group.<br>
	 * This value is used when the group is positioned in a circle.
	 * @return The rotation angle as a {@code float}.
	 */
	public float getAngle()
	{
		return angle;
	}
}
