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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerPetsDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.pet.PetDopingBag;
import com.aionemu.gameserver.model.templates.pet.PetFunctionType;
import com.aionemu.gameserver.model.templates.pet.PetTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.toypet.PetAdoptionService;
import com.aionemu.gameserver.services.toypet.PetFeedProgress;
import com.aionemu.gameserver.services.toypet.PetHungryLevel;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.idfactory.IDFactory;

/**
 * This class holds common data and properties shared by all types of pets.<br>
 * It serves as a base model for pet-related objects in the game world.<br>
 * It implements {@link IExpirable} to handle time-limited pet effects or statuses.
 * @author ATracer
 */
public class PetCommonData extends VisibleObjectTemplate implements IExpirable
{
	private int decoration;
	private String name;
	private final int petId;
	private Timestamp birthday;
	PetFeedProgress feedProgress = null;
	PetDopingBag dopingBag = null;
	private volatile boolean cancelFeed = false;
	private boolean feedingTime = true;
	private long refeedTime;
	private final int petObjectId;
	private final int masterObjectId;
	private long startMoodTime;
	private int shuggleCounter;
	private int lastSentPoints;
	private long moodCdStarted;
	private long giftCdStarted;
	private final int expireTime;
	private Timestamp despawnTime;
	private boolean isLooting = false;
	private boolean isBuffing = false;
	private boolean isSeller = false;
	
	/**
	 * Initializes a new instance of {@code PetCommonData}.<br>
	 * This constructor sets up the basic pet properties and initializes specific data based on the template.
	 * @param petId The unique identifier for the pet type.
	 * @param masterObjectId The object ID of the owner.
	 * @param expireTime The time at which this pet will expire.
	 */
	public PetCommonData(int petId, int masterObjectId, int expireTime)
	{
		petObjectId = IDFactory.getInstance().nextId();
		this.petId = petId;
		this.masterObjectId = masterObjectId;
		this.expireTime = expireTime;
		final PetTemplate template = DataManager.PET_DATA.getPetTemplate(petId);
		if (template.ContainsFunction(PetFunctionType.FOOD))
		{
			final int flavourId = template.getPetFunction(PetFunctionType.FOOD).getId();
			final int lovedLimit = DataManager.PET_FEED_DATA.getFlavourById(flavourId).getLovedFoodLimit();
			feedProgress = new PetFeedProgress((byte) (lovedLimit & 0xFF));
		}
		
		if (template.ContainsFunction(PetFunctionType.DOPING))
		{
			dopingBag = new PetDopingBag();
		}
	}
	
	/**
	 * Retrieves the current decoration value for the pet.
	 * @return the {@code int} value of the decoration.
	 */
	public int getDecoration()
	{
		return decoration;
	}
	
