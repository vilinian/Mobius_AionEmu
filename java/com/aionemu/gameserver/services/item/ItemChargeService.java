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
package com.aionemu.gameserver.services.item;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.items.ChargeInfo;
import com.aionemu.gameserver.model.templates.item.Improvement;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles the logic for charging items with specific effects.<br>
 * It manages the interaction between {@link Item} objects and their associated {@link ChargeInfo}.<br>
 * It ensures that item charges are correctly applied, updated, and synchronized with the client.
 * @author ATracer
 */
public class ItemChargeService
{
	/**
	 * Filters a list of items based on the specified charging conditions.<br>
	 * This method checks if an item is eligible for charging by the {@link Player}.<br>
	 * It returns a collection of valid {@code Item} objects.
	 * @param player The {@link Player} who owns the items.
	 * @param selectedItem The specific {@code Item} to check; if not {@code null}, it is returned immediately.
	 * @param chargeWay The required charging method for the item.
	 * @return A {@code Collection} of filtered {@code Item} objects.
	 */
	public static Collection<Item> filterItemsToCondition(Player player, Item selectedItem, int chargeWay)
	{
		if (selectedItem != null)
		{
			return Collections.singletonList(selectedItem);
		}
		
		return player.getEquipment().getEquippedItems().stream().filter(item -> (item.getChargeLevelMax() != 0) && (item.getImprovement() != null) && (item.getImprovement().getChargeWay() == chargeWay) && (item.getChargePoints() < ChargeInfo.LEVEL2)).collect(Collectors.toList());
	}
	
