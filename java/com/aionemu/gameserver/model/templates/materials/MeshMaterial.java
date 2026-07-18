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
package com.aionemu.gameserver.model.templates.materials;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the material properties for a 3D mesh object.<br>
 * This class stores data used to define how surfaces appear in the game world.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeshMaterial")
public class MeshMaterial
{
	@XmlAttribute(name = "material_id", required = true)
	protected int materialId;
	@XmlAttribute(name = "path", required = true)
	protected String path;
	@XmlAttribute(name = "zone")
	private String zoneName;
	
	/**
	 * Retrieves the name of the zone associated with this material.<br>
	 * This value is stored in the {@code zoneName} field.
	 * @return The name of the zone as a {@code String}.
	 */
	public String getZoneName()
	{
		return zoneName;
	}
}
