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
package com.aionemu.gameserver.model.ai;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class defines the configuration data for bomb items in the game.<br>
 * It serves as a template used to initialize various properties of bombs.<br>
 * You can use this model to manage how different types of bombs behave.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BombTemplate")
public class BombTemplate
{
	@XmlAttribute(name = "skillId")
	private int SkillId = 0;
	@XmlAttribute(name = "cd")
	private int cd = 0;
	
	/**
	 * Retrieves the cooldown value for this bomb template.<br>
	 * This value is used to determine how long a player must wait before using the skill again.
	 * @return The {@code int} value of the cooldown.
	 */
	public int getCd()
	{
		return cd;
	}
	
	/**
	 * Retrieves the unique identifier for the skill associated with this AI.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return SkillId;
	}
}
