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
package com.aionemu.gameserver.questEngine.handlers.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a monster that has been spawned as part of a quest.<br>
 * This model extends {@link Monster} to provide specific data for quest-related entities.
 * @author vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpawnedMonster")
public class SpawnedMonster extends Monster
{
	@XmlAttribute(name = "spawner_object", required = true)
	protected int spawnerObject;
	
	/**
	 * Retrieves the unique identifier for the object that spawned this monster.<br>
	 * This value is stored in the {@code spawnerObject} field.
	 * @return The {@code int} ID of the spawner object.
	 */
	public int getSpawnerObject()
	{
		return spawnerObject;
	}
}
