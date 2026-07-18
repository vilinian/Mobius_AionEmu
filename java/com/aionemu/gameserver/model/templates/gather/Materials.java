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
package com.aionemu.gameserver.model.templates.gather;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the data structure for gathering materials.<br>
 * It defines the properties and templates used when players collect resources in the game world.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Materials", propOrder =
{
	"material"
})
public class Materials
{
	protected List<Material> material;
	
	/**
	 * Retrieves the list of {@link Material} objects.<br>
	 * If the internal list is {@code null}, it creates a new {@code ArrayList}.
	 * @return A {@code List} containing all materials.
	 */
	public List<Material> getMaterial()
	{
		if (material == null)
		{
			material = new ArrayList<>();
		}
		
		return material;
	}
}
