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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.staticdoor.StaticDoorState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the logic and state transitions for {@link StaticDoor} objects in the game world.<br>
 * This service handles interactions such as opening, closing, and locking doors.
 * @author Wakizashi
 */
public class StaticDoorService
{
	private static final Logger log = LoggerFactory.getLogger(StaticDoorService.class);
	
	/**
	 * Provides the global instance of the {@link StaticDoorService}.<br>
	 * Use this method to access the service from anywhere in the code.<br>
	 * This follows the singleton design pattern.
	 * @return The single instance of {@code StaticDoorService}.
	 */
	public static StaticDoorService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final StaticDoorService instance = new StaticDoorService();
	}
	
	/**
	 * Opens a specific static door for a player.<br>
	 * This method checks if the player has the required key.<br>
	 * It also sends debug messages to players with an access level of 1 or higher.
	 * @param player The {@link Player} attempting to open the door.
	 * @param doorId The unique identifier for the {@link StaticDoor}.
	 */
	public void openStaticDoor(Player player, int doorId)
	{
		if (player.getAccessLevel() >= 1)
		{
			PacketSendUtility.sendMessage(player, "door id : " + doorId);
		}
		
		final StaticDoor door = player.getPosition().getWorldMapInstance().getDoors().get(doorId);
		if (door == null)
		{
			log.warn("Not spawned door worldId: " + player.getWorldId() + " doorId: " + doorId);
			return;
		}
		
		final int keyId = door.getObjectTemplate().getKeyId();
		
		if (player.getAccessLevel() >= 1)
		{
			PacketSendUtility.sendMessage(player, "key id : " + keyId);
		}
		
		if (checkStaticDoorKey(player, keyId))
		{
			door.setOpen(true);
		}
		
		else
		{
			log.info("Opening door without key ...");
		}
	}
	
	/**
	 * Updates the state of a specific static door.<br>
	 * This method changes whether the door is open and updates its status flags.<br>
	 * It also sends a confirmation message to the {@code Player}.
	 * @param player The {@link Player} who triggered the change.
	 * @param doorId The unique identifier for the door in the world map.
	 * @param open Set to {@code true} if the door should be opened, or {@code false} to close it.
	 * @param state The integer value representing the new state of the door.
	 */
	public void changeStaticDoorState(Player player, int doorId, boolean open, int state)
	{
		final StaticDoor door = player.getPosition().getWorldMapInstance().getDoors().get(doorId);
		if (door == null)
		{
			PacketSendUtility.sendMessage(player, "Door is not spawned!");
			return;
		}
		
		door.changeState(open, state);
		String currentStates = "";
		for (StaticDoorState st : StaticDoorState.values())
		{
			if (st == StaticDoorState.NONE)
			{
				continue;
			}
			
			if (door.getStates().contains(st))
			{
				currentStates += st.toString() + ", ";
			}
		}
		
		if ("".equals(currentStates))
		{
			currentStates = "NONE";
		}
		else
		{
			currentStates = currentStates.substring(0, currentStates.length() - 2);
		}
		
		PacketSendUtility.sendMessage(player, "Door states now are: " + currentStates);
	}
	
	/**
	 * Checks if a {@link Player} has the correct key to open a door.<br>
	 * This method verifies access levels and inventory items.<br>
	 * It returns {@code true} if the player can proceed.
	 * @param player The {@link Player} attempting to open the door.
	 * @param keyId The unique identifier for the required key item.
	 * @return {@code true} if the player has access or the correct key, otherwise {@code false}.
	 */
	public boolean checkStaticDoorKey(Player player, int keyId)
	{
		if ((player.getAccessLevel() >= AdminConfig.DOORS_OPEN) || (keyId == 0))
		{
			return true;
		}
		
		if (keyId == 1)
		{
			return false;
		}
		
		if (!player.getInventory().decreaseByItemId(keyId, 1))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_OPEN_DOOR_NEED_KEY_ITEM);
			return false;
		}
		
		return true;
	}
}
