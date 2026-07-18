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

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.geometry.Point3D;
import com.aionemu.gameserver.model.instance.playerreward.BalaurMarchingRoutePlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * This class handles the rewards granted to players for completing the {@code BalaurMarchingRoute} instance.<br>
 * It manages the distribution of specific items and effects defined in {@link BalaurMarchingRoutePlayerReward}.
 * @author Falke_34
 */
public class BalaurMarchingRouteReward extends InstanceReward<BalaurMarchingRoutePlayerReward>
{
	
	/**
	 * Calculates Total Asmo & Elyos Point<br>
	 * <br>
	 * Default Start Points: 3800
	 */
	private final AtomicInteger asmodiansPoints = new AtomicInteger(3800);
	private final AtomicInteger elyosPoins = new AtomicInteger(3800);
	
	/**
	 * Calculates Total Asmo & Elyos PvP Kills<br>
	 * <br>
	 * Default PvP Kills: 0
	 */
	private final AtomicInteger asmodiansPvpKills = new AtomicInteger(0);
	private final AtomicInteger elyosPvpKills = new AtomicInteger(0);
	
	// Determines the Winner or Looser Race
	private Race race;
	
	protected WorldMapInstance instance;
	
	private final int winnerPoints;
	private final int looserPoints;
	private final int capPoints;
	private long instanceTime;
	private final int bonusTime;
	private final byte buffId;
	
	/**
	 * Creates a new reward object for the Balaur Marching Route.<br>
	 * This constructor initializes default points and time values.<br>
	 * It sets up the instance data required to track rewards.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the specific instance.
	 * @param instance The {@link WorldMapInstance} where the event takes place.
	 */
	public BalaurMarchingRouteReward(Integer mapId, int instanceId, WorldMapInstance instance)
	{
		super(mapId, instanceId);
		this.instance = instance;
		winnerPoints = 3000;
		looserPoints = 2500;
		capPoints = 30000;
		bonusTime = 12000;
		buffId = 11;
	}
	
