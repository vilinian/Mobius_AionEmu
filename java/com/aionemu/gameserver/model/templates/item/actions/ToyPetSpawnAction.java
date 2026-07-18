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

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.KiskService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * Handles the logic for spawning a toy pet when an item is used.<br>
 * It utilizes the {@link SpawnEngine} to create the visual object in the current zone.<br>
 * This action allows players to interact with specific items to summon decorative pets.
 * @author Sarynth, Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ToyPetSpawnAction")
public class ToyPetSpawnAction extends AbstractItemAction
{
	@XmlAttribute
	protected int npcid;
	@XmlAttribute
	protected int time;
	
	/**
	 * Gets the unique identifier of the NPC.<br>
	 * This value is used to identify which NPC should be spawned.
	 * @return The {@code int} ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcid;
	}
	
	/**
	 * Retrieves the duration associated with this {@code AutoGroupType}.<br>
	 * The value is returned in milliseconds.
	 * @return the time value as an {@code int}
	 */
	public int getTime()
	{
		return time;
	}
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It checks flight state, instance status, kisk restrictions, and location.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		if (player.getFlyState() != 0)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_BINDSTONE_ITEM_WHILE_FLYING);
			return false;
		}
		
		if (player.isInInstance())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_FAR_FROM_NPC);
			return false;
		}
		
		if (KiskService.getInstance().haveKisk(player.getObjectId()) && CustomConfig.ENABLE_KISK_RESTRICTION)
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1390160));
			return false;
		}
		
		if (!isPutKiskZone(player))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_LOCATION);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Executes the action to spawn a toy pet.<br>
	 * This method handles the logic when a {@link Player} uses an item to summon a kisk.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		// ShowAction
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, parentItem.getObjectId(), parentItem.getItemId(), 10000, 0), true);
		final ItemUseObserver observer = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300427)); // Item use cancel
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 2), true);
			}
		};
		
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, parentItem.getObjectId(), parentItem.getItemId(), 0, 1), true);
				player.getObserveController().removeObserver(observer);
				
				// RemoveKisk
				if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
				{
					return;
				}
				
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(parentItem.getItemTemplate().getNameId())));
				final float x = player.getX();
				final float y = player.getY();
				final float z = player.getZ();
				final byte heading = (byte) ((player.getHeading() + 60) % 120);
				final int worldId = player.getWorldId();
				final int instanceId = player.getInstanceId();
				final SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcid, x, y, z, heading);
				
				final Kisk kisk = VisibleObjectSpawner.spawnKisk(spawn, instanceId, player);
				final Integer objOwnerId = player.getObjectId();
				
				// Schedule Despawn Action
				final Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable()
				{
					
					@Override
					public void run()
					{
						kisk.getController().onDelete();
					}
				}, 7200000);
				// Fixed 2 hours 2 * 60 * 60 * 1000
				kisk.getController().addTask(TaskId.DESPAWN, task);
				
				// ShowFinalAction is a bad idea.
				// player.getController().cancelUseItem();
				player.getController().cancelTask(TaskId.ITEM_USE);
				KiskService.getInstance().regKisk(kisk, objOwnerId);
				
				if (kisk.getMaxMembers() > 1)
				{
					kisk.getController().onDialogRequest(player);
				}
				else
				{
					KiskService.getInstance().onBind(kisk, player);
				}
			}
		}, 10000));
	}
	
	/**
	 * Checks if the player is in a valid area to place a kisk.<br>
	 * It verifies all zones at the current position.
	 * @param player The {@link Player} to check.
	 * @return {@code true} if all zones allow kisk placement, otherwise {@code false}.
	 */
	private boolean isPutKiskZone(Player player)
	{
		for (ZoneInstance zone : player.getPosition().getMapRegion().getZones(player))
		{
			if (!zone.canPutKisk())
			{
				return false;
			}
		}
		
		return true;
	}
}
