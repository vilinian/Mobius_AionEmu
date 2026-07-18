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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a collection of items obtained through extraction processes.<br>
 * It extends {@link ResultedItemsCollection} to handle specific extracted item data.
 * @author antness
 */
@XmlType(name = "ExtractedItemsCollection")
public class ExtractedItemsCollection extends ResultedItemsCollection
{
	@XmlAttribute(name = "chance")
	protected float chance = 100f;
	@XmlAttribute(name = "minlevel")
	protected int minLevel;
	@XmlAttribute(name = "maxlevel")
	protected int maxLevel;
	
	/**
	 * Retrieves the probability of this drop occurring.<br>
	 * The value is stored as a {@code float}.
	 * @return The drop chance value.
	 */
	public float getChance()
	{
		return chance;
	}
	
	/**
	 * Retrieves the minimum level required for this auto group.<br>
	 * This value is fetched from the underlying template.
	 * @return The minimum level as an {@code int}.
	 */
	public int getMinLevel()
	{
		return minLevel;
	}
	
	/**
	 * Gets the maximum level allowed for this challenge task.<br>
	 * This value is retrieved from the {@code maxLevel} field.
	 * @return The maximum level as an {@code int}.
	 */
	public int getMaxLevel()
	{
		return maxLevel;
	}
}
