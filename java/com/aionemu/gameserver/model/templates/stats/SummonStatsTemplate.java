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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class defines the base statistics for a summon.<br>
 * It serves as a template used to initialize various attributes for summoned entities.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "summon_stats_template")
public class SummonStatsTemplate extends StatsTemplate
{
	@XmlAttribute(name = "pdefense")
	private int pdefense;
	@XmlAttribute(name = "mresist")
	private int mresist;
	@XmlAttribute(name = "mcrit")
	private int mcrit;
	
	/**
	 * Retrieves the physical defense value.<br>
	 * This value represents how much physical damage is reduced.
	 * @return the {@code pdefense} value.
	 */
	public int getPdefense()
	{
		return pdefense;
	}
	
	/**
	 * Retrieves the magic resistance value of the NPC.<br>
	 * This value is used to calculate damage reduction from magic attacks.
	 * @return The current {@code mresist} value as an {@code int}.
	 */
	public int getMresist()
	{
		return mresist;
	}
	
	/**
	 * Retrieves the magic critical hit rate.<br>
	 * This value is stored in the {@code mcrit} field.
	 * @return the {@code mcrit} value as an {@code int}.
	 */
	public int getMcrit()
	{
		return mcrit;
	}
}
