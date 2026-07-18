/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Aion-Lightning is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. * You should have received a copy of the GNU General Public License
 * along with Aion-Lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.cubics;

import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * Represents a specific entry in the Minecraft (MC) data structure for a player.<br>
 * This class extends {@link MCEntry} to store player-specific information within the game world.
 * @author Phantom_KNA
 */
public class PlayerMCEntry extends MCEntry
{
	private PersistentState persistentState;
	
	/**
	 * Creates a new instance of {@link PlayerMCEntry}.<br>
	 * This constructor initializes the basic cube data and the persistent state.
	 * @param cubeid The unique identifier for the cube.
	 * @param rank The current rank of the cube.
	 * @param level The current level of the cube.
	 * @param stat_value The numerical value of the cube's stats.
	 * @param category The classification category of the cube.
	 * @param paramPersistentState The {@code PersistentState} associated with this entry.
	 */
	public PlayerMCEntry(int cubeid, int rank, int level, int stat_value, int category, PersistentState paramPersistentState)
	{
		super(cubeid, rank, level, stat_value, category);
		persistentState = paramPersistentState;
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the {@code persistentState} of this entry.<br>
	 * This method handles specific logic for {@code PersistentState.DELETED} and {@code PersistentState.UPDATE_REQUIRED}.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		switch (persistentState)
		{
			case DELETED:
				if (persistentState == PersistentState.NEW)
				{
					persistentState = PersistentState.NOACTION;
				}
				else
				{
					persistentState = PersistentState.DELETED;
				}
				break;
			case UPDATE_REQUIRED:
				if (persistentState != PersistentState.NEW)
				{
					persistentState = PersistentState.UPDATE_REQUIRED;
				}
				break;
			case NOACTION:
				break;
			default:
				this.persistentState = persistentState;
				break;
		}
	}
}
