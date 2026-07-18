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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * Represents the additional statistics granted to an item based on its enchantment level.<br>
 * This class maps specific {@link StatFunction} modifiers to different enchantment tiers.
 * @author Alcapwnd
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ItemEnchantBouns")
public class ItemEnchantBonus
{
	@XmlElement(name = "modifiers", required = false)
	private ModifiersTemplate modifiers;
	@XmlAttribute(name = "level")
	private int level;
	
	/**
	 * Retrieves the list of stat modifiers for this {@link ItemEnchantBonus}.<br>
	 * This method returns all active effects applied to the item.
	 * @return a {@code List} of {@link StatFunction} objects.
	 */
	public List<StatFunction> getModifiers()
	{
		return modifiers.getModifiers();
	}
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
}
