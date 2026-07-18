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
import com.aionemu.gameserver.model.instance.playerreward.NeviwindCanyonPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * This class handles the reward logic for the {@code NeviwindCanyon} instance.<br>
 * It manages how players receive items or other benefits upon completing this specific content.
 * @author Falke_34
 */
public class NeviwindCanyonReward extends InstanceReward<NeviwindCanyonPlayerReward>
{
	private final AtomicInteger asmodiansPoints = new AtomicInteger(3800);
	private final AtomicInteger elyosPoins = new AtomicInteger(3800);
	
	private final AtomicInteger asmodiansPvpKills = new AtomicInteger(0);
	private final AtomicInteger elyosPvpKills = new AtomicInteger(0);
	
	private final int capPoints;
	private Race race;
	protected WorldMapInstance instance;
	private long instanceTime;
	private final int bonusTime;
	private final byte buffId;
	
	/**
	 * Creates a new {@link NeviwindCanyonReward} object.<br>
	 * This constructor initializes the reward with specific map and instance data.<br>
	 * It also sets default values for points, time limits, and buff IDs.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the instance.
	 * @param instance The {@link WorldMapInstance} where the reward is active.
	 */
	public NeviwindCanyonReward(Integer mapId, int instanceId, WorldMapInstance instance)
	{
		super(mapId, instanceId);
		this.instance = instance;
		capPoints = 500000;
		bonusTime = 12000;
		buffId = 10;
	}
	
	/**
	 * Calculates the total reward points for an Abyss instance.<br>
	 * The result depends on whether the player won and if a boss was killed.
	 * @param isWin Indicates if the player won the match.
	 * @param isBossKilled Indicates if the boss was defeated during the match.
	 * @return The total points awarded based on the outcome.
	 */
	public int AbyssReward(boolean isWin, boolean isBossKilled)
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
	 * Calculates the glory points awarded based on the match outcome.<br>
	 * It checks if the player won and if a boss was defeated.
	 * @param isWin The result of the match.
	 * @param isBossKilled Whether the boss was killed during the instance.
	 * @return The total amount of glory points awarded.
	 */
	public int GloryReward(boolean isWin, boolean isBossKilled)
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
	 * Calculates the experience points awarded based on the match outcome.<br>
	 * It checks if the player won or lost and if a boss was defeated.
	 * @param isWin The result of the match.
	 * @param isBossKilled Whether the boss was killed during the instance.
	 * @return The total experience points to be awarded.
	 */
	public int ExpReward(boolean isWin, boolean isBossKilled)
	{
		final int BossKilled = 20000;
		final int Win = 10000;
		final int Loss = 5000;
		
		if (isBossKilled)
		{
			return isWin ? (Win + BossKilled) : (Loss + BossKilled);
		}
		
		return isWin ? Win : Loss;
	}
	