	/**
	 * Updates the decoration value for this pet.<br>
	 * This method sets the {@code decoration} field to a new integer value.
	 * @param decoration The new decoration ID to assign.
	 */
	public void setDecoration(int decoration)
	{
		this.decoration = decoration;
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
	 * Sets the name.
	 * @param name The new name to assign.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Retrieves the unique identifier for this pet.<br>
	 * This value is obtained from the {@link PetTemplate}.
	 * @return The {@code int} ID of the pet template.
	 */
	public int getPetId()
	{
		return petId;
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
	 * Retrieves the time when the pet should be fed again.<br>
	 * This value is used to manage the feeding schedule.
	 * @return the {@code long} timestamp of the next refeed time.
	 */
	public long getRefeedTime()
	{
		return refeedTime;
	}
	
	/**
	 * Updates the internal refeed time for the pet.<br>
	 * This method sets the {@code refeedTime} field to the provided value.
	 * @param curentTime The new timestamp to set for the refeed time.
	 */
	public void setRefeedTime(long curentTime)
	{
		refeedTime = curentTime;
	}
	
	/**
	 * Updates the feeding status of the pet.<br>
	 * This method sets the {@code feedingTime} variable to the provided value.
	 * @param food The new feeding status to set as {@code true} or {@code false}.
	 */
	public void setIsFeedingTime(boolean food)
	{
		feedingTime = food;
	}
	
	/**
	 * Checks if the pet is currently in its feeding period.<br>
	 * This method returns the current state of the {@code feedingTime} variable.
	 * @return {@code true} if it is feeding time, or {@code false} otherwise.
	 */
	public boolean isFeedingTime()
	{
		return feedingTime;
	}
	
	/**
	 * Checks if the feeding process has been cancelled.
	 * @return {@code true} if the feed is cancelled, {@code false} otherwise.
	 */
	public boolean getCancelFeed()
	{
		return cancelFeed;
	}
	
	/**
	 * Updates the status of whether the feeding process should be stopped.<br>
	 * This method sets the {@code cancelFeed} flag to either {@code true} or {@code false}.
	 * @param cancelFeed The new value to set for the cancellation status.
	 */
	public void setCancelFeed(boolean cancelFeed)
	{
		this.cancelFeed = cancelFeed;
	}
	
	/**
	 * Schedules a task to reset the pet's feeding status.<br>
	 * This method sets {@code feedingTime} to {@code false} immediately.<br>
	 * It uses {@link ThreadPoolManager} to trigger a refeed after a delay.
	 * @param reFoodTime The delay in milliseconds before the refeed occurs.
	 */
	public void scheduleRefeed(long reFoodTime)
	{
		setIsFeedingTime(false);
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				feedingTime = true;
				refeedTime = 0;
				feedProgress.setHungryLevel(PetHungryLevel.HUNGRY);
			}
		}, reFoodTime);
	}
	
	/**
	 * Calculates the remaining time until the pet can be fed again.<br>
	 * It compares the {@code refeedTime} with the current system time.<br>
	 * If the time has already passed, it returns 0.
	 * @return The number of milliseconds remaining until the next feeding is allowed.
	 */
	public long getRefeedDelay()
	{
		long time = refeedTime - System.currentTimeMillis();
		if (time < 0)
		{
			refeedTime = 0;
			time = 0;
		}
		
		return time;
	}
	
	/**
	 * Retrieves the unique object identifier for this pet.<br>
	 * This value is used to identify the specific instance in the game world.
	 * @return The unique {@code int} ID of the pet object.
	 */
	public int getObjectId()
	{
		return petObjectId;
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
	 * Retrieves the unique identifier for this minion template.<br>
	 * This value corresponds to the {@code minionId} field.
	 * @return The unique template ID as an {@code int}.
	 */
	@Override
	public int getTemplateId()
	{
		return petId;
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
	 * Retrieves the timestamp when the mood calculation started.<br>
	 * This value is stored in the {@code startMoodTime} field.
	 * @return The starting time of the mood as a {@code long}.
	 */
	public long getMoodStartTime()
	{
		return startMoodTime;
	}
	
	/**
	 * Retrieves the current value of the {@code shuggleCounter}.<br>
	 * This counter tracks specific pet-related actions.
	 * @return The current integer value of the {@code shuggleCounter}.
	 */
	public int getShuggleCounter()
	{
		return shuggleCounter;
	}
	
	/**
	 * Updates the {@code shuggleCounter} value for this pet.<br>
	 * This method sets the internal counter used to track specific pet actions.
	 * @param shuggleCounter The new integer value to assign to the counter.
	 */
	public void setShuggleCounter(int shuggleCounter)
	{
		this.shuggleCounter = shuggleCounter;
	}
	
	/**
	 * Calculates the current mood points for a pet.<br>
	 * This method computes points based on elapsed time and the {@code shuggleCounter}.<br>
	 * If {@code forPacket} is {@code true}, the result is capped at {@code 9000}.
	 * @param forPacket Determines if the value should be capped for network packets.
	 * @return The calculated mood points as an {@code int}.
	 */
	public int getMoodPoints(boolean forPacket)
	{
		if (startMoodTime == 0)
		{
			startMoodTime = System.currentTimeMillis();
		}
		
		final int points = Math.round((System.currentTimeMillis() - startMoodTime) / 1000f) + (shuggleCounter * 1000);
		if (forPacket && (points > 9000))
		{
			return 9000;
		}
		
		return points;
	}
	
	/**
	 * Retrieves the number of points sent during the last update.<br>
	 * This value is used to track progress for pet interactions.
	 * @return The integer value of {@code lastSentPoints}.
	 */
	public int getLastSentPoints()
	{
		return lastSentPoints;
	}
	
	/**
	 * Updates the value of the last sent points.<br>
	 * This method stores the {@code int} value provided in the {@code lastSentPoints} field.
	 * @param points The number of points to set.
	 */
	public void setLastSentPoints(int points)
	{
		lastSentPoints = points;
	}
	
	/**
	 * Increments the {@code shuggleCounter} for the pet.<br>
	 * This method only succeeds if the mood remaining time is less than or equal to {@code 0}.<br>
	 * It updates the {@code moodCdStarted} timestamp when successful.
	 * @return {@code true} if the counter was successfully increased, {@code false} otherwise.
	 */
	public boolean increaseShuggleCounter()
	{
		if (getMoodRemainingTime() > 0)
		{
			return false;
		}
		
		moodCdStarted = System.currentTimeMillis();
		shuggleCounter++;
		return true;
	}
	
	/**
	 * Resets the mood-related statistics for this pet.<br>
	 * This method sets {@code startMoodTime} and {@code shuggleCounter} to {@code 0}.
	 */
	public void clearMoodStatistics()
	{
		startMoodTime = 0;
		shuggleCounter = 0;
	}
	
	/**
	 * Sets the starting time for the mood calculation.<br>
	 * This value is used to track when a pet's mood state began.
	 * @param startMoodTime The timestamp to set for the mood start.
	 */
	public void setStartMoodTime(long startMoodTime)
	{
		this.startMoodTime = startMoodTime;
	}
	
	/**
	 * Retrieves the timestamp when the mood cooldown started.<br>
	 * This value is used to track the duration of the cooldown period.
	 * @return The {@code long} value representing the start time of the mood cooldown.
	 */
	public long getMoodCdStarted()
	{
		return moodCdStarted;
	}
	
	/**
	 * Sets the start time for the mood cooldown.<br>
	 * This value is used to track when a cooldown period began.
	 * @param moodCdStarted The timestamp representing the start of the cooldown.
	 */
	public void setMoodCdStarted(long moodCdStarted)
	{
		this.moodCdStarted = moodCdStarted;
	}
	
	/**
	 * Calculates the remaining time for the mood cooldown.<br>
	 * It checks if the cooldown has finished and returns the seconds left.<br>
	 * If the cooldown is over, it resets {@code moodCdStarted} to 0.
	 * @return The number of seconds remaining until the mood cooldown ends.
	 */
	public int getMoodRemainingTime()
	{
		final long stop = moodCdStarted + 600000;
		final long remains = stop - System.currentTimeMillis();
		if (remains <= 0)
		{
			setMoodCdStarted(0);
			return 0;
		}
		
		return (int) (remains / 1000);
	}
	
	/**
	 * Retrieves the timestamp when the gift cooldown started.<br>
	 * This value is used to track the duration of the gift action.
	 * @return The {@code long} value representing the start time of the gift cooldown.
	 */
	public long getGiftCdStarted()
	{
		return giftCdStarted;
	}
	
	/**
	 * Sets the start time for the gift cooldown.<br>
	 * This value is stored in the {@code giftCdStarted} field.
	 * @param giftCdStarted The timestamp when the gift cooldown began.
	 */
	public void setGiftCdStarted(long giftCdStarted)
	{
		this.giftCdStarted = giftCdStarted;
	}
	
	/**
	 * Calculates the remaining time for the gift cooldown.<br>
	 * It checks if the current time has passed the expiration point.<br>
	 * If the time is up, it resets the cooldown to {@code 0}.
	 * @return The remaining time in seconds as an {@code int}.
	 */
	public int getGiftRemainingTime()
	{
		final long stop = giftCdStarted + (3600 * 1000);
		final long remains = stop - System.currentTimeMillis();
		if (remains <= 0)
		{
			setGiftCdStarted(0);
			return 0;
		}
		
		return (int) (remains / 1000);
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
	 * Saves the current mood data for this pet to the database.<br>
	 * This method uses {@link PlayerPetsDAO} to persist the information.
	 */
	public void savePetMoodData()
	{
		DAOManager.getDAO(PlayerPetsDAO.class).savePetMoodData(this);
	}
	
	/**
	 * Retrieves the current feeding progress of the pet.<br>
	 * This method returns the {@code PetFeedProgress} object associated with this pet.
	 * @return the current {@code PetFeedProgress} or {@code null} if no progress exists.
	 */
	public PetFeedProgress getFeedProgress()
	{
		return feedProgress;
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
	 * Retrieves the {@code PetDopingBag} associated with this pet.
	 * @return the current {@code PetDopingBag} object or {@code null} if none exists.
	 */
	public PetDopingBag getDopingBag()
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
	
	/**
	 * Updates the seller status of the pet.<br>
	 * This method sets whether the pet is currently acting as a seller.
	 * @param isSeller The new {@code boolean} value to set for the seller status.
	 */
	public void setIsSeller(boolean isSeller)
	{
		this.isSeller = isSeller;
	}
	
	/**
	 * Checks if the pet is currently acting as a seller.
	 * @return {@code true} if the pet is a seller, otherwise {@code false}.
	 */
	public boolean isSeller()
	{
		return isSeller;
	}
	
	// pet id is not unique for adopt action, this not explicit
	
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
	 * Handles the expiration of a pet for a specific player.<br>
	 * This method sends a system message to the {@link Player}.<br>
	 * It then calls {@code int)} to remove the pet.
	 * @param player The {@link Player} who triggered the expiration.
	 */
	@Override
	public void expireEnd(Player player)
	{
		if (player == null)
		{
			return;
		}
		
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PET_ABANDON_EXPIRE_TIME_COMPLETE(name));
		PetAdoptionService.surrenderPet(player, petId);
	}
	
	/**
	 * Checks if the chair object is allowed to expire at this moment.<br>
	 * This method currently always returns {@code true}.
	 * @return {@code true} if the object can expire now, otherwise {@code false}.
	 */
	@Override
	public boolean canExpireNow()
	{
		return true;
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
}
