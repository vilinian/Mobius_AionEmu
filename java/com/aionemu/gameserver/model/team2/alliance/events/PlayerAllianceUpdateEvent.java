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
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceMember;
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_MEMBER_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event handles updates related to a {@link Player}'s membership in an alliance.<br>
 * It is triggered when changes occur within the {@link PlayerAlliance} structure.
 * @author ATracer
 */
public class PlayerAllianceUpdateEvent extends AlwaysTrueTeamEvent implements Predicate<PlayerAllianceMember>
{
	private final PlayerAlliance alliance;
	private final Player player;
	private final PlayerAllianceEvent allianceEvent;
	private final PlayerAllianceMember updateMember;
	
	/**
	 * Creates a new event to update a player's status within an alliance.<br>
	 * This constructor initializes the required data for the {@link PlayerAllianceUpdateEvent}.<br>
	 * It automatically retrieves the {@code PlayerAllianceMember} using the player's object ID.
	 * @param alliance The {@link PlayerAlliance} that the player belongs to.
	 * @param player The {@link Player} who is being updated.
	 * @param allianceEvent The specific {@link PlayerAllianceEvent} being processed.
	 */
	public PlayerAllianceUpdateEvent(PlayerAlliance alliance, Player player, PlayerAllianceEvent allianceEvent)
	{
		this.alliance = alliance;
		this.player = player;
		this.allianceEvent = allianceEvent;
		updateMember = alliance.getMember(player.getObjectId());
	}
	
	/**
	 * Processes the update or movement of an alliance.<br>
	 * This method applies the logic for the specific {@code PlayerAllianceEvent}.<br>
	 * It updates the state of the {@code PlayerAlliance} based on the event type.
	 */
	@Override
	public void handleEvent()
	{
		switch (allianceEvent)
		{
			case MOVEMENT:
			case UPDATE:
				alliance.apply(this);
				break;
			default:
				// Unsupported
				break;
		}
		
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
		if (!member.getObjectId().equals(player.getObjectId()))
		{
			PacketSendUtility.sendPacket(member.getObject(), new SM_ALLIANCE_MEMBER_INFO(updateMember, allianceEvent));
		}
		
		return true;
	}
}
