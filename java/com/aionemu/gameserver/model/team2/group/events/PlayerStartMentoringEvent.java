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
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.group.PlayerFilters.MentorSuiteFilter;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANK_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import java.util.function.Predicate;

/**
 * This event is triggered when a player begins the mentoring process.<br>
 * It handles the necessary logic for initializing mentor-related actions and notifications.
 * @author ATracer
 */
public class PlayerStartMentoringEvent extends AlwaysTrueTeamEvent implements Predicate<Player>
{
	private final PlayerGroup group;
	private final Player player;
	
	/**
	 * Creates a new event for when a {@link Player} starts mentoring.<br>
	 * This event links the specific {@link Player} to a {@link PlayerGroup}.
	 * @param group The {@code PlayerGroup} associated with this action.
	 * @param player The {@link Player} who is starting the mentoring process.
	 */
	public PlayerStartMentoringEvent(PlayerGroup group, Player player)
	{
		this.group = group;
		this.player = player;
	}
	
	/**
	 * Processes the start of a mentoring session for a {@code Player}.<br>
	 * This method updates the mentor status and sends relevant packets.<br>
	 * It also triggers group-wide logic via {@code apply}.
	 */
	@Override
	public void handleEvent()
	{
		if (group.filterMembers(new MentorSuiteFilter(player)).size() == 0)
		{
			AuditLogger.info(player, "Send fake start mentoring packet");
			return;
		}
		
		player.setMentor(true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_MENTOR_START);
		group.applyOnMembers(this);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_ABYSS_RANK_UPDATE(2, player));
	}
	
	/**
	 * Applies the mentoring start event to a specific member.<br>
	 * This method sends system messages and group information packets to the {@code member}.
	 * @param member The {@code Player} who will receive the update.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player member)
	{
		if (!player.equals(member))
		{
			PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_MENTOR_START_PARTYMSG(player.getName()));
		}
		
		PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(group, player, GroupEvent.MOVEMENT));
		return true;
	}
}
