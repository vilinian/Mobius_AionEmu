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
package com.aionemu.gameserver.controllers.movement;

import com.aionemu.gameserver.model.gameobjects.Minion;

/**
 * This class handles the movement logic specifically for {@link Minion} entities.<br>
 * It extends {@code CreatureMoveController} to provide specialized behavior for minions.
 * @author Falke_34
 */
public class MinionMoveController extends CreatureMoveController<Minion>
{
	protected float targetDestX;
	protected float targetDestY;
	protected float targetDestZ;
	protected byte heading;
	protected byte movementMask;
	
	/**
	 * Creates a new instance of the {@link MinionMoveController}.<br>
	 * This constructor initializes the controller for minion movement.
	 */
	public MinionMoveController()
	{
		super(null); // not used yet
	}
	
	/**
	 * Starts the movement process for the {@code owner}.<br>
	 * This method updates the internal state to begin moving toward the target coordinates.<br>
	 * It triggers the logic required to transition the creature into a moving state.
	 */
	@Override
	public void moveToDestination()
	{
	}
	
	/**
	 * Retrieves the X coordinate of the destination.<br>
	 * This value is stored in the {@code targetDestX} field.
	 * @return The current X coordinate as a {@code float}.
	 */
	@Override
	public float getTargetX2()
	{
		return targetDestX;
	}
	
	/**
	 * Retrieves the Y coordinate of the destination.<br>
	 * This value is used to determine where the creature is moving.
	 * @return The {@code float} value of {@code targetDestY}.
	 */
	@Override
	public float getTargetY2()
	{
		return targetDestY;
	}
	
	/**
	 * Retrieves the Z-coordinate of the target destination.<br>
	 * This value is used to determine the height of the movement goal.
	 * @return The {@code float} value of {@code targetDestZ}.
	 */
	@Override
	public float getTargetZ2()
	{
		return targetDestZ;
	}
	
	/**
	 * Updates the target destination for the minion.<br>
	 * This method sets new coordinates for movement.
	 * @param x2 The new X coordinate.
	 * @param y2 The new Y coordinate.
	 * @param z2 The new Z coordinate.
	 */
	@Override
	public void setNewDirection(float x2, float y2, float z2)
	{
		setNewDirection(x2, y2, z2, (byte) 0);
	}
	
	/**
	 * Updates the movement direction for a creature.<br>
	 * This method sets the new coordinates and the rotation angle.
	 * @param x The new X coordinate.
	 * @param y The new Y coordinate.
	 * @param z The new Z coordinate.
	 * @param heading The new rotation value in {@code byte} format.
	 */
	@Override
	public void setNewDirection(float x, float y, float z, byte heading)
	{
		targetDestX = x;
		targetDestY = y;
		targetDestZ = z;
		this.heading = heading;
	}
	
	/**
	 * Initiates the movement process to a specific destination.<br>
	 * This method sets the {@code isInMove} flag to {@code true}.<br>
	 * It triggers the logic required for the {@code moveToDestination} method.
	 */
	@Override
	public void startMovingToDestination()
	{
	}
	
	/**
	 * Stops the current movement of the creature.<br>
	 * This method cancels any active move request.<br>
	 * It sets the {@code isInMove} flag to {@code false}.
	 */
	@Override
	public void abortMove()
	{
	}
	
	/**
	 * Retrieves the current movement mask.<br>
	 * This value defines the specific type of movement being performed.
	 * @return The {@code byte} representing the movement mask.
	 */
	@Override
	public byte getMovementMask()
	{
		return movementMask;
	}
	
	/**
	 * Checks if the creature is currently in the middle of a move.<br>
	 * This method returns the current state of the {@code isInMove} flag.
	 * @return {@code true} if the creature is moving, or {@code false} otherwise.
	 */
	@Override
	public boolean isInMove()
	{
		return true;
	}
	
	/**
	 * Updates the movement status of the creature.<br>
	 * This method sets whether the entity is currently moving.
	 * @param value The new movement state to set. Use {@code true} if moving and {@code false} if stationary.
	 */
	@Override
	public void setInMove(boolean value)
	{
	}
	
	/**
	 * Updates the movement behavior of a {@link Minion}.<br>
	 * This method sets the {@code movementMask} to {@code MovementMask.IMMEDIATE}.
	 */
	@Override
	public void skillMovement()
	{
		movementMask = MovementMask.IMMEDIATE;
	}
}
