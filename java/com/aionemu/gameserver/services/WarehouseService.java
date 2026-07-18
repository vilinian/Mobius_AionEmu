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
package com.aionemu.gameserver.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.templates.WarehouseExpandTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service manages all warehouse-related operations for players.<br>
 * It handles storage interactions, including viewing items and expanding capacity.<br>
 * Use this class to interact with {@link StorageType} data and manage player inventory.
 * @author Simple
 */
public class WarehouseService
{
	private static final Logger log = LoggerFactory.getLogger(WarehouseService.class);
	private static final int MIN_EXPAND = 0;
	private static final int MAX_EXPAND = 11;
	
	/**
	 * This method handles the logic for expanding a player's warehouse.<br>
	 * It checks if the {@link Player} can afford and is allowed to increase their storage size.<br>
	 * If successful, it opens a confirmation window for the user.
	 * @param player The {@code Player} who wants to expand their warehouse.
	 * @param npc The {@code Npc} that the player is interacting with.
	 */
	public static void expandWarehouse(Player player, Npc npc)
	{
		final WarehouseExpandTemplate expandTemplate = DataManager.WAREHOUSEEXPANDER_DATA.getWarehouseExpandListTemplate(npc.getNpcId());
		
		if (expandTemplate == null)
		{
			log.error("Warehouse Expand Template could not be found for Npc ID: " + npc.getObjectTemplate().getTemplateId());
			return;
		}
		
		if (npcCanExpandLevel(expandTemplate, player.getWarehouseSize() + 1) && validateNewSize(player.getWarehouseSize() + 1))
		{
			if (validateNewSize(player.getWarehouseSize() + 1))
			{
				/**
				 * Check if our player can pay the warehouse expand price
				 */
				final int price = getPriceByLevel(expandTemplate, player.getWarehouseSize() + 1);
				final RequestResponseHandler responseHandler = new RequestResponseHandler(npc)
				{
					
					@Override
					public void acceptRequest(Creature requester, Player responder)
					{
						if (player.getInventory().getKinah() < price)
						{
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300831));
							return;
						}
						
						expand(responder);
						player.getInventory().decreaseKinah(price);
					}
					
					@Override
					public void denyRequest(Creature requester, Player responder)
					{
						// nothing to do
					}
				};
				
				final boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_WAREHOUSE_EXPAND_WARNING, responseHandler);
				if (result)
				{
					PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_WAREHOUSE_EXPAND_WARNING, 0, 0, String.valueOf(price)));
				}
			}
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300432));
		}
	}
	
	/**
	 * Increases the warehouse size for a specific player.<br>
	 * This method checks if the {@link Player} is eligible to expand.<br>
	 * It updates the size and sends a confirmation message.
	 * @param player The {@code Player} object to modify.
	 */
	public static void expand(Player player)
	{
		if (!canExpand(player))
		{
			return;
		}
		
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300433, "8")); // 8 Slots added
		player.setWarehouseSize(player.getWarehouseSize() + 1);
		
		sendWarehouseInfo(player, false);
	}
	
	/**
	 * Checks if the provided size is within the allowed limits.<br>
	 * It ensures the value is not smaller than {@code 0}.<br>
	 * It also ensures the value does not exceed {@code 15}.
	 * @param level The new size to validate.
	 * @return {@code true} if the size is valid, otherwise {@code false}.
	 */
	private static boolean validateNewSize(int level)
	{
		// check min and max level
		return !((level < MIN_EXPAND) || (level > MAX_EXPAND));
	}
	
	/**
	 * Checks if a {@link Player} is allowed to expand their cube.<br>
	 * This method verifies the next expansion level against the maximum limit.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the expansion is valid, {@code false} otherwise.
	 */
	public static boolean canExpand(Player player)
	{
		return validateNewSize(player.getWarehouseSize() + 1);
	}
	
	/**
	 * Checks if an NPC is allowed to expand to a specific level.<br>
	 * This method verifies the level against the provided {@code WarehouseExpandTemplate}.
	 * @param clist The template containing valid expansion levels.
	 * @param level The target level to check.
	 * @return {@code true} if the level exists in the template, {@code false} otherwise.
	 */
	private static boolean npcCanExpandLevel(WarehouseExpandTemplate clist, int level)
	{
		// check if level exists in template
		return clist.contains(level);
	}
	
	/**
	 * Retrieves the price for a specific warehouse expansion level.<br>
	 * This method looks up the cost based on the provided {@code WarehouseExpandTemplate}.
	 * @param clist The template containing the list of expansion data.
	 * @param level The specific expansion level to check.
	 * @return The price associated with the given level as an {@code int}.
	 */
	private static int getPriceByLevel(WarehouseExpandTemplate clist, int level)
	{
		return clist.get(level).getPrice();
	}
	
	/**
	 * Sends the warehouse information to a specific {@link Player}.<br>
	 * This method handles sending packets for both regular and account warehouses.
	 * @param player The {@code Player} who will receive the warehouse data.
	 * @param sendAccountWh Set to {@code true} to include account warehouse data in the transmission.
	 */
	public static void sendWarehouseInfo(Player player, boolean sendAccountWh)
	{
		final List<Item> items = player.getStorage(StorageType.REGULAR_WAREHOUSE.getId()).getItems();
		
		final int whSize = player.getWarehouseSize();
		final int itemsSize = items.size();
		
		/**
		 * Regular warehouse
		 */
		boolean firstPacket = true;
		if (itemsSize != 0)
		{
			int index = 0;
			
			while ((index + 10) < itemsSize)
			{
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(items.subList(index, index + 10), StorageType.REGULAR_WAREHOUSE.getId(), whSize, firstPacket, player));
				index += 10;
				firstPacket = false;
			}
			
			PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(items.subList(index, itemsSize), StorageType.REGULAR_WAREHOUSE.getId(), whSize, firstPacket, player));
		}
		
		PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, StorageType.REGULAR_WAREHOUSE.getId(), whSize, false, player));
		
		if (sendAccountWh)
		{
			/**
			 * Account warehouse
			 */
			PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(player.getStorage(StorageType.ACCOUNT_WAREHOUSE.getId()).getItemsWithKinah(), StorageType.ACCOUNT_WAREHOUSE.getId(), 0, true, player));
		}
		
		PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, StorageType.ACCOUNT_WAREHOUSE.getId(), 0, false, player));
	}
}
