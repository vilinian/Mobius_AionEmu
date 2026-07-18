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
package com.aionemu.gameserver.model.templates.teleport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a specific destination for teleportation within the game world.<br>
 * This class stores the coordinates and metadata required to move a player to a designated location.
 * @author ATracer
 */
@XmlRootElement(name = "telelocation")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeleportLocation
{
	@XmlAttribute(name = "loc_id", required = true)
	private int locId;
	@XmlAttribute(name = "teleportid")
	private int teleportid = 0;
	@XmlAttribute(name = "price", required = true)
	private int price = 0;
	@XmlAttribute(name = "pricePvp")
	private int pricePvp = 0;
	@XmlAttribute(name = "required_quest")
	private int required_quest = 0;
	@XmlAttribute(name = "type", required = true)
	private TeleportType type;
	
	/**
	 * Retrieves the location identifier for this event drop.<br>
	 * This value corresponds to the {@code loc_id} attribute.
	 * @return The unique integer ID of the location.
	 */
	public int getLocId()
	{
		return locId;
	}
	
	/**
	 * Retrieves the unique identifier for this teleport.<br>
	 * This value is used to identify specific teleport points in the game.
	 * @return The {@code int} value of the teleport ID.
	 */
	public int getTeleportId()
	{
		return teleportid;
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
	
	/**
	 * Retrieves the cost for using this teleport in PvP mode.<br>
	 * This value is stored as an {@code int}.
	 * @return The price of the teleport during PvP.
	 */
	public int getPricePvp()
	{
		return pricePvp;
	}
	
	/**
	 * Retrieves the quest ID needed to use this teleport.<br>
	 * This value is stored in the {@code required_quest} field.
	 * @return The integer ID of the required quest.
	 */
	public int getRequiredQuest()
	{
		return required_quest;
	}
	
	/**
	 * Retrieves the teleport type for this location.<br>
	 * This method returns the {@code TeleportType} associated with the teleport.
	 * @return the {@code TeleportType} of this teleport.
	 */
	public TeleportType getType()
	{
		return type;
	}
}
