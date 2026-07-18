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
package com.aionemu.gameserver.model;

/**
 * Represents a unique identifier for game descriptions.<br>
 * This class is used to map specific IDs to their corresponding descriptive content within the {@code model} package.
 * @author MrPoke
 */
public final class DescriptionId
{
	private int value;
	
	/**
	 * Creates a new {@link DescriptionId} instance.<br>
	 * Sets the internal ID to the provided {@code value}.
	 * @param value The integer ID to assign to this object.
	 */
	public DescriptionId(int value)
	{
		this.value = value;
	}
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	public int getValue()
	{
		return value;
	}
	
	/**
	 * Updates the internal value of this {@link DescriptionId}.<br>
	 * This method replaces the current integer with a new one.
	 * @param val The new integer value to set.
	 */
	public void setValue(int val)
	{
		value = val;
	}
}
