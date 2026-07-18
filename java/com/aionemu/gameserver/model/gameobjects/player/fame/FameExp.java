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
package com.aionemu.gameserver.model.gameobjects.player.fame;

/**
 * Represents the different types of experience points used for the {@code Fame} system.<br>
 * This enum helps categorize how players gain progress toward specific fame milestones.
 */
public enum FameExp
{
	
	LEVEL1(1, 128520),
	LEVEL2(2, 257040),
	LEVEL3(3, 385560),
	LEVEL4(4, 514080),
	LEVEL5(5, 642600),
	LEVEL6(6, 742600),
	LEVEL7(7, 842600),
	LEVEL8(8, 942600),
	LEVEL9(9, 1285200);
	
	int level;
	long exp;
	
	/**
	 * Creates a new {@link FameExp} instance.<br>
	 * Sets the internal values for this enum constant.
	 * @param level The integer level of the fame experience.
	 * @param exp The long value representing the required experience.
	 */
	private FameExp(int level, long exp)
	{
		this.level = level;
		this.exp = exp;
	}
	
	/**
	 * Retrieves a {@link FameExp} object based on its level.<br>
	 * This method searches through all available levels.<br>
	 * It throws an exception if the level does not exist.
	 * @param value The level number to look up.
	 * @return The corresponding {@code FameExp} for the given level.
	 */
	public static FameExp getFameExp(int value)
	{
		for (FameExp pc : FameExp.values())
		{
			if (pc.getLevel() != value)
			{
				continue;
			}
			
			return pc;
		}
		
		throw new IllegalArgumentException("There is no fame level with id " + value);
	}
	
	/**
	 * Retrieves the current experience points of the player.<br>
	 * This value represents the total accumulated {@code exp}.
	 * @return The current experience points as a {@code long}.
	 */
	public long getExp()
	{
		return exp;
	}
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
}
