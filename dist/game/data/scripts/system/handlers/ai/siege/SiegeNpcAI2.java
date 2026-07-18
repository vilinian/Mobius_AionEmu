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
package system.handlers.ai.siege;

import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the artificial intelligence for NPCs participating in siege events.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specific behaviors for siege scenarios.
 * @author ATracer
 */
public class SiegeNpcAI2 extends AggressiveNpcAI2
{
	/**
	 * This method handles specific logic for the {@code bomb} NPC.<br>
	 * It checks the type of {@code question} provided by the system.<br>
	 * It returns a predefined {@link AIAnswer} based on the question type.
	 * @param question The {@code AIQuestion} being asked to this instance.
	 * @return The corresponding {@code AIAnswer} or {@code null}.
	 */
	@Override
	protected AIAnswer pollInstance(AIQuestion question)
	{
		switch (question)
		{
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.POSITIVE;
			default:
				return null;
		}
	}
	
	/**
	 * Retrieves the spawn template for this siege.<br>
	 * This method calls the parent class to get the {@code SiegeSpawnTemplate}.
	 * @return the {@code SiegeSpawnTemplate} associated with this object.
	 */
	@Override
	protected SiegeSpawnTemplate getSpawnTemplate()
	{
		return (SiegeSpawnTemplate) super.getSpawnTemplate();
	}
}
