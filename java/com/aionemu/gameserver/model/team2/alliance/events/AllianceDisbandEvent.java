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
import com.aionemu.gameserver.model.team2.common.events.PlayerLeavedEvent.LeaveReson;
import java.util.function.Predicate;

/**
 * This event is triggered when an alliance is disbanded.<br>
 * It handles the logic for cleaning up {@link PlayerAlliance} data and notifying affected players.
 * @author ATracer
 */
public class AllianceDisbandEvent extends AlwaysTrueTeamEvent implements Predicate<Player>
{
	private final PlayerAlliance alliance;
	
	/**
	 * Creates a new {@link AllianceDisbandEvent}.<br>
	 * This event is triggered when an alliance is disbanded.<br>
	 * It stores the specific {@code PlayerAlliance} that was dissolved.
	 * @param alliance The {@code PlayerAlliance} being disbanded.
	 */
	public AllianceDisbandEvent(PlayerAlliance alliance)
	{
		this.alliance = alliance;
	}
	
	/**
	 * Processes the disbanding of an alliance.<br>
	 * This method notifies all members of the {@code PlayerAlliance}.<br>
	 * It triggers the logic defined in {@code apply}.
	 */
	@Override
	public void handleEvent()
	{
		alliance.applyOnMembers(this);
	}
	
	/**
	 * Applies the alliance disband logic to a specific {@link Player}.<br>
	 * This method triggers a {@code PlayerAllianceLeavedEvent} for the player.
	 * @param player The {@code Player} who is being processed.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player player)
	{
		alliance.onEvent(new PlayerAllianceLeavedEvent(alliance, player, LeaveReson.DISBAND));
		return true;
	}
}
