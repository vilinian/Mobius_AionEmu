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
package com.aionemu.gameserver.ai2.manager;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * Manages the behavior of {@link Npc} entities that are following a target.<br>
 * It handles the logic for movement and positioning during follow actions.
 * @author ATracer
 */
public class FollowManager
{
	/**
	 * Handles logic for when a target is too far away.<br>
	 * It checks if the {@link Npc} can see its current target.<br>
	 * It also evaluates if the NPC should switch to a more hated creature.<br>
	 * If conditions are not met, it triggers a {@code TARGET_GIVEUP} event.
	 * @param npcAI The {@code NpcAI2} instance to process.
	 */
	public static void targetTooFar(NpcAI2 npcAI)
	{
		final Npc npc = npcAI.getOwner();
		if (npcAI.isLogging())
		{
			AI2Logger.info(npcAI, "Follow manager - targetTooFar");
		}
		
		if (npcAI.isMoveSupported())
		{
			npc.getMoveController().moveToTargetObject();
		}
	}
}
