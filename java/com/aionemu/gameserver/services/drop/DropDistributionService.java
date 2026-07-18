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
package com.aionemu.gameserver.services.drop;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.DropNpc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_LOOT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles the logic for distributing loot among players.<br>
 * It manages how {@link DropItem} objects are assigned from a {@link DropNpc}.<br>
 * It ensures that distribution follows specific rules defined in {@link LootGroupRules}.
 * @author xTz
 */
public class DropDistributionService
{
	private static Logger log = LoggerFactory.getLogger(DropDistributionService.class);
	
	/**
	 * Gets the singleton instance of this service.<br>
	 * Use this method to access the global {@link DropDistributionService}.
	 * @return The single instance of {@code DropDistributionService}.
	 */
	public static DropDistributionService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Processes the loot roll result for a specific player.<br>
	 * This method handles sending packets to group members and distributes the item.<br>
	 * It checks if the {@code player} is in a valid group or alliance before proceeding.
	 * @param player The {@link Player} who performed the roll.
	 * @param roll The result of the roll, where {@code 0} indicates a give-up.
	 * @param itemId The unique identifier for the item being rolled for.
	 * @param npcId The unique identifier for the NPC that dropped the item.
	 * @param index The specific index of the drop from the NPC.
	 */
	public void handleRoll(Player player, int roll, int itemId, int npcId, int index)
	{
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if ((player == null) || (dropNpc == null))
		{
			return;
		}
		
		int luck = 0;
		if (player.isInGroup2() || player.isInAlliance2())
		{
			if (roll == 0)
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_GIVEUP_ME);
			}
			else
			{
				luck = Rnd.get(1, 100);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_RESULT_ME(luck, 100));
			}
			
			for (Player member : dropNpc.getInRangePlayers())
			{
				if (member == null)
				{
					log.warn("member null Owner is in group? " + player.isInGroup2() + " Owner is in Alliance? " + player.isInAlliance2());
					continue;
				}
				
				final int teamId = member.getCurrentTeamId();
				PacketSendUtility.sendPacket(member, new SM_GROUP_LOOT(teamId, member.getObjectId(), itemId, npcId, dropNpc.getDistributionId(), luck, index));
				if (!player.equals(member) && member.isOnline())
				{
					if (roll == 0)
					{
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_DICE_GIVEUP_OTHER(player.getName()));
					}
					else
					{
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_DICE_RESULT_OTHER(player.getName(), luck, 100));
					}
				}
			}
			
			distributeLoot(player, luck, itemId, npcId);
		}
	}
	
	/**
	 * Processes a loot bid made by a {@link Player}.<br>
	 * Validates the bid amount against the player's inventory.<br>
	 * Sends notifications to all nearby group members.<br>
	 * Triggers the final loot distribution for the specified item.
	 * @param player The {@link Player} who is making the bid.
	 * @param bid The amount of currency offered for the item.
	 * @param itemId The unique identifier of the item being bid on.
	 * @param npcId The ID of the NPC associated with the drop.
	 * @param index The specific index of the drop in the loot list.
	 */
	public void handleBid(Player player, long bid, int itemId, int npcId, int index)
	{
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if ((player == null) || (dropNpc == null))
		{
			return;
		}
		
		if (player.isInGroup2() || player.isInAlliance2())
		{
			if (((bid > 0) && (player.getInventory().getKinah() < bid)) || (bid < 0) || (bid > 999999999))
			{
				bid = 0; // Set BID to 0 if player has bid more KINAH then they have in inventory or send negative value
			}
			
			if (bid > 0)
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PAY_RESULT_ME);
			}
			else
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PAY_GIVEUP_ME);
			}
			
			for (Player member : dropNpc.getInRangePlayers())
			{
				if (member == null)
				{
					log.warn("member null Owner is in group? " + player.isInGroup2() + " Owner is in Alliance? " + player.isInAlliance2());
					continue;
				}
				
				final int teamId = member.getCurrentTeamId();
				PacketSendUtility.sendPacket(member, new SM_GROUP_LOOT(teamId, member.getObjectId(), itemId, npcId, dropNpc.getDistributionId(), bid, index));
				if (!player.equals(member) && member.isOnline())
				{
					if (bid > 0)
					{
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_PAY_RESULT_OTHER(player.getName()));
					}
					else
					{
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_PAY_GIVEUP_OTHER(player.getName()));
					}
				}
			}
			
			distributeLoot(player, bid, itemId, npcId);
		}
	}
	
	/**
	 * Handles the distribution of a specific loot item to a player.<br>
	 * This method updates the winning status and notifies nearby group members.<br>
	 * It also manages {@link PlayerMode} changes and loot group rules.
	 * @param player The {@code Player} who is currently interacting with the drop.
	 * @param luckyPlayer The highest bid or roll value associated with the item.
	 * @param itemId The unique identifier for the requested item.
	 * @param npcId The identifier of the NPC from which the loot dropped.
	 */
	private void distributeLoot(Player player, long luckyPlayer, int itemId, int npcId)
	{
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		final Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npcId);
		DropItem requestedItem = null;
		
		if (dropItems == null)
		{
			return;
		}
		
		synchronized (dropItems)
		{
			for (DropItem dropItem : dropItems)
			{
				if (dropItem.getIndex() == dropNpc.getCurrentIndex())
				{
					requestedItem = dropItem;
					break;
				}
			}
		}
		
		if (requestedItem == null)
		{
			return;
		}
		
		player.unsetPlayerMode(PlayerMode.IN_ROLL);
		
		// Removes player from ARRAY once they have rolled or bid
		if (dropNpc.containsPlayerStatus(player))
		{
			dropNpc.delPlayerStatus(player);
		}
		
		if (luckyPlayer > requestedItem.getHighestValue())
		{
			requestedItem.setHighestValue(luckyPlayer);
			requestedItem.setWinningPlayer(player);
		}
		
		if (!dropNpc.getPlayerStatus().isEmpty())
		{
			return;
		}
		
		if (player.isInGroup2() || player.isInAlliance2())
		{
			for (Player member : dropNpc.getInRangePlayers())
			{
				if (member == null)
				{
					continue;
				}
				
				if (requestedItem.getWinningPlayer() == null)
				{
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_PAY_ALL_GIVEUP);
				}
				
				final int teamId = member.getCurrentTeamId();
				PacketSendUtility.sendPacket(member, new SM_GROUP_LOOT(teamId, requestedItem.getWinningPlayer() != null ? requestedItem.getWinningPlayer().getObjectId() : 1, itemId, npcId, dropNpc.getDistributionId(), 0xFFFFFFFF, requestedItem.getIndex()));
			}
		}
		
		final LootGroupRules lgr = player.getLootGroupRules();
		if (lgr != null)
		{
			lgr.removeItemToBeDistributed(requestedItem);
		}
		
		// Check if there is a Winning Player registered if not all members must have passed...
		if (requestedItem.getWinningPlayer() == null)
		{
			requestedItem.isFreeForAll(true);
			if ((lgr != null) && !lgr.getItemsToBeDistributed().isEmpty())
			{
				DropService.getInstance().canDistribute(player, lgr.getItemsToBeDistributed().getFirst());
			}
			return;
		}
		
		requestedItem.isDistributeItem(true);
		DropService.getInstance().requestDropItem(player, npcId, dropNpc.getCurrentIndex());
		if ((lgr != null) && !lgr.getItemsToBeDistributed().isEmpty())
		{
			DropService.getInstance().canDistribute(player, lgr.getItemsToBeDistributed().getFirst());
		}
	}
	
	private static class SingletonHolder
	{
		protected static final DropDistributionService instance = new DropDistributionService();
	}
}
