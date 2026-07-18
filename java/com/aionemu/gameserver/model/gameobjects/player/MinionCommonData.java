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

import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.minion.MinionDopingBag;
import com.aionemu.gameserver.model.templates.minion.MinionTemplate;
import com.aionemu.gameserver.utils.idfactory.IDFactory;

/**
 * This class holds the common data and properties for all types of minions.<br>
 * It serves as a base template that implements {@link IExpirable} to handle expiration logic.
 */
public class MinionCommonData extends VisibleObjectTemplate implements IExpirable
{
	private final int minionId;
	private Timestamp birthday;
	private int minionObjId;
	private final int masterObjectId;
	private final String minionGrade;
	private String name;
	private final int minionLevel;
	private int growthPoints;
	private boolean locked;
	private boolean isLooting = false;
	private boolean isBuffing = false;
	MinionDopingBag dopingBag = null;
	private Timestamp despawnTime;
	private int expireTime;
	
	/**
	 * Creates a new instance of {@link MinionCommonData}.<br>
	 * This constructor initializes all basic minion properties.<br>
	 * It also generates a unique object ID if one is not provided.
	 * @param minionId The unique identifier for the minion type.
	 * @param masterObjectId The object ID of the owner.
	 * @param name The display name of the minion.
	 * @param minionGrade The rank or grade of the minion.
	 * @param minionLevel The current level of the minion.
	 * @param growthPoints The available points for growth.
	 * @param locked Whether the minion is currently locked.
	 */
	public MinionCommonData(int minionId, int masterObjectId, String name, String minionGrade, int minionLevel, int growthPoints, boolean locked)
	{
		if (minionObjId == 0)
		{
			minionObjId = IDFactory.getInstance().nextId();
		}
		
		this.minionId = minionId;
		this.masterObjectId = masterObjectId;
		this.name = name;
		this.minionGrade = minionGrade;
		this.minionLevel = minionLevel;
		this.growthPoints = growthPoints;
		this.locked = locked;
		dopingBag = new MinionDopingBag();
	}
	
	/**
	 * Sets the unique object identifier for this minion.<br>
	 * This updates the {@code minionObjId} field.
	 * @param minionObjId The new unique ID to assign to the minion.
	 */
	public void setObjectId(int minionObjId)
	{
		this.minionObjId = minionObjId;
	}
	
	/**
	 * Retrieves the unique object identifier for this minion.<br>
	 * This value is used to identify the specific instance in the game world.
	 * @return The unique {@code int} ID of the minion object.
	 */
	public int getObjectId()
	{
		return minionObjId;
	}
	
	/**
	 * Retrieves the unique identifier of the master object.<br>
	 * This ID links the minion to its owner or parent entity.
	 * @return The {@code int} value representing the master object ID.
	 */
	public int getMasterObjectId()
	{
		return masterObjectId;
	}
	
	/**
	 * Retrieves the unique identifier for this minion.<br>
	 * This value is obtained from the {@link MinionTemplate}.
	 * @return The {@code int} ID of the minion template.
	 */
	public int getMinionId()
	{
		return minionId;
	}
	
	/**
	 * Retrieves the grade of the minion.<br>
	 * This value represents the quality level assigned to the minion.
	 * @return The {@code String} representing the minion's grade.
	 */
	public String getMinionGrade()
	{
		return minionGrade;
	}
	
	/**
	 * Retrieves the current level of the minion.<br>
	 * This value represents the progress made by the minion.
	 * @return The {@code int} level of the minion.
	 */
	public int getMinionLevel()
	{
		return minionLevel;
	}
	
	/**
	 * Retrieves the current number of growth points for this minion.<br>
	 * This value represents the progress made toward leveling up.
	 * @return The total amount of {@code growthPoints}.
	 */
	public int getGrowthPoints()
	{
		return growthPoints;
	}
	
	/**
	 * Retrieves the birthday of the minion.<br>
	 * This method converts the {@code Timestamp} into a Unix timestamp in seconds.<br>
	 * If no birthday is set, it returns {@code 0}.
	 * @return The birthday as an {@code int} representing seconds since the epoch.
	 */
	public int getBirthday()
	{
		if (birthday == null)
		{
			return 0;
		}
		
		return (int) (birthday.getTime() / 1000);
	}
	
	/**
	 * Checks if the minion is currently in a locked state.<br>
	 * This status determines if certain actions can be performed on the object.
	 * @return {@code true} if the minion is locked, {@code false} otherwise.
	 */
	public boolean isLocked()
	{
		return locked;
	}
	
