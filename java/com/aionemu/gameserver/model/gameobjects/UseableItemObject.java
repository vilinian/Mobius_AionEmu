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
package com.aionemu.gameserver.model.gameobjects;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import java.time.ZonedDateTime;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.housing.HousingUseableItem;
import com.aionemu.gameserver.model.templates.housing.LimitType;
import com.aionemu.gameserver.model.templates.housing.UseItemAction;
import com.aionemu.gameserver.network.PacketWriteHelper;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_EDIT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_OBJECT_USE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Represents an interactive object within a house that can be used by players.<br>
 * This class handles the logic for executing actions defined in a {@link HousingUseableItem} template.
 * @author Rolandas
 */
public class UseableItemObject extends HouseObject<HousingUseableItem>
{
	private volatile boolean mustGiveLastReward = false;
	private final AtomicReference<Player> usingPlayer = new AtomicReference<>();
	private UseDataWriter entryWriter = null;
	
	/**
	 * Creates a new instance of a {@link UseableItemObject}.<br>
	 * This constructor initializes the object with its owner and unique identifiers.<br>
	 * It also checks if the item should provide a final reward based on its template action.
	 * @param owner The {@link House} that owns this object.
	 * @param objId The unique instance ID of the object.
	 * @param templateId The ID of the template used to create this object.
	 */
	public UseableItemObject(House owner, int objId, int templateId)
	{
		super(owner, objId, templateId);
		final UseItemAction action = getObjectTemplate().getAction();
		if ((action != null) && (action.getFinalRewardId() != null) && (getUseSecondsLeft() == 0))
		{
			mustGiveLastReward = true;
		}
		
		entryWriter = new UseDataWriter(this);
	}
	
	static class UseDataWriter extends PacketWriteHelper
	{
		UseableItemObject obj;
		
		public UseDataWriter(UseableItemObject obj)
		{
			this.obj = obj;
		}
		
		@Override
		protected void writeMe(ByteBuffer buffer)
		{
			if (obj.getObjectTemplate().getUseCount() != null)
			{
				writeD(buffer, obj.getOwnerUsedCount() + obj.getVisitorUsedCount());
			}
			else
			{
				writeD(buffer, 0);
			}
			
			int checkType = 0;
			final UseItemAction action = obj.getObjectTemplate().getAction();
			if ((action != null) && (action.getCheckType() != null))
			{
				checkType = action.getCheckType();
			}
			
			writeC(buffer, checkType);
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} interacts with this object.<br>
	 * This method validates requirements such as ownership, cooldowns, and inventory space.<br>
	 * It processes rewards, updates usage counts, and manages object destruction or expiration.
	 * @param player The {@code Player} who used the object.
	 */
	@Override
	public void onUse(Player player)
	{
		if (!usingPlayer.compareAndSet(null, player))
		{
			// The same player is using, return. It might be double-click
			if (usingPlayer.compareAndSet(player, player))
			{
				return;
			}
			
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_OCCUPIED_BY_OTHER);
			return;
		}
		
		final boolean isOwner = getOwnerHouse().getOwnerId() == player.getObjectId();
		if (getObjectTemplate().isOwnerOnly() && !isOwner)
		{
			warnAndRelease(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_IS_ONLY_FOR_OWNER_VALID);
			return;
		}
		
		final boolean cdExpired = player.getHouseObjectCooldownList().isCanUseObject(getObjectId());
		if (!cdExpired)
		{
			if ((getObjectTemplate().getCd() != null) && (getObjectTemplate().getCd() != 0))
			{
				warnAndRelease(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANNOT_USE_FLOWERPOT_COOLTIME);
				return;
			}
			
			if (getObjectTemplate().isOwnerOnly() && isOwner)
			{
				warnAndRelease(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_CANT_USE_PER_DAY);
				return;
			}
		}
		
		final UseItemAction action = getObjectTemplate().getAction();
		if (action == null)
		{
			// Some objects do not have actions; they are test items now
			warnAndRelease(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_ALL_CANT_USE);
			return;
		}
		
		if (getObjectTemplate().getPlacementLimit() == LimitType.COOKING)
		{
			// Check if player already has an item
			if (player.getInventory().getItemCountByItemId(action.getRewardId()) > 0)
			{
				final int nameId = DataManager.ITEM_DATA.getItemTemplate(action.getRewardId()).getNameId();
				final SM_SYSTEM_MESSAGE msg = SM_SYSTEM_MESSAGE.STR_MSG_CANNOT_USE_ALREADY_HAVE_REWARD_ITEM(nameId, getObjectTemplate().getNameId());
				warnAndRelease(player, msg);
				return;
			}
		}
		
