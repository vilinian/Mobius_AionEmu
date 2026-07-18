package com.aionemu.gameserver.spawnengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Manages the spawning logic for conquest-related entities in the game world.<br>
 * It handles the distribution and lifecycle of {@link SpawnTemplate} objects within specific zones. This class ensures that spawns are synchronized with the current server state.
 * @author CoolyT
 */
public class ConquestSpawnManager
{
	private static final Logger log = LoggerFactory.getLogger(ConquestSpawnManager.class);
	
	private static Map<WorldPosition, List<SpawnTemplate>> spawnsByPosition = new HashMap<>();
	private static Map<Integer, List<WorldPosition>> portals = new HashMap<>();
	
	private static List<VisibleObject> spawned = new ArrayList<>();
	
	/**
	 * This method triggers the spawning of all available conquest NPCs.<br>
	 * It iterates through every position in {@code spawnsByPosition}.<br>
	 * For each location, it selects one random {@link SpawnTemplate} to spawn.<br>
	 * The total number of spawned objects is logged via {@code log}.
	 */
	public static void spawnAll()
	{
		int spawns = 0;
		for (Entry<WorldPosition, List<SpawnTemplate>> spawnpos : spawnsByPosition.entrySet())
		{
			if (spawnpos.getValue().size() == 0)
			{
				continue;
			}
			
			final int index = Rnd.get(0, (spawnpos.getValue().size() - 1));
			
			final SpawnTemplate st = spawnpos.getValue().get(index);
			spawns++;
			spawn(st);
		}
		
		GameServer.log.info("[ConquestSpawnManager] Spawned " + spawns + " ConquestNpcs");
		// writeTemplate();
	}
	
	/**
	 * Spawns a random entity at the specified location.<br>
	 * This method searches for templates associated with the {@code pos}.<br>
	 * It schedules the spawn to occur after a delay of 895 seconds.
	 * @param pos The {@link WorldPosition} where the spawn should occur.
	 */
	public static void spawnByLoc(WorldPosition pos)
	{
		// boolean found = false;
		for (Entry<WorldPosition, List<SpawnTemplate>> spawnpos : spawnsByPosition.entrySet())
		{
			if ((spawnpos.getKey().getX() == pos.getX()) && (spawnpos.getKey().getY() == pos.getY()) && (spawnpos.getKey().getZ() == pos.getZ()))
			{
				// found = true;
				final int index = Rnd.get(0, (spawnpos.getValue().size() - 1));
				final SpawnTemplate st = spawnpos.getValue().get(index);
				final int respawntime = 895;
				ThreadPoolManager.getInstance().schedule(() -> spawn(st), respawntime * 1000);
			}
		}
		
		// if (!found) log.warn("Didn't found Location for "+pos.toString());
	}
	
	/**
	 * Removes all active spawns at a specific location.<br>
	 * This method checks the {@code spawned} list for objects matching the coordinates of {@code pos}.<br>
	 * If a match is found, it deletes the object and removes it from the list.
	 * @param pos The {@code WorldPosition} where the spawns should be removed.
	 */
	public static void deSpawnByLoc(WorldPosition pos)
	{
		for (VisibleObject spawn : spawned)
		{
			if ((spawn.getPosition().getX() == pos.getX()) && (spawn.getPosition().getY() == pos.getY()) && (spawn.getPosition().getZ() == pos.getZ()))
			{
				if (spawn.isSpawned())
				{
					spawn.getController().delete();
					spawned.remove(spawn);
					break;
				}
			}
		}
	}
	
	/**
	 * This method handles the creation of a new object in the game world.<br>
	 * It sets the respawn time to {@code 0}.<br>
	 * It uses {@code int)} to create the entity.<br>
	 * The resulting {@code VisibleObject} is added to the global spawned list.
	 * @param st The {@code SpawnTemplate} containing the data for the object to be created.
	 */
	private static void spawn(SpawnTemplate st)
	{
		st.setRespawnTime(0);
		final VisibleObject visibleObject = SpawnEngine.spawnObject(st, 1);
		spawned.add(visibleObject);
	}
	
	/**
	 * Registers new conquest spawn templates into the system.<br>
	 * This method processes all templates within a {@link SpawnGroup2}.<br>
	 * It maps each template to its corresponding {@code WorldPosition}.
	 * @param sg The {@code SpawnGroup2} containing the templates to add.
	 */
	public static void addConquestSpawnTemplate(SpawnGroup2 sg)
	{
		for (SpawnTemplate spawn : sg.getSpawnTemplates())
		{
			WorldPosition pos = new WorldPosition(spawn.getWorldId());
			for (Entry<WorldPosition, List<SpawnTemplate>> set : spawnsByPosition.entrySet())
			{
				if ((spawn.getX() == set.getKey().getX()) && (spawn.getY() == set.getKey().getY()) && (spawn.getZ() == set.getKey().getZ()))
				{
					pos = set.getKey();
				}
			}
			
			if (spawnsByPosition.containsKey(pos))
			{
				spawnsByPosition.get(pos).add(spawn);
			}
			else
			{
				final List<SpawnTemplate> stList = new ArrayList<>();
				pos.setXYZH(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getHeading());
				spawnsByPosition.put(pos, stList);
			}
		}
	}
	
