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

import com.aionemu.gameserver.model.SkillElement;

/**
 * Defines the different types of attacks that an item can perform.<br>
 * This enumeration is used to categorize combat behaviors for various items in the game.
 * @author ATracer
 */
@XmlEnum
public enum ItemAttackType
{
	PHYSICAL(false, SkillElement.NONE),
	MAGICAL_EARTH(true, SkillElement.EARTH),
	MAGICAL_WATER(true, SkillElement.WATER),
	MAGICAL_WIND(true, SkillElement.WIND),
	MAGICAL_FIRE(true, SkillElement.FIRE);
	
	private final boolean magic;
	private final SkillElement elem;
	
	/**
	 * Creates a new instance of {@link ItemAttackType}.<br>
	 * This constructor sets the basic properties for an attack type.
	 * @param magic A boolean indicating if the attack is magical.
	 * @param elem The {@link SkillElement} associated with this attack.
	 */
	private ItemAttackType(boolean magic, SkillElement elem)
	{
		this.magic = magic;
		this.elem = elem;
	}
	
	/**
	 * Checks if the attack type is magical.<br>
	 * This method returns {@code true} for all magical types.<br>
	 * It returns {@code false} for physical types.
	 * @return {@code true} if the item is magical, otherwise {@code false}.
	 */
	public boolean isMagical()
	{
		return magic;
	}
	
	/**
	 * Retrieves the magical element associated with this attack type.<br>
	 * This method returns the {@code SkillElement} value stored in the enum.
	 * @return The {@code SkillElement} of the item.
	 */
	public SkillElement getMagicalElement()
	{
		return elem;
	}
}
