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
package com.aionemu.gameserver.model.templates.pet;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.services.toypet.PetFeedCalculator;
import com.aionemu.gameserver.services.toypet.PetFeedProgress;
import com.aionemu.gameserver.services.toypet.PetHungryLevel;

/**
 * Represents the flavor characteristics of a pet in the game.<br>
 * This class defines how specific pets behave or appear based on their unique traits.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetFlavour", propOrder =
{
	"food"
})
public class PetFlavour
{
	@XmlElement(required = true)
	protected List<PetRewards> food;
	@XmlAttribute(required = true)
	protected int id;
	@XmlAttribute(name = "full_count")
	protected int fullCount = 1;
	@XmlAttribute(name = "loved_limit")
	protected int lovedFoodLimit = 0;
	@XmlAttribute(name = "cd", required = true)
	protected int cooldown = 0;
	
	/**
	 * Retrieves the list of rewards associated with pet food.<br>
	 * If the internal list is {@code null}, it returns a new empty {@code ArrayList}.
	 * @return A {@code List} of {@link PetRewards} objects.
	 */
	public List<PetRewards> getFood()
	{
		if (food == null)
		{
			food = new ArrayList<>();
		}
		
		return food;
	}
	
	/**
	 * Retrieves the {@code FoodType} for a specific item ID.<br>
	 * It checks if the provided {@code itemId} matches any food in the rewards list.<br>
	 * This method uses {@code ITEM_GROUPS_DATA} to validate the type.
	 * @param itemId The unique identifier of the item to check.
	 * @return The corresponding {@code FoodType}, or {@code null} if no match is found.
	 */
	public FoodType getFoodType(int itemId)
	{
		for (PetRewards rewards : getFood())
		{
			if (DataManager.ITEM_GROUPS_DATA.isFood(itemId, rewards.getType()))
			{
				return rewards.getType();
			}
		}
		
		return null;
	}
	
	/**
	 * Calculates the result of feeding a pet based on the provided progress and levels.<br>
	 * This method updates the {@link PetFeedProgress} and determines if rewards are granted.
	 * @param progress The current progress state of the pet's hunger.
	 * @param foodType The type of food being used for feeding.
	 * @param itemLevel The level of the food item.
	 * @param playerLevel The level of the player performing the action.
	 * @return A {@code PetFeedResult} object if successful, or {@code null} otherwise.
	 */
	public PetFeedResult processFeedResult(PetFeedProgress progress, FoodType foodType, int itemLevel, int playerLevel)
	{
		PetRewards rewardGroup = null;
		for (PetRewards rewards : getFood())
		{
			if (rewards.getType() == foodType)
			{
				rewardGroup = rewards;
				break;
			}
		}
		
		if (rewardGroup == null)
		{
			return null;
		}
		
		int maxFeedCount = 1;
		if (rewardGroup.isLoved())
		{
			progress.setIsLovedFeeded();
		}
		else
		{
			maxFeedCount = fullCount;
		}
		
		PetFeedCalculator.updatePetFeedProgress(progress, itemLevel, maxFeedCount);
		if (progress.getHungryLevel() != PetHungryLevel.FULL)
		{
			return null;
		}
		
		return PetFeedCalculator.getReward(maxFeedCount, rewardGroup, progress, playerLevel);
	}
	
	/**
	 * Checks if a specific food item is considered a favorite.<br>
	 * This method looks up the {@code FoodType} in the list of rewards.<br>
	 * It returns {@code true} if the reward group is marked as loved.
	 * @param foodType The type of food to check.
	 * @param itemId The unique identifier for the food item.
	 * @return {@code true} if the food is a favorite, otherwise {@code false}.
	 */
	public boolean isLovedFood(FoodType foodType, int itemId)
	{
		PetRewards rewardGroup = null;
		for (PetRewards rewards : getFood())
		{
			if (rewards.getType() == foodType)
			{
				rewardGroup = rewards;
				break;
			}
		}
		
		if (rewardGroup == null)
		{
			return false;
		}
		
		return rewardGroup.isLoved();
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the total count for this pet flavor.<br>
	 * This value is stored in the {@code fullCount} field.
	 * @return The current {@code int} value of the full count.
	 */
	public int getFullCount()
	{
		return fullCount;
	}
	
	/**
	 * Retrieves the maximum limit for loved food.<br>
	 * This value is used to determine how many times a pet can be fed its favorite food.
	 * @return The {@code int} value of the {@code lovedFoodLimit}.
	 */
	public int getLovedFoodLimit()
	{
		return lovedFoodLimit;
	}
	
	/**
	 * Retrieves the current cooldown value.<br>
	 * This value is used to determine the wait time for pet actions.
	 * @return The {@code int} value of the cooldown.
	 */
	public int getCooldDown()
	{
		return cooldown;
	}
}