		final Integer useCount = getObjectTemplate().getUseCount();
		int currentUseCount = 0;
		
		if (useCount != null)
		{
			// Counter is for both, but could be made custom from configs
			currentUseCount = getOwnerUsedCount() + getVisitorUsedCount();
			if (((currentUseCount >= useCount) && !isOwner) || ((currentUseCount > useCount) && isOwner))
			{
				// If an expiration is set and the inventory is full, the final reward must be given to the owner only; otherwise, the object would have despawned before reaching this check.
				if (!mustGiveLastReward || !isOwner)
				{
					warnAndRelease(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_ACHIEVE_USE_COUNT);
					return;
				}
			}
		}
		
		final Integer requiredItem = getObjectTemplate().getRequiredItem();
		
		if (mustGiveLastReward && !isOwner)
		{
			// Expired, waiting owner
			final int descId = DataManager.ITEM_DATA.getItemTemplate(requiredItem).getNameId();
			warnAndRelease(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_DELETE_EXPIRE_TIME(descId));
			return;
		}
		else if (requiredItem != null)
		{
			final int checkType = action.getCheckType();
			if (checkType == 1)
			{
				// equip item needed
				final List<Item> items = player.getEquipment().getEquippedItemsByItemId(requiredItem);
				if (items.size() == 0)
				{
					final int descId = DataManager.ITEM_DATA.getItemTemplate(requiredItem).getNameId();
					warnAndRelease(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_USE_HOUSE_OBJECT_ITEM_EQUIP(new DescriptionId(descId)));
					return;
				}
			}
			else if (player.getInventory().getItemCountByItemId(requiredItem) < action.getRemoveCount())
			{
				final int descId = DataManager.ITEM_DATA.getItemTemplate(requiredItem).getNameId();
				warnAndRelease(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_USE_HOUSE_OBJECT_ITEM_CHECK(new DescriptionId(descId)));
				return;
			}
		}
		
		if (player.getInventory().isFull())
		{
			warnAndRelease(player, SM_SYSTEM_MESSAGE.STR_WAREHOUSE_TOO_MANY_ITEMS_INVENTORY);
			return;
		}
		
