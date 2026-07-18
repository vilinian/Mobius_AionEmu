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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.World;

/**
 * This class serves as a base controller for handling movement logic for playable entities.<br>
 * It provides common functionality for both players and summoned creatures.<br>
 * It extends {@link CreatureMoveController} to manage spatial updates in the game world.
 * @author ATracer base class for summon & player move controller
 * @param <T>
 */
public abstract class PlayableMoveController<T extends Creature>extends CreatureMoveController<T>
{
	private boolean sendMovePacket = true;
	private int movementHeading = -1;
	public float vehicleX;
	public float vehicleY;
	public float vehicleZ;
	public int vehicleSpeed;
	public float vectorX;
	public float vectorY;
	public float vectorZ;
	public byte glideFlag;
	public int unk1;
	public int unk2;
	
	/**
	 * Creates a new instance of this controller.<br>
	 * It initializes the movement logic for a specific creature.
	 * @param owner The {@code Creature} that owns this move controller.
	 */
	public PlayableMoveController(T owner)
	{
		super(owner);
	}
	
	/**
	 * Starts the movement process to a specific destination.<br>
	 * This method sets the {@code isInMove} flag to {@code true}.<br>
	 * It triggers the logic required for the {@code moveToDestination} method.
	 */
	@Override
	public void startMovingToDestination()
	{
		updateLastMove();
		if (owner.canPerformMove())
		{
			if (isControlled() && started.compareAndSet(false, true))
			{
				movementMask = MovementMask.NPC_STARTMOVE;
				sendForcedMovePacket();
				PlayerMoveTaskManager.getInstance().addPlayer(owner);
			}
		}
	}
	
	/**
	 * Checks if the entity is currently under a fear effect.<br>
	 * This determines if movement is being restricted by an external force.
	 * @return {@code true} if the owner is under fear, {@code false} otherwise.
	 */
	private boolean isControlled()
	{
		return owner.getEffectController().isUnderFear();
	}
	
	/**
	 * Sends a forced movement packet to the owner.<br>
	 * This method uses {@code broadcastPacketAndReceive}.<br>
	 * It sets the {@code sendMovePacket} flag to {@code false} after execution.
	 */
	private void sendForcedMovePacket()
	{
		PacketSendUtility.broadcastPacketAndReceive(owner, new SM_MOVE(owner));
		sendMovePacket = false;
	}
	
	/**
	 * Updates the position of the {@code owner} toward the target destination.<br>
	 * This method calculates the next coordinates based on current speed and distance.<br>
	 * It updates the world position if the movement is valid.
	 */
	@Override
	public void moveToDestination()
	{
		if (!owner.canPerformMove())
		{
			if (started.compareAndSet(true, false))
			{
				setAndSendStopMove(owner);
			}
			
			updateLastMove();
			return;
		}
		
		if (sendMovePacket && isControlled())
		{
			sendForcedMovePacket();
		}
		
		final float x = owner.getX();
		final float y = owner.getY();
		final float z = owner.getZ();
		
		final float currentSpeed = StatFunctions.getMovementModifier(owner, StatEnum.SPEED, owner.getGameStats().getMovementSpeedFloat());
		float futureDistPassed = (currentSpeed * (System.currentTimeMillis() - lastMoveUpdate)) / 1000f;
		final float dist = (float) MathUtil.getDistance(x, y, z, targetDestX, targetDestY, targetDestZ);
		
		if (dist == 0)
		{
			return;
		}
		
		if (futureDistPassed > dist)
		{
			futureDistPassed = dist;
		}
		
		final float distFraction = futureDistPassed / dist;
		final float newX = ((targetDestX - x) * distFraction) + x;
		final float newY = ((targetDestY - y) * distFraction) + y;
		final float newZ = ((targetDestZ - z) * distFraction) + z;
		
		/*
		 * if ((movementMask & MovementMask.MOUSE) == 0) { targetDestX = newX + vectorX; targetDestY = newY + vectorY; targetDestZ = newZ + vectorZ; }
		 */
		World.getInstance().updatePosition(owner, newX, newY, newZ, heading, false);
		updateLastMove();
	}
	
	/**
	 * Stops the current movement of the creature.<br>
	 * This method cancels any active move request.<br>
	 * It sets the {@code isInMove} flag to {@code false}.
	 */
	@Override
	public void abortMove()
	{
		started.set(false);
		PlayerMoveTaskManager.getInstance().removePlayer(owner);
		targetDestX = 0;
		targetDestY = 0;
		targetDestZ = 0;
		setAndSendStopMove(owner);
	}
	
	/**
	 * Updates the target destination coordinates for movement.<br>
	 * This method recalculates the {@code movementHeading} based on the new position.<br>
	 * It sets {@code sendMovePacket} to {@code true} if the coordinates have changed.
	 * @param x The new X coordinate.
	 * @param y The new Y coordinate.
	 * @param z The new Z coordinate.
	 */
	@Override
	public void setNewDirection(float x, float y, float z)
	{
		if ((targetDestX != x) || (targetDestY != y) || (targetDestZ != z))
		{
			sendMovePacket = true;
		}
		
		targetDestX = x;
		targetDestY = y;
		targetDestZ = z;
		
		final float h = MathUtil.calculateAngleFrom(owner.getX(), owner.getY(), targetDestX, targetDestY);
		if (h != 0)
		{
			int value = (int) (((heading * 3) - h) / 45);
			if (value < 0)
			{
				value += 8;
			}
			
			if (movementHeading != value)
			{
				movementHeading = value;
			}
		}
	}
	
	/**
	 * Updates the movement behavior of a {@code Minion}.<br>
	 * This method sets the {@code movementMask} to {@code MovementMask.IMMEDIATE}.
	 */
	@Override
	public void skillMovement()
	{
		movementMask = MovementMask.IMMEDIATE;
		PacketSendUtility.broadcastPacketAndReceive(owner, new SM_MOVE(owner));
	}
	
	/**
	 * Retrieves the current heading of the movement.<br>
	 * This method checks if the entity is currently moving.<br>
	 * If it is not moving, it returns {@code -1}.
	 * @return The integer value of the movement heading or {@code -1} if not in motion.
	 */
	public int getMovementHeading()
	{
		if (!isInMove())
		{
			return -1;
		}
		
		return movementHeading;
	}
}
