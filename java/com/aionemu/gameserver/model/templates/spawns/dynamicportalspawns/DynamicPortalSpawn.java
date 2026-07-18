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
package com.aionemu.gameserver.model.templates.spawns.dynamicportalspawns;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.dynamicportal.DynamicPortalStateType;
import com.aionemu.gameserver.model.templates.spawns.Spawn;

/**
 * Represents a spawn point for dynamic portals within the game world.<br>
 * This class defines how and where {@code DynamicPortal} instances appear.<br>
 * It extends the functionality of the base {@link com.aionemu.gameserver.model.templates.spawns.Spawn} template.
 * @author Falke_34
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DynamicPortalSpawn")
public class DynamicPortalSpawn
{
	@XmlAttribute(name = "id")
	private int id;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	@XmlElement(name = "dynamic_portal_type")
	private List<DynamicPortalSpawn.DynamicPortalStateTemplate> DynamicPortalStateTemplate;
	
	/**
	 * Retrieves the list of templates for siege mode states.<br>
	 * This method provides data used to configure dynamic portals during siege events.
	 * @return A {@code List} of {@code DynamicPortalStateTemplate} objects.
	 */
	public List<DynamicPortalStateTemplate> getSiegeModTemplates()
	{
		return DynamicPortalStateTemplate;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "DynamicPortalStateTemplate")
	public static class DynamicPortalStateTemplate
	{
		@XmlElement(name = "spawn")
		private List<Spawn> spawns;
		
		@XmlAttribute(name = "dstate")
		private DynamicPortalStateType dynamicPortalType;
		
		public List<Spawn> getSpawns()
		{
			return spawns;
		}
		
		public DynamicPortalStateType getDynamicPortalType()
		{
			return dynamicPortalType;
		}
	}
}
