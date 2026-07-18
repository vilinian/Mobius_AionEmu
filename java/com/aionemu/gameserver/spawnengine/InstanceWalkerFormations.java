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
package com.aionemu.gameserver.spawnengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * This class manages the formation patterns for walker NPCs within instances.<br>
 * It defines how groups of NPCs move and position themselves relative to each other.
 * @author Rolandas
 */
public class InstanceWalkerFormations
{
	private static final Logger log = LoggerFactory.getLogger(InstanceWalkerFormations.class);
	private final Map<String, List<ClusteredNpc>> groupedSpawnObjects;
	private final Map<String, WalkerGroup> walkFormations;
	private final Map<String, List<WalkerGroup>> formationVariants;
	private final Map<String, List<ClusteredNpc>> walkerVariants;
	
	/**
	 * Initializes a new instance of the {@code InstanceWalkerFormations} class.<br>
	 * This constructor sets up the internal maps for walker groups and formations.
	 */
	public InstanceWalkerFormations()
	{
		groupedSpawnObjects = new HashMap<>();
		walkFormations = new HashMap<>();
		formationVariants = new HashMap<>();
		walkerVariants = new HashMap<>();
	}
	
	/**
	 * Retrieves a {@link WalkerGroup} based on the provided identifier.<br>
	 * This method looks up the group in the internal formation map.<br>
	 * It returns {@code null} if no matching ID is found.
	 * @param walkerId The unique identifier for the walker to look up.
	 * @return The {@link WalkerGroup} associated with the given ID, or {@code null}.
	 */
	public WalkerGroup getSpawnWalkerGroup(String walkerId)
	{
		return walkFormations.get(walkerId);
	}
	
	/**
	 * Checks if an {@link ClusteredNpc} can be added to the cache.<br>
	 * It identifies the correct group based on the route ID.<br>
	 * The method adds the candidate to the list and returns its success status.
	 * @param npcWalker The {@link ClusteredNpc} object to be cached.
	 * @return {@code true} if the candidate was successfully added, otherwise {@code false}.
	 */
	protected synchronized boolean cacheWalkerCandidate(ClusteredNpc npcWalker)
	{
		final String walkerId = npcWalker.getWalkTemplate().getRouteId();
		List<ClusteredNpc> candidateList = groupedSpawnObjects.get(walkerId);
		if (candidateList == null)
		{
			candidateList = new ArrayList<>();
			groupedSpawnObjects.put(walkerId, candidateList);
		}
		
		return candidateList.add(npcWalker);
	}
	
	/**
	 * Organizes and spawns entities for the current instance.<br>
	 * This method groups {@link ClusteredNpc} objects by their positions.<br>
	 * It handles formation logic and selects random variants to spawn.
	 */
	protected void organizeAndSpawn()
	{
		for (List<ClusteredNpc> candidates : groupedSpawnObjects.values())
		{
			final Map<Integer, List<ClusteredNpc>> bySize = candidates.stream().collect(Collectors.groupingBy(ClusteredNpc::getPositionHash, LinkedHashMap::new, Collectors.toList()));
			final Set<Integer> keys = bySize.keySet();
			int maxSize = 0;
			List<ClusteredNpc> npcs = null;
			for (Integer key : keys)
			{
				if (bySize.get(key).size() > maxSize)
				{
					npcs = bySize.get(key);
					maxSize = npcs.size();
				}
			}
			
			if (npcs == null)
			{
				continue;
			}
			
			if (maxSize == 1)
			{
				if (candidates.size() != 1)
				{
					// log.warn("Walkers not aligned for route: " + candidates.get(0).getWalkTemplate().getRouteId());
					for (ClusteredNpc snpc : candidates)
					{
						snpc.spawn(snpc.getNpc().getSpawn().getZ());
					}
				}
				else
				{
					final ClusteredNpc singleNpc = candidates.get(0);
					if (singleNpc.getWalkTemplate().getVersionId() != null)
					{
						List<ClusteredNpc> variants = walkerVariants.get(singleNpc.getWalkTemplate().getVersionId());
						if (variants == null)
						{
							variants = new ArrayList<>();
							walkerVariants.put(singleNpc.getWalkTemplate().getVersionId(), variants);
						}
						
						variants.add(singleNpc);
					}
					else
					{
						singleNpc.spawn(singleNpc.getNpc().getSpawn().getZ());
					}
				}
			}
			else
			{
				final WalkerGroup wg = new WalkerGroup(npcs);
				if (candidates.get(0).getWalkTemplate().getPool() != candidates.size())
				{
					log.warn("Incorrect pool for route: " + candidates.get(0).getWalkTemplate().getRouteId());
				}
				
				walkFormations.put(candidates.get(0).getWalkTemplate().getRouteId(), wg);
				wg.form();
				if (wg.getVersionId() == null)
				{
					wg.spawn();
					
					// spawn the rest which didn't have the same coordinates
					for (ClusteredNpc snpc : candidates)
					{
						if (npcs.contains(snpc))
						{
							continue;
						}
						
						snpc.spawn(snpc.getNpc().getZ());
					}
				}
				else
				{
					List<WalkerGroup> variants = formationVariants.get(wg.getVersionId());
					if (variants == null)
					{
						variants = new ArrayList<>();
						formationVariants.put(wg.getVersionId(), variants);
					}
					
					variants.add(wg);
				}
			}
			
			// Now that all variants are in the map, spawn one randomly
			for (List<WalkerGroup> varGroups : formationVariants.values())
			{
				final WalkerGroup spawnedGroup = varGroups.get(Rnd.get(varGroups.size()));
				spawnedGroup.spawn();
			}
			
			for (List<ClusteredNpc> varWalkers : walkerVariants.values())
			{
				final ClusteredNpc spawnedWalker = varWalkers.get(Rnd.get(varWalkers.size()));
				spawnedWalker.spawn(spawnedWalker.getNpc().getZ());
			}
		}
	}
	
