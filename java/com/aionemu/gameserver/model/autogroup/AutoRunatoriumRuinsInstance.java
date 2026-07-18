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

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.instancereward.RunatoriumReward;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.services.instance.RunatoriumService;

/**
 * Represents an automated instance of the Runatorium Ruins.<br>
 * This class handles specific logic for auto-grouping and rewards within this map.<br>
 * It extends {@link AutoInstance} to provide specialized behavior for the ruins.
 * @author GiGatR00n
 */
public class AutoRunatoriumRuinsInstance extends AutoInstance
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
			
			final EntryRequestType ert = searchInstance.getEntryRequestType();
			final List<AGPlayer> playersByRace = getAGPlayersByRace(player.getRace());
			if (ert.isGroupEntry())
			{
				if ((searchInstance.getMembers().size() + playersByRace.size()) > 6)
				{
					return AGQuestion.FAILED;
				}
				
				for (Player member : player.getPlayerGroup2().getOnlineMembers())
				{
					if (searchInstance.getMembers().contains(member.getObjectId()))
					{
						players.put(member.getObjectId(), new AGPlayer(player));
					}
				}
			}
			else
			{
				if (playersByRace.size() >= 6)
				{
					return AGQuestion.FAILED;
				}
				
				players.put(player.getObjectId(), new AGPlayer(player));
			}
			
			return instance != null ? AGQuestion.ADDED : (players.size() == agt.getPlayerSize() ? AGQuestion.READY : AGQuestion.ADDED);
		}
		finally
		{
			super.writeUnlock();
		}
	}
	
	/**
	 * This method is called when a {@link Player} enters the instance.<br>
	 * It handles group logic based on the player's race.<br>
	 * It ensures the player and their group are registered with the instance.
	 * @param player The {@link Player} object who entered the instance.
	 */
	@Override
	public void onEnterInstance(Player player)
	{
		super.onEnterInstance(player);
		final List<Player> playersByRace = getPlayersByRace(player.getRace());
		playersByRace.remove(player);
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
		RunatoriumService.getInstance().addCoolDown(player);
		((RunatoriumReward) instance.getInstanceHandler().getInstanceReward()).portToPosition(player);
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
	 * Retrieves a list of {@link AGPlayer} objects based on their race.<br>
	 * This method filters the current players to match the specified {@code race}.
	 * @param race The {@code Race} type to filter by.
	 * @return A {@code List} of {@link AGPlayer} objects that match the given race.
	 */
	private List<AGPlayer> getAGPlayersByRace(Race race)
	{
		return players.values().stream().filter(p -> p.getRace() == race).collect(Collectors.toList());
	}
	
	/**
	 * Retrieves a list of players based on their specific race.<br>
	 * This method filters all players currently inside the instance.
	 * @param race The {@code Race} type to filter by.
	 * @return A {@code List} of {@link Player} objects matching the given race.
	 */
	private List<Player> getPlayersByRace(Race race)
	{
		return instance.getPlayersInside().stream().filter(p -> p.getRace() == race).collect(Collectors.toList());
	}
}
