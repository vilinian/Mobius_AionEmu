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

import com.aionemu.gameserver.controllers.PlaceableObjectController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.housing.AbstractHouseObject;
import com.aionemu.gameserver.model.templates.housing.HouseType;
import com.aionemu.gameserver.model.templates.housing.HousingCategory;
import com.aionemu.gameserver.model.templates.housing.LimitType;
import com.aionemu.gameserver.model.templates.housing.PlaceArea;
import com.aionemu.gameserver.model.templates.housing.PlaceLocation;
import com.aionemu.gameserver.model.templates.housing.PlaceableHouseObject;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.PlayerAwareKnownList;

/**
 * Represents a generic object that can be placed within a {@link House}.<br>
 * This class serves as the base for various house-related decorations and functional items.<br>
 * It handles common properties shared by all objects of type {@code T}.
 * @author Rolandas
 * @param <T>
 */
public abstract class HouseObject<T extends PlaceableHouseObject>extends VisibleObject implements IExpirable
{
	private int expireEnd;
	private float x;
	private float y;
	private float z;
	private byte heading;
	private int ownerUsedCount = 0;
	private int visitorUsedCount = 0;
	private Integer color = null;
	private int colorExpireEnd;
	private final House ownerHouse;
	
	// don't set it directly, ever!!! Use setPersistentState() method instead
	private PersistentState persistentState = PersistentState.NEW;
	
