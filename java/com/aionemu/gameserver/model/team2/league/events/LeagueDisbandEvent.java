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

import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.league.League;
import com.aionemu.gameserver.model.team2.league.events.LeagueLeftEvent.LeaveReson;
import java.util.function.Predicate;

/**
 * Represents an event triggered when a {@link League} is disbanded.<br>
 * This class allows the system to check if a {@code PlayerAlliance} is affected by the disbanding process.
 * @author ATracer
 */
public class LeagueDisbandEvent extends AlwaysTrueTeamEvent implements Predicate<PlayerAlliance>
{
	private final League league;
	
	/**
	 * Creates a new {@link LeagueDisbandEvent}.<br>
	 * This event is triggered when a {@code League} is disbanded.
	 * @param league The {@code League} object that was disbanded.
	 */
	public LeagueDisbandEvent(League league)
	{
		this.league = league;
	}
	
	/**
	 * Processes the disbanding of an alliance.<br>
	 * This method notifies all members of the {@code PlayerAlliance}.<br>
	 * It triggers the logic defined in {@code apply}.
	 */
	@Override
	public void handleEvent()
	{
		league.applyOnMembers(this);
	}
	
	/**
	 * Applies the league disband logic to a specific player alliance.<br>
	 * This method triggers a {@link LeagueLeftEvent} for the associated league.
	 * @param alliance The {@code PlayerAlliance} to process.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(PlayerAlliance alliance)
	{
		league.onEvent(new LeagueLeftEvent(league, alliance, LeaveReson.DISBAND));
		return true;
	}
}
