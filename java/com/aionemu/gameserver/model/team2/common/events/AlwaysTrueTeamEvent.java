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

import com.aionemu.gameserver.model.team2.TeamEvent;

/**
 * This class serves as a base for team events that are always considered valid.<br>
 * It provides a default implementation where the event condition consistently returns {@code true}.<br>
 * Use this when an action should not be restricted by specific logic checks.
 * @author ATracer
 */
public abstract class AlwaysTrueTeamEvent implements TeamEvent
{
	/**
	 * Checks if the current event conditions are met.<br>
	 * This method always returns {@code true}.
	 * @return {@code true} regardless of the state.
	 */
	@Override
	public boolean checkCondition()
	{
		return true;
	}
}
