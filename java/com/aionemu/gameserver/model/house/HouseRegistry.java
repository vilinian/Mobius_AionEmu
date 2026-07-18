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
package com.aionemu.gameserver.model.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerRegisteredItemsDAO;
import com.aionemu.gameserver.model.gameobjects.HouseDecoration;
import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.templates.housing.PartType;

/**
 * This class manages the registration and retrieval of house-related data.<br>
 * It serves as a central repository for {@link HouseDecoration} and {@link HouseObject} templates.<br>
 * Use this registry to access predefined housing configurations throughout the server.
 * @author Rolandas
 */
public class HouseRegistry
{
	private static final Logger log = LoggerFactory.getLogger(HouseRegistry.class);
	private final House owner;
	private final Map<Integer, HouseObject<?>> objects;
	private final Map<Integer, HouseDecoration> customParts;
	private final HouseDecoration[] defaultParts = new HouseDecoration[28];
	private PersistentState persistentState = PersistentState.UPDATED;
	
	/**
	 * Creates a new {@link HouseRegistry} for a specific house.<br>
	 * This constructor initializes the internal object and decoration maps.
	 * @param owner The {@code House} that owns this registry.
	 */
	public HouseRegistry(House owner)
	{
		this.owner = owner;
		objects = new HashMap<>();
		customParts = new HashMap<>();
	}
	
	/**
	 * Retrieves the {@link House} that owns this registry.<br>
	 * This method returns the primary house associated with the current object.
	 * @return The {@code House} object representing the owner.
	 */
	public House getOwner()
	{
		return owner;
	}
	
	/**
	 * Retrieves all house objects associated with this registry.<br>
	 * It returns a new {@code List} containing every {@link HouseObject}.
	 * @return A {@code List} of all {@code HouseObject} instances.
	 */
	public List<HouseObject<?>> getObjects()
	{
		final List<HouseObject<?>> temp = new ArrayList<>();
		for (HouseObject<?> obj : objects.values())
		{
			temp.add(obj);
		}
		
		return temp;
	}
	
	/**
	 * Retrieves all house objects that have been spawned by a player.<br>
	 * It filters out any objects with a {@code PersistentState.DELETED} status.
	 * @return A {@code List} containing the filtered {@link HouseObject} instances.
	 */
	public List<HouseObject<?>> getSpawnedObjects()
	{
		final List<HouseObject<?>> temp = new ArrayList<>();
		for (HouseObject<?> obj : objects.values())
		{
			if (obj.isSpawnedByPlayer() && (obj.getPersistentState() != PersistentState.DELETED))
			{
				temp.add(obj);
			}
		}
		
		return temp;
	}
	
	/**
	 * Retrieves all house objects that have not been spawned by a player.<br>
	 * It filters out objects with the {@code PersistentState.DELETED} state.
	 * @return A {@code List} containing the filtered {@link HouseObject} instances.
	 */
	public List<HouseObject<?>> getNotSpawnedObjects()
	{
		final List<HouseObject<?>> temp = new ArrayList<>();
		for (HouseObject<?> obj : objects.values())
		{
			if (!obj.isSpawnedByPlayer() && (obj.getPersistentState() != PersistentState.DELETED))
			{
				temp.add(obj);
			}
		}
		
		return temp;
	}
	
	/**
	 * Retrieves a {@link HouseObject} based on its unique ID.<br>
	 * This method looks up the object in the internal registry.
	 * @param itemObjId The unique identifier of the house object to find.
	 * @return The {@code HouseObject} associated with the given ID, or {@code null} if not found.
	 */
	public HouseObject<?> getObjectByObjId(int itemObjId)
	{
		return objects.get(itemObjId);
	}
	
	/**
	 * Adds a new {@link HouseObject} to the registry.<br>
	 * This method returns {@code false} if the object ID already exists or is not in a {@code NEW} state.<br>
	 * It updates the persistent state of the house when successful.
	 * @param houseObject The {@code HouseObject} to be added.
	 * @return {@code true} if the object was successfully added, otherwise {@code false}.
	 */
	public boolean putObject(HouseObject<?> houseObject)
	{
		if (objects.containsKey(houseObject.getObjectId()))
		{
			return false;
		}
		
		if (houseObject.getPersistentState() != PersistentState.NEW)
		{
			log.error("Inserting not new HouseObject: " + houseObject.getObjectId());
			return false;
		}
		
		objects.put(houseObject.getObjectId(), houseObject);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		return true;
	}
	
