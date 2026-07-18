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
package com.aionemu.gameserver.services.toypet;

/**
 * This class manages the progress data for feeding a {@code ToyPet}.<br>
 * It tracks how much food has been consumed and updates the pet's growth status accordingly.
 * @author Rolandas
 */
public final class PetFeedProgress
{
	private int totalPoints = 0;
	private short regularConsumed = 0;
	private short lovedConsumed = 0;
	private PetHungryLevel hungryLevel = PetHungryLevel.HUNGRY;
	private short lovedFoodMax = 0;
	private boolean lovedFeeded = false;
	
	/**
	 * Creates a new {@code PetFeedProgress} instance.<br>
	 * This constructor initializes the maximum limit for loved food.<br>
	 * It uses a bitwise mask to ensure the value fits within 6 bits.
	 * @param lovedFoodLimit The maximum amount of loved food allowed.
	 */
	public PetFeedProgress(short lovedFoodLimit)
	{
		lovedFoodMax = (short) (lovedFoodLimit & 0x3F);
	}
	
	/**
	 * Retrieves the current total points accumulated by the pet.<br>
	 * This value represents the sum of all progress made so far.
	 * @return The total number of points as an {@code int}.
	 */
	public int getTotalPoints()
	{
		return totalPoints;
	}
	
	/**
	 * Updates the total points for the pet feed progress.<br>
	 * This method applies a bitwise mask to ensure the value stays within range.
	 * @param points The new point value to set.
	 */
	public void setTotalPoints(int points)
	{
		totalPoints = points & 0x3FFF;
	}
	
	/**
	 * Retrieves the current hunger level of the pet.<br>
	 * This value is used to determine how hungry the pet feels.
	 * @return the current {@code PetHungryLevel}
	 */
	public PetHungryLevel getHungryLevel()
	{
		return hungryLevel;
	}
	
	/**
	 * Updates the current hunger status of the pet.<br>
	 * This method sets the {@code hungryLevel} field to a new value.
	 * @param level The new {@link PetHungryLevel} to assign.
	 */
	public void setHungryLevel(PetHungryLevel level)
	{
		hungryLevel = level;
	}
	
	/**
	 * Retrieves the number of regular food items consumed.<br>
	 * This method returns the {@code regularConsumed} value as an {@code int}.
	 * @return The count of regular food items.
	 */
	public int getRegularCount()
	{
		return regularConsumed & 0xFF;
	}
	
	/**
	 * Updates the number of regular items consumed.<br>
	 * This method sets the {@code regularConsumed} field to the provided value.
	 * @param count The new amount of regular items to set.
	 */
	public void setRegularCount(short count)
	{
		regularConsumed = count;
	}
	
	/**
	 * Calculates the amount of favorite food left.<br>
	 * It subtracts {@code lovedConsumed} from {@code lovedFoodMax}.
	 * @return The number of remaining favorite food items.
	 */
	public int getLovedFoodRemaining()
	{
		return lovedFoodMax - lovedConsumed;
	}
	
	/**
	 * Checks if the pet has been fed with special food.<br>
	 * Returns {@code true} if it has, otherwise returns {@code false}.
	 * @return The current status of the loved feed.
	 */
	public boolean isLovedFeeded()
	{
		return lovedFeeded;
	}
	
	/**
	 * Updates the {@code lovedFeeded} status to {@code true}.<br>
	 * This method marks that the pet has been fed with loved food.
	 */
	public void setIsLovedFeeded()
	{
		lovedFeeded = true;
	}
	
	/**
	 * Updates the count of food consumed by the pet.<br>
	 * It checks if the food is a special item or regular food.<br>
	 * The method increments either {@code lovedConsumed} or {@code regularConsumed}.
	 * @param lovedFood Set to {@code true} if the food is special, otherwise {@code false}.
	 */
	public void incrementCount(boolean lovedFood)
	{
		if (lovedFood)
		{
			lovedConsumed++;
		}
		else
		{
			regularConsumed++;
		}
	}
	
	/**
	 * Resets the progress values to their default states.<br>
	 * This method clears {@code totalPoints} and {@code regularConsumed} if no loved food has been fed.<br>
	 * It also ensures that {@code lovedFeeded} is set to {@code false}.
	 */
	public void reset()
	{
		if (lovedFeeded)
		{
			lovedFeeded = false;
		}
		else
		{
			totalPoints = 0;
			regularConsumed = 0;
		}
	}
	
	/**
	 * This method retrieves the packed data for a network packet.<br>
	 * It combines multiple fields into a single {@code int}.<br>
	 * Use this to send pet progress information to the client.
	 * @return The encoded integer value containing all relevant pet feed data.
	 */
	public int getDataForPacket()
	{
		int value = getRegularCount() & 0xFF;
		value <<= 14;
		value |= totalPoints >> 2;
		value <<= 6;
		value |= lovedConsumed & 0x3F;
		value <<= 4; // unk
		return value;
	}
	
	/**
	 * Updates the internal state using a packed integer.<br>
	 * This method extracts multiple values from a single {@code int}.<br>
	 * It updates fields like {@code lovedConsumed}, {@code totalPoints}, and {@code regularConsumed}.
	 * @param savedData The packed integer containing all progress data.
	 */
	public void setData(int savedData)
	{
		savedData >>= 4; // drop unk
		lovedConsumed = (short) (savedData & 0x3F);
		savedData >>= 6;
		totalPoints = (savedData & 0x3FFF) << 2;
		savedData >>= 14;
		regularConsumed = (short) (savedData & 0xFF);
	}
}
