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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.geometry.Point3D;
import com.aionemu.gameserver.model.instance.playerreward.PandaemoniumBattlefieldPlayerReward;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * This class handles the reward logic for the Pandaemonium Battlefield instance.<br>
 * It manages how {@link PandaemoniumBattlefieldPlayerReward} objects are distributed to players.
 * @author Falke_34
 */
public class PandaemoniumBattlefieldReward extends InstanceReward<PandaemoniumBattlefieldPlayerReward>
{
	private int points;
	private int npcKills;
	private int rank = 7;
	protected WorldMapInstance instance;
	
	/**
	 * Creates a new reward object for the Pandaemonium Battlefield.<br>
	 * This constructor initializes the reward with specific map and instance data.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the specific instance.
	 * @param instance The {@link WorldMapInstance} where the battle takes place.
	 */
	public PandaemoniumBattlefieldReward(Integer mapId, int instanceId, WorldMapInstance instance)
	{
		super(mapId, instanceId);
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
		final Point3D AsmoStartPoint = new Point3D(1240.918f + Rx, 1177.3456f + Ry, 214.66058f);
		
		if (player.getRace() == Race.ASMODIANS)
		{
			TeleportService2.teleportTo(player, mapId, instanceId, AsmoStartPoint.getX(), AsmoStartPoint.getY(), AsmoStartPoint.getZ(), (byte) 15);
		}
	}
	
	/**
	 * Adds a specific amount of points to the current total.<br>
	 * This updates the internal {@code points} field.
	 * @param points The number of points to add.
	 */
	public void addPoints(int points)
	{
		this.points += points;
	}
	
	/**
	 * Retrieves the current number of points for this rank.<br>
	 * This value is used to determine the player's standing in the arena.
	 * @return The current {@code int} value of points.
	 */
	public int getPoints()
	{
		return points;
	}
	
	/**
	 * Increments the total number of NPC kills.<br>
	 * This updates the {@code npcKills} counter for the current reward instance.
	 */
	public void addNpcKill()
	{
		npcKills++;
	}
	
	/**
	 * Retrieves the total number of NPCs killed.<br>
	 * This value is updated by calling {@code addNpcKill}.
	 * @return The current count of NPC kills as an {@code int}.
	 */
	public int getNpcKills()
	{
		return npcKills;
	}
	
	/**
	 * Updates the current ranking of the player.<br>
	 * This method sets the {@code rank} field to a new value.
	 * @param rank The new integer value for the rank.
	 */
	public void setRank(int rank)
	{
		this.rank = rank;
	}
	
	/**
	 * Retrieves the current rank of the {@code MCEntry}.<br>
	 * This value is used to determine the item's standing.
	 * @return The integer value representing the rank.
	 */
	public int getRank()
	{
		return rank;
	}
}
