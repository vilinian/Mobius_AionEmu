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
package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SIEGE_LOCATION_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Handles the automated logic for siege races within the game world.<br>
 * This class manages the progression and rules for {@link SiegeRace} events.<br>
 * It coordinates between {@link SiegeService} and other core systems to ensure smooth race execution.
 */
public class SiegeAutoRace
{
	private static String[] siegeIds = SiegeConfig.SIEGE_AUTO_LOCID.split(";");
	
	/**
	 * Automatically manages the race and ownership of a siege location.<br>
	 * This method updates the {@code SiegeRace} based on configuration.<br>
	 * It handles NPC spawning, de-spawning, and system messages for players.<br>
	 * The updated data is saved to the database via {@link SiegeDAO}.
	 * @param locid The unique identifier of the siege location.
	 */
	public static void AutoSiegeRace(int locid)
	{
		final SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(locid);
		if (!loc.getRace().equals(SiegeRace.ASMODIANS) || !loc.getRace().equals(SiegeRace.ELYOS))
		{
			ThreadPoolManager.getInstance().schedule(new Runnable()
			{
				@Override
				public void run()
				{
					SiegeService.getInstance().startSiege(locid);
				}
			}, 300000);
			SiegeService.getInstance().deSpawnNpcs(locid);
			final int oldOwnerRaceId = loc.getRace().getRaceId();
			final int legionId = loc.getLegionId();
			final String legionName = legionId != 0 ? LegionService.getInstance().getLegion(legionId).getLegionName() : "";
			final DescriptionId NameId = new DescriptionId(loc.getTemplate().getNameId());
			if (ElyosAutoSiege(locid))
			{
				loc.setRace(SiegeRace.ELYOS);
			}
			
			if (AsmoAutoSiege(locid))
			{
				loc.setRace(SiegeRace.ASMODIANS);
			}
			
			loc.setLegionId(0);
			World.getInstance().doOnAllPlayers(new Visitor<Player>()
			{
				@Override
				public void visit(Player player)
				{
					if ((legionId != 0) && (player.getRace().getRaceId() == oldOwnerRaceId))
					{
						// %0 has conquered %1.
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1301038, legionName, NameId));
					}
					
					// %0 succeeded in conquering %1.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1301039, loc.getRace().getDescriptionId(), NameId));
					PacketSendUtility.sendPacket(player, new SM_SIEGE_LOCATION_INFO(loc));
				}
			});
			if (ElyosAutoSiege(locid))
			{
				SiegeService.getInstance().spawnNpcs(locid, SiegeRace.ELYOS, SiegeModType.PEACE);
			}
			else if (AsmoAutoSiege(locid))
			{
				SiegeService.getInstance().spawnNpcs(locid, SiegeRace.ASMODIANS, SiegeModType.PEACE);
			}
			
			DAOManager.getDAO(SiegeDAO.class).updateSiegeLocation(loc);
		}
		
		SiegeService.getInstance().broadcastUpdate(loc);
	}
	
	/**
	 * Checks if a specific location is configured for an auto-siege race.<br>
	 * This method evaluates both {@code ElyosAutoSiege} and {@code AsmoAutoSiege} conditions.
	 * @param locId The unique identifier of the siege location.
	 * @return {@code true} if the location is an auto-siege, otherwise {@code false}.
	 */
	public static boolean isAutoSiege(int locId)
	{
		return ElyosAutoSiege(locId) || AsmoAutoSiege(locId);
	}
	
	/**
	 * Checks if a specific location is enabled for Elyos auto siege.<br>
	 * This method compares the provided {@code locId} against the configured list.
	 * @param locId The unique identifier of the siege location to check.
	 * @return {@code true} if the location is in the auto siege list, otherwise {@code false}.
	 */
	public static boolean ElyosAutoSiege(int locId)
	{
		for (String id : siegeIds[0].split(","))
		{
			if (locId == Integer.parseInt(id))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific location is set for an automatic Asmo siege.<br>
	 * This method compares the provided {@code locId} against the configured list.
	 * @param locId The unique identifier of the siege location to check.
	 * @return {@code true} if the location is enabled for auto-siege, otherwise {@code false}.
	 */
	public static boolean AsmoAutoSiege(int locId)
	{
		for (String id : siegeIds[1].split(","))
		{
			if (locId == Integer.parseInt(id))
			{
				return true;
			}
		}
		
		return false;
	}
}
