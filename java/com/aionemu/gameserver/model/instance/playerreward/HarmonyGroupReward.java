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
package com.aionemu.gameserver.model.instance.playerreward;

import java.util.List;

import com.aionemu.gameserver.model.autogroup.AGPlayer;

/**
 * Represents rewards granted to players within a {@link AGPlayer} group during Harmony events.<br>
 * This class extends {@link PvPArenaPlayerReward} to handle specific reward logic for these groups.
 * @author xTz
 */
public class HarmonyGroupReward extends PvPArenaPlayerReward
{
	private final List<AGPlayer> players;
	
	/**
	 * Creates a new {@link HarmonyGroupReward} instance.<br>
	 * This constructor initializes the reward with specific group data.
	 * @param object The unique identifier for the target.
	 * @param timeBonus The amount of bonus time to apply.
	 * @param buffId The ID of the buff to be granted.
	 * @param players The list of {@link AGPlayer} objects receiving the reward.
	 */
	public HarmonyGroupReward(Integer object, int timeBonus, byte buffId, List<AGPlayer> players)
	{
		super(object, timeBonus, buffId);
		this.players = players;
	}
	
	/**
	 * Retrieves the list of {@link AGPlayer} objects associated with this reward.<br>
	 * This method returns all players included in the group.
	 * @return a {@code List} of {@code AGPlayer} objects.
	 */
	public List<AGPlayer> getAGPlayers()
	{
		return players;
	}
	
	/**
	 * Checks if a specific player is part of the group rewards.<br>
	 * It compares the provided {@code object} ID against all players in the list.
	 * @param object The unique identifier of the player to check.
	 * @return {@code true} if the player is found, otherwise {@code false}.
	 */
	public boolean containPlayer(Integer object)
	{
		for (AGPlayer agp : players)
		{
			if (agp.getObjectId().equals(object))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Finds a specific {@link AGPlayer} based on their unique ID.<br>
	 * It searches through the internal list of players.
	 * @param object The unique identifier to search for.
	 * @return The matching {@code AGPlayer} object, or {@code null} if no match is found.
	 */
	public AGPlayer getAGPlayer(Integer object)
	{
		for (AGPlayer agp : players)
		{
			if (agp.getObjectId().equals(object))
			{
				return agp;
			}
		}
		
		return null;
	}
}
