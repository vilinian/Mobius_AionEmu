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

import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * Defines the different categories of {@link Npc} templates available in the game.<br>
 * This enum is used to classify NPCs for specific behaviors and logic.
 * @author ATracer
 */
public enum NpcTemplateType
{
	MERCENARY,
	GENERAL,
	GUARD,
	SUMMON_PET,
	ABYSS_GUARD,
	MONSTER,
	HOUSING,
	FLAG,
	RAID_MONSTER,
	NONE
}
