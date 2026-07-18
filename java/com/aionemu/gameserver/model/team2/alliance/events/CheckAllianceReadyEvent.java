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
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.common.events.TeamCommand;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_READY_CHECK;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event handles the logic for checking if an alliance is ready.<br>
 * It validates whether a {@link Player} meets the requirements to proceed with alliance actions.
 * @author ATracer
 */
public class CheckAllianceReadyEvent extends AlwaysTrueTeamEvent implements Predicate<Player>
{
	private final PlayerAlliance alliance;
	private final Player player;
	private final TeamCommand eventCode;
	
	/**
	 * Creates a new instance of {@code CheckAllianceReadyEvent}.<br>
	 * This constructor initializes the required data for checking an alliance's readiness.
	 * @param alliance The {@link PlayerAlliance} associated with this event.
	 * @param player The {@link Player} who triggered the action.
	 * @param eventCode The specific {@link TeamCommand} code to process.
	 */
	public CheckAllianceReadyEvent(PlayerAlliance alliance, Player player, TeamCommand eventCode)
	{
		this.alliance = alliance;
		this.player = player;
		this.eventCode = eventCode;
	}
	
	/**
	 * Updates the ready status of the alliance based on the {@code eventCode}.<br>
	 * This method calculates the new status and applies it to all members.<br>
	 * It updates the {@code PlayerAlliance} state accordingly.
	 */
	@Override
	public void handleEvent()
	{
		int readyStatus = alliance.getAllianceReadyStatus();
		switch (eventCode)
		{
			case ALLIANCE_CHECKREADY_CANCEL:
				readyStatus = 0;
				break;
			case ALLIANCE_CHECKREADY_START:
				readyStatus = alliance.onlineMembers() - 1;
				break;
			case ALLIANCE_CHECKREADY_AUTOCANCEL:
				readyStatus = 0;
				break;
			case ALLIANCE_CHECKREADY_READY:
			case ALLIANCE_CHECKREADY_NOTREADY:
				readyStatus -= 1;
				break;
			default:
				break;
		}
		
		alliance.setAllianceReadyStatus(readyStatus);
		alliance.applyOnMembers(this);
	}
	
	/**
	 * Processes the alliance ready check for a specific player.<br>
	 * This method sends an {@link SM_ALLIANCE_READY_CHECK} packet based on the current event code.
	 * @param member The {@code Player} who will receive the update.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player member)
	{
		switch (eventCode)
		{
			case ALLIANCE_CHECKREADY_CANCEL:
				PacketSendUtility.sendPacket(member, new SM_ALLIANCE_READY_CHECK(player.getObjectId(), 0));
				break;
			case ALLIANCE_CHECKREADY_START:
				PacketSendUtility.sendPacket(member, new SM_ALLIANCE_READY_CHECK(player.getObjectId(), 5));
				PacketSendUtility.sendPacket(member, new SM_ALLIANCE_READY_CHECK(player.getObjectId(), 1));
				break;
			case ALLIANCE_CHECKREADY_AUTOCANCEL:
				PacketSendUtility.sendPacket(member, new SM_ALLIANCE_READY_CHECK(player.getObjectId(), 2));
				break;
			case ALLIANCE_CHECKREADY_READY:
				PacketSendUtility.sendPacket(member, new SM_ALLIANCE_READY_CHECK(player.getObjectId(), 5));
				if (alliance.getAllianceReadyStatus() == 0)
				{
					PacketSendUtility.sendPacket(member, new SM_ALLIANCE_READY_CHECK(0, 3));
				}
				break;
			case ALLIANCE_CHECKREADY_NOTREADY:
				PacketSendUtility.sendPacket(member, new SM_ALLIANCE_READY_CHECK(player.getObjectId(), 4));
				if (alliance.getAllianceReadyStatus() == 0)
				{
					PacketSendUtility.sendPacket(member, new SM_ALLIANCE_READY_CHECK(0, 3));
				}
				break;
			default:
				break;
		}
		
		return true;
	}
}
