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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the different types of quality cores available in the game.<br>
 * This enum is used to categorize core items within the {@code cubics} system.
 * @author Phantom_KNA
 */
@XmlType(name = "QualityCoreType")
@XmlEnum
public enum QualityCoreType
{
	
	PLATINUM,
	GOLD,
	SILVER,
	BRONZE;
	
	/**
	 * This is a private constructor for the {@link QualityCoreType} enum.<br>
	 * It is used to prevent the creation of new instances from outside this class.
	 */
	private QualityCoreType()
	{
	}
}
