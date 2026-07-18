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
package com.aionemu.gameserver.skillengine.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a specific movement or animation data for a skill.<br>
 * This model is used by the {@link com.aionemu.gameserver.skillengine.SkillEngine} to handle character motions.
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Motion")
public class Motion
{
	@XmlAttribute(required = true)
	protected String name; // TODO enum
	@XmlAttribute
	protected int speed = 100;
	@XmlAttribute(name = "instant_skill")
	protected boolean instantSkill = false;
	
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
	 * Retrieves the current movement speed.<br>
	 * This value is used to determine how fast a motion occurs.
	 * @return The {@code int} value of the speed.
	 */
	public int getSpeed()
	{
		return speed;
	}
	
	/**
	 * Checks if the motion is an instant skill.<br>
	 * This method returns the value of the {@code instantSkill} attribute.
	 * @return {@code true} if it is an instant skill, {@code false} otherwise.
	 */
	public boolean getInstantSkill()
	{
		return instantSkill;
	}
}
