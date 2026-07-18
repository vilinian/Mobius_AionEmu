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
package com.aionemu.gameserver.model.town;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.Spawn;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TOWNS_LIST;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * Represents a town entity within the game world.<br>
 * This class manages town-specific data such as its name, coordinates, and associated {@link Spawn} points.
 * @author ViAl
 */
public class Town
{
	private final int id;
	private int level;
	private int points;
	private final Timestamp levelUpDate;
	private final Race race;
	private PersistentState persistentState;
	private final List<Npc> spawnedNpcs;
	
	/**
	 * Creates a new {@link Town} instance using the provided data.<br>
	 * This constructor initializes all required fields and triggers the initial object spawning.
	 * @param id The unique identifier for the town.
	 * @param level The current level of the town.
	 * @param points The amount of points assigned to the town.
	 * @param race The {@link Race} type associated with this town.
	 * @param levelUpDate The timestamp of the last level update.
	 */
	public Town(int id, int level, int points, Race race, Timestamp levelUpDate)
	{
		this.id = id;
		this.level = level;
		this.points = points;
		this.levelUpDate = levelUpDate;
		this.race = race;
		persistentState = PersistentState.UPDATED;
		spawnedNpcs = new ArrayList<>();
		spawnNewObjects();
	}
	
	/**
	 * Creates a new {@link Town} instance with default values.<br>
	 * This constructor sets the initial level to {@code 1}.<br>
	 * It also initializes the points to {@code 0}.<br>
	 * The persistent state is set to {@code PersistentState.NEW}.
	 * @param id The unique identifier for the town.
	 * @param race The {@link Race} type associated with this town.
	 */
	public Town(int id, Race race)
	{
		this(id, 1, 0, race, new Timestamp(60000));
		persistentState = PersistentState.NEW;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the current number of points for this rank.<br>
	 * This value is used to determine the player's standing in the arena.
	 * @return The current {@code int} value of points.
	 */
	public int getPoints()
	{
		return points;
	}
	
	/**
	 * Adds a specific number of points to the town.<br>
	 * This method checks if the new total reaches the threshold for a level up.<br>
	 * It then updates the internal state and marks it as requiring an update.
	 * @param amount The number of points to add to the current total.
	 */
	public synchronized void increasePoints(int amount)
	{
		switch (level)
		{
			case 1:
				if ((points + amount) >= 1000)
				{
					increaseLevel();
				}
				break;
			case 2:
				if ((points + amount) >= 2000)
				{
					increaseLevel();
				}
				break;
			case 3:
				if ((points + amount) >= 3000)
				{
					increaseLevel();
				}
				break;
			case 4:
				if ((points + amount) >= 4000)
				{
					increaseLevel();
				}
				break;
		}
		
		points += amount;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	/**
	 * Increases the current level of the town.<br>
	 * Updates the {@code levelUpDate} to the current system time.<br>
	 * Triggers a broadcast and refreshes the spawned objects.
	 */
	private void increaseLevel()
	{
		level++;
		levelUpDate.setTime(System.currentTimeMillis());
		broadcastUpdate();
		despawnOldObjects();
		spawnNewObjects();
	}
	
	/**
	 * Sends the updated town information to all relevant players.<br>
	 * It filters players based on their {@code race}.<br>
	 * The update is sent using the {@link SM_TOWNS_LIST} packet.
	 */
	private void broadcastUpdate()
	{
		final Map<Integer, Town> data = new HashMap<>(1);
		data.put(id, this);
		final SM_TOWNS_LIST packet = new SM_TOWNS_LIST(data);
		World.getInstance().doOnAllPlayers(player ->
		{
			if (player.getRace() == race)
			{
				PacketSendUtility.sendPacket(player, packet);
			}
		});
	}
	
	/**
	 * Creates new objects for the town based on its current level.<br>
	 * This method retrieves spawn data from {@link DataManager}.<br>
	 * It uses {@link SpawnEngine} to add and spawn new entities into the world.
	 */
	private void spawnNewObjects()
	{
		final List<Spawn> newSpawns = DataManager.TOWN_SPAWNS_DATA.getSpawns(id, level);
		final int worldId = DataManager.TOWN_SPAWNS_DATA.getWorldIdForTown(id);
		for (Spawn spawn : newSpawns)
		{
			for (SpawnSpotTemplate sst : spawn.getSpawnSpotTemplates())
			{
				final SpawnTemplate spawnTemplate = SpawnEngine.addNewSpawn(worldId, spawn.getNpcId(), sst.getX(), sst.getY(), sst.getZ(), sst.getHeading(), spawn.getRespawnTime());
				spawnTemplate.setStaticId(sst.getStaticId());
				spawnTemplate.setRandomWalk(0);
				final VisibleObject object = SpawnEngine.spawnObject(spawnTemplate, 1);
				if (object instanceof Npc)
				{
					((Npc) object).setTownId(id);
					spawnedNpcs.add((Npc) object);
				}
			}
		}
	}
	
	/**
	 * Removes all currently active NPCs from the town.<br>
	 * This method calls {@code delete()} on each NPC controller.<br>
	 * It then clears the {@code spawnedNpcs} list to reset the state.
	 */
	private void despawnOldObjects()
	{
		for (Npc npc : spawnedNpcs)
		{
			npc.getController().delete();
		}
		
		spawnedNpcs.clear();
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the date and time when the town last leveled up.<br>
	 * This value is stored as a {@code Timestamp}.
	 * @return The {@code Timestamp} of the last level up.
	 */
	public Timestamp getLevelUpDate()
	{
		return levelUpDate;
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Updates the current {@code persistentState} of this town.<br>
	 * This method prevents changing from {@code PersistentState.NEW} to {@code PersistentState.UPDATE_REQUIRED}.
	 * @param state The new {@link PersistentState} to apply.
	 */
	public void setPersistentState(PersistentState state)
	{
		if ((persistentState == PersistentState.NEW) && (state == PersistentState.UPDATE_REQUIRED))
		{
			return;
		}
		
		persistentState = state;
	}
}
