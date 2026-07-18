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
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_EXP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the logic for extracting experience from an item.<br>
 * This action allows a {@link Player} to gain experience points when using a specific {@link Item}.<br>
 * It manages the removal of the item and sends the corresponding {@code SM_STATUPDATE_EXP} packet.
 * @author Rolandas
 * @author Alcapwnd
 */
public class ExpExtractAction extends AbstractItemAction
{
	@XmlAttribute
	protected int cost;
	@XmlAttribute(name = "percent")
	protected boolean isPercent;
	@XmlAttribute(name = "item_id")
	protected int itemId;
	@XmlTransient
	private final boolean isEventExp = false;
	@XmlTransient
	private final Logger log = LoggerFactory.getLogger(ExpExtractAction.class);
	
	/**
	 * Checks if a {@link Player} is allowed to perform this action.<br>
	 * It verifies that the player has experience points and inventory space.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the requirements are met, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		if ((player.getCommonData().getExp() == 0) || player.getInventory().isFull())
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Executes the action to extract experience from an item.<br>
	 * This method handles the logic when a {@link Player} uses an item to reduce their current experience and receive a new item.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), 0, parentItem.getObjectId().intValue(), parentItem.getItemTemplate().getTemplateId(), 5000, 0));
		player.getController().cancelTask(TaskId.ITEM_USE);
		final ItemUseObserver observer = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ITEM_USE);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_CANCELED(parentItem.getNameId()));
				PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), 0, parentItem.getObjectId().intValue(), parentItem.getItemTemplate().getTemplateId(), 0, 2));
				player.getObserveController().removeObserver(this);
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				player.getObserveController().removeObserver(observer);
				int toDecrease = 0;
				if (isPercent)
				{
					toDecrease = (int) (player.getCommonData().getExpNeed() / 100) * cost;
				}
				else
				{
					toDecrease = cost;
				}
				
				player.getCommonData().setExp(player.getCommonData().getExp() - toDecrease);
				ItemService.addItem(player, itemId, 1);
				player.getInventory().decreaseByItemId(parentItem.getItemId(), 1);
				PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), 0, parentItem.getObjectId().intValue(), parentItem.getItemTemplate().getTemplateId(), 0, 1));
			}
		}, 5000));
		PacketSendUtility.sendPacket(player, new SM_STATUPDATE_EXP(player.getCommonData().getExpShown(), player.getCommonData().getExpRecoverable(), player.getCommonData().getExpNeed(), player.getCommonData().getCurrentReposteEnergy(), player.getCommonData().getMaxReposteEnergy(), player.getCommonData().getGoldenStarEnergy(), player.getCommonData().getGrowthEnergy()));
	}
}
