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
package com.aionemu.gameserver.model.templates.spawns;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.TribeClass;

/**
 * Represents the visual and behavioral model for a spawn point.<br>
 * This class defines how an entity appears and behaves when it is spawned in the game world.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpawnModel")
public class SpawnModel
{
	@XmlAttribute(name = "tribe")
	private TribeClass tribe;
	@XmlAttribute(name = "ai")
	private String ai;
	
	/**
	 * Retrieves the {@link TribeClass} of this creature.<br>
	 * This method returns the specific class type assigned to the object.
	 * @return the {@code TribeClass} associated with this entity.
	 */
	public TribeClass getTribe()
	{
		return tribe;
	}
	
	/**
	 * Retrieves the artificial intelligence type for this NPC.<br>
	 * It checks if the NPC is a teleporter at a level greater than 1.<br>
	 * If it is a teleporter, it returns {@code siege_teleporter}.<br>
	 * Otherwise, it returns the default AI value.
	 * @return The AI type as a {@code String}.
	 */
	public String getAi()
	{
		return ai;
	}
}
