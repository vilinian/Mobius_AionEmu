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
package com.aionemu.gameserver.model.team2.league.events;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.team2.league.League;
import com.aionemu.gameserver.model.team2.league.LeagueService;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Represents a request to invite a player to a {@link League}.<br>
 * This class handles the logic for processing and responding to league invitation events.
 * @author ATracer
 */
public class LeagueInvite extends RequestResponseHandler
{
	private final Player inviter;
	private final Player invited;
	
	/**
	 * Creates a new {@link LeagueInvite} instance.<br>
	 * This object handles the invitation between two players.
	 * @param inviter The {@code Player} who is sending the invite.
	 * @param invited The {@code Player} who is receiving the invite.
	 */
	public LeagueInvite(Player inviter, Player invited)
	{
		super(inviter);
		this.inviter = inviter;
		this.invited = invited;
	}
	
	/**
	 * Processes the request to join an alliance.<br>
	 * This method checks if the {@code invited} player can join and adds them along with their group members.<br>
	 * It also handles cases where the alliance is already full.
	 * @param requester The {@code Creature} who sent the request.
	 * @param responder The {@link Player} who is responding to the request.
	 */
	@Override
	public void acceptRequest(Creature requester, Player responder)
	{
		if (LeagueService.canInvite(inviter, invited))
		{
			PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_UNION_ENTER_HIM(invited.getName()));
			League league = inviter.getPlayerAlliance2().getLeague();
			
			if (league == null)
			{
				league = LeagueService.createLeague(inviter, invited);
			}
			else if (league.size() == 48)
			{
				PacketSendUtility.sendMessage(invited, "That league is already full.");
				PacketSendUtility.sendMessage(inviter, "Your league is already full.");
				return;
			}
			else if (invited.isInAlliance2() && ((invited.getPlayerAlliance2().size() + league.size()) > 48))
			{
				PacketSendUtility.sendMessage(invited, "That league is now too full for your group to join.");
				PacketSendUtility.sendMessage(inviter, "Your league is now too full for that group to join.");
				return;
			}
			
			if (!invited.isInLeague())
			{
				LeagueService.addAlliance(league, invited.getPlayerAlliance2());
			}
		}
	}
	
	/**
	 * This method handles the rejection of a request.<br>
	 * It notifies the {@code requester} that their action was declined by the {@link Player}.
	 * @param requester The {@code Creature} who sent the initial request.
	 * @param responder The {@code Player} who is declining the request.
	 */
	@Override
	public void denyRequest(Creature requester, Player responder)
	{
		PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_UNION_REJECT_HIM(responder.getName()));
	}
}
