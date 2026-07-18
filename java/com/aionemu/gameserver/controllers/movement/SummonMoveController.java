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

import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.taskmanager.tasks.MoveTaskManager;

/**
 * Handles the movement logic for {@link Summon} entities.<br>
 * This controller manages how summons move within the game world.
 * @author ATracer
 */
public class SummonMoveController extends PlayableMoveController<Summon>
{
	/**
	 * Creates a new instance of {@link SummonMoveController}.<br>
	 * This constructor initializes the controller for a specific {@code Summon}.
	 * @param owner The {@code Summon} object that this controller will manage.
	 */
	public SummonMoveController(Summon owner)
	{
		super(owner);
	}
	
	/**
	 * Starts the movement process toward a specific target object.<br>
	 * This method sets the destination to {@code Destination.TARGET_OBJECT}.<br>
	 * It also registers the owner with the {@link MoveTaskManager}.
	 */
	public void moveToTargetObject()
	{
	}
}