	/**
	 * Starts the process of charging all eligible equipped items for a player.<br>
	 * This method calculates the total cost and sends a confirmation window to the user.<br>
	 * If the payment is successful, it applies the maximum charge level to each item.
	 * @param player The {@link Player} who will perform the action.
	 * @param senderObj The object ID of the source that triggered this action.
	 * @param chargeWay The specific method or type of charging to be used.
	 */
	public static void startChargingEquippedItems(Player player, int senderObj, int chargeWay)
	{
		// TODO: Check this : SM_QUESTION_WINDOW.STR_ITEM_CHARGE_CONFIRM_SOME_ALREADY_CHARGED !!!
		final Collection<Item> filteredItems = filterItemsToCondition(player, null, chargeWay);
		if (filteredItems.isEmpty())
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(chargeWay == 1 ? 1400895 : 1401343));
			return;
		}
		
		final long payAmount = calculatePrice(filteredItems);
		
		final RequestResponseHandler request = new RequestResponseHandler(player)
		{
			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				if (processPayment(player, chargeWay, payAmount))
				{
					for (Item item : filteredItems)
					{
						chargeItem(player, item, item.getChargeLevelMax());
					}
				}
			}
			
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				// Nothing Happens
			}
		};
		final int msg = chargeWay == 1 ? SM_QUESTION_WINDOW.STR_ITEM_CHARGE_ALL_CONFIRM : SM_QUESTION_WINDOW.STR_ITEM_CHARGE2_ALL_CONFIRM;
		if (player.getResponseRequester().putRequest(msg, request))
		{
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(msg, senderObj, 0, String.valueOf(payAmount)));
		}
	}
	
	/**
	 * Calculates the total cost for a group of items.<br>
	 * It sums the price for each {@link Item} based on its maximum charge level.
	 * @param items The collection of {@code Item} objects to calculate.
	 * @return The total calculated price as a {@code long}.
	 */
	private static long calculatePrice(Collection<Item> items)
	{
		long result = 0;
		for (Item item : items)
		{
			result += getPayAmountForService(item, item.getChargeLevelMax());
		}
		
		return result;
	}
	
	/**
	 * This method charges multiple items for a {@link Player}.<br>
	 * It iterates through the provided collection and applies the charge.<br>
	 * Each item is processed using the specified {@code level}.
	 * @param player The {@link Player} who owns the items.
	 * @param items The collection of {@link Item} objects to be charged.
	 * @param level The charge level to apply to each item.
	 */
	public static void chargeItems(Player player, Collection<Item> items, int level)
	{
		for (Item item : items)
		{
			chargeItem(player, item, level);
		}
	}
	
	/**
	 * Charges a specific item for the player.<br>
	 * This method updates the charge points of an {@link Item}.<br>
	 * It also sends the necessary update packets to the {@link Player}.
	 * @param player The {@link Player} who owns the item.
	 * @param item The {@link Item} to be charged.
	 * @param level The target charge level for the item.
	 */
	public static void chargeItem(Player player, Item item, int level)
	{
		final Improvement improvement = item.getImprovement();
		if (improvement == null)
		{
			return;
		}
		
		final int chargeWay = improvement.getChargeWay();
		final int currentCharge = item.getChargePoints();
		switch (level)
		{
			case 1:
				item.getConditioningInfo().updateChargePoints(ChargeInfo.LEVEL1 - currentCharge);
				break;
			case 2:
				item.getConditioningInfo().updateChargePoints(ChargeInfo.LEVEL2 - currentCharge);
				break;
		}
		
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item, ItemUpdateType.CHARGE));
		player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
		if (chargeWay == 1)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_CHARGE_SUCCESS(new DescriptionId(item.getNameId()), level));
		}
		else
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_CHARGE2_SUCCESS(new DescriptionId(item.getNameId()), level));
		}
		
		player.getGameStats().updateStatsVisually();
	}
	
	/**
	 * Validates if a {@link Player} can afford the cost of an item upgrade.<br>
	 * This method checks the required currency for a specific {@code level}.<br>
	 * It returns {@code true} if the payment is successful.
	 * @param player The {@link Player} making the purchase.
	 * @param item The {@link Item} being upgraded.
	 * @param level The target upgrade level.
	 * @return {@code true} if the payment succeeds, otherwise {@code false}.
	 */
	public static boolean processPayment(Player player, Item item, int level)
	{
		return processPayment(player, item.getImprovement().getChargeWay(), getPayAmountForService(item, level));
	}
	
	/**
	 * Processes a payment for an item charge service.<br>
	 * It checks the {@code chargeWay} to determine the currency type.<br>
	 * This method handles either Kinah or AP payments based on the input.
	 * @param player The {@link Player} making the payment.
	 * @param chargeWay The method of charging, where {@code 1} is Kinah and {@code 2} is AP.
	 * @param amount The total amount to be deducted from the player.
	 * @return {@code true} if the payment was successful, or {@code false} otherwise.
	 */
	public static boolean processPayment(Player player, int chargeWay, long amount)
	{
		switch (chargeWay)
		{
			case 1:
				return processKinahPayment(player, amount);
			case 2:
				return processAPPayment(player, amount);
		}
		
		return false;
	}
	
	/**
	 * Deducts the required amount of Kinah from a {@link Player}.<br>
	 * This method checks if the player has enough currency.<br>
	 * It returns {@code true} if the transaction succeeds.<br>
	 * It returns {@code false} if the player has insufficient funds.
	 * @param player The {@link Player} who will pay the cost.
	 * @param requiredKinah The amount of Kinah to be removed from the inventory.
	 * @return {@code true} if the payment was successful, otherwise {@code false}.
	 */
	public static boolean processKinahPayment(Player player, long requiredKinah)
	{
		return player.getInventory().tryDecreaseKinah(requiredKinah);
	}
	
	/**
	 * Checks if a {@link Player} has enough Abyss Points to pay for a service.<br>
	 * Deducts the required amount from the player's balance if successful.
	 * @param player The {@code Player} object who will be charged.
	 * @param requiredAP The amount of AP to deduct from the player.
	 * @return {@code true} if the payment was successful, or {@code false} if the player has insufficient funds.
	 */
	public static boolean processAPPayment(Player player, long requiredAP)
	{
		if (player.getAbyssRank().getAp() < requiredAP)
		{
			return false;
		}
		
		AbyssPointsService.addAp(player, (int) -requiredAP);
		return true;
	}
	
	/**
	 * Calculates the cost to charge a specific item.<br>
	 * This method uses the {@code Improvement} data of the provided {@link Item}.<br>
	 * It returns 0 if the item has no improvement data.
	 * @param item The {@link Item} that needs to be charged.
	 * @param chargeLevel The level of the charge service to perform.
	 * @return The total cost as a {@code long}.
	 */
	public static long getPayAmountForService(Item item, int chargeLevel)
	{
		final Improvement improvement = item.getImprovement();
		if (improvement == null)
		{
			return 0;
		}
		
		final int price1 = improvement.getPrice1();
		final int price2 = improvement.getPrice2();
		final double firstLevel = price1 / 2;
		final double updateLevel = Math.round(firstLevel + ((price2 - price1) / 2d));
		double money = 0;
		switch (chargeLevel)
		{
			case 1:
				money = firstLevel;
				break;
			case 2:
				switch (getNextChargeLevel(item))
				{
					case 1: // full
						money = (firstLevel + updateLevel);
						break;
					case 2: // update
						money = updateLevel;
						break;
				}
		}
		
		return (long) money;
	}
	
	/**
	 * Determines the next available charge level for a specific item.<br>
	 * It checks the current points of the {@code item}.<br>
	 * This method helps identify if an item should move to {@code ChargeInfo.LEVEL1} or {@code ChargeInfo.LEVEL2}.
	 * @param item The {@link Item} object to check for its charge level.
	 * @return The integer value representing the next charge level.
	 */
	private static int getNextChargeLevel(Item item)
	{
		final int charge = item.getChargePoints();
		if (charge < ChargeInfo.LEVEL1)
		{
			return 1;
		}
		
		if (charge < ChargeInfo.LEVEL2)
		{
			return 2;
		}
		
		throw new IllegalArgumentException("Invalid charge level " + charge);
	}
}