	/**
	 * Removes a specific object from the house registry.<br>
	 * This method updates the {@code PersistentState} of the removed item.<br>
	 * It returns the object that was removed or {@code null} if it does not exist.
	 * @param itemObjId The unique identifier of the object to remove.
	 * @return The removed {@link HouseObject} instance, or {@code null}.
	 */
	public HouseObject<?> removeObject(int itemObjId)
	{
		if (!objects.containsKey(itemObjId))
		{
			return null;
		}
		
		final HouseObject<?> oldObject = objects.get(itemObjId);
		if (oldObject.getPersistentState() == PersistentState.NEW)
		{
			discardObject(itemObjId);
		}
		else
		{
			oldObject.setPersistentState(PersistentState.DELETED);
		}
		
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		
		return oldObject;
	}
	
	/**
	 * Retrieves a list of custom house decorations.<br>
	 * This method filters out items that are marked as {@code DELETED} or are currently in use.
	 * @return A {@link List} containing the valid {@link HouseDecoration} objects.
	 */
	public List<HouseDecoration> getCustomParts()
	{
		final List<HouseDecoration> temp = new ArrayList<>();
		for (HouseDecoration decor : customParts.values())
		{
			if ((decor.getPersistentState() != PersistentState.DELETED) && !decor.isUsed())
			{
				temp.add(decor);
			}
		}
		
		return temp;
	}
	
