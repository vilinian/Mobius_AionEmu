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
package com.aionemu.gameserver.model.ai;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the artificial intelligence behavior for game entities.<br>
 * This class defines how NPCs and other non-player characters act within the world.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ai")
public class Ai
{
	@XmlElement(name = "summons")
	private Summons summons;
	@XmlElement(name = "bombs")
	private Bombs bombs;
	@XmlAttribute(name = "npcId")
	private int npcId;
	
	/**
	 * Retrieves the {@link Summons} object associated with this AI.<br>
	 * This method returns the current summon data.
	 * @return the {@code Summons} object or {@code null} if no summons exist.
	 */
	public Summons getSummons()
	{
		return summons;
	}
	
	/**
	 * Retrieves the {@code Bombs} object associated with this AI.<br>
	 * This method returns the current bomb data for the NPC.
	 * @return The {@link Bombs} instance.
	 */
	public Bombs getBombs()
	{
		return bombs;
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
