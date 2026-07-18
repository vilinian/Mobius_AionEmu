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
package com.aionemu.gameserver.model.team2.group.events;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.common.events.PlayerLeavedEvent.LeaveReson;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import java.util.function.Predicate;

/**
 * This event is triggered when a {@link PlayerGroup} is disbanded.<br>
 * It provides information about the group dissolution to other systems.
 * @author ATracer
 */
public class GroupDisbandEvent extends AlwaysTrueTeamEvent implements Predicate<Player>
{
	private final PlayerGroup group;
	
	/**
	 * Creates a new {@link GroupDisbandEvent}.<br>
	 * This event is triggered when a {@code PlayerGroup} is disbanded.
	 * @param group The {@code PlayerGroup} that was disbanded.
	 */
	public GroupDisbandEvent(PlayerGroup group)
	{
		this.group = group;
	}
	
	/**
	 * Processes the disbanding of a group.<br>
	 * This method notifies all members of the {@code PlayerGroup}.<br>
	 * It triggers the logic defined in {@code apply}.
	 */
	@Override
	public void handleEvent()
	{
		group.applyOnMembers(this);
	}
	
	/**
	 * Applies the group disband logic to a specific {@link Player}.<br>
	 * This method triggers a {@code PlayerGroupLeavedEvent} for the player.
	 * @param player The {@code Player} who is being processed.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player player)
	{
		group.onEvent(new PlayerGroupLeavedEvent(group, player, LeaveReson.DISBAND));
		return true;
	}
}
