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
package com.aionemu.gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Handles the logic for remodeling an item.<br>
 * This action allows a {@link Player} to change the appearance of an {@link Item}.
 * @author Rolandas
 */
public class RemodelAction extends AbstractItemAction
{
	@XmlAttribute(name = "type")
	private int extractType;
	@XmlAttribute(name = "minutes")
	private int expireMinutes;
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It currently always returns {@code false}.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		return false;
	}
	
	/**
	 * Executes the action for adopting a pet.<br>
	 * This method handles the logic when a {@link Player} interacts with an item to adopt it.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
	}
	
	/**
	 * Gets the number of minutes until the pet adoption expires.<br>
	 * This value is retrieved from the {@code expireMinutes} field.
	 * @return The expiration time in minutes as an {@code int}.
	 */
	public int getExpireMinutes()
	{
		return expireMinutes;
	}
	
	/**
	 * Retrieves the type of extraction for this action.<br>
	 * This value is used to determine how the item is processed.
	 * @return The {@code int} value representing the extraction type.
	 */
	public int getExtractType()
	{
		return extractType;
	}
}
