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
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHOW_BRAND;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event handles the logic for displaying a brand to players.<br>
 * It checks if a {@link Player} should receive the {@code SM_SHOW_BRAND} packet based on team membership.
 * @author ATracer
 * @param <T>
 */
public class ShowBrandEvent<T extends TemporaryPlayerTeam<? extends TeamMember<Player>>>extends AlwaysTrueTeamEvent implements Predicate<Player>
{
	private final T team;
	private final int targetObjId;
	private final int brandId;
	
	/**
	 * Creates a new {@link ShowBrandEvent} to display a specific brand.<br>
	 * This event links a team with a target object and a unique brand identifier.
	 * @param team The {@code T} team instance associated with this event.
	 * @param targetObjId The unique ID of the target object to display the brand on.
	 * @param brandId The specific ID of the brand to be shown.
	 */
	public ShowBrandEvent(T team, int targetObjId, int brandId)
	{
		this.team = team;
		this.targetObjId = targetObjId;
		this.brandId = brandId;
	}
	
	/**
	 * Processes the brand display event.<br>
	 * This method applies the logic to all members of the {@code team}.<br>
	 * It triggers the action defined in {@code apply}.
	 */
	@Override
	public void handleEvent()
	{
		team.applyOnMembers(this);
	}
	
	/**
	 * Sends a brand display packet to the specified player.<br>
	 * This method uses {@link PacketSendUtility} to deliver the data.
	 * @param member The {@code Player} who will receive the update.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player member)
	{
		PacketSendUtility.sendPacket(member, new SM_SHOW_BRAND(brandId, targetObjId));
		return true;
	}
}
