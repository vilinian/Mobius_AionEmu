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
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event handles updates related to a {@link PlayerGroup}.<br>
 * It is triggered when changes occur within a group of players.<br>
 * It also acts as a {@code Predicate} to filter {@link Player} objects.
 * @author ATracer
 */
public class PlayerGroupUpdateEvent extends AlwaysTrueTeamEvent implements Predicate<Player>
{
	private final PlayerGroup group;
	private final Player player;
	private final GroupEvent groupEvent;
	
	/**
	 * Creates a new event for updating a {@link PlayerGroup}.<br>
	 * This constructor initializes the required data for the update.
	 * @param group The {@link PlayerGroup} being updated.
	 * @param player The {@link Player} involved in the change.
	 * @param groupEvent The specific {@link GroupEvent} that triggered this action.
	 */
	public PlayerGroupUpdateEvent(PlayerGroup group, Player player, GroupEvent groupEvent)
	{
		this.group = group;
		this.player = player;
		this.groupEvent = groupEvent;
	}
	
	/**
	 * Processes the disbanding of a group.<br>
	 * This method notifies all members of the {@code PlayerGroup}.<br>
	 * It triggers the logic defined in {@code apply}.
	 */
	@Override
	public void handleEvent()
	{
		group.applyOnMembers(this);
	}
	
	/**
	 * Updates the group member information for a specific player.<br>
	 * This method sends an {@link SM_GROUP_MEMBER_INFO} packet to the {@code member}.<br>
	 * It ensures that the update is only sent if the {@code member} is not the same as the primary {@code player}.
	 * @param member The {@code Player} who will receive the group information.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player member)
	{
		if (!player.equals(member))
		{
			PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(group, player, groupEvent));
		}
		
		return true;
	}
}
