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
package com.aionemu.gameserver.model.instance;

/**
 * Defines the different types of game instances available in the server.<br>
 * This enumeration is used to categorize and identify specific {@code Instance} categories.
 * @author Ever'
 */
public enum InstanceType
{
	NORMAL,
	BATTLEFIELD;
	
	/**
	 * Checks if the current instance type is {@code NORMAL}.<br>
	 * This method compares the current value against {@code NORMAL}.
	 * @return {@code true} if it is a normal instance, otherwise {@code false}.
	 */
	public boolean isNormalInstance()
	{
		return equals(InstanceType.NORMAL);
	}
	
	/**
	 * Checks if the current instance type is a battlefield.<br>
	 * This method compares the current value against {@code BATTLEFIELD}.
	 * @return {@code true} if it is a battlefield, otherwise {@code false}.
	 */
	public boolean isBattlefieldInstance()
	{
		return equals(InstanceType.BATTLEFIELD);
	}
}
