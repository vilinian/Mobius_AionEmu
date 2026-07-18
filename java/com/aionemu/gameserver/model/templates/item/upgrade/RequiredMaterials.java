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
package com.aionemu.gameserver.model.templates.item.upgrade;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents the materials required for an item upgrade.<br>
 * It maps to the {@code <RequiredMaterials>} XML element in the configuration files.<br>
 * Use this model to define which items and quantities are needed for specific upgrades.
 * @author Ranastic
 * @rework Navyan
 */
@XmlRootElement(name = "RequiredMaterials")
@XmlAccessorType(XmlAccessType.FIELD)
public class RequiredMaterials
{
	@XmlElement(required = true)
	protected List<SubMaterialItem> sub_material_item;
	
	/**
	 * Retrieves the list of required sub-materials.<br>
	 * This method returns the {@code sub_material_item} field from this object.
	 * @return a {@code List} of {@link SubMaterialItem} objects.
	 */
	public List<SubMaterialItem> getSubMaterialItem()
	{
		return sub_material_item;
	}
}
