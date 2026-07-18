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
package com.aionemu.gameserver.model.actions;

import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * This class defines the behaviors and actions performed by {@link Npc} entities.<br>
 * It serves as a base for handling interactions between non-player characters and the game world.
 * @author xTz
 */
public class NpcActions extends CreatureActions
{
	/**
	 * Schedules a respawn for the given {@link Npc}.<br>
	 * This method tells the NPC controller to restart the entity.
	 * @param npc The {@code Npc} object to be rescheduled.
	 */
	public static void scheduleRespawn(Npc npc)
	{
		npc.getController().scheduleRespawn();
	}
}
