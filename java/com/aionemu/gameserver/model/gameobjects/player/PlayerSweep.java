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
package com.aionemu.gameserver.model.gameobjects.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerShugoSweepDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * This class represents the sweep data for a {@link Player}.<br>
 * It handles information related to player-specific sweep mechanics.<br>
 * Use this model to manage and retrieve sweep states within the game world.
 * @author Ghostfur
 */
public class PlayerSweep
{
	Logger log = LoggerFactory.getLogger(PlayerSweep.class);
	private PersistentState persistentState;
	
	private int step;
	private int freeDice;
	private int boardId;
	
	/**
	 * Creates a new {@code PlayerSweep} instance.<br>
	 * This constructor initializes the game state for a player.<br>
	 * It sets the initial progress and dice count.
	 * @param step The current step number of the player.
	 * @param freeDice The amount of free dice available to the player.
	 * @param boardId The unique identifier for the game board.
	 */
	public PlayerSweep(int step, int freeDice, int boardId)
	{
		this.step = step;
		this.freeDice = freeDice;
		this.boardId = boardId;
		persistentState = PersistentState.NEW;
	}
	
	/**
	 * Creates a new instance of {@code PlayerSweep}.<br>
	 * This is the default constructor for initializing a player's sweep data.
	 */
	public PlayerSweep()
	{
	}
	
	/**
	 * Retrieves the current number of free dice.<br>
	 * This value is stored in the {@code freeDice} field.
	 * @return The total count of free dice as an {@code int}.
	 */
	public int getFreeDice()
	{
		return freeDice;
	}
	
	/**
	 * Sets the number of free dice for the player.<br>
	 * This updates the {@code freeDice} field with a new value.
	 * @param dice The number of free dice to set.
	 */
	public void setFreeDice(int dice)
	{
		freeDice = dice;
	}
	
	/**
	 * Retrieves the current step of the player. <br>
	 * This value represents the progress in the game sequence.
	 * @return The current {@code int} step value.
	 */
	public int getStep()
	{
		return step;
	}
	
	/**
	 * Updates the current step of the player.<br>
	 * This method sets the {@code step} value for this object.
	 * @param step The new step number to assign.
	 */
	public void setStep(int step)
	{
		this.step = step;
	}
	
	/**
	 * Retrieves the unique identifier for the game board.<br>
	 * This value is stored in the {@code boardId} field.
	 * @return The current {@code int} board ID.
	 */
	public int getBoardId()
	{
		return boardId;
	}
	
	/**
	 * Sets the unique identifier for the game board.<br>
	 * This updates the {@code boardId} field of the current object.
	 * @param boardId The new ID to assign to the board.
	 */
	public void setBoardId(int boardId)
	{
		this.boardId = boardId;
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
	 * Updates the Shugo Sweep data in the database for a specific player.<br>
	 * This method saves the current {@code freeDice}, {@code step}, and {@code boardId}.
	 * @param playerId The unique identifier of the player.
	 */
	public void setShugoSweepByObjId(int playerId)
	{
		DAOManager.getDAO(PlayerShugoSweepDAO.class).setShugoSweepByObjId(playerId, getFreeDice(), getStep(), getBoardId());
	}
	
	/**
	 * Updates the {@code persistentState} of this quest.<br>
	 * This method prevents changing from {@code PersistentState.NEW} to {@code PersistentState.UPDATE_REQUIRED}.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		switch (persistentState)
		{
			case UPDATE_REQUIRED:
				if (this.persistentState == PersistentState.NEW)
				{
					break;
				}
			default:
				this.persistentState = persistentState;
		}
	}
}
