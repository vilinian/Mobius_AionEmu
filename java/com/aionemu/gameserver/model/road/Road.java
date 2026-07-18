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
package com.aionemu.gameserver.model.road;

import com.aionemu.gameserver.controllers.RoadController;
import com.aionemu.gameserver.model.flyring.FlyRing;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.road.RoadTemplate;
import com.aionemu.gameserver.model.utils3d.Plane3D;
import com.aionemu.gameserver.model.utils3d.Point3D;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.SphereKnownList;

/**
 * Represents a road object within the game world.<br>
 * This class extends {@link VisibleObject} to handle spatial data and rendering properties for roads.<br>
 * It is used by the {@link RoadController} to manage road entities in the environment.
 * @author SheppeR
 */
public class Road extends VisibleObject
{
	private RoadTemplate template = null;
	private String name = null;
	private Plane3D plane = null;
	private Point3D center = null;
	private Point3D p1 = null;
	private Point3D p2 = null;
	
	/**
	 * Creates a new {@link Road} instance based on a provided template.<br>
	 * This constructor initializes the spatial coordinates and properties from the {@code RoadTemplate}.<br>
	 * It also sets up the required {@link com.aionemu.gameserver.controllers.RoadController}.
	 * @param template The {@code RoadTemplate} containing the base data for this road.
	 * @param instanceId The unique identifier for this specific road instance.
	 */
	public Road(RoadTemplate template, Integer instanceId)
	{
		super(IDFactory.getInstance().nextId(), new RoadController(), null, null, World.getInstance().createPosition(template.getMap(), template.getCenter().getX(), template.getCenter().getY(), template.getCenter().getZ(), (byte) 0, instanceId));
		
		((RoadController) getController()).setOwner(this);
		this.template = template;
		name = template.getName() == null ? "ROAD" : template.getName();
		center = new Point3D(template.getCenter().getX(), template.getCenter().getY(), template.getCenter().getZ());
		p1 = new Point3D(template.getP1().getX(), template.getP1().getY(), template.getP1().getZ());
		p2 = new Point3D(template.getP2().getX(), template.getP2().getY(), template.getP2().getZ());
		plane = new Plane3D(center, p1, p2);
		setKnownlist(new SphereKnownList(this, template.getRadius() * 2));
	}
	
	/**
	 * Retrieves the 3D plane associated with this {@link FlyRing}.<br>
	 * This is used to determine the orientation of the object.
	 * @return the {@code Plane3D} object representing the plane.
	 */
	public Plane3D getPlane()
	{
		return plane;
	}
	
	/**
	 * Retrieves the {@code RoadTemplate} associated with this road.<br>
	 * This provides access to the base configuration of the object.
	 * @return the {@link RoadTemplate} for this instance.
	 */
	public RoadTemplate getTemplate()
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
	 * Creates the {@code Road} object in the game world.<br>
	 * This method calls the {@code spawn} method.<br>
	 * It makes the object visible to all players.
	 */
	public void spawn()
	{
		final World w = World.getInstance();
		w.storeObject(this);
		w.spawn(this);
	}
}
