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

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the movement logic for {@link Creature} entities in the game world.<br>
 * It processes movement requests and updates the position of objects extending {@code T}.
 * @author ATracer
 * @param <T>
 */
public abstract class CreatureMoveController<T extends VisibleObject> implements MoveController
{
	protected T owner;
	protected byte heading;
	protected long lastMoveUpdate = System.currentTimeMillis();
	protected boolean isInMove = false;
	protected transient AtomicBoolean started = new AtomicBoolean(false);
	public byte movementMask;
	protected float targetDestX;
	protected float targetDestY;
	protected float targetDestZ;
	
	/**
	 * Creates a new instance of this controller.<br>
	 * It assigns the provided {@code owner} to this controller.
	 * @param owner The {@link VisibleObject} that owns this movement controller.
	 */
	public CreatureMoveController(T owner)
	{
		this.owner = owner;
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
		this.heading = heading;
		setNewDirection(x, y, z);
	}
	
	/**
	 * Updates the target destination coordinates for the creature.<br>
	 * This method sets the internal {@code targetDestX}, {@code targetDestY}, and {@code targetDestZ} fields.
	 * @param x The new X coordinate.
	 * @param y The new Y coordinate.
	 * @param z The new Z coordinate.
	 */
	protected void setNewDirection(float x, float y, float z)
	{
		targetDestX = x;
		targetDestY = y;
		targetDestZ = z;
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
	 * Stops the movement of a specific creature.<br>
	 * This method sets the {@code movementMask} to {@code MovementMask.IMMEDIATE}.<br>
	 * It then broadcasts an {@link SM_MOVE} packet to all players.
	 * @param owner The {@link Creature} that is stopping its movement.
	 */
	protected void setAndSendStopMove(Creature owner)
	{
		movementMask = MovementMask.IMMEDIATE;
		PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
	}
	
	/**
	 * Updates the timestamp of the last movement.<br>
	 * This method sets {@code lastMoveUpdate} to the current system time.<br>
	 * It is used to track when a creature last moved.
	 */
	public void updateLastMove()
	{
		lastMoveUpdate = System.currentTimeMillis();
	}
	
	/**
	 * Retrieves the timestamp of the most recent movement update.<br>
	 * This value is updated whenever a move occurs.
	 * @return The {@code long} timestamp of the last update.
	 */
	public long getLastMoveUpdate()
	{
		return lastMoveUpdate;
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
		return isInMove;
	}
	
	/**
	 * Updates the movement status of the creature.<br>
	 * This method sets whether the entity is currently moving.
	 * @param value The new movement state to set. Use {@code true} if moving and {@code false} if stationary.
	 */
	@Override
	public void setInMove(boolean value)
	{
		isInMove = value;
	}
}
