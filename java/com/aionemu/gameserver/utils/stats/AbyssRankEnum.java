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
package com.aionemu.gameserver.utils.stats;

import javax.xml.bind.annotation.XmlEnum;

import com.aionemu.gameserver.configs.main.RateConfig;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Defines the possible ranks available for players in the Abyss.<br>
 * This enumeration is used to categorize and manage player progression levels within this specific game mode.
 * @author ATracer
 * @author Sarynth
 * @author Imaginary
 * @rework Ever' for 4.5
 */
@XmlEnum
public enum AbyssRankEnum
{
	// Abyss Points
	GRADE9_SOLDIER(1, 300, 90, 0, 0, 0, 0, 0, 1802431),
	GRADE8_SOLDIER(2, 414, 103, 1200, 0, 0, 0, 0, 1802433),
	GRADE7_SOLDIER(3, 475, 118, 4220, 0, 0, 0, 0, 1802435),
	GRADE6_SOLDIER(4, 546, 136, 10990, 0, 0, 0, 0, 1802437),
	GRADE5_SOLDIER(5, 627, 156, 23500, 0, 0, 0, 0, 1802439),
	GRADE4_SOLDIER(6, 721, 180, 42780, 0, 0, 0, 0, 1802441),
	GRADE3_SOLDIER(7, 865, 216, 69700, 0, 0, 0, 0, 1802443),
	GRADE2_SOLDIER(8, 1038, 259, 105600, 0, 0, 0, 0, 1802445),
	GRADE1_SOLDIER(9, 1245, 311, 150800, 0, 0, 0, 0, 1802447),
	// Glory Points
	STAR1_OFFICER(10, 1868, 467, 0, 1244, 1000, 7, 49, 1802449),
	STAR2_OFFICER(11, 2241, 560, 0, 1368, 700, 14, 98, 1802451),
	STAR3_OFFICER(12, 2577, 644, 0, 1915, 500, 28, 196, 1802453),
	STAR4_OFFICER(13, 2964, 741, 0, 3064, 300, 49, 343, 1802455),
	STAR5_OFFICER(14, 4446, 1511, 0, 5210, 100, 107, 749, 1802457),
	GENERAL(15, 4890, 1662, 0, 8335, 30, 119, 833, 1802459),
	GREAT_GENERAL(16, 5378, 1828, 0, 10002, 10, 122, 854, 1802461),
	COMMANDER(17, 5916, 2011, 0, 11503, 3, 127, 889, 1802463),
	SUPREME_COMMANDER(18, 7099, 2413, 0, 12437, 1, 147, 1029, 1802465);
	
	private final int id;
	private final int pointsGained;
	private final int pointsLost;
	private final int requiredAp;
	private final int requiredGp;
	private final int quota;
	private final int dailyReduceGp;
	private final int weeklyReduceGp;
	private final int descriptionId;
	
