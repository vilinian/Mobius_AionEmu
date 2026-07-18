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
package com.aionemu.gameserver.model.templates.minion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.stats.StatsTemplate;

/**
 * This class defines the base statistics for a minion.<br>
 * It extends {@link StatsTemplate} to provide specific attributes used by minions in the game.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MinionStatsTemplate")
public class MinionStatsTemplate extends StatsTemplate
{
	@XmlAttribute(name = "height")
	private float height;
	@XmlAttribute(name = "altitude")
	private float altitude;
	
	/**
	 * Retrieves the current height of the player.<br>
	 * This value is stored as a {@code float}.
	 * @return The player's height.
	 */
	public float getHeight()
	{
		return height;
	}
	
	/**
	 * Retrieves the current altitude of the minion.<br>
	 * This value is stored in the {@code altitude} field.
	 * @return The altitude as a {@code float}.
	 */
	public float getAltitude()
	{
		return altitude;
	}
}
