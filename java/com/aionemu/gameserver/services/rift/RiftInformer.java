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
package com.aionemu.gameserver.services.rift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.aionemu.gameserver.controllers.RVController;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RIFT_ANNOUNCE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * This class manages the broadcasting of rift-related information to players.<br>
 * It handles sending {@link SM_RIFT_ANNOUNCE} packets to notify users about active rifts.<br>
 * It works in coordination with the {@link RVController} to update the game world state.
 * @author Source
 */
public class RiftInformer
{
	/**
	 * Retrieves a list of all spawned rifts for a specific world.<br>
	 * This method filters the global rift list by the provided {@code worldId}.
	 * @param worldId The unique identifier of the world to check.
	 * @return A {@code List} of {@link Npc} objects representing the rifts in that world.
	 */
	public static List<Npc> getSpawned(int worldId)
	{
		final List<Npc> rifts = RiftManager.getSpawned();
		final List<Npc> worldRifts = new CopyOnWriteArrayList<>();
		for (Npc rift : rifts)
		{
			if (rift.getWorldId() == worldId)
			{
				worldRifts.add(rift);
			}
		}
		
		return worldRifts;
	}
	
	/**
	 * Sends the current rift information to all players in a specific world.<br>
	 * This method synchronizes data for both the primary and twin worlds.
	 * @param worldId The unique identifier of the world to update.
	 */
	public static void sendRiftsInfo(int worldId)
	{
		syncRiftsState(worldId, getPackets(worldId));
		final int twinId = getTwinId(worldId);
		if (twinId > 0)
		{
			syncRiftsState(twinId, getPackets(twinId));
		}
	}
	
	/**
	 * Sends the current rift information to a specific player.<br>
	 * This method updates the player's view of rifts in their own world.<br>
	 * It also sends data for twin worlds if they exist.
	 * @param player The {@link Player} who will receive the rift data.
	 */
	public static void sendRiftsInfo(Player player)
	{
		syncRiftsState(player, getPackets(player.getWorldId()));
		final int twinId = getTwinId(player.getWorldId());
		if (twinId > 0)
		{
			syncRiftsState(twinId, getPackets(twinId));
		}
	}
	
	/**
	 * Sends rift information to all players in the specified worlds.<br>
	 * This method iterates through an array of world IDs.<br>
	 * It synchronizes the current state of rifts for each world.
	 * @param worlds An {@code int[]} array containing the IDs of the worlds to update.
	 */
	public static void sendRiftInfo(int[] worlds)
	{
		for (int worldId : worlds)
		{
			syncRiftsState(worldId, getPackets(worlds[0], -1));
		}
	}
	
	/**
	 * Sends a notification to remove a specific rift from the game.<br>
	 * This method updates the state for all players in the specified world.
	 * @param worldId The unique identifier of the world.
	 * @param objId The unique identifier of the object to despawn.
	 */
	public static void sendRiftDespawn(int worldId, int objId)
	{
		syncRiftsState(worldId, getPackets(worldId, objId), true);
	}
	
	/**
	 * Retrieves the list of packets for a specific world.<br>
	 * This method calls {@code int)} using a default object ID of {@code 0}.
	 * @param worldId The unique identifier of the world to query.
	 * @return A {@code List} of {@link AionServerPacket} objects for the given world.
	 */
	private static List<AionServerPacket> getPackets(int worldId)
	{
		return getPackets(worldId, 0);
	}
	
	/**
	 * Retrieves a list of {@link AionServerPacket} objects for a specific world and object.<br>
	 * This method generates the correct announcement packets based on the provided {@code objId}.<br>
	 * It handles different logic depending on whether {@code objId} is -1, greater than 0, or other values.
	 * @param worldId The unique identifier for the world.
	 * @param objId The unique identifier for the object.
	 * @return A {@code List} of {@link AionServerPacket} objects to be sent to the client.
	 */
	private static List<AionServerPacket> getPackets(int worldId, int objId)
	{
		final List<AionServerPacket> packets = new ArrayList<>();
		if (objId == -1)
		{
			for (Npc rift : getSpawned(worldId))
			{
				final RVController controller = (RVController) rift.getController();
				if (!controller.isMaster())
				{
					continue;
				}
				
				packets.add(new SM_RIFT_ANNOUNCE(controller, false));
			}
		}
		else if (objId > 0)
		{
			packets.add(new SM_RIFT_ANNOUNCE(objId));
		}
		else
		{
			packets.add(new SM_RIFT_ANNOUNCE(getAnnounceData(worldId)));
			for (Npc rift : getSpawned(worldId))
			{
				final RVController controller = (RVController) rift.getController();
				if (!controller.isMaster())
				{
					continue;
				}
				
				packets.add(new SM_RIFT_ANNOUNCE(controller, true));
				packets.add(new SM_RIFT_ANNOUNCE(controller, false));
			}
		}
		
		return packets;
	}
	
