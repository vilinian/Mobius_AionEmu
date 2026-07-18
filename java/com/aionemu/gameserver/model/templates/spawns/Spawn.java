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

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.event.EventTemplate;
import com.aionemu.gameserver.spawnengine.SpawnHandlerType;

/**
 * Represents a template for an entity spawn point within the game world.<br>
 * It defines the configuration and properties required by the {@link com.aionemu.gameserver.spawnengine.SpawnHandlerType} to manage monster or NPC appearances.
 * @author xTz
 * @modified Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Spawn")
public class Spawn
{
	@XmlAttribute(name = "npc_id", required = true)
	private int npcId;
	@XmlAttribute(name = "respawn_time")
	private Integer respawnTime = 0;
	@XmlAttribute(name = "pool")
	private Integer pool = 0;
	@XmlAttribute(name = "difficult_id")
	private byte difficultId;
	@XmlAttribute(name = "custom")
	private Boolean isCustom = false;
	@XmlAttribute(name = "handler")
	private SpawnHandlerType handler;
	@XmlElement(name = "temporary_spawn")
	private TemporarySpawn temporaySpawn;
	@XmlElement(name = "spot")
	private List<SpawnSpotTemplate> spawnTemplates;
	@XmlTransient
	private EventTemplate eventTemplate;
	
	/**
	 * Creates a new instance of the {@link Spawn} class.<br>
	 * This constructor initializes a default spawn object with no specific values.
	 */
	public Spawn()
	{
	}
	
	/**
	 * Creates a new {@link Spawn} object.<br>
	 * This constructor initializes the NPC ID, respawn time, and handler type.
	 * @param npcId The unique identifier for the NPC.
	 * @param respawnTime The amount of time before the NPC reappears.
	 * @param handler The {@link SpawnHandlerType} used to manage this spawn.
	 */
	public Spawn(int npcId, int respawnTime, SpawnHandlerType handler)
	{
		this.npcId = npcId;
		this.respawnTime = respawnTime;
		this.handler = handler;
	}
	
	/**
	 * Prepares the object data before it is converted to XML.<br>
	 * This method sets certain fields to {@code null} if they contain default values.<br>
	 * It ensures that only relevant data is included in the final output.
	 * @param marshaller The {@link Marshaller} used to convert the object to XML.
	 */
	void beforeMarshal(Marshaller marshaller)
	{
		if (pool == 0)
		{
			pool = null;
		}
		
		if (!isCustom)
		{
			isCustom = null;
		}
	}
	
	/**
	 * This method is called after the object is marshaled to XML.<br>
	 * It ensures that {@code isCustom} and {@code pool} have default values if they are {@code null}.
	 * @param marshaller The {@link Marshaller} used for the operation.
	 */
	void afterMarshal(Marshaller marshaller)
	{
		if (isCustom == null)
		{
			isCustom = false;
		}
		
		if (pool == null)
		{
			pool = 0;
		}
	}
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
	}
	
	/**
	 * Retrieves the current value of the spawn pool.<br>
	 * This value is used to determine the size or quantity of the spawn group.
	 * @return The integer value of the {@code pool}.
	 */
	public int getPool()
	{
		return pool;
	}
	
	/**
	 * Retrieves the temporary spawn data for this {@link Spawn}.<br>
	 * This may return {@code null} if no temporary spawn is defined.
	 * @return The {@code TemporarySpawn} object associated with this spawn.
	 */
	public TemporarySpawn getTemporarySpawn()
	{
		return temporaySpawn;
	}
	
	/**
	 * Retrieves the time it takes for an NPC to reappear.<br>
	 * This value is used by the spawn engine to manage respawns.
	 * @return The respawn time as an {@code int}.
	 */
	public int getRespawnTime()
	{
		return respawnTime;
	}
	
	/**
	 * Retrieves the type of spawn handler assigned to this spawn.<br>
	 * This determines how the server manages the spawning logic.
	 * @return the {@code SpawnHandlerType} associated with this spawn.
	 */
	public SpawnHandlerType getSpawnHandlerType()
	{
		return handler;
	}
	
	/**
	 * Retrieves the list of spawn spot templates for this {@link Spawn}.<br>
	 * If no templates exist, it returns an empty {@code ArrayList}.
	 * @return a {@code List} of {@code SpawnSpotTemplate} objects.
	 */
	public List<SpawnSpotTemplate> getSpawnSpotTemplates()
	{
		if (spawnTemplates == null)
		{
			spawnTemplates = new ArrayList<>();
		}
		
		return spawnTemplates;
	}
	
	/**
	 * Adds a new spawn spot to the current {@link Spawn} object.<br>
	 * This method updates the list of {@code SpawnSpotTemplate} objects.
	 * @param template The {@code SpawnSpotTemplate} to be added.
	 */
	public void addSpawnSpot(SpawnSpotTemplate template)
	{
		getSpawnSpotTemplates().add(template);
	}
	
	/**
	 * Checks if this spawn is marked as a custom type.<br>
	 * It returns {@code false} if the value is {@code null}.
	 * @return {@code true} if it is custom, otherwise {@code false}.
	 */
	public boolean isCustom()
	{
		return isCustom == null ? false : isCustom;
	}
	
	/**
	 * Sets whether this spawn is marked as a custom entry.<br>
	 * This updates the {@code isCustom} field.
	 * @param isCustom The value to set for the custom flag.
	 */
	public void setCustom(boolean isCustom)
	{
		this.isCustom = isCustom;
	}
	
	/**
	 * Checks if this spawn is associated with an event.<br>
	 * It returns {@code true} if the {@link EventTemplate} is not {@code null}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if it is an event spawn, {@code false} otherwise.
	 */
	public boolean isEventSpawn()
	{
		return eventTemplate != null;
	}
	
	/**
	 * Retrieves the {@code EventTemplate} associated with this spawn.<br>
	 * This template defines specific event behaviors for the spawn.
	 * @return the {@link EventTemplate} object or {@code null} if no template is set.
	 */
	public EventTemplate getEventTemplate()
	{
		return eventTemplate;
	}
	
	/**
	 * Sets the {@code EventTemplate} for this spawn.<br>
	 * This defines the specific event data associated with the spawn object.
	 * @param eventTemplate The {@link EventTemplate} to assign.
	 */
	public void setEventTemplate(EventTemplate eventTemplate)
	{
		this.eventTemplate = eventTemplate;
	}
	
	/**
	 * Retrieves the unique identifier for the difficulty level.<br>
	 * This value is used to distinguish between different difficulty settings.
	 * @return the {@code byte} value representing the difficulty ID.
	 */
	public byte getDifficultId()
	{
		return difficultId;
	}
}
