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
package com.aionemu.gameserver.model.templates.Guides;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a template for survey data within the game server.<br>
 * This class defines the structure used to store and manage survey information in the database.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SurveyTemplate")
public class SurveyTemplate
{
	@XmlAttribute(name = "itemId")
	private int itemId;
	@XmlAttribute(name = "count")
	private long count;
	
	/**
	 * Retrieves the current quantity of the item.<br>
	 * This value is updated by the {@code calculateCount} method.
	 * @return The total number of items as a {@code long}.
	 */
	public long getCount()
	{
		return count;
	}
	
	/**
	 * Retrieves the unique identifier for this wardrobe item.<br>
	 * This value corresponds to the {@code itemId} assigned during object creation.
	 * @return The unique {@code int} ID of the item.
	 */
	public int getItemId()
	{
		return itemId;
	}
}
