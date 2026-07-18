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
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the data model for an {@code Idian} item template.<br>
 * This class is used to map XML configuration data into the game server's item system.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Idian")
public class Idian
{
	@XmlAttribute(name = "burn_defend")
	private int burnDefend;
	@XmlAttribute(name = "burn_attack")
	private int burnAttack;
	
	/**
	 * Retrieves the current burn attack value.<br>
	 * This value represents the strength of the burn effect.
	 * @return The {@code int} value of the burn attack.
	 */
	public int getBurnAttack()
	{
		return burnAttack;
	}
	
	/**
	 * Retrieves the defense value against burning effects.<br>
	 * This value is used to calculate how much damage is reduced.
	 * @return The {@code int} value of the burn defense.
	 */
	public int getBurnDefend()
	{
		return burnDefend;
	}
}
