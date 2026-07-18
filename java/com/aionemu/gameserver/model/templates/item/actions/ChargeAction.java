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

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemChargeService;

/**
 * Handles the logic for charging an item.<br>
 * This action allows a {@link Player} to use an item that requires a charge process via the {@link ItemChargeService}.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargeItemAction")
public class ChargeAction extends AbstractItemAction
{
	@XmlAttribute
	protected int capacity;
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It checks if any required conditioning items are available.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		final Collection<Item> conditioningItems = ItemChargeService.filterItemsToCondition(player, null, parentItem.getImprovement().getChargeWay());
		return conditioningItems.size() > 0;
	}
	
	/**
	 * Executes the charging action for an item.<br>
	 * This method handles the logic when a {@link Player} uses an item to charge others.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
		{
			return;
		}
		
		final Collection<Item> conditioningItems = ItemChargeService.filterItemsToCondition(player, null, parentItem.getImprovement().getChargeWay());
		ItemChargeService.chargeItems(player, conditioningItems, capacity);
	}
}
