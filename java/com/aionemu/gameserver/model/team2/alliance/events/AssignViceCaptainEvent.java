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
import com.aionemu.gameserver.model.team2.common.events.AbstractTeamPlayerEvent;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This event handles the process of assigning a new vice captain to an {@link PlayerAlliance}.<br>
 * It manages the necessary logic and notifications when a player is promoted to this role.
 * @author ATracer
 */
public class AssignViceCaptainEvent extends AbstractTeamPlayerEvent<PlayerAlliance>
{
	public static enum AssignType
	{
		PROMOTE,
		DEMOTE_CAPTAIN_TO_VICECAPTAIN,
		DEMOTE
	}
	
	private final AssignType assignType;
	
	/**
	 * Creates a new instance of an event for assigning a vice captain.<br>
	 * This event handles changes to the leadership roles within a {@link PlayerAlliance}.
	 * @param team The alliance where the assignment takes place.
	 * @param eventPlayer The player who is being assigned the role.
	 * @param assignType The specific type of assignment, such as {@code PROMOTE} or {@code DEMOTE}.
	 */
	public AssignViceCaptainEvent(PlayerAlliance team, Player eventPlayer, AssignType assignType)
	{
		super(team, eventPlayer);
		this.assignType = assignType;
	}
	
	/**
	 * Verifies if the event can be processed.<br>
	 * It checks if the {@code eventPlayer} is not {@code null}.<br>
	 * It also ensures that the player is currently online.
	 * @return {@code true} if all conditions are met, otherwise {@code false}.
	 */
	@Override
	public boolean checkCondition()
	{
		return (eventPlayer != null) && eventPlayer.isOnline();
	}
	
	/**
	 * Processes the assignment of a vice captain.<br>
	 * This method updates the {@code PlayerAlliance} based on the {@code assignType}.<br>
	 * It handles promotion, demotion, or specific rank changes for the player.
	 */
	@Override
	public void handleEvent()
	{
		switch (assignType)
		{
			case DEMOTE:
				team.getViceCaptainIds().remove(eventPlayer.getObjectId());
				break;
			case PROMOTE:
				if (team.getViceCaptainIds().size() == 4)
				{
					PacketSendUtility.sendPacket(team.getLeaderObject(), SM_SYSTEM_MESSAGE.STR_FORCE_CANNOT_PROMOTE_MANAGER);
					return;
				}
				
				team.getViceCaptainIds().add(eventPlayer.getObjectId());
				break;
			case DEMOTE_CAPTAIN_TO_VICECAPTAIN:
				team.getViceCaptainIds().add(eventPlayer.getObjectId());
				break;
		}
		
		team.applyOnMembers(this);
	}
	
	/**
	 * Applies the vice captain assignment logic to a specific {@link Player}.<br>
	 * This method sends an {@code SM_ALLIANCE_INFO} packet to the player based on the promotion type.
	 * @param player The {@code Player} who receives the alliance information packet.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player player)
	{
		int messageId = 0;
		switch (assignType)
		{
			case PROMOTE:
				messageId = SM_ALLIANCE_INFO.FORCE_PROMOTE_MANAGER;
				break;
			case DEMOTE:
				messageId = SM_ALLIANCE_INFO.FORCE_DEMOTE_MANAGER;
				break;
			default:
				break;
		}
		
		// TODO check whether same is sent to eventPlayer
		PacketSendUtility.sendPacket(player, new SM_ALLIANCE_INFO(team, messageId, eventPlayer.getName()));
		return true;
	}
}
