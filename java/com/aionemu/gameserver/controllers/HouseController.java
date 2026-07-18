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
package com.aionemu.gameserver.controllers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.housing.HouseType;
import com.aionemu.gameserver.model.templates.zone.ZoneInfo;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_HOUSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_RENDER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * Manages the logic and interactions for {@link House} objects in the game world.<br>
 * This controller handles house-related actions, updates, and synchronization with clients.
 * @author Rolandas
 */
public class HouseController extends VisibleObjectController<House>
{
	Map<Integer, ActionObserver> observed = new ConcurrentHashMap<>();
	
	/**
	 * This method registers a {@link Player} as an observer.<br>
	 * It creates a new {@code FlyRingObserver} for the target.<br>
	 * The observer is added to the player's observation controller.
	 * @param object The {@code VisibleObject} representing the player to observe.
	 */
	@Override
	public void see(VisibleObject object)
	{
		final Player p = (Player) object;
		final ActionObserver observer = new ActionObserver(ObserverType.MOVE);
		p.getObserveController().addObserver(observer);
		observed.put(p.getObjectId(), observer);
		AionServerPacket packet;
		if (getOwner().isInInstance())
		{
			packet = new SM_HOUSE_UPDATE(getOwner());
		}
		else
		{
			packet = new SM_HOUSE_RENDER(getOwner());
		}
		
		PacketSendUtility.sendPacket(p, packet);
		
		spawnObjects();
	}
	
	/**
	 * Removes a player from the observed list and updates their status.<br>
	 * If {@code isOutOfRange} is {@code true}, it triggers a move event.<br>
	 * It also sends a deletion packet if the owner is not in an instance.
	 * @param object The {@code VisibleObject} representing the player to remove.
	 * @param isOutOfRange Whether the player has moved out of range.
	 */
	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange)
	{
		final Player p = (Player) object;
		final ActionObserver observer = observed.remove(p.getObjectId());
		if (isOutOfRange)
		{
			observer.moved();
			if (!getOwner().isInInstance())
			{
				PacketSendUtility.sendPacket(p, new SM_DELETE_HOUSE(getOwner().getAddress().getId()));
			}
		}
		
		p.getObserveController().removeObserver(observer);
	}
	
	/**
	 * Spawns all objects registered to the house owner.<br>
	 * This method iterates through the {@code HouseObject} list.<br>
	 * It calls the {@code spawn()} method on each object.
	 */
	public void spawnObjects()
	{
		if (getOwner().getRegistry() != null)
		{
			for (HouseObject<?> obj : getOwner().getRegistry().getSpawnedObjects())
			{
				obj.spawn();
			}
		}
	}
	
	/**
	 * Updates the visual appearance of a house for all observers.<br>
	 * This method runs asynchronously using {@link ThreadPoolManager}.<br>
	 * It sends an {@code SM_HOUSE_UPDATE} packet to every player in the {@code observed} map.
	 */
	public void updateAppearance()
	{
		ThreadPoolManager.getInstance().execute(new Runnable()
		{
			@Override
			public void run()
			{
				for (int playerId : observed.keySet())
				{
					final Player player = World.getInstance().findPlayer(playerId);
					if (player == null)
					{
						continue;
					}
					
					PacketSendUtility.sendPacket(player, new SM_HOUSE_UPDATE(getOwner()));
				}
			}
		});
	}
	
	/**
	 * Sends the house appearance data to all observed players.<br>
	 * This method runs asynchronously using {@link ThreadPoolManager}.<br>
	 * It sends an {@code SM_HOUSE_RENDER} packet for each valid player found in the {@code observed} map.
	 */
	public void broadcastAppearance()
	{
		ThreadPoolManager.getInstance().execute(new Runnable()
		{
			@Override
			public void run()
			{
				for (int playerId : observed.keySet())
				{
					final Player player = World.getInstance().findPlayer(playerId);
					if (player == null)
					{
						continue;
					}
					
					PacketSendUtility.sendPacket(player, new SM_HOUSE_RENDER(getOwner()));
				}
			}
		});
	}
	
	/**
	 * Removes visitors from the house based on specific rules.<br>
	 * This method checks if players are inside the house zone and moves them out.<br>
	 * It handles exceptions for friends and sends system messages to the kicker.
	 * @param kicker The {@code Player} who initiates the kick action.
	 * @param kickFriends If {@code true}, it will not exclude friends from being kicked.
	 * @param onSettingsChange Determines if the move should trigger a settings change update.
	 */
	public void kickVisitors(Player kicker, boolean kickFriends, boolean onSettingsChange)
	{
		final List<ZoneInfo> zoneInfo = DataManager.ZONE_DATA.getZones().get(getOwner().getWorldId());
		for (ZoneInfo info : zoneInfo)
		{
			if (info.getZoneTemplate().getName().name().equals(getOwner().getName()))
			{
				for (Integer objId : observed.keySet())
				{
					if ((objId == getOwner().getOwnerId()) || (!kickFriends && (kicker != null) && (kicker.getFriendList().getFriend(objId) != null)))
					{
						continue;
					}
					
					final Player visitor = World.getInstance().findPlayer(objId);
					if (visitor != null)
					{
						if (visitor.isInsideZone(info.getZoneTemplate().getName()))
						{
							moveOutside(visitor, onSettingsChange);
						}
					}
				}
				break;
			}
		}
		
		if (kicker != null)
		{
			if (!kickFriends)
			{
				PacketSendUtility.sendPacket(kicker, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_ORDER_OUT_WITHOUT_FRIENDS);
			}
			else
			{
				PacketSendUtility.sendPacket(kicker, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_ORDER_OUT_ALL);
			}
		}
	}
	
	/**
	 * Teleports the {@code player} to a location outside of the house.<br>
	 * It determines the destination based on whether the house is a {@code STUDIO}.<br>
	 * Sends a system message depending on the value of {@code onSettingsChange}.
	 * @param player The {@code Player} object to be moved.
	 * @param onSettingsChange A boolean flag indicating if the move was caused by a settings change.
	 */
	private void moveOutside(Player player, boolean onSettingsChange)
	{
		if (getOwner().getHouseType() == HouseType.STUDIO)
		{
			final float x = getOwner().getAddress().getExitX();
			final float y = getOwner().getAddress().getExitY();
			final float z = getOwner().getAddress().getExitZ();
			TeleportService2.teleportTo(player, getOwner().getAddress().getExitMapId(), 1, x, y, z, player.getHeading(), TeleportAnimation.BEAM_ANIMATION);
		}
		else
		{
			final Npc sign = getOwner().getCurrentSign();
			final double radian = Math.toRadians(MathUtil.convertHeadingToDegree(sign.getHeading()));
			final float x = (float) (sign.getX() + (8 * Math.cos(radian)));
			final float y = (float) (sign.getY() + (8 * Math.sin(radian)));
			TeleportService2.teleportTo(player, getOwner().getWorldId(), 1, x, y, player.getZ() + 1, player.getHeading(), TeleportAnimation.BEAM_ANIMATION);
		}
		
		if (onSettingsChange)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CHANGE_OWNER);
		}
		else
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_REQUEST_OUT);
		}
	}
}
