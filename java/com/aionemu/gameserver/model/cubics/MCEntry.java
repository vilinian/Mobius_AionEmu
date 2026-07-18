/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Aion-Lightning is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. * You should have received a copy of the GNU General Public License
 * along with Aion-Lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.cubics;

/**
 * Represents a single entry within the {@code MCEntry} data structure.<br>
 * This class is used to store and manage specific cubic model information.
 * @author Phantom_KNA
 */
public class MCEntry
{
	private final int cubeid;
	private final int rank;
	private final int level;
	private final int stat_value;
	private final int category;
	
	/**
	 * Creates a new instance of {@link MCEntry}.<br>
	 * This constructor initializes all required fields for a cube entry.
	 * @param cubeid The unique identifier for the cube.
	 * @param rank The rank level of the cube.
	 * @param level The current level of the cube.
	 * @param stat_value The numerical value of the statistic.
	 * @param category The classification category of the entry.
	 */
	public MCEntry(int cubeid, int rank, int level, int stat_value, int category)
	{
		this.cubeid = cubeid;
		this.rank = rank;
		this.level = level;
		this.stat_value = stat_value;
		this.category = category;
	}
	
	/**
	 * Retrieves the unique identifier for the cube.<br>
	 * This value is stored in the {@code cubeid} field.
	 * @return The {@code int} ID of the cube.
	 */
	public int getCubeId()
	{
		return cubeid;
	}
	
	/**
	 * Retrieves the current rank of the {@code MCEntry}.<br>
	 * This value is used to determine the item's standing.
	 * @return The integer value representing the rank.
	 */
	public int getRank()
	{
		return rank;
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
	
	/**
	 * Retrieves the current value of the statistic.<br>
	 * This method returns the {@code stat_value} field from this {@link MCEntry} instance.
	 * @return The integer value of the statistic.
	 */
	public int getStatValue()
	{
		return stat_value;
	}
	
	/**
	 * Retrieves the category of the {@link MCEntry}.<br>
	 * This value identifies which group the entry belongs to.
	 * @return The integer value representing the category.
	 */
	public int getCategory()
	{
		return category;
	}
}
