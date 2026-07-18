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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.instanceposition.ChaosInstancePosition;
import com.aionemu.gameserver.model.instance.instanceposition.DisciplineInstancePosition;
import com.aionemu.gameserver.model.instance.instanceposition.GenerealInstancePosition;
import com.aionemu.gameserver.model.instance.instanceposition.GloryInstancePosition;
import com.aionemu.gameserver.model.instance.instanceposition.HarmonyInstancePosition;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * Represents the rewards granted to players for participating in the {@code PvPArena}.<br>
 * This class handles the logic for distributing specific items or scores based on arena performance.
 * @author xTz
 */
public class PvPArenaReward extends InstanceReward<PvPArenaPlayerReward>
{
	private final Map<Integer, Boolean> positions = new HashMap<>();
	private final List<Integer> zones = new ArrayList<>();
	private int round = 1;
	private Integer zone;
	private final int bonusTime;
	private final int capPoints;
	private long instanceTime;
	private final byte buffId;
	protected WorldMapInstance instance;
	private GenerealInstancePosition instancePosition;
	
	/**
	 * Initializes a new {@code PvPArenaReward} object.<br>
	 * This constructor sets up the arena rules based on the map type.<br>
	 * It configures points, zones, and positions for solo or group play.
	 * @param mapId The unique identifier for the world map.
	 * @param instanceId The specific ID of the current instance.
	 * @param instance The {@link WorldMapInstance} where the reward occurs.
	 */
	public PvPArenaReward(Integer mapId, int instanceId, WorldMapInstance instance)
	{
		super(mapId, instanceId);
		this.instance = instance;
		final boolean isSolo = isSoloArena();
		capPoints = isSolo ? 14400 : 50000;
		bonusTime = isSolo ? 8100 : 12000;
		Collections.addAll(zones, isSolo ? new Integer[]
		{
			1,
			2,
			3,
			4
		} : new Integer[]
		{
			1,
			2,
			3,
			4,
			5,
			6
		});
		int positionSize;
		if (isSolo)
		{
			positionSize = 4;
			buffId = 8;
			instancePosition = new DisciplineInstancePosition();
		}
		else if (isGlory())
		{
			buffId = 7;
			positionSize = 8;
			instancePosition = new GloryInstancePosition();
		}
		else if ((mapId == 300450000) || (mapId == 300570000) || (mapId == 301100000))
		{
			buffId = 7;
			positionSize = 12;
			instancePosition = new HarmonyInstancePosition();
		}
		else
		{
			buffId = 7;
			positionSize = 12;
			instancePosition = new ChaosInstancePosition();
		}
		
		instancePosition.initsialize(mapId, instanceId);
		for (int i = 1; i <= positionSize; i++)
		{
			positions.put(i, Boolean.FALSE);
		}
		
		setRndZone();
	}
	
	/**
	 * Checks if the current arena is a solo type.<br>
	 * This method compares the {@code mapId} against specific IDs.
	 * @return {@code true} if the map is a solo arena, otherwise {@code false}.
	 */
	public boolean isSoloArena()
	{
		return (mapId == 300360000) || (mapId == 300430000);
	}
	
	/**
	 * Checks if the current reward instance is a Glory type.<br>
	 * This method compares the {@code mapId} against the specific value {@code 300550000}.
	 * @return {@code true} if the map ID matches the Glory map, otherwise {@code false}.
	 */
	public boolean isGlory()
	{
		return mapId == 300550000;
	}
	
	/**
	 * Retrieves the maximum point limit for the instance.<br>
	 * This value is used to determine if a score has reached its cap.
	 * @return The total number of points allowed as a cap.
	 */
	public int getCapPoints()
	{
		return capPoints;
	}
	
	/**
	 * Selects a random zone from the available list.<br>
	 * This method uses {@link com.aionemu.commons.utils.Rnd} to pick an index.<br>
	 * The selected zone is removed from the internal collection after being assigned.
	 */
	public void setRndZone()
	{
		final int index = Rnd.get(zones.size());
		zone = zones.get(index);
		zones.remove(index);
	}
	
	/**
	 * Identifies all available spots in the arena.<br>
	 * It checks the {@code positions} map for entries marked as false.
	 * @return A {@code List<Integer>} containing the IDs of free positions.
	 */
	private List<Integer> getFreePositions()
	{
		final List<Integer> p = new ArrayList<>();
		for (Integer key : positions.keySet())
		{
			if (!positions.get(key))
			{
				p.add(key);
			}
		}
		
		return p;
	}
	
