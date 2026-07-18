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

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.ai.Ai;
import com.aionemu.gameserver.model.stats.calc.StatOwner;

/**
 * Represents the configuration data for item upgrades in the game.<br>
 * This class defines how items change when upgraded and provides access to their associated stats via {@link StatOwner}.
 * @author Ranastic
 * @rework Navyan
 */
@XmlRootElement(name = "ItemUpgrade")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemUpgradeTemplate implements StatOwner
{
	protected List<UpgradeResultItem> upgrade_result_item;
	@XmlAttribute(name = "base_item")
	private int upgrade_base_item_id;
	@XmlAttribute(name = "name")
	private String name;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code aiTemplate} map using the list of {@link Ai} templates.<br>
	 * The {@code aiTemplate} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
	}
	
	/**
	 * Retrieves the list of items resulting from an upgrade.<br>
	 * This method returns the {@code upgrade_result_item} collection.
	 * @return a {@code List} of {@link UpgradeResultItem} objects.
	 */
	public List<UpgradeResultItem> getUpgrade_result_item()
	{
		return upgrade_result_item;
	}
	
	/**
	 * Retrieves the unique identifier for the base item.<br>
	 * This ID identifies which item is being upgraded.
	 * @return The {@code int} value of the base item ID.
	 */
	public int getUpgrade_base_item_id()
	{
		return upgrade_base_item_id;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
}
