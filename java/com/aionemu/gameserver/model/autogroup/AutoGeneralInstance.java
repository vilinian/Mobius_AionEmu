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
package com.aionemu.gameserver.model.autogroup;

import java.util.List;
import java.util.stream.Collectors;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.templates.portal.PortalLoc;
import com.aionemu.gameserver.model.templates.portal.PortalPath;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * Represents a general instance for the auto-grouping system.<br>
 * It handles logic for players who are not part of specific specialized groups. This class extends {@link AutoInstance} to provide shared functionality.
 * @author xTz
 */
public class AutoGeneralInstance extends AutoInstance
{
	/**
	 * Adds a {@link Player} to the current instance.<br>
	 * This method checks if the player meets all requirements for entry.<br>
	 * It handles both individual and group entry logic.
	 * @param player The {@link Player} attempting to join.
	 * @param searchInstance The {@link SearchInstance} containing the request details.
	 * @return An {@link AGQuestion} representing the result of the addition.
	 */
	@Override
	public AGQuestion addPlayer(Player player, SearchInstance searchInstance)
	{
		super.writeLock();
		try
		{
			if (!satisfyTime(searchInstance) || (players.size() >= agt.getPlayerSize()))
			{
				return AGQuestion.FAILED;
			}
			
			final PlayerClass playerClass = player.getPlayerClass();
			final int clericSize = getPlayersByClass(PlayerClass.CLERIC).size();
			final int templarSize = getPlayersByClass(PlayerClass.TEMPLAR).size();
			if (playerClass.equals(PlayerClass.CLERIC))
			{
				if (clericSize > 0)
				{
					return AGQuestion.FAILED;
				}
			}
			else if (playerClass.equals(PlayerClass.TEMPLAR))
			{
				if (templarSize > 0)
				{
					return AGQuestion.FAILED;
				}
			}
			else
			{
				int size = players.size();
				size -= clericSize;
				size -= templarSize;
				if (size >= 4)
				{
					return AGQuestion.FAILED;
				}
			}
			
			players.put(player.getObjectId(), new AGPlayer(player));
			return instance != null ? AGQuestion.ADDED : (players.size() == agt.getPlayerSize() ? AGQuestion.READY : AGQuestion.ADDED);
		}
		finally
		{
			super.writeUnlock();
		}
	}
	
	/**
	 * This method is called when a {@link Player} enters the instance.<br>
	 * It handles group logic for players entering alone or with others.<br>
	 * It ensures the player is registered within the current instance.
	 * @param player The {@link Player} object who entered the instance.
	 */
	@Override
	public void onEnterInstance(Player player)
	{
		super.onEnterInstance(player);
		final List<Player> playersByRace = instance.getPlayersInside();
		if ((playersByRace.size() == 1) && !playersByRace.get(0).isInGroup2())
		{
			final PlayerGroup newGroup = PlayerGroupService.createGroup(playersByRace.get(0), player, TeamType.AUTO_GROUP);
			final int groupId = newGroup.getObjectId();
			if (!instance.isRegistered(groupId))
			{
				instance.register(groupId);
			}
		}
		else if (!playersByRace.isEmpty() && playersByRace.get(0).isInGroup2())
		{
			PlayerGroupService.addPlayer(playersByRace.get(0).getPlayerGroup2(), player);
		}
		
		final Integer object = player.getObjectId();
		if (!instance.isRegistered(object))
		{
			instance.register(object);
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} presses enter to join the instance.<br>
	 * It triggers the cooldown for the player.<br>
	 * It also moves the player to the correct starting position.
	 * @param player The {@code Player} who is entering the instance.
	 */
	@Override
	public void onPressEnter(Player player)
	{
		super.onPressEnter(player);
		final int worldId = instance.getMapId();
		final PortalPath portal = DataManager.PORTAL2_DATA.getPortalDialog(worldId, 10000, player.getRace());
		if (portal == null)
		{
			return;
		}
		
		final PortalLoc loc = DataManager.PORTAL_LOC_DATA.getPortalLoc(portal.getLocId());
		if (loc == null)
		{
			return;
		}
		
		TeleportService2.teleportTo(player, worldId, instance.getInstanceId(), loc.getX(), loc.getY(), loc.getZ(), loc.getH());
		
		if (player.getPortalCooldownList().getPortalCooldownItem(loc.getWorldId()) != null)
		{
			player.getPortalCooldownList().addPortalCooldown(loc.getWorldId(), 1, DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, worldId));
		}
		else
		{
			player.getPortalCooldownList().addEntry(worldId);
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} leaves this instance.<br>
	 * This method is called to clean up any specific data for the player.
	 * @param player The {@code Player} object who is leaving the instance.
	 */
	@Override
	public void onLeaveInstance(Player player)
	{
		super.unregister(player);
		PlayerGroupService.removePlayer(player);
	}
	
	/**
	 * Retrieves a list of {@link AGPlayer} objects based on their class.<br>
	 * It filters the current players to match the provided {@code playerClass}.
	 * @param playerClass The specific {@code PlayerClass} to filter by.
	 * @return A {@code List} containing all matching {@link AGPlayer} objects.
	 */
	private List<AGPlayer> getPlayersByClass(PlayerClass playerClass)
	{
		return players.values().stream().filter(p -> p.getPlayerClass() == playerClass).collect(Collectors.toList());
	}
}
