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
package com.aionemu.gameserver.model.templates.housing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a housing sale template in the game world.<br>
 * This class defines the data structure for selling housing properties.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "sale")
public class Sale
{
	@XmlAttribute(name = "point_price", required = true)
	protected int pointPrice;
	@XmlAttribute(name = "gold_price", required = true)
	protected long goldPrice;
	@XmlAttribute(required = true)
	protected int level;
	
	/**
	 * Retrieves the price of the item in points.<br>
	 * This value is stored in the {@code pointPrice} field.
	 * @return The current point price as an {@code int}.
	 */
	public int getPointPrice()
	{
		return pointPrice;
	}
	
	/**
	 * Retrieves the current price of an item in gold.<br>
	 * This value is stored in the {@code goldPrice} field.
	 * @return The amount of gold required for this sale.
	 */
	public long getGoldPrice()
	{
		return goldPrice;
	}
	
	/**
	 * Retrieves the minimum level required for this auto group.<br>
	 * This value is fetched from the underlying template.
	 * @return The minimum level as an {@code int}.
	 */
	public int getMinLevel()
	{
		return level;
	}
}
