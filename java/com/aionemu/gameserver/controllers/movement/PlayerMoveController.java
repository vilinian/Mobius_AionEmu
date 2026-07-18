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

import com.aionemu.gameserver.configs.main.FallDamageConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.stats.StatFunctions;

/**
 * Handles the movement logic for {@link Player} objects.<br>
 * This controller manages how players move within the game world.
 * @author ATracer
 */
public class PlayerMoveController extends PlayableMoveController<Player>
{
	private float fallDistance;
	private float lastFallZ;
	
	/**
	 * Creates a new instance of {@link PlayerMoveController}.<br>
	 * This constructor initializes the controller for a specific player.
	 * @param owner The {@code Player} who owns this movement controller.
	 */
	public PlayerMoveController(Player owner)
	{
		super(owner);
	}
	
	/**
	 * Updates the current falling distance of the player.<br>
	 * This method calculates how far the player has fallen based on the change in {@code newZ}.<br>
	 * It triggers fall damage if the distance exceeds the limit defined in {@link FallDamageConfig}.
	 * @param newZ The new vertical position of the player.
	 */
	public void updateFalling(float newZ)
	{
		if (lastFallZ != 0)
		{
			fallDistance += lastFallZ - newZ;
			if (fallDistance >= FallDamageConfig.MAXIMUM_DISTANCE_MIDAIR)
			{
				StatFunctions.calculateFallDamage(owner, fallDistance, false);
			}
		}
		
		lastFallZ = newZ;
		owner.getObserveController().notifyMoveObservers();
	}
	
	/**
	 * Stops the current falling state for the player.<br>
	 * This method calculates fall damage if the player is not flying.<br>
	 * It resets the {@code fallDistance} and {@code lastFallZ} values to {@code 0}.<br>
	 * It also notifies all move observers via the {@code getObserveController} method.
	 */
	public void stopFalling()
	{
		if (lastFallZ != 0)
		{
			if (!owner.isFlying())
			{
				StatFunctions.calculateFallDamage(owner, fallDistance, true);
			}
			
			fallDistance = 0;
			lastFallZ = 0;
			owner.getObserveController().notifyMoveObservers();
		}
	}
}
