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
package com.aionemu.gameserver.services.teleport;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOTSPOT_TELEPORT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This service handles the logic for teleporting players to specific hotspots.<br>
 * It manages the movement of {@link Player} objects and sends the corresponding {@code SM_HOTSPOT_TELEPORT} packets.
 */
public class HotspotTeleportService
{
	int delay = CustomConfig.HOTSPOT_TELEPORT_DELAY; // 10 Seconds = 10000 Milliseconds
	
	/**
	 * Retrieves the singleton instance of the {@link HotspotTeleportService}.<br>
	 * Use this method to access the teleport service from anywhere in the code.
	 * @return The active {@code HotspotTeleportService} instance.
	 */
	public static HotspotTeleportService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Teleports a {@link Player} to a specific hotspot location.<br>
	 * This method handles the delay, price deduction, and safety checks during the process.<br>
	 * It cancels the teleport if the player is attacked or enters an abnormal state.
	 * @param player The {@link Player} who will be moved.
	 * @param teleportId The unique identifier for the destination hotspot.
	 * @param price The amount of Kinah to deduct from the player's inventory.
	 */
	public void doTeleport(Player player, int teleportId, int price)
	{
		final int worldId = DataManager.HOTSPOT_TELEPORTER_DATA.getHotspotTemplate(teleportId).getMapId();
		final float getX = DataManager.HOTSPOT_TELEPORTER_DATA.getHotspotTemplate(teleportId).getX();
		final float getY = DataManager.HOTSPOT_TELEPORTER_DATA.getHotspotTemplate(teleportId).getY();
		final float getZ = DataManager.HOTSPOT_TELEPORTER_DATA.getHotspotTemplate(teleportId).getZ();
		final int cooldown = CustomConfig.HOTSPOT_TELEPORT_COOLDOWN_DELAY; // 1 Minute = 60 Seconds
		
		player.getController().addTask(TaskId.HOTSPOT_TELEPORT, ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_HOTSPOT_TELEPORT(3, player.getObjectId(), teleportId));
				player.getController().addTask(TaskId.HOTSPOT_TELEPORT, ThreadPoolManager.getInstance().schedule(new Runnable()
				{
					
					@Override
					public void run()
					{
						TeleportService2.teleportTo(player, worldId, getX, getY, getZ);
						player.getInventory().decreaseKinah(price);
						PacketSendUtility.sendPacket(player, new SM_HOTSPOT_TELEPORT(player, 3, teleportId, cooldown));
					}
				}, 1000));
				final ActionObserver attackedObserver = new ActionObserver(ObserverType.ATTACKED)
				{
					
					@Override
					public void attacked(Creature creature)
					{
						player.getController().cancelTask(TaskId.HOTSPOT_TELEPORT);
					}
				};
				player.getObserveController().addObserver(attackedObserver);
				player.setHotTeleObservers(attackedObserver);
				final ActionObserver rideObserver = new ActionObserver(ObserverType.ABNORMALSETTED)
				{
					
					@Override
					public void abnormalsetted(AbnormalState state)
					{
						if (state.getId() > 0)
						{
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402444));
							player.getController().cancelTask(TaskId.HOTSPOT_TELEPORT);
						}
					}
				};
				player.getObserveController().addObserver(rideObserver);
				player.setHotTeleObservers(rideObserver);
				final ActionObserver dotAttackedObserver = new ActionObserver(ObserverType.DOT_ATTACKED)
				{
					
					@Override
					public void dotattacked(Creature creature, Effect dotEffect)
					{
						player.getController().cancelTask(TaskId.HOTSPOT_TELEPORT);
					}
				};
				player.getObserveController().addObserver(dotAttackedObserver);
				player.setHotTeleObservers(dotAttackedObserver);
			}
		}, delay));
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_HOTSPOT_TELEPORT(1, player.getObjectId(), teleportId));
	}
	
	private static class SingletonHolder
	{
		protected static final HotspotTeleportService instance = new HotspotTeleportService();
	}
}
