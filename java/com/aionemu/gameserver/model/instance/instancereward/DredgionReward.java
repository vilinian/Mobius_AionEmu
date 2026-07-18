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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.geometry.Point3D;
import com.aionemu.gameserver.model.instance.playerreward.DredgionPlayerReward;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * Represents the rewards granted to players for completing a {@link DredgionPlayerReward} instance.<br>
 * This class handles the logic for distributing specific items or benefits associated with the Dredgion content.
 * @author xTz
 */
public class DredgionReward extends InstanceReward<DredgionPlayerReward>
{
	private final int winnerPoints;
	private final int looserPoints;
	@SuppressWarnings("unused")
	private final int drawPoins;
	private final AtomicInteger asmodiansPoints = new AtomicInteger(0);
	private final AtomicInteger elyosPoins = new AtomicInteger(0);
	private Race race;
	private final List<DredgionRooms> dredgionRooms = new ArrayList<>();
	private Point3D asmodiansStartPosition;
	private Point3D elyosStartPosition;
	
	/**
	 * Creates a new {@link DredgionReward} instance.<br>
	 * This constructor initializes the reward points based on the provided map ID.<br>
	 * It also sets up the starting positions and available rooms.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the specific instance.
	 */
	public DredgionReward(Integer mapId, int instanceId)
	{
		super(mapId, instanceId);
		winnerPoints = mapId == 300110000 ? 3000 : 4500;
		looserPoints = mapId == 300110000 ? 1500 : 2500;
		drawPoins = mapId == 300110000 ? 2250 : 3750;
		setStartPositions();
		for (int i = 1; i < 15; i++)
		{
			dredgionRooms.add(new DredgionRooms(i));
		}
	}
	
	/**
	 * Initializes the starting coordinates for both races.<br>
	 * This method randomly assigns positions to {@code asmodiansStartPosition} and {@code elyosStartPosition}.<br>
	 * It uses {@code get} to decide which race starts at which location.
	 */
	private void setStartPositions()
	{
		final Point3D a = new Point3D(570.468f, 166.897f, 432.28986f);
		final Point3D b = new Point3D(400.741f, 166.713f, 432.290f);
		
		if (Rnd.get(2) == 0)
		{
			asmodiansStartPosition = a;
			elyosStartPosition = b;
		}
		else
		{
			asmodiansStartPosition = b;
			elyosStartPosition = a;
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
	 * 1 Primary Armory 2 Backup Armory 3 Gravity Control 4 Engine Room 5 Auxiliary Power 6 Weapons Deck 7 Lower Weapons Deck 8 Ready Room 1 9 Ready Room 2 10 Barracks 11 Logistics Managment 12 Logistics Storage 13 The Bridge 14 Captain's Room
	 */
	public class DredgionRooms
	{
		private final int roomId;
		private int state = 0xFF;
		
		public DredgionRooms(int roomId)
		{
			this.roomId = roomId;
		}
		
		public int getRoomId()
		{
			return roomId;
		}
		
		public void captureRoom(Race race)
		{
			state = race.equals(Race.ASMODIANS) ? 0x01 : 0x00;
		}
		
		public int getState()
		{
			return state;
		}
	}
	
	/**
	 * Retrieves the list of rooms associated with this {@link DredgionReward}.<br>
	 * This method returns all {@code DredgionRooms} currently stored in the instance.
	 * @return A {@code List} containing all {@code DredgionRooms}.
	 */
	public List<DredgionRooms> getDredgionRooms()
	{
		return dredgionRooms;
	}
	
	/**
	 * Finds a specific room within the {@link DredgionReward} instance.<br>
	 * It searches through all available rooms to find a match for the provided ID.
	 * @param roomId The unique identifier of the room to find.
	 * @return The matching {@code DredgionRooms} object, or {@code null} if no match is found.
	 */
	public DredgionRooms getDredgionRoomById(int roomId)
	{
		for (DredgionRooms dredgionRoom : dredgionRooms)
		{
			if (dredgionRoom.getRoomId() == roomId)
			{
				return dredgionRoom;
			}
		}
		
		return null;
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
	 * Resets all data associated with this reward.<br>
	 * It clears the internal room list and calls {@code clear} on the parent class.
	 */
	@Override
	public void clear()
	{
		super.clear();
		dredgionRooms.clear();
	}
}