	/**
	 * Updates the PvP kill count for a specific race.<br>
	 * This method adds the provided {@code points} to the current total for the given {@link Race}.<br>
	 * It ensures that the resulting value never drops below {@code 0}.
	 * @param race The {@link Race} type to update.
	 * @param points The number of kills to add to the score.
	 */
	public void addPvpKillsByRace(Race race, int points)
	{
		final AtomicInteger racePoints = getPvpKillsByRace(race);
		racePoints.addAndGet(points);
		if (racePoints.intValue() < 0)
		{
			racePoints.set(0);
		}
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
	 * Checks if any player has reached the required point threshold.<br>
	 * It compares the highest score among all rewards to {@code capPoints}.
	 * @return {@code true} if at least one reward meets or exceeds {@code capPoints}, otherwise {@code false}.
	 */
	public boolean hasCapPoints()
	{
		return getInstanceRewards().stream().mapToInt(BalaurMarchingRoutePlayerReward::getPoints).max().orElse(0) >= capPoints;
	}
	
	/**
	 * Retrieves the reward for a specific player.<br>
	 * This method casts the result to {@code BalaurMarchingRoutePlayerReward}.
	 * @param object The unique identifier of the player.
	 * @return The {@code BalaurMarchingRoutePlayerReward} associated with the given object.
	 */
	@Override
	public BalaurMarchingRoutePlayerReward getPlayerReward(Integer object)
	{
		return (BalaurMarchingRoutePlayerReward) super.getPlayerReward(object);
	}
	
	/**
	 * Retrieves the total points for a specific race.<br>
	 * This method checks if the provided {@code race} is {@code ELYOS} or {@code ASMODIANS}.<br>
	 * It returns the corresponding {@code AtomicInteger} value based on the current instance state.
	 * @param race The {@code Race} type to check for points.
	 * @return A {@code AtomicInteger} containing the points, or {@code null} if the race is invalid.
	 */
	public AtomicInteger getPointsByRace(Race race)
	{
		return (race == Race.ELYOS) ? elyosPoins : (race == Race.ASMODIANS) ? asmodiansPoints : null;
	}
	
	/**
	 * Retrieves the total points earned by the winning side.<br>
	 * This value is used to determine the final score of the instance.
	 * @return The total points for the winner as an {@code int}.
	 */
	public int getWinnerPoints()
	{
		return winnerPoints;
	}
	
	/**
	 * Teleports the player to a random starting position based on their race.<br>
	 * It uses {@code teleportTo} to move the character.
	 * @param player The {@code Player} object to be teleported.
	 */
	public void portToPosition(Player player)
	{
		final float Rx = Rnd.get(-5, 5);
		final float Ry = Rnd.get(-5, 5);
		final Point3D AsmoStartPoint = new Point3D(758.2061f + Rx, 560.8301f + Ry, 576.874f);
		final Point3D ElyosStartPoint = new Point3D(322.03433f + Rx, 490.0247f + Ry, 596.1155f);
		
		if (player.getRace() == Race.ASMODIANS)
		{
			TeleportService2.teleportTo(player, mapId, instanceId, AsmoStartPoint.getX(), AsmoStartPoint.getY(), AsmoStartPoint.getZ(), (byte) 45);
		}
		else
		{
			TeleportService2.teleportTo(player, mapId, instanceId, ElyosStartPoint.getX(), ElyosStartPoint.getY(), ElyosStartPoint.getZ(), (byte) 105);
		}
	}
	
	/**
	 * Sorts the player rewards based on their scores.<br>
	 * The list is ordered from highest score to lowest score.
	 * @return A {@code List} of {@link BalaurMarchingRoutePlayerReward} objects sorted by points in descending order.
	 */
	public List<BalaurMarchingRoutePlayerReward> sortPoints()
	{
		return getInstanceRewards().stream().sorted(Comparator.comparingInt(BalaurMarchingRoutePlayerReward::getScorePoints).reversed()).collect(Collectors.toList());
	}
	
	/**
	 * Calculates the remaining time for the instance.<br>
	 * The result is based on the difference between the current system time and {@code instanceTime}.<br>
	 * It returns 0 if the time has expired.
	 * @return the remaining time as an {@code int}
	 */
	public int getTime()
	{
		final long result = System.currentTimeMillis() - instanceTime;
		if (result < 45000)
		{
			return (int) (45000 - result);
		}
		
		if (result < 1800000)
		{
			// 30 Minutes
			return (int) (1800000 - (result - 20000));
		}
		
		return 0;
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
	 * Retrieves the winning race for this instance.<br>
	 * This method returns the {@code Race} object stored in the private field.
	 * @return The {@code Race} that won the event.
	 */
	public Race getWinningRace()
	{
		return race;
	}
	
	/**
	 * Sets the winning {@link Race} for this reward instance.<br>
	 * This updates the internal {@code race} field.
	 * @param race The {@code Race} that is determined to be the winner.
	 */
	public void setWinningRace(Race race)
	{
		this.race = race;
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
	 * Registers a reward for a specific {@link Player}.<br>
	 * It checks if the player is already in the instance.<br>
	 * If not, it adds a new {@code BalaurMarchingRoutePlayerReward} to the list.
	 * @param player The {@code Player} object to receive the reward.
	 */
	public void regPlayerReward(Player player)
	{
		if (!containPlayer(player.getObjectId()))
		{
			addPlayerReward(new BalaurMarchingRoutePlayerReward(player.getObjectId(), bonusTime, player.getRace()));
		}
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
	 * Retrieves the total PvP kills for a specific race.<br>
	 * This method checks if the provided {@code race} is {@code ELYOS} or {@code ASMODIANS}.<br>
	 * It returns the corresponding {@code AtomicInteger} value based on the result.
	 * @param race The {@link Race} to check for PvP kills.
	 * @return A {@code AtomicInteger} containing the kill count, or {@code null} if the race is invalid.
	 */
	public AtomicInteger getPvpKillsByRace(Race race)
	{
		return (race == Race.ELYOS) ? elyosPvpKills : (race == Race.ASMODIANS) ? asmodiansPvpKills : null;
	}
	
	/**
	 * Determines which race won based on the current scores.<br>
	 * It compares {@code asmodiansPoints} and {@code elyosPoins}.<br>
	 * Returns {@code ASMODIANS} if they have more points, otherwise returns {@code ELYOS}.
	 * @return The winning {@code Race} type.
	 */
	public Race getWinningRaceByScore()
	{
		return Integer.compare(asmodiansPoints.intValue(), elyosPoins.intValue()) > 0 ? Race.ASMODIANS : Race.ELYOS;
	}
	
	/**
	 * Adds a specific player reward to the collection.<br>
	 * This method calls the {@code addPlayerReward} method.
	 * @param reward The {@code BalaurMarchingRoutePlayerReward} object to add.
	 */
	@Override
	public void addPlayerReward(BalaurMarchingRoutePlayerReward reward)
	{
		super.addPlayerReward(reward);
	}
	
	/**
	 * Retrieves the total points for the losing side.<br>
	 * This value is calculated during the instance reward process.
	 * @return The number of points assigned to the loser.
	 */
	public int getLooserPoints()
	{
		return looserPoints;
	}
	
	/**
	 * Adds a specific amount of points to a race.<br>
	 * This method updates the score for the provided {@code Race}.<br>
	 * The total points will not drop below {@code 0}.
	 * @param race The {@link Race} type to receive the points.
	 * @param points The number of points to add to the score.
	 */
	public void addPointsByRace(Race race, int points)
	{
		final AtomicInteger racePoints = getPointsByRace(race);
		racePoints.addAndGet(points);
		if (racePoints.intValue() < 0)
		{
			racePoints.set(0);
		}
	}
	
	/**
	 * Resets all reward data to their default values.<br>
	 * This clears the points and kills for both races.<br>
	 * It also removes any registered player rewards.
	 */
	@Override
	public void clear()
	{
		super.clear();
	}
}
