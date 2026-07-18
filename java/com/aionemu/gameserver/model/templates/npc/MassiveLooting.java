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
package com.aionemu.gameserver.model.templates.npc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class defines the configuration for massive looting mechanics.<br>
 * It handles how loot is distributed when multiple items are dropped at once.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MassiveLooting")
public class MassiveLooting
{
	@XmlAttribute
	protected int itemid;
	
	@XmlAttribute(name = "looting_num")
	protected int lootingNum;
	
	@XmlAttribute(name = "min_level")
	protected int minLevel;
	
	@XmlAttribute(name = "max_level")
	protected int maxLevel;
	
	/**
	 * Retrieves the total number of items to be looted.<br>
	 * This value is stored in the {@code lootingNum} field.
	 * @return The current {@code lootingNum} as an {@code int}.
	 */
	public int getLootingNum()
	{
		return lootingNum;
	}
	
	/**
	 * Retrieves the unique identifier for this material.<br>
	 * This value corresponds to the {@code itemid} field.
	 * @return The integer ID of the item.
	 */
	public int getItemid()
	{
		return itemid;
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
