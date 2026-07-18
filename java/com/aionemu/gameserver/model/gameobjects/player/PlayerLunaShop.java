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
import com.aionemu.gameserver.dao.PlayerLunaShopDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * Represents the personal Luna Shop data for a player.<br>
 * This class manages items and settings stored in the {@link PlayerLunaShopDAO}.
 */
public class PlayerLunaShop
{
	Logger log = LoggerFactory.getLogger(PlayerLunaShop.class);
	private PersistentState persistentState;
	
	private boolean FreeUnderpath;
	private boolean FreeFactory;
	private boolean FreeChest;
	
	/**
	 * Creates a new instance of {@link PlayerLunaShop}.<br>
	 * This constructor initializes the shop's free access settings.<br>
	 * It also sets the initial persistent state to {@code PersistentState.NEW}.
	 * @param freeUnderpath Sets whether the underpath is free.
	 * @param freeFactory Sets whether the factory is free.
	 * @param freeChest Sets whether the chest is free.
	 */
	public PlayerLunaShop(boolean freeUnderpath, boolean freeFactory, boolean freeChest)
	{
		FreeUnderpath = freeUnderpath;
		FreeFactory = freeFactory;
		FreeChest = freeChest;
		persistentState = PersistentState.NEW;
	}
	
	/**
	 * Creates a new instance of the {@code PlayerLunaShop} class.<br>
	 * This constructor initializes a default shop object with no specific settings.
	 */
	public PlayerLunaShop()
	{
	}
	
	/**
	 * Checks if the underpath is free.<br>
	 * This method returns the current status of the {@code FreeUnderpath} flag.
	 * @return {@code true} if the underpath is free, {@code false} otherwise.
	 */
	public boolean isFreeUnderpath()
	{
		return FreeUnderpath;
	}
	
	/**
	 * Updates the {@code FreeUnderpath} status.<br>
	 * This method sets whether the underpath is currently free.
	 * @param free The new value to set for the underpath status.
	 */
	public void setFreeUnderpath(boolean free)
	{
		FreeUnderpath = free;
	}
	
	/**
	 * Checks if the factory is free.<br>
	 * This method returns the current state of the {@code FreeFactory} flag.
	 * @return {@code true} if the factory is free, {@code false} otherwise.
	 */
	public boolean isFreeFactory()
	{
		return FreeFactory;
	}
	
	/**
	 * Sets whether the factory is free.<br>
	 * This updates the {@code FreeFactory} status of the player's shop.
	 * @param free The new value to set for the factory status.
	 */
	public void setFreeFactory(boolean free)
	{
		FreeFactory = free;
	}
	
	/**
	 * Checks if the chest is currently free.<br>
	 * This method returns the status of the {@code FreeChest} flag.
	 * @return {@code true} if the chest is free, {@code false} otherwise.
	 */
	public boolean isFreeChest()
	{
		return FreeChest;
	}
	
	/**
	 * Updates the status of the free chest.<br>
	 * This method sets the {@code FreeChest} flag to a new value.
	 * @param free The new boolean value to set for the free chest.
	 */
	public void setFreeChest(boolean free)
	{
		FreeChest = free;
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
	 * Updates the Luna Shop data in the database for a specific player.<br>
	 * This method saves the current state of {@code FreeUnderpath}, {@code FreeFactory}, and {@code FreeChest}.<br>
	 * It uses the {@link PlayerLunaShopDAO} to perform the update.
	 * @param playerId The unique identifier of the player.
	 */
	public void setLunaShopByObjId(int playerId)
	{
		DAOManager.getDAO(PlayerLunaShopDAO.class).setLunaShopByObjId(playerId, isFreeUnderpath(), isFreeFactory(), isFreeChest());
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
