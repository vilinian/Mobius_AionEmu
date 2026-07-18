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
 * Defines the different types of cooldown periods for game instances.<br>
 * This enum is used to manage how long a player must wait before re-entering an instance.
 * @author xTz
 */
public enum InstanceCoolTimeType
{
	RELATIVE,
	WEEKLY,
	DAILY;
	
	/**
	 * Checks if the cool time type is relative.<br>
	 * This method returns {@code true} if the type matches {@code RELATIVE}.<br>
	 * It returns {@code false} for all other types.
	 * @return {@code true} if the instance is relative, otherwise {@code false}.
	 */
	public boolean isRelative()
	{
		return equals(InstanceCoolTimeType.RELATIVE);
	}
	
	/**
	 * Checks if the current instance type is weekly.<br>
	 * This method compares the value to {@code WEEKLY}.
	 * @return {@code true} if the type is weekly, otherwise {@code false}.
	 */
	public boolean isWeekly()
	{
		return equals(InstanceCoolTimeType.WEEKLY);
	}
	
	/**
	 * Checks if the current instance type is daily.<br>
	 * This method compares the value to {@code DAILY}.
	 * @return {@code true} if the type is daily, otherwise {@code false}.
	 */
	public boolean isDaily()
	{
		return equals(InstanceCoolTimeType.DAILY);
	}
}
