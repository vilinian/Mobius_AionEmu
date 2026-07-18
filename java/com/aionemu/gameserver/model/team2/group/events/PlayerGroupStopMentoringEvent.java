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
import com.aionemu.gameserver.model.team2.common.events.PlayerStopMentoringEvent;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This event is triggered when a player stops mentoring within a {@link PlayerGroup}.<br>
 * It extends the base {@link PlayerStopMentoringEvent} to handle group-specific logic.
 * @author ATracer
 */
public class PlayerGroupStopMentoringEvent extends PlayerStopMentoringEvent<PlayerGroup>
{
	/**
	 * This event is triggered when a {@link Player} stops mentoring within a {@link PlayerGroup}.<br>
	 * It handles the logic for ending the mentoring status.
	 * @param group The {@code PlayerGroup} where the action occurred.
	 * @param player The {@code Player} who stopped mentoring.
	 */
	public PlayerGroupStopMentoringEvent(PlayerGroup group, Player player)
	{
		super(group, player);
	}
	
	/**
	 * Sends a group information packet to a specific member.<br>
	 * This occurs when the mentoring session ends for that player.<br>
	 * It uses {@code sendPacket} to deliver the data.
	 * @param member The {@code Player} who will receive the network packet.
	 */
	@Override
	protected void sendGroupPacketOnMentorEnd(Player member)
	{
		PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(team, player, GroupEvent.MOVEMENT));
	}
}
