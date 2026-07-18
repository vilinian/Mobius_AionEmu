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
package com.aionemu.gameserver.model.instance.instancereward;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.aionemu.gameserver.model.autogroup.AGPlayer;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.playerreward.HarmonyGroupReward;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Represents the rewards granted to players after completing a Harmony Arena instance.<br>
 * This class handles the logic for distributing specific loot and scores based on arena performance. It extends {@link PvPArenaReward} to provide specialized behavior for this game mode.
 * @author xTz
 */
public class HarmonyArenaReward extends PvPArenaReward
{
	private final List<HarmonyGroupReward> groups = new ArrayList<>();
	
	/**
	 * Creates a new {@link HarmonyArenaReward} object.<br>
	 * This constructor initializes the reward with specific map and instance data.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the instance.
	 * @param instance The {@code WorldMapInstance} where the reward is located.
	 */
	public HarmonyArenaReward(Integer mapId, int instanceId, WorldMapInstance instance)
	{
		super(mapId, instanceId, instance);
	}
	
	/**
	 * Retrieves the {@link HarmonyGroupReward} for a specific player.<br>
	 * It searches through all groups to find one containing the provided {@code object}.
	 * @param object The unique identifier of the player to check.
	 * @return The matching {@code HarmonyGroupReward} or {@code null} if no group is found.
	 */
	public HarmonyGroupReward getHarmonyGroupReward(Integer object)
	{
		for (InstancePlayerReward reward : groups)
		{
			final HarmonyGroupReward harmonyReward = (HarmonyGroupReward) reward;
			if (harmonyReward.containPlayer(object))
			{
				return harmonyReward;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the list of {@link HarmonyGroupReward} objects that are currently active inside an instance.<br>
	 * It checks if at least one player from a group is present in the instance.
	 * @return A {@code List} containing the valid {@code HarmonyGroupReward} objects.
	 */
	public List<HarmonyGroupReward> getHarmonyGroupInside()
	{
		final List<HarmonyGroupReward> harmonyGroups = new ArrayList<>();
		for (HarmonyGroupReward group : groups)
		{
			for (AGPlayer agp : group.getAGPlayers())
			{
				if (agp.isInInstance())
				{
					harmonyGroups.add(group);
					break;
				}
			}
		}
		
		return harmonyGroups;
	}
	
	/**
	 * Retrieves all {@link Player} objects located within a specific group.<br>
	 * It checks every player in the instance to see if they belong to the provided {@code group}.
	 * @param group The {@code HarmonyGroupReward} used to filter the players.
	 * @return A {@code List} containing the matching {@link Player} objects.
	 */
	public List<Player> getPlayersInside(HarmonyGroupReward group)
	{
		final List<Player> players = new ArrayList<>();
		for (Player playerInside : instance.getPlayersInside())
		{
			if (group.containPlayer(playerInside.getObjectId()))
			{
				players.add(playerInside);
			}
		}
		
		return players;
	}
	
	/**
	 * Adds a new {@link HarmonyGroupReward} to the internal list.<br>
	 * This method updates the collection of rewards for harmony groups.
	 * @param reward The {@code HarmonyGroupReward} object to add.
	 */
	public void addHarmonyGroup(HarmonyGroupReward reward)
	{
		groups.add(reward);
	}
	
	/**
	 * Retrieves the list of all harmony group rewards.<br>
	 * This method returns the internal {@code groups} collection.
	 * @return a {@link List} containing all {@link HarmonyGroupReward} objects.
	 */
	public List<HarmonyGroupReward> getGroups()
	{
		return groups;
	}
	
	/**
	 * Sends a score packet to all players in the instance.<br>
	 * This method uses {@code sendPacket} to deliver information.
	 * @param type The packet type identifier.
	 * @param object The specific object associated with the reward data.
	 */
	public void sendPacket(int type, Integer object)
	{
		instance.doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(type, getTime(), getInstanceReward(), object));
			}
		});
	}
	
	/**
	 * Calculates the ranking of a group based on its score.<br>
	 * It compares the provided {@code points} against all sorted rewards.
	 * @param points The score to check for ranking.
	 * @return The calculated rank as an {@code int}.
	 */
	@Override
	public int getRank(int points)
	{
		int rank = -1;
		for (HarmonyGroupReward reward : sortGroupPoints())
		{
			if (reward.getPoints() >= points)
			{
				rank++;
			}
		}
		
		return rank;
	}
	
	/**
	 * Sorts the harmony groups based on their points.<br>
	 * The list is ordered from highest to lowest score.
	 * @return A {@code List} of {@link HarmonyGroupReward} objects sorted by points.
	 */
	public List<HarmonyGroupReward> sortGroupPoints()
	{
		return groups.stream().sorted(Comparator.comparingInt(HarmonyGroupReward::getPoints).reversed()).collect(Collectors.toList());
	}
	
	/**
	 * Calculates the sum of all points from every group.<br>
	 * This method iterates through the {@code groups} list.<br>
	 * It uses the {@code getPoints} method to get each value.
	 * @return The total sum of points as an {@code int}.
	 */
	@Override
	public int getTotalPoints()
	{
		return groups.stream().mapToInt(HarmonyGroupReward::getPoints).sum();
	}
	
	/**
	 * Removes all harmony group rewards from this instance.<br>
	 * This method also calls {@code clear} from the parent class.<br>
	 * After calling this, the internal list will be empty.
	 */
	@Override
	public void clear()
	{
		groups.clear();
		super.clear();
	}
}
