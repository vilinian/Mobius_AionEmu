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
package com.aionemu.gameserver.model.templates.npcshout;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * <p/>
 * Java class for NpcShout complex type.
 * <p/>
 * The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * 
 * <pre>
 * &lt;complexType name="NpcShout">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="string_id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="when" use="required" type="{}ShoutEventType" />
 *       &lt;attribute name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="param" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{}ShoutType" default="BROADCAST" />
 *       &lt;attribute name="skill_no" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *       &lt;attribute name="poll_delay" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NpcShout")
public class NpcShout
{
	@XmlAttribute(name = "string_id", required = true)
	protected int stringId;
	@XmlAttribute(name = "when", required = true)
	protected ShoutEventType when;
	@XmlAttribute(name = "pattern")
	protected String pattern;
	@XmlAttribute(name = "param")
	protected String param;
	@XmlAttribute(name = "type")
	protected ShoutType type;
	@XmlAttribute(name = "skill_no")
	protected Integer skillNo;
	@XmlAttribute(name = "poll_delay")
	protected Integer pollDelay;
	
	/**
	 * Retrieves the unique identifier for the shout string.<br>
	 * This value corresponds to the {@code string_id} attribute.
	 * @return the {@code int} value of the string ID.
	 */
	public int getStringId()
	{
		return stringId;
	}
	
	/**
	 * Retrieves the event type that triggers this shout.<br>
	 * This value is defined by the {@code when} attribute.
	 * @return the {@code ShoutEventType} of the shout.
	 */
	public ShoutEventType getWhen()
	{
		return when;
	}
	
	/**
	 * Retrieves the shout pattern associated with this {@link NpcShout}.<br>
	 * This value is used to format the final message.
	 * @return The {@code String} representing the pattern.
	 */
	public String getPattern()
	{
		return pattern;
	}
	
	/**
	 * Retrieves the parameter associated with this {@link NpcShout}.<br>
	 * This value is used to fill patterns in shout messages.
	 * @return The {@code String} value of the parameter.
	 */
	public String getParam()
	{
		return param;
	}
	
	/**
	 * Retrieves the shout type for this NPC shout.<br>
	 * If the internal type is {@code null}, it returns {@code BROADCAST}.
	 * @return The {@code ShoutType} of the shout.
	 */
	public ShoutType getShoutType()
	{
		if (type == null)
		{
			return ShoutType.BROADCAST;
		}
		
		return type;
	}
	
	/**
	 * Retrieves the skill number associated with this shout.<br>
	 * Returns {@code 0} if no skill is assigned.
	 * @return The integer value of the skill number.
	 */
	public int getSkillNo()
	{
		if (skillNo == null)
		{
			return 0;
		}
		
		return skillNo;
	}
	
	/**
	 * Retrieves the delay time for polling this shout.<br>
	 * This value determines how often the system checks the shout conditions.
	 * @return The poll delay as an {@code int}. Returns {@code 0} if no delay is set.
	 */
	public int getPollDelay()
	{
		if (pollDelay == null)
		{
			return 0;
		}
		
		return pollDelay;
	}
	
	/**
	 * Retrieves the minimum shout range for a specific {@link Npc}.<br>
	 * This value is taken from the NPC's object template.
	 * @param npc The {@code Npc} object to check.
	 * @return The minimum shout range as an {@code int}.
	 */
	public int getShoutRange(Npc npc)
	{
		return npc.getObjectTemplate().getMinimumShoutRange();
	}
}
