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
package com.aionemu.gameserver.ai2.poll;

import com.aionemu.gameserver.ai2.NpcAI2;

/**
 * This class manages the polling logic for {@link NpcAI2} entities.<br>
 * It handles periodic checks to update NPC behaviors and states.
 * @author ATracer
 */
public class NpcAIPolls
{
	/**
	 * Determines if the NPC should decay.<br>
	 * This method checks the state of the provided {@link NpcAI2}.<br>
	 * It currently always returns a positive result.
	 * @param npcAI The {@code NpcAI2} object to check.
	 * @return An {@code AIAnswer} indicating whether the NPC should decay.
	 */
	public static AIAnswer shouldDecay(NpcAI2 npcAI)
	{
		return AIAnswers.POSITIVE;
	}
	
	/**
	 * Determines if an NPC should respawn.<br>
	 * This method checks the current state of the {@code NpcAI2}.<br>
	 * It currently always returns a positive result.
	 * @param npcAI The {@link NpcAI2} object to check.
	 * @return An {@code AIAnswer} indicating if the NPC should respawn.
	 */
	public static AIAnswer shouldRespawn(NpcAI2 npcAI)
	{
		return AIAnswers.POSITIVE;
	}
}