	/**
	 * Creates a new instance of a {@link HouseObject}.<br>
	 * This constructor initializes the object with its owner and template data.<br>
	 * It also sets up the required controller and known list for the object.
	 * @param owner The {@link House} that owns this object.
	 * @param objId The unique identifier for the object instance.
	 * @param templateId The ID of the template used to create this object.
	 */
	public HouseObject(House owner, int objId, int templateId)
	{
		super(objId, new PlaceableObjectController<>(), null, DataManager.HOUSING_OBJECT_DATA.getTemplateById(templateId), null);
		ownerHouse = owner;
		getController().setOwner(this);
		setKnownlist(new PlayerAwareKnownList(this));
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
	 * Updates the {@code persistentState} of this house object.<br>
	 * This method handles state transitions and notifies the registry when an update is required.
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
				else if (this.persistentState != PersistentState.DELETED)
				{
					this.persistentState = PersistentState.DELETED;
					ownerHouse.getRegistry().setPersistentState(PersistentState.UPDATE_REQUIRED);
				}
				break;
			case UPDATE_REQUIRED:
				if (this.persistentState == PersistentState.NEW)
				{
					break;
				}
			default:
				if (this.persistentState != persistentState)
				{
					this.persistentState = persistentState;
					ownerHouse.getRegistry().setPersistentState(PersistentState.UPDATE_REQUIRED);
				}
		}
	}
	
	/**
	 * Retrieves the expiration time of this object.<br>
	 * This value represents when the object will expire.
	 * @return The expiration time as an {@code int}.
	 */
	@Override
	public int getExpireTime()
	{
		return expireEnd;
	}
	
	/**
	 * Sets the expiration time for this object.<br>
	 * The value is stored in the {@code expireEnd} field.
	 * @param time The new expiration time to set.
	 */
	public void setExpireTime(int time)
	{
		expireEnd = time;
	}
	
	/**
	 * Marks the house object as deleted for a specific player.<br>
	 * This method updates the {@code PersistentState} to {@code DELETED}.
	 * @param player The {@link Player} who triggered the expiration.
	 */
	@Override
	public void expireEnd(Player player)
	{
		setPersistentState(PersistentState.DELETED);
	}
	
	/**
	 * Calculates the remaining time until this object expires.<br>
	 * It returns the difference between the expiration time and the current system time.<br>
	 * If the object is not set to expire, it returns -1.<br>
	 * If the object has already expired, it returns 0.
	 * @return The number of seconds remaining until expiration, or -1 if no expiration is set.
	 */
	public int getUseSecondsLeft()
	{
		if (expireEnd == 0)
		{
			return -1;
		}
		
		final int diff = expireEnd - (int) (System.currentTimeMillis() / 1000);
		if (diff < 0)
		{
			return 0;
		}
		
		return diff;
	}
	
	/**
	 * Sends an expiration message to a specific player.<br>
	 * This method notifies the {@code Player} that an object is expiring.<br>
	 * It uses the provided {@code time} value to format the remaining duration.
	 * @param player The {@code Player} who will receive the notification.
	 * @param time The amount of time left before expiration.
	 */
	@Override
	public void expireMessage(Player player, int time)
	{
		// TODO Add if it exists
	}
	
	/**
	 * Retrieves the name of the house object.<br>
	 * This method returns the {@code String} representation of the name ID from the template.
	 * @return The name of the house object as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return String.valueOf(objectTemplate.getNameId());
	}
	
	/**
	 * Retrieves the template associated with this house object.<br>
	 * This method returns the base configuration for the object.
	 * @return The template of type {@code T}.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T getObjectTemplate()
	{
		return (T) objectTemplate;
	}
	
	/**
	 * Retrieves the X coordinate of the bookmark.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the X coordinate.
	 */
	@Override
	public float getX()
	{
		return x;
	}
	
	/**
	 * Updates the {@code x} coordinate of this object.<br>
	 * This method marks the state as requiring an update if the value changes.<br>
	 * It also updates the position if it is not {@code null}.
	 * @param x The new {@code float} value for the horizontal coordinate.
	 */
	public void setX(float x)
	{
		if (this.x != x)
		{
			this.x = x;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			if (position != null)
			{
				position.setXYZH(x, null, null, null);
			}
		}
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	@Override
	public float getY()
	{
		return y;
	}
	
	/**
	 * Updates the {@code y} coordinate of this object.<br>
	 * This method marks the state as requiring an update if the value changes.<br>
	 * It also updates the position if it is not {@code null}.
	 * @param y The new {@code float} value for the vertical coordinate.
	 */
	public void setY(float y)
	{
		if (this.y != y)
		{
			this.y = y;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			if (position != null)
			{
				position.setXYZH(null, y, null, null);
			}
		}
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	@Override
	public float getZ()
	{
		return z;
	}
	
	/**
	 * Updates the vertical position of the object.<br>
	 * This method sets the {@code z} coordinate to a new value.<br>
	 * It also updates the persistent state and the current position if available.
	 * @param z The new height value for the object.
	 */
	public void setZ(float z)
	{
		if (this.z != z)
		{
			this.z = z;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			if (position != null)
			{
				position.setXYZH(null, null, z, null);
			}
		}
	}
	
	/**
	 * Retrieves the current rotation direction of the object.<br>
	 * This value is stored as a {@code byte}.
	 * @return The heading value of the object.
	 */
	@Override
	public byte getHeading()
	{
		return heading;
	}
	
	/**
	 * Updates the rotation direction of this object.<br>
	 * This method sets the {@code heading} value and marks the state for an update.<br>
	 * It also updates the position if it is not {@code null}.
	 * @param heading The new rotation value to apply.
	 */
	public void setHeading(byte heading)
	{
		if (this.heading != heading)
		{
			this.heading = heading;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			if (position != null)
			{
				position.setXYZH(null, null, null, heading);
			}
		}
	}
	
	/**
	 * Retrieves the current rotation of the object.<br>
	 * This value is calculated based on the {@code heading} field.<br>
	 * The result is multiplied by {@code 3}.
	 * @return The calculated rotation as an {@code int}.
	 */
	public int getRotation()
	{
		final int rotation = heading & 0xFF;
		return rotation * 3;
	}
	
	/**
	 * Updates the rotation of the object.<br>
	 * This method calculates the new heading based on the provided value.
	 * @param rotation The new rotation value to apply.
	 */
	public void setRotation(int rotation)
	{
		setHeading((byte) Math.ceil(rotation / 3f));
	}
	
	/**
	 * Retrieves the specific location of this house object.<br>
	 * This method casts the internal template to a {@code PlaceableHouseObject}.<br>
	 * It returns the {@code PlaceLocation} defined in the template.
	 * @return the {@code PlaceLocation} of the object.
	 */
	public PlaceLocation getPlaceLocation()
	{
		return ((PlaceableHouseObject) objectTemplate).getLocation();
	}
	
	/**
	 * Retrieves the area associated with this house object.<br>
	 * This method accesses the {@code PlaceArea} from the underlying template.
	 * @return the {@link PlaceArea} of the object.
	 */
	public PlaceArea getPlaceArea()
	{
		return ((PlaceableHouseObject) objectTemplate).getArea();
	}
	
	/**
	 * Retrieves the maximum number of objects allowed for placement.<br>
	 * This value depends on the house size and whether it is a trial account.
	 * @param trial Set to {@code true} if the player is using a trial account.
	 * @return The integer limit for placing this object.
	 */
	public int getPlacementLimit(boolean trial)
	{
		final LimitType limitType = ((PlaceableHouseObject) objectTemplate).getPlacementLimit();
		final HouseType size = HouseType.fromValue(ownerHouse.getBuilding().getSize());
		if (trial)
		{
			return limitType.getTrialObjectPlaceLimit(size);
		}
		
		return limitType.getObjectPlaceLimit(size);
	}
	
	/**
	 * Retrieves the quality level of this house object.<br>
	 * This method calls {@code getQuality} on the underlying template.
	 * @return the {@code ItemQuality} of the object.
	 */
	public ItemQuality getQuality()
	{
		return ((AbstractHouseObject) objectTemplate).getQuality();
	}
	
	/**
	 * Retrieves the distance at which players can talk to this object.<br>
	 * This value is fetched from the underlying {@code AbstractHouseObject} template.
	 * @return The talking distance as a {@code float}.
	 */
	public float getTalkingDistance()
	{
		return ((AbstractHouseObject) objectTemplate).getTalkingDistance();
	}
	
	/**
	 * Retrieves the category of the house object.<br>
	 * This method calls {@code getCategory} on the underlying template.
	 * @return the {@code HousingCategory} associated with this object.
	 */
	public HousingCategory getCategory()
	{
		return ((AbstractHouseObject) objectTemplate).getCategory();
	}
	
	/**
	 * Retrieves the {@link House} that owns this object.<br>
	 * This method returns the house associated with the current instance.
	 * @return The {@code House} object belonging to this item.
	 */
	public House getOwnerHouse()
	{
		return ownerHouse;
	}
	
	/**
	 * Retrieves the unique identifier of the house owner.<br>
	 * This value is obtained from the {@link House} object associated with this item.
	 * @return The {@code int} ID of the house owner.
	 */
	public int getPlayerId()
	{
		return ownerHouse.getOwnerId();
	}
	
	/**
	 * Retrieves the number of times the owner has used this object.<br>
	 * This value is updated via {@code incrementOwnerUsedCount}.
	 * @return The total count of uses by the owner as an {@code int}.
	 */
	public int getOwnerUsedCount()
	{
		return ownerUsedCount;
	}
	
	/**
	 * Increases the number of times the owner has used this object.<br>
	 * This method updates the {@code ownerUsedCount} field.<br>
	 * It also sets the persistent state to {@code UPDATE_REQUIRED}.
	 */
	public void incrementOwnerUsedCount()
	{
		ownerUsedCount++;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Increases the number of times a visitor has used this object.<br>
	 * This method updates the {@code visitorUsedCount} field.<br>
	 * It also sets the persistent state to {@code PersistentState.UPDATE_REQUIRED}.
	 */
	public void incrementVisitorUsedCount()
	{
		visitorUsedCount++;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Updates the number of times the owner has used this object.<br>
	 * This method marks the state as requiring an update if the value changes.
	 * @param ownerUsedCount The new count to set for the owner's usage.
	 */
	public void setOwnerUsedCount(int ownerUsedCount)
	{
		if (this.ownerUsedCount != ownerUsedCount)
		{
			this.ownerUsedCount = ownerUsedCount;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
	}
	
	/**
	 * Retrieves the total number of times this object has been used by visitors.<br>
	 * This count is tracked separately from the owner's usage.
	 * @return The current {@code int} value of visitor uses.
	 */
	public int getVisitorUsedCount()
	{
		return visitorUsedCount;
	}
	
	/**
	 * Updates the number of times a visitor has used this object.<br>
	 * This method marks the state as requiring an update if the value changes.
	 * @param visitorUsedCount The new count to set for visitors.
	 */
	public void setVisitorUsedCount(int visitorUsedCount)
	{
		if (this.visitorUsedCount != visitorUsedCount)
		{
			this.visitorUsedCount = visitorUsedCount;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
	}
	
	/**
	 * Checks if the object was placed by a player.<br>
	 * It verifies if the coordinates are non-zero.
	 * @return {@code true} if any coordinate is not {@code 0}, otherwise {@code false}.
	 */
	public boolean isSpawnedByPlayer()
	{
		return (x != 0) || (y != 0) || (z != 0);
	}
	
	/**
	 * Retrieves the controller associated with this object.<br>
	 * This method returns a {@link PlaceableObjectController} cast to the specific type {@code T}.
	 * @return The {@code PlaceableObjectController} for this object.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PlaceableObjectController<T> getController()
	{
		return (PlaceableObjectController<T>) super.getController();
	}
	
	/**
	 * Spawns the object into the game world if it was created by a player.<br>
	 * This method creates a new {@code Position} and registers the object with the {@link SpawnEngine}.<br>
	 * It also updates the {@link PlayerAwareKnownList} for the current object.
	 */
	public void spawn()
	{
		if (!isSpawnedByPlayer())
		{
			return;
		}
		
		final World w = World.getInstance();
		if ((position == null) || !isSpawned())
		{
			position = w.createPosition(ownerHouse.getWorldId(), x, y, z, heading, ownerHouse.getInstanceId());
			SpawnEngine.bringIntoWorld(this);
		}
		
		updateKnownlist();
	}
	
	/**
	 * Resets the position and heading of this house object.<br>
	 * It sets the {@code x}, {@code y}, and {@code z} coordinates to {@code 0}.<br>
	 * The {@code heading} is also reset to {@code 0}.
	 */
	public void removeFromHouse()
	{
		this.setX(0);
		this.setY(0);
		this.setZ(0);
		this.setHeading((byte) 0);
	}
	
	/**
	 * Handles the logic when a {@link Player} interacts with this chair.<br>
	 * This method is triggered by the use action.
	 * @param player The {@code Player} who used the object.
	 */
	public void onUse(Player player)
	{
	}
	
	/**
	 * Handles the request from a {@link Player} to start a dialog.<br>
	 * This method is triggered when a player interacts with an NPC.
	 * @param player The {@code Player} object who initiated the request.
	 */
	public void onDialogRequest(Player player)
	{
		onUse(player);
	}
	
	/**
	 * Handles the logic when a creature is despawned.<br>
	 * This method cancels the {@code DECAY} task.<br>
	 * It also clears the aggro list and observation controller of the owner if they are still spawned.
	 */
	public void onDespawn()
	{
	}
	
	/**
	 * Retrieves the current color of the house object.<br>
	 * This value may be {@code null} if no color is assigned.
	 * @return The {@code Integer} color value.
	 */
	public Integer getColor()
	{
		return color;
	}
	
	/**
	 * Updates the visual color of the house object.<br>
	 * This method marks the state as {@code UPDATE_REQUIRED} if the value changes.
	 * @param color The new {@code Integer} color value to apply.
	 */
	public void setColor(Integer color)
	{
		if (color != this.color)
		{
			this.color = color;
			setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
	}
	
	/**
	 * Gets the expiration time for the object's color.<br>
	 * This value represents when the current color effect will end.
	 * @return The {@code int} value of the color expiration time.
	 */
	public int getColorExpireEnd()
	{
		return colorExpireEnd;
	}
	
	/**
	 * Sets the expiration time for the object's color.<br>
	 * This updates the {@code colorExpireEnd} field.
	 * @param colorExpireEnd The timestamp when the current color expires.
	 */
	public void setColorExpireEnd(int colorExpireEnd)
	{
		this.colorExpireEnd = colorExpireEnd;
	}
}
