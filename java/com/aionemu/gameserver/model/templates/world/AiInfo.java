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
package com.aionemu.gameserver.model.templates.world;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the configuration data for an AI entity in the game world.<br>
 * It stores essential properties used to define how NPCs behave and appear.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AiInfo")
public class AiInfo
{
	public static final AiInfo DEFAULT = new AiInfo();
	@XmlAttribute(name = "chase_target")
	private int chaseTarget = 50;
	@XmlAttribute(name = "chase_home")
	private int chaseHome = 200;
	
	/**
	 * Retrieves the current target value for chasing.<br>
	 * This value is used by the AI to determine its behavior.
	 * @return The {@code int} value of the {@code chaseTarget}.
	 */
	public int getChaseTarget()
	{
		return chaseTarget;
	}
	
	/**
	 * Retrieves the distance for the home chase.<br>
	 * This value is used to determine when an AI returns home.
	 * @return The {@code int} value of the chase home distance.
	 */
	public int getChaseHome()
	{
		return chaseHome;
	}
}
