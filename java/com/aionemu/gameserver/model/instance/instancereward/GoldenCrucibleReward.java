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
import com.aionemu.gameserver.model.instance.playerreward.GoldenCruciblePlayerReward;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * Represents the rewards granted to players for completing the {@link GoldenCruciblePlayerReward} instance.<br>
 * This class handles the logic for distributing specific items or effects associated with this content.
 * @author
 */
public class GoldenCrucibleReward extends InstanceReward<GoldenCruciblePlayerReward>
{
	/**
	 * Creates a new {@code GoldenCrucibleReward} object.<br>
	 * This constructor initializes the reward with specific location data.<br>
	 * It uses the values provided to set up the instance properties.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the specific instance.
	 */
	public GoldenCrucibleReward(Integer mapId, int instanceId)
	{
		super(mapId, instanceId);
		// TODO Auto-generated constructor stub
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
		final Point3D ElyosStartPoint = new Point3D(383.37f + Rx, 224.3448f + Ry, 231.12335f); // Elyos Center
		final Point3D AsmoStartPoint = new Point3D(386.1356f + Rx, 283.4616f + Ry, 231.1234f); // Asmo Center
		
		if (player.getRace() == Race.ASMODIANS)
		{
			TeleportService2.teleportTo(player, mapId, instanceId, AsmoStartPoint.getX(), AsmoStartPoint.getY(), AsmoStartPoint.getZ(), (byte) 90);
		}
		else
		{
			TeleportService2.teleportTo(player, mapId, instanceId, ElyosStartPoint.getX(), ElyosStartPoint.getY(), ElyosStartPoint.getZ(), (byte) 30);
		}
	}
	
}
