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
package com.aionemu.gameserver.model.shield;

import com.aionemu.gameserver.controllers.ShieldController;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.shield.ShieldTemplate;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.knownlist.SphereKnownList;

/**
 * Represents a shield object within the game world.<br>
 * This class handles the data and behavior for shields used by players.<br>
 * It extends {@link VisibleObject} to provide spatial and visual properties.
 * @author Wakizashi
 */
public class Shield extends VisibleObject
{
	private ShieldTemplate template = null;
	private String name = null;
	private int id = 0;
	
	/**
	 * Creates a new {@link Shield} instance based on a provided template.<br>
	 * This constructor initializes the shield properties and sets up its controller.<br>
	 * It also configures the visibility range using the template radius.
	 * @param template The {@code ShieldTemplate} used to define the shield's attributes.
	 */
	public Shield(ShieldTemplate template)
	{
		super(IDFactory.getInstance().nextId(), new ShieldController(), null, null, null);
		
		((ShieldController) getController()).setOwner(this);
		this.template = template;
		name = (template.getName() == null) ? "SHIELD" : template.getName();
		id = template.getId();
		setKnownlist(new SphereKnownList(this, template.getRadius() * 2));
	}
	
	/**
	 * Retrieves the {@link ShieldTemplate} associated with this shield.<br>
	 * This method returns the base configuration for the object.
	 * @return The {@code ShieldTemplate} of this instance.
	 */
	public ShieldTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return name;
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
	 * Creates the {@code Shield} object in the game world.<br>
	 * This method calls the {@code spawn} method.<br>
	 * It makes the object visible to all players.
	 */
	public void spawn()
	{
		final World w = World.getInstance();
		final WorldPosition position = w.createPosition(template.getMap(), template.getCenter().getX(), template.getCenter().getY(), template.getCenter().getZ(), (byte) 0, 0);
		setPosition(position);
		w.storeObject(this);
		w.spawn(this);
	}
}
