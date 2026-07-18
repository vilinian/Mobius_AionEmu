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
import com.aionemu.gameserver.model.team2.TeamEvent;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;

import java.util.function.Predicate;

/**
 * This class serves as a base for events involving players within a {@link TemporaryPlayerTeam}.<br>
 * It provides common functionality for handling team-related actions and implements the {@code Predicate} interface to filter {@link Player} objects.
 * @author ATracer
 * @param <T>
 */
public abstract class AbstractTeamPlayerEvent<T extends TemporaryPlayerTeam<?>> implements Predicate<Player>, TeamEvent
{
	protected final T team;
	protected final Player eventPlayer;
	
	/**
	 * Creates a new instance of an abstract team player event.<br>
	 * This constructor initializes the required team and player data.
	 * @param team The {@code T} type team associated with this event.
	 * @param eventPlayer The {@link Player} who triggered the event.
	 */
	public AbstractTeamPlayerEvent(T team, Player eventPlayer)
	{
		this.team = team;
		this.eventPlayer = eventPlayer;
	}
}
