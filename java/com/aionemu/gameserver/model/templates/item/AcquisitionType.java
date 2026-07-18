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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the different methods through which an item can be obtained in the game.<br>
 * This enum is used to categorize how items are acquired by players.
 * @author Rolandas
 */
@XmlType(name = "acquisitionType")
@XmlEnum
public enum AcquisitionType
{
	AP(0),
	ABYSS(1),
	REWARD(2), // They are the same now
	COUPON(2);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link AcquisitionType}.<br>
	 * This constructor assigns the unique identifier to the enum constant.
	 * @param id The integer value representing the acquisition type.
	 */
	private AcquisitionType(int id)
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
}
