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
import com.aionemu.gameserver.model.instance.playerreward.KamarBattlefieldPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * This class handles the reward logic for the Kamar Battlefield instance.<br>
 * It manages how {@link KamarBattlefieldPlayerReward} objects are distributed to players.
 * @author Alcapwnd
 */
public class KamarBattlefieldReward extends InstanceReward<KamarBattlefieldPlayerReward>
{
	private final int winnerPoints;
	private final int looserPoints;
	private final int capPoints;
	private final AtomicInteger asmodiansPoints = new AtomicInteger(3800);
	private final AtomicInteger elyosPoins = new AtomicInteger(3800);
	private final AtomicInteger asmodiansPvpKills = new AtomicInteger(0);
	private final AtomicInteger elyosPvpKills = new AtomicInteger(0);
	private Race race;
	private Point3D asmodiansStartPosition;
	private Point3D elyosStartPosition;
	protected WorldMapInstance instance;
	private long instanceTime;
	private final int bonusTime;
	
	/**
	 * Creates a new reward object for the Kamar Battlefield.<br>
	 * This constructor initializes default points and sets start positions.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the specific instance.
	 * @param instance The {@link WorldMapInstance} where the battle occurs.
	 */
	public KamarBattlefieldReward(Integer mapId, int instanceId, WorldMapInstance instance)
	{
		super(mapId, instanceId);
		this.instance = instance;
		winnerPoints = 3000;
		looserPoints = 2500;
		capPoints = 30000;
		bonusTime = 12000;
		setStartPositions();
	}
	
	/**
	 * Sorts the player rewards based on their scores.<br>
	 * The list is ordered from highest score to lowest score.
	 * @return A {@code List} of {@link KamarBattlefieldPlayerReward} objects sorted by points.
	 */
	public List<KamarBattlefieldPlayerReward> sortPoints()
	{
		return getInstanceRewards().stream().sorted(Comparator.comparingInt(KamarBattlefieldPlayerReward::getScorePoints).reversed()).collect(Collectors.toList());
	}
	
	/**
	 * Initializes the starting coordinates for both races.<br>
	 * This method randomly assigns positions to {@code asmodiansStartPosition} and {@code elyosStartPosition}.<br>
	 * It uses {@code get} to decide which race starts at which location.
	 */
	private void setStartPositions()
	{
		final Point3D a = new Point3D(1535.6626f, 1573.9294f, 612.42f);
		final Point3D b = new Point3D(1463.7689f, 1227.7777f, 581.62f);
		final Point3D c = new Point3D(1204.9827f, 1350.6719f, 612.91f);
		final Point3D d = new Point3D(1098.1141f, 1540.7119f, 585.10f);
		if (Rnd.get(2) == 0)
		{
			asmodiansStartPosition = a;
			elyosStartPosition = c;
		}
		else
		{
			asmodiansStartPosition = b;
			elyosStartPosition = d;
		}
	}
	
	/**
	 * Teleports the player to a random starting position based on their race.<br>
	 * It uses {@code teleportTo} to move the character.
	 * @param player The {@code Player} object to be teleported.
	 */
	public void portToPosition(Player player)
	{
		if (player.getRace() == Race.ASMODIANS)
		{
			TeleportService2.teleportTo(player, mapId, instanceId, asmodiansStartPosition.getX(), asmodiansStartPosition.getY(), asmodiansStartPosition.getZ());
		}
		else
		{
			TeleportService2.teleportTo(player, mapId, instanceId, elyosStartPosition.getX(), elyosStartPosition.getY(), elyosStartPosition.getZ());
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
		switch (race)
		{
			case ELYOS:
				return elyosPoins;
			case ASMODIANS:
				return asmodiansPoints;
			default:
				break;
		}
		
		return null;
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
		switch (race)
		{
			case ELYOS:
				return elyosPvpKills;
			case ASMODIANS:
				return asmodiansPvpKills;
			default:
				break;
		}
		
		return null;
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
	 * Retrieves the total points for the losing side.<br>
	 * This value is calculated during the instance reward process.
	 * @return The number of points assigned to the loser.
	 */
	public int getLooserPoints()
	{
		return looserPoints;
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
	 * If not, it adds a new {@code KamarBattlefieldPlayerReward} to the list.
	 * @param player The {@code Player} object to receive the reward.
	 */
	public void regPlayerReward(Player player)
	{
		if (!containPlayer(player.getObjectId()))
		{
			addPlayerReward(new KamarBattlefieldPlayerReward(player.getObjectId(), bonusTime, player.getRace()));
		}
	}
	
	/**
	 * Adds a specific reward to the list of rewards for this instance.<br>
	 * This method calls the {@code addPlayerReward} method.
	 * @param reward The {@code KamarBattlefieldPlayerReward} object to be added.
	 */
	@Override
	public void addPlayerReward(KamarBattlefieldPlayerReward reward)
	{
		super.addPlayerReward(reward);
	}
	
	/**
	 * Retrieves the reward for a specific player in the Kamar Battlefield.<br>
	 * This method casts the result to a {@code KamarBattlefieldPlayerReward}.
	 * @param object The unique identifier of the player.
	 * @return The {@code KamarBattlefieldPlayerReward} associated with the given ID.
	 */
	@Override
	public KamarBattlefieldPlayerReward getPlayerReward(Integer object)
	{
		return (KamarBattlefieldPlayerReward) super.getPlayerReward(object);
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
	 * Calculates the remaining time for the current instance.<br>
	 * The result is returned as an {@code int}.
	 * @return the remaining time in milliseconds.
	 */
	public int getTime()
	{
		final long result = System.currentTimeMillis() - instanceTime;
		if (result < 45000)
		{
			return (int) (45000 - result);
		}
		else if (result < 1800000)
		{
			// 30 Minutes.
			return (int) (1800000 - (result - 20000));
		}
		
		return 0;
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
		return getInstanceRewards().stream().mapToInt(KamarBattlefieldPlayerReward::getPoints).max().orElse(0) >= capPoints;
	}
}
