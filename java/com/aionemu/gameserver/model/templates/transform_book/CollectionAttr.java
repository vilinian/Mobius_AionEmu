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
package com.aionemu.gameserver.model.templates.transform_book;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * Represents the attributes associated with a transformation book collection.<br>
 * This class stores specific data used to define collection properties within the {@code transform_book} system.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CollectionAttr")
public class CollectionAttr
{
	@XmlAttribute(name = "name", required = true)
	protected StatEnum name;
	@XmlAttribute(name = "value", required = true)
	protected int value;
	
	/**
	 * Retrieves the name of the statistic associated with this bonus.<br>
	 * This returns a {@link StatEnum} value.
	 * @return The {@code StatEnum} representing the name.
	 */
	public StatEnum getName()
	{
		return name;
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