	/**
	 * Retrieves a specific {@link HouseDecoration} based on its type and floor.<br>
	 * This method searches through all custom parts in the registry.<br>
	 * It returns the first matching decoration that is not deleted.
	 * @param partType The {@code PartType} to filter by.
	 * @param floor The specific floor number for the decoration.
	 * @return The matching {@code HouseDecoration} object or {@code null} if no match is found.
	 */
	public HouseDecoration getCustomPartByType(PartType partType, int floor)
	{
		for (HouseDecoration deco : customParts.values())
		{
			if ((deco.getPersistentState() != PersistentState.DELETED) && (deco.getTemplate().getType() == partType))
			{
				if (floor == deco.getFloor())
				{
					return deco;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves a specific {@link HouseDecoration} using its unique ID.<br>
	 * This method looks up the decoration in the custom parts collection.<br>
	 * It returns {@code null} if no matching decoration is found.
	 * @param itemObjId The unique identifier of the house decoration to find.
	 * @return The {@link HouseDecoration} object associated with the given ID, or {@code null}.
	 */
	public HouseDecoration getCustomPartByObjId(int itemObjId)
	{
		return customParts.get(itemObjId);
	}
	
	/**
	 * Retrieves a specific {@link HouseDecoration} based on its unique ID and floor.<br>
	 * This method searches through all custom parts in the registry.<br>
	 * It returns the first decoration that matches both criteria and is not deleted.
	 * @param partId The unique identifier of the house part.
	 * @param floor The specific floor level where the part is located.
	 * @return The matching {@link HouseDecoration} object, or {@code null} if no match is found.
	 */
	public HouseDecoration getCustomPartByPartId(int partId, int floor)
	{
		for (HouseDecoration deco : customParts.values())
		{
			if ((deco.getPersistentState() != PersistentState.DELETED) && (deco.getTemplate().getId() == partId) && (deco.getFloor() == floor))
			{
				return deco;
			}
		}
		
		return null;
	}
	
	/**
	 * Counts the number of custom parts that match a specific ID.<br>
	 * It only counts items that are not marked as {@code PersistentState.DELETED}.
	 * @param partId The unique identifier for the part type to count.
	 * @return The total number of active custom parts with the given {@code partId}.
	 */
	public int getCustomPartCountByPartId(int partId)
	{
		int counter = 0;
		for (HouseDecoration deco : customParts.values())
		{
			if ((deco.getPersistentState() != PersistentState.DELETED) && (deco.getTemplate().getId() == partId))
			{
				counter++;
			}
		}
		
		return counter;
	}
	
	/**
	 * Adds a new {@link HouseDecoration} to the custom parts list.<br>
	 * This method checks if the object already exists or is not in a {@code NEW} state.<br>
	 * It updates the registry status to {@code UPDATE_REQUIRED} upon success.
	 * @param houseDeco The decoration to add to the registry.
	 * @return {@code true} if the part was added successfully, otherwise {@code false}.
	 */
	public boolean putCustomPart(HouseDecoration houseDeco)
	{
		if (customParts.containsKey(houseDeco.getObjectId()))
		{
			return false;
		}
		
		if (houseDeco.getPersistentState() != PersistentState.NEW)
		{
			log.error("Inserting not new HouseDecoration: " + houseDeco.getObjectId());
			return false;
		}
		
		customParts.put(houseDeco.getObjectId(), houseDeco);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		return true;
	}
	
	/**
	 * Removes a custom decoration from the house registry.<br>
	 * This method marks the decoration as deleted if it exists.<br>
	 * It also updates the persistent state of the house.
	 * @param itemObjId The unique identifier of the decoration to remove.
	 * @return The {@code HouseDecoration} object that was removed, or {@code null} if not found.
	 */
	public HouseDecoration removeCustomPart(int itemObjId)
	{
		HouseDecoration obj = null;
		
		if (customParts.containsKey(itemObjId))
		{
			obj = customParts.get(itemObjId);
			obj.setPersistentState(PersistentState.DELETED);
			setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
		
		return obj;
	}
	
	/**
	 * Retrieves the list of default house decorations.<br>
	 * This method filters out any {@code null} entries from the internal array.
	 * @return A {@link List} containing all non-null {@link HouseDecoration} objects.
	 */
	public List<HouseDecoration> getDefaultParts()
	{
		final List<HouseDecoration> temp = new ArrayList<>();
		for (HouseDecoration deco : defaultParts)
		{
			if (deco != null)
			{
				temp.add(deco);
			}
		}
		
		return temp;
	}
	
	/**
	 * Retrieves the default decoration for a specific type and floor.<br>
	 * This method looks up the item in the internal {@code defaultParts} array.
	 * @param partType The category of the house part.
	 * @param floor The floor number where the part is located.
	 * @return The {@link HouseDecoration} object corresponding to the input.
	 */
	public HouseDecoration getDefaultPartByType(PartType partType, int floor)
	{
		return defaultParts[partType.getStartLineNr() + floor];
	}
	
	/**
	 * Adds a {@link HouseDecoration} to the default parts list.<br>
	 * This method uses the decoration type and the floor number to determine its position.<br>
	 * It also sets the persistent state of the decoration to {@code PersistentState.NOACTION}.
	 * @param houseDeco The decoration object to store.
	 * @param floor The floor level for the decoration.
	 */
	public void putDefaultPart(HouseDecoration houseDeco, int floor)
	{
		defaultParts[houseDeco.getTemplate().getType().getStartLineNr() + floor] = houseDeco;
		houseDeco.setPersistentState(PersistentState.NOACTION);
	}
	
	/**
	 * Retrieves all decoration parts for the house.<br>
	 * This includes both default and custom parts.<br>
	 * It filters out any {@code null} values from the defaults.
	 * @return A {@link List} containing all {@link HouseDecoration} objects.
	 */
	public List<HouseDecoration> getAllParts()
	{
		final List<HouseDecoration> temp = new ArrayList<>();
		for (HouseDecoration deco : defaultParts)
		{
			if (deco != null)
			{
				temp.add(deco);
			}
		}
		
		for (HouseDecoration decor : customParts.values())
		{
			temp.add(decor);
		}
		
		return temp;
	}
	
	/**
	 * Retrieves a decoration part for the house.<br>
	 * This method searches for a used custom part matching the given {@code PartType} and {@code floor}.<br>
	 * If no custom part is found, it returns the default part for that type and floor.
	 * @param partType The {@code PartType} of the decoration to find.
	 * @param floor The floor number where the decoration is located.
	 * @return The {@link HouseDecoration} object corresponding to the request.
	 */
	public HouseDecoration getRenderPart(PartType partType, int floor)
	{
		for (HouseDecoration decor : customParts.values())
		{
			if ((decor.getTemplate().getType() == partType) && decor.isUsed() && (decor.getFloor() == floor))
			{
				return decor;
			}
		}
		
		return getDefaultPartByType(partType, floor);
	}
	
	/**
	 * Marks a specific decoration as being in use on a certain floor.<br>
	 * This method updates the usage status of {@link HouseDecoration} objects.<br>
	 * It ensures that only one decoration of the same type is active per floor.
	 * @param decorationUse The {@code HouseDecoration} object to be marked as used.
	 * @param floor The floor number where the decoration is placed.
	 */
	public void setPartInUse(HouseDecoration decorationUse, int floor)
	{
		final HouseDecoration defaultDecor = defaultParts[decorationUse.getTemplate().getType().getStartLineNr() + floor];
		if (defaultDecor.getTemplate().getId() == decorationUse.getTemplate().getId())
		{
			defaultDecor.setUsed(true);
			for (HouseDecoration decor : customParts.values())
			{
				if (decor.getTemplate().getType() != decorationUse.getTemplate().getType())
				{
					continue;
				}
				
				if (decor.getPersistentState() != PersistentState.DELETED)
				{
					if (decor.isUsed())
					{
						decor.setUsed(false);
						decor.setFloor(-1);
						if (decor.getPersistentState() == PersistentState.NEW)
						{
							discardPart(decor);
						}
						else
						{
							decor.setPersistentState(PersistentState.DELETED);
						}
					}
				}
			}
			return;
		}
		
		for (HouseDecoration decor : customParts.values())
		{
			if (decor.getTemplate().getType() != decorationUse.getTemplate().getType())
			{
				continue;
			}
			
			if (decor.getPersistentState() != PersistentState.DELETED)
			{
				if (decorationUse.equals(decor))
				{
					decor.setUsed(true);
					decor.setFloor(floor);
					defaultDecor.setUsed(false);
				}
				else
				{
					if (decor.isUsed() && !decorationUse.equals(decor) && (decor.getFloor() == floor))
					{
						decor.setUsed(false);
						decor.setFloor(-1);
						if (decor.getPersistentState() == PersistentState.NEW)
						{
							discardPart(decor);
						}
						else
						{
							decor.setPersistentState(PersistentState.DELETED);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Removes a specific object from the registry.<br>
	 * This method deletes the entry associated with the provided {@code objectId}.
	 * @param objectId The unique identifier of the object to remove.
	 */
	public void discardObject(Integer objectId)
	{
		objects.remove(objectId);
	}
	
	/**
	 * Removes a specific decoration from the custom parts list.<br>
	 * This method uses the {@code HouseDecoration} object ID to identify the part.<br>
	 * It updates the internal registry by removing the entry.
	 * @param decor The {@code HouseDecoration} instance to be removed.
	 */
	public void discardPart(HouseDecoration decor)
	{
		customParts.remove(decor.getObjectId());
	}
	
	/**
	 * Saves the current house data to the database.<br>
	 * This method checks if a save is required based on the {@code persistentState}.<br>
	 * It uses the {@link PlayerRegisteredItemsDAO} to perform the storage operation.
	 */
	public void save()
	{
		if (persistentState == PersistentState.UPDATE_REQUIRED)
		{
			DAOManager.getDAO(PlayerRegisteredItemsDAO.class).store(this, getOwner().getOwnerId());
		}
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
	 * Updates the {@code persistentState} of this decoration.<br>
	 * This method assigns a new {@link PersistentState} to the object.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		this.persistentState = persistentState;
	}
	
	/**
	 * Returns the total number of items in this registry.<br>
	 * This count includes both {@code HouseObject} and {@code HouseDecoration} entries.
	 * @return The sum of all objects and custom parts.
	 */
	public int size()
	{
		return objects.size() + customParts.size();
	}
	
	/**
	 * Removes all active objects from the house.<br>
	 * This method handles the cleanup of spawned items.<br>
	 * It interacts with {@link PlayerRegisteredItemsDAO} to reset data if necessary.
	 */
	public void despawnObjects()
	{
		if (getSpawnedObjects().isEmpty())
		{
			DAOManager.getDAO(PlayerRegisteredItemsDAO.class).resetRegistry(owner.getOwnerId());
		}
		else
		{
			despawnObjects(true);
		}
	}
	
	/**
	 * This method handles the removal of objects from the house.<br>
	 * It calls {@code onDelete()} on all currently spawned objects.<br>
	 * If {@code remove} is {@code true}, it also deletes them from the registry and updates the database.
	 * @param remove Determines whether to permanently delete the objects from the house data.
	 */
	public void despawnObjects(boolean remove)
	{
		for (HouseObject<?> obj : getSpawnedObjects())
		{
			if (obj.isInWorld())
			{
				obj.getController().onDelete();
				obj.clearKnownlist();
			}
			
			if (remove)
			{
				obj.removeFromHouse();
			}
		}
		
		if (remove)
		{
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			save();
		}
	}
}
