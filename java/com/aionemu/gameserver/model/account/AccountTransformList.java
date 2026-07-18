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
package com.aionemu.gameserver.model.account;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.AccountTransformDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORMATION;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages a collection of transformations associated with an {@link Player} account.<br>
 * It provides utility methods to handle and synchronize transformation data between the server and the client.
 */
public class AccountTransformList
{
	private final Player player;
	private final Map<Integer, AccountTransfo> transformations = new HashMap<>();
	private final Map<Integer, AccountTransfo> transformationsCreated = new HashMap<>();
	
	/**
	 * Creates a new instance of {@code AccountTransformList}.<br>
	 * This constructor initializes the list for a specific {@link Player}.<br>
	 * It automatically calls {@code loadAccountTransformList} to populate the data.
	 * @param player The {@code Player} object associated with this transformation list.
	 */
	public AccountTransformList(Player player)
	{
		this.player = player;
		loadAccountTransformList();
	}
	
	/**
	 * Loads the list of transformations for the current player.<br>
	 * This method fetches data from the {@link AccountTransformDAO}.<br>
	 * It populates the internal {@code transformations} map.
	 */
	public void loadAccountTransformList()
	{
		for (AccountTransfo cmd : DAOManager.getDAO(AccountTransformDAO.class).loadAccountTransfo(player.getPlayerAccount()).values())
		{
			transformations.put(cmd.getCardId(), cmd);
		}
	}
	
	/**
	 * Retrieves all account transformations for the current player.<br>
	 * This method returns a collection of {@link AccountTransfo} objects.
	 * @return A {@code Collection} containing all loaded transformations.
	 */
	public Collection<AccountTransfo> getTransformations()
	{
		return transformations.values();
	}
	
	/**
	 * Retrieves a specific transformation based on its unique ID.<br>
	 * This method looks up the {@code AccountTransfo} object in the internal map.
	 * @param transformationId The unique identifier for the transformation to find.
	 * @return The {@code AccountTransfo} associated with the given ID, or {@code null} if not found.
	 */
	public AccountTransfo getTransformation(int transformationId)
	{
		return transformations.get(transformationId);
	}
	
	/**
	 * Adds a new transformation to the player's account.<br>
	 * This method saves the data to the database and updates the {@link Player} object.
	 * @param player The {@code Player} who will receive the transformation.
	 * @param transformationId The unique ID of the transformation.
	 * @param count The number of transformations to add.
	 * @return The newly created {@code AccountTransfo} object.
	 */
	public AccountTransfo addNewTransformation(Player player, int transformationId, int count)
	{
		final AccountTransfo transformationCommonData = new AccountTransfo(transformationId, count);
		transformationCommonData.setCount(count);
		DAOManager.getDAO(AccountTransformDAO.class).addTransfo(player.getPlayerAccount(), transformationCommonData);
		transformations.put(transformationCommonData.getCardId(), transformationCommonData);
		player.getTransformCreated().add(transformationCommonData);
		return transformationCommonData;
	}
	
	/**
	 * Checks if a specific transformation exists in the account list.<br>
	 * This method looks up the {@code transformationId} within the internal map.
	 * @param transformationId The unique ID of the transformation to check.
	 * @return {@code true} if the transformation is found, {@code false} otherwise.
	 */
	public boolean hasTransformation(int transformationId)
	{
		return transformations.containsKey(transformationId);
	}
	
	/**
	 * Removes a specific transformation from an {@link Account}.<br>
	 * This method checks if the transformation exists before deleting it.<br>
	 * It updates both the database and the local cache.
	 * @param account The {@code Account} object that owns the transformation.
	 * @param transformationId The unique ID of the transformation to remove.
	 */
	public void deleteTransformation(Account account, int transformationId)
	{
		if (hasTransformation(transformationId))
		{
			DAOManager.getDAO(AccountTransformDAO.class).deleteTransfo(account, transformationId);
			transformations.remove(transformationId);
		}
	}
	
	/**
	 * Refreshes the list of transformations for the current {@link Player}.<br>
	 * This method clears existing data and reloads it from the database.<br>
	 * It also sends an {@code SM_TRANSFORMATION} packet to the player.
	 */
	public void updateTransformationsList()
	{
		transformations.clear();
		for (AccountTransfo transformationCommonData : DAOManager.getDAO(AccountTransformDAO.class).loadAccountTransfo(player.getPlayerAccount()).values())
		{
			transformations.put(transformationCommonData.getCardId(), transformationCommonData);
		}
		
		if (transformations != null)
		{
			PacketSendUtility.sendPacket(player, new SM_TRANSFORMATION(0, player));
		}
	}
	
	/**
	 * Retrieves all transformations that were created during the current session.<br>
	 * This method returns a {@code Map} where the key is the transformation ID.
	 * @return A {@code Map} containing the IDs and details of newly created {@link AccountTransfo} objects.
	 */
	public Map<Integer, AccountTransfo> getTransformationsCreated()
	{
		return transformationsCreated;
	}
}
