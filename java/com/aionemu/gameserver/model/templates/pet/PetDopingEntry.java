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
package com.aionemu.gameserver.model.templates.pet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.flypath.FlyPathEntry;

/**
 * Represents a data entry for pet doping configurations.<br>
 * This class maps the {@code dope} XML template to an object used by the game server.<br>
 * It defines the properties required to manage pet enhancement effects.
 * @author Rolandas
 */
@XmlType(name = "dope")
@XmlAccessorType(XmlAccessType.NONE)
public class PetDopingEntry
{
	@XmlAttribute(name = "id", required = true)
	private short id;
	@XmlAttribute(name = "usedrink", required = true)
	private boolean usedrink;
	@XmlAttribute(name = "usefood", required = true)
	private boolean usefood;
	@XmlAttribute(name = "usescroll", required = true)
	private int usescroll;
	
	/**
	 * Retrieves the unique identifier for this {@link FlyPathEntry}.<br>
	 * This value is used to distinguish between different paths.
	 * @return The {@code short} ID of the entry.
	 */
	public short getId()
	{
		return id;
	}
	
	/**
	 * Checks if the drink has been used.<br>
	 * This method returns the value of the {@code usedrink} attribute.
	 * @return {@code true} if the drink was used, {@code false} otherwise.
	 */
	public boolean isUseDrink()
	{
		return usedrink;
	}
	
	/**
	 * Checks if the pet uses food.<br>
	 * This method returns the value of the {@code usefood} attribute.
	 * @return {@code true} if food is used, {@code false} otherwise.
	 */
	public boolean isUseFood()
	{
		return usefood;
	}
	
	/**
	 * Gets the total number of scrolls used.<br>
	 * This value corresponds to the {@code usescroll} attribute.
	 * @return the count of scrolls used as an {@code int}.
	 */
	public int getScrollsUsed()
	{
		return usescroll;
	}
}
