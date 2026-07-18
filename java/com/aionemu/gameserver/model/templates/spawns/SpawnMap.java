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
package com.aionemu.gameserver.model.templates.spawns;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.assemblednpc.AssembledNpc;
import com.aionemu.gameserver.model.templates.spawns.basespawns.BaseSpawn;
import com.aionemu.gameserver.model.templates.spawns.dynamicportalspawns.DynamicPortalSpawn;
import com.aionemu.gameserver.model.templates.spawns.riftspawns.RiftSpawn;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawn;
import com.aionemu.gameserver.model.templates.spawns.vortexspawns.VortexSpawn;

/**
 * Represents a collection of spawn points within the game world.<br>
 * This class manages various types of spawns, including {@link BaseSpawn}, {@link DynamicPortalSpawn}, and {@link SiegeSpawn}.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SpawnMap")
public class SpawnMap
{
	@XmlElement(name = "spawn")
	private List<Spawn> spawns;
	@XmlElement(name = "base_spawn")
	private List<BaseSpawn> baseSpawns;
	@XmlElement(name = "rift_spawn")
	private List<RiftSpawn> riftSpawns;
	@XmlElement(name = "siege_spawn")
	private List<SiegeSpawn> siegeSpawns;
	@XmlElement(name = "vortex_spawn")
	private List<VortexSpawn> vortexSpawns;
	@XmlElement(name = "dynamic_portal_spawn")
	private List<DynamicPortalSpawn> dynamicPortalSpawns;
	
	@XmlAttribute(name = "map_id")
	private int mapId;
	
	/**
	 * Creates a new instance of the {@link SpawnMap} class.<br>
	 * This constructor initializes an empty map for spawn data.
	 */
	public SpawnMap()
	{
	}
	
	/**
	 * Creates a new {@link SpawnMap} instance.<br>
	 * This constructor sets the unique identifier for the map.
	 * @param mapId The unique ID of the map to be assigned.
	 */
	public SpawnMap(int mapId)
	{
		this.mapId = mapId;
	}
	
	/**
	 * Retrieves the unique identifier for the map.<br>
	 * This value is assigned during the creation of the {@link AssembledNpc}.
	 * @return The {@code int} representing the map ID.
	 */
	public int getMapId()
	{
		return mapId;
	}
	
	/**
	 * Retrieves the list of {@link Spawn} objects for this map.<br>
	 * If the internal list is {@code null}, it returns a new empty {@code ArrayList}.
	 * @return A {@code List} containing all {@code Spawn} entries.
	 */
	public List<Spawn> getSpawns()
	{
		if (spawns == null)
		{
			spawns = new ArrayList<>();
		}
		
		return spawns;
	}
	
	/**
	 * Adds a new {@link Spawn} to the list of spawns.<br>
	 * This method updates the internal collection of spawn data.
	 * @param spawns The {@code Spawn} object to be added.
	 */
	public void addSpawns(Spawn spawns)
	{
		getSpawns().add(spawns);
	}
	
	/**
	 * Removes a specific spawn from the list of spawns.<br>
	 * This method calls {@code getSpawns} to access the internal list.
	 * @param spawns The {@code Spawn} object to be removed.
	 */
	public void removeSpawns(Spawn spawns)
	{
		getSpawns().remove(spawns);
	}
	
	/**
	 * Retrieves the list of {@link BaseSpawn} objects for this map.<br>
	 * If the list is null, it initializes a new {@code ArrayList}
	 * @return A {@code List} of {@code BaseSpawn} objects.
	 */
	public List<BaseSpawn> getBaseSpawns()
	{
		if (baseSpawns == null)
		{
			baseSpawns = new ArrayList<>();
		}
		
		return baseSpawns;
	}
	
	/**
	 * Adds a new {@link BaseSpawn} to the list of base spawns.<br>
	 * This method updates the internal collection used by {@code getBaseSpawns}.
	 * @param spawns The {@code BaseSpawn} object to add.
	 */
	public void addBaseSpawns(BaseSpawn spawns)
	{
		getBaseSpawns().add(spawns);
	}
	
	/**
	 * Retrieves the list of {@link RiftSpawn} objects for this map.<br>
	 * If no spawns exist, it returns an empty {@code ArrayList}.
	 * @return a {@code List} of {@code RiftSpawn} objects.
	 */
	public List<RiftSpawn> getRiftSpawns()
	{
		if (riftSpawns == null)
		{
			riftSpawns = new ArrayList<>();
		}
		
		return riftSpawns;
	}
	
	/**
	 * Adds a new {@link RiftSpawn} to the list of rift spawns.<br>
	 * This method updates the internal collection for the current map.
	 * @param spawns The {@code RiftSpawn} object to be added.
	 */
	public void addRiftSpawns(RiftSpawn spawns)
	{
		getRiftSpawns().add(spawns);
	}
	
	/**
	 * Retrieves the list of {@link SiegeSpawn} objects for this map.<br>
	 * If the list is null, it returns a new empty {@code ArrayList}.
	 * @return A {@code List} of {@code SiegeSpawn} objects.
	 */
	public List<SiegeSpawn> getSiegeSpawns()
	{
		if (siegeSpawns == null)
		{
			siegeSpawns = new ArrayList<>();
		}
		
		return siegeSpawns;
	}
	
	/**
	 * Adds a new {@link SiegeSpawn} to the list of siege spawns.<br>
	 * This method updates the internal collection for the current map.
	 * @param spawns The {@code SiegeSpawn} object to be added.
	 */
	public void addSiegeSpawns(SiegeSpawn spawns)
	{
		getSiegeSpawns().add(spawns);
	}
	
	/**
	 * Retrieves the list of {@link VortexSpawn} objects for this map.<br>
	 * If no spawns exist, it returns a new empty {@code ArrayList}.
	 * @return A {@code List} of {@code VortexSpawn} objects.
	 */
	public List<VortexSpawn> getVortexSpawns()
	{
		if (vortexSpawns == null)
		{
			vortexSpawns = new ArrayList<>();
		}
		
		return vortexSpawns;
	}
	
	/**
	 * Adds a new {@link VortexSpawn} to the list of vortex spawns.<br>
	 * This method updates the internal collection for the current map.
	 * @param spawns The {@code VortexSpawn} object to add.
	 */
	public void addVortexSpawns(VortexSpawn spawns)
	{
		getVortexSpawns().add(spawns);
	}
	
	/**
	 * Retrieves the list of {@link DynamicPortalSpawn} objects for this map.<br>
	 * If the list is null, it initializes a new {@code ArrayList}.
	 * @return A {@code List} containing all dynamic portal spawns.
	 */
	public List<DynamicPortalSpawn> getDynamicPortalSpawns()
	{
		if (dynamicPortalSpawns == null)
		{
			dynamicPortalSpawns = new ArrayList<>();
		}
		
		return dynamicPortalSpawns;
	}
	
	/**
	 * Adds a new dynamic portal spawn to the current map.<br>
	 * This method updates the internal list of {@link DynamicPortalSpawn} objects.
	 * @param spawns The {@code DynamicPortalSpawn} object to add.
	 */
	public void addDynamicPortalSpawns(DynamicPortalSpawn spawns)
	{
		getDynamicPortalSpawns().add(spawns);
	}
}
