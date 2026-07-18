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
package com.aionemu.gameserver.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.dao.TownDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.housing.HouseAddress;
import com.aionemu.gameserver.model.templates.housing.HousingLand;
import com.aionemu.gameserver.model.town.Town;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TOWNS_LIST;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * Manages the logic and data related to towns within the game world.<br>
 * This service handles town information, player interactions with towns, and {@link Town} management.
 * @author ViAl
 */
public class TownService
{
	private static final Logger log = LoggerFactory.getLogger(TownService.class);
	private final Map<Integer, Town> elyosTowns;
	private final Map<Integer, Town> asmosTowns;
	
	private static class SingletonHolder
	{
		protected static final TownService instance = new TownService();
	}
	
	/**
	 * Provides access to the singleton instance of {@link TownService}.<br>
	 * Use this method to get the global service for town-related operations.
	 * @return The single shared instance of {@code TownService}.
	 */
	public static TownService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor for the {@link TownService} class.<br>
	 * This constructor initializes the town data from the database.<br>
	 * It also loads any missing town data from the housing configuration files.
	 */
	private TownService()
	{
		elyosTowns = DAOManager.getDAO(TownDAO.class).load(Race.ELYOS);
		asmosTowns = DAOManager.getDAO(TownDAO.class).load(Race.ASMODIANS);
		if ((elyosTowns.size() == 0) && (asmosTowns.size() == 0))
		{
			for (HousingLand land : DataManager.HOUSE_DATA.getLands())
			{
				for (HouseAddress address : land.getAddresses())
				{
					if (address.getTownId() != 0)
					{
						final Race townRace = DataManager.NPC_DATA.getNpcTemplate(land.getManagerNpcId()).getTribe() == TribeClass.GENERAL ? Race.ELYOS : Race.ASMODIANS;
						if (((townRace == Race.ELYOS) && !elyosTowns.containsKey(address.getTownId())) || ((townRace == Race.ASMODIANS) && !asmosTowns.containsKey(address.getTownId())))
						{
							final Town town = new Town(address.getTownId(), townRace);
							if (townRace == Race.ELYOS)
							{
								elyosTowns.put(town.getId(), town);
							}
							else
							{
								asmosTowns.put(town.getId(), town);
							}
							
							DAOManager.getDAO(TownDAO.class).store(town);
						}
						
					}
				}
			}
		}
		
		GameServer.log.info("[TownService] Loaded totally " + (asmosTowns.size() + elyosTowns.size()) + " Towns (Elyos: " + elyosTowns.size() + " / Asmos: " + asmosTowns.size() + ")");
	}
	
	/**
	 * Retrieves a {@link Town} object based on its unique identifier.<br>
	 * This method searches both Elyos and Asmos town maps.
	 * @param townId The unique ID of the town to find.
	 * @return The {@code Town} object if found, or {@code null} otherwise.
	 */
	public Town getTownById(int townId)
	{
		if (elyosTowns.containsKey(townId))
		{
			return elyosTowns.get(townId);
		}
		
		return asmosTowns.get(townId);
	}
	
	/**
	 * Retrieves the town ID where a {@link Player} currently resides.<br>
	 * This method checks if the player has an active {@code House}.<br>
	 * If no house is found, it returns {@code 0}.
	 * @param player The {@code Player} object to check for residence.
	 * @return The unique town ID of the player's house or {@code 0} if none exists.
	 */
	public int getTownResidence(Player player)
	{
		final House house = player.getActiveHouse();
		if (house == null)
		{
			return 0;
		}
		
		return house.getAddress().getTownId();
	}
	
	/**
	 * Finds the {@code townId} associated with a specific creature's location.<br>
	 * This method first checks if the {@link Creature} is an {@link Npc} with a valid ID.<br>
	 * If not, it searches for a town ID within the current map region and zones.
	 * @param creature The {@code Creature} to check for its position.
	 * @return The integer ID of the town, or {@code 0} if no town is found.
	 */
	public int getTownIdByPosition(Creature creature)
	{
		if (creature instanceof Npc)
		{
			if (((Npc) creature).getTownId() != 0)
			{
				return ((Npc) creature).getTownId();
			}
		}
		
		int townId = 0;
		final MapRegion region = creature.getPosition().getMapRegion();
		if (region == null)
		{
			log.warn("[TownService] npc " + creature.getName() + " haven't any map region!");
			return 0;
		}
		
		final List<ZoneInstance> zones = region.getZones(creature);
		for (ZoneInstance zone : zones)
		{
			townId = zone.getTownId();
			if (townId > 0)
			{
				break;
			}
		}
		
		return townId;
	}
	
	/**
	 * Handles special logic when a {@link Player} enters specific world IDs.<br>
	 * It sends {@code SM_FLAG_INFO} packets to all players based on spawned NPCs in those worlds.<br>
	 * This method also triggers a zone update for the player controller.
	 * @param player The {@link Player} object that entered the world.
	 */
	public void onEnterWorld(Player player)
	{
		if ((player.getWorldId() != 700010000) && (player.getWorldId() != 710010000))
		{
			// offi 4.9.1 send empty packet
			PacketSendUtility.sendPacket(player, new SM_TOWNS_LIST(new HashMap<>()));
			return;
		}
		
		switch (player.getRace())
		{
			case ELYOS:
				if (player.getWorldId() == 700010000)
				{
					PacketSendUtility.sendPacket(player, new SM_TOWNS_LIST(elyosTowns));
				}
				break;
			case ASMODIANS:
				if (player.getWorldId() == 710010000)
				{
					PacketSendUtility.sendPacket(player, new SM_TOWNS_LIST(asmosTowns));
				}
				break;
			default:
				break;
		}
	}
}
