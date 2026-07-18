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

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for items that grant experience points to a {@link Player}.<br>
 * This action processes the reward and notifies the user via a {@code SM_SYSTEM_MESSAGE}.
 * @author Alex on 23
 */
public class ExpAction extends AbstractItemAction
{
	@XmlAttribute(name = "cost")
	protected Integer cost;
	
	@XmlAttribute(name = "percent")
	protected boolean isPercent;
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It returns {@code true} if the {@code cost} is not {@code null}.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		if (cost != null)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Executes the action to gain experience from an item.<br>
	 * This method handles the logic when a {@link Player} uses an item to receive experience points.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		final long exp = player.getCommonData().getExpNeed();
		final long expPercent = isPercent ? ((exp * cost) / 100) : cost;
		if (player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
		{
			player.getCommonData().setExp(player.getCommonData().getExp() + expPercent);
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId()));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GET_EXP_DESC(new DescriptionId(parentItem.getNameId()), expPercent));
		}
		else
		{
			return;
		}
	}
}
