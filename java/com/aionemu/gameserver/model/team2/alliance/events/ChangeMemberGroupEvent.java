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

import java.util.Objects;

import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceGroup;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceMember;
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_MEMBER_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event handles the logic for moving a {@link PlayerAllianceMember} between different groups.<br>
 * It ensures that group changes are processed correctly within the alliance system.
 * @author ATracer
 */
public class ChangeMemberGroupEvent extends AlwaysTrueTeamEvent implements Predicate<PlayerAllianceMember>
{
	private final PlayerAlliance alliance;
	private final int firstMemberId;
	private final int secondMemberId;
	private final int allianceGroupId;
	private PlayerAllianceMember firstMember;
	private PlayerAllianceMember secondMember;
	
	/**
	 * Creates a new event to swap members between groups.<br>
	 * This event handles the logic for moving players within an {@link PlayerAlliance}.
	 * @param alliance The {@link PlayerAlliance} where the change occurs.
	 * @param firstMemberId The unique ID of the first member involved in the move.
	 * @param secondMemberId The unique ID of the second member involved in the move.
	 * @param allianceGroupId The target group ID for the movement.
	 */
	public ChangeMemberGroupEvent(PlayerAlliance alliance, int firstMemberId, int secondMemberId, int allianceGroupId)
	{
		this.alliance = alliance;
		this.firstMemberId = firstMemberId;
		this.secondMemberId = secondMemberId;
		this.allianceGroupId = allianceGroupId;
	}
	
	/**
	 * Processes the change of a member's group within an alliance.<br>
	 * This method swaps two members or moves one member to a new group.<br>
	 * It then applies the changes to the {@code PlayerAlliance}.
	 */
	@Override
	public void handleEvent()
	{
		firstMember = alliance.getMember(firstMemberId);
		secondMember = alliance.getMember(secondMemberId);
		Objects.requireNonNull(firstMember, "First member should not be null");
		if (!((secondMemberId == 0) || (secondMember != null)))
		{
			throw new IllegalArgumentException("Second member should not be null");
		}
		if (secondMember != null)
		{
			swapMembersInGroup(firstMember, secondMember);
		}
		else
		{
			moveMemberToGroup(firstMember, allianceGroupId);
		}
		
		alliance.apply(this);
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
		PacketSendUtility.sendPacket(member.getObject(), new SM_ALLIANCE_MEMBER_INFO(firstMember, PlayerAllianceEvent.MEMBER_GROUP_CHANGE));
		if (secondMember != null)
		{
			PacketSendUtility.sendPacket(member.getObject(), new SM_ALLIANCE_MEMBER_INFO(secondMember, PlayerAllianceEvent.MEMBER_GROUP_CHANGE));
		}
		
		return true;
	}
	
	/**
	 * Swaps the positions of two members between their respective groups.<br>
	 * This method removes each member from their current {@link PlayerAllianceGroup}.<br>
	 * It then adds each member to the other person's original group.
	 * @param firstMember The first {@code PlayerAllianceMember} to be moved.
	 * @param secondMember The second {@code PlayerAllianceMember} to be moved.
	 */
	private void swapMembersInGroup(PlayerAllianceMember firstMember, PlayerAllianceMember secondMember)
	{
		final PlayerAllianceGroup firstAllianceGroup = firstMember.getPlayerAllianceGroup();
		final PlayerAllianceGroup secondAllianceGroup = secondMember.getPlayerAllianceGroup();
		firstAllianceGroup.removeMember(firstMember);
		secondAllianceGroup.removeMember(secondMember);
		firstAllianceGroup.addMember(secondMember);
		secondAllianceGroup.addMember(firstMember);
	}
	
	/**
	 * Moves a member from their current group to a different group.<br>
	 * This method removes the {@code firstMember} from their old group.<br>
	 * It then adds them to the group identified by {@code allianceGroupId}.
	 * @param firstMember The {@link PlayerAllianceMember} to be moved.
	 * @param allianceGroupId The ID of the target {@link PlayerAllianceGroup}.
	 */
	private void moveMemberToGroup(PlayerAllianceMember firstMember, int allianceGroupId)
	{
		final PlayerAllianceGroup firstAllianceGroup = firstMember.getPlayerAllianceGroup();
		firstAllianceGroup.removeMember(firstMember);
		final PlayerAllianceGroup newAllianceGroup = alliance.getAllianceGroup(allianceGroupId);
		newAllianceGroup.addMember(firstMember);
	}
}
