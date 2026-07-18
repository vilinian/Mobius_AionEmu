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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents an item that can be used within the housing system.<br>
 * This class defines the properties and behaviors for interactable objects in a player's home.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HousingUseableItem", propOrder =
{
	"action"
})
public class HousingUseableItem extends PlaceableHouseObject
{
	@XmlElement(required = true)
	protected UseItemAction action;
	@XmlAttribute(required = true)
	protected boolean owner;
	@XmlAttribute
	protected Integer cd;
	@XmlAttribute(required = true)
	protected int delay;
	@XmlAttribute(name = "use_count")
	protected Integer useCount;
	@XmlAttribute(name = "required_item")
	protected Integer requiredItem;
	
	/**
	 * Retrieves the action associated with this house item.<br>
	 * This method returns the {@code UseItemAction} defined for the object.
	 * @return the {@code UseItemAction} of this item.
	 */
	public UseItemAction getAction()
	{
		return action;
	}
	
	/**
	 * Checks if the item can only be used by its owner.<br>
	 * This method returns the value of the {@code owner} field.
	 * @return {@code true} if only the owner can use it, {@code false} otherwise.
	 */
	public boolean isOwnerOnly()
	{
		return owner;
	}
	
	/**
	 * Retrieves the cooldown value for this item.<br>
	 * This value is used to determine how long a wait period lasts.
	 * @return the {@code Integer} cooldown value.
	 */
	public Integer getCd()
	{
		return cd;
	}
	
	/**
	 * Retrieves the time delay for this announcement.<br>
	 * This value is stored as an {@code int}.
	 * @return The current delay value.
	 */
	public int getDelay()
	{
		return delay;
	}
	
	/**
	 * Retrieves the total number of times this item can be used.<br>
	 * This value is stored in the {@code useCount} field.
	 * @return The current usage count as an {@code Integer}.
	 */
	public Integer getUseCount()
	{
		return useCount;
	}
	
	/**
	 * Retrieves the ID of the item needed to use this object.<br>
	 * This value is stored in the {@code requiredItem} field.
	 * @return the {@code Integer} ID of the required item, or {@code null} if none is required.
	 */
	public Integer getRequiredItem()
	{
		return requiredItem;
	}
	
	/**
	 * Retrieves the unique identifier for this object type.<br>
	 * This value is used to identify the chair in the game world.
	 * @return The {@code byte} ID of the housing chair, which is always {@code 5}.
	 */
	@Override
	public byte getTypeId()
	{
		return 1;
	}
}
