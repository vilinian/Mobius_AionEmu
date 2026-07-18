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

import java.util.function.Predicate;

/**
 * This class provides statistics and data related to a group of {@link Player} objects.<br>
 * It implements the {@code Predicate} interface to filter players based on specific group criteria.
 * @author ATracer
 */
public class PlayerGroupStats implements Predicate<Player>
{
	private final PlayerGroup group;
	private int minExpPlayerLevel;
	private int maxExpPlayerLevel;
	Player minLevelPlayer;
	Player maxLevelPlayer;
	
	/**
	 * Creates a new instance of {@code PlayerGroupStats}.<br>
	 * This constructor initializes the stats for a specific {@link PlayerGroup}.
	 * @param group The {@code PlayerGroup} to associate with these statistics.
	 */
	PlayerGroupStats(PlayerGroup group)
	{
		this.group = group;
	}
	
	/**
	 * This method is called when a new member joins the group.<br>
	 * It updates the group statistics and recalculates the experience levels.
	 * @param member The {@code PlayerGroupMember} being added to the group.
	 */
	public void onAddPlayer(PlayerGroupMember member)
	{
		group.applyOnMembers(this);
		calculateExpLevels();
	}
	
	/**
	 * This method is called when a member leaves the group.<br>
	 * It triggers an update to the group statistics.<br>
	 * The {@link PlayerGroupStats} instance will re-evaluate the members.
	 * @param member The {@code PlayerGroupMember} that was removed.
	 */
	public void onRemovePlayer(PlayerGroupMember member)
	{
		group.applyOnMembers(this);
	}
	
	/**
	 * Updates the minimum and maximum experience levels for the group.<br>
	 * This method retrieves levels from {@code minLevelPlayer} and {@code maxLevelPlayer}.<br>
	 * It then sets both player references to {@code null}.
	 */
	private void calculateExpLevels()
	{
		minExpPlayerLevel = minLevelPlayer.getLevel();
		maxExpPlayerLevel = maxLevelPlayer.getLevel();
		minLevelPlayer = null;
		maxLevelPlayer = null;
	}
	
	/**
	 * Updates the minimum and maximum level players within the group.<br>
	 * This method compares the {@code Player} experience against current bounds.<br>
	 * It updates the internal references if a new extreme is found.
	 * @param player The {@link Player} to evaluate for group statistics.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player player)
	{
		if ((minLevelPlayer == null) || (maxLevelPlayer == null))
		{
			minLevelPlayer = player;
			maxLevelPlayer = player;
		}
		else
		{
			if (player.getCommonData().getExp() < minLevelPlayer.getCommonData().getExp())
			{
				minLevelPlayer = player;
			}
			
			if (!player.isMentor() && (player.getCommonData().getExp() > maxLevelPlayer.getCommonData().getExp()))
			{
				maxLevelPlayer = player;
			}
		}
		
		return true;
	}
	
	/**
	 * Retrieves the minimum experience level required for a player.<br>
	 * This value is used to filter eligible members.
	 * @return the minimum experience level as an {@code int}.
	 */
	public int getMinExpPlayerLevel()
	{
		return minExpPlayerLevel;
	}
	
	/**
	 * Retrieves the maximum experience level allowed for a player in this alliance.<br>
	 * This value is used to check if a member meets the required level requirements.
	 * @return The maximum experience level as an {@code int}.
	 */
	public int getMaxExpPlayerLevel()
	{
		return maxExpPlayerLevel;
	}
}
