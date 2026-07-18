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
package com.aionemu.gameserver.skillengine.model;

import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.services.MotionLoggingService;

/**
 * This class acts as a wrapper for the {@link WeaponType} model.<br>
 * It provides additional functionality and allows for comparison between different weapon types.
 * @author kecimis
 */
public class WeaponTypeWrapper implements Comparable<WeaponTypeWrapper>
{
	private WeaponType mainHand = null;
	private WeaponType offHand = null;
	
	/**
	 * Creates a new {@link WeaponTypeWrapper} instance.<br>
	 * This constructor sets the primary and secondary weapon types.<br>
	 * It handles specific logic for dual-wielding certain one-handed weapons.
	 * @param mainHand The type of the weapon held in the main hand.
	 * @param offHand The type of the weapon held in the off hand.
	 */
	public WeaponTypeWrapper(WeaponType mainHand, WeaponType offHand)
	{
		if ((mainHand != null) && (offHand != null))
		{
			switch (mainHand)
			{
				case DAGGER_1H:
					this.mainHand = WeaponType.DAGGER_1H;
					this.offHand = WeaponType.DAGGER_1H;
					break;
				case SWORD_1H:
					this.mainHand = WeaponType.SWORD_1H;
					this.offHand = WeaponType.SWORD_1H;
					break;
				case TOOLHOE_1H:
					this.mainHand = WeaponType.TOOLHOE_1H;
					this.offHand = WeaponType.TOOLHOE_1H;
				case GUN_1H:
					this.mainHand = WeaponType.GUN_1H;
					this.offHand = WeaponType.GUN_1H;
					break;
				default:
					this.mainHand = mainHand;
					this.offHand = null;
			}
		}
		else
		{
			this.mainHand = mainHand;
			this.offHand = offHand;
		}
	}
	
	/**
	 * Compares this object with another object for equality.<br>
	 * It checks if both objects are of type {@code WeaponTypeWrapper}.<br>
	 * Two wrappers are equal if they have the same outer type, main hand, and off hand.
	 * @param obj The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		
		if ((obj == null) || (getClass() != obj.getClass()))
		{
			return false;
		}
		
		final WeaponTypeWrapper other = (WeaponTypeWrapper) obj;
		if (!getOuterType().equals(other.getOuterType()) || (mainHand != other.mainHand) || (offHand != other.offHand))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns a string representation of the weapon types.<br>
	 * It shows both the {@code mainHand} and {@code offHand} values.<br>
	 * If a value is missing, it displays {@code null}.
	 * @return A formatted string containing the weapon type information.
	 */
	@Override
	public String toString()
	{
		return "mainHandType=\"" + (mainHand != null ? mainHand.toString() : "null") + "\"" + " offHandType=\"" + (offHand != null ? offHand.toString() : "null");
	}
	
	/**
	 * Returns a hash code value for this {@link WeaponTypeWrapper} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code mainHand}, {@code offHand}, and outer type fields.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + getOuterType().hashCode();
		result = (prime * result) + ((mainHand == null) ? 0 : mainHand.hashCode());
		result = (prime * result) + ((offHand == null) ? 0 : offHand.hashCode());
		return result;
	}
	
	/**
	 * Compares this {@code WeaponTypeWrapper} with another one.<br>
	 * It determines the order based on the presence of off-hands and then by main-hand names.
	 * @param o The other {@code WeaponTypeWrapper} to compare against.
	 * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(WeaponTypeWrapper o)
	{
		if ((mainHand == null) || (o.getMainHand() == null))
		{
			return 0;
		}
		else if ((offHand != null) && (o.getOffHand() != null))
		{
			return 0;
		}
		else if ((offHand != null) && (o.getOffHand() == null))
		{
			return 1;
		}
		else if ((offHand == null) && (o.getOffHand() != null))
		{
			return -1;
		}
		else
		{
			return mainHand.toString().compareTo(o.getMainHand().toString());
		}
	}
	
	/**
	 * Retrieves the weapon type used in the main hand.<br>
	 * This is useful for checking the primary equipment of a character.
	 * @return the {@code WeaponType} of the main hand.
	 */
	public WeaponType getMainHand()
	{
		return mainHand;
	}
	
	/**
	 * Retrieves the weapon type used in the off-hand slot.<br>
	 * This method returns the {@code WeaponType} associated with the character's secondary weapon.
	 * @return The {@code WeaponType} of the off-hand weapon.
	 */
	public WeaponType getOffHand()
	{
		return offHand;
	}
	
	/**
	 * Retrieves the singleton instance of the {@link MotionLoggingService}.<br>
	 * This method provides access to the logging service used by the outer type.
	 * @return the current {@code MotionLoggingService} instance.
	 */
	private MotionLoggingService getOuterType()
	{
		return MotionLoggingService.getInstance();
	}
}
