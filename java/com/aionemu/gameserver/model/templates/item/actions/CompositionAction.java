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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.CompositionConfig;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for combining multiple items to create a new item.<br>
 * This action processes the composition requirements and rewards defined in {@link CompositionConfig}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompositionAction")
public class CompositionAction extends AbstractItemAction
{
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
	 * Checks if a player can perform a composition action.<br>
	 * It verifies the types and levels of the items provided.<br>
	 * Returns {@code true} if all requirements are met.
	 * @param player The {@link Player} attempting the action.
	 * @param tools The item used as a tool for the combination.
	 * @param first The first enchantment stone required.
	 * @param second The second enchantment stone required.
	 * @return {@code true} if the action is valid, otherwise {@code false}.
	 */
	public boolean canAct(Player player, Item tools, Item first, Item second)
	{
		if (!tools.getItemTemplate().isCombinationItem() || !first.getItemTemplate().isEnchantmentStone() || !second.getItemTemplate().isEnchantmentStone())
		{
			return false;
		}
		
		if ((first.getItemCount() < 1) || (second.getItemCount() < 1) || (first.getItemTemplate().getLevel() > 95) || (second.getItemTemplate().getLevel() > 95))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Executes the item composition action for a player.<br>
	 * This method handles the animation and logic to combine two items using specific tools.<br>
	 * It removes the required items from the inventory and grants the new result.
	 * @param player The {@link Player} performing the action.
	 * @param tools The {@link Item} used as a tool for composition.
	 * @param first The first {@link Item} to be consumed.
	 * @param second The second {@link Item} to be consumed.
	 */
	public void act(Player player, Item tools, Item first, Item second)
	{
		PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, tools.getObjectId(), tools.getItemTemplate().getTemplateId(), CompositionConfig.COMPOSITION_SPEED, 0));
		player.getController().cancelTask(TaskId.ITEM_USE);
		
		final ItemUseObserver observer = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ITEM_USE);
				PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, tools.getObjectId(), tools.getItemTemplate().getTemplateId(), 0, 2));
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
				final boolean result = player.getInventory().decreaseByObjectId(tools.getObjectId(), 1);
				final boolean result1 = player.getInventory().decreaseByObjectId(first.getObjectId(), 1);
				final boolean result2 = player.getInventory().decreaseByObjectId(second.getObjectId(), 1);
				if (result && result1 && result2)
				{
					ItemService.addItem(player, getItemId(calcLevel(first.getItemTemplate().getLevel(), second.getItemTemplate().getLevel(), tools.getItemTemplate().getTemplateId())), CompositionConfig.COMPOSITION_STONE_QUANTITY);
				}
				
				PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, tools.getObjectId(), tools.getItemTemplate().getTemplateId(), 0, 1));
			}
		}, CompositionConfig.COMPOSITION_SPEED));
	}
	
	/**
	 * Calculates the resulting level for a composition action.<br>
	 * It uses the average of {@code first} and {@code second} as a base.<br>
	 * The result is adjusted based on random values and the specific {@code tools} used.
	 * @param first The level of the first item.
	 * @param second The level of the second item.
	 * @param tools The ID of the tool being used.
	 * @return The calculated resulting level as an {@code int}.
	 */
	private int calcLevel(int first, int second, int tools)
	{
		final int itemId = tools;
		int value = ((first + second) / 2);
		if (value < 11)
		{
			value = Rnd.get(1, 20);
		}
		else
		{
			final int random = Rnd.get(CompositionConfig.COMPOSITION_RND_MIN, CompositionConfig.COMPOSITION_RND_MAX);
			final int bit = Rnd.get(0, 1);
			if (itemId == 165010001)
			{
				// Vindachinerk's Fine Combination Tool should only give +
				value = (bit == 0 ? value + random : value + random);
			}
			else
			{
				value = (bit == 0 ? value - random : value + random);
			}
		}
		
		return value;
	}
	
	/**
	 * This method calculates a specific item ID based on an input value.<br>
	 * It adds the {@code value} to a base constant of {@code 166000000}.
	 * @param value The numeric offset to add to the base ID.
	 * @return The resulting unique item ID as an {@code int}.
	 */
	public int getItemId(int value)
	{
		return 166000000 + value;
	}
}
