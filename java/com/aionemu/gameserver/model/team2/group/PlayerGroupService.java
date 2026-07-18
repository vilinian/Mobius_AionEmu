/*
 * This file is part of the Mobius AionEmu project.
 * 
 * Mobius AionEmu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Mobius AionEmu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.team2.group;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.common.events.PlayerLeavedEvent.LeaveReson;
import com.aionemu.gameserver.model.team2.common.events.ShowBrandEvent;
import com.aionemu.gameserver.model.team2.common.events.TeamKinahDistributionEvent;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.team2.group.events.ChangeGroupLeaderEvent;
import com.aionemu.gameserver.model.team2.group.events.ChangeGroupLootRulesEvent;
import com.aionemu.gameserver.model.team2.group.events.GroupDisbandEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerConnectedEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerDisconnectedEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerEnteredEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerGroupInvite;
import com.aionemu.gameserver.model.team2.group.events.PlayerGroupLeavedEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerGroupStopMentoringEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerGroupUpdateEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerStartMentoringEvent;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.FindGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.TimeUtil;

import java.util.function.Predicate;

/**
 * Manages the logic and lifecycle of player groups within the game server.<br>
 * This service handles group creation, membership changes, and loot rule configurations.<br>
 * It coordinates interactions between {@link Player} objects and various group-related events.
 * @author ATracer, Mobius
 */
public class PlayerGroupService
{
	private static final Logger log = LoggerFactory.getLogger(PlayerGroupService.class);
	private static final Map<Integer, PlayerGroup> groups = new ConcurrentHashMap<>();
	private static final AtomicBoolean offlineCheckStarted = new AtomicBoolean();
	private static Map<Integer, PlayerGroup> groupMembers;
	
