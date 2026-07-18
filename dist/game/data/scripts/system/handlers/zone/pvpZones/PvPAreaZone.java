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
package system.handlers.zone.pvpZones;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.handler.ZoneNameAnnotation;

/**
 * Handles the specific logic and behaviors for Player vs Player (PvP) zones.<br>
 * This class manages how players interact within designated combat areas.<br>
 * It extends {@link PvPZone} to provide specialized zone functionality.
 * @author MrPoke
 */
@ZoneNameAnnotation(value = "LC1_PVP_SUB_C DC1_PVP_ZONE")
public class PvPAreaZone extends PvPZone
{
	/**
	 * Moves a player to a specific location based on the provided zone.<br>
	 * This method uses {@link TeleportService2} to handle the movement.
	 * @param player The {@code Player} object to be moved.
	 * @param zoneName The {@code ZoneName} that determines the destination coordinates.
	 */
	@Override
	protected void doTeleport(Player player, ZoneName zoneName)
	{
		if (zoneName == ZoneName.get("LC1_PVP_SUB_C"))
		{
			TeleportService2.teleportTo(player, 110010000, 1, 1470.3f, 1343.5f, 563.7f);
		}
		else if (zoneName == ZoneName.get("DC1_PVP_ZONE"))
		{
			TeleportService2.teleportTo(player, 120010000, 1, 1005.1f, 1528.9f, 222.1f);
		}
	}
}