		final int delay = getObjectTemplate().getDelay();
		final int ownerId = getOwnerHouse().getOwnerId();
		final int usedCount = useCount == null ? 0 : currentUseCount + 1;
		final ItemUseObserver observer = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 0, 9));
				player.getObserveController().removeObserver(this);
				warnAndRelease(player, null);
			}
			
			@Override
			public void itemused(Item item)
			{
				abort();
			}
		};
		
		player.getObserveController().attach(observer);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_USE(getObjectTemplate().getNameId()));
		PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), delay, 8));
		player.getController().addTask(TaskId.HOUSE_OBJECT_USE, ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 0, 9));
					final Integer removeCount = action.getRemoveCount();
					if ((requiredItem != null) && (removeCount != null) && (removeCount != 0))
					{
						player.getInventory().decreaseByItemId(requiredItem, removeCount);
					}
					
					final UseableItemObject myself = UseableItemObject.this;
					int rewardId = 0;
					boolean delete = false;
					
					if (useCount != null)
					{
						if ((action.getFinalRewardId() != null) && ((useCount + 1) == usedCount))
						{
							// visitors do not get final rewards
							rewardId = action.getFinalRewardId();
							ItemService.addItem(player, rewardId, 1);
							delete = true;
						}
						else if (action.getRewardId() != null)
						{
							rewardId = action.getRewardId();
							ItemService.addItem(player, rewardId, 1);
							if (useCount == usedCount)
							{
								PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_FLOWERPOT_GOAL(myself.getObjectTemplate().getNameId()));
								if (action.getFinalRewardId() == null)
								{
									delete = true;
								}
								else
								{
									myself.setMustGiveLastReward(true);
									myself.setExpireTime((int) (System.currentTimeMillis() / 1000));
									myself.setPersistentState(PersistentState.UPDATE_REQUIRED);
								}
							}
						}
					}
					else if (action.getRewardId() != null)
					{
						rewardId = action.getRewardId();
						ItemService.addItem(player, rewardId, 1);
					}
					
					if (usedCount > 0)
					{
						if (!delete)
						{
							if (player.getObjectId() == ownerId)
							{
								myself.incrementOwnerUsedCount();
							}
							else
							{
								myself.incrementVisitorUsedCount();
							}
						}
						
						PacketSendUtility.broadcastPacket(player, new SM_OBJECT_USE_UPDATE(player.getObjectId(), ownerId, usedCount, myself), true);
					}
					
					if (rewardId > 0)
					{
						final int rewardNameId = DataManager.ITEM_DATA.getItemTemplate(rewardId).getNameId();
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_REWARD_ITEM(myself.getObjectTemplate().getNameId(), rewardNameId));
					}
					
					if (delete)
					{
						selfDestroy(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_DELETE_USE_COUNT_FINAL(getObjectTemplate().getNameId()));
					}
					else
					{
						Integer cd = myself.getObjectTemplate().getCd();
						ZonedDateTime repeatDate;
						if ((cd == null) || (cd == 0))
						{
							// use once per day
							final ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);
							repeatDate = ZonedDateTime.of(tomorrow.getYear(), tomorrow.getMonthValue(), tomorrow.getDayOfMonth(), 0, 0, 0, 0, java.time.ZoneId.systemDefault());
							cd = (int) (repeatDate.toInstant().toEpochMilli() - ZonedDateTime.now().toInstant().toEpochMilli()) / 1000;
						}
						
						player.getHouseObjectCooldownList().addHouseObjectCooldown(myself.getObjectId(), cd);
					}
				}
				finally
				{
					player.getObserveController().removeObserver(observer);
					warnAndRelease(player, null);
				}
			}
		}, delay));
	}
	
	/**
	 * Sends a warning message to the player.<br>
	 * Releases the object so other players can use it.
	 * @param player The {@link Player} who is currently using the object.
	 * @param systemMessage The {@code SM_SYSTEM_MESSAGE} to send to the player.
	 */
	private void warnAndRelease(Player player, SM_SYSTEM_MESSAGE systemMessage)
	{
		if (systemMessage != null)
		{
			PacketSendUtility.sendPacket(player, systemMessage);
		}
		
		usingPlayer.set(null);
	}
	
	/**
	 * Removes this object from the game world.<br>
	 * Sends a notification to the player.<br>
	 * Cleans up internal data and triggers deletion logic.
	 * @param player The {@code Player} who interacted with the object.
	 * @param message The {@code SM_SYSTEM_MESSAGE} to display to the player.
	 */
	private void selfDestroy(Player player, SM_SYSTEM_MESSAGE message)
	{
		PacketSendUtility.sendPacket(player, new SM_HOUSE_EDIT(7, 0, getObjectId()));
		getController().onDelete();
		clearKnownlist();
		PacketSendUtility.sendPacket(player, new SM_HOUSE_EDIT(4, 1, getObjectId()));
		PacketSendUtility.sendPacket(player, message);
		super.expireEnd(player);
	}
	
	/**
	 * Sets whether the object should always grant a reward on its final use.<br>
	 * This property determines if the last available reward is forced to be given.
	 * @param mustGiveLastReward The boolean value to set for this behavior.
	 */
	public void setMustGiveLastReward(boolean mustGiveLastReward)
	{
		this.mustGiveLastReward = mustGiveLastReward;
	}
	
	/**
	 * Handles the expiration logic for a house object.<br>
	 * It sends a system message to the {@link Player}.<br>
	 * If the object was spawned by the player, it is destroyed.<br>
	 * Otherwise, it updates the object state and notifies the client.
	 * @param player The {@link Player} who triggered the expiration.
	 */
	@Override
	public void expireEnd(Player player)
	{
		final int descId = getObjectTemplate().getNameId();
		final SM_SYSTEM_MESSAGE msg = SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_DELETE_EXPIRE_TIME(descId);
		if (isSpawnedByPlayer())
		{
			selfDestroy(player, msg);
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_HOUSE_EDIT(4, 1, getObjectId()));
			PacketSendUtility.sendPacket(player, msg);
			super.expireEnd(player);
		}
	}
	
	/**
	 * Checks if the object is allowed to expire at this moment.<br>
	 * It returns {@code false} if a reward must still be given or if a player is currently using it.
	 * @return {@code true} if the object can expire now, otherwise {@code false}.
	 */
	@Override
	public boolean canExpireNow()
	{
		if (mustGiveLastReward)
		{
			return false;
		}
		
		return usingPlayer.get() == null;
	}
	
	/**
	 * Writes the usage data to a provided {@code ByteBuffer}.<br>
	 * This method calls the internal {@code UseDataWriter} to perform the operation.
	 * @param buffer The {@code ByteBuffer} where the data will be written.
	 */
	public void writeUsageData(ByteBuffer buffer)
	{
		entryWriter.writeMe(buffer);
	}
}
