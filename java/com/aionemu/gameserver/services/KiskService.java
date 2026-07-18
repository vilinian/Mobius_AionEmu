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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BIND_POINT_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service manages the logic and interactions for {@link Kisk} objects in the game world.<br>
 * It handles various actions related to kisks and provides necessary updates to {@link Player} entities.
 * @author Sarynth, nrg
 */
public class KiskService
{
	private static final KiskService instance = new KiskService();
	private final Map<Integer, Kisk> boundButOfflinePlayer = new ConcurrentHashMap<>();
	private final Map<Integer, Kisk> ownerPlayer = new ConcurrentHashMap<>();
	
	/**
	 * Removes a {@link Kisk} from the system.<br>
	 * This method clears all offline player binds associated with the kisk.<br>
	 * It also removes the kisk from the owner map and updates all members.<br>
	 * Members are notified to clear their bind point information.
	 * @param kisk The {@code Kisk} object to be removed.
	 */
	public void removeKisk(Kisk kisk)
	{
		// remove offline binds
		for (int memberId : kisk.getCurrentMemberIds())
		{
			boundButOfflinePlayer.remove(memberId);
		}
		
		for (Integer obj : ownerPlayer.keySet())
		{
			if (ownerPlayer.get(obj).equals(kisk))
			{
				ownerPlayer.remove(obj);
				break;
			}
		}
		
		// send players SET_BIND_POINT and send them die packet again, if they lie dead, but are still not revived
		for (Player member : kisk.getCurrentMemberList())
		{
			member.setKisk(null);
			PacketSendUtility.sendPacket(member, new SM_BIND_POINT_INFO(0, 0f, 0f, 0f, member));
			if (member.getLifeStats().isAlreadyDead())
			{
				member.getController().sendDie();
			}
		}
	}
	
	/**
	 * Handles the binding process between a {@link Kisk} and a {@link Player}.<br>
	 * This method updates the kisk ownership.<br>
	 * It also sends necessary network packets to the player.
	 * @param kisk The {@code Kisk} object being bound.
	 * @param player The {@code Player} who is performing the bind.
	 */
	public void onBind(Kisk kisk, Player player)
	{
		if (player.getKisk() != null)
		{
			player.getKisk().removePlayer(player);
		}
		
		kisk.addPlayer(player);
		
		// Send Bind Point Data
		TeleportService2.sendSetBindPoint(player);
		
		// Send System Message
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_BINDSTONE_REGISTER);
		
		// Send Animated Bind Flash
		PacketSendUtility.broadcastPacket(player, new SM_LEVEL_UPDATE(player.getObjectId(), 2, player.getCommonData().getLevel()), true);
	}
	
	/**
	 * Handles the logic for re-binding a {@code Kisk} to a player who is logging in.<br>
	 * This method checks if there is an offline {@code Kisk} bound to the {@code Player}.<br>
	 * If found, it attaches the {@code Kisk} to the {@code Player} and removes it from the offline list.
	 * @param player The {@code Player} object who is currently logging into the game.
	 */
	public void onLogin(Player player)
	{
		final Kisk kisk = boundButOfflinePlayer.get(player.getObjectId());
		if (kisk != null)
		{
			kisk.addPlayer(player);
			boundButOfflinePlayer.remove(player.getObjectId());
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} logs out.<br>
	 * It saves the {@code Kisk} object if the player has one.<br>
	 * This ensures the binding is kept while the player is offline.
	 * @param player The {@code Player} who is logging out.
	 */
	public void onLogout(Player player)
	{
		final Kisk kisk = player.getKisk();
		
		// store binding if existent
		if (kisk != null)
		{
			boundButOfflinePlayer.put(player.getObjectId(), kisk);
		}
	}
	
	/**
	 * Registers a {@code Kisk} object to a specific owner.<br>
	 * This method maps the {@code Kisk} to the provided {@code objOwnerId}.<br>
	 * It updates the internal ownership records in the {@link KiskService}.
	 * @param kisk The {@code Kisk} object to register.
	 * @param objOwnerId The unique identifier of the owner.
	 */
	public void regKisk(Kisk kisk, Integer objOwnerId)
	{
		ownerPlayer.put(objOwnerId, kisk);
	}
	
	/**
	 * Checks if a specific player owns a {@code Kisk}.<br>
	 * This method looks up the ID in the internal ownership map.
	 * @param objOwnerId The unique identifier of the player to check.
	 * @return {@code true} if the player owns a kisk, otherwise {@code false}.
	 */
	public boolean haveKisk(Integer objOwnerId)
	{
		return ownerPlayer.containsKey(objOwnerId);
	}
	
	/**
	 * Provides the global access point for the {@link KiskService}.<br>
	 * This method returns the singleton instance of the service.
	 * @return The singleton {@code KiskService} instance.
	 */
	public static KiskService getInstance()
	{
		return instance;
	}
}