	/**
	 * Assigns a random position to a specific player reward.<br>
	 * This method clears the old position if it exists.<br>
	 * It then selects a new position from the available free positions.
	 * @param object The unique identifier for the {@link PvPArenaPlayerReward}.
	 */
	public synchronized void setRndPosition(Integer object)
	{
		final PvPArenaPlayerReward reward = getPlayerReward(object);
		final int position = reward.getPosition();
		if (position != 0)
		{
			clearPosition(position, Boolean.FALSE);
		}
		
		final Integer key = getFreePositions().get(Rnd.get(getFreePositions().size()));
		clearPosition(key, Boolean.TRUE);
		reward.setPosition(key);
	}
	
	/**
	 * Updates the status of a specific position in the reward map.<br>
	 * This method stores the {@code result} for the given {@code position}.<br>
	 * It uses synchronization to ensure thread safety during the update.
	 * @param position The unique identifier for the position to update.
	 * @param result The status value to associate with the specified position.
	 */
	public synchronized void clearPosition(int position, Boolean result)
	{
		positions.put(position, result);
	}
	
	/**
	 * Retrieves the current round number.<br>
	 * This value is used to track the progress of the arena match.
	 * @return The current {@code int} value of the round.
	 */
	public int getRound()
	{
		return round;
	}
	
	/**
	 * Sets the current round number for the reward.<br>
	 * This updates the {@code round} field of this instance.
	 * @param round The new round number to set.
	 */
	public void setRound(int round)
	{
		this.round = round;
	}
	
	/**
	 * Registers a reward for a player in the arena.<br>
	 * This method checks if the {@code object} is already contained.<br>
	 * If not, it adds a new {@link PvPArenaPlayerReward} to the list.
	 * @param object The unique identifier of the player.
	 */
	public void regPlayerReward(Integer object)
	{
		if (!containPlayer(object))
		{
			addPlayerReward(new PvPArenaPlayerReward(object, bonusTime, buffId));
		}
	}
	
	/**
	 * Adds a new reward to the list of rewards for this instance.<br>
	 * This method calls the {@code addPlayerReward} method.
	 * @param reward The {@code PvPArenaPlayerReward} object to be added.
	 */
	@Override
	public void addPlayerReward(PvPArenaPlayerReward reward)
	{
		super.addPlayerReward(reward);
	}
	
	/**
	 * Retrieves the reward for a specific player.<br>
	 * This method calls the {@code getPlayerReward} method.
	 * @param object The unique identifier of the player.
	 * @return The {@code PvPArenaPlayerReward} associated with the given object.
	 */
	@Override
	public PvPArenaPlayerReward getPlayerReward(Integer object)
	{
		return (PvPArenaPlayerReward) super.getPlayerReward(object);
	}
	
	/**
	 * Teleports the {@link Player} to a random position and applies rewards.<br>
	 * This method registers the player reward, sets a random position, and applies morale effects.<br>
	 * It then uses {@code instancePosition.port} to move the character to the final location.
	 * @param player The {@code Player} object to be teleported.
	 */
	public void portToPosition(Player player)
	{
		final Integer object = player.getObjectId();
		regPlayerReward(object);
		setRndPosition(object);
		final PvPArenaPlayerReward playerReward = getPlayerReward(object);
		playerReward.applyBoostMoraleEffect(player);
		instancePosition.port(player, zone, playerReward.getPosition());
	}
	
	/**
	 * Sorts the player rewards based on their score points.<br>
	 * The list is ordered from highest to lowest score.
	 * @return A {@code List} of {@link PvPArenaPlayerReward} objects sorted by descending points.
	 */
	public List<PvPArenaPlayerReward> sortPoints()
	{
		return getInstanceRewards().stream().sorted(Comparator.comparingInt(PvPArenaPlayerReward::getScorePoints).reversed()).collect(Collectors.toList());
	}
	
	/**
	 * Checks if a player is eligible to receive an opportunity token.<br>
	 * This depends on the arena type and the player's final rank.
	 * @param rewardedPlayer The {@link PvPArenaPlayerReward} object of the player to check.
	 * @return {@code true} if the player qualifies for the token, otherwise {@code false}.
	 */
	public boolean canRewardOpportunityToken(PvPArenaPlayerReward rewardedPlayer)
	{
		if (rewardedPlayer != null)
		{
			final int rank = getRank(rewardedPlayer.getScorePoints());
			return (isSoloArena() && (rank == 1)) || (rank > 2);
		}
		
		return false;
	}
	
	/**
	 * Determines the ranking of a player based on their score.<br>
	 * This method compares the provided {@code points} against all rewards returned by {@code sortPoints}.
	 * @param points The score to check for ranking.
	 * @return The calculated rank as an {@code int}.
	 */
	public int getRank(int points)
	{
		int rank = -1;
		for (PvPArenaPlayerReward reward : sortPoints())
		{
			if (reward.getScorePoints() >= points)
			{
				rank++;
			}
		}
		
		return rank;
	}
	
