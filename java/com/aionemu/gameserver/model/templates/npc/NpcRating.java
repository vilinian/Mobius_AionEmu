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
package com.aionemu.gameserver.model.templates.npc;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.state.CreatureSeeState;

/**
 * Represents the different rating levels for {@link com.aionemu.gameserver.model.gameobjects.Npc}.<br>
 * This enum is used to categorize NPCs based on their importance or status within the game world.
 * @author ATracer
 */
@XmlType(name = "rating")
@XmlEnum
public enum NpcRating
{
	JUNK(CreatureSeeState.NORMAL),
	NORMAL(CreatureSeeState.NORMAL),
	ELITE(CreatureSeeState.SEARCH1),
	HERO(CreatureSeeState.SEARCH2),
	LEGENDARY(CreatureSeeState.SEARCH2);
	
	private final CreatureSeeState congenitalSeeState;
	
	/**
	 * This is a private constructor for the {@link NpcRating} enum.<br>
	 * It initializes the {@code congenitalSeeState} field.
	 * @param congenitalSeeState The {@code CreatureSeeState} assigned to this rating.
	 */
	private NpcRating(CreatureSeeState congenitalSeeState)
	{
		this.congenitalSeeState = congenitalSeeState;
	}
	
	/**
	 * Retrieves the default visibility state for this NPC rating.<br>
	 * This value determines how creatures perceive this entity based on its type.
	 * @return The {@code CreatureSeeState} associated with this rating.
	 */
	public CreatureSeeState getCongenitalSeeState()
	{
		return congenitalSeeState;
	}
}
