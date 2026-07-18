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
package com.aionemu.gameserver.world.knownlist;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * Manages a list of known objects within a specific spherical area.<br>
 * It helps the server track which {@link VisibleObject} instances are visible to a player based on distance.<br>
 * This class extends {@link PlayerAwareKnownList} to provide specialized spatial awareness.
 * @author ATracer
 */
public class SphereKnownList extends PlayerAwareKnownList
{
	private final float radius;
	
	/**
	 * Creates a new {@link SphereKnownList} for a specific object.<br>
	 * This list tracks objects within a circular area defined by a radius.
	 * @param owner The {@code VisibleObject} that owns this known list.
	 * @param radius The size of the detection sphere.
	 */
	public SphereKnownList(VisibleObject owner, float radius)
	{
		super(owner);
		this.radius = radius;
	}
	
	/**
	 * Checks if a {@link VisibleObject} is within the allowed range.<br>
	 * It uses the owner's position and the defined radius to calculate the distance.
	 * @param newObject The {@code VisibleObject} to check.
	 * @return {@code true} if the object is in range, {@code false} otherwise.
	 */
	@Override
	protected boolean checkReversedObjectInRange(VisibleObject newObject)
	{
		return MathUtil.isIn3dRange(owner, newObject, radius);
	}
}
