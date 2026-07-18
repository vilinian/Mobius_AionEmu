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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents an item improvement template in the game database.<br>
 * This class defines the properties and attributes for enhancing equipment.<br>
 * It is used by the item system to manage upgrade data.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Improvement")
public class Improvement
{
	@XmlAttribute(name = "way", required = true)
	private int way;
	@XmlAttribute(name = "price2")
	private int price2;
	@XmlAttribute(name = "price1")
	private int price1;
	@XmlAttribute(name = "burn_defend")
	private int burnDefend;
	@XmlAttribute(name = "burn_attack")
	private int burnAttack;
	@XmlAttribute(name = "level")
	private int level;
	
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
	 * Retrieves the charge way value.<br>
	 * This method returns the {@code way} attribute of the improvement.
	 * @return the current charge way as an {@code int}.
	 */
	public int getChargeWay()
	{
		return way;
	}
	
	/**
	 * Retrieves the first price of the improvement.<br>
	 * This value is stored in the {@code price1} field.
	 * @return the {@code price1} value as an {@code int}.
	 */
	public int getPrice1()
	{
		return price1;
	}
	
	/**
	 * Retrieves the second price value for this improvement.<br>
	 * This value is stored in the {@code price2} field.
	 * @return the {@code price2} value as an {@code int}.
	 */
	public int getPrice2()
	{
		return price2;
	}
	
	/**
	 * Retrieves the current burn attack value.<br>
	 * This value represents the strength of the burn effect.
	 * @return The {@code int} value of the burn attack.
	 */
	public int getBurnAttack()
	{
		return burnAttack;
	}
	
	/**
	 * Retrieves the defense value against burning effects.<br>
	 * This value is used to calculate how much damage is reduced.
	 * @return The {@code int} value of the burn defense.
	 */
	public int getBurnDefend()
	{
		return burnDefend;
	}
}
