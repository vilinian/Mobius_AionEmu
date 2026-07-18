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
package com.aionemu.gameserver.model;

import java.util.concurrent.CountDownLatch;

/**
 * This interface defines the core logic for managing the game world and its lifecycle.<br>
 * It serves as the primary entry point for processing game state updates and handling engine operations.
 * @author ATracer
 */
public interface GameEngine
{
	/**
	 * Load resources for engine
	 * @param progressLatch
	 */
	void load(CountDownLatch progressLatch);
	
	/**
	 * Cleanup resources for engine
	 */
	void shutdown();
}
