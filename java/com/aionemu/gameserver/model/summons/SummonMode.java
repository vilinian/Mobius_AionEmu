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
package com.aionemu.gameserver.model.summons;

/**
 * Defines the different modes available for summons in the game.<br>
 * This enum is used to determine how a summon behaves or interacts with the world.
 * @author xTz
 */
public enum SummonMode
{
	ATTACK(0),
	GUARD(1),
	REST(2),
	RELEASE(3),
	UNK(5);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link SummonMode}.<br>
	 * This constructor assigns the internal ID to the enum constant.
	 * @param id The unique integer identifier for this mode.
	 */
	private SummonMode(int id)
	{
		this.id = id;
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
	 * Retrieves a {@link SummonMode} based on its unique identifier.<br>
	 * This method searches through all available modes to find a match.
	 * @param id The integer ID of the summon mode to look for.
	 * @return The matching {@code SummonMode} object, or {@code null} if no match is found.
	 */
	public static SummonMode getSummonModeById(int id)
	{
		for (SummonMode mode : values())
		{
			if (mode.getId() == id)
			{
				return mode;
			}
		}
		
		return null;
	}
}
