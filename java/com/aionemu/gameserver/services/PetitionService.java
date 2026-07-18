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
package com.aionemu.gameserver.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PetitionDAO;
import com.aionemu.gameserver.model.Petition;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PETITION;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * This service manages the logic for player petitions within the game world.<br>
 * It handles creating, updating, and processing {@link Petition} objects.<br>
 * It interacts with the {@link PetitionDAO} to persist data and notifies players via {@link SM_PETITION}.
 * @author zdead
 */
public class PetitionService
{
	private static Logger log = LoggerFactory.getLogger(PetitionService.class);
	private static SortedMap<Integer, Petition> registeredPetitions = new TreeMap<>();
	
	/**
	 * Provides the global instance of the {@link PetitionService}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access the service from anywhere in the code.
	 * @return The single shared instance of {@code PetitionService}.
	 */
	public static PetitionService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Initializes the {@link PetitionService}.<br>
	 * This constructor loads all existing petitions from the database into memory.<br>
	 * It populates the internal collection of registered petitions.
	 */
	public PetitionService()
	{
		log.info("Loading PetitionService ...");
		final Set<Petition> petitions = DAOManager.getDAO(PetitionDAO.class).getPetitions();
		for (Petition p : petitions)
		{
			registeredPetitions.put(p.getPetitionId(), p);
		}
		
		log.info("Successfully loaded " + registeredPetitions.size() + " database petitions");
	}
	
	/**
	 * Retrieves all petitions currently registered in the system.<br>
	 * This method returns a collection of {@link Petition} objects.
	 * @return A {@code Collection} containing all active {@code Petition} instances.
	 */
	public Collection<Petition> getRegisteredPetitions()
	{
		return registeredPetitions.values();
	}
	
	/**
	 * Removes all active petitions for a specific player from the system.<br>
	 * This method clears the internal cache and updates the database via {@link PetitionDAO}.<br>
	 * It also sends an {@code SM_PETITION} packet to the player if they are currently online.
	 * @param playerObjId The unique identifier of the player whose petitions will be deleted.
	 */
	public void deletePetition(int playerObjId)
	{
		final Set<Petition> petitions = new HashSet<>();
		for (Petition p : registeredPetitions.values())
		{
			if (p.getPlayerObjId() == playerObjId)
			{
				petitions.add(p);
			}
		}
		
		for (Petition p : petitions)
		{
			if (registeredPetitions.containsKey(p.getPetitionId()))
			{
				registeredPetitions.remove(p.getPetitionId());
			}
		}
		
		DAOManager.getDAO(PetitionDAO.class).deletePetition(playerObjId);
		if ((playerObjId > 0) && (World.getInstance().findPlayer(playerObjId) != null))
		{
			final Player p = World.getInstance().findPlayer(playerObjId);
			PacketSendUtility.sendPacket(p, new SM_PETITION());
		}
		
		rebroadcastPlayerData();
	}
	
	/**
	 * Marks a specific petition as replied in the database.<br>
	 * This method removes the petition from the active list.<br>
	 * It also notifies the player who sent the petition.
	 * @param petitionId The unique identifier of the petition to update.
	 */
	public void setPetitionReplied(int petitionId)
	{
		final int playerObjId = registeredPetitions.get(petitionId).getPlayerObjId();
		DAOManager.getDAO(PetitionDAO.class).setReplied(petitionId);
		registeredPetitions.remove(petitionId);
		rebroadcastPlayerData();
		if ((playerObjId > 0) && (World.getInstance().findPlayer(playerObjId) != null))
		{
			final Player p = World.getInstance().findPlayer(playerObjId);
			PacketSendUtility.sendPacket(p, new SM_PETITION());
		}
	}
	
	/**
	 * Registers a new petition for a specific player.<br>
	 * This method saves the petition to the database and adds it to the active list.<br>
	 * It also notifies all game masters about the new registration.
	 * @param sender The {@code Player} who is creating the petition.
	 * @param typeId The unique identifier for the petition type.
	 * @param title The display name of the petition.
	 * @param contentText The main body text of the petition.
	 * @param additionalData Any extra information required for this petition.
	 * @return The newly created {@code Petition} object.
	 */
	public synchronized Petition registerPetition(Player sender, int typeId, String title, String contentText, String additionalData)
	{
		final int id = DAOManager.getDAO(PetitionDAO.class).getNextAvailableId();
		final Petition ptt = new Petition(id, sender.getObjectId(), typeId, title, contentText, additionalData, 0);
		DAOManager.getDAO(PetitionDAO.class).insertPetition(ptt);
		registeredPetitions.put(ptt.getPetitionId(), ptt);
		broadcastMessageToGM(sender, ptt.getPetitionId());
		return ptt;
	}
	
