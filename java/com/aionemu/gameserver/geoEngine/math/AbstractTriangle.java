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
package com.aionemu.gameserver.geoEngine.math;

import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;

/**
 * Provides a base implementation for triangle-based geometry in the game engine.<br>
 * This class serves as an abstract foundation for objects that implement {@link Collidable}.<br>
 * It is used to perform spatial calculations and collision detection within the {@code geoEngine}.
 */
public abstract class AbstractTriangle implements Collidable
{
	public abstract Vector3f get1();
	
	public abstract Vector3f get2();
	
	public abstract Vector3f get3();
	
	public abstract void set(Vector3f v1, Vector3f v2, Vector3f v3);
	
	/**
	 * Checks if this object collides with another object.<br>
	 * This method delegates the collision check to the {@code other} object.<br>
	 * It updates the provided {@code results} object if a collision occurs.
	 * @param other The {@link Collidable} object to check against.
	 * @param results The {@link CollisionResults} container to store any detected hits.
	 * @return Returns 1 if a collision occurred, or 0 if no collision was found.
	 */
	@Override
	public int collideWith(Collidable other, CollisionResults results)
	{
		return other.collideWith(this, results);
	}
}
