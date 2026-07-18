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
import com.aionemu.gameserver.model.instance.playerreward.RunatoriumPlayerReward;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * Represents the rewards granted to players upon completing a {@link WorldMapInstance} in the Runatorium.<br>
 * This class handles the logic for distributing specific items or effects defined in {@link RunatoriumPlayerReward}.
 * @author GiGatR00n v4.7.5.x
 */
public class RunatoriumReward extends InstanceReward<RunatoriumPlayerReward>
{
	/**
	 * Calculates Total Asmo & Elyos Point<br>
	 * <br>
	 * Default Start Points: 1000
	 */
	private final AtomicInteger asmodiansPoints = new AtomicInteger(1000);
	private final AtomicInteger elyosPoins = new AtomicInteger(1000);
	
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
	
	private long instanceStartTime;
	private final long instanceEndTime;
	private final long PreparingTime;
	private final int bonusTime;
	
	/**
	 * Creates a new {@code RunatoriumReward} object.<br>
	 * This constructor initializes the reward with specific map and instance data.<br>
	 * It also sets the default preparation time and total duration for the event.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the instance.
	 * @param instance The {@link WorldMapInstance} associated with this reward.
	 */
	public RunatoriumReward(Integer mapId, int instanceId, WorldMapInstance instance)
	{
		super(mapId, instanceId);
		this.instance = instance;
		PreparingTime = 97504; // v4.7.5.13 NA Retail
		instanceEndTime = 20 * 60 * 1000; // 20-minutes
		bonusTime = 12000;
	}
	
	/**
	 * Calculates the bonus reward for an Abyss instance.<br>
	 * The result depends on whether the player won and if a boss was killed.
	 * @param isWin Indicates if the player won the match.
	 * @param isBossKilled Indicates if the boss was defeated during the match.
	 * @return The total reward points based on the outcome.
	 */
	public int CalcBonusAbyssReward(boolean isWin, boolean isBossKilled)
	{
		final int BossKilled = 1993;
		final int Win = 3163;
		final int Loss = 1031;
		
		if (isBossKilled)
		{
			return isWin ? (Win + BossKilled) : (Loss + BossKilled);
		}
		
		return isWin ? Win : Loss;
	}
	
	/**
	 * Calculates the glory reward points based on victory and boss status.<br>
	 * The result depends on whether the player won or lost.<br>
	 * It also adds a bonus if a boss was killed.
	 * @param isWin Indicates if the player won the match.
	 * @param isBossKilled Indicates if the boss was defeated during the match.
	 * @return The total calculated glory reward points.
	 */
	public int CalcBonusGloryReward(boolean isWin, boolean isBossKilled)
	{
		final int BossKilled = 50;
		final int Win = 150;
		final int Loss = 75;
		
		if (isBossKilled)
		{
			return isWin ? (Win + BossKilled) : (Loss + BossKilled);
		}
		
		return isWin ? Win : Loss;
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
	
	/**
	 * Registers a reward for a specific {@link Player}.<br>
	 * It checks if the player is already in the instance.<br>
	 * If not, it adds a new {@code RunatoriumPlayerReward} to the list.
	 * @param player The {@code Player} object to receive the reward.
	 */
	public void regPlayerReward(Player player)
	{
		if (!containPlayer(player.getObjectId()))
		{
			addPlayerReward(new RunatoriumPlayerReward(player.getObjectId(), bonusTime, player.getRace()));
		}
	}
	
	/**
	 * Adds a specific reward to the list of player rewards.<br>
	 * This method calls the {@code addPlayerReward} method.
	 * @param reward The {@code RunatoriumPlayerReward} object to be added.
	 */
	@Override
	public void addPlayerReward(RunatoriumPlayerReward reward)
	{
		super.addPlayerReward(reward);
	}
	
	/**
	 * Retrieves the specific reward for a player based on an object identifier.<br>
	 * This method calls the parent {@code getPlayerReward} method.<br>
	 * It casts the result to a {@code RunatoriumPlayerReward} type.
	 * @param object The unique identifier of the object or player.
	 * @return The {@code RunatoriumPlayerReward} associated with the provided object.
	 */
	@Override
	public RunatoriumPlayerReward getPlayerReward(Integer object)
	{
		return (RunatoriumPlayerReward) super.getPlayerReward(object);
	}
	
	/**
	 * Sorts the player rewards based on their scores.<br>
	 * The list is ordered from highest score to lowest score.
	 * @return A {@code List} of {@link RunatoriumPlayerReward} objects sorted by points in descending order.
	 */
	public List<RunatoriumPlayerReward> sortPoints()
	{
		return getInstanceRewards().stream().sorted(Comparator.comparingInt(RunatoriumPlayerReward::getScorePoints).reversed()).collect(Collectors.toList());
	}
	
	/**
	 * Teleports the player to a random starting position based on their race.<br>
	 * It uses {@code teleportTo} to move the character.
	 * @param player The {@code Player} object to be teleported.
	 */
	public void portToPosition(Player player)
	{
		/*
		 * Get Random Position (Elyos - Asmo)
		 */
		final float Rx = Rnd.get(-5, 5);
		final float Ry = Rnd.get(-5, 5);
		final Point3D ElyosStartPoint = new Point3D(270.1437f + Rx, 348.6699f + Ry, 79.44365f); // Elyos Center
		final Point3D AsmoStartPoint = new Point3D(258.5553f + Rx, 169.85149f + Ry, 79.430855f); // Asmo Center
		
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
	 * Sets the winning {@link Race} for this reward instance.<br>
	 * This updates the internal {@code race} field.
	 * @param race The {@code Race} that is determined to be the winner.
	 */
	public void setWinningRace(Race race)
	{
		this.race = race;
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
	 * Records the current system time as the start time for this instance.<br>
	 * This method updates the {@code instanceStartTime} field using {@code System.currentTimeMillis()}.
	 */
	public void setInstanceStartTime()
	{
		instanceStartTime = System.currentTimeMillis();
	}
	
	/**
	 * Retrieves the preparation time for the instance.<br>
	 * This value is stored in the {@code PreparingTime} field.
	 * @return The total preparation time as a {@code long}.
	 */
	public long getPreparingTime()
	{
		return PreparingTime;
	}
	
	/**
	 * Retrieves the total end time for the instance.<br>
	 * This value is calculated by adding {@code PreparingTime} to {@code instanceEndTime}.
	 * @return The sum of the preparation time and the instance end time as a {@code long}.
	 */
	public long getEndTime()
	{
		return (PreparingTime + instanceEndTime);
	}
	
	/**
	 * Calculates the time remaining until the instance ends.<br>
	 * It accounts for both the preparation period and the active duration.<br>
	 * Returns {@code 0} if the instance has already finished.
	 * @return The remaining time in seconds as an {@code int}.
	 */
	public int getRemainingTime()
	{
		final long result = System.currentTimeMillis() - instanceStartTime;
		if (result < PreparingTime)
		{
			return (int) (PreparingTime - result); // ...Count Down Before Starting Instance. 60,59,58,57,...,0
		}
		else if (result < getEndTime())
		{// ..........20 Minutes + PreparingTime(1.30 Min)
			return (int) (instanceEndTime - (result - PreparingTime));
		}
		
		return 0;
	}
}
