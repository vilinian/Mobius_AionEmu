/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Aion-Lightning is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. * You should have received a copy of the GNU General Public License
 * along with Aion-Lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.cubics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * This class represents a collection of core statistics for cubic items.<br>
 * It maps specific {@link StatEnum} values to their corresponding data structures.
 * @author Phantom_KNA
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatCoreList")
public class StatCoreList
{
	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(required = true)
	protected StatEnum stat;
	@XmlAttribute(name = "level")
	protected int level;
	@XmlAttribute(name = "value")
	protected int value;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the {@code StatEnum} associated with this bonus.<br>
	 * This method returns the specific type of statistic for the random bonus.
	 * @return The {@link StatEnum} value.
	 */
	public StatEnum getStat()
	{
		return stat;
	}
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	public int getValue()
	{
		return value;
	}
}
