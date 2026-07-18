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
package com.aionemu.gameserver.model.siege;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;

/**
 * Represents the different races available for siege warfare.<br>
 * This enum maps specific {@link Race} types to their corresponding siege mechanics.
 * @author Sarynth
 */
public enum SiegeRace
{
	ELYOS(0, 1800481),
	ASMODIANS(1, 1800483),
	BALAUR(2, 1800485);
	
	private final int raceId;
	private final DescriptionId descriptionId;
	
	/**
	 * Creates a new instance of {@link SiegeRace}.<br>
	 * This constructor initializes the internal IDs.
	 * @param id The unique identifier for the race.
	 * @param descriptionId The numeric ID used to fetch the race description.
	 */
	private SiegeRace(int id, int descriptionId)
	{
		raceId = id;
		this.descriptionId = new DescriptionId(descriptionId);
	}
	
	/**
	 * Retrieves the unique identifier for this {@link Race}.<br>
	 * This value is used to identify different types of races in the game.
	 * @return The integer ID associated with the race.
	 */
	public int getRaceId()
	{
		return raceId;
	}
	
	/**
	 * Retrieves the corresponding {@link SiegeRace} based on a {@code Race}.<br>
	 * This method maps standard races to their specific siege race types.
	 * @param race The {@code Race} type to look up.
	 * @return The matching {@code SiegeRace} constant.
	 */
	public static SiegeRace getByRace(Race race)
	{
		switch (race)
		{
			case ASMODIANS:
				return SiegeRace.ASMODIANS;
			case ELYOS:
				return SiegeRace.ELYOS;
			default:
				return SiegeRace.BALAUR;
		}
	}
	
	/**
	 * Retrieves the unique identifier for the fame description.<br>
	 * This ID is used to fetch the correct text in the game.
	 * @return the {@code DescriptionId} associated with this fame.
	 */
	public DescriptionId getDescriptionId()
	{
		return descriptionId;
	}
}
