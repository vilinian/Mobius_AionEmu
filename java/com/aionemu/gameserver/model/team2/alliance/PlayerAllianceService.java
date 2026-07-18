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
package com.aionemu.gameserver.model.team2.alliance;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.alliance.events.AllianceDisbandEvent;
import com.aionemu.gameserver.model.team2.alliance.events.AssignViceCaptainEvent;
import com.aionemu.gameserver.model.team2.alliance.events.AssignViceCaptainEvent.AssignType;
import com.aionemu.gameserver.model.team2.alliance.events.ChangeAllianceLeaderEvent;
import com.aionemu.gameserver.model.team2.alliance.events.ChangeAllianceLootRulesEvent;
import com.aionemu.gameserver.model.team2.alliance.events.ChangeMemberGroupEvent;
import com.aionemu.gameserver.model.team2.alliance.events.CheckAllianceReadyEvent;
import com.aionemu.gameserver.model.team2.alliance.events.PlayerAllianceInvite;
import com.aionemu.gameserver.model.team2.alliance.events.PlayerAllianceLeavedEvent;
import com.aionemu.gameserver.model.team2.alliance.events.PlayerAllianceUpdateEvent;
import com.aionemu.gameserver.model.team2.alliance.events.PlayerConnectedEvent;
import com.aionemu.gameserver.model.team2.alliance.events.PlayerDisconnectedEvent;
import com.aionemu.gameserver.model.team2.alliance.events.PlayerEnteredEvent;
import com.aionemu.gameserver.model.team2.common.events.PlayerLeavedEvent.LeaveReson;
import com.aionemu.gameserver.model.team2.common.events.ShowBrandEvent;
import com.aionemu.gameserver.model.team2.common.events.TeamCommand;
import com.aionemu.gameserver.model.team2.common.events.TeamKinahDistributionEvent;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import com.aionemu.gameserver.model.team2.league.LeagueService;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.FindGroupService;
import com.aionemu.gameserver.services.VortexService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.TimeUtil;

import java.util.function.Predicate;

/**
 * Manages the core logic and operations for player alliances within the game.<br>
 * This service handles alliance creation, membership updates, and role assignments.<br>
 * It coordinates events related to {@link Player} interactions with their respective alliance groups.
 * @author ATracer, Mobius
 */
public class PlayerAllianceService
{
	private static final Logger log = LoggerFactory.getLogger(PlayerAllianceService.class);
	private static final Map<Integer, PlayerAlliance> alliances = new ConcurrentHashMap<>();
	private static final AtomicBoolean offlineCheckStarted = new AtomicBoolean();
	
	/**
	 * Sends an alliance invitation to another player.<br>
	 * This method checks if the {@code inviter} is allowed to invite the {@code invited} player.<br>
	 * If valid, it displays a request window for the recipient.
	 * @param inviter The player sending the invitation.
	 * @param invited The player receiving the invitation.
	 */
	public static void inviteToAlliance(Player inviter, Player invited)
	{
		if (canInvite(inviter, invited))
		{
			final PlayerAllianceInvite invite = new PlayerAllianceInvite(inviter, invited);
			if (invited.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_MSGBOX_FORCE_INVITE_PARTY, invite))
			{
				if (invited.isInGroup2())
				{
					PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_INVITED_HIS_PARTY(invited.getName()));
				}
				else
				{
					PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_FORCE_INVITED_HIM(invited.getName()));
				}
				
