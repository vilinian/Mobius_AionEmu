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
package com.aionemu.gameserver.ai2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This class provides logging functionality for the {@link Creature} AI system.<br>
 * It helps developers track and debug AI behaviors within the game server.
 * @author ATracer
 */
public class AI2Logger
{
	private static final Logger log = LoggerFactory.getLogger(AI2Logger.class);
	
	/**
	 * Logs an informational message for a specific {@link AbstractAI}.<br>
	 * The message is only recorded if the AI has logging enabled.
	 * @param ai The {@code AbstractAI} instance to check for logging status.
	 * @param message The text message to be logged.
	 */
	public static void info(AbstractAI ai, String message)
	{
		if (ai.isLogging())
		{
			log.info("[AI2] " + ai.getOwner().getObjectId() + " - " + message);
		}
	}
	
	/**
	 * Logs an informational message for a specific {@code AI2} instance.<br>
	 * This method forwards the log request to the general {@code String)} method.
	 * @param ai The {@code AI2} object that is generating the log.
	 * @param message The text message to be displayed in the logs.
	 */
	public static void info(AI2 ai, String message)
	{
		info((AbstractAI) ai, message);
	}
	
	/**
	 * Logs movement information for a specific {@link Creature}.<br>
	 * This method only triggers if {@code AIConfig.MOVE_DEBUG} is enabled.<br>
	 * It checks if the creature's AI instance has logging turned on.
	 * @param owner The {@code Creature} that owns the AI.
	 * @param message The specific information to log.
	 */
	public static void moveinfo(Creature owner, String message)
	{
		if (AIConfig.MOVE_DEBUG && owner.getAi2().isLogging())
		{
			log.info("[AI2] " + owner.getObjectId() + " - " + message);
		}
	}
}