	/**
	 * Sorts the player rewards based on their scores.<br>
	 * The list is ordered from highest score to lowest score.
	 * @return A {@code List} of {@link NeviwindCanyonPlayerReward} objects sorted by points.
	 */
	public List<NeviwindCanyonPlayerReward> sortPoints()
	{
		return getInstanceRewards().stream().sorted(Comparator.comparingInt(NeviwindCanyonPlayerReward::getScorePoints).reversed()).collect(Collectors.toList());
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
		final Point3D asmodiansStartPosition = new Point3D(1094.0016f + Rx, 752.5455f + Ry, 336.30457f);
		final Point3D elyosStartPosition = new Point3D(402.67786f + Rx, 751.9347f + Ry, 336.30457f);
		
		if (player.getRace() == Race.ASMODIANS)
		{
			TeleportService2.teleportTo(player, mapId, instanceId, asmodiansStartPosition.getX(), asmodiansStartPosition.getY(), asmodiansStartPosition.getZ(), (byte) 45);
		}
		else
		{
			TeleportService2.teleportTo(player, mapId, instanceId, elyosStartPosition.getX(), elyosStartPosition.getY(), elyosStartPosition.getZ(), (byte) 45);
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
		return (race == Race.ELYOS) ? elyosPoins : ((race == Race.ASMODIANS) ? asmodiansPoints : null);
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
		final AtomicInteger pointsByRace = getPointsByRace(race);
		pointsByRace.addAndGet(points);
		if (pointsByRace.intValue() < 0)
		{
			pointsByRace.set(0);
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
		final AtomicInteger pvpKillsByRace = getPvpKillsByRace(race);
		pvpKillsByRace.addAndGet(points);
		if (pvpKillsByRace.intValue() < 0)
		{
			pvpKillsByRace.set(0);
		}
	}
	
	/**
	 * Sets the winning {@link Race} for this instance.<br>
	 * This updates the internal {@code race} field.
	 * @param race The {@code Race} that won the competition.
	 */
	public void setWinnerRace(Race race)
	{
		this.race = race;
	}
	
	/**
	 * Retrieves the winning race for this instance.<br>
	 * This method returns the {@code Race} object stored in the private field.
	 * @return The {@code Race} that won the event.
	 */
	public Race getWinnerRace()
	{
		return race;
	}
	
	/**
	 * Determines the winning race based on current scores.<br>
	 * It compares {@code asmodiansPoints} and {@code elyosPoins}.<br>
	 * Returns {@code ASMODIANS} if they have more points.<br>
	 * Otherwise, it returns {@code ELYOS}.
	 * @return The {@code Race} that achieved the higher score.
	 */
	public Race getWinnerRaceByScore()
	{
		return Integer.compare(asmodiansPoints.intValue(), elyosPoins.intValue()) > 0 ? Race.ASMODIANS : Race.ELYOS;
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
	 * If not, it adds a new {@code NeviwindCanyonPlayerReward} to the list.
	 * @param player The {@code Player} object to receive the reward.
	 */
	public void regPlayerReward(Player player)
	{
		if (!containPlayer(player.getObjectId()))
		{
			addPlayerReward(new NeviwindCanyonPlayerReward(player.getObjectId(), bonusTime, player.getRace()));
		}
	}
	
	/**
	 * Adds a specific reward to the list of rewards for this instance.<br>
	 * This method calls the {@code addPlayerReward} method.
	 * @param reward The {@code NeviwindCanyonPlayerReward} object to be added.
	 */
	@Override
	public void addPlayerReward(NeviwindCanyonPlayerReward reward)
	{
		super.addPlayerReward(reward);
	}
	
	/**
	 * Retrieves the reward for a specific player.<br>
	 * This method casts the result of the parent class call to {@code NeviwindCanyonPlayerReward}.
	 * @param object The unique identifier of the player.
	 * @return The {@code NeviwindCanyonPlayerReward} associated with the given object.
	 */
	@Override
	public NeviwindCanyonPlayerReward getPlayerReward(Integer object)
	{
		return (NeviwindCanyonPlayerReward) super.getPlayerReward(object);
	}
	
	/**
	 * Sends a score packet to all players in the instance.<br>
	 * This method uses {@code sendPacket} to deliver information.
	 * @param type The packet type identifier.
	 * @param object The specific object associated with the reward data.
	 */
	public void sendPacket(int type, Integer object)
	{
		instance.doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(type, getTime(), getInstanceReward(), object)));
	}
	
	/**
	 * Calculates the remaining time for the current instance.<br>
	 * The result is returned as an {@code int}.
	 * @return the remaining time in milliseconds.
	 */
	public int getTime()
	{
		final long result = System.currentTimeMillis() - instanceTime;
		if (result < 90000)
		{
			return (int) (90000 - result);
		}
		
		if (result < 1800000)
		{
			return (int) (1800000 - (result - 90000));
		}
		
		return 0;
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
	 * Records the current system time as the start time for this instance.<br>
	 * This method updates the {@code instanceTime} field using {@code System.currentTimeMillis()}.
	 */
	public void setInstanceStartTime()
	{
		instanceTime = System.currentTimeMillis();
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
	 * Checks if any player has reached the required point threshold.<br>
	 * It compares the highest score among all rewards to {@code capPoints}.
	 * @return {@code true} if at least one reward meets or exceeds {@code capPoints}, otherwise {@code false}.
	 */
	public boolean hasCapPoints()
	{
		return getInstanceRewards().stream().mapToInt(NeviwindCanyonPlayerReward::getPoints).max().orElse(0) >= capPoints;
	}
}
