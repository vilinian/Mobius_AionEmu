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
package com.aionemu.gameserver.model.craft;

import com.aionemu.gameserver.model.Race;

/**
 * This class defines the list of expert quests available for crafting.<br>
 * It maps specific quest types to their respective requirements and rewards.
 * @author synchro2
 */
public enum ExpertQuestsList
{
	COOKING_ELYOS(new int[]
	{
		1944,
		1979,
		1978,
		3952,
		3951,
		3950
	}, Race.ELYOS, 40001),
	COOKING_ASMODIANS(new int[]
	{
		2934,
		2979,
		2978,
		4956,
		4955,
		4954
	}, Race.ASMODIANS, 40001),
	WEAPONSMITHING_ELYOS(new int[]
	{
		1941,
		1973,
		1972,
		3943,
		3942,
		3941
	}, Race.ELYOS, 40002),
	WEAPONSMITHING_ASMODIANS(new int[]
	{
		2931,
		2973,
		2972,
		4947,
		4946,
		4945
	}, Race.ASMODIANS, 40002),
	ARMORSMITHING_ELYOS(new int[]
	{
		1942,
		1975,
		1974,
		3946,
		3945,
		3944
	}, Race.ELYOS, 40003),
	ARMORSMITHING_ASMODIANS(new int[]
	{
		2912,
		2975,
		2974,
		4950,
		4949,
		4948
	}, Race.ASMODIANS, 40003),
	TAILORING_ELYOS(new int[]
	{
		1946,
		1983,
		1982,
		3958,
		3957,
		3956
	}, Race.ELYOS, 40004),
	TAILORING_ASMODIANS(new int[]
	{
		2936,
		2983,
		2982,
		4962,
		4961,
		4960
	}, Race.ASMODIANS, 40004),
	ALCHEMY_ELYOS(new int[]
	{
		1945,
		1981,
		1980,
		3955,
		3954,
		3953
	}, Race.ELYOS, 40007),
	ALCHEMY_ASMODIANS(new int[]
	{
		2935,
		2981,
		2980,
		4959,
		4958,
		4957
	}, Race.ASMODIANS, 40007),
	HANDICRAFTING_ELYOS(new int[]
	{
		1943,
		1977,
		1976,
		3949,
		3948,
		3947
	}, Race.ELYOS, 40008),
	HANDICRAFTING_ASMODIANS(new int[]
	{
		2933,
		2977,
		2976,
		4953,
		4952,
		4951
	}, Race.ASMODIANS, 40008),
	MENUSIER_ELYOS(new int[]
	{
		19050,
		19053,
		1052,
		19056,
		19055,
		19054
	}, Race.ELYOS, 40010),
	MENUSIER_ASMODIANS(new int[]
	{
		29050,
		29053,
		29052,
		29056,
		29055,
		29054
	}, Race.ASMODIANS, 40010);
	
	private final int[] skillsIds;
	private final Race race;
	private final int craftSkillId;
	
	/**
	 * Creates a new instance of {@link ExpertQuestsList}.<br>
	 * This constructor initializes the quest data for a specific race and skill.
	 * @param skillsIds An array of {@code int} IDs representing the required skills.
	 * @param race The {@link Race} type associated with these quests.
	 * @param craftSkillId The unique {@code int} identifier for the crafting skill.
	 */
	private ExpertQuestsList(int[] skillsIds, Race race, int craftSkillId)
	{
		this.skillsIds = skillsIds;
		this.race = race;
		this.craftSkillId = craftSkillId;
	}
	
	/**
	 * Retrieves the {@code Race} associated with this quest list.<br>
	 * This method returns the internal {@code race} field.
	 * @return The {@link Race} of the current quest entry.
	 */
	private Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the unique identifier for the crafting skill.<br>
	 * This value is used to identify specific skills like {@code COOKING_ELYOS}.
	 * @return The {@code int} ID of the craft skill.
	 */
	private int getCraftSkillId()
	{
		return craftSkillId;
	}
	
	/**
	 * Retrieves the list of skill IDs for a specific craft and race.<br>
	 * This method searches through all {@link ExpertQuestsList} entries.<br>
	 * It returns an array of integers if a match is found.
	 * @param craftSkillId The unique identifier for the crafting skill.
	 * @param race The {@code Race} type to filter by.
	 * @return An array of skill IDs associated with the given parameters.
	 */
	public static int[] getSkillsIds(int craftSkillId, Race race)
	{
		for (ExpertQuestsList eql : values())
		{
			if (race.equals(eql.getRace()) && (craftSkillId == eql.getCraftSkillId()))
			{
				return eql.skillsIds;
			}
		}
		
		throw new IllegalArgumentException("Invalid craftSkillId: " + craftSkillId + " or race: " + race);
	}
}
