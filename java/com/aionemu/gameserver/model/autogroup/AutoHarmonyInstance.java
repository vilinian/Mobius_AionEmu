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

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.instancereward.HarmonyArenaReward;
import com.aionemu.gameserver.model.instance.playerreward.HarmonyGroupReward;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * Represents an instance specifically for the {@link PlayerGroup} auto-harmony system.<br>
 * This class manages the logic and rewards associated with automated harmony group activities.
 * @author xTz
 */
public class AutoHarmonyInstance extends AutoInstance
{
	private final List<AGPlayer> group1 = new ArrayList<>();
	private final List<AGPlayer> group2 = new ArrayList<>();
	
	/**
	 * This method is called when a new {@link WorldMapInstance} is created.<br>
	 * It initializes the local doors map from the provided instance.<br>
	 * It calls the superclass implementation of {@code onInstanceCreate}.
	 * @param instance The {@code WorldMapInstance} being created.
	 */
	@Override
	public void onInstanceCreate(WorldMapInstance instance)
	{
		super.onInstanceCreate(instance);
		final HarmonyArenaReward reward = (HarmonyArenaReward) instance.getInstanceHandler().getInstanceReward();
		reward.addHarmonyGroup(new HarmonyGroupReward(1, 12000, (byte) 7, group1));
		reward.addHarmonyGroup(new HarmonyGroupReward(2, 12000, (byte) 7, group2));
	}
	
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
			
			AGQuestion result;
			if (searchInstance.getEntryRequestType().isGroupEntry())
			{
				result = canAddGroup(group1, player, searchInstance);
				if (result.isFailed())
				{
					result = canAddGroup(group2, player, searchInstance);
				}
				
				return result;
			}
			
			result = canAddPlayer(group1, player);
			if (result.isFailed())
			{
				result = canAddPlayer(group2, player);
			}
			
