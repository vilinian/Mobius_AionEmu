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
import com.aionemu.gameserver.model.team2.TeamEvent;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceMember;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHOW_BRAND;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event is triggered when a {@link Player} joins an alliance.<br>
 * It handles the necessary logic and notifications for new members joining the group.
 * @author ATracer
 */
public class PlayerEnteredEvent implements Predicate<PlayerAllianceMember>, TeamEvent
{
	private final PlayerAlliance alliance;
	private final Player invited;
	private PlayerAllianceMember invitedMember;
	
	/**
	 * Creates a new event for when a {@link Player} joins an alliance.<br>
	 * This constructor initializes the event with the target alliance and player.
	 * @param alliance The {@code PlayerAlliance} that the player is joining.
	 * @param player The {@code Player} who has entered the alliance.
	 */
	public PlayerEnteredEvent(PlayerAlliance alliance, Player player)
	{
		this.alliance = alliance;
		invited = player;
	}
	
	/**
	 * Checks if the player is not already in the alliance.<br>
	 * It verifies that the {@code invited} player's ID is missing from the {@link PlayerAlliance}.
	 * @return {@code true} if the player is not a member, otherwise {@code false}.
	 */
	@Override
	public boolean checkCondition()
	{
		return !alliance.hasMember(invited.getObjectId());
	}
	
	/**
	 * Processes the entry of a player into an alliance.<br>
	 * This method adds the {@code invited} player to the {@code alliance}.<br>
	 * It sends several network packets to notify the player of their new status.
	 */
	@Override
	public void handleEvent()
	{
		PlayerAllianceService.addPlayerToAlliance(alliance, invited);
		
		invitedMember = alliance.getMember(invited.getObjectId());
		
		PacketSendUtility.sendPacket(invited, new SM_ALLIANCE_INFO(alliance));
		PacketSendUtility.sendPacket(invited, new SM_SHOW_BRAND(0, 0));
		PacketSendUtility.sendPacket(invited, SM_SYSTEM_MESSAGE.STR_FORCE_ENTERED_FORCE);
		PacketSendUtility.sendPacket(invited, new SM_ALLIANCE_MEMBER_INFO(invitedMember, PlayerAllianceEvent.JOIN));
		
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
		final Player player = member.getObject();
		if (!invited.getObjectId().equals(player.getObjectId()))
		{
			PacketSendUtility.sendPacket(player, new SM_ALLIANCE_MEMBER_INFO(invitedMember, PlayerAllianceEvent.JOIN));
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_INFO(invited, false, alliance));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FORCE_HE_ENTERED_FORCE(invited.getName()));
			
			PacketSendUtility.sendPacket(invited, new SM_ALLIANCE_MEMBER_INFO(member, PlayerAllianceEvent.ENTER));
		}
		
		return true;
	}
}
