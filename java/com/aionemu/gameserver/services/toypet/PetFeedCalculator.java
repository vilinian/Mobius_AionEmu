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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.pet.PetFeedResult;
import com.aionemu.gameserver.model.templates.pet.PetFlavour;
import com.aionemu.gameserver.model.templates.pet.PetRewards;

/**
 * This class handles the calculation of experience points gained from feeding toy pets.<br>
 * It determines the correct point values based on the pet's level and the quantity of feed items used.
 * @author Rolandas
 */
public final class PetFeedCalculator
{
	static byte ITEM_MAX_LEVEL = 60;
	static final short[] fullCounts;
	static final byte[] itemLevels;
	static final int[][] pointValues;
	
	static
	{
		final TreeSet<Short> counts = new TreeSet<>();
		for (PetFlavour flavour : DataManager.PET_FEED_DATA.getPetFlavours())
		{
			if (flavour.getFullCount() > 0)
			{
				counts.add((short) (flavour.getFullCount() & 0xFFFF));
			}
		}
		
		fullCounts = new short[counts.size()];
		int i = 0;
		final Iterator<Short> countIter = counts.iterator();
		while (countIter.hasNext())
		{
			fullCounts[i++] = countIter.next();
		}
		
		itemLevels = new byte[ITEM_MAX_LEVEL / 5];
		itemLevels[0] = 5;
		for (int j = 1; j < itemLevels.length; j++)
		{
			itemLevels[j] = (byte) (itemLevels[j - 1] + 5);
		}
		
		pointValues = new int[itemLevels.length][fullCounts.length];
		calculate();
	}
	
	/**
	 * This method pre-calculates the pet feed points for various levels and counts.<br>
	 * It populates the {@code pointValues} array based on item levels and full counts.<br>
	 * The logic ensures compatibility with retail packet requirements.
	 */
	static void calculate()
	{
		for (byte levelByte : itemLevels)
		{
			final short level = (short) (levelByte & 0xFF);
			if (level < 10)
			{
				continue;
			}
			
			int countIndex = 0;
			for (short countByte : fullCounts)
			{
				final short count = (short) (countByte & 0xFF);
				int finalLevel = level;
				if ((finalLevel % 5) == 0)
				{
					finalLevel--;
				}
				
				final int pointLevel = itemLevels[(finalLevel / 5)];
				final int feedPoints = (Math.max(0, pointLevel - 5) / 5) * 8;
				// System.out.println("ITEM LEVEL: " + level + ", COUNT: " + count + ", STEP: " + feedPoints);
				pointValues[finalLevel / 5][countIndex++] = getPoints(feedPoints, count);
			}
		}
	}
	
	/**
	 * Calculates the total points gained from feeding a pet.<br>
	 * This method simulates point accumulation based on feed limits and state transitions.
	 * @param feedPoints The number of points provided by a single feed item.
	 * @param maxFeedCount The maximum number of items that can be fed in one session.
	 * @return The total calculated points as an {@code int}.
	 */
	static int getPoints(int feedPoints, int maxFeedCount)
	{
		int points = 0;
		int state = 0;
		int consumed = 0;
		while (consumed < maxFeedCount)
		{
			boolean needSwitch = false;
			final int oldPoints = points;
			if (((state == 0) && (consumed > (maxFeedCount * 0.5f))) || ((state == 1) && (consumed > (maxFeedCount * 0.8f))) || ((state == 2) && (consumed > (maxFeedCount * 1.05))))
			{
				needSwitch = true;
			}
			
			points += feedPoints;
			if (needSwitch)
			{
				state++;
				if (((state == 1) && (consumed <= (0.487f * maxFeedCount))) || ((state == 2) && (consumed <= (0.78f * maxFeedCount))))
				{
					state--;
					points = oldPoints;
				}
			}
			
			consumed++;
		}
		
		return points;
	}
	
