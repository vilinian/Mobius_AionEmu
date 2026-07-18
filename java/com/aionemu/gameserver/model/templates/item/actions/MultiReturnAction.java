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

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.teleport.ScrollItem;
import com.aionemu.gameserver.model.templates.teleport.ScrollItemLocationList;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.services.teleport.ScrollsTeleporterService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for items that teleport a player to multiple locations.<br>
 * This action processes {@link ScrollItemLocationList} data to execute sequential teleports.
 * @author Alcapwnd
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiReturnAction")
public class MultiReturnAction extends AbstractItemAction
{
	@XmlAttribute(name = "id")
	private int id;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It currently always returns {@code true}.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		return true;
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
	 * Executes the teleport action for a multi-return scroll.<br>
	 * This method handles the animation and teleports the {@link Player} to a specific location.
	 * @param player The {@link Player} who is using the item.
	 * @param ScrollItem The {@link ScrollItem} being used by the player.
	 * @param SelectedMapIndex The index of the destination map chosen by the player.
	 */
	public void act(Player player, Item ScrollItem, int SelectedMapIndex)
	{
		PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, ScrollItem.getObjectId(), ScrollItem.getItemTemplate().getTemplateId(), 5000, 0));
		player.getController().cancelTask(TaskId.ITEM_USE);
		
		final ItemUseObserver observer = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ITEM_USE);
				PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, ScrollItem.getObjectId(), ScrollItem.getItemTemplate().getTemplateId(), 0, 2));
				player.getObserveController().removeObserver(this);
				player.removeItemCoolDown(ScrollItem.getItemTemplate().getUseLimits().getDelayId());
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			player.getObserveController().removeObserver(observer);
			if (player.getInventory().decreaseByObjectId(ScrollItem.getObjectId(), 1))
			{
				
				final int ScrollItemId = getId();
				final ScrollItem sItem = DataManager.MULTI_RETURN_ITEM_DATA.getScrollItembyId(ScrollItemId);
				
				if ((sItem != null) && (sItem.getLocationList() != null))
				{
					
					final ScrollItemLocationList LocData = sItem.getLocDatabyId(SelectedMapIndex);
					if (LocData != null)
					{
						
						final int LocCount = sItem.getLocationList().size();
						if (SelectedMapIndex <= (LocCount - 1))
						{
							
							final int worldId = LocData.getWorldId();
							int LocId = ScrollsTeleporterService.getScrollLocIdbyWorldId(worldId, player.getRace());
							
							if (LocId == 0)
							{
								LocId = Integer.parseInt(Integer.toString(worldId).substring(0, 7));
							}
							
							ScrollsTeleporterService.ScrollTeleprter(player, LocId, worldId);
						}
					}
				}
			}
			
			PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, ScrollItem.getObjectId(), ScrollItem.getItemTemplate().getTemplateId(), 0, 1));
		}, 5000));
	}
}
