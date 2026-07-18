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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.StaticObjectController;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Represents a non-moving object within the game world.<br>
 * This class handles static entities that are visible to players but do not possess independent movement logic.<br>
 * It extends {@link VisibleObject} to provide basic spatial and visual properties.
 * @author ATracer
 */
public class StaticObject extends VisibleObject
{
	/**
	 * Creates a new instance of a {@link StaticObject}.<br>
	 * This constructor initializes the object with its required templates and controller.<br>
	 * It also sets this object as the owner of the provided {@code controller}.
	 * @param objectId The unique identifier for the object.
	 * @param controller The {@code StaticObjectController} that manages this object.
	 * @param spawnTemplate The template containing spawn data like world ID.
	 * @param objectTemplate The template defining the visual properties of the object.
	 */
	public StaticObject(int objectId, StaticObjectController controller, SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate)
	{
		super(objectId, controller, spawnTemplate, objectTemplate, new WorldPosition(spawnTemplate.getWorldId()));
		controller.setOwner(this);
	}
	
	/**
	 * Retrieves the name of this gatherable object.<br>
	 * This method returns the {@code String} name from the associated {@link GatherableTemplate}.
	 * @return The name of the object as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return objectTemplate.getName();
	}
}
