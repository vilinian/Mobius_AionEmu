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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.controllers.StaticObjectController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.staticdoor.DoorType;
import com.aionemu.gameserver.model.templates.staticdoor.StaticDoorTemplate;
import com.aionemu.gameserver.model.templates.staticdoor.StaticDoorWorld;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.knownlist.PlayerAwareKnownList;

/**
 * Manages the spawning and lifecycle of {@link StaticDoor} objects within the game world.<br>
 * It handles the initialization of doors based on {@link StaticDoorTemplate} data.<br>
 * This class ensures that static doors are correctly placed and updated in the {@link World}.
 * @author MrPoke
 */
public class StaticDoorSpawnManager
{
	private static Logger log = LoggerFactory.getLogger(StaticDoorSpawnManager.class);
	
	/**
	 * Spawns all valid static doors for a specific world and instance.<br>
	 * This method iterates through the {@code StaticDoorWorld} data.<br>
	 * It creates new {@link StaticDoor} objects and adds them to the game world.
	 * @param worldId The unique identifier of the world.
	 * @param instanceIndex The index of the specific world instance.
	 */
	public static void spawnTemplate(int worldId, int instanceIndex)
	{
		final StaticDoorWorld staticDoorWorld = DataManager.STATICDOOR_DATA.getStaticDoorWorlds(worldId);
		if (staticDoorWorld == null)
		{
			return;
		}
		
		int counter = 0;
		for (StaticDoorTemplate data : staticDoorWorld.getStaticDoors())
		{
			if (data.getDoorType() != DoorType.DOOR)
			{
				// TODO: assign house doors to houses, so geo doors could be triggered by changing house settings;
				// The same for abyss doors, they need to have owners.
				continue;
			}
			
			final SpawnTemplate spawn = new SpawnTemplate(new SpawnGroup2(worldId, 300001), data.getX(), data.getY(), data.getZ(), (byte) 0, 0, null, 0, 0);
			spawn.setStaticId(data.getDoorId());
			final int objectId = IDFactory.getInstance().nextId();
			final StaticDoor staticDoor = new StaticDoor(objectId, new StaticObjectController(), spawn, data, instanceIndex);
			staticDoor.setKnownlist(new PlayerAwareKnownList(staticDoor));
			bringIntoWorld(staticDoor, spawn, instanceIndex);
			if (staticDoor.getDoorName() != null)
			{
				GeoService.getInstance().setDoorState(worldId, instanceIndex, staticDoor.getDoorName(), staticDoor.isOpen());
			}
			
			counter++;
		}
		
		if (counter > 0)
		{
			log.info("Spawned static doors: " + worldId + " [" + instanceIndex + "] : " + counter);
		}
	}
	
	/**
	 * Adds a {@link VisibleObject} to the game world.<br>
	 * This method sets the object position based on a {@link SpawnTemplate}.<br>
	 * It finalizes the process by calling {@code spawn}.
	 * @param visibleObject The object to be added to the world.
	 * @param spawn The template containing coordinates and world data.
	 * @param instanceIndex The specific instance index for the world location.
	 */
	private static void bringIntoWorld(VisibleObject visibleObject, SpawnTemplate spawn, int instanceIndex)
	{
		final World world = World.getInstance();
		world.storeObject(visibleObject);
		world.setPosition(visibleObject, spawn.getWorldId(), instanceIndex, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getHeading());
		world.spawn(visibleObject);
	}
}
