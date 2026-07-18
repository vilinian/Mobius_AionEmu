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
import com.aionemu.gameserver.model.team2.TeamEvent;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event is triggered when a {@link Player} disconnects from the server.<br>
 * It handles the necessary logic to update group statuses and notify other members.<br>
 * It implements {@link Predicate} to check player states and extends {@link TeamEvent}.
 * @author ATracer
 */
public class PlayerDisconnectedEvent implements Predicate<Player>, TeamEvent
{
	private final PlayerGroup group;
	private final Player player;
	
	/**
	 * Creates a new event for when a {@link Player} leaves a {@link PlayerGroup}.<br>
	 * This event is used to handle cleanup and notifications.
	 * @param group The {@code PlayerGroup} that the player was part of.
	 * @param player The {@code Player} who disconnected from the game.
	 */
	public PlayerDisconnectedEvent(PlayerGroup group, Player player)
	{
		this.group = group;
		this.player = player;
	}
	
	/**
	 * Verifies if the player belongs to the current group.<br>
	 * It checks if the {@code player} is a member of the {@link PlayerGroup}.
	 * @return {@code true} if the player is in the group, otherwise {@code false}.
	 */
	@Override
	public boolean checkCondition()
	{
		return group.hasMember(player.getObjectId());
	}
	
	/**
	 * Processes the logic for a player disconnecting from a group.<br>
	 * It checks if the group should be disbanded based on remaining members.<br>
	 * If the disconnected player was the leader, it triggers {@link ChangeGroupLeaderEvent}.<br>
	 * Otherwise, it applies the event to all remaining members.
	 */
	@Override
	public void handleEvent()
	{
		if (group.onlineMembers() <= 1)
		{
			PlayerGroupService.disband(group);
		}
		else
		{
			if (player.equals(group.getLeader().getObject()))
			{
				group.onEvent(new ChangeGroupLeaderEvent(group));
			}
			
			group.applyOnMembers(this);
		}
	}
	
	/**
	 * Updates the group members when a player disconnects.<br>
	 * This method sends system messages and group info packets to other members.
	 * @param member The {@code Player} who will receive the update.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player member)
	{
		if (!member.equals(player))
		{
			PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_PARTY_HE_BECOME_OFFLINE(player.getName()));
			PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(group, player, GroupEvent.DISCONNECTED));
			
			// disconnect other group members on logout? check
			PacketSendUtility.sendPacket(player, new SM_GROUP_MEMBER_INFO(group, member, GroupEvent.DISCONNECTED));
		}
		
		return true;
	}
}
