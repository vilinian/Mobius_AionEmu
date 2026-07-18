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
import com.aionemu.gameserver.model.templates.pet.PetTemplate;

/**
 * Handles the logic for a player to adopt a pet.<br>
 * This action is triggered when interacting with a specific {@link Item}.<br>
 * It manages the transition of ownership and initialization for the pet.
 * @author Rolandas
 */
public class AdoptPetAction extends AbstractItemAction
{
	@XmlAttribute(name = "petId")
	private int petId;
	@XmlAttribute(name = "minutes")
	private int expireMinutes;
	@XmlAttribute(name = "sidekick")
	private Boolean isSideKick = false;
	
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
	 * Retrieves the unique identifier for this pet.<br>
	 * This value is obtained from the {@link PetTemplate}.
	 * @return The {@code int} ID of the pet template.
	 */
	public int getPetId()
	{
		return petId;
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
	 * Checks if the current action belongs to a sidekick.<br>
	 * This method returns the value of the {@code isSideKick} field.
	 * @return {@code true} if it is a sidekick, otherwise {@code false}.
	 */
	public Boolean isSideKick()
	{
		return isSideKick;
	}
}
