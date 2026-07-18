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

import java.util.Collection;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceMember;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.alliance.events.AssignViceCaptainEvent.AssignType;
import com.aionemu.gameserver.model.team2.common.events.ChangeLeaderEvent;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event handles the process of changing the leader of a {@link PlayerAlliance}.<br>
 * It manages the necessary logic to update leadership roles and notify relevant players.<br>
 * It extends {@link ChangeLeaderEvent} to provide specific functionality for alliance structures.
 * @author ATracer
 */
public class ChangeAllianceLeaderEvent extends ChangeLeaderEvent<PlayerAlliance>
{
	/**
	 * Creates a new event to change the leader of an alliance.<br>
	 * This constructor initializes the event with the target team and the player who triggered it.
	 * @param team The {@link PlayerAlliance} where the leadership change will occur.
	 * @param eventPlayer The {@link Player} who is initiating the change.
	 */
	public ChangeAllianceLeaderEvent(PlayerAlliance team, Player eventPlayer)
	{
		super(team, eventPlayer);
	}
	
	/**
	 * Creates a new event to change the leader of an alliance.<br>
	 * This constructor initializes the event with a specific {@link PlayerAlliance}.<br>
	 * It sets the initial player for the event to {@code null}.
	 * @param team The {@link PlayerAlliance} that will have its leader changed.
	 */
	public ChangeAllianceLeaderEvent(PlayerAlliance team)
	{
		super(team, null);
	}
	
	/**
	 * Processes the change of an alliance leader.<br>
	 * This method updates the leadership based on the provided {@code eventPlayer}.<br>
	 * If no player is specified, it promotes an online vice captain to leader.
	 */
	@Override
	public void handleEvent()
	{
		final Player oldLeader = team.getLeaderObject();
		if (eventPlayer == null)
		{
			final Collection<Integer> viceCaptainIds = team.getViceCaptainIds();
			for (Integer viceCaptainId : viceCaptainIds)
			{
				final PlayerAllianceMember viceCaptain = team.getMember(viceCaptainId);
				if (viceCaptain.isOnline())
				{
					changeLeaderTo(viceCaptain.getObject());
					viceCaptainIds.remove(viceCaptainId);
					break;
				}
			}
			
			if (team.isLeader(oldLeader))
			{
				team.applyOnMembers(this);
			}
		}
		else
		{
			changeLeaderTo(eventPlayer);
		}
		
		checkLeaderChanged(oldLeader);
		if (eventPlayer != null)
		{
			PlayerAllianceService.changeViceCaptain(oldLeader, AssignType.DEMOTE_CAPTAIN_TO_VICECAPTAIN);
		}
	}
	
	/**
	 * Updates the leader of the alliance to a new player.<br>
	 * This method updates the team data and sends notification packets to all members.
	 * @param player The {@code Player} who will become the new leader.
	 */
	@Override
	protected void changeLeaderTo(Player player)
	{
		team.changeLeader(team.getMember(player.getObjectId()));
		final var eventTeam = team;
		team.applyOnMembers(new Predicate<Player>()
		{
			@Override
			public boolean test(Player member)
			{
				PacketSendUtility.sendPacket(member, new SM_ALLIANCE_INFO(eventTeam));
				if (!player.equals(member))
				{
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_FORCE_HE_IS_NEW_LEADER(player.getName()));
				}
				else
				{
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_FORCE_YOU_BECOME_NEW_LEADER);
				}
				
				return true;
			}
		});
	}
}
