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
package com.aionemu.gameserver.model.team2.group;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.PlayerTeamMember;

/**
 * Represents a {@link Player} who is part of a specific group.<br>
 * This class extends {@link PlayerTeamMember} to handle group-specific logic.
 * @author ATracer
 */
public class PlayerGroupMember extends PlayerTeamMember
{
	/**
	 * Creates a new {@link PlayerGroupMember} instance.<br>
	 * This constructor links a specific {@code Player} to a group.
	 * @param player The {@code Player} object to add to the group.
	 */
	public PlayerGroupMember(Player player)
	{
		super(player);
	}
}
