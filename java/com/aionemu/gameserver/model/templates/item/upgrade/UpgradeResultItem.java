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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents an item received as a result of an upgrade process.<br>
 * This class holds the data for items granted when a player attempts to upgrade equipment.
 * @author Ranastic
 * @rework Navyan
 */
@XmlRootElement(name = "UpgradeResultItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class UpgradeResultItem
{
	@XmlAttribute(name = "item_id")
	private int item_id;
	@XmlAttribute(name = "name")
	private String name;
	@XmlAttribute(name = "check_enchant_count")
	private int check_enchant_count;
	
	private RequiredMaterials required_materials;
	private NeedAbyssPoint abyss_point_needed;
	private NeedKinah kinah_needed;
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the number of enchantments to check.<br>
	 * This value is used during the upgrade process.
	 * @return the {@code check_enchant_count} value.
	 */
	public int getCheck_enchant_count()
	{
		return check_enchant_count;
	}
	
	/**
	 * Retrieves the unique identifier for this item.<br>
	 * This value is used to identify the specific item in the database.
	 * @return the {@code int} value of the item ID.
	 */
	public int getItem_id()
	{
		return item_id;
	}
	
	/**
	 * Retrieves the materials needed for an upgrade.<br>
	 * This method returns a {@link RequiredMaterials} object.
	 * @return the {@code required_materials} associated with this item.
	 */
	public RequiredMaterials getUpgrade_materials()
	{
		return required_materials;
	}
	
	/**
	 * Retrieves the amount of abyss points required for this upgrade.<br>
	 * This method returns a {@link NeedAbyssPoint} object.
	 * @return the {@code NeedAbyssPoint} value.
	 */
	public NeedAbyssPoint getNeed_abyss_point()
	{
		return abyss_point_needed;
	}
	
	/**
	 * Retrieves the amount of Kinah required for this upgrade.<br>
	 * This method returns a {@link NeedKinah} object.
	 * @return the {@code kinah_needed} value.
	 */
	public NeedKinah getNeed_kinah()
	{
		return kinah_needed;
	}
}
