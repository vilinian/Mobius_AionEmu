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
package com.aionemu.gameserver.model.templates.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the template for absolute statistics in the game.<br>
 * It defines the base values used to calculate character attributes and stats.<br>
 * It is mapped to XML data for easy configuration.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatsSet", propOrder =
{
	"modifiers"
})
public class AbsoluteStatsTemplate
{
	@XmlElement(required = true)
	protected ModifiersTemplate modifiers;
	@XmlAttribute(required = true)
	protected int id;
	
	/**
	 * Retrieves the {@code ModifiersTemplate} associated with this collection.<br>
	 * This method returns the modifier data used for experience calculations.
	 * @return the {@link ModifiersTemplate} object or {@code null} if no modifiers exist.
	 */
	public ModifiersTemplate getModifiers()
	{
		return modifiers;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
