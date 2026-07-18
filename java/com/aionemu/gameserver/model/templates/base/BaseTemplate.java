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
package com.aionemu.gameserver.model.templates.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class serves as the base model for all game templates.<br>
 * It provides common properties and data structures shared across different template types.<br>
 * Use this class to define core attributes required by {@link com.aionemu.gameserver.model.templates.base.BaseTemplate}.
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Base")
public class BaseTemplate
{
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "world")
	protected int world;
	
	@XmlAttribute(name = "name")
	protected String nameId;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return world;
	}
	
	/**
	 * Retrieves the name of the template.<br>
	 * This method returns the {@code String} value stored in the {@code nameId} field.
	 * @return The name of the template as a {@code String}.
	 */
	public String getName()
	{
		return nameId;
	}
}
