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
package com.aionemu.gameserver.utils.gametime;

/**
 * This class is responsible for periodically updating the game clock.<br>
 * It ensures that the server time remains synchronized and accurate for all players.
 * @author Ben
 */
public class GameTimeUpdater implements Runnable
{
	private final GameTime time;
	
	/**
	 * Creates a new instance of {@link GameTimeUpdater}.<br>
	 * This constructor initializes the updater with a specific {@code GameTime} object.<br>
	 * The provided {@code time} will be updated by the {@code run()} method.
	 * @param time The {@code GameTime} object to be updated.
	 */
	public GameTimeUpdater(GameTime time)
	{
		this.time = time;
	}
	
	@Override
	public void run()
	{
		time.increase();
	}
}
