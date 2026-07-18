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
package com.aionemu.gameserver.questEngine.handlers;

/**
 * Represents the possible outcomes of a quest handler execution.<br>
 * This enum is used by {@link com.aionemu.gameserver.questEngine.handlers.QuestHandler} to communicate success or failure states.
 * @author Rolandas
 */
public enum HandlerResult
{
	UNKNOWN, // allow other handlers to process
	SUCCESS,
	FAILED;
	
	/**
	 * Converts a {@code Boolean} value into a {@link HandlerResult}.<br>
	 * Returns {@code HandlerResult.SUCCESS} if the value is {@code true}.<br>
	 * Returns {@code HandlerResult.FAILED} if the value is {@code false}.<br>
	 * Returns {@code HandlerResult.UNKNOWN} if the value is {@code null}.
	 * @param value The boolean value to convert.
	 * @return The corresponding {@link HandlerResult}.
	 */
	public static HandlerResult fromBoolean(Boolean value)
	{
		if (value == null)
		{
			return HandlerResult.UNKNOWN;
		}
		else if (value)
		{
			return HandlerResult.SUCCESS;
		}
		
		return HandlerResult.FAILED;
	}
}