	/**
	 * Updates the progress of a pet's feeding status.<br>
	 * This method calculates points based on the item level and updates the hungry level.<br>
	 * It handles special logic for loved food and checks if a hunger level transition is required.
	 * @param progress The {@code PetFeedProgress} object to update.
	 * @param itemLevel The level of the feed item used.
	 * @param maxFeedCount The maximum number of feeds allowed for the current stage.
	 */
	public static void updatePetFeedProgress(PetFeedProgress progress, int itemLevel, int maxFeedCount)
	{
		final PetHungryLevel currHungryLevel = progress.getHungryLevel();
		if (progress.isLovedFeeded())
		{
			// loved food
			if (progress.getLovedFoodRemaining() == 0)
			{
				return;
			}
			
			progress.setHungryLevel(PetHungryLevel.FULL);
			progress.incrementCount(true);
			return;
		}
		
		final int oldPoints = progress.getTotalPoints();
		boolean needSwitch = false;
		
		if (((currHungryLevel == PetHungryLevel.HUNGRY) && (progress.getRegularCount() > (maxFeedCount * 0.5f))) || ((currHungryLevel == PetHungryLevel.CONTENT) && (progress.getRegularCount() > (maxFeedCount * 0.8f))) || ((currHungryLevel == PetHungryLevel.SEMIFULL) && (progress.getRegularCount() > (maxFeedCount * 1.05))))
		{
			// forcefully switch level
			needSwitch = true;
		}
		else
		{
			int finalLevel = itemLevel;
			if ((finalLevel % 5) == 0)
			{
				finalLevel--;
			}
			
			final byte pointLevel = itemLevels[(finalLevel / 5)];
			final byte pointsEarned = (byte) ((Math.max(0, pointLevel - 5) / 5) * 8);
			final int feedProgress = progress.getTotalPoints() + pointsEarned;
			progress.setTotalPoints(feedProgress);
		}
		
		if (needSwitch)
		{
			// just a prevention to not switch level
			final PetHungryLevel nextLevel = progress.getHungryLevel().getNextValue();
			if (((nextLevel == PetHungryLevel.CONTENT) && (progress.getRegularCount() <= (0.487f * maxFeedCount))) || ((nextLevel == PetHungryLevel.SEMIFULL) && (progress.getRegularCount() <= (0.78f * maxFeedCount))))
			{
				progress.setTotalPoints(oldPoints);
			}
			else
			{
				progress.setHungryLevel(nextLevel);
			}
		}
		
		progress.incrementCount(false);
	}
	
	/**
	 * Calculates the reward for a pet based on feeding progress.<br>
	 * This method checks if the pet is full and determines which {@link PetFeedResult} to give.<br>
	 * It considers the player level and total points accumulated.
	 * @param fullCount The number of full feeds completed.
	 * @param rewardGroup The group of possible rewards for this progress step.
	 * @param progress The current feeding progress of the pet.
	 * @param playerLevel The current level of the player.
	 * @return The resulting {@link PetFeedResult} or {@code null} if no reward is available.
	 */
	public static PetFeedResult getReward(int fullCount, PetRewards rewardGroup, PetFeedProgress progress, int playerLevel)
	{
		if ((progress.getHungryLevel() != PetHungryLevel.FULL) || (rewardGroup.getResults().size() == 0))
		{
			return null;
		}
		
		final int pointsIndex = indexOf(fullCounts, (short) fullCount);
		if (pointsIndex == -1)
		{
			return null;
		}
		
		if (progress.isLovedFeeded())
		{
			// for cash feed
			if (rewardGroup.getResults().size() == 1)
			{
				return rewardGroup.getResults().get(0);
			}
			
			final List<PetFeedResult> validRewards = new ArrayList<>();
			int maxLevel = 0;
			for (PetFeedResult result : rewardGroup.getResults())
			{
				final int resultLevel = DataManager.ITEM_DATA.getItemTemplate(result.getItem()).getLevel();
				if (resultLevel > playerLevel)
				{
					continue;
				}
				
				if (resultLevel > maxLevel)
				{
					maxLevel = resultLevel;
					validRewards.clear();
				}
				
				validRewards.add(result);
			}
			
			if (validRewards.size() == 0)
			{
				return null;
			}
			
			if (validRewards.size() == 1)
			{
				return validRewards.get(0);
			}
			
			return validRewards.get(Rnd.get(validRewards.size()));
		}
		
		int rewardIndex = 0;
		final int totalRewards = rewardGroup.getResults().size();
		for (int row = 1; row < pointValues.length; row++)
		{
			final int[] points = pointValues[row];
			if (points[pointsIndex] <= progress.getTotalPoints())
			{
				rewardIndex = Math.round(((float) totalRewards / (pointValues.length - 1)) * row) - 1;
			}
		}
		
		// Fix rounding discrepancy
		if (rewardIndex < 0)
		{
			rewardIndex = 0;
		}
		else if (rewardIndex > (rewardGroup.getResults().size() - 1))
		{
			rewardIndex = rewardGroup.getResults().size() - 1;
		}
		
		return rewardGroup.getResults().get(rewardIndex);
	}
	
	/**
	 * Returns the index of the first occurrence of a value in a {@code short} array.
	 * @param array the array to search
	 * @param value the value to find
	 * @return the zero-based index of the first match, or {@code -1} if not present
	 */
	private static int indexOf(short[] array, short value)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] == value)
			{
				return i;
			}
		}
		
		return -1;
	}
}