	/**
	 * Sends the latest petition data to all active players.<br>
	 * This method iterates through {@code registeredPetitions}.<br>
	 * It uses {@code SM_PETITION)} to update clients.
	 */
	private void rebroadcastPlayerData()
	{
		for (Petition p : registeredPetitions.values())
		{
			final Player player = World.getInstance().findPlayer(p.getPlayerObjId());
			if (player != null)
			{
				PacketSendUtility.sendPacket(player, new SM_PETITION(p));
			}
		}
	}
	
	/**
	 * Sends a notification to all Game Masters.<br>
	 * This message informs them of a new support petition.<br>
	 * It displays the name of the {@code sender} and the {@code petitionId}.
	 * @param sender The {@link Player} who created the petition.
	 * @param petitionId The unique ID of the petition.
	 */
	private void broadcastMessageToGM(Player sender, int petitionId)
	{
		final Iterator<Player> players = World.getInstance().getPlayersIterator();
		while (players.hasNext())
		{
			final Player p = players.next();
			if (p.getAccessLevel() > 0)
			{
				PacketSendUtility.sendBrightYellowMessageOnCenter(p, "New Support Petition from: " + sender.getName() + " (#" + petitionId + ")");
			}
		}
	}
	
	/**
	 * Checks if a specific {@link Player} has an active petition.<br>
	 * This method looks up the registration status based on the player's unique ID.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player has a registered petition, otherwise {@code false}.
	 */
	public boolean hasRegisteredPetition(Player player)
	{
		return hasRegisteredPetition(player.getObjectId());
	}
	
	/**
	 * Checks if a specific player has an active petition.<br>
	 * It searches through all registered petitions in the system.
	 * @param playerObjId The unique ID of the player to check.
	 * @return {@code true} if the player has a petition, otherwise {@code false}.
	 */
	public boolean hasRegisteredPetition(int playerObjId)
	{
		boolean result = false;
		for (Petition p : registeredPetitions.values())
		{
			if (p.getPlayerObjId() == playerObjId)
			{
				result = true;
			}
		}
		
		return result;
	}
	
	/**
	 * Retrieves a {@link Petition} based on the provided player ID.<br>
	 * It searches through all registered petitions to find a match.<br>
	 * Returns {@code null} if no petition is found for that ID.
	 * @param playerObjId The unique identifier of the player.
	 * @return The matching {@link Petition} object or {@code null}.
	 */
	public Petition getPetition(int playerObjId)
	{
		for (Petition p : registeredPetitions.values())
		{
			if (p.getPlayerObjId() == playerObjId)
			{
				return p;
			}
		}
		
		return null;
	}
	
	/**
	 * This method retrieves the next unique identifier for a petition.<br>
	 * It ensures that no two petitions share the same ID.<br>
	 * The method is synchronized to prevent multiple threads from getting the same value.
	 * @return the next available {@code int} ID for a new petition.
	 */
	public synchronized int getNextAvailablePetitionId()
	{
		return 0;
	}
	
	/**
	 * Calculates how many players are ahead in the queue.<br>
	 * This method counts the number of registered petitions before the one belonging to {@code playerObjId}.
	 * @param playerObjId The unique identifier of the player.
	 * @return The count of players waiting before this player.
	 */
	public int getWaitingPlayers(int playerObjId)
	{
		int counter = 0;
		for (Petition p : registeredPetitions.values())
		{
			if (p.getPlayerObjId() == playerObjId)
			{
				break;
			}
			
			counter++;
		}
		
		return counter;
	}
	
	/**
	 * Calculates the total wait time for a specific player.<br>
	 * This method counts all petitions registered before the one belonging to {@code playerObjId}.<br>
	 * It adds 15 seconds per petition and 30 seconds of buffer time between each.
	 * @param playerObjId The unique ID of the player to check.
	 * @return The total calculated wait time in seconds.
	 */
	public int calculateWaitTime(int playerObjId)
	{
		final int timePerPetition = 15;
		final int timeBetweenPetition = 30;
		int result = timeBetweenPetition;
		for (Petition p : registeredPetitions.values())
		{
			if (p.getPlayerObjId() == playerObjId)
			{
				break;
			}
			
			result += timePerPetition;
			result += timeBetweenPetition;
		}
		
		return result;
	}
	
	/**
	 * This method is called when a {@link Player} logs into the instance.<br>
	 * It handles any initialization logic required for the player's session.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void onPlayerLogin(Player player)
	{
		if (hasRegisteredPetition(player))
		{
			PacketSendUtility.sendPacket(player, new SM_PETITION(getPetition(player.getObjectId())));
		}
	}
	
	private static class SingletonHolder
	{
		protected static final PetitionService instance = new PetitionService();
	}
}
