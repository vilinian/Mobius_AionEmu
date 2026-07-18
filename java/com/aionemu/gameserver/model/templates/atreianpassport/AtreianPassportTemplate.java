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
package com.aionemu.gameserver.model.templates.atreianpassport;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class serves as a data template for the {@code AtreianPassport} object.<br>
 * It defines the structure and properties required to initialize passport data within the game server.
 * @author Falke_34
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "atreian_passport")
public class AtreianPassportTemplate
{
	@XmlElement(name = "rewards")
	protected List<AtreianPassportRewards> rewards;
	
	@XmlAttribute(name = "id", required = true)
	protected int id;
	
	@XmlAttribute(name = "name")
	protected String name;
	
	@XmlAttribute(name = "active")
	protected int active;
	
	@XmlAttribute(name = "attend_type", required = true)
	private AttendType attendType;
	
	/**
	 * Retrieves the list of rewards for this passport.<br>
	 * If no rewards exist, it returns an empty {@code List}.
	 * @return a {@code List} of {@link AtreianPassportRewards} objects.
	 */
	public List<AtreianPassportRewards> getRewards()
	{
		if (rewards == null)
		{
			rewards = new ArrayList<>();
		}
		
		return rewards;
	}
	
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
	 * Retrieves the current activity status of the passport.<br>
	 * This value is stored in the {@code active} attribute.
	 * @return The integer value representing whether the passport is active.
	 */
	public int getActive()
	{
		return active;
	}
	
	/**
	 * Retrieves the attendance type for this passport.<br>
	 * This value determines how the attendance is categorized.
	 * @return the {@code AttendType} associated with this template.
	 */
	public AttendType getAttendType()
	{
		return attendType;
	}
}
