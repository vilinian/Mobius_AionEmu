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
package com.aionemu.gameserver.model.team2.common.events;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamMember;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the distribution of {@code Kinah} among members of a team.<br>
 * This event ensures that rewards are correctly allocated to players within a {@link TemporaryPlayerTeam}.
 * @author ATracer
 * @param <T>
 */
public class TeamKinahDistributionEvent<T extends TemporaryPlayerTeam<? extends TeamMember<Player>>>extends AbstractTeamPlayerEvent<T>
{
	private final long amount;
	private long rewardPerPlayer;
	private long teamSize;
	
	/**
	 * Creates a new event for distributing {@code Kinah} to a team.<br>
	 * This constructor initializes the distribution details.
	 * @param team The {@code T} team that will receive the rewards.
	 * @param distributor The {@link Player} who is giving out the currency.
	 * @param amount The total amount of {@code Kinah} to be distributed.
	 */
	public TeamKinahDistributionEvent(T team, Player distributor, long amount)
	{
		super(team, distributor);
		this.amount = amount;
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
		return team.hasMember(eventPlayer.getObjectId());
	}
	
	/**
	 * Processes the distribution of Kinah to team members.<br>
	 * This method checks if the distributor has enough currency.<br>
	 * It calculates the reward based on the number of online members.<br>
	 * It then triggers the logic defined in {@code apply}.
	 */
	@Override
	public void handleEvent()
	{
		if (eventPlayer.getInventory().getKinah() < amount)
		{
			// TODO retail message ?
			return;
		}
		
		teamSize = team.onlineMembers();
		if (teamSize <= amount)
		{
			rewardPerPlayer = amount / teamSize;
			team.applyOnMembers(this);
		}
	}
	
	/**
	 * Distributes Kinah to a specific player based on the event rules.<br>
	 * This method updates the {@code Player} inventory and sends a system message.
	 * @param member The {@code Player} who will receive the update.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player member)
	{
		if (member.isOnline())
		{
			if (member.equals(eventPlayer))
			{
				member.getInventory().decreaseKinah(amount);
				member.getInventory().increaseKinah(rewardPerPlayer);
				PacketSendUtility.sendPacket(eventPlayer, new SM_SYSTEM_MESSAGE(1390247, amount, teamSize, rewardPerPlayer));
			}
			else
			{
				member.getInventory().increaseKinah(rewardPerPlayer);
				PacketSendUtility.sendPacket(member, new SM_SYSTEM_MESSAGE(1390248, eventPlayer.getName(), amount, teamSize, rewardPerPlayer));
			}
		}
		
		return true;
	}
}
