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
package com.aionemu.gameserver.model.flyring;

import com.aionemu.gameserver.controllers.FlyRingController;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;
import com.aionemu.gameserver.model.utils3d.Plane3D;
import com.aionemu.gameserver.model.utils3d.Point3D;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.SphereKnownList;

/**
 * Represents a fly ring object within the game world.<br>
 * This class handles the data and behavior for rings used by players to fly.<br>
 * It extends {@link VisibleObject} to manage its presence in the 3D environment.
 * @author xavier
 */
public class FlyRing extends VisibleObject
{
	private FlyRingTemplate template = null;
	private String name = null;
	private Plane3D plane = null;
	private Point3D center = null;
	private Point3D left = null;
	private Point3D right = null;
	
	/**
	 * Creates a new {@link FlyRing} instance.<br>
	 * This constructor initializes the object using a provided {@code template}.<br>
	 * It sets up the spatial coordinates and the controller for the ring.
	 * @param template The {@code FlyRingTemplate} containing the data for this ring.
	 * @param instanceId The unique identifier for this specific instance.
	 */
	public FlyRing(FlyRingTemplate template, int instanceId)
	{
		super(IDFactory.getInstance().nextId(), new FlyRingController(), null, null, World.getInstance().createPosition(template.getMap(), template.getCenter().getX(), template.getCenter().getY(), template.getCenter().getZ(), (byte) 0, instanceId));
		
		((FlyRingController) getController()).setOwner(this);
		this.template = template;
		name = (template.getName() == null) ? "FLY_RING" : template.getName();
		center = new Point3D(template.getCenter().getX(), template.getCenter().getY(), template.getCenter().getZ());
		left = new Point3D(template.getP1().getX(), template.getP1().getY(), template.getP1().getZ());
		right = new Point3D(template.getP2().getX(), template.getP2().getY(), template.getP2().getZ());
		plane = new Plane3D(center, left, right);
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
	 * Retrieves the {@code FlyRingTemplate} associated with this object.<br>
	 * This provides access to the base configuration data for the fly ring.
	 * @return the {@link FlyRingTemplate} instance.
	 */
	public FlyRingTemplate getTemplate()
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
	 * Creates the {@code FlyRing} object in the game world.<br>
	 * This method calls the {@code spawn} method.<br>
	 * It makes the object visible to all players.
	 */
	public void spawn()
	{
		World.getInstance().spawn(this);
	}
}
