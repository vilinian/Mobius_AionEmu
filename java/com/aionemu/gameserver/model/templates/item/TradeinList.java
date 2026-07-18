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
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.aionemu.gameserver.model.templates.item;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a collection of items that can be exchanged or traded in.<br>
 * This class defines the data structure for trade-in lists within the game templates.
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TradeinList", propOrder =
{
	"tradeinItem"
})
public class TradeinList
{
	@XmlAttribute
	protected int ap;
	@XmlAttribute
	protected int price;
	
	@XmlElement(name = "tradein_item")
	protected List<TradeinItem> tradeinItem;
	
	/**
	 * Retrieves the list of items available for trade-in.<br>
	 * This method returns all {@link TradeinItem} objects stored in this list.
	 * @return a {@code List} containing all {@code TradeinItem} objects.
	 */
	public List<TradeinItem> getTradeinItem()
	{
		return tradeinItem;
	}
	
	/**
	 * Retrieves the first item from the {@code tradeinItem} list.<br>
	 * This method returns the element at index {@code 0}.
	 * @return The first {@link TradeinItem} in the list.
	 */
	public TradeinItem getFirstTradeInItem()
	{
		return tradeinItem.get(0);
	}
	
	/**
	 * Retrieves the current amount of AP.<br>
	 * This value represents the player's active AP points.
	 * @return The current {@code int} value of AP.
	 */
	public int getAp()
	{
		return ap;
	}
	
	/**
	 * Retrieves the cost of this bind point.<br>
	 * This value is stored as an {@code int}.
	 * @return The current price of the template.
	 */
	public int getPrice()
	{
		return price;
	}
}