	/**
	 * Generates and logs XML spawn templates for specific maps.<br>
	 * This method processes {@code spawnsByPosition} to create data for Gelkmaros and Inggison.<br>
	 * The resulting XML strings are printed to the server log.
	 */
	public static void writeTemplate()
	{
		final StringBuilder spots_gelk = new StringBuilder();
		final StringBuilder spots_ingg = new StringBuilder();
		final StringBuilder template_gelk = new StringBuilder();
		final StringBuilder template_ingg = new StringBuilder();
		final List<Integer> npc_gelk = new ArrayList<>();
		final List<Integer> npc_ingg = new ArrayList<>();
		
		// for (int i = 236307; i <= 236362; i++) // Npc Inggison
		for (int i = 661632; i <= 661655; i++) // Npc Inggison
		{
			npc_ingg.add(i);
		}
		
		// for (int i = 236363; i <= 236418; i++) // Npc Gelkmaros
		for (int i = 661934; i <= 661957; i++) // Npc Gelkmaros
		{
			npc_gelk.add(i);
		}
		
		for (WorldPosition wpos : spawnsByPosition.keySet())
		{
			if ((wpos.getX() == 0.0) && (wpos.getY() == 0.0))
			{
				continue;
			}
			
			if (wpos.getMapId() == 220070000)
			{
				spots_gelk.append("\t\t\t<spot h=\"" + wpos.getHeading() + "\" x=\"" + wpos.getX() + "\" y=\"" + wpos.getY() + "\" z=\"" + wpos.getZ() + "\"/>\r\n");
			}
			else if (wpos.getMapId() == 210050000)
			{
				spots_ingg.append("\t\t\t<spot h=\"" + wpos.getHeading() + "\" x=\"" + wpos.getX() + "\" y=\"" + wpos.getY() + "\" z=\"" + wpos.getZ() + "\"/>\r\n");
			}
		}
		
		spots_gelk.append("\t\t</spawn>\r\n");
		spots_ingg.append("\t\t</spawn>\r\n");
		
		template_gelk.append("\r\n<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n");
		template_gelk.append("<spawns>\r\n");
		template_gelk.append("\t<spawn_map map_id=\"220070000\">\r\n");
		
		// SpawnTemplate Gelkmaros
		for (int id : npc_gelk)
		{
			template_gelk.append("\t\t<!-- " + DataManager.NPC_DATA.getNpcTemplate(id).getName() + " -->\r\n");
			template_gelk.append("\t\t<spawn npc_id=\"" + id + "\" respawn_time=\"895\" handler=\"CONQUEST\">\r\n");
			template_gelk.append(spots_gelk);
		}
		template_gelk.append("\t</spawn_map>\r\n");
		template_gelk.append("</spawns>\r\n");
		
		template_ingg.append("\r\n<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n");
		template_ingg.append("<spawns>\r\n");
		template_ingg.append("\t<spawn_map map_id=\"210050000\">\r\n");
		
		// SpawnTemplate Inggison
		for (int id : npc_ingg)
		{
			template_ingg.append("\t\t<!-- " + DataManager.NPC_DATA.getNpcTemplate(id).getName() + " -->\r\n");
			template_ingg.append("\t\t<spawn npc_id=\"" + id + "\" respawn_time=\"895\" handler=\"CONQUEST\">\r\n");
			template_ingg.append(spots_ingg);
		}
		template_ingg.append("\t</spawn_map>\r\n");
		template_ingg.append("</spawns>\r\n");
		
		log.warn(template_gelk.toString());
		log.warn("------------------------------------------------------------------------------------------");
		log.warn(template_ingg.toString());
	}
	
	/**
	 * Retrieves a list of {@link WorldPosition} objects for a specific NPC.<br>
	 * This method looks up the locations associated with the provided {@code npcId}.<br>
	 * It returns an empty {@code List} if no locations are found.
	 * @param npcId The unique identifier of the NPC to search for.
	 * @return A {@code List} containing all positions for the given ID.
	 */
	public static List<WorldPosition> getLocationsByNpcId(int npcId)
	{
		if (portals.containsKey(npcId))
		{
			return portals.get(npcId);
		}
		
		return new ArrayList<>();
	}
}