				PacketSendUtility.sendPacket(invited, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_MSGBOX_FORCE_INVITE_PARTY, 0, 0, inviter.getName()));
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
		
		final PlayerAlliance alliance = inviter.getPlayerAlliance2();
		if ((alliance != null) && alliance.getTeamType().isDefence())
		{
			if (invited.isInTeam())
			{
				for (Player tm : invited.getCurrentTeam().getMembers())
				{
					if (tm.isInInstance())
					{
						// You cannot invite the player to the force as the group leader of the player is in an Instanced Zone.
						PacketSendUtility.sendPacket(inviter, new SM_SYSTEM_MESSAGE(1400128));
						return false;
					}
					else if (!VortexService.getInstance().isInsideVortexZone(tm))
					{
						// TODO: chk on retail
						PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_CANT_INVITE_WHEN_HE_IS_ASKED_QUESTION(tm.getName()));
						return false;
					}
				}
			}
			else if (!VortexService.getInstance().isInsideVortexZone(invited))
			{
				// You cannot invite someone in a different area.
				PacketSendUtility.sendPacket(inviter, new SM_SYSTEM_MESSAGE(1401527));
				return false;
			}
		}
		
		return RestrictionsManager.canInviteToAlliance(inviter, invited);
	}
	
	/**
	 * Creates a new {@link PlayerAlliance} for the specified players.<br>
	 * This method initializes the alliance with a leader and an invited member.<br>
	 * It also triggers the offline check initialization if it has not started yet.
	 * @param leader The {@code Player} who will lead the new alliance.
	 * @param invited The {@code Player} being added to the alliance initially.
	 * @param type The {@code TeamType} assigned to this alliance.
	 * @return The newly created {@code PlayerAlliance} object.
	 */
	public static PlayerAlliance createAlliance(Player leader, Player invited, TeamType type)
	{
		final PlayerAlliance newAlliance = new PlayerAlliance(new PlayerAllianceMember(leader), type);
		alliances.put(newAlliance.getTeamId(), newAlliance);
		addPlayer(newAlliance, leader);
		addPlayer(newAlliance, invited);
		if (offlineCheckStarted.compareAndSet(false, true))
		{
			initializeOfflineCheck();
		}
		
		FindGroupService.getInstance().onAllianceCreated(leader);
		return newAlliance;
	}
	
	/**
	 * Sets up a background task to check for offline players in alliances.<br>
	 * This method schedules the {@code OfflinePlayerAllianceChecker} to run every 30 seconds.
	 */
	private static void initializeOfflineCheck()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new OfflinePlayerAllianceChecker(), 1000, 30 * 1000);
	}
	
	/**
	 * Adds a specific player to an existing alliance.<br>
	 * This method updates the {@link PlayerAlliance} member list.
	 * @param alliance The {@code PlayerAlliance} object to receive the new member.
	 * @param invited The {@code Player} who will be added to the group.
	 */
	public static void addPlayerToAlliance(PlayerAlliance alliance, Player invited)
	{
		// TODO leader member is already set
		alliance.addMember(new PlayerAllianceMember(invited));
		FindGroupService.getInstance().onPlayerAddedToAlliance(alliance, invited);
	}
	
	/**
	 * Updates the loot rules for a specific alliance.<br>
	 * This method triggers a {@code ChangeAllianceLootRulesEvent}.
	 * @param alliance The {@link PlayerAlliance} to modify.
	 * @param lootRules The new {@code LootGroupRules} to apply.
	 */
	public static void changeGroupRules(PlayerAlliance alliance, LootGroupRules lootRules)
	{
		alliance.onEvent(new ChangeAllianceLootRulesEvent(alliance, lootRules));
	}
	
	/**
	 * Handles logic when a {@link Player} logs into the server.<br>
	 * This method checks if the player belongs to any existing alliances.<br>
	 * It triggers a {@code PlayerConnectedEvent} for each alliance they join.
	 * @param player The {@code Player} object that is currently logging in.
	 */
	public static void onPlayerLogin(Player player)
	{
		for (PlayerAlliance alliance : alliances.values())
		{
			final PlayerAllianceMember member = alliance.getMember(player.getObjectId());
			if (member != null)
			{
				alliance.onEvent(new PlayerConnectedEvent(alliance, player));
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
		final PlayerAlliance alliance = player.getPlayerAlliance2();
		if (alliance != null)
		{
			final PlayerAllianceMember member = alliance.getMember(player.getObjectId());
			member.updateLastOnlineTime();
			alliance.onEvent(new PlayerDisconnectedEvent(alliance, player));
		}
	}
	
	/**
	 * Updates the alliance status for a specific {@link Player}.<br>
	 * This method checks if the {@code player} belongs to an alliance.<br>
	 * If an alliance exists, it triggers a {@link PlayerAllianceUpdateEvent}.
	 * @param player The {@code Player} whose alliance needs updating.
	 * @param allianceEvent The specific {@code PlayerAllianceEvent} to process.
	 */
	public static void updateAlliance(Player player, PlayerAllianceEvent allianceEvent)
	{
		final PlayerAlliance alliance = player.getPlayerAlliance2();
		if (alliance != null)
		{
			alliance.onEvent(new PlayerAllianceUpdateEvent(alliance, player, allianceEvent));
		}
	}
	
	/**
	 * Adds a {@link Player} to a specific {@link PlayerAlliance}.<br>
	 * This method triggers the {@code PlayerEnteredEvent} for the alliance.
	 * @param alliance The {@link PlayerAlliance} that will receive the new member.
	 * @param player The {@link Player} who is joining the alliance.
	 */
	public static void addPlayer(PlayerAlliance alliance, Player player)
	{
		Objects.requireNonNull(alliance, "Alliance should not be null");
		alliance.onEvent(new PlayerEnteredEvent(alliance, player));
	}
	
	/**
	 * Removes a {@link Player} from their current alliance.<br>
	 * This method checks if the player belongs to an alliance first.<br>
	 * It handles specific logic for defense teams and triggers a {@code PlayerAllianceLeavedEvent}.
	 * @param player The {@code Player} object to be removed.
	 */
	public static void removePlayer(Player player)
	{
		final PlayerAlliance alliance = player.getPlayerAlliance2();
		if (alliance != null)
		{
			if (alliance.getTeamType().isDefence())
			{
				VortexService.getInstance().removeDefenderPlayer(player);
			}
			
			alliance.onEvent(new PlayerAllianceLeavedEvent(alliance, player));
		}
	}
	
	/**
	 * Bans a specific player from an alliance.<br>
	 * This method removes the {@code bannedPlayer} from the {@link PlayerAlliance} of the {@code banGiver}.<br>
	 * It also handles removing the player from defense roles if applicable.
	 * @param bannedPlayer The {@code Player} object to be removed from the alliance.
	 * @param banGiver The {@code Player} who is performing the ban action.
	 */
	public static void banPlayer(Player bannedPlayer, Player banGiver)
	{
		Objects.requireNonNull(bannedPlayer, "Banned player should not be null");
		Objects.requireNonNull(banGiver, "Bangiver player should not be null");
		final PlayerAlliance alliance = banGiver.getPlayerAlliance2();
		if (alliance != null)
		{
			if (alliance.getTeamType().isDefence())
			{
				VortexService.getInstance().removeDefenderPlayer(bannedPlayer);
			}
			
			final PlayerAllianceMember bannedMember = alliance.getMember(bannedPlayer.getObjectId());
			if (bannedMember != null)
			{
				alliance.onEvent(new PlayerAllianceLeavedEvent(alliance, bannedMember.getObject(), LeaveReson.BAN, banGiver.getName()));
			}
			else
			{
				log.warn("TEAM2: banning player not in alliance {}", alliance.onlineMembers());
			}
		}
	}
	
	/**
	 * Disbands the specified {@code PlayerAlliance}.<br>
	 * This method removes the alliance from the active list.<br>
	 * It triggers an {@link AllianceDisbandEvent} for the alliance.<br>
	 * The alliance must have one or fewer online members to be disbanded.
	 * @param alliance The {@code PlayerAlliance} object to disband.
	 */
	public static void disband(PlayerAlliance alliance)
	{
		FindGroupService.getInstance().onAllianceDisbanded(alliance);
		if (!(alliance.onlineMembers() <= 1))
		{
			throw new IllegalStateException("Can't disband alliance with more than one online member");
		}
		alliances.remove(alliance.getTeamId());
		alliance.onEvent(new AllianceDisbandEvent(alliance));
		LeagueService.onAllianceDisbanded(alliance);
	}
	
	/**
	 * Updates the leader of the alliance for a specific player.<br>
	 * This method checks if the {@code Player} belongs to an alliance.<br>
	 * If they do, it triggers a {@link ChangeAllianceLeaderEvent}.
	 * @param player The {@code Player} who will become the new leader.
	 */
	public static void changeLeader(Player player)
	{
		final PlayerAlliance alliance = player.getPlayerAlliance2();
		if (alliance != null)
		{
			alliance.onEvent(new ChangeAllianceLeaderEvent(alliance, player));
		}
	}
	
	/**
	 * Updates the vice captain status for a specific player.<br>
	 * This method triggers an {@link AssignViceCaptainEvent} for the player's alliance.<br>
	 * It checks if the {@code Player} belongs to an alliance before proceeding.
	 * @param player The {@code Player} whose role is being changed.
	 * @param assignType The type of assignment to apply.
	 */
	public static void changeViceCaptain(Player player, AssignType assignType)
	{
		final PlayerAlliance alliance = player.getPlayerAlliance2();
		if (alliance != null)
		{
			alliance.onEvent(new AssignViceCaptainEvent(alliance, player, assignType));
		}
	}
	
	/**
	 * Finds the {@link PlayerAlliance} that a specific player belongs to.<br>
	 * It searches through all active alliances for the given {@code playerObjId}.
	 * @param playerObjId The unique identifier of the player to search for.
	 * @return The {@code PlayerAlliance} object if found, or {@code null} if no alliance is associated with the ID.
	 */
	public static PlayerAlliance searchAlliance(Integer playerObjId)
	{
		for (PlayerAlliance alliance : alliances.values())
		{
			if (alliance.hasMember(playerObjId))
			{
				return alliance;
			}
		}
		
		return null;
	}
	
	/**
	 * Changes the member group of players within an alliance.<br>
	 * This method requires the {@code player} to be a leader or vice captain.<br>
	 * If they lack permission, a message is sent to the {@code player}.
	 * @param player The player attempting to perform the action.
	 * @param firstPlayer The ID of the first player involved in the group change.
	 * @param secondPlayer The ID of the second player involved in the group change.
	 * @param allianceGroupId The ID of the target alliance group.
	 */
	public static void changeMemberGroup(Player player, int firstPlayer, int secondPlayer, int allianceGroupId)
	{
		final PlayerAlliance alliance = player.getPlayerAlliance2();
		Objects.requireNonNull(alliance, "Alliance should not be null for group change");
		if (alliance.isLeader(player) || alliance.isViceCaptain(player))
		{
			alliance.onEvent(new ChangeMemberGroupEvent(alliance, firstPlayer, secondPlayer, allianceGroupId));
		}
		else
		{
			PacketSendUtility.sendMessage(player, "You do not have the authority for that.");
		}
	}
	
	/**
	 * Verifies if the player is ready for a specific team command.<br>
	 * This method retrieves the {@link PlayerAlliance} of the given {@code player}.<br>
	 * If an alliance exists, it triggers a {@code CheckAllianceReadyEvent}.
	 * @param player The {@code Player} object to check.
	 * @param eventCode The specific {@code TeamCommand} code being processed.
	 */
	public static void checkReady(Player player, TeamCommand eventCode)
	{
		final PlayerAlliance alliance = player.getPlayerAlliance2();
		if (alliance != null)
		{
			alliance.onEvent(new CheckAllianceReadyEvent(alliance, player, eventCode));
		}
	}
	
	/**
	 * Distributes a specific amount of Kinah to the alliance of a given player.<br>
	 * This method checks if the {@code Player} belongs to an alliance.<br>
	 * If they do, it triggers a {@code TeamKinahDistributionEvent}.
	 * @param player The {@link Player} who will initiate the distribution.
	 * @param amount The total amount of Kinah to be distributed.
	 */
	public static void distributeKinah(Player player, long amount)
	{
		final PlayerAlliance alliance = player.getPlayerAlliance2();
		if (alliance != null)
		{
			alliance.onEvent(new TeamKinahDistributionEvent<>(alliance, player, amount));
		}
	}
	
	/**
	 * Distributes a specific amount of Kinah to the group of the given {@code Player}.<br>
	 * This method checks if the {@code Player} belongs to an alliance group.<br>
	 * If the group exists, it triggers a distribution event for that group.
	 * @param player The {@code Player} who will initiate the distribution.
	 * @param amount The total amount of Kinah to be distributed.
	 */
	public static void distributeKinahInGroup(Player player, long amount)
	{
		final PlayerAllianceGroup allianceGroup = player.getPlayerAllianceGroup2();
		if (allianceGroup != null)
		{
			allianceGroup.onEvent(new TeamKinahDistributionEvent<>(allianceGroup, player, amount));
		}
	}
	
	/**
	 * Displays a specific brand to the players in an alliance.<br>
	 * This method triggers a {@code ShowBrandEvent} for the player's current alliance.
	 * @param player The {@link Player} who initiates the action.
	 * @param targetObjId The unique identifier of the target object.
	 * @param brandId The unique identifier of the brand to display.
	 */
	public static void showBrand(Player player, int targetObjId, int brandId)
	{
		final PlayerAlliance alliance = player.getPlayerAlliance2();
		if (alliance != null)
		{
			alliance.onEvent(new ShowBrandEvent<>(alliance, targetObjId, brandId));
		}
	}
	
	/**
	 * Retrieves the current status of the alliance system.<br>
	 * This method returns a string showing the total count of active alliances.
	 * @return A {@code String} containing the number of alliances.
	 */
	public static String getServiceStatus()
	{
		return "Number of alliances: " + alliances.size();
	}
	
	public static class OfflinePlayerAllianceChecker implements Runnable, Predicate<PlayerAllianceMember>
	{
		private PlayerAlliance currentAlliance;
		
		@Override
		public void run()
		{
			for (PlayerAlliance alliance : alliances.values())
			{
				currentAlliance = alliance;
				alliance.apply(this);
			}
			
			currentAlliance = null;
		}
		
		@Override
		public boolean test(PlayerAllianceMember member)
		{
			final int kickDelay = currentAlliance.getTeamType().isAutoTeam() ? 60 : GroupConfig.ALLIANCE_REMOVE_TIME;
			if (!member.isOnline() && TimeUtil.isExpired(member.getLastOnlineTime() + (kickDelay * 1000)))
			{
				if (currentAlliance.getTeamType().isOffence())
				{
					VortexService.getInstance().removeInvaderPlayer(member.getObject());
				}
				
				currentAlliance.onEvent(new PlayerAllianceLeavedEvent(currentAlliance, member.getObject(), LeaveReson.LEAVE_TIMEOUT));
			}
			
			return true;
		}
	}
}
