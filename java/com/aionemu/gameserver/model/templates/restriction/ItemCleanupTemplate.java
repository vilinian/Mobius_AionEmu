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
package com.aionemu.gameserver.model.templates.restriction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class defines the template for item cleanup restrictions.<br>
 * It manages how items are automatically removed from the game world.<br>
 * Use this model to configure specific cleanup rules in the configuration files.
 * @author KID
 */
@XmlRootElement(name = "item_restriction_cleanups")
@XmlAccessorType(XmlAccessType.NONE)
public class ItemCleanupTemplate
{
	@XmlAttribute(name = "id", required = true)
	private int id;
	@XmlAttribute
	private byte trade = -1;
	@XmlAttribute
	private byte sell = -1;
	@XmlAttribute
	private byte wh = -1;
	@XmlAttribute
	private byte awh = -1;
	@XmlAttribute
	private byte lwh = -1;
	
	/**
	 * Retrieves the trade restriction status for this item.<br>
	 * This method returns the value stored in the {@code trade} field.
	 * @return The {@code byte} value representing the trade result.
	 */
	public byte resultTrade()
	{
		return trade;
	}
	
	/**
	 * Retrieves the selling restriction for this item.<br>
	 * This method returns the value stored in the {@code sell} field.
	 * @return The {@code byte} value representing the sell status.
	 */
	public byte resultSell()
	{
		return sell;
	}
	
	/**
	 * Retrieves the warehouse restriction value.<br>
	 * This method returns the {@code wh} attribute for this template.
	 * @return The current warehouse restriction as a {@code byte}.
	 */
	public byte resultWH()
	{
		return wh;
	}
	
	/**
	 * Retrieves the account warehouse restriction status.<br>
	 * This method returns the value of the {@code awh} attribute.
	 * @return The result of the account warehouse check as a {@code byte}.
	 */
	public byte resultAccountWH()
	{
		return awh;
	}
	
	/**
	 * Retrieves the Legion Warehouse restriction value.<br>
	 * This method returns the {@code lwh} attribute for the current template.
	 * @return The result of the Legion Warehouse check as a {@code byte}.
	 */
	public byte resultLegionWH()
	{
		return lwh;
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
