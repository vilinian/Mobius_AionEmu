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
package com.aionemu.gameserver.model.team2.alliance.events;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceMember;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.common.events.PlayerLeavedEvent;
import com.aionemu.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEAVE_GROUP_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * This event is triggered when a {@link Player} leaves an alliance.<br>
 * It handles the necessary logic to update the {@link PlayerAlliance} and notify other members.
 * @author ATracer
 */
public class PlayerAllianceLeavedEvent extends PlayerLeavedEvent<PlayerAllianceMember, PlayerAlliance>
{
	/**
	 * Creates a new event for when a {@link Player} leaves an alliance.<br>
	 * This constructor initializes the event with the specific alliance and player involved.
	 * @param alliance The {@code PlayerAlliance} that the player is leaving.
	 * @param player The {@code Player} who is departing from the alliance.
	 */
	public PlayerAllianceLeavedEvent(PlayerAlliance alliance, Player player)
	{
		super(alliance, player);
	}
	
	/**
	 * Creates a new event for when a player leaves an alliance.<br>
	 * This constructor includes the specific reason and any related ban information.
	 * @param team The {@code PlayerAlliance} that the player is leaving.
	 * @param player The {@code Player} who is departing from the alliance.
	 * @param reason The {@code PlayerLeavedEvent.LeaveReson} explaining why the player left.
	 * @param banPersonName The name of the person responsible for a ban, if applicable.
	 */
	public PlayerAllianceLeavedEvent(PlayerAlliance team, Player player, PlayerLeavedEvent.LeaveReson reason, String banPersonName)
	{
		super(team, player, reason, banPersonName);
	}
	
	/**
	 * Creates a new event for when a player leaves an alliance.<br>
	 * This constructor initializes the event with the specific alliance and player details.<br>
	 * It also captures the reason why the player left.
	 * @param alliance The {@link PlayerAlliance} that the player is leaving.
	 * @param player The {@link Player} who is departing from the alliance.
	 * @param reason The {@code LeaveReson} provided by the {@link PlayerLeavedEvent} class.
	 */
	public PlayerAllianceLeavedEvent(PlayerAlliance alliance, Player player, PlayerLeavedEvent.LeaveReson reason)
	{
		super(alliance, player, reason);
	}
	
	/**
	 * Processes the logic for a player leaving an alliance.<br>
	 * This method removes the member from the {@code PlayerAlliance}.<br>
	 * It handles packet notifications and checks if the alliance should be disbanded.
	 */
	@Override
	public void handleEvent()
	{
		team.removeMember(leavedPlayer.getObjectId());
		team.getViceCaptainIds().remove(leavedPlayer.getObjectId());
		
		if (leavedPlayer.isOnline())
		{
			PacketSendUtility.sendPacket(leavedPlayer, new SM_LEAVE_GROUP_MEMBER());
		}
		
		team.apply(this);
		
		switch (reason)
		{
			case BAN:
			case LEAVE:
			case LEAVE_TIMEOUT:
				if (team.onlineMembers() <= 1)
				{
					PlayerAllianceService.disband(team);
				}
				else
				{
					if (leavedPlayer.equals(team.getLeader().getObject()))
					{
						team.onEvent(new ChangeAllianceLeaderEvent(team));
					}
				}
				
				if (reason == LeaveReson.BAN)
				{
					PacketSendUtility.sendPacket(leavedPlayer, SM_SYSTEM_MESSAGE.STR_FORCE_BAN_ME(banPersonName));
				}
				
				break;
			case DISBAND:
				PacketSendUtility.sendPacket(leavedPlayer, SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_DISPERSED);
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
					if (!leftPlayer.isInAlliance2())
					{
						final WorldMapInstance instance = leftPlayer.getPosition().getWorldMapInstance();
						if ((instance.getRegistredAlliance() != null) || (instance.getRegistredLeague() != null))
						{
							InstanceService.moveToExitPoint(leftPlayer);
						}
					}
				}
			}, 10000);
		}
	}
	
	/**
	 * Sends the necessary network packets to update member information.<br>
	 * This method notifies the client about group changes for both members.
	 * @param member The {@code PlayerAllianceMember} receiving the update.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(PlayerAllianceMember member)
	{
		final Player player = member.getObject();
		
		PacketSendUtility.sendPacket(player, new SM_ALLIANCE_MEMBER_INFO(leavedTeamMember, PlayerAllianceEvent.LEAVE));
		PacketSendUtility.sendPacket(player, new SM_ALLIANCE_INFO(team));
		
		switch (reason)
		{
			case LEAVE_TIMEOUT:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_HE_LEAVED_PARTY(leavedPlayer.getName()));
				break;
			case LEAVE:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_HE_LEAVED_PARTY(leavedPlayer.getName()));
				break;
			case DISBAND:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_DISPERSED);
				break;
			case BAN:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FORCE_BAN_HIM(banPersonName, leavedPlayer.getName()));
				break;
		}
		
		return true;
	}
}
