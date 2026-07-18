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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.team2.group.events.ChangeGroupLeaderEvent;

/**
 * This event is triggered when the leader of a {@link TemporaryPlayerTeam} changes.<br>
 * It handles the logic for updating leadership roles within a team structure.
 * @author ATracer
 * @param <T>
 */
public abstract class ChangeLeaderEvent<T extends TemporaryPlayerTeam<?>>extends AbstractTeamPlayerEvent<T>
{
	private static final Logger log = LoggerFactory.getLogger(ChangeGroupLeaderEvent.class);
	
	/**
	 * Creates a new instance of a leader change event.<br>
	 * This event handles the process of changing a team leader.
	 * @param team The {@code T} type team involved in the change.
	 * @param eventPlayer The {@link Player} who triggered this event.
	 */
	public ChangeLeaderEvent(T team, Player eventPlayer)
	{
		super(team, eventPlayer);
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
		return (eventPlayer == null) || eventPlayer.isOnline();
	}
	
	/**
	 * Updates the team leader to the specified {@link Player}.<br>
	 * This method checks if the player is online and not already the leader.
	 * @param player The {@code Player} to be evaluated for leadership.
	 * @return {@code false} if the leader was successfully changed, otherwise {@code true}.
	 */
	@Override
	public boolean test(Player player)
	{
		if (!player.getObjectId().equals(team.getLeader().getObjectId()) && player.isOnline())
		{
			changeLeaderTo(player);
			return false;
		}
		
		return true;
	}
	
	/**
	 * This method verifies if the group leader has actually changed.<br>
	 * It compares the current state against the {@code oldLeader}.<br>
	 * If no change occurred, it logs the team status to the console.
	 * @param oldLeader The {@code Player} who was previously the leader.
	 */
	protected void checkLeaderChanged(Player oldLeader)
	{
		if (team.isLeader(oldLeader))
		{
			log.info("TEAM2: leader is not changed, total: {}, online: {}", team.size(), team.onlineMembers());
		}
	}
	
	protected abstract void changeLeaderTo(Player player);
}
