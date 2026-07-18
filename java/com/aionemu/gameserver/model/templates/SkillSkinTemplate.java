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
package com.aionemu.gameserver.model.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the configuration data for a skill skin.<br>
 * This template defines how specific visual effects are applied to skills in the game.<br>
 * It is used by the server to manage and load {@code skill_skin} assets.
 * @author Ghostfur (Aion-Unique)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "skill_skin")
public class SkillSkinTemplate
{
	@XmlAttribute(name = "id", required = true)
	private int id;
	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlAttribute(name = "skill_group", required = true)
	private String skillgroup;
	@XmlAttribute(name = "motion_name", required = true)
	private String motionName;
	@XmlAttribute(name = "ammo_speed", required = true)
	private int ammoSpeed;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
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
	 * Retrieves the group name for this skill skin.<br>
	 * This value is used to categorize different skills.
	 * @return The {@code String} representing the skill group.
	 */
	public String getSkillGroup()
	{
		return skillgroup;
	}
	
	/**
	 * Retrieves the name of the motion associated with this skill skin.<br>
	 * This value is used to identify the specific animation to play.
	 * @return The {@code String} representing the motion name.
	 */
	public String getMotionName()
	{
		return motionName;
	}
	
	/**
	 * Retrieves the speed of the ammunition for this skill skin.<br>
	 * This value is used to determine how fast projectiles move.
	 * @return The {@code int} value representing the ammo speed.
	 */
	public int getAmmoSpeed()
	{
		return ammoSpeed;
	}
}