	/**
	 * Sends a group invitation to another player.<br>
	 * This method checks if the {@code inviter} is allowed to invite the {@code invited} player.<br>
	 * If successful, it opens a question window for the recipient to accept or decline.
	 * @param inviter The player who is sending the invitation.
	 * @param invited The player who will receive the invitation.
	 */
	public static void inviteToGroup(Player inviter, Player invited)
	{
		if (canInvite(inviter, invited))
		{
			final PlayerGroupInvite invite = new PlayerGroupInvite(inviter, invited);
			if (invited.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_PARTY_DO_YOU_ACCEPT_INVITATION, invite))
			{
				PacketSendUtility.sendPacket(invited, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_PARTY_DO_YOU_ACCEPT_INVITATION, 0, 0, inviter.getName()));
			}
		}
	}
	
	/**
	 * Checks if a player is allowed to send an alliance invitation.<br>
	 * This method validates instance status and zone restrictions for both players.<br>
	 * It also verifies team membership rules for defense type alliances.
	 * @param inviter The {@code Player} sending the invitation.
	 * @param invited The {@code Player} receiving the invitation.
	 * @return {@code true} if the invitation is allowed, otherwise {@code false}.
	 */
	public static boolean canInvite(Player inviter, Player invited)
	{
		if (inviter.isInInstance())
		{
			if (AutoGroupService.getInstance().isAutoInstance(inviter.getInstanceId()))
			{
				PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_CANT_OPERATE_PARTY_COMMAND);
				return false;
			}
		}
		
		if (invited.isInInstance())
		{
			if (AutoGroupService.getInstance().isAutoInstance(invited.getInstanceId()))
			{
				PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_CANT_OPERATE_PARTY_COMMAND);
				return false;
			}
		}
		
		final PlayerGroup group = inviter.getPlayerGroup2();
		if (group != null)
		{
			if (invited.isInTeam())
			{
				for (Player pm : invited.getCurrentTeam().getMembers())
				{
					if (pm.isInInstance())
					{
						PacketSendUtility.sendPacket(inviter, new SM_SYSTEM_MESSAGE(1400128));
						return false;
					}
				}
			}
		}
		
		return RestrictionsManager.canInviteToGroup(inviter, invited);
	}
	
	/**
	 * Creates a new {@link PlayerGroup} with a leader and an invited player.<br>
	 * This method initializes the group based on the specified {@code TeamType}.<br>
	 * It also triggers the offline check initialization if it has not started yet.
	 * @param leader The {@code Player} who will lead the new group.
	 * @param invited The {@code Player} being added to the group.
	 * @param type The {@code TeamType} assigned to this group.
	 * @return The newly created {@code PlayerGroup} object.
	 */
	public static PlayerGroup createGroup(Player leader, Player invited, TeamType type)
	{
		final PlayerGroup newGroup = new PlayerGroup(new PlayerGroupMember(leader), type);
		groups.put(newGroup.getTeamId(), newGroup);
		addPlayer(newGroup, leader);
		addPlayer(newGroup, invited);
		if (offlineCheckStarted.compareAndSet(false, true))
		{
			initializeOfflineCheck();
		}
		
		FindGroupService.getInstance().onGroupCreated(leader);
		return newGroup;
	}
	
	/**
	 * Creates a new {@link PlayerGroup} with a single leader.<br>
	 * This method initializes the group and registers it in the system.<br>
	 * It also triggers the offline check if it has not been started yet.
	 * @param leader The {@code Player} who will lead the new group.
	 * @return The newly created {@code PlayerGroup} object.
	 */
	public static PlayerGroup createGroup(Player leader)
	{
		final PlayerGroup newGroup = new PlayerGroup(new PlayerGroupMember(leader), TeamType.GROUP);
		groups.put(newGroup.getTeamId(), newGroup);
		addPlayer(newGroup, leader);
		if (offlineCheckStarted.compareAndSet(false, true))
		{
			initializeOfflineCheck();
		}
		
		FindGroupService.getInstance().onGroupCreated(leader);
		return newGroup;
	}
	
	/**
	 * Sets up a background task to check for offline players in alliances.<br>
	 * This method schedules the {@code OfflinePlayerChecker} to run every 30 seconds.
	 */
	private static void initializeOfflineCheck()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new OfflinePlayerChecker(), 1000, 30 * 1000);
	}
	
	/**
	 * Adds a specific {@code Player} to the provided {@link PlayerGroup}.<br>
	 * This method creates a new member entry for the invited player.
	 * @param group The {@code PlayerGroup} that will receive the new member.
	 * @param invited The {@code Player} who is being added to the group.
	 */
	public static void addPlayerToGroup(PlayerGroup group, Player invited)
	{
		group.addMember(new PlayerGroupMember(invited));
		FindGroupService.getInstance().onPlayerAddedToGroup(group, invited);
	}
	
	/**
	 * Updates the loot rules for a specific group.<br>
	 * This method triggers a {@code ChangeGroupLootRulesEvent}.
	 * @param group The {@link PlayerGroup} to modify.
	 * @param lootRules The new {@code LootGroupRules} to apply.
	 */
	public static void changeGroupRules(PlayerGroup group, LootGroupRules lootRules)
	{
		group.onEvent(new ChangeGroupLootRulesEvent(group, lootRules));
	}
	
	/**
	 * Handles logic when a {@link Player} logs into the server.<br>
	 * This method checks if the player belongs to any existing alliances.<br>
	 * It triggers a {@code PlayerConnectedEvent} for each alliance they join.
	 * @param player The {@code Player} object that is currently logging in.
	 */
	public static void onPlayerLogin(Player player)
	{
		for (PlayerGroup group : groups.values())
		{
			final PlayerGroupMember member = group.getMember(player.getObjectId());
			if (member != null)
			{
				group.onEvent(new PlayerConnectedEvent(group, player));
			}
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} logs out of the game.<br>
	 * It updates the last online time for members in an alliance.<br>
	 * It also triggers a {@code PlayerDisconnectedEvent} if the player belongs to one.
	 * @param player The {@code Player} object who is logging out.
	 */
	public static void onPlayerLogout(Player player)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			final PlayerGroupMember member = group.getMember(player.getObjectId());
			member.updateLastOnlineTime();
			group.onEvent(new PlayerDisconnectedEvent(group, player));
		}
	}
	
	/**
	 * Updates the group status for a specific {@link Player}.<br>
	 * This method checks if the {@code player} belongs to a group.<br>
	 * If a group exists, it triggers a {@code PlayerGroupUpdateEvent} using the provided {@code groupEvent}.
	 * @param player The {@link Player} whose group needs to be updated.
	 * @param groupEvent The {@link GroupEvent} containing the update details.
	 */
	public static void updateGroup(Player player, GroupEvent groupEvent)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new PlayerGroupUpdateEvent(group, player, groupEvent));
		}
	}
	
	/**
	 * Adds a {@link Player} to a specific {@link PlayerGroup}.<br>
	 * This method triggers the {@code PlayerEnteredEvent} for the group.
	 * @param group The {@link PlayerGroup} that will receive the player.
	 * @param player The {@link Player} being added to the group.
	 */
	public static void addPlayer(PlayerGroup group, Player player)
	{
		Objects.requireNonNull(group, "Group should not be null");
		group.onEvent(new PlayerEnteredEvent(group, player));
	}
	
	/**
	 * Removes a {@link Player} from their current group.<br>
	 * This method checks if the {@code Player} belongs to a group.<br>
	 * It triggers a {@code PlayerGroupLeavedEvent} if the player is in a group.
	 * @param player The {@code Player} object to be removed.
	 */
	public static void removePlayer(Player player)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new PlayerGroupLeavedEvent(group, player));
		}
	}
	
	/**
	 * Bans a specific player from the current group.<br>
	 * This method checks if {@code bannedPlayer} is a member of the {@code banGiver} group.<br>
	 * If they are in the group, it triggers a {@link PlayerGroupLeavedEvent} with the reason {@code BAN}.
	 * @param bannedPlayer The {@code Player} object to be removed from the group.
	 * @param banGiver The {@code Player} who is performing the ban action.
	 */
	public static void banPlayer(Player bannedPlayer, Player banGiver)
	{
		Objects.requireNonNull(bannedPlayer, "Banned player should not be null");
		Objects.requireNonNull(banGiver, "Bangiver player should not be null");
		final PlayerGroup group = banGiver.getPlayerGroup2();
		if (group != null)
		{
			if (group.hasMember(bannedPlayer.getObjectId()))
			{
				group.onEvent(new PlayerGroupLeavedEvent(group, bannedPlayer, LeaveReson.BAN, banGiver.getName()));
			}
			else
			{
				log.warn("TEAM2: banning player not in group {}", group.onlineMembers());
			}
		}
	}
	
	/**
	 * Disbands the specified {@code PlayerGroup}.<br>
	 * This method removes the group from the active list.<br>
	 * It triggers a {@link com.aionemu.gameserver.model.team2.group.events.GroupDisbandEvent}.<br>
	 * The group must have one or fewer online members to be disbanded.
	 * @param group The {@code PlayerGroup} to disband.
	 */
	public static void disband(PlayerGroup group)
	{
		FindGroupService.getInstance().onGroupDisbanded(group);
		if (!(group.onlineMembers() <= 1))
		{
			throw new IllegalStateException("Can't disband group with more than one online member");
		}
		groups.remove(group.getTeamId());
		group.onEvent(new GroupDisbandEvent(group));
	}
	
	/**
	 * Distributes a specific amount of currency to the group of a player.<br>
	 * This method checks if the {@code Player} belongs to a group.<br>
	 * If a group exists, it triggers a {@link TeamKinahDistributionEvent}.
	 * @param player The {@code Player} who is initiating the distribution.
	 * @param kinah The amount of currency to be distributed.
	 */
	public static void distributeKinah(Player player, long kinah)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new TeamKinahDistributionEvent<>(group, player, kinah));
		}
	}
	
	/**
	 * Displays a specific brand to the players in a group.<br>
	 * This method triggers a {@code ShowBrandEvent} for the player's current group.
	 * @param player The {@link Player} who initiates the action.
	 * @param targetObjId The unique identifier of the target object.
	 * @param brandId The unique identifier of the brand to display.
	 */
	public static void showBrand(Player player, int targetObjId, int brandId)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new ShowBrandEvent<>(group, targetObjId, brandId));
		}
	}
	
	/**
	 * Changes the leader of a group for a specific player.<br>
	 * This method retrieves the {@code PlayerGroup} associated with the {@code Player}.<br>
	 * If the group exists, it triggers a {@link ChangeGroupLeaderEvent}.
	 * @param player The {@code Player} who will become the new leader.
	 */
	public static void changeLeader(Player player)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new ChangeGroupLeaderEvent(group, player));
		}
	}
	
	/**
	 * Starts the mentoring process for a specific player.<br>
	 * This method checks if the {@code Player} belongs to a group.<br>
	 * If they do, it triggers a {@code PlayerStartMentoringEvent}.
	 * @param player The {@code Player} who will begin mentoring.
	 */
	public static void startMentoring(Player player)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new PlayerStartMentoringEvent(group, player));
		}
	}
	
	/**
	 * Stops the mentoring status for a specific player.<br>
	 * This method checks if the {@code Player} belongs to a group.<br>
	 * If they do, it triggers a {@code PlayerGroupStopMentoringEvent}.
	 * @param player The {@code Player} whose mentoring should be stopped.
	 */
	public static void stopMentoring(Player player)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new PlayerGroupStopMentoringEvent(group, player));
		}
	}
	
	/**
	 * Cleans up all active group data.<br>
	 * This method clears the internal {@code groups} collection.<br>
	 * It logs the current service status before performing the cleanup.
	 */
	public static void cleanup()
	{
		log.info(getServiceStatus());
		groups.clear();
	}
	
	/**
	 * Retrieves the current status of the group system.<br>
	 * This method returns a string showing the total count of active groups.
	 * @return A {@code String} containing the number of groups.
	 */
	public static String getServiceStatus()
	{
		return "Number of groups: " + groups.size();
	}
	
	/**
	 * Finds the {@link PlayerGroup} that contains a specific player.<br>
	 * It searches through all active groups for the provided ID.
	 * @param playerObjId The unique identifier of the player to search for.
	 * @return The {@code PlayerGroup} containing the player, or {@code null} if no group is found.
	 */
	public static PlayerGroup searchGroup(Integer playerObjId)
	{
		for (PlayerGroup group : groups.values())
		{
			if (group.hasMember(playerObjId))
			{
				return group;
			}
		}
		
		return null;
	}
	
	public static class OfflinePlayerChecker implements Runnable, Predicate<PlayerGroupMember>
	{
		private PlayerGroup currentGroup;
		
		@Override
		public void run()
		{
			for (PlayerGroup group : groups.values())
			{
				currentGroup = group;
				group.apply(this);
			}
			
			currentGroup = null;
		}
		
		@Override
		public boolean test(PlayerGroupMember member)
		{
			if (!member.isOnline() && TimeUtil.isExpired(member.getLastOnlineTime() + (GroupConfig.GROUP_REMOVE_TIME * 1000)))
			{
				// TODO LEAVE_TIMEOUT type
				currentGroup.onEvent(new PlayerGroupLeavedEvent(currentGroup, member.getObject()));
			}
			
			return true;
		}
	}
	
	/**
	 * Adds a {@code Player} to the group members cache.<br>
	 * This method checks if the {@code Player} is already in the cache before adding them.<br>
	 * It uses the unique object ID of the {@code Player} as the key.
	 * @param player The {@code Player} to be added to the cache.
	 */
	public static void addGroupMemberToCache(Player player)
	{
		if (!groupMembers.containsKey(player.getObjectId()))
		{
			groupMembers.put(player.getObjectId(), player.getPlayerGroup2());
		}
	}
}
