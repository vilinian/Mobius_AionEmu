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
package com.aionemu.gameserver.services.teleport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.ReturnScrollsConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.portal.PortalLoc;

/**
 * Handles the logic for teleporting players using scrolls.<br>
 * This service manages scroll consumption and coordinates with {@link com.aionemu.gameserver.dataholders.DataManager} to update player data.
 * @author GiGatR00n
 */
public class ScrollsTeleporterService
{
	private static final Logger log = LoggerFactory.getLogger(ScrollsTeleporterService.class);
	
	/**
	 * Teleports a {@link Player} to a specific location using a scroll.<br>
	 * This method retrieves the coordinates from {@code LocId} and moves the player to the target world.<br>
	 * It also applies an animation based on the server configuration.
	 * @param player The {@link Player} object to be teleported.
	 * @param LocId The unique identifier for the destination portal location.
	 * @param worldId The ID of the world where the player will land.
	 */
	public static void ScrollTeleprter(Player player, int LocId, int worldId)
	{
		final PortalLoc loc = DataManager.PORTAL_LOC_DATA.getPortalLoc(LocId);
		
		if (loc == null)
		{
			log.warn("No Portal location for locId" + LocId);
			return;
		}
		
		if (ReturnScrollsConfig.TELEPORT_ANIMATION == 0)
		{
			TeleportService2.teleportTo(player, worldId, loc.getX(), loc.getY(), loc.getZ(), player.getHeading(), TeleportAnimation.NO_ANIMATION);
		}
		else if (ReturnScrollsConfig.TELEPORT_ANIMATION == 1)
		{
			TeleportService2.teleportTo(player, worldId, loc.getX(), loc.getY(), loc.getZ(), player.getHeading(), TeleportAnimation.BEAM_ANIMATION);
		}
		else if (ReturnScrollsConfig.TELEPORT_ANIMATION == 2)
		{
			TeleportService2.teleportTo(player, worldId, loc.getX(), loc.getY(), loc.getZ(), player.getHeading(), TeleportAnimation.JUMP_ANIMATION);
		}
		else if (ReturnScrollsConfig.TELEPORT_ANIMATION == 3)
		{
			TeleportService2.teleportTo(player, worldId, loc.getX(), loc.getY(), loc.getZ(), player.getHeading(), TeleportAnimation.JUMP_ANIMATION_2);
		}
		else if (ReturnScrollsConfig.TELEPORT_ANIMATION == 4)
		{
			TeleportService2.teleportTo(player, worldId, loc.getX(), loc.getY(), loc.getZ(), player.getHeading(), TeleportAnimation.JUMP_ANIMATION_3);
		}
		else
		{
			TeleportService2.teleportTo(player, worldId, loc.getX(), loc.getY(), loc.getZ(), player.getHeading(), TeleportAnimation.NO_ANIMATION);
		}
	}
	
	/**
	 * Retrieves the correct location ID for a scroll based on the world and race.<br>
	 * This method maps specific {@code worldId} values to unique IDs for {@link Race}.ELYOS or Asmo characters.<br>
	 * It returns 0 if the provided {@code worldId} is not recognized.
	 * @param worldId The unique identifier of the world.
	 * @param race The character race used to determine the specific location.
	 * @return The corresponding scroll location ID or 0 if not found.
	 */
	public static int getScrollLocIdbyWorldId(int worldId, Race race)
	{
		switch (worldId)
		{
			case 600050000: // Kaisinel's Beacon (Elyos Katalam) | Danuar Spire (Asmo Katalam)
				return (race == Race.ELYOS ? 6000502 : 6000503);
			case 600070000: // Idian Depths
				return (race == Race.ELYOS ? 6000700 : 6000701);
			case 400010000: // Teminon Fortress (Elyos) | Primum Fortress (Asmo)
				return (race == Race.ELYOS ? 4000100 : 4000101);
			case 700010000: // Oriel (Elyos) | Pernon (Asmo)
			case 710010000:
				return (race == Race.ELYOS ? 7000101 : 7100100);
			case 110070000: // Kaisinel Academy (Elyos) | Marchutan Priory (Asmo)
			case 120080000:
				return (race == Race.ELYOS ? 1100702 : 1200800);
			case 600100000: // Levinshor Gerha (Elyos) | Levinshor Gerha (Asmo)
				return (race == Race.ELYOS ? 6001007 : 6001008);
			case 600060000: // Pandarunerk's Delve (Elyos Danarina) | Pandarunerk (Asmo Danarina)
				return 6000603;
		}
		
		return 0;
	}
}
