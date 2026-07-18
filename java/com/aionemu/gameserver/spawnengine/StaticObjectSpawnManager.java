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

import com.aionemu.gameserver.controllers.StaticObjectController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.PlayerAwareKnownList;

/**
 * Manages the spawning and lifecycle of {@link StaticObject} entities within the game world.<br>
 * It handles the distribution of objects based on {@link SpawnTemplate} configurations.<br>
 * This class ensures that static decorations are correctly placed and updated in the {@link World}.
 * @author ATracer
 */
public class StaticObjectSpawnManager
{
	/**
	 * Spawns objects based on a {@link SpawnGroup2} configuration.<br>
	 * This method handles both pooled and non-pooled spawn templates.<br>
	 * It creates new {@code StaticObject} instances for the specified world instance.
	 * @param spawn The group of spawn templates to use.
	 * @param instanceIndex The index of the world instance where objects will be spawned.
	 */
	public static void spawnTemplate(SpawnGroup2 spawn, int instanceIndex)
	{
		final VisibleObjectTemplate objectTemplate = DataManager.ITEM_DATA.getItemTemplate(spawn.getNpcId());
		if (objectTemplate == null)
		{
			return;
		}
		
		if (spawn.hasPool())
		{
			spawn.resetTemplates(instanceIndex);
			for (int i = 0; i < spawn.getPool(); i++)
			{
				final SpawnTemplate template = spawn.getRndTemplate(instanceIndex);
				final int objectId = IDFactory.getInstance().nextId();
				final StaticObject staticObject = new StaticObject(objectId, new StaticObjectController(), template, objectTemplate);
				staticObject.setKnownlist(new PlayerAwareKnownList(staticObject));
				bringIntoWorld(staticObject, template, instanceIndex);
			}
		}
		else
		{
			for (SpawnTemplate template : spawn.getSpawnTemplates())
			{
				final int objectId = IDFactory.getInstance().nextId();
				final StaticObject staticObject = new StaticObject(objectId, new StaticObjectController(), template, objectTemplate);
				staticObject.setKnownlist(new PlayerAwareKnownList(staticObject));
				bringIntoWorld(staticObject, template, instanceIndex);
			}
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
