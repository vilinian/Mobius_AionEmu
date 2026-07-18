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
package com.aionemu.gameserver.model.templates.chest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the data template for a chest object in the game world.<br>
 * It defines properties such as loot contents and appearance for {@code Chest}.
 * @author Wakizashi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Chest")
public class ChestTemplate
{
	@XmlAttribute(name = "npcid")
	protected int npcId;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlElement(name = "keyitem")
	protected List<KeyItem> keyItem;
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
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
	
	/**
	 * Retrieves the list of items required to open this chest.<br>
	 * This method returns the {@code keyItem} collection associated with the template.
	 * @return a {@code List} of {@link KeyItem} objects.
	 */
	public List<KeyItem> getKeyItem()
	{
		return keyItem;
	}
}
