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
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHOW_BRAND;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event is triggered when a {@link Player} connects to the game server.<br>
 * It handles the initialization of alliance-related data for the player.<br>
 * It also serves as a {@code Predicate} to check {@link PlayerAllianceMember} status.
 * @author ATracer
 */
public class PlayerConnectedEvent extends AlwaysTrueTeamEvent implements Predicate<PlayerAllianceMember>
{
	private final PlayerAlliance alliance;
	private final Player connected;
	private PlayerAllianceMember connectedMember;
	
	/**
	 * Creates a new event for when a {@link Player} joins an alliance.<br>
	 * This event is used to handle connection logic for the {@code PlayerAlliance}.
	 * @param alliance The {@code PlayerAlliance} that the player is joining.
	 * @param player The {@code Player} who has just connected.
	 */
	public PlayerConnectedEvent(PlayerAlliance alliance, Player player)
	{
		this.alliance = alliance;
		connected = player;
	}
	
	/**
	 * Processes the connection of a player to an alliance.<br>
	 * This method updates the {@code PlayerAlliance} member list.<br>
	 * It sends several network packets to the {@code connected} player.<br>
	 * Finally, it triggers the logic in {@code apply}.
	 */
	@Override
	public void handleEvent()
	{
		alliance.removeMember(connected.getObjectId());
		connectedMember = new PlayerAllianceMember(connected);
		alliance.addMember(connectedMember);
		
		PacketSendUtility.sendPacket(connected, new SM_ALLIANCE_INFO(alliance));
		PacketSendUtility.sendPacket(connected, new SM_ALLIANCE_MEMBER_INFO(connectedMember, PlayerAllianceEvent.RECONNECT));
		PacketSendUtility.sendPacket(connected, new SM_SHOW_BRAND(0, 0));
		
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
		if (!connected.getObjectId().equals(player.getObjectId()))
		{
			PacketSendUtility.sendPacket(player, new SM_ALLIANCE_MEMBER_INFO(connectedMember, PlayerAllianceEvent.RECONNECT));
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_INFO(connected, false, alliance));
			
			PacketSendUtility.sendPacket(connected, new SM_ALLIANCE_MEMBER_INFO(member, PlayerAllianceEvent.RECONNECT));
		}
		
		return true;
	}
}
