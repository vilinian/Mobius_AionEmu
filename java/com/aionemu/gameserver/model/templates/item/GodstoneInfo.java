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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the data model for a {@code Godstone} item.<br>
 * This class holds the configuration properties used by the game server to define godstone behavior.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Godstone")
public class GodstoneInfo
{
	@XmlAttribute
	private int skillid;
	@XmlAttribute
	private int skilllvl;
	@XmlAttribute
	private int probability;
	@XmlAttribute
	private int probabilityleft;
	@XmlAttribute
	private int breakprob;
	@XmlAttribute
	private int breakcount;
	
	/**
	 * Retrieves the unique identifier for the skill associated with this {@link GodstoneInfo}.<br>
	 * This value is used to identify which specific skill is linked to the item.
	 * @return the {@code skillid} as an {@code int}.
	 */
	public int getSkillid()
	{
		return skillid;
	}
	
	/**
	 * Retrieves the current skill level.<br>
	 * This value is stored in the {@code skilllvl} field.
	 * @return the skill level as an {@code int}.
	 */
	public int getSkilllvl()
	{
		return skilllvl;
	}
	
	/**
	 * Retrieves the success chance for this item.<br>
	 * This value is stored in the {@code probability} field.
	 * @return the current probability as an {@code int}.
	 */
	public int getProbability()
	{
		return probability;
	}
	
	/**
	 * Retrieves the remaining probability value.<br>
	 * This value is used to track progress during an action.
	 * @return the current {@code probabilityleft} value.
	 */
	public int getProbabilityleft()
	{
		return probabilityleft;
	}
	
	/**
	 * Retrieves the probability of an item breaking.<br>
	 * This value is stored in the {@code breakprob} field.
	 * @return the current break probability as an {@code int}.
	 */
	public int getBreakprob()
	{
		return breakprob;
	}
	
	/**
	 * Retrieves the total number of breaks.<br>
	 * This value is stored in the {@code breakcount} field.
	 * @return the current break count as an {@code int}.
	 */
	public int getBreakcount()
	{
		return breakcount;
	}
}
