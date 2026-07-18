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
package com.aionemu.gameserver.controllers.observer;

/**
 * This class listens for events related to a character starting to move.<br>
 * It handles the logic triggered when an entity initiates movement in the game world.
 * @author ATracer
 */
public class StartMovingListener extends ActionObserver
{
	private boolean effectorMoved = false;
	
	/**
	 * Creates a new instance of {@link StartMovingListener}.<br>
	 * This constructor initializes the listener to track {@code ObserverType.MOVE} events.
	 */
	public StartMovingListener()
	{
		super(ObserverType.MOVE);
	}
	
	/**
	 * Checks if the effector has been moved.<br>
	 * This method returns the current state of the {@code effectorMoved} flag.
	 * @return {@code true} if the effector was moved, {@code false} otherwise.
	 */
	public boolean isEffectorMoved()
	{
		return effectorMoved;
	}
	
	/**
	 * Updates the movement status of the effector.<br>
	 * Sets the {@code effectorMoved} flag to {@code true}.
	 */
	@Override
	public void moved()
	{
		effectorMoved = true;
	}
}
