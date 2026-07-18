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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerPetsDAO;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;

/**
 * This class manages the collection of pets owned by a player.<br>
 * It handles loading, saving, and tracking pet data for the {@link com.aionemu.gameserver.model.gameobjects.player.Player} object.
 * @author ATracer
 */
public class PetList
{
	private final Player player;
	private int lastUsedPetId;
	private final Map<Integer, PetCommonData> pets = new HashMap<>();
	
	/**
	 * Creates a new {@code PetList} for a specific player.<br>
	 * This constructor initializes the player reference.<br>
	 * It automatically calls {@code loadPets} to populate the pet data.
	 * @param player The {@code Player} object associated with this list.
	 */
	PetList(Player player)
	{
		this.player = player;
		loadPets();
	}
	
	/**
	 * Loads all pets for the current player from the database.<br>
	 * This method populates the internal {@code pets} map.<br>
	 * It also registers expiration tasks and identifies the last used pet.
	 */
	public void loadPets()
	{
		final List<PetCommonData> playerPets = DAOManager.getDAO(PlayerPetsDAO.class).getPlayerPets(player);
		PetCommonData lastUsedPet = null;
		for (PetCommonData pet : playerPets)
		{
			if (pet.getExpireTime() > 0)
			{
				ExpireTimerTask.getInstance().addTask(pet, player);
			}
			
			pets.put(pet.getPetId(), pet);
			if ((lastUsedPet == null) || pet.getDespawnTime().after(lastUsedPet.getDespawnTime()))
			{
				lastUsedPet = pet;
			}
		}
		
		if (lastUsedPet != null)
		{
			lastUsedPetId = lastUsedPet.getPetId();
		}
	}
	
	/**
	 * Retrieves all pets associated with the player.<br>
	 * This method returns a collection of {@link PetCommonData} objects.
	 * @return A {@code Collection} containing all loaded pet data.
	 */
	public Collection<PetCommonData> getPets()
	{
		return pets.values();
	}
	
	/**
	 * Retrieves a specific pet based on its unique ID.<br>
	 * This method looks up the data in the internal {@code pets} map.
	 * @param petId The unique identifier of the pet to find.
	 * @return The {@link PetCommonData} object for the given ID, or {@code null} if not found.
	 */
	public PetCommonData getPet(int petId)
	{
		return pets.get(petId);
	}
	
	/**
	 * Retrieves the last pet that was used by the player.<br>
	 * It uses the {@code lastUsedPetId} to find the data.
	 * @return the {@link PetCommonData} of the last used pet, or {@code null} if not found.
	 */
	public PetCommonData getLastUsedPet()
	{
		return getPet(lastUsedPetId);
	}
	
	/**
	 * Updates the ID of the most recently used pet.<br>
	 * This value is used by {@code getLastUsedPet} to retrieve the correct pet data.
	 * @param lastUsedPetId The unique identifier for the pet to be saved.
	 */
	public void setLastUsedPetId(int lastUsedPetId)
	{
		this.lastUsedPetId = lastUsedPetId;
	}
	
	/**
	 * Adds a new pet to the player's collection.<br>
	 * This method creates and stores {@code PetCommonData}.
	 * @param player The {@link Player} who will own the pet.
	 * @param petId The unique identifier for the pet type.
	 * @param decorationId The ID of the visual decoration for the pet.
	 * @param name The custom name given to the pet.
	 * @param expireTime The time in milliseconds when the pet expires.
	 * @return The newly created {@code PetCommonData} object.
	 */
	public PetCommonData addPet(Player player, int petId, int decorationId, String name, int expireTime)
	{
		return addPet(player, petId, decorationId, System.currentTimeMillis(), name, expireTime);
	}
	
	/**
	 * Adds a new pet to the player's collection.<br>
	 * This method saves the pet data to the database and updates the local list.
	 * @param player The {@link Player} who owns the new pet.
	 * @param petId The unique identifier for the pet type.
	 * @param decorationId The ID of the visual decoration for the pet.
	 * @param birthday The timestamp representing the pet's birth date.
	 * @param name The custom name given to the pet.
	 * @param expireTime The time in milliseconds until the pet expires.
	 * @return The newly created {@link PetCommonData} object.
	 */
	public PetCommonData addPet(Player player, int petId, int decorationId, long birthday, String name, int expireTime)
	{
		final PetCommonData petCommonData = new PetCommonData(petId, player.getObjectId(), expireTime);
		petCommonData.setDecoration(decorationId);
		petCommonData.setName(name);
		petCommonData.setBirthday(new Timestamp(birthday));
		petCommonData.setDespawnTime(new Timestamp(System.currentTimeMillis()));
		DAOManager.getDAO(PlayerPetsDAO.class).insertPlayerPet(petCommonData);
		pets.put(petId, petCommonData);
		return petCommonData;
	}
	
	/**
	 * Checks if a specific pet exists in the player's list.<br>
	 * This method looks for the {@code petId} within the internal collection.
	 * @param petId The unique identifier of the pet to check.
	 * @return {@code true} if the pet is found, otherwise {@code false}.
	 */
	public boolean hasPet(int petId)
	{
		return pets.containsKey(petId);
	}
	
	/**
	 * Removes a specific pet from the player's collection.<br>
	 * This method checks if the pet exists before deleting it.<br>
	 * It updates both the local memory and the database.
	 * @param petId The unique identifier of the pet to remove.
	 */
	public void deletePet(int petId)
	{
		if (hasPet(petId))
		{
			pets.remove(petId);
			DAOManager.getDAO(PlayerPetsDAO.class).removePlayerPet(player, petId);
		}
	}
}
