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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.services.CubeExpandService;
import com.aionemu.gameserver.services.WarehouseService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for expanding a player's inventory capacity.<br>
 * This action interacts with {@link CubeExpandService} to process expansion items.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExpandInventoryAction")
public class ExpandInventoryAction extends AbstractItemAction
{
	@XmlAttribute(name = "level")
	private int level;
	@XmlAttribute(name = "storage")
	private StorageType storage;
	
	/**
	 * Checks if the {@link Player} is allowed to perform this action.<br>
	 * It validates requirements based on the current storage type.<br>
	 * For {@code WAREHOUSE}, it checks permissions via {@link WarehouseService}.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		switch (storage)
		{
			case CUBE:
				// return CubeExpandService.canExpandByTicket(player, level); //TODO: is a Key now
			case WAREHOUSE:
				return WarehouseService.canExpand(player);
		}
		
		return false;
	}
	
	/**
	 * Executes the action to expand inventory space.<br>
	 * This method handles logic for expanding {@code CUBE} or {@code WAREHOUSE} storage.<br>
	 * It removes the {@link Item} from the player and plays an animation.
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
		
		final ItemTemplate itemTemplate = parentItem.getItemTemplate();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), itemTemplate.getTemplateId()), true);
		
		switch (storage)
		{
			case CUBE:
				CubeExpandService.expand(player, false);
				break;
			case WAREHOUSE:
				WarehouseService.expand(player);
				break;
		}
	}
}

enum StorageType
{
	CUBE,
	WAREHOUSE
}