	/*
	 * Sends generated rift info packets to player
	 */
	/**
	 * Updates the rift state for a specific player.<br>
	 * This method sends all provided packets to the {@code Player}.
	 * @param player The {@link Player} who will receive the updates.
	 * @param packets A {@code List} of {@link AionServerPacket} objects to be sent.
	 */
	private static void syncRiftsState(Player player, List<AionServerPacket> packets)
	{
		for (AionServerPacket packet : packets)
		{
			PacketSendUtility.sendPacket(player, packet);
		}
	}
	
	/*
	 * Sends generated rift info packets to all players within world
	 */
	/**
	 * Synchronizes the current state of rifts for a specific world.<br>
	 * This method updates the data using the provided list of {@code AionServerPacket} objects.<br>
	 * It ensures that all clients receive consistent information about active rifts.
	 * @param worldId The unique identifier for the world to sync.
	 * @param packets The list of {@code AionServerPacket} objects containing rift data.
	 */
	private static void syncRiftsState(int worldId, List<AionServerPacket> packets)
	{
		syncRiftsState(worldId, packets, false);
	}
	
	/**
	 * Synchronizes the current state of rifts for all players in a specific world.<br>
	 * This method handles both general rift updates and specific despawn information.
	 * @param worldId The unique identifier of the world to update.
	 * @param packets A {@code List} of {@link AionServerPacket} objects to be sent to players.
	 * @param isDespawnInfo Set to {@code true} if the packets contain despawn data, otherwise {@code false}.
	 */
	private static void syncRiftsState(int worldId, List<AionServerPacket> packets, boolean isDespawnInfo)
	{
		World.getInstance().getWorldMap(worldId).getMainWorldMapInstance().doOnAllPlayers(player -> syncRiftsState(player, packets));
	}
	
	/**
	 * Retrieves the announcement data for a specific world.<br>
	 * This method calculates rift information based on spawned NPCs.<br>
	 * It initializes a map with 14 default entries.
	 * @param worldId The unique identifier of the world to process.
	 * @return A {@code Map} containing the calculated announcement data.
	 */
	private static Map<Integer, Integer> getAnnounceData(int worldId)
	{
		Map<Integer, Integer> localRifts = new HashMap<>();
		
		// init empty list
		for (int i = 0; i < 14; i++)
		{
			// OLD 8 (TODO)
			localRifts.put(i, 0);
		}
		
		for (Npc rift : getSpawned(worldId))
		{
			final RVController rc = (RVController) rift.getController();
			localRifts = calcRiftsData(rc, localRifts);
		}
		
		return localRifts;
	}
	
	/**
	 * Calculates and updates rift data based on the state of a specific {@link RVController}.<br>
	 * This method increments counters in a shared map depending on whether the rift is a master or a vortex.
	 * @param rift The {@code RVController} object representing the current rift.
	 * @param local A {@code Map} used to store and update the calculated rift statistics.
	 * @return The updated {@code Map} containing the new data.
	 */
	private static Map<Integer, Integer> calcRiftsData(RVController rift, Map<Integer, Integer> local)
	{
		if (rift.isMaster())
		{
			local.put(0, local.get(0) + 1);
			if (rift.isVortex())
			{
				local.put(1, local.get(1) + 1);
			}
			
			local.put(2, local.get(2) + 1); // live party
			local.put(3, local.get(3) + 1); // shugo emperor vault
			local.put(4, local.get(4) + 1); // rift battle
		}
		else
		{
			local.put(5, local.get(5) + 1); // rift battle
			local.put(6, local.get(6) + 1); // rift battle
			if (rift.isVortex())
			{
				local.put(7, local.get(7) + 1);
			}
		}
		
		return local;
	}
	
	/**
	 * Retrieves the corresponding twin world ID for a given location.<br>
	 * This method maps specific world IDs to their paired locations.<br>
	 * It returns {@code 0} if no mapping exists.
	 * @param worldId The unique identifier of the source world.
	 * @return The unique identifier of the twin world, or {@code 0} if not found.
	 */
	private static int getTwinId(int worldId)
	{
		switch (worldId)
		{
			case 110070000: // Kaisinel Academy -> Brusthonin
				return 220050000;
			case 210020000: // Eltnen -> Morheim
				return 220020000;
			case 210040000: // Heiron -> Beluslan
				return 220040000;
			case 210050000: // Inggison -> Gelkmaros
				return 220070000;
			case 210060000: // Theobomos -> Marchutan Priory
				return 120080000;
			case 210070000: // Cygnea -> Enshar
				return 220080000;
			case 210100000: // Iluma -> Norsvold
				return 220110000;
			case 120080000: // Marchutan Priory -> Theobomos
				return 210060000;
			case 220020000: // Morheim -> Eltnen
				return 210020000;
			case 220040000: // Beluslan -> Heiron
				return 210040000;
			case 220050000: // Brusthonin -> Kaisinel Academy
				return 110070000;
			case 220070000: // Gelkmaros -> Inggison
				return 210050000;
			case 220080000: // Enshar -> Cygnea
				return 210070000;
			case 220110000: // Norsvold -> Iluma
				return 210100000;
			default:
				return 0;
		}
	}
}
