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
package com.aionemu.gameserver.questEngine.model;

/**
 * This class represents the variables associated with a quest.<br>
 * It stores and manages specific data points required for quest progression.<br>
 * Use this model to track player progress within the {@link com.aionemu.gameserver.questEngine.QuestEngine}.
 * @author MrPoke
 */
public class QuestVars
{
	private final Integer[] questVars = new Integer[6];
	
	/**
	 * Creates a new instance of the {@link QuestVars} class.<br>
	 * This initializes the internal quest variables to their default values.
	 */
	public QuestVars()
	{
	}
	
	/**
	 * Creates a new {@link QuestVars} instance.<br>
	 * This constructor initializes the object and sets an initial value.
	 * @param var The integer value to set for the quest variable.
	 */
	public QuestVars(int var)
	{
		setVar(var);
	}
	
	/**
	 * Retrieves the value of a specific variable.<br>
	 * This method uses the provided {@code id} to find the correct entry in the array.
	 * @param id The unique identifier for the variable.
	 * @return The integer value associated with the given {@code id}.
	 */
	public int getVarById(int id)
	{
		return questVars[id];
	}
	
	/**
	 * Updates a specific variable in the quest system.<br>
	 * This method uses the {@code id} to locate the correct position.<br>
	 * It then assigns the new value provided by {@code var}.
	 * @param id The unique index of the variable to update.
	 * @param var The new integer value to store.
	 */
	public void setVarById(int id, int var)
	{
		questVars[id] = var;
	}
	
	/**
	 * Retrieves all quest variables as a single integer.<br>
	 * This method combines the values from the {@code questVars} array.<br>
	 * It processes the elements in reverse order to build the final result.
	 * @return The combined integer value of all quest variables.
	 */
	public int getQuestVars()
	{
		int var = 0;
		for (int i = 5; i >= 0; i--)
		{
			var <<= 0x06;
			var |= questVars[i];
		}
		
		return var;
	}
	
	/**
	 * Updates the internal quest variables using a bitmask.<br>
	 * This method extracts values from the {@code var} parameter.<br>
	 * It populates all available slots in the {@link QuestVars} array.
	 * @param var The integer value containing the data to be stored.
	 */
	public void setVar(int var)
	{
		for (int i = 0; i <= 5; i++)
		{
			questVars[i] = var & 0x3F;
			var >>= 0x06;
		}
	}
}
