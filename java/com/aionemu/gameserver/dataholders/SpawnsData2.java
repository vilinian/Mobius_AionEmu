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
package com.aionemu.gameserver.dataholders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.spawns.Spawn;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnMap;
import com.aionemu.gameserver.model.templates.spawns.SpawnSearchResult;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.basespawns.BaseSpawn;
import com.aionemu.gameserver.model.templates.spawns.dynamicportalspawns.DynamicPortalSpawn;
import com.aionemu.gameserver.model.templates.spawns.riftspawns.RiftSpawn;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawn;
import com.aionemu.gameserver.model.templates.spawns.vortexspawns.VortexSpawn;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.spawnengine.SpawnHandlerType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class manages the data for game world spawns.<br>
 * It handles the loading and storage of {@link SpawnTemplate} objects from XML files.<br>
 * It provides a centralized repository for accessing spawn information across different maps.
 * @author xTz
 * @modified Rolandas
 */
@XmlRootElement(name = "spawns")
@XmlType(namespace = "", name = "SpawnsData2")
@XmlAccessorType(XmlAccessType.NONE)
public class SpawnsData2
{
	private static final Logger log = LoggerFactory.getLogger(SpawnsData2.class);
	@XmlElement(name = "spawn_map", type = SpawnMap.class)
	protected List<SpawnMap> templates;
	private final TIntObjectHashMap<Map<Integer, SimpleEntry<SpawnGroup2, Spawn>>> allSpawnMaps = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<List<SpawnGroup2>> baseSpawnMaps = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<List<SpawnGroup2>> riftSpawnMaps = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<List<SpawnGroup2>> siegeSpawnMaps = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<List<SpawnGroup2>> vortexSpawnMaps = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<List<SpawnGroup2>> dynamicPortalSpawnMaps = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<Spawn> customs = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the object is unmarshalled from XML.<br>
	 * It initializes internal maps and organizes spawn data based on the loaded templates.<br>
	 * It processes various spawn types including base, rift, siege, vortex, and dynamic portal spawns.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of this instance.
	 */
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	public void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (templates != null)
		{
			for (SpawnMap spawnMap : templates)
			{
				final int mapId = spawnMap.getMapId();
				if (!allSpawnMaps.containsKey(mapId))
				{
					allSpawnMaps.put(mapId, new HashMap<>());
				}
				
				for (Spawn spawn : spawnMap.getSpawns())
				{
					if (spawn.isCustom())
					{
						if (allSpawnMaps.get(mapId).containsKey(spawn.getNpcId()))
						{
							allSpawnMaps.get(mapId).remove(spawn.getNpcId());
						}
						
						customs.put(spawn.getNpcId(), spawn);
					}
					else if (customs.containsKey(spawn.getNpcId()))
					{
						continue;
					}
					
					allSpawnMaps.get(mapId).put(spawn.getNpcId(), new SimpleEntry(new SpawnGroup2(mapId, spawn), spawn));
				}
				
				if (!allSpawnMaps.containsKey(mapId))
				{
					allSpawnMaps.put(mapId, new HashMap<>());
				}
				
				for (BaseSpawn BaseSpawn : spawnMap.getBaseSpawns())
				{
					final int baseId = BaseSpawn.getId();
					if (!baseSpawnMaps.containsKey(baseId))
					{
						baseSpawnMaps.put(baseId, new ArrayList<>());
					}
					
					for (BaseSpawn.SimpleRaceTemplate simpleRace : BaseSpawn.getBaseRaceTemplates())
					{
						for (Spawn spawn : simpleRace.getSpawns())
						{
							if (spawn.isCustom())
							{
								if (allSpawnMaps.get(mapId).containsKey(spawn.getNpcId()))
								{
									allSpawnMaps.get(mapId).remove(spawn.getNpcId());
								}
								
								customs.put(spawn.getNpcId(), spawn);
							}
							else if (customs.containsKey(spawn.getNpcId()))
							{
								continue;
							}
							
							final SpawnGroup2 spawnGroup = new SpawnGroup2(mapId, spawn, baseId, simpleRace.getBaseRace());
							allSpawnMaps.get(mapId).put(spawn.getNpcId(), new SimpleEntry(spawnGroup, spawn));
							baseSpawnMaps.get(baseId).add(spawnGroup);
						}
					}
				}
				
				for (RiftSpawn rift : spawnMap.getRiftSpawns())
				{
					final int id = rift.getId();
					if (!riftSpawnMaps.containsKey(id))
					{
						riftSpawnMaps.put(id, new ArrayList<>());
					}
					
					for (Spawn spawn : rift.getSpawns())
					{
						if (spawn.isCustom())
						{
							if (allSpawnMaps.get(mapId).containsKey(spawn.getNpcId()))
							{
								allSpawnMaps.get(mapId).remove(spawn.getNpcId());
							}
							
							customs.put(spawn.getNpcId(), spawn);
						}
						else if (customs.containsKey(spawn.getNpcId()))
						{
							continue;
						}
						
						final SpawnGroup2 spawnGroup = new SpawnGroup2(mapId, spawn, id);
						allSpawnMaps.get(mapId).put(spawn.getNpcId(), new SimpleEntry(spawnGroup, spawn));
						riftSpawnMaps.get(id).add(spawnGroup);
					}
				}
				
				for (SiegeSpawn SiegeSpawn : spawnMap.getSiegeSpawns())
				{
					final int siegeId = SiegeSpawn.getSiegeId();
					if (!siegeSpawnMaps.containsKey(siegeId))
					{
						siegeSpawnMaps.put(siegeId, new ArrayList<>());
					}
					
					for (SiegeSpawn.SiegeRaceTemplate race : SiegeSpawn.getSiegeRaceTemplates())
					{
						for (SiegeSpawn.SiegeRaceTemplate.SiegeModTemplate mod : race.getSiegeModTemplates())
						{
							if ((mod == null) || (mod.getSpawns() == null))
							{
								continue;
							}
							
							for (Spawn spawn : mod.getSpawns())
							{
								if (spawn.isCustom())
								{
									if (allSpawnMaps.get(mapId).containsKey(spawn.getNpcId()))
									{
										allSpawnMaps.get(mapId).remove(spawn.getNpcId());
									}
									
									customs.put(spawn.getNpcId(), spawn);
								}
								else if (customs.containsKey(spawn.getNpcId()))
								{
									continue;
								}
								
								final SpawnGroup2 spawnGroup = new SpawnGroup2(mapId, spawn, siegeId, race.getSiegeRace(), mod.getSiegeModType());
								allSpawnMaps.get(mapId).put(spawn.getNpcId(), new SimpleEntry(spawnGroup, spawn));
								siegeSpawnMaps.get(siegeId).add(spawnGroup);
							}
						}
					}
				}
				
				for (VortexSpawn VortexSpawn : spawnMap.getVortexSpawns())
				{
					final int id = VortexSpawn.getId();
					if (!vortexSpawnMaps.containsKey(id))
					{
						vortexSpawnMaps.put(id, new ArrayList<>());
					}
					
					for (VortexSpawn.VortexStateTemplate type : VortexSpawn.getSiegeModTemplates())
					{
						if ((type == null) || (type.getSpawns() == null))
						{
							continue;
						}
						
						for (Spawn spawn : type.getSpawns())
						{
							if (spawn.isCustom())
							{
								if (allSpawnMaps.get(mapId).containsKey(spawn.getNpcId()))
								{
									allSpawnMaps.get(mapId).remove(spawn.getNpcId());
								}
								
								customs.put(spawn.getNpcId(), spawn);
							}
							else if (customs.containsKey(spawn.getNpcId()))
							{
								continue;
							}
							
							final SpawnGroup2 spawnGroup = new SpawnGroup2(mapId, spawn, id, type.getStateType());
							vortexSpawnMaps.get(id).add(spawnGroup);
						}
					}
				}
				
				for (DynamicPortalSpawn DynamicPortalSpawn : spawnMap.getDynamicPortalSpawns())
				{
					final int id = DynamicPortalSpawn.getId();
					if (!dynamicPortalSpawnMaps.containsKey(id))
					{
						dynamicPortalSpawnMaps.put(id, new ArrayList<>());
					}
					
					for (DynamicPortalSpawn.DynamicPortalStateTemplate type : DynamicPortalSpawn.getSiegeModTemplates())
					{
						if ((type == null) || (type.getSpawns() == null))
						{
							continue;
						}
						
						for (Spawn spawn : type.getSpawns())
						{
							if (spawn.isCustom())
							{
								if (allSpawnMaps.get(mapId).containsKey(spawn.getNpcId()))
								{
									allSpawnMaps.get(mapId).remove(spawn.getNpcId());
								}
								
								customs.put(spawn.getNpcId(), spawn);
							}
							else if (customs.containsKey(spawn.getNpcId()))
							{
								continue;
							}
							
							final SpawnGroup2 spawnGroup = new SpawnGroup2(mapId, spawn, id, type.getDynamicPortalType());
							dynamicPortalSpawnMaps.get(id).add(spawnGroup);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Removes all loaded templates from the internal list.<br>
	 * This method sets the {@code templates} field to {@code null}.<br>
	 * Use this to clear memory when data is no longer needed.
	 */
	public void clearTemplates()
	{
		if (templates != null)
		{
			templates.clear();
			templates = null;
		}
	}
	
	/**
	 * Retrieves a list of {@link SpawnGroup2} objects for a specific world.<br>
	 * This method looks up the spawn maps associated with the provided {@code worldId}.<br>
	 * If no data exists for that ID, it returns an empty list.
	 * @param worldId The unique identifier of the world to search in.
	 * @return A {@code List} of {@link SpawnGroup2} objects belonging to the specified world.
	 */
	public List<SpawnGroup2> getSpawnsByWorldId(int worldId)
	{
		if (!allSpawnMaps.containsKey(worldId))
		{
			return Collections.emptyList();
		}
		
		return allSpawnMaps.get(worldId).values().stream().map(SimpleEntry::getKey).collect(Collectors.toList());
	}
	
	/**
	 * Retrieves the {@code Spawn} data for a specific NPC in a given world.<br>
	 * This method checks if the provided IDs exist in the current spawn maps.<br>
	 * It returns {@code null} if no matching spawn is found.
	 * @param worldId The unique identifier for the world.
	 * @param npcId The unique identifier for the NPC.
	 * @return The {@code Spawn} object associated with the IDs, or {@code null}.
	 */
	public Spawn getSpawnsForNpc(int worldId, int npcId)
	{
		if (!allSpawnMaps.containsKey(worldId) || !allSpawnMaps.get(worldId).containsKey(npcId))
		{
			return null;
		}
		
		return allSpawnMaps.get(worldId).get(npcId).getValue();
	}
	
	/**
	 * Retrieves the list of {@link SpawnGroup2} objects for a specific location.<br>
	 * This method looks up the data using the provided {@code id}.
	 * @param id The unique identifier for the location.
	 * @return A {@code List} of {@link SpawnGroup2} objects, or {@code null} if no data is found.
	 */
	public List<SpawnGroup2> getBaseSpawnsByLocId(int id)
	{
		return baseSpawnMaps.get(id);
	}
	
	/**
	 * Retrieves the list of {@link SpawnGroup2} objects for a specific Rift location.<br>
	 * This method looks up the data using the provided {@code id}.
	 * @param id The unique identifier for the Rift location.
	 * @return A {@code List} of {@link SpawnGroup2} objects, or {@code null} if no matches are found.
	 */
	public List<SpawnGroup2> getRiftSpawnsByLocId(int id)
	{
		return riftSpawnMaps.get(id);
	}
	
	/**
	 * Retrieves the list of {@link SpawnGroup2} objects for a specific siege.<br>
	 * This method looks up the spawn data using the provided {@code siegeId}.
	 * @param siegeId The unique identifier for the siege.
	 * @return A {@code List} of {@link SpawnGroup2} objects associated with the siege, or {@code null} if not found.
	 */
	public List<SpawnGroup2> getSiegeSpawnsByLocId(int siegeId)
	{
		return siegeSpawnMaps.get(siegeId);
	}
	
	/**
	 * Retrieves the list of {@link SpawnGroup2} objects for a specific vortex location.<br>
	 * This method looks up the data using the provided {@code id}.
	 * @param id The unique identifier for the location.
	 * @return A {@code List} of {@link SpawnGroup2} objects, or {@code null} if no matches are found.
	 */
	public List<SpawnGroup2> getVortexSpawnsByLocId(int id)
	{
		return vortexSpawnMaps.get(id);
	}
	
	/**
	 * Retrieves the list of {@link SpawnGroup2} for a specific location ID.<br>
	 * This method looks up data from the internal {@code dynamicPortalSpawnMaps}.
	 * @param id The unique identifier for the location.
	 * @return A {@code List} of {@link SpawnGroup2} objects, or {@code null} if no data is found.
	 */
	public List<SpawnGroup2> getDynamicPortalSpawnsByLocId(int id)
	{
		return dynamicPortalSpawnMaps.get(id);
	}
	
	/**
	 * Saves the spawn location of a {@link VisibleObject} to the static data files.<br>
	 * This method updates the coordinates and heading for the specified object.<br>
	 * It handles both adding new spots and updating existing ones based on the {@code delete} flag.
	 * @param admin The {@link Player} who performed the action.
	 * @param visibleObject The {@link VisibleObject} to be saved.
	 * @param delete If {@code true}, it removes the spot; if {@code false}, it updates or adds it.
	 * @return {@code true} if the save was successful, {@code false} otherwise.
	 * @throws IOException
	 */
	public synchronized boolean saveSpawn(Player admin, VisibleObject visibleObject, boolean delete) throws IOException
	{
		final SpawnTemplate spawn = visibleObject.getSpawn();
		Spawn oldGroup = DataManager.SPAWNS_DATA2.getSpawnsForNpc(visibleObject.getWorldId(), spawn.getNpcId());
		
		final File xml = new File("./data/static_data/spawns/" + getRelativePath(visibleObject));
		SpawnsData2 data = null;
		final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		JAXBContext jc = null;
		boolean addGroup = false;
		
		try
		{
			schema = sf.newSchema(new File("./data/static_data/spawns/spawns.xsd"));
			jc = JAXBContext.newInstance(SpawnsData2.class);
		}
		catch (Exception e)
		{
			// ignore, if schemas are wrong then we even could not call the command;
		}
		
		FileInputStream fin = null;
		if (xml.exists())
		{
			try
			{
				fin = new FileInputStream(xml);
				if (jc != null)
				{
					final Unmarshaller unmarshaller = jc.createUnmarshaller();
					unmarshaller.setSchema(schema);
					data = (SpawnsData2) unmarshaller.unmarshal(fin);
				}
			}
			catch (Exception e)
			{
				log.error(e.getMessage());
				PacketSendUtility.sendMessage(admin, "Could not load old XML file!");
				return false;
			}
			finally
			{
				if (fin != null)
				{
					fin.close();
				}
			}
		}
		
		if ((oldGroup == null) || oldGroup.isCustom())
		{
			if (data == null)
			{
				data = new SpawnsData2();
			}
			
			oldGroup = data.getSpawnsForNpc(visibleObject.getWorldId(), spawn.getNpcId());
			if (oldGroup == null)
			{
				oldGroup = new Spawn(spawn.getNpcId(), spawn.getRespawnTime(), spawn.getHandlerType());
				addGroup = true;
			}
		}
		else
		{
			if (data == null)
			{
				data = DataManager.SPAWNS_DATA2;
			}
			
			// only remove from memory, will be added back later
			allSpawnMaps.get(visibleObject.getWorldId()).remove(spawn.getNpcId());
			addGroup = true;
		}
		
		final SpawnSpotTemplate spot = new SpawnSpotTemplate(visibleObject.getX(), visibleObject.getY(), visibleObject.getZ(), visibleObject.getHeading(), visibleObject.getSpawn().getRandomWalk(), visibleObject.getSpawn().getWalkerId(), visibleObject.getSpawn().getWalkerIndex());
		final boolean changeX = visibleObject.getX() != spawn.getX();
		final boolean changeY = visibleObject.getY() != spawn.getY();
		final boolean changeZ = visibleObject.getZ() != spawn.getZ();
		boolean changeH = visibleObject.getHeading() != spawn.getHeading();
		if (changeH && (visibleObject instanceof Npc))
		{
			final Npc npc = (Npc) visibleObject;
			if (!npc.isAtSpawnLocation() || !npc.isInState(CreatureState.NPC_IDLE) || changeX || changeY || changeZ)
			{
				// if H changed, XSD validation fails, because it may be negative; thus, reset it back
				visibleObject.setXYZH(null, null, null, spawn.getHeading());
				changeH = false;
			}
		}
		
		SpawnSpotTemplate oldSpot = null;
		for (SpawnSpotTemplate s : oldGroup.getSpawnSpotTemplates())
		{
			if ((s.getX() == spot.getX()) && (s.getY() == spot.getY()) && (s.getZ() == spot.getZ()) && (s.getHeading() == spot.getHeading()))
			{
				if (delete || !Objects.equals(s.getWalkerId(), spot.getWalkerId()))
				{
					oldSpot = s;
					break;
				}
				
				return false; // nothing to change
			}
			else if ((changeX && (s.getY() == spot.getY()) && (s.getZ() == spot.getZ()) && (s.getHeading() == spot.getHeading())) || (changeY && (s.getX() == spot.getX()) && (s.getZ() == spot.getZ()) && (s.getHeading() == spot.getHeading())) || (changeZ && (s.getX() == spot.getX()) && (s.getY() == spot.getY()) && (s.getHeading() == spot.getHeading())) || (changeH && (s.getX() == spot.getX()) && (s.getY() == spot.getY()) && (s.getZ() == spot.getZ())))
			{
				oldSpot = s;
				break;
			}
		}
		
		if (oldSpot != null)
		{
			oldGroup.getSpawnSpotTemplates().remove(oldSpot);
		}
		
		if (!delete)
		{
			oldGroup.addSpawnSpot(spot);
		}
		
		oldGroup.setCustom(true);
		
		SpawnMap map = null;
		if (data.templates == null)
		{
			data.templates = new ArrayList<>();
			map = new SpawnMap(spawn.getWorldId());
			data.templates.add(map);
		}
		else
		{
			map = data.templates.get(0);
		}
		
		if (addGroup)
		{
			map.addSpawns(oldGroup);
		}
		
		FileOutputStream fos = null;
		try
		{
			xml.getParentFile().mkdir();
			fos = new FileOutputStream(xml);
			if (jc != null)
			{
				final Marshaller marshaller = jc.createMarshaller();
				marshaller.setSchema(schema);
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(data, fos);
			}
			
			DataManager.SPAWNS_DATA2.templates = data.templates;
			DataManager.SPAWNS_DATA2.afterUnmarshal(null, null);
			DataManager.SPAWNS_DATA2.clearTemplates();
			data.clearTemplates();
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
			PacketSendUtility.sendMessage(admin, "Could not save XML file!");
			return false;
		}
		finally
		{
			if (fos != null)
			{
				fos.close();
			}
		}
		
		return true;
	}
	
	/**
	 * Gets the file path for a specific {@link VisibleObject}.<br>
	 * This method determines the folder based on the object type.<br>
	 * It returns a formatted string containing the world ID and map name.
	 * @param visibleObject The object to retrieve the path for.
	 * @return A {@code String} representing the relative file path.
	 */
	String getRelativePath(VisibleObject visibleObject)
	{
		String path;
		final WorldMap map = World.getInstance().getWorldMap(visibleObject.getWorldId());
		if (visibleObject.getSpawn().getHandlerType() == SpawnHandlerType.RIFT)
		{
			path = "Rifts";
		}
		else if (visibleObject instanceof Gatherable)
		{
			path = "Gather";
		}
		else if (map.isInstanceType())
		{
			path = "Instances";
		}
		else
		{
			path = "Npcs";
		}
		
		return path + "/New/" + visibleObject.getWorldId() + "_" + map.getName().replace(' ', '_') + ".xml";
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return allSpawnMaps.size();
	}
	
	/**
	 * Finds the first available spawn for a specific NPC.<br>
	 * This method searches across different worlds if the primary world returns no results.<br>
	 * It returns a {@code SpawnSearchResult} containing the correct world ID and the first spot template.
	 * @param worldId The initial world ID to search in.
	 * @param npcId The unique identifier of the NPC.
	 * @return A {@code SpawnSearchResult} object or {@code null} if no spawn is found.
	 */
	public SpawnSearchResult getFirstSpawnByNpcId(int worldId, int npcId)
	{
		Spawn spawns = DataManager.SPAWNS_DATA2.getSpawnsForNpc(worldId, npcId);
		
		if (spawns == null)
		{
			for (WorldMapTemplate template : DataManager.WORLD_MAPS_DATA)
			{
				if (template.getMapId() == worldId)
				{
					continue;
				}
				
				spawns = DataManager.SPAWNS_DATA2.getSpawnsForNpc(template.getMapId(), npcId);
				if (spawns != null)
				{
					worldId = template.getMapId();
					break;
				}
			}
			
			if (spawns == null)
			{
				return null;
			}
		}
		
		return new SpawnSearchResult(worldId, spawns.getSpawnSpotTemplates().get(0));
	}
	
	/**
	 * Adds a new {@code SpawnMap} to the internal list of templates.<br>
	 * This method initializes the template list if it is currently {@code null}.
	 * @param spawnMap The {@code SpawnMap} object to be added.
	 */
	public void addNewSpawnMap(SpawnMap spawnMap)
	{
		if (templates == null)
		{
			templates = new ArrayList<>();
		}
		
		templates.add(spawnMap);
	}
	
	/**
	 * Removes objects from the spawn maps that are identified as event spawns.<br>
	 * This method iterates through a list of {@code VisibleObject} items.<br>
	 * It checks if the object belongs to an active event template.<br>
	 * If it matches, the corresponding entry is removed from the internal data structures.
	 * @param objects The list of {@code VisibleObject} instances to check and potentially remove.
	 */
	public void removeEventSpawnObjects(List<VisibleObject> objects)
	{
		for (VisibleObject visObj : objects)
		{
			if (!allSpawnMaps.contains(visObj.getWorldId()))
			{
				continue;
			}
			
			final SimpleEntry<SpawnGroup2, Spawn> entry = allSpawnMaps.get(visObj.getWorldId()).get(visObj.getObjectTemplate().getTemplateId());
			if (!entry.getValue().isEventSpawn())
			{
				continue;
			}
			
			if (entry.getValue().getEventTemplate().equals(visObj.getSpawn().getEventTemplate()))
			{
				allSpawnMaps.get(visObj.getWorldId()).remove(visObj.getObjectTemplate().getTemplateId());
			}
		}
	}
	
	/**
	 * Retrieves the list of all loaded spawn maps.<br>
	 * This method returns the internal collection of {@link SpawnMap} objects.
	 * @return a {@code List} containing all {@code SpawnMap} templates.
	 */
	public List<SpawnMap> getTemplates()
	{
		return templates;
	}
	
	/**
	 * Creates a copy of the current {@code SpawnsData2} object.<br>
	 * This method copies all entries from the internal spawn maps into a new instance.
	 * @return A new {@code SpawnsData2} object containing the same data.
	 */
	@Override
	public SpawnsData2 clone()
	{
		final SpawnsData2 sd = new SpawnsData2();
		sd.allSpawnMaps.putAll(allSpawnMaps);
		return sd;
	}
	
}
