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
package com.aionemu.gameserver.world.knownlist;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.MapRegion;

/**
 * This class manages a list of objects that are currently known to the game world.<br>
 * It helps track entities like {@link Player}, {@link Npc}, and other {@link AionObject} instances.<br>
 * Use this class to efficiently manage visibility and spatial awareness for game objects.
 * @author -Nemesiss-
 * @modified kosyachok
 */
public class KnownList
{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(KnownList.class);
	/**
	 * Owner of this KnownList.
	 */
	protected final VisibleObject owner;
	/**
	 * List of objects that this KnownList owner known
	 */
	protected final Map<Integer, VisibleObject> knownObjects = new ConcurrentHashMap<>();
	/**
	 * List of player that this KnownList owner known
	 */
	protected volatile Map<Integer, Player> knownPlayers;
	/**
	 * List of objects that this KnownList owner known
	 */
	protected final Map<Integer, VisibleObject> visualObjects = new ConcurrentHashMap<>();
	/**
	 * List of player that this KnownList owner known
	 */
	protected volatile Map<Integer, Player> visualPlayers;
	private final ReentrantLock lock = new ReentrantLock();
	
	/**
	 * Creates a new {@link KnownList} instance.<br>
	 * This constructor assigns the provided {@code VisibleObject} as the owner of the list.
	 * @param owner The {@code VisibleObject} that owns this known list.
	 */
	public KnownList(VisibleObject owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Updates the list of known objects for the owner.<br>
	 * This method clears old objects and finds new visible ones.<br>
	 * It uses a {@code ReentrantLock} to ensure thread safety during the update.
	 */
	public void doUpdate()
	{
		lock.lock();
		try
		{
			forgetObjects();
			findVisibleObjects();
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Clears all known objects and players from the list.<br>
	 * This method removes entries from {@code knownObjects}, {@code knownPlayers}, {@code visualObjects}, and {@code visualPlayers}.<br>
	 * It also notifies each object to remove this owner from its own known list.
	 */
	public void clear()
	{
		for (VisibleObject object : knownObjects.values())
		{
			object.getKnownList().del(owner, false);
		}
		
		knownObjects.clear();
		if (knownPlayers != null)
		{
			knownPlayers.clear();
		}
		
		visualObjects.clear();
		if (visualPlayers != null)
		{
			visualPlayers.clear();
		}
	}
	
	/**
	 * Checks if the owner of this list knows a specific object.<br>
	 * It returns {@code true} if the object is in the known list.<br>
	 * Otherwise, it returns {@code false}.
	 * @param object The {@link AionObject} to check.
	 * @return {@code true} if the object is known, {@code false} otherwise.
	 */
	public boolean knowns(AionObject object)
	{
		return knownObjects.containsKey(object.getObjectId());
	}
	
	/**
	 * Adds a {@code VisibleObject} to the list of known objects.<br>
	 * This method checks if the owner is aware of the object first.<br>
	 * It also handles special logic for {@link Player} types.
	 * @param object The {@code VisibleObject} to be added.
	 * @return {@code true} if the object was successfully added, otherwise {@code false}.
	 */
	protected boolean add(VisibleObject object)
	{
		if (!isAwareOf(object))
		{
			return false;
		}
		
		if (knownObjects.put(object.getObjectId(), object) == null)
		{
			if (object instanceof Player)
			{
				checkKnownPlayersInitialized();
				knownPlayers.put(object.getObjectId(), (Player) object);
			}
			
			addVisualObject(object);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Adds a {@link VisibleObject} to the list of objects known by the owner.<br>
	 * This method updates the visual tracking for creatures and other objects.<br>
	 * It also handles special logic for players and visibility checks.
	 * @param object The {@code VisibleObject} to be added to the known list.
	 */
	public void addVisualObject(VisibleObject object)
	{
		if (object instanceof Creature)
		{
			if (SecurityConfig.INVIS && (object instanceof Player))
			{
				if (!owner.canSee((Player) object))
				{
					return;
				}
			}
			
			if (visualObjects.put(object.getObjectId(), object) == null)
			{
				if (object instanceof Player)
				{
					checkVisiblePlayersInitialized();
					visualPlayers.put(object.getObjectId(), (Player) object);
				}
				
				owner.getController().see(object);
			}
		}
		else if (visualObjects.put(object.getObjectId(), object) == null)
		{
			owner.getController().see(object);
		}
	}
	
	/**
	 * Removes an object from the known list.<br>
	 * This method updates both the {@code knownObjects} and {@code knownPlayers} maps.<br>
	 * It also calls {@code boolean)} to handle visual updates.
	 * @param object The {@code VisibleObject} to be removed.
	 * @param isOutOfRange A boolean indicating if the object is currently out of range.
	 */
	private void del(VisibleObject object, boolean isOutOfRange)
	{
		/**
		 * object was known.
		 */
		if (knownObjects.remove(object.getObjectId()) != null)
		{
			if (knownPlayers != null)
			{
				knownPlayers.remove(object.getObjectId());
			}
			
			delVisualObject(object, isOutOfRange);
		}
	}
	
	/**
	 * Removes a specific object from the visual list.<br>
	 * This method updates the {@code visualObjects} and {@code visualPlayers} maps.<br>
	 * It also notifies the owner's controller that the object is no longer seen.
	 * @param object The {@link VisibleObject} to be removed.
	 * @param isOutOfRange A boolean indicating if the object is out of range.
	 */
	public void delVisualObject(VisibleObject object, boolean isOutOfRange)
	{
		if (visualObjects.remove(object.getObjectId()) != null)
		{
			if (visualPlayers != null)
			{
				visualPlayers.remove(object.getObjectId());
			}
			
			owner.getController().notSee(object, isOutOfRange);
		}
	}
	
	/**
	 * Removes objects from the known list that are out of range.<br>
	 * This method iterates through all {@code knownObjects}.<br>
	 * It calls {@code boolean)} if the object is no longer reachable.
	 */
	private void forgetObjects()
	{
		for (VisibleObject object : knownObjects.values())
		{
			if (!checkObjectInRange(object) && !object.getKnownList().checkReversedObjectInRange(owner))
			{
				del(object, true);
				object.getKnownList().del(owner, true);
			}
		}
	}
	
	/**
	 * This method updates the list of objects known by the {@code owner}.<br>
	 * It scans all neighboring regions for new {@link VisibleObject} instances.<br>
	 * Objects are added if they are within range and not already known.
	 */
	protected void findVisibleObjects()
	{
		if ((owner == null) || !owner.isSpawned())
		{
			return;
		}
		
		final MapRegion[] regions = owner.getActiveRegion().getNeighbours();
		for (int i = 0; i < regions.length; i++)
		{
			final MapRegion r = regions[i];
			final Map<Integer, VisibleObject> objects = r.getObjects();
			for (VisibleObject newObject : objects.values())
			{
				if ((newObject == owner) || (newObject == null) || !isAwareOf(newObject) || knownObjects.containsKey(newObject.getObjectId()))
				{
					continue;
				}
				
				if (!checkObjectInRange(newObject) && !newObject.getKnownList().checkReversedObjectInRange(owner))
				{
					continue;
				}
				
				/**
				 * New object is not known.
				 */
				if (add(newObject))
				{
					newObject.getKnownList().add(owner);
				}
			}
		}
	}
	
	/**
	 * Checks if the system recognizes a specific object.<br>
	 * This method determines if the {@code newObject} is an instance of {@link Creature}.
	 * @param newObject The object to check.
	 * @return {@code true} if the object is a creature, otherwise {@code false}.
	 */
	protected boolean isAwareOf(VisibleObject newObject)
	{
		return true;
	}
	
	/**
	 * Checks if a {@code VisibleObject} is within the valid range of the owner.<br>
	 * It verifies both the vertical distance and the horizontal distance.
	 * @param newObject The object to check for proximity.
	 * @return {@code true} if the object is in range, {@code false} otherwise.
	 */
	protected boolean checkObjectInRange(VisibleObject newObject)
	{
		// check if Z distance is greater than maxZvisibleDistance
		if (Math.abs(owner.getZ() - newObject.getZ()) > owner.getMaxZVisibleDistance())
		{
			return false;
		}
		
		return MathUtil.isInRange(owner, newObject, owner.getVisibilityDistance());
	}
	
	/**
	 * Checks if a reversed object is within the valid range.<br>
	 * This method currently always returns {@code false}.
	 * @param newObject The {@link VisibleObject} to check.
	 * @return {@code false} for all inputs.
	 */
	protected boolean checkReversedObjectInRange(VisibleObject newObject)
	{
		return false;
	}
	
	/**
	 * Performs an action on every {@link Npc} in the known list.<br>
	 * It uses the provided {@code visitor} to process each NPC.
	 * @param visitor The {@code Visitor<Npc>} used to perform actions on each NPC.
	 */
	public void doOnAllNpcs(Visitor<Npc> visitor)
	{
		doOnAllNpcs(visitor, Integer.MAX_VALUE);
	}
	
	/**
	 * Executes a {@link Visitor} operation on all known {@code Npc} objects.<br>
	 * This method stops processing once the {@code iterationLimit} is reached.<br>
	 * It returns the total number of NPCs visited during the process.
	 * @param visitor The visitor to apply to each NPC found.
	 * @param iterationLimit The maximum number of iterations allowed.
	 * @return The count of NPCs that were successfully visited.
	 */
	public int doOnAllNpcs(Visitor<Npc> visitor, int iterationLimit)
	{
		int counter = 0;
		try
		{
			for (VisibleObject newObject : knownObjects.values())
			{
				if (newObject instanceof Npc)
				{
					if ((++counter) == iterationLimit)
					{
						break;
					}
					
					visitor.visit((Npc) newObject);
				}
			}
		}
		catch (Exception ex)
		{
			// log.error("Exception when running visitor on all npcs" + ex);
		}
		
		return counter;
	}
	
	/**
	 * Performs an action on all {@link Npc} objects that have a specific owner.<br>
	 * This method uses the provided {@code VisitorWithOwner} to process each NPC.<br>
	 * It is a convenience wrapper for the version of this method that takes an iteration limit.
	 * @param visitor The visitor used to perform actions on NPCs and their owners.
	 */
	public void doOnAllNpcsWithOwner(VisitorWithOwner<Npc, VisibleObject> visitor)
	{
		doOnAllNpcsWithOwner(visitor, Integer.MAX_VALUE);
	}
	
	/**
	 * Executes a visitor action on all {@link Npc} objects in the known list.<br>
	 * This method also provides the owner of the {@link KnownList} to the visitor.<br>
	 * It stops processing once it reaches the specified iteration limit.
	 * @param visitor The visitor to apply to each {@link Npc}.
	 * @param iterationLimit The maximum number of NPCs to process.
	 * @return The total number of NPCs that were visited.
	 */
	public int doOnAllNpcsWithOwner(VisitorWithOwner<Npc, VisibleObject> visitor, int iterationLimit)
	{
		int counter = 0;
		try
		{
			for (VisibleObject newObject : knownObjects.values())
			{
				if (newObject instanceof Npc)
				{
					if ((++counter) == iterationLimit)
					{
						break;
					}
					
					visitor.visit((Npc) newObject, owner);
				}
			}
		}
		catch (Exception ex)
		{
			// log.error("Exception when running visitor on all npcs" + ex);
		}
		
		return counter;
	}
	
	/**
	 * Iterates through all {@link Player} objects in the current location.<br>
	 * Applies the provided {@code Visitor} to each non-null player found.<br>
	 * Logs an error if any exception occurs during the process.
	 * @param visitor The {@code Visitor} to apply to every player.
	 */
	public void doOnAllPlayers(Visitor<Player> visitor)
	{
		if (knownPlayers == null)
		{
			return;
		}
		
		try
		{
			for (Player player : knownPlayers.values())
			{
				if (player != null)
				{
					visitor.visit(player);
				}
			}
		}
		catch (Exception ex)
		{
			// log.error("Exception when running visitor on all players" + ex);
		}
	}
	
	/**
	 * Iterates through every {@link VisibleObject} currently in the world.<br>
	 * Applies the provided {@code visitor} logic to each non-null object found.<br>
	 * Logs an error if any exception occurs during the process.
	 * @param visitor The {@code Visitor<VisibleObject>} to execute on each object.
	 */
	public void doOnAllObjects(Visitor<VisibleObject> visitor)
	{
		try
		{
			for (VisibleObject newObject : knownObjects.values())
			{
				if (newObject != null)
				{
					visitor.visit(newObject);
				}
			}
		}
		catch (Exception ex)
		{
			// log.error("Exception when running visitor on all objects" + ex);
		}
	}
	
	/**
	 * Retrieves the collection of all objects currently known by this list.<br>
	 * This method returns the internal {@code Map} containing these objects.
	 * @return A {@code Map} where the key is an {@code Integer} ID and the value is a {@link VisibleObject}.
	 */
	public Map<Integer, VisibleObject> getKnownObjects()
	{
		return knownObjects;
	}
	
	/**
	 * Retrieves the collection of objects currently visible to the owner.<br>
	 * This method returns the internal {@code visualObjects} map.
	 * @return A {@link Map} containing the IDs and {@link VisibleObject} instances that are visible.
	 */
	public Map<Integer, VisibleObject> getVisibleObjects()
	{
		return visualObjects;
	}
	
	/**
	 * Retrieves the list of players currently known by this object.<br>
	 * If no players are known, it returns an empty {@code Map}.
	 * @return A {@code Map} containing player IDs and their corresponding {@link Player} objects.
	 */
	public Map<Integer, Player> getKnownPlayers()
	{
		return knownPlayers != null ? knownPlayers : Collections.<Integer, Player> emptyMap();
	}
	
	/**
	 * Retrieves the list of players currently visible to the owner.<br>
	 * It returns an empty {@code Map} if no players are visible.
	 * @return A {@code Map} containing the IDs and {@link Player} objects that are visible.
	 */
	public Map<Integer, Player> getVisiblePlayers()
	{
		return visualPlayers != null ? visualPlayers : Collections.<Integer, Player> emptyMap();
	}
	
	/**
	 * Verifies if the {@code knownPlayers} map has been initialized.<br>
	 * If it is {@code null}, this method creates a new shared {@code Map}.<br>
	 * It uses double-checked locking to ensure thread safety during initialization.
	 */
	void checkKnownPlayersInitialized()
	{
		if (knownPlayers == null)
		{
			synchronized (this)
			{
				if (knownPlayers == null)
				{
					knownPlayers = new ConcurrentHashMap<>();
				}
			}
		}
	}
	
	/**
	 * Checks if the {@code visualPlayers} map has been initialized.<br>
	 * If it is {@code null}, this method creates a new shared {@code Map} to store them.<br>
	 * It uses double-checked locking to ensure thread safety during initialization.
	 */
	void checkVisiblePlayersInitialized()
	{
		if (visualPlayers == null)
		{
			synchronized (this)
			{
				if (visualPlayers == null)
				{
					visualPlayers = new ConcurrentHashMap<>();
				}
			}
		}
	}
	
	/**
	 * Retrieves a {@link VisibleObject} from the list of known objects.<br>
	 * It uses the provided unique identifier to find the object.
	 * @param targetObjectId The unique ID of the object to retrieve.
	 * @return The {@link VisibleObject} associated with the ID, or {@code null} if not found.
	 */
	public VisibleObject getObject(int targetObjectId)
	{
		return knownObjects.get(targetObjectId);
	}
}
