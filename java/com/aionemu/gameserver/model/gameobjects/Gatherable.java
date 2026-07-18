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

import com.aionemu.gameserver.controllers.GatherableController;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Represents an object in the game world that can be gathered by players.<br>
 * This class handles entities like herbs or ores defined via a {@link GatherableTemplate}.<br>
 * It extends {@link VisibleObject} to provide basic spatial and visual properties.
 * @author ATracer
 */
public class Gatherable extends VisibleObject
{
	/**
	 * Creates a new instance of a {@link Gatherable} object.<br>
	 * This constructor initializes the object with its required templates and controller.<br>
	 * It also sets this instance as the owner of the provided {@code controller}.
	 * @param spawnTemplate The template defining where the object spawns.
	 * @param objectTemplate The visual template for the object.
	 * @param objId The unique identifier for this specific object.
	 * @param controller The controller responsible for managing the object's logic.
	 */
	public Gatherable(SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate, int objId, GatherableController controller)
	{
		super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition(spawnTemplate.getWorldId()));
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
	
	/**
	 * Retrieves the template for this gatherable object.<br>
	 * This method casts the internal {@code VisibleObjectTemplate} to a {@link GatherableTemplate}.
	 * @return The {@code GatherableTemplate} associated with this object.
	 */
	@Override
	public GatherableTemplate getObjectTemplate()
	{
		return (GatherableTemplate) objectTemplate;
	}
	
	/**
	 * Retrieves the controller associated with this {@link Gatherable}.<br>
	 * This method casts the base controller to a {@code GatherableController}.
	 * @return The {@code GatherableController} for this object.
	 */
	@Override
	public GatherableController getController()
	{
		return (GatherableController) super.getController();
	}
}