	/**
	 * Updates the current walker group to a new variation.<br>
	 * This method checks if the {@code walkerGroup} has a valid version ID.<br>
	 * It selects a random unspawned group from the available variants.<br>
	 * The new group is spawned and the old one is despawned if it was active.
	 * @param walkerGroup The current {@link WalkerGroup} to be updated.
	 */
	protected void changeCluster(WalkerGroup walkerGroup)
	{
		if (walkerGroup.getVersionId() == null)
		{
			return;
		}
		
		final List<WalkerGroup> varGroups = formationVariants.get(walkerGroup.getVersionId());
		if (varGroups == null)
		{
			return;
		}
		
		final List<WalkerGroup> notSpawned = varGroups.stream().filter(wg -> !wg.isSpawned()).collect(Collectors.toList());
		final WalkerGroup newGroup = notSpawned.get(Rnd.get(notSpawned.size()));
		newGroup.spawn();
		if (walkerGroup.isSpawned())
		{
			walkerGroup.despawn();
		}
	}
	
	/**
	 * Updates the current walker for a specific NPC.<br>
	 * This method replaces an existing {@code Npc} with a new one from the correct variant list.<br>
	 * It ensures that only non-spawned walkers are selected and handles the despawning of old entities.
	 * @param npc The {@code Npc} object to be processed for a walker change.
	 */
	protected void changeWalker(Npc npc)
	{
		final String walkerId = npc.getSpawn().getWalkerId();
		if (walkerId == null)
		{
			return;
		}
		
		final String versionId = DataManager.WALKER_VERSIONS_DATA.getRouteVersionId(walkerId);
		if (versionId == null)
		{
			return;
		}
		
		final List<ClusteredNpc> varWalkers = walkerVariants.get(versionId);
		if (varWalkers == null)
		{
			return;
		}
		
		final List<ClusteredNpc> notSpawned = varWalkers.stream().filter(cn -> !cn.getNpc().isSpawned()).collect(Collectors.toList());
		final ClusteredNpc newWalker = notSpawned.get(Rnd.get(notSpawned.size()));
		newWalker.spawn(newWalker.getNpc().getZ());
		if (!npc.isSpawned())
		{
			return;
		}
		
		for (ClusteredNpc snpc : varWalkers)
		{
			if (snpc.getNpc().equals(npc))
			{
				snpc.despawn();
				break;
			}
		}
	}
	
	/**
	 * Cleans up the internal data structures when an instance is destroyed.<br>
	 * This method clears {@code groupedSpawnObjects} and {@code walkFormations}.<br>
	 * It ensures that no stale data remains in memory.
	 */
	protected synchronized void onInstanceDestroy()
	{
		groupedSpawnObjects.clear();
		walkFormations.clear();
	}
}
