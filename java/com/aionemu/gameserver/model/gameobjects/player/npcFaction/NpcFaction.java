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
package com.aionemu.gameserver.model.gameobjects.player.npcFaction;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.templates.challenge.ChallengeQuestTemplate;

/**
 * Represents the faction affiliation of an NPC.<br>
 * This class manages how NPCs interact with different player groups and other entities.<br>
 * It is used by the game server to determine friendly or hostile behaviors.
 * @author MrPoke
 */
public class NpcFaction
{
	private final int id;
	private int time;
	private boolean active;
	private final boolean mentor;
	private ENpcFactionQuestState state;
	private int questId;
	private PersistentState persistentState;
	
	/**
	 * Creates a new instance of {@link NpcFaction}.<br>
	 * This constructor initializes the faction data and sets the initial persistent state.
	 * @param id The unique identifier for the NPC faction.
	 * @param time The current time associated with this faction.
	 * @param active Determines if the faction is currently {@code true} or {@code false}.
	 * @param state The current quest state of the faction.
	 * @param questId The specific ID of the quest linked to this faction.
	 */
	public NpcFaction(int id, int time, boolean active, ENpcFactionQuestState state, int questId)
	{
		this.id = id;
		this.time = time;
		this.active = active;
		this.state = state;
		mentor = DataManager.NPC_FACTIONS_DATA.getNpcFactionById(id).isMentor();
		this.questId = questId;
		persistentState = PersistentState.NEW;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the duration associated with this {@code AutoGroupType}.<br>
	 * The value is returned in milliseconds.
	 * @return the time value as an {@code int}
	 */
	public int getTime()
	{
		return time;
	}
	
	/**
	 * Checks if the motion is currently active.<br>
	 * This method returns the current state of the {@code active} field.
	 * @return {@code true} if the motion is active, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return active;
	}
	
	/**
	 * Checks if the current player has mentor status.
	 * @return {@code true} if the player is a mentor, {@code false} otherwise.
	 */
	public boolean isMentor()
	{
		return mentor;
	}
	
	/**
	 * Retrieves the current quest state for this NPC faction.<br>
	 * This method returns the {@code ENpcFactionQuestState} object.
	 * @return The current {@code ENpcFactionQuestState}.
	 */
	public ENpcFactionQuestState getState()
	{
		return state;
	}
	
	/**
	 * Updates the current time for this {@link NpcFaction}.<br>
	 * This method also marks the state as requiring an update.
	 * @param time The new time value to set.
	 */
	public void setTime(int time)
	{
		this.time = time;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Updates the active status of this {@link NpcFaction}.<br>
	 * Sets the internal state to {@code true} or {@code false}.
	 * @param active The new status to set for the faction.
	 */
	public void setActive(boolean active)
	{
		this.active = active;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Updates the current quest state of the NPC faction.<br>
	 * This method also sets the {@code persistentState} to {@code UPDATE_REQUIRED}.
	 * @param state The new {@link ENpcFactionQuestState} to apply.
	 */
	public void setState(ENpcFactionQuestState state)
	{
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		this.state = state;
	}
	
	/**
	 * Retrieves the unique identifier for this quest.<br>
	 * This value is obtained from the {@link ChallengeQuestTemplate}.
	 * @return The {@code int} ID of the quest.
	 */
	public int getQuestId()
	{
		return questId;
	}
	
	/**
	 * Sets the unique identifier for the current quest.<br>
	 * This method also updates the {@link PersistentState} to require an update.
	 * @param questId The new {@code int} value for the quest ID.
	 */
	public void setQuestId(int questId)
	{
		this.questId = questId;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
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
	 * Updates the {@code persistentState} of this wardrobe entry.<br>
	 * This method applies specific logic to handle state transitions.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		switch (persistentState)
		{
			case DELETED:
				if (this.persistentState == PersistentState.NEW)
				{
					this.persistentState = PersistentState.NOACTION;
				}
				else
				{
					this.persistentState = PersistentState.DELETED;
				}
				break;
			case UPDATE_REQUIRED:
				if (this.persistentState != PersistentState.NEW)
				{
					this.persistentState = PersistentState.UPDATE_REQUIRED;
				}
				break;
			case NOACTION:
				break;
			default:
				this.persistentState = persistentState;
		}
	}
}
