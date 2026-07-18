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
import com.aionemu.gameserver.model.team2.common.events.ChangeLeaderEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event handles the logic for changing the leader of a {@link PlayerGroup}.<br>
 * It extends {@link ChangeLeaderEvent} to specifically manage group-based leadership transitions.
 * @author ATracer
 */
public class ChangeGroupLeaderEvent extends ChangeLeaderEvent<PlayerGroup>
{
	/**
	 * Creates a new {@link ChangeGroupLeaderEvent} for a specific group.<br>
	 * This event handles the logic when a leader changes within a {@code PlayerGroup}.
	 * @param team The {@code PlayerGroup} where the leadership change occurs.
	 * @param eventPlayer The {@code Player} who is triggering the event.
	 */
	public ChangeGroupLeaderEvent(PlayerGroup team, Player eventPlayer)
	{
		super(team, eventPlayer);
	}
	
	/**
	 * Creates a new event to change the leader of a group.<br>
	 * This constructor initializes the event with a specific {@link PlayerGroup}.<br>
	 * It sets the initial player for this action to {@code null}.
	 * @param team The {@code PlayerGroup} that will have its leader changed.
	 */
	public ChangeGroupLeaderEvent(PlayerGroup team)
	{
		super(team, null);
	}
	
	/**
	 * Processes the change of a group leader.<br>
	 * This method updates the leader for the {@code PlayerGroup}.<br>
	 * It handles both new leader assignments and disbanding logic.
	 */
	@Override
	public void handleEvent()
	{
		final Player oldLeader = team.getLeaderObject();
		if (eventPlayer == null)
		{
			team.applyOnMembers(this);
		}
		else
		{
			changeLeaderTo(eventPlayer);
		}
		
		checkLeaderChanged(oldLeader);
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
				PacketSendUtility.sendPacket(member, new SM_GROUP_INFO(eventTeam));
				if (!player.equals(member))
				{
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_PARTY_HE_IS_NEW_LEADER(player.getName()));
				}
				else
				{
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_PARTY_YOU_BECOME_NEW_LEADER);
				}
				
				return true;
			}
		});
	}
}
