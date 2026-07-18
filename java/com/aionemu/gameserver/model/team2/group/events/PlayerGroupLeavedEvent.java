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
package com.aionemu.gameserver.model.team2.group.events;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.common.events.PlayerLeavedEvent;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupMember;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEAVE_GROUP_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This event is triggered when a {@link Player} leaves a {@link PlayerGroup}.<br>
 * It handles the logic for updating group membership and notifying other players.<br>
 * It extends {@link PlayerLeavedEvent} to specifically manage {@link PlayerGroupMember} data.
 * @author ATracer
 */
public class PlayerGroupLeavedEvent extends PlayerLeavedEvent<PlayerGroupMember, PlayerGroup>
{
	/**
	 * Creates a new event for when a {@link Player} leaves a {@link PlayerGroup}.<br>
	 * This constructor initializes the basic data needed for the leave action.
	 * @param alliance The {@link PlayerGroup} that the player is leaving.
	 * @param player The {@link Player} who has left the group.
	 */
	public PlayerGroupLeavedEvent(PlayerGroup alliance, Player player)
	{
		super(alliance, player);
	}
	
	/**
	 * Creates a new event for when a player leaves a group.<br>
	 * This constructor includes the specific reason and any related ban information.
	 * @param team The {@code PlayerGroup} that the player is leaving.
	 * @param player The {@code Player} who left the group.
	 * @param reason The {@code PlayerLeavedEvent.LeaveReson} explaining why they left.
	 * @param banPersonName The name of the person responsible for a ban, if applicable.
	 */
	public PlayerGroupLeavedEvent(PlayerGroup team, Player player, PlayerLeavedEvent.LeaveReson reason, String banPersonName)
	{
		super(team, player, reason, banPersonName);
	}
	
	/**
	 * Creates a new event for when a player leaves a group.<br>
	 * This constructor initializes the event with the specific alliance and leave reason.
	 * @param alliance The {@code PlayerGroup} that the player is leaving.
	 * @param player The {@link Player} who left the group.
	 * @param reason The {@code PlayerLeavedEvent.LeaveReson} explaining why the player left.
	 */
	public PlayerGroupLeavedEvent(PlayerGroup alliance, Player player, PlayerLeavedEvent.LeaveReson reason)
	{
		super(alliance, player, reason);
	}
	
	/**
	 * Processes the logic for a player leaving a group.<br>
	 * This method removes the member from the {@code PlayerGroup}.<br>
	 * It handles mentor status changes and leader transitions.<br>
	 * It also sends necessary network packets to the affected players.
	 */
	@Override
	public void handleEvent()
	{
		team.removeMember(leavedPlayer.getObjectId());
		
		if (leavedPlayer.isMentor())
		{
			team.onEvent(new PlayerGroupStopMentoringEvent(team, leavedPlayer));
		}
		
		team.apply(this);
		
		PacketSendUtility.sendPacket(leavedPlayer, new SM_LEAVE_GROUP_MEMBER());
		switch (reason)
		{
			case BAN:
			case LEAVE:
				// PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_SECEDE); // client side?
				if (team.onlineMembers() <= 1)
				{
					PlayerGroupService.disband(team);
				}
				else
				{
					if (leavedPlayer.equals(team.getLeader().getObject()))
					{
						team.onEvent(new ChangeGroupLeaderEvent(team));
					}
				}
				
				if (reason == LeaveReson.BAN)
				{
					PacketSendUtility.sendPacket(leavedPlayer, SM_SYSTEM_MESSAGE.STR_PARTY_YOU_ARE_BANISHED);
				}
				break;
			case DISBAND:
				PacketSendUtility.sendPacket(leavedPlayer, SM_SYSTEM_MESSAGE.STR_PARTY_IS_DISPERSED);
				break;
			default:
				break;
		}
		
		if (leavedPlayer.isInInstance())
		{
			final Player leftPlayer = leavedPlayer;
			ThreadPoolManager.getInstance().schedule(new Runnable()
			{
				@Override
				public void run()
				{
					if (!leftPlayer.isInGroup2())
					{
						if (leftPlayer.getPosition().getWorldMapInstance().getRegisteredGroup() != null)
						{
							InstanceService.moveToExitPoint(leftPlayer);
						}
					}
				}
			}, 10000);
		}
	}
	
	/**
	 * Updates the group status for a specific member.<br>
	 * This method sends the necessary network packets to the player.<br>
	 * It also displays system messages based on the reason for leaving.
	 * @param member The {@code PlayerGroupMember} to apply the event to.
	 * @return {@code true} if the operation was successful.
	 */
	@Override
	public boolean test(PlayerGroupMember member)
	{
		final Player player = member.getObject();
		PacketSendUtility.sendPacket(player, new SM_GROUP_MEMBER_INFO(team, leavedPlayer, GroupEvent.LEAVE));
		
		switch (reason)
		{
			case LEAVE:
			case DISBAND:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_HE_LEAVE_PARTY(leavedPlayer.getName()));
				break;
			case BAN:
				// TODO find out empty strings (Retail has +2 empty strings
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_HE_IS_BANISHED(leavedPlayer.getName()));
				break;
			default:
				break;
		}
		
		return true;
	}
}