			return result;
		}
		finally
		{
			super.writeUnlock();
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
		if (agt.isHarmonyArena())
		{
			if (!decrease(player, 186000184, 1))
			{
				players.remove(player.getObjectId());
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 5));
				if (players.isEmpty())
				{
					AutoGroupService.getInstance().unRegisterInstance(instance.getInstanceId());
				}
				return;
			}
		}
		
		((HarmonyArenaReward) instance.getInstanceHandler().getInstanceReward()).portToPosition(player);
		instance.register(player.getObjectId());
	}
	
	/**
	 * This method is called when a {@link Player} enters the instance.<br>
	 * It handles group logic and registration for players entering the area.<br>
	 * It ensures that the player is correctly associated with a {@link PlayerGroup}.
	 * @param player The {@link Player} object who entered the instance.
	 */
	@Override
	public void onEnterInstance(Player player)
	{
		super.onEnterInstance(player);
		if (player.isInGroup2())
		{
			return;
		}
		
		final Integer object = player.getObjectId();
		final List<AGPlayer> group = getGroup(object);
		if (group != null)
		{
			final List<Player> _players = getPlayerFromGroup(group);
			_players.remove(player);
			if ((_players.size() == 1) && !_players.get(0).isInGroup2())
			{
				final PlayerGroup newGroup = PlayerGroupService.createGroup(_players.get(0), player, TeamType.AUTO_GROUP);
				final int groupId = newGroup.getObjectId();
				if (!instance.isRegistered(groupId))
				{
					instance.register(groupId);
				}
			}
			else if (!_players.isEmpty() && _players.get(0).isInGroup2())
			{
				PlayerGroupService.addPlayer(_players.get(0).getPlayerGroup2(), player);
			}
			
			if (!instance.isRegistered(object))
			{
				instance.register(object);
			}
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
		unregister(player);
		PlayerGroupService.removePlayer(player);
	}
	
	/**
	 * Removes a {@link Player} from the auto-group lists.<br>
	 * This method updates the internal group tracking for the specified player.<br>
	 * It also calls the {@code unregister} method of the parent class.
	 * @param player The {@code Player} to remove from the system.
	 */
	@Override
	public void unregister(Player player)
	{
		final AGPlayer agp = players.get(player.getObjectId());
		if (agp != null)
		{
			if (group1.contains(agp))
			{
				group1.remove(agp);
			}
			else if (group2.contains(agp))
			{
				group2.remove(agp);
			}
		}
		
		super.unregister(player);
	}
	
	/**
	 * Clears all players from the current instance.<br>
	 * This method removes data from both {@code group1} and {@code group2}.<br>
	 * It also calls the {@code clear} method.
	 */
	@Override
	public void clear()
	{
		super.clear();
		group1.clear();
		group2.clear();
	}
	
	/**
	 * Converts a list of {@code AGPlayer} objects into a list of {@link Player} objects.<br>
	 * This method matches players based on their unique object IDs.
	 * @param group The list of {@code AGPlayer} entities to convert.
	 * @return A list containing the corresponding {@link Player} objects.
	 */
	private List<Player> getPlayerFromGroup(List<AGPlayer> group)
	{
		final List<Player> _players = new ArrayList<>();
		for (AGPlayer agp : group)
		{
			for (Player p : instance.getPlayersInside())
			{
				if (p.getObjectId().equals(agp.getObjectId()))
				{
					_players.add(p);
					break;
				}
			}
		}
		
		return _players;
	}
	
	/**
	 * Retrieves the {@link AGPlayer} group associated with a specific player.<br>
	 * It checks if the player exists in either {@code group1} or {@code group2}.
	 * @param obj The index of the player to look up.
	 * @return A {@code List} containing the players in the group, or {@code null} if no group is found.
	 */
	private List<AGPlayer> getGroup(Integer obj)
	{
		final AGPlayer agp = players.get(obj);
		if (agp != null)
		{
			if (group1.contains(agp))
			{
				return group1;
			}
			else if (group2.contains(agp))
			{
				return group2;
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if a {@link Player} can be added to an existing group.<br>
	 * It verifies the player's race and checks the total member limit.<br>
	 * If successful, it adds members from the player's group to the list.
	 * @param group The current list of {@link AGPlayer} objects in the group.
	 * @param player The {@link Player} attempting to join or add others.
	 * @param searchInstance The {@link SearchInstance} being processed.
	 * @return An {@link AGQuestion} result indicating success, failure, or ready status.
	 */
	private AGQuestion canAddGroup(List<AGPlayer> group, Player player, SearchInstance searchInstance)
	{
		if (group.size() > 0)
		{
			if (!group.get(0).getRace().equals(player.getRace()))
			{
				return AGQuestion.FAILED;
			}
		}
		
		if ((group.size() + searchInstance.getMembers().size()) <= 3)
		{
			for (Player member : player.getPlayerGroup2().getOnlineMembers())
			{
				final Integer obj = member.getObjectId();
				if (searchInstance.getMembers().contains(obj))
				{
					final AGPlayer agp = new AGPlayer(member);
					group.add(agp);
					players.put(obj, agp);
				}
			}
			
			return instance != null ? AGQuestion.ADDED : (players.size() == agt.getPlayerSize() ? AGQuestion.READY : AGQuestion.ADDED);
		}
		
		return AGQuestion.FAILED;
	}
	
	/**
	 * Checks if a {@link Player} can join a specific group.<br>
	 * It validates the current group size and race requirements.<br>
	 * If successful, it adds the player to the list and returns an {@code AGQuestion}.
	 * @param group The list of {@link AGPlayer} objects currently in the group.
	 * @param player The {@link Player} attempting to join the group.
	 * @return An {@code AGQuestion} representing the result of the addition attempt.
	 */
	private AGQuestion canAddPlayer(List<AGPlayer> group, Player player)
	{
		final Integer obj = player.getObjectId();
		final AGPlayer agp = new AGPlayer(player);
		if (group.size() < 3)
		{
			if (group.isEmpty())
			{
				group.add(agp);
				players.put(obj, agp);
				return AGQuestion.ADDED;
			}
			else if (getAGPlayerByIndex(group, 0).getRace().equals(player.getRace()))
			{
				group.add(agp);
				players.put(obj, agp);
				return instance != null ? AGQuestion.ADDED : (players.size() == agt.getPlayerSize() ? AGQuestion.READY : AGQuestion.ADDED);
			}
		}
		
		return AGQuestion.FAILED;
	}
	
	/**
	 * Retrieves a specific {@link AGPlayer} from a list.<br>
	 * It uses the provided position to find the player.
	 * @param group The {@code List} of players to search in.
	 * @param index The numerical position of the player.
	 * @return The {@code AGPlayer} at the specified index.
	 */
	private AGPlayer getAGPlayerByIndex(List<AGPlayer> group, int index)
	{
		return group.get(index);
	}
}
