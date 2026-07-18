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
package com.aionemu.gameserver.model.npcdrops;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents the data model for NPC loot drops defined in XML files.<br>
 * It allows the server to load and manage drop tables for various NPCs.<br>
 * It maps directly to the {@code npc_drop} root element.
 * @author Falke_34
 */
@XmlRootElement(name = "npc_drop")
@XmlAccessorType(XmlAccessType.NONE)
public class XmlNpcDrops
{
	@XmlElement(name = "drop_group")
	protected List<XmlDropGroup> dropGroup;
	@XmlAttribute(name = "npc_id", required = true)
	protected int npcId;
	
	/**
	 * Retrieves the list of drop groups for this NPC.<br>
	 * If no groups exist, it returns an empty {@code List}.
	 * @return a {@code List} of {@link XmlDropGroup} objects.
	 */
	public List<XmlDropGroup> getDropGroup()
	{
		if (dropGroup == null)
		{
			return Collections.emptyList();
		}
		
		return dropGroup;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
	}
}