	/**
	 * Initializes a new instance of an {@link AbyssRankEnum}.<br>
	 * This constructor sets the rank properties and applies the global rate configuration.
	 * @param id The unique identifier for the rank.
	 * @param pointsGained The amount of points gained per action.
	 * @param pointsLost The amount of points lost per action.
	 * @param requiredAp The base Abyss Points required for this rank.
	 * @param requiredGp The base Glory Points required for this rank.
	 * @param quota The specific quota assigned to this rank.
	 * @param dailyReduceGp The amount of Glory Points reduced daily.
	 * @param weeklyReduceGp The amount of Glory Points reduced weekly.
	 * @param descriptionId The {@link DescriptionId} for the rank text.
	 */
	private AbyssRankEnum(int id, int pointsGained, int pointsLost, int requiredAp, int requiredGp, int quota, int dailyReduceGp, int weeklyReduceGp, int descriptionId)
	{
		this.id = id;
		this.pointsGained = pointsGained;
		this.pointsLost = pointsLost;
		this.requiredAp = requiredAp * RateConfig.ABYSS_RANK_RATE;
		this.requiredGp = requiredGp * RateConfig.ABYSS_RANK_RATE;
		this.quota = quota;
		this.dailyReduceGp = dailyReduceGp;
		this.weeklyReduceGp = weeklyReduceGp;
		this.descriptionId = descriptionId;
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
	 * Retrieves the amount of points lost for this rank.<br>
	 * This value is used to calculate penalties or costs associated with the {@code AbyssRankEnum}.
	 * @return the number of points lost as an {@code int}.
	 */
	public int getPointsLost()
	{
		return pointsLost;
	}
	
	/**
	 * Retrieves the amount of points gained for this rank.<br>
	 * This value is used to calculate rewards based on the {@code AbyssRankEnum}.
	 * @return The total number of points gained as an {@code int}.
	 */
	public int getPointsGained()
	{
		return pointsGained;
	}
	
	/**
	 * Retrieves the amount of {@code ap} required for this rank.<br>
	 * If the value is {@code 0}, it returns {@code 1}.
	 * @return the required {@code ap} value.
	 */
	public int getRequiredAp()
	{
		return requiredAp == 0 ? 1 : requiredAp;
	}
	
	/**
	 * Retrieves the Glory Points required for this rank.<br>
	 * If the value is {@code 0}, it returns {@code 1}.
	 * @return The amount of Glory Points needed.
	 */
	public int getRequiredGp()
	{
		return requiredGp == 0 ? 1 : requiredGp;
	}
	
	/**
	 * Retrieves the quota value for this rank.<br>
	 * This value is used to determine specific limits or requirements associated with the {@code AbyssRankEnum}.
	 * @return The integer value of the quota.
	 */
	public int getQuota()
	{
		return quota;
	}
	
	/**
	 * Retrieves the amount of Glory Points reduced daily.<br>
	 * This value is specific to the current {@code AbyssRankEnum}.
	 * @return The daily reduction amount as an {@code int}.
	 */
	public int getDailyReduceGp()
	{
		return dailyReduceGp;
	}
	
	/**
	 * Retrieves the amount of Glory Points reduced on a weekly basis.<br>
	 * This value is specific to the current {@code AbyssRankEnum}.
	 * @return The weekly reduction amount as an {@code int}.
	 */
	public int getWeeklyReduceGp()
	{
		return weeklyReduceGp;
	}
	
	/**
	 * Retrieves the unique identifier for the rank description.<br>
	 * This value is used to fetch the correct text for this rank.
	 * @return the {@code int} representation of the {@link DescriptionId}.
	 */
	public int getDescriptionId()
	{
		return descriptionId;
	}
	
	/**
	 * Retrieves the correct {@link DescriptionId} based on a player's current abyss rank.<br>
	 * This method adjusts the ID depending on whether the {@link Player} belongs to the {@code Race.ELYOS} race.
	 * @param player The {@link Player} object used to determine the rank and race.
	 * @return The calculated {@link DescriptionId} for the player's rank.
	 */
	public static DescriptionId getRankDescriptionId(Player player)
	{
		final int pRankId = player.getAbyssRank().getRank().getId();
		for (AbyssRankEnum rank : values())
		{
			if (rank.getId() == pRankId)
			{
				final int descId = rank.getDescriptionId();
				return (player.getRace() == Race.ELYOS) ? new DescriptionId(descId) : new DescriptionId(descId + 36);
			}
		}
		
		throw new IllegalArgumentException("No rank Description Id found for player: " + player);
	}
	
	/**
	 * Retrieves an {@link AbyssRankEnum} based on its unique identifier.<br>
	 * This method searches through all available ranks to find a match.<br>
	 * It throws an {@code IllegalArgumentException} if the ID is not found.
	 * @param id The unique integer ID of the rank to retrieve.
	 * @return The corresponding {@link AbyssRankEnum} for the given ID.
	 */
	public static AbyssRankEnum getRankById(int id)
	{
		for (AbyssRankEnum rank : values())
		{
			if (rank.getId() == id)
			{
				return rank;
			}
		}
		
		throw new IllegalArgumentException("Invalid abyss rank provided" + id);
	}
	
	/**
	 * Determines the highest achievable rank based on Abyss Points.<br>
	 * This method iterates through all available ranks in {@link AbyssRankEnum}.<br>
	 * It returns the rank where the provided points meet the requirement.
	 * @param ap The amount of Abyss Points to check.
	 * @return The corresponding {@code AbyssRankEnum} for the given points.
	 */
	public static AbyssRankEnum getRankForAp(int ap)
	{
		AbyssRankEnum r = AbyssRankEnum.GRADE9_SOLDIER;
		for (AbyssRankEnum rank : values())
		{
			if (rank.getRequiredAp() <= ap)
			{
				r = rank;
			}
			else
			{
				break;
			}
		}
		
		return r;
	}
	
	/**
	 * Determines the highest achievable rank based on Glory Points.<br>
	 * It iterates through all {@link AbyssRankEnum} values to find a match.<br>
	 * The method returns the rank where the required points are less than or equal to the input.
	 * @param gp The amount of Glory Points owned by the player.
	 * @return The corresponding {@link AbyssRankEnum} for the given points.
	 */
	public static AbyssRankEnum getRankForGp(int gp)
	{
		AbyssRankEnum rgp = null;
		for (AbyssRankEnum rank : values())
		{
			if (rank.getRequiredGp() <= gp)
			{
				rgp = rank;
			}
			else
			{
				break;
			}
		}
		
		return rgp;
	}
	
	/**
	 * Determines the correct {@link AbyssRankEnum} based on both AP and GP values.<br>
	 * It prioritizes the rank associated with the highest GP value.<br>
	 * If no GP rank is found, it checks if the AP rank is a soldier grade.<br>
	 * Otherwise, it defaults to the first available officer rank.
	 * @param ap The current Abyss Points of the player.
	 * @param gp The current Glory Points of the player.
	 * @return The calculated {@link AbyssRankEnum} for the given stats.
	 */
	public static AbyssRankEnum getRank(int ap, int gp)
	{
		final AbyssRankEnum rap = getRankForAp(ap);
		final AbyssRankEnum rgp = getRankForGp(gp);
		if (rgp != null)
		{
			return rgp;
		}
		
		if (rap.getId() <= 9)
		{
			return rap;
		}
		
		return getRankById(9);
	}
}
