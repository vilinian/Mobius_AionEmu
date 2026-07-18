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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupMember;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event is triggered when a {@link Player} successfully connects to the server.<br>
 * It handles the necessary logic for updating group information and notifying relevant clients.
 * @author ATracer
 */
public class PlayerConnectedEvent extends AlwaysTrueTeamEvent implements Predicate<Player>
{
	private static final Logger log = LoggerFactory.getLogger(PlayerConnectedEvent.class);
	private final PlayerGroup group;
	private final Player player;
	
	/**
	 * Creates a new event for when a {@link Player} joins a {@link PlayerGroup}.<br>
	 * This event is used to handle logic related to group membership.
	 * @param group The {@code PlayerGroup} that the player is joining.
	 * @param player The {@code Player} who has connected to the group.
	 */
	public PlayerConnectedEvent(PlayerGroup group, Player player)
	{
		this.group = group;
		this.player = player;
	}
	
	/**
	 * Processes the connection of a player to a group.<br>
	 * This method updates the {@code PlayerGroup} membership for the {@code Player}.<br>
	 * It sends updated group information packets to the client.
	 */
	@Override
	public void handleEvent()
	{
		group.removeMember(player.getObjectId());
		group.addMember(new PlayerGroupMember(player));
		
		// TODO this probably should never happen
		if (player.sameObjectId(group.getLeader().getObjectId()))
		{
			log.warn("[TEAM2] leader connected {}", group.size());
			group.changeLeader(new PlayerGroupMember(player));
		}
		
		PacketSendUtility.sendPacket(player, new SM_GROUP_INFO(group));
		PacketSendUtility.sendPacket(player, new SM_GROUP_MEMBER_INFO(group, player, GroupEvent.JOIN));
		group.applyOnMembers(this);
	}
	
	/**
	 * Updates the group information for both players.<br>
	 * This method sends {@link SM_GROUP_MEMBER_INFO} and {@link SM_INSTANCE_INFO} packets.
	 * @param member The {@code Player} being processed by this event.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player member)
	{
		if (!player.equals(member))
		{
			PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(group, player, GroupEvent.ENTER));
			PacketSendUtility.sendPacket(member, new SM_INSTANCE_INFO(player, false, group));
			PacketSendUtility.sendPacket(player, new SM_GROUP_MEMBER_INFO(group, member, GroupEvent.ENTER));
		}
		
		return true;
	}
}
