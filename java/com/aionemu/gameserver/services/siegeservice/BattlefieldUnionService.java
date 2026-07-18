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
package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BATTLEFIELD_UNION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BATTLEFIELD_UNION_REGISTER;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * Manages the logic for battlefield unions in the game world.<br>
 * This service handles player registration and synchronization for specific battle zones.<br>
 * It facilitates communication between players using {@link SM_BATTLEFIELD_UNION} packets.
 */
public class BattlefieldUnionService
{
	/**
	 * Handles logic when a {@link Player} enters the world.<br>
	 * It checks if the player is in the Reshanta map.<br>
	 * If so, it sends an {@code SM_BATTLEFIELD_UNION} packet to the player.
	 * @param player The {@link Player} object that entered the world.
	 */
	public void onEnterWorld(Player player)
	{
		if (player.getWorldId() == WorldMapType.RESHANTA.getId())
		{
			PacketSendUtility.sendPacket(player, new SM_BATTLEFIELD_UNION(1132, true, 0));
		}
	}
	
	/**
	 * Handles the registration of a player into a battlefield union.<br>
	 * This method sends the necessary packets to the {@code Player}.
	 * @param player The {@link Player} who is registering.
	 * @param requestId The unique identifier for the requested union.
	 */
	@SuppressWarnings("unused")
	public void onRegister(Player player, int requestId)
	{
		if (!false)
		{
			PacketSendUtility.sendPacket(player, new SM_BATTLEFIELD_UNION_REGISTER(requestId, true));
			PacketSendUtility.sendPacket(player, new SM_BATTLEFIELD_UNION(1132, true, 2));
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_BATTLEFIELD_UNION_REGISTER(requestId, false));
			PacketSendUtility.sendPacket(player, new SM_BATTLEFIELD_UNION(1132, true, 0));
		}
	}
	
	/**
	 * Provides the global instance of the {@link BattlefieldUnionService}.<br>
	 * Use this method to access the service from anywhere in the code.<br>
	 * This follows the singleton design pattern.
	 * @return The single instance of {@code BattlefieldUnionService}.
	 */
	public static BattlefieldUnionService getInstance()
	{
		return NewSingletonHolder.INSTANCE;
	}
	
	private static class NewSingletonHolder
	{
		private static final BattlefieldUnionService INSTANCE = new BattlefieldUnionService();
	}
}
