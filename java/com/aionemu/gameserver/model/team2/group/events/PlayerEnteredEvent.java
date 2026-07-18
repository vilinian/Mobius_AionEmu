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
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event is triggered when a {@link Player} joins a group.<br>
 * It handles the necessary logic and packet updates for new members.<br>
 * It implements {@link TeamEvent} to integrate with the team system.
 * @author ATracer
 */
public class PlayerEnteredEvent implements Predicate<Player>, TeamEvent
{
	private final PlayerGroup group;
	private final Player enteredPlayer;
	
	/**
	 * Creates a new event for when a player joins a group.<br>
	 * This event tracks which {@link PlayerGroup} and {@code Player} are involved.
	 * @param group The group that the player is joining.
	 * @param enteredPlayer The player who has just joined the group.
	 */
	public PlayerEnteredEvent(PlayerGroup group, Player enteredPlayer)
	{
		this.group = group;
		this.enteredPlayer = enteredPlayer;
	}
	
	/**
	 * Checks if the player is not already in the group.<br>
	 * It verifies that the {@code enteredPlayer} object ID is missing from the {@link PlayerGroup}.
	 * @return {@code true} if the player is not a member, otherwise {@code false}.
	 */
	@Override
	public boolean checkCondition()
	{
		return !group.hasMember(enteredPlayer.getObjectId());
	}
	
	/**
	 * Processes the logic for a player joining a group.<br>
	 * This method adds the {@code enteredPlayer} to the {@code group}.<br>
	 * It sends several network packets to notify the player of their new status.<br>
	 * Finally, it triggers the event logic for all existing members.
	 */
	@Override
	public void handleEvent()
	{
		PlayerGroupService.addPlayerToGroup(group, enteredPlayer);
		PacketSendUtility.sendPacket(enteredPlayer, new SM_GROUP_INFO(group));
		PacketSendUtility.sendPacket(enteredPlayer, new SM_GROUP_MEMBER_INFO(group, enteredPlayer, GroupEvent.JOIN));
		PacketSendUtility.sendPacket(enteredPlayer, SM_SYSTEM_MESSAGE.STR_PARTY_ENTERED_PARTY);
		group.applyOnMembers(this);
	}
	
	/**
	 * Updates the group information for a {@link Player}.<br>
	 * This method sends network packets to notify players about a new member.<br>
	 * It ensures that both the joining player and existing members receive updates.
	 * @param player The {@code Player} who receives the update notification.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player player)
	{
		if (!player.getObjectId().equals(enteredPlayer.getObjectId()))
		{
			// TODO probably here JOIN event
			PacketSendUtility.sendPacket(player, new SM_GROUP_MEMBER_INFO(group, enteredPlayer, GroupEvent.ENTER));
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_INFO(enteredPlayer, false, group));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_HE_ENTERED_PARTY(enteredPlayer.getName()));
			
			PacketSendUtility.sendPacket(enteredPlayer, new SM_GROUP_MEMBER_INFO(group, player, GroupEvent.ENTER));
		}
		
		return true;
	}
}
