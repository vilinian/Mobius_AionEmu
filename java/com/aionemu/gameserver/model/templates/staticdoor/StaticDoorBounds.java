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
package com.aionemu.gameserver.model.templates.staticdoor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.geoEngine.bounding.BoundingBox;
import com.aionemu.gameserver.geoEngine.math.Vector3f;

/**
 * This class defines the physical boundaries for a {@link com.aionemu.gameserver.geoEngine.bounding.BoundingBox} of a static door.<br>
 * It provides spatial data used by the game engine to handle collisions and interactions with doors in the world.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StaticDoorBounds")
public class StaticDoorBounds
{
	@XmlAttribute
	private float x1;
	@XmlAttribute
	private float y1;
	@XmlAttribute
	private float z1;
	@XmlAttribute
	private float x2;
	@XmlAttribute
	private float y2;
	@XmlAttribute
	private float z2;
	@XmlTransient
	private BoundingBox boundingBox;
	
	/**
	 * Retrieves the {@link BoundingBox} for this door.<br>
	 * It creates a new box using coordinates if one does not exist.
	 * @return the current {@code BoundingBox} object.
	 */
	public BoundingBox getBoundingBox()
	{
		if (boundingBox == null)
		{
			boundingBox = new BoundingBox(new Vector3f(x1, y1, z1), new Vector3f(x2, y2, z2));
		}
		
		return boundingBox;
	}
}