	/**
	 * Retrieves the birth date of the minion.<br>
	 * This method returns the {@code Timestamp} value stored in the {@code birthday} field.
	 * @return The {@code Timestamp} representing the minion's birthday.
	 */
	public Timestamp getBirthdayTimestamp()
	{
		return birthday;
	}
	
	/**
	 * Sets the birth date of the minion.<br>
	 * This updates the {@code birthday} field with a new value.
	 * @param birthday The {@code Timestamp} representing the date of birth.
	 */
	public void setBirthday(Timestamp birthday)
	{
		this.birthday = birthday;
	}
	
	/**
	 * Sets the name.
	 * @param name The new name to assign.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Updates the current growth points for this minion.<br>
	 * This method sets the {@code growthPoints} field to a new value.
	 * @param growthPoints The new number of growth points to assign.
	 */
	public void setGrowthPoints(int growthPoints)
	{
		this.growthPoints = growthPoints;
	}
	
	/**
	 * Updates the lock status of the minion.<br>
	 * This method sets whether the minion is currently locked or not.
	 * @param locked The new lock state to apply.
	 */
	public void setLocked(boolean locked)
	{
		this.locked = locked;
	}
	
	/**
	 * Retrieves the expiration time of this object.<br>
	 * This value represents when the object will expire.
	 * @return The expiration time as an {@code int}.
	 */
	@Override
	public int getExpireTime()
	{
		return expireTime;
	}
	
	/**
	 * Finalizes the expiration process for the minion.<br>
	 * This method handles the logic when a minion reaches its end of life.
	 * @param player The {@link Player} who triggered the expiration.
	 */
	@Override
	public void expireEnd(Player player)
	{
	}
	
	/**
	 * Checks if the minion is allowed to expire at this moment.<br>
	 * This method currently always returns {@code false}.
	 * @return {@code true} if the object can expire now, otherwise {@code false}.
	 */
	@Override
	public boolean canExpireNow()
	{
		return false;
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
	}
	
	/**
	 * Retrieves the unique identifier for this minion template.<br>
	 * This value corresponds to the {@code minionId} field.
	 * @return The unique template ID as an {@code int}.
	 */
	@Override
	public int getTemplateId()
	{
		return minionId;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	@Override
	public int getNameId()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Retrieves the time when this object will disappear.<br>
	 * This value is stored as a {@code Timestamp}.
	 * @return The {@code Timestamp} representing the despawn time.
	 */
	public Timestamp getDespawnTime()
	{
		return despawnTime;
	}
	
	/**
	 * Sets the specific time when this object should disappear.<br>
	 * This updates the {@code despawnTime} field.
	 * @param despawnTime The {@code Timestamp} representing the expiration moment.
	 */
	public void setDespawnTime(Timestamp despawnTime)
	{
		this.despawnTime = despawnTime;
	}
	
	/**
	 * Updates the looting status of the minion.<br>
	 * This method sets whether the minion is currently in a looting state.
	 * @param isLooting The new {@code boolean} value to set for the looting status.
	 */
	public void setIsLooting(boolean isLooting)
	{
		this.isLooting = isLooting;
	}
	
	/**
	 * Checks if the minion is currently in a looting state.<br>
	 * This method returns the current status of the {@code isLooting} flag.
	 * @return {@code true} if the minion is looting, {@code false} otherwise.
	 */
	public boolean isLooting()
	{
		return isLooting;
	}
	
	/**
	 * Retrieves the {@code MinionDopingBag} associated with this minion.<br>
	 * This method returns the current doping bag object.
	 * @return the {@code MinionDopingBag} instance or {@code null} if none exists.
	 */
	public MinionDopingBag getDopingBag()
	{
		return dopingBag;
	}
	
	/**
	 * Updates the buffing status of the minion.<br>
	 * This method sets whether the minion is currently applying a buff.
	 * @param isBuffing The new buffing state to set as {@code true} or {@code false}.
	 */
	public void setIsBuffing(boolean isBuffing)
	{
		this.isBuffing = isBuffing;
	}
	
	/**
	 * Checks if the minion is currently applying a buff.<br>
	 * This method returns the current state of the {@code isBuffing} flag.
	 * @return {@code true} if the minion is buffing, {@code false} otherwise.
	 */
	public boolean isBuffing()
	{
		return isBuffing;
	}
}
