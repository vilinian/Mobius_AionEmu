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

import com.aionemu.gameserver.controllers.RVController;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.rift.RiftLocation;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.NpcKnownList;

/**
 * Manages the lifecycle and spawning of rifts within the game world.<br>
 * It handles {@link RiftLocation} data and coordinates with {@link World} to ensure correct placement.<br>
 * This class serves as the primary controller for rift-related mechanics.
 * @author Source
 * @modified CoolyT
 */
public class RiftManager
{
	private static List<Npc> rifts = new ArrayList<>();
	private static Map<String, SpawnTemplate> riftGroups = new HashMap<>();
	
	/**
	 * Registers a new {@code SpawnGroup2} into the rift group map.<br>
	 * This method handles both pooled and non-pooled spawn templates.<br>
	 * It uses the anchor of each template as the unique key in the registry.
	 * @param spawn The {@code SpawnGroup2} object containing the templates to add.
	 */
	public static void addRiftSpawnTemplate(SpawnGroup2 spawn)
	{
		if (spawn.hasPool())
		{
			final SpawnTemplate template = spawn.getSpawnTemplates().get(0);
			riftGroups.put(template.getAnchor(), template);
		}
		else
		{
			for (SpawnTemplate template : spawn.getSpawnTemplates())
			{
				riftGroups.put(template.getAnchor(), template);
			}
		}
	}
	
	/**
	 * Creates a new rift at the specified location.<br>
	 * This method initializes the rift and returns its statistics.
	 * @param loc The {@code RiftLocation} where the rift will be spawned.
	 * @return A {@code RiftStatistics} object containing data about the new rift.
	 */
	public RiftStatistics spawnRift(RiftLocation loc)
	{
		final RiftEnum rift = RiftEnum.getRift(loc.getId());
		return spawnRift(rift, null, loc);
	}
	
	/**
	 * Creates a new vortex instance at the specified location.<br>
	 * This method determines the correct {@link RiftEnum} based on the defender race.<br>
	 * It then calls the internal {@code spawnRift} logic to initialize the rift.
	 * @param loc The {@code VortexLocation} where the vortex will be created.
	 * @return A {@code RiftStatistics} object containing data about the new rift.
	 */
	public RiftStatistics spawnVortex(VortexLocation loc)
	{
		final RiftEnum rift = RiftEnum.getVortex(loc.getDefendersRace());
		return spawnRift(rift, loc, null);
	}
	
	/**
	 * Spawns a master and slave NPC for a specific rift type.<br>
	 * This method handles the logic for both standard rifts and vortexes.<br>
	 * It updates the statistics based on the number of instances in the world map.
	 * @param rift The {@link RiftEnum} defining the rift type and templates.
	 * @param vl The {@link VortexLocation} to update if the rift is a vortex.
	 * @param rl The {@link RiftLocation} to update for standard rifts.
	 * @return A {@link RiftStatistics} object containing the results of the spawn operation.
	 */
	private RiftStatistics spawnRift(RiftEnum rift, VortexLocation vl, RiftLocation rl)
	{
		final RiftStatistics stat = new RiftStatistics();
		final SpawnTemplate masterTemplate = riftGroups.get(rift.getMaster());
		SpawnTemplate slaveTemplate = riftGroups.get(rift.getSlave());
		
		if ((masterTemplate == null) || (slaveTemplate == null))
		{
			return stat;
		}
		
		stat.setWorldMap(masterTemplate.getWorldId());
		
		int spawned = 0;
		final int instanceCount = World.getInstance().getWorldMap(masterTemplate.getWorldId()).getInstanceCount();
		
		if (slaveTemplate.hasPool())
		{
			slaveTemplate = slaveTemplate.changeTemplate(1);
		}
		
		for (int i = 1; i <= instanceCount; i++)
		{
			final Npc slave = spawnInstance(i, slaveTemplate, new RVController(null, rift));
			final Npc master = spawnInstance(i, masterTemplate, new RVController(slave, rift));
			
			if (rift.isVortex())
			{
				vl.setVortexController((RVController) master.getController());
				spawned = vl.getSpawned().size();
				vl.getSpawned().add(master);
				vl.getSpawned().add(slave);
				stat.setVortex(true);
			}
			else
			{
				spawned = rl.getSpawned().size();
				rl.getSpawned().add(master);
				rl.getSpawned().add(slave);
			}
			
			if (i == 1)
			{
				stat.setSpawnedNpcs(spawned);
				stat.addSpawnedRifts(1);
			}
		}
		
		return stat;
		// log.info("Rift opened: " + rift.name() + " successfully spawned " + spawned + " Npc.");
	}
	
	/**
	 * Creates and spawns a new {@link Npc} instance in the game world.<br>
	 * This method initializes the NPC with its required controllers and data.<br>
	 * It then places the NPC at the correct coordinates for the specified instance.
	 * @param instance The unique ID of the map instance where the NPC will appear.
	 * @param template The {@link SpawnTemplate} containing the NPC's data and location.
	 * @param controller The {@link RVController} to be associated with this NPC.
	 * @return The newly created and spawned {@link Npc} object.
	 */
	private Npc spawnInstance(int instance, SpawnTemplate template, RVController controller)
	{
		final NpcTemplate masterObjectTemplate = DataManager.NPC_DATA.getNpcTemplate(template.getNpcId());
		final Npc npc = new Npc(IDFactory.getInstance().nextId(), controller, template, masterObjectTemplate);
		
		npc.setKnownlist(new NpcKnownList(npc));
		npc.setEffectController(new EffectController(npc));
		
		final World world = World.getInstance();
		world.storeObject(npc);
		world.setPosition(npc, template.getWorldId(), instance, template.getX(), template.getY(), template.getZ(), template.getHeading());
		world.spawn(npc);
		rifts.add(npc);
		
		return npc;
	}
	
	/**
	 * Retrieves a list of all currently spawned NPCs.<br>
	 * This method returns the internal {@code rifts} collection.
	 * @return A {@code List} containing all active {@link Npc} objects.
	 */
	public static List<Npc> getSpawned()
	{
		return rifts;
	}
	
	/**
	 * Provides the global instance of the {@link RiftManager}.<br>
	 * Use this method to access the manager from anywhere in your code.<br>
	 * This follows the singleton design pattern.
	 * @return The single shared instance of {@code RiftManager}.
	 */
	public static RiftManager getInstance()
	{
		return RiftManagerHolder.INSTANCE;
	}
	
	private static class RiftManagerHolder
	{
		private static final RiftManager INSTANCE = new RiftManager();
	}
}
