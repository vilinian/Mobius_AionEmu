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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.housing.HousePart;

/**
 * Represents a decorative object placed within a house.<br>
 * This class handles the properties and behavior of items used for interior furnishing. It extends {@link AionObject} to integrate with the game world's object system.
 * @author Rolandas
 */
public class HouseDecoration extends AionObject
{
	private final int templateId;
	private byte floor;
	private boolean isUsed;
	private PersistentState persistentState;
	
	/**
	 * Creates a new {@link HouseDecoration} instance.<br>
	 * This constructor initializes the decoration with a default floor value of -1.
	 * @param objectId The unique identifier for the object.
	 * @param templateId The ID of the decoration template.
	 */
	public HouseDecoration(int objectId, int templateId)
	{
		this(objectId, templateId, -1);
	}
	
	/**
	 * Creates a new {@link HouseDecoration} instance.<br>
	 * This constructor initializes the decoration with a specific floor level.<br>
	 * It also sets the initial state to {@code PersistentState.NEW}.
	 * @param objectId The unique identifier for this object.
	 * @param templateId The ID of the decoration template.
	 * @param floor The floor number where the decoration is placed.
	 */
	public HouseDecoration(int objectId, int templateId, int floor)
	{
		super(objectId);
		this.templateId = templateId;
		this.floor = (byte) floor;
		persistentState = PersistentState.NEW;
	}
	
	/**
	 * Retrieves the {@link HousePart} associated with this decoration.<br>
	 * This method looks up the data using the internal {@code templateId}.
	 * @return The {@code HousePart} object corresponding to this decoration.
	 */
	public HousePart getTemplate()
	{
		return DataManager.HOUSE_PARTS_DATA.getPartById(templateId);
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
	 * Retrieves the name of the decoration.<br>
	 * This method returns the {@code String} name from the associated {@link HousePart}.
	 * @return The name of the decoration as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return getTemplate().getName();
	}
	
	/**
	 * Retrieves the current floor level of the house decoration.<br>
	 * This value represents which story the object is placed on.
	 * @return The {@code byte} value representing the floor.
	 */
	public byte getFloor()
	{
		return floor;
	}
	
	/**
	 * Sets the floor level for this house decoration.<br>
	 * This method updates the {@code floor} field if the new value is different.<br>
	 * It also marks the state as {@code UPDATE_REQUIRED} if changes are made to an existing object.
	 * @param value The new floor level to assign.
	 */
	public void setFloor(int value)
	{
		if (value != floor)
		{
			floor = (byte) value;
			if ((persistentState != PersistentState.NEW) && (persistentState != PersistentState.NOACTION))
			{
				persistentState = PersistentState.UPDATE_REQUIRED;
			}
		}
	}
	
	/**
	 * Checks if this tribe class is currently in use.
	 * @return {@code true} if the class is used, {@code false} otherwise.
	 */
	public boolean isUsed()
	{
		return isUsed;
	}
	
	/**
	 * Updates the usage status of this {@code HouseDecoration}.<br>
	 * Sets the internal state to {@code true} or {@code false}.
	 * @param isUsed The new usage status to assign.
	 */
	public void setUsed(boolean isUsed)
	{
		if ((this.isUsed != isUsed) && (persistentState != PersistentState.DELETED))
		{
			this.isUsed = isUsed;
			if ((persistentState != PersistentState.NEW) && (persistentState != PersistentState.NOACTION))
			{
				persistentState = PersistentState.UPDATE_REQUIRED;
			}
		}
	}
	
	/**
	 * Compares this decoration to another object.<br>
	 * It checks if the other object is a {@link HouseDecoration}.<br>
	 * Two objects are considered equal if they have the same unique ID.
	 * @param object The object to compare with this one.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof HouseDecoration))
		{
			return false;
		}
		
		return ((HouseDecoration) object).getObjectId().equals(getObjectId());
	}
	
	/**
	 * Returns a hash code value for this {@link HouseDecoration} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the unique object ID.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		return getObjectId();
	}
}