	/**
	 * Checks if the point threshold for capping has been met.<br>
	 * It evaluates the scores of all rewards in the instance.<br>
	 * For solo arenas, it also checks if the score difference is at least {@code 1500}.
	 * @return {@code true} if the conditions are met, otherwise {@code false}.
	 */
	public boolean hasCapPoints()
	{
		if (isSoloArena() && ((getInstanceRewards().stream().mapToInt(PvPArenaPlayerReward::getPoints).max().orElse(0) - getInstanceRewards().stream().mapToInt(PvPArenaPlayerReward::getPoints).min().orElse(0)) >= 1500))
		{
			return true;
		}
		
		return getInstanceRewards().stream().mapToInt(PvPArenaPlayerReward::getPoints).max().orElse(0) >= capPoints;
	}
	
	/**
	 * Calculates the total score points for all rewards in this instance.<br>
	 * This method sums up the values returned by {@code getScorePoints}.
	 * @return The sum of all player reward scores as an {@code int}.
	 */
	public int getTotalPoints()
	{
		return getInstanceRewards().stream().mapToInt(PvPArenaPlayerReward::getScorePoints).sum();
	}
	
	/**
	 * Checks if the current map is eligible for rewards.<br>
	 * This method verifies if the {@code mapId} matches specific allowed values.
	 * @return {@code true} if the map allows rewards, {@code false} otherwise.
	 */
	public boolean canRewarded()
	{
		return (mapId == 300350000) || (mapId == 300360000) || (mapId == 300420000) || (mapId == 300430000) || (mapId == 300450000) || (mapId == 300550000) || (mapId == 300570000) || (mapId == 301100000);
	}
	
	/**
	 * Retrieves the specific bonus skill value for a given NPC.<br>
	 * This method maps an {@code npcId} to its corresponding skill code.<br>
	 * It returns {@code 0} if the ID is not recognized.
	 * @param npcId The unique identifier of the NPC.
	 * @return The integer code for the bonus skill.
	 */
	public int getNpcBonusSkill(int npcId)
	{
		switch (npcId)
		{
			case 701175: // Plaza Flame Thrower
			case 701176:
			case 701177:
			case 701178:
				return 0x4E5732; // 20055 50
			case 701189: // Plaza Flame Thrower
			case 701190:
			case 701191:
			case 701192:
				return 0x4FDB3C; // 20443 60
			case 701317: // Illusion of Hope level 47
				return 0x4f8532; // 20357 50
			case 701318: // Illusion of Hope level 51
				return 0x4f8537; // 20357 55
			case 701319: // Illusion of Hope level 56
				return 0x4f853C; // 20357 60
			case 701220: // Blesed Relic
				return 0x4E5537; // 20053 55 //20068, 20072
			case 207118:
			case 207119:
				return 0x50C101; // 20673 1
			case 207100:
				return 0x50BE01; // 20670 1
			default:
				return 0;
		}
	}
	
	/**
	 * Calculates the remaining time for the current instance round.<br>
	 * This method returns {@code 0} if the reward has already been granted.<br>
	 * The result is calculated based on the elapsed time since {@code instanceTime}.
	 * @return the remaining time as an {@code int}
	 */
	public int getTime()
	{
		final long result = System.currentTimeMillis() - instanceTime;
		if (isRewarded())
		{
			return 0;
		}
		
		if (result < 120000)
		{
			return (int) (120000 - result);
		}
		
		return (int) ((180000 * getRound()) - (result - 120000));
	}
	
	/**
	 * Records the current system time as the start time for this instance.<br>
	 * This method updates the {@code instanceTime} field using {@code System.currentTimeMillis()}.
	 */
	public void setInstanceStartTime()
	{
		instanceTime = System.currentTimeMillis();
	}
	
	/**
	 * Sends the current instance score to all players.<br>
	 * This method uses {@code sendPacket} to deliver a new {@code SM_INSTANCE_SCORE} packet.<br>
	 * It retrieves the list of players from the current {@code WorldMapInstance}.
	 */
	public void sendPacket()
	{
		final List<Player> players = instance.getPlayersInside();
		instance.doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(getTime(), getInstanceReward(), players)));
	}
	
	/**
	 * Retrieves the unique identifier for the reward buff.
	 * @return The {@code byte} ID of the buff.
	 */
	public byte getBuffId()
	{
		return buffId;
	}
	
	/**
	 * Clears all reward data and internal positions.<br>
	 * This method calls {@code clear} to reset the base rewards.<br>
	 * It also clears the {@code positions} map to remove any stored player locations.
	 */
	@Override
	public void clear()
	{
		super.clear();
		positions.clear();
	}
}
