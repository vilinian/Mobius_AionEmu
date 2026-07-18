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
 * This class represents a collection of enchantment chances for items.<br>
 * It is used to define the probability of success for various item enchantments.
 */
@XmlType(name = "ItemEnchantChanceList")
public class ItemEnchantChanceList
{
	@XmlAttribute(name = "level")
	private int level;
	@XmlAttribute(name = "chance")
	private int chance;
	@XmlAttribute(name = "crit")
	private int crit;
	
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
	 * Retrieves the success rate for an enchantment.<br>
	 * This value represents the percentage chance of a successful outcome.
	 * @return The current {@code chance} value as an {@code int}.
	 */
	public int getChance()
	{
		return chance;
	}
	
	/**
	 * Retrieves the critical hit chance for this item enchantment.<br>
	 * This value is stored as an {@code int}.
	 * @return The current {@code crit} value.
	 */
	public int getCrit()
	{
		return crit;
	}
}
