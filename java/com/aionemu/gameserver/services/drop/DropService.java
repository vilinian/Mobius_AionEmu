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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.DropConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.DropNpc;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.InRoll;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.team2.common.legacy.LootRuleType;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_LOOT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_ITEMLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.services.item.ItemInfoService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemService.ItemUpdatePredicate;
import com.aionemu.gameserver.taskmanager.tasks.TemporaryTradeTimeTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * Manages the logic for item drops and loot distribution within the game world.<br>
 * This service handles how {@link Item} objects are generated from {@link Npc} deaths and managed by {@link DropNpc}.<br>
 * It coordinates interactions between players, loot rules, and inventory updates.
 * @author ATracer, xTz
 */
public class DropService
{
	private static final Logger log = LoggerFactory.getLogger(DropService.class);
	
	/**
	 * Retrieves the singleton instance of the {@link DropService}.<br>
	 * This provides a global access point to the drop management system.
	 * @return The active {@code DropService} instance.
	 */
	public static DropService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Schedules a free-for-all loot event for a specific NPC.<br>
	 * This method waits for {@code 240000} milliseconds before starting the event.<br>
	 * It updates the drop status and broadcasts a packet to nearby players.
	 * @param npcUniqueId The unique identifier of the NPC involved in the event.
	 */
	public void scheduleFreeForAll(int npcUniqueId)
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcUniqueId);
			if (dropNpc != null)
			{
				DropRegistrationService.getInstance().getDropRegistrationMap().get(npcUniqueId).startFreeForAll();
				final VisibleObject npc = World.getInstance().findVisibleObject(npcUniqueId);
				if ((npc != null) && npc.isSpawned())
				{
					PacketSendUtility.broadcastPacket(npc, new SM_LOOT_STATUS(npcUniqueId, 0));
				}
			}
		}, 240000);
	}
	
	/**
	 * Removes an {@link Npc} from the active drop registration system.<br>
	 * This method clears the NPC from both current and registered drop maps.
	 * @param npc The {@code Npc} object to unregister.
	 */
	public void unregisterDrop(Npc npc)
	{
		final Integer npcObjId = npc.getObjectId();
		final Map<Integer, DropNpc> dropRegmap = DropRegistrationService.getInstance().getDropRegistrationMap();
		DropRegistrationService.getInstance().getCurrentDropMap().remove(npcObjId);
		
		if (dropRegmap.containsKey(npcObjId))
		{
			dropRegmap.remove(npcObjId);
		}
	}
	
	/**
	 * Requests the list of items dropped by a specific NPC for a player.<br>
	 * This method checks if the {@code player} has permission to loot the NPC.<br>
	 * If successful, it sends the item list and updates the player state to looting.
	 * @param player The {@link Player} requesting the drop list.
	 * @param npcId The unique identifier of the NPC.
	 */
	public void requestDropList(Player player, int npcId)
	{
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if ((player == null) || (dropNpc == null))
		{
			return;
		}
		
		if (!dropNpc.containsKey(player.getObjectId()) && !dropNpc.isFreeForAll())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_LOOT_NO_RIGHT);
			return;
		}
		
		if (dropNpc.isBeingLooted())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_LOOT_FAIL_ONLOOTING);
			return;
		}
		
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npcId);
		
		if ((dropItems == null) || (dropItems.size() == 0))
		{
			dropItems = Collections.emptySet();
			ThreadPoolManager.getInstance().schedule(() ->
			{
				return;
			}, 350); // Blocks Loot for 350ms and return if DropList is empty
		}
		else
		{
			dropNpc.setBeingLooted(player);
			final VisibleObject visObj = World.getInstance().findVisibleObject(npcId);
			if (visObj instanceof Npc)
			{
				final Npc npc = ((Npc) visObj);
				final ScheduledFuture<?> decayTask = (ScheduledFuture<?>) npc.getController().cancelTask(TaskId.DECAY);
				if (decayTask != null)
				{
					final long reamingDecayTime = decayTask.getDelay(TimeUnit.MILLISECONDS);
					dropNpc.setReamingDecayTime(reamingDecayTime);
				}
			}
			
			PacketSendUtility.sendPacket(player, new SM_LOOT_ITEMLIST(npcId, dropItems, player));
			PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, 2));
			player.unsetState(CreatureState.ACTIVE);
			player.setState(CreatureState.LOOTING);
			player.setLootingNpcOid(npcId);
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, npcId), true);
		}
	}
	
	/**
	 * Closes the loot list for a specific player and NPC.<br>
	 * This method resets the {@code CreatureState} of the {@link Player}.<br>
	 * It also handles decay tasks and updates the loot status for the {@code npcId}.
	 * @param player The {@link Player} who is closing the drop list.
	 * @param npcId The unique identifier of the NPC being looted.
	 */
	public void closeDropList(Player player, int npcId)
	{
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if (dropNpc == null)
		{
			return;
		}
		
		player.unsetState(CreatureState.LOOTING);
		player.setState(CreatureState.ACTIVE);
		player.setLootingNpcOid(0);
		
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_LOOT, 0, npcId), true);
		
		if (dropNpc.getBeingLooted() != player)
		{
			return; // cheater :)
		}
		
		final Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npcId);
		dropNpc.setBeingLooted(null);
		
		final Npc npc = (Npc) World.getInstance().findVisibleObject(npcId);
		if (npc != null)
		{
			if ((dropItems == null) || dropItems.isEmpty())
			{
				npc.getController().onDelete();
				return;
			}
			
			final Future<?> decayTask = RespawnService.scheduleDecayTask(npc, dropNpc.getReamingDecayTime());
			npc.getController().addTask(TaskId.DECAY, decayTask);
			
			final LootGroupRules lootGrouRules = player.getLootGroupRules();
			if ((lootGrouRules != null) && (dropNpc.getInRangePlayers().size() > 1) && (dropNpc.getPlayersObjectId().size() == 1))
			{
				final LootRuleType lrt = lootGrouRules.getLootRule();
				if (lrt != LootRuleType.FREEFORALL)
				{
					for (Player member : dropNpc.getInRangePlayers())
					{
						if (member != null)
						{
							final Integer object = member.getObjectId();
							dropNpc.setPlayerObjectId(object);
						}
					}
					
					DropRegistrationService.getInstance().setItemsToWinner(dropItems, 0);
				}
			}
			
			if (dropNpc.isFreeForAll())
			{
				PacketSendUtility.broadcastPacket(npc, new SM_LOOT_STATUS(npcId, 0));
			}
			else
			{
				PacketSendUtility.broadcastPacket(player, new SM_LOOT_STATUS(npcId, 0), true, object -> dropNpc.containsKey(object.getObjectId()));
			}
		}
	}
	
	/**
	 * Checks if a {@link Player} is allowed to receive a specific {@link DropItem}.<br>
	 * This method validates loot distribution rules based on the player's group settings.<br>
	 * It returns {@code false} if the item is already being distributed or is restricted.<br>
	 * Otherwise, it returns {@code true} to allow the distribution.
	 * @param player The {@link Player} attempting to receive the item.
	 * @param requestedItem The {@link DropItem} that is being requested.
	 * @return {@code true} if the distribution is allowed, or {@code false} otherwise.
	 */
	public boolean canDistribute(Player player, DropItem requestedItem)
	{
		final int npcId = requestedItem.getNpcObj();
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if (dropNpc == null)
		{
			return false;
		}
		
		final int itemId = requestedItem.getDropTemplate().getItemId();
		final ItemQuality quality = ItemInfoService.getQuality(itemId);
		LootGroupRules lootGrouRules = player.getLootGroupRules();
		if (lootGrouRules == null)
		{
			return true;
		}
		
		if (itemId != 182400001)
		{
			lootGrouRules = player.getLootGroupRules();
			if (dropNpc.getGroupSize() > 1)
			{
				dropNpc.setDistributionId(lootGrouRules.getAutodistribution().getId());
				dropNpc.setDistributionType(lootGrouRules.getQualityRule(quality));
			}
			else
			{
				dropNpc.setDistributionId(0);
			}
			
			if ((dropNpc.getDistributionId() > 1) && dropNpc.getDistributionType())
			{
				final boolean containDropItem = lootGrouRules.containDropItem(requestedItem);
				if (lootGrouRules.getItemsToBeDistributed().isEmpty() || containDropItem)
				{
					dropNpc.setCurrentIndex(requestedItem.getIndex());
					for (Player member : dropNpc.getInRangePlayers())
					{
						final Player finalPlayer = World.getInstance().findPlayer(member.getObjectId());
						if ((finalPlayer != null) && finalPlayer.isOnline())
						{
							dropNpc.addPlayerStatus(finalPlayer);
							finalPlayer.setPlayerMode(PlayerMode.IN_ROLL, new InRoll(npcId, itemId, requestedItem.getIndex(), dropNpc.getDistributionId()));
							PacketSendUtility.sendPacket(finalPlayer, new SM_GROUP_LOOT(finalPlayer.getCurrentTeamId(), 0, itemId, npcId, dropNpc.getDistributionId(), 1, requestedItem.getIndex()));
						}
					}
					
					lootGrouRules.setPlayersInRoll(dropNpc.getInRangePlayers(), dropNpc.getDistributionId() == 2 ? 17000 : 32000, requestedItem.getIndex(), npcId);
					if (!containDropItem)
					{
						lootGrouRules.addItemToBeDistributed(requestedItem);
					}
					
					return false;
				}
				
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LOOT_ALREADY_DISTRIBUTING_ITEM(new DescriptionId(ItemInfoService.getNameId(itemId))));
				if (!containDropItem)
				{
					lootGrouRules.addItemToBeDistributed(requestedItem);
				}
				
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if a player is allowed to automatically loot a specific item.<br>
	 * This method validates the request against group rules and item quality.<br>
	 * It ensures that distribution logic follows the configured server settings.
	 * @param player The {@link Player} attempting to loot the item.
	 * @param requestedItem The {@link DropItem} being requested for auto-loot.
	 * @return {@code true} if the player can auto-loot the item, otherwise {@code false}.
	 */
	public boolean canAutoLoot(Player player, DropItem requestedItem)
	{
		final int npcId = requestedItem.getNpcObj();
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if (dropNpc == null)
		{
			return false;
		}
		
		final LootGroupRules lootGroupRules = player.getLootGroupRules();
		if (lootGroupRules == null)
		{
			return true;
		}
		
		final int itemId = requestedItem.getDropTemplate().getItemId();
		final ItemQuality quality = ItemInfoService.getQuality(itemId);
		if (itemId == 182400001)
		{
			return true;
		}
		
		int distId = lootGroupRules.getAutodistribution().getId();
		if (dropNpc.getGroupSize() <= 1)
		{
			distId = 0;
			dropNpc.setDistributionId(distId);
		}
		
		if ((distId > 1) && lootGroupRules.getQualityRule(quality))
		{
			boolean anyOnline = false;
			for (Player member : dropNpc.getInRangePlayers())
			{
				final Player finalPlayer = World.getInstance().findPlayer(member.getObjectId());
				if ((finalPlayer != null) && finalPlayer.isOnline())
				{
					anyOnline = true;
					break;
				}
			}
			
			return !anyOnline;
		}
		
		return true;
	}
	
	/**
	 * Requests a specific item to be dropped from an {@link Npc}.<br>
	 * This method identifies the item using its index in the drop list.<br>
	 * It defaults to manual looting by setting the auto-loot flag to {@code false}.
	 * @param player The {@link Player} making the request.
	 * @param npcId The unique identifier of the {@link Npc} owning the loot.
	 * @param itemIndex The position of the item in the drop list.
	 */
	public void requestDropItem(Player player, int npcId, int itemIndex)
	{
		requestDropItem(player, npcId, itemIndex, false);
	}
	
	/**
	 * Processes a request from a player to loot a specific item from an NPC.<br>
	 * This method validates the request and handles distribution rules based on group status.<br>
	 * It also manages inventory limits and special items like Kinah.
	 * @param player The {@link Player} making the loot request.
	 * @param npcId The unique identifier of the NPC containing the drops.
	 * @param itemIndex The index of the specific item in the drop list.
	 * @param autoLoot Set to {@code true} to automatically remove the item from the pool if it is successfully looted.
	 */
	public void requestDropItem(Player player, int npcId, int itemIndex, boolean autoLoot)
	{
		final Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npcId);
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		final Npc npcID = (Npc) World.getInstance().findVisibleObject(npcId);
		DropItem requestedItem = null;
		
		// drop was unregistered
		if ((dropItems == null) || (dropNpc == null))
		{
			return;
		}
		
		synchronized (dropItems)
		{
			for (DropItem dropItem : dropItems)
			{
				if (dropItem.getIndex() == itemIndex)
				{
					requestedItem = dropItem;
					break;
				}
			}
		}
		
		if (requestedItem == null)
		{
			log.warn("Null requested index item: " + itemIndex + " npcId: " + npcID.getNpcId() + " player: " + player.getObjectId());
			return;
		}
		
		// fix exploit
		if (!requestedItem.isDistributeItem() && !dropNpc.containsKey(player.getObjectId()) && !dropNpc.isFreeForAll())
		{
			return;
		}
		
		final int itemId = requestedItem.getDropTemplate().getItemId();
		final ItemTemplate item = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (requestedItem.getDropTemplate().getItemTemplate().hasLimitOne())
		{
			if ((player.getInventory().getFirstItemByItemId(itemId) != null) || (player.getStorage(StorageType.REGULAR_WAREHOUSE.getId()).getFirstItemByItemId(itemId) != null))
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CAN_NOT_GET_LORE_ITEM((new DescriptionId(item.getNameId()))));
				return;
			}
		}
		
		long currentDropItemCount = requestedItem.getCount();
		final ItemQuality quality = ItemInfoService.getQuality(itemId);
		LootGroupRules lootGrouRules = player.getLootGroupRules();
		if ((lootGrouRules != null) && !requestedItem.isDistributeItem() && !requestedItem.isFreeForAll())
		{
			if (lootGrouRules.containDropItem(requestedItem))
			{
				if (!autoLoot)
				{
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1390219));
				}
				return;
			}
			
			if (autoLoot && !canAutoLoot(player, requestedItem))
			{
				return;
			}
			
			requestedItem.setNpcObj(npcId);
			if (!canDistribute(player, requestedItem))
			{
				return;
			}
		}
		
		if (itemId == 182400001)
		{
			// Kinah
			lootGrouRules = player.getLootGroupRules();
			final Collection<Player> pList = new ArrayList<>();
			for (Player member : dropNpc.getInRangePlayers())
			{
				final Player finalPlayer = World.getInstance().findPlayer(member.getObjectId());
				if ((finalPlayer != null) && finalPlayer.isOnline() && !finalPlayer.isMentor())
				{
					pList.add(member);
				}
			}
			
			if (pList.size() > 1)
			{
				// distribute Kinah in Group same amount for all groupmembers
				final long kinahCountPerPlayer = (currentDropItemCount / pList.size());
				for (Player member : dropNpc.getInRangePlayers())
				{
					ItemService.addItem(member, itemId, kinahCountPerPlayer, ItemService.DEFAULT_UPDATE_PREDICATE);
				}
				
				currentDropItemCount = 0;
			}
			else
			{
				currentDropItemCount = ItemService.addItem(player, itemId, currentDropItemCount, ItemService.DEFAULT_UPDATE_PREDICATE);
			}
		}
		else if (!player.isInGroup2() && !player.isInAlliance2() && !requestedItem.isItemWonNotCollected() && (dropNpc.getDistributionId() == 0))
		{
			currentDropItemCount = ItemService.addItem(player, itemId, currentDropItemCount, ItemService.DEFAULT_UPDATE_PREDICATE);
			uniqueDropAnnounce(player, requestedItem);
		}
		
		if (autoLoot)
		{
			if (currentDropItemCount <= 0)
			{
				synchronized (dropItems)
				{
					dropItems.remove(requestedItem);
				}
			}
			else
			{
				requestedItem.setCount(currentDropItemCount);
			}
			
			if (dropItems.size() == 0)
			{
				final Npc npc = (Npc) World.getInstance().findVisibleObject(npcId);
				if (npc != null)
				{
					npc.getController().onDelete();
				}
			}
			return;
		}
		else if (!requestedItem.isDistributeItem())
		{
			if (player.isInGroup2() || player.isInAlliance2())
			{
				lootGrouRules = player.getLootGroupRules();
				if (lootGrouRules.isMisc(quality))
				{
					final Collection<Player> members = dropNpc.getInRangePlayers();
					
					if (members.size() > lootGrouRules.getNrMisc())
					{
						lootGrouRules.setNrMisc(lootGrouRules.getNrMisc() + 1);
					}
					else
					{
						lootGrouRules.setNrMisc(1);
					}
					
					int i = 0;
					for (Player p : members)
					{
						i++;
						if (i == lootGrouRules.getNrMisc())
						{
							requestedItem.setWinningPlayer(p);
							break;
						}
					}
				}
				else
				{
					requestedItem.setWinningPlayer(player);
				}
			}
			else if (requestedItem.getWinningPlayer() == null)
			{
				requestedItem.setWinningPlayer(player);
			}
			
			if (requestedItem.getWinningPlayer() != null)
			{
				currentDropItemCount = ItemService.addItem(requestedItem.getWinningPlayer(), itemId, currentDropItemCount, new TempTradeDropPredicate(dropNpc));
				
				winningNormalActions(player, npcId, requestedItem);
				uniqueDropAnnounce(player, requestedItem);
			}
		}
		
		// handles distribution of item to correct player and messages accordingly
		if (requestedItem.isDistributeItem())
		{
			if ((player != requestedItem.getWinningPlayer()) && requestedItem.isItemWonNotCollected())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LOOT_ANOTHER_OWNER_ITEM);
				return;
			}
			else if (requestedItem.getWinningPlayer().getInventory().isFull(requestedItem.getDropTemplate().getItemTemplate().getExtraInventoryId()))
			{
				PacketSendUtility.sendPacket(requestedItem.getWinningPlayer(), SM_SYSTEM_MESSAGE.STR_MSG_DICE_INVEN_ERROR);
				requestedItem.isItemWonNotCollected(true);
				return;
			}
			
			currentDropItemCount = ItemService.addItem(requestedItem.getWinningPlayer(), itemId, currentDropItemCount, new TempTradeDropPredicate(dropNpc));
			
			switch (dropNpc.getDistributionId())
			{
				case 2:
					winningRollActions(requestedItem.getWinningPlayer(), itemId, npcId);
					break;
				case 3:
					winningBidActions(requestedItem.getWinningPlayer(), npcId, requestedItem.getHighestValue());
			}
			
			uniqueDropAnnounce(player, requestedItem);
		}
		
		if (currentDropItemCount <= 0)
		{
			synchronized (dropItems)
			{
				dropItems.remove(requestedItem);
			}
		}
		else
		{
			requestedItem.setCount(currentDropItemCount);
		}
		
		resendDropList(dropNpc.getBeingLooted(), npcId, dropItems);
	}
	
	/**
	 * Sends the updated loot list to a specific player.<br>
	 * This method handles both active loot lists and empty loot states.<br>
	 * If the set is empty, it clears the looting state for the {@code Player}.
	 * @param player The {@link Player} who will receive the packet.
	 * @param npcId The unique identifier of the {@link Npc} being looted.
	 * @param dropItems A {@link Set} containing the items to be displayed.
	 */
	private void resendDropList(Player player, int npcId, Set<DropItem> dropItems)
	{
		final Npc npc = (Npc) World.getInstance().findVisibleObject(npcId);
		if (dropItems.size() != 0)
		{
			if (player != null)
			{
				PacketSendUtility.sendPacket(player, new SM_LOOT_ITEMLIST(npcId, dropItems, player));
			}
		}
		else
		{
			if (player != null)
			{
				PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, 3));
				player.unsetState(CreatureState.LOOTING);
				player.setState(CreatureState.ACTIVE);
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_LOOT, 0, npcId), true);
			}
			
			if (npc != null)
			{
				npc.getController().onDelete();
			}
		}
	}
	
	/**
	 * Executes actions for a player who won a roll.<br>
	 * It sends a system message to the winner.<br>
	 * It also notifies nearby group or alliance members about the win.
	 * @param player The {@code Player} who won the item.
	 * @param itemId The unique identifier of the {@code Item}.
	 * @param npcId The unique identifier of the {@code Npc} associated with the drop.
	 */
	private void winningRollActions(Player player, int itemId, int npcId)
	{
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LOOT_GET_ITEM_ME(new DescriptionId(ItemInfoService.getNameId(itemId))));
		
		if (player.isInGroup2() || player.isInAlliance2())
		{
			for (Player member : DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId).getInRangePlayers())
			{
				if ((member != null) && !player.equals(member) && member.isOnline())
				{
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_LOOT_GET_ITEM_OTHER(player.getName(), new DescriptionId(ItemInfoService.getNameId(itemId))));
				}
			}
		}
	}
	
	/**
	 * Handles the actions for a player who won a bid.<br>
	 * It deducts the {@code highestValue} from the winner's inventory.<br>
	 * If the player is in a group or alliance, it distributes the amount to other members.
	 * @param player The {@link Player} who won the bid.
	 * @param npcId The unique identifier for the {@link Npc}.
	 * @param highestValue The total amount of Kinah involved in the bid.
	 */
	private void winningBidActions(Player player, int npcId, long highestValue)
	{
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if (highestValue > 0)
		{
			if (!player.getInventory().tryDecreaseKinah(highestValue))
			{
				return;
			}
			
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PAY_ACCOUNT_ME(highestValue));
		}
		
		if (player.isInGroup2() || player.isInAlliance2())
		{
			for (Player member : dropNpc.getInRangePlayers())
			{
				if ((member != null) && !player.equals(member) && member.isOnline())
				{
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_PAY_ACCOUNT_OTHER(player.getName(), highestValue));
					final long distributeKinah = highestValue / (dropNpc.getGroupSize() - 1);
					member.getInventory().increaseKinah(distributeKinah);
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_PAY_DISTRIBUTE(highestValue, dropNpc.getGroupSize() - 1, distributeKinah));
				}
			}
		}
	}
	
	/**
	 * Handles actions for a player who won a normal drop.<br>
	 * This method sends a system message to party members in Group 2 or Alliance 2.<br>
	 * It notifies them about the item obtained by the winning player.
	 * @param player The {@code Player} object of the winner.
	 * @param npcId The unique identifier for the {@code Npc}.
	 * @param requestedItem The {@code DropItem} that was won.
	 */
	private void winningNormalActions(Player player, int npcId, DropItem requestedItem)
	{
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if ((player == null) || (dropNpc == null))
		{
			return;
		}
		
		final int itemId = requestedItem.getDropTemplate().getItemId();
		if (player.isInGroup2() || player.isInAlliance2())
		{
			for (Player member : dropNpc.getInRangePlayers())
			{
				if ((member != null) && !requestedItem.getWinningPlayer().equals(member) && member.isOnline())
				{
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_GET_ITEM_PARTYNOTICE(requestedItem.getWinningPlayer().getName(), new DescriptionId(ItemInfoService.getNameId(itemId))));
				}
			}
		}
	}
	
	/**
	 * Checks if a {@link Player} can see the loot from an {@link Npc}.<br>
	 * It sends a status packet to the player if they are already involved or if it is a free-for-all.
	 * @param player The {@link Player} who is viewing the loot.
	 * @param owner The {@link Npc} that owns the drop.
	 */
	public void see(Player player, Npc owner)
	{
		final int id = owner.getObjectId();
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(id);
		
		if (dropNpc == null)
		{
			return;
		}
		
		if (dropNpc.containsKey(player.getObjectId()) || dropNpc.isFreeForAll())
		{
			ThreadPoolManager.getInstance().schedule(() -> PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(id, 0)), 5000);
		}
	}
	
	/**
	 * Announces a unique item drop to all nearby players.<br>
	 * This method checks if the announcement is enabled in {@code DropConfig}.<br>
	 * It only sends messages for high-quality items like {@code UNIQUE} or {@code EPIC}.<br>
	 * The message is sent to players in the same map and instance.
	 * @param player The player who triggered the drop action.
	 * @param requestedItem The item that was successfully dropped.
	 */
	private void uniqueDropAnnounce(Player player, DropItem requestedItem)
	{
		if (DropConfig.ENABLE_UNIQUE_DROP_ANNOUNCE && !player.getInventory().isFull(requestedItem.getDropTemplate().getItemTemplate().getExtraInventoryId()))
		{
			final ItemTemplate itemTemplate = ItemInfoService.getItemTemplate(requestedItem.getDropTemplate().getItemId());
			switch (itemTemplate.getItemQuality())
			{
				case RARE:
				case LEGEND:
				case UNIQUE:
				case EPIC:
				case MYTHIC:
				case ANCIENT:
				case RELIC:
				case FINALITY:
					final String lastGetName = requestedItem.getWinningPlayer() != null ? requestedItem.getWinningPlayer().getName() : player.getName();
					final int pObjectId = player.getObjectId();
					final int pRaceId = player.getRace().getRaceId();
					final int pMapId = player.getWorldId();
					final int pInstance = player.isInInstance() ? player.getInstanceId() : 0;
					
					World.getInstance().doOnAllPlayers(other ->
					{
						
						final int oObjectId = other.getObjectId();
						final int oRaceId = other.getRace().getRaceId();
						final int oMapId = other.getWorldId();
						final int oInstance = other.isInInstance() ? other.getInstanceId() : 0;
						
						if ((oObjectId != pObjectId) && other.isSpawned() && (oRaceId == pRaceId) && (oMapId == pMapId) && (oInstance == pInstance))
						{
							PacketSendUtility.sendPacket(other, new SM_SYSTEM_MESSAGE(1390003, lastGetName, "[item: " + requestedItem.getDropTemplate().getItemId() + "]"));
						}
					});
					break;
				default:
					break;
			}
		}
	}
	
	private static final class TempTradeDropPredicate extends ItemUpdatePredicate
	{
		private final DropNpc dropNpc;
		
		private TempTradeDropPredicate(DropNpc dropNpc)
		{
			this.dropNpc = dropNpc;
		}
		
		@Override
		public boolean changeItem(Item input)
		{
			if (dropNpc.getPlayersObjectId().size() > 1)
			{
				final ItemTemplate template = input.getItemTemplate();
				if (template.getTempExchangeTime() != 0)
				{
					input.setTemporaryExchangeTime((int) (System.currentTimeMillis() / 1000) + (template.getTempExchangeTime() * 60));
					TemporaryTradeTimeTask.getInstance().addTask(input, dropNpc.getPlayersObjectId());
				}
				
				return true;
			}
			
			return false;
		}
	}
	
	private static class SingletonHolder
	{
		protected static final DropService instance = new DropService();
	}
}
