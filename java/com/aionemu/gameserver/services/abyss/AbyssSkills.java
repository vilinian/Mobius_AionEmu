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
package com.aionemu.gameserver.services.abyss;

import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

/**
 * This class defines the set of skills available for players in the {@link Race} abyss.<br>
 * It maps specific skill identifiers to their respective mechanics and requirements.
 */
enum AbyssSkills
{
	SUPREME_COMMANDER(Race.ELYOS, AbyssRankEnum.SUPREME_COMMANDER, new int[]
	{
		11889,
		11898,
		11900,
		11903,
		11904,
		11905,
		11906
	}),
	COMMANDER(Race.ELYOS, AbyssRankEnum.COMMANDER, new int[]
	{
		11888,
		11898,
		11900,
		11903,
		11904
	}),
	GREAT_GENERAL(Race.ELYOS, AbyssRankEnum.GREAT_GENERAL, new int[]
	{
		11887,
		11897,
		11899,
		11903
	}),
	GENERAL(Race.ELYOS, AbyssRankEnum.GENERAL, new int[]
	{
		11886,
		11896,
		11899
	}),
	STAR5_OFFICER(Race.ELYOS, AbyssRankEnum.STAR5_OFFICER, new int[]
	{
		11885,
		11895
	}),
	SUPREME_COMMANDER_A(Race.ASMODIANS, AbyssRankEnum.SUPREME_COMMANDER, new int[]
	{
		11894,
		11898,
		11902,
		11903,
		11904,
		11905,
		11906
	}),
	COMMANDER_A(Race.ASMODIANS, AbyssRankEnum.COMMANDER, new int[]
	{
		11893,
		11898,
		11902,
		11903,
		11904
	}),
	GREAT_GENERAL_A(Race.ASMODIANS, AbyssRankEnum.GREAT_GENERAL, new int[]
	{
		11892,
		11897,
		11901,
		11903
	}),
	GENERAL_A(Race.ASMODIANS, AbyssRankEnum.GENERAL, new int[]
	{
		11891,
		11896,
		11901
	}),
	STAR5_OFFICER_A(Race.ASMODIANS, AbyssRankEnum.STAR5_OFFICER, new int[]
	{
		11890,
		11895
	});
	
	private final int[] skills;
	private final AbyssRankEnum rankenum;
	private final Race race;
	
	/**
	 * Creates a new instance of {@code AbyssSkills}.<br>
	 * This constructor initializes the required data for an abyss skill.
	 * @param race The {@link Race} type associated with this skill.
	 * @param rankEnum The {@link AbyssRankEnum} level of the skill.
	 * @param skills An array of integers representing the specific skill IDs.
	 */
	private AbyssSkills(Race race, AbyssRankEnum rankEnum, int[] skills)
	{
		this.race = race;
		rankenum = rankEnum;
		this.skills = skills;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the list of skill IDs associated with this access level.<br>
	 * These values are used to determine specific permissions.
	 * @return an {@code int[]} array containing the skill identifiers.
	 */
	public int[] getSkills()
	{
		return skills;
	}
	
	/**
	 * Retrieves the skill IDs for a specific race and rank.<br>
	 * This method searches through all available {@link AbyssSkills}.<br>
	 * It returns an empty array if no matching skills are found.
	 * @param race The {@code Race} of the character.
	 * @param rank The {@code AbyssRankEnum} level reached.
	 * @return An {@code int[]} containing the skill IDs.
	 */
	public static int[] getSkills(Race race, AbyssRankEnum rank)
	{
		for (AbyssSkills aSkills : values())
		{
			if ((aSkills.race == race) && (aSkills.rankenum == rank))
			{
				return aSkills.skills;
			}
		}
		
		LoggerFactory.getLogger(AbyssSkills.class).warn("No abyss skills for: " + race + " " + rank);
		return new int[0];
	}
}
