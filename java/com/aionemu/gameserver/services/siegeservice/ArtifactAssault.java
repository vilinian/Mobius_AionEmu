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
package com.aionemu.gameserver.services.siegeservice;

/**
 * This class handles the logic for the {@code ArtifactAssault} siege event.<br>
 * It extends the base {@link Assault} class to manage specific mechanics for {@link ArtifactSiege}.
 * @author Luzien
 */
public class ArtifactAssault extends Assault<ArtifactSiege>
{
	/**
	 * Creates a new {@link ArtifactAssault} instance.<br>
	 * This constructor initializes the assault using a specific siege.
	 * @param siege The {@code ArtifactSiege} object used to set up the assault.
	 */
	public ArtifactAssault(ArtifactSiege siege)
	{
		super(siege);
	}
	
	/**
	 * Schedules the start of an assault.<br>
	 * This method sets a timer for when the action begins.
	 * @param delay The time in milliseconds to wait before starting.
	 */
	@Override
	public void scheduleAssault(int delay)
	{
	}
	
	/**
	 * This method is called when an assault has finished.<br>
	 * It handles the logic for updating the game state based on the result.
	 * @param captured A {@code boolean} indicating if the objective was successfully taken.
	 */
	@Override
	public void onAssaultFinish(boolean captured)
	{
	}
}
